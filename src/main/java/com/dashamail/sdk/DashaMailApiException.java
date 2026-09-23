package com.dashamail.sdk;

import java.util.HashMap;
import java.util.Map;

/**
 * Raised for any error response from the DashaMail API (HTTP status &gt;= 400
 * with a JSON {@code {"error": {"code", "message", "details"}}} body).
 *
 * <p>The HTTP status and DashaMail's own error {@code code} are different
 * numbers: {@code code} is stable across API versions and documented at
 * https://dashamail.ru/api/errors/, while the HTTP status is a coarser
 * REST-ification of it.</p>
 */
public class DashaMailApiException extends RuntimeException {

    private final int httpStatus;
    private final int apiCode;
    private final Map<String, Object> details;

    public DashaMailApiException(String message, int httpStatus, int apiCode, Map<String, Object> details) {
        super(message);
        this.httpStatus = httpStatus;
        this.apiCode = apiCode;
        this.details = details != null ? details : new HashMap<>();
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public int getApiCode() {
        return apiCode;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    /** Builds the most specific exception subclass for a given HTTP status. */
    @SuppressWarnings("unchecked")
    public static DashaMailApiException fromError(int httpStatus, Map<String, Object> error) {
        Map<String, Object> safeError = error != null ? error : new HashMap<>();

        String message = "DashaMail API error";
        Object messageValue = safeError.get("message");
        if (messageValue != null) {
            message = messageValue.toString();
        }

        int apiCode = httpStatus;
        Object codeValue = safeError.get("code");
        if (codeValue instanceof Number) {
            apiCode = ((Number) codeValue).intValue();
        }

        Map<String, Object> details = null;
        Object detailsValue = safeError.get("details");
        if (detailsValue instanceof Map) {
            details = (Map<String, Object>) detailsValue;
        }

        switch (httpStatus) {
            case 401:
                return new DashaMailAuthenticationException(message, httpStatus, apiCode, details);
            case 402:
                return new DashaMailPaymentRequiredException(message, httpStatus, apiCode, details);
            case 403:
                return new DashaMailAuthorizationException(message, httpStatus, apiCode, details);
            case 404:
                return new DashaMailNotFoundException(message, httpStatus, apiCode, details);
            case 409:
                return new DashaMailConflictException(message, httpStatus, apiCode, details);
            case 413:
                return new DashaMailPayloadTooLargeException(message, httpStatus, apiCode, details);
            case 422:
                return new DashaMailValidationException(message, httpStatus, apiCode, details);
            case 429:
                return new DashaMailRateLimitException(message, httpStatus, apiCode, details);
            default:
                break;
        }

        if (httpStatus >= 500) {
            return new DashaMailServerException(message, httpStatus, apiCode, details);
        }

        return new DashaMailApiException(message, httpStatus, apiCode, details);
    }
}
