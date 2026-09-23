package com.dashamail.sdk;

/** The request never got an HTTP response (DNS, TLS, timeout, connection reset...). */
public class DashaMailNetworkException extends RuntimeException {
    public DashaMailNetworkException(String message) {
        super(message);
    }

    public DashaMailNetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
