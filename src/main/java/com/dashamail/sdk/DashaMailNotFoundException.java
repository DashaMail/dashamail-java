package com.dashamail.sdk;

import java.util.Map;

/** HTTP 404 — the resource (or the account itself) does not exist. */
public class DashaMailNotFoundException extends DashaMailApiException {
    public DashaMailNotFoundException(String message, int httpStatus, int apiCode, Map<String, Object> details) {
        super(message, httpStatus, apiCode, details);
    }
}
