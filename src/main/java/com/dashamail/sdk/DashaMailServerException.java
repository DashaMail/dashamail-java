package com.dashamail.sdk;

import java.util.Map;

/** HTTP 5xx — something failed on DashaMail's side. Usually safe to retry. */
public class DashaMailServerException extends DashaMailApiException {
    public DashaMailServerException(String message, int httpStatus, int apiCode, Map<String, Object> details) {
        super(message, httpStatus, apiCode, details);
    }
}
