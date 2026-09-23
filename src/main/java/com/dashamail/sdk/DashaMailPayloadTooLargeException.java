package com.dashamail.sdk;

import java.util.Map;

/** HTTP 413 — the uploaded file or attachment is too large. */
public class DashaMailPayloadTooLargeException extends DashaMailApiException {
    public DashaMailPayloadTooLargeException(String message, int httpStatus, int apiCode, Map<String, Object> details) {
        super(message, httpStatus, apiCode, details);
    }
}
