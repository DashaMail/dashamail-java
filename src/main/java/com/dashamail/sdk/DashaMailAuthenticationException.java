package com.dashamail.sdk;

import java.util.Map;

/** HTTP 401 — missing or invalid API key. */
public class DashaMailAuthenticationException extends DashaMailApiException {
    public DashaMailAuthenticationException(String message, int httpStatus, int apiCode, Map<String, Object> details) {
        super(message, httpStatus, apiCode, details);
    }
}
