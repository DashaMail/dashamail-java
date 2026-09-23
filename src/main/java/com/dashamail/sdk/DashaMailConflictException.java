package com.dashamail.sdk;

import java.util.Map;

/** HTTP 409 — the request conflicts with the resource's current state. */
public class DashaMailConflictException extends DashaMailApiException {
    public DashaMailConflictException(String message, int httpStatus, int apiCode, Map<String, Object> details) {
        super(message, httpStatus, apiCode, details);
    }
}
