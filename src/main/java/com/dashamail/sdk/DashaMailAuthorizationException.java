package com.dashamail.sdk;

import java.util.Map;

/** HTTP 403 — the API key is valid but lacks the rights or scope for this action. */
public class DashaMailAuthorizationException extends DashaMailApiException {
    public DashaMailAuthorizationException(String message, int httpStatus, int apiCode, Map<String, Object> details) {
        super(message, httpStatus, apiCode, details);
    }
}
