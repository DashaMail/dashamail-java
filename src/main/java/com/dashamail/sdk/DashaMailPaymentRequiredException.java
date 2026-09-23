package com.dashamail.sdk;

import java.util.Map;

/** HTTP 402 — the account's balance or plan does not allow this action. */
public class DashaMailPaymentRequiredException extends DashaMailApiException {
    public DashaMailPaymentRequiredException(String message, int httpStatus, int apiCode, Map<String, Object> details) {
        super(message, httpStatus, apiCode, details);
    }
}
