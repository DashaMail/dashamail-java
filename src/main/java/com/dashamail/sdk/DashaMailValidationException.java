package com.dashamail.sdk;

import java.util.Map;

/** HTTP 422 — a required field is missing or a value is invalid. */
public class DashaMailValidationException extends DashaMailApiException {
    public DashaMailValidationException(String message, int httpStatus, int apiCode, Map<String, Object> details) {
        super(message, httpStatus, apiCode, details);
    }
}
