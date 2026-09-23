package com.dashamail.sdk;

import java.util.Map;

/** HTTP 429 — too many requests. See {@link #getRetryAfter()} for how long to back off. */
public class DashaMailRateLimitException extends DashaMailApiException {
    public DashaMailRateLimitException(String message, int httpStatus, int apiCode, Map<String, Object> details) {
        super(message, httpStatus, apiCode, details);
    }

    public Integer getRetryAfter() {
        Object value = getDetails().get("retry_after");
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }
}
