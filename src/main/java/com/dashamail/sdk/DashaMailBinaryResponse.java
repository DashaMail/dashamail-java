package com.dashamail.sdk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Raw bytes returned by an endpoint that can answer outside JSON —
 * currently only {@code POST /images/optimize?response=binary}.
 */
public class DashaMailBinaryResponse {

    private final byte[] body;
    private final String contentType;
    private final int httpStatus;

    public DashaMailBinaryResponse(byte[] body, String contentType, int httpStatus) {
        this.body = body;
        this.contentType = contentType;
        this.httpStatus = httpStatus;
    }

    public byte[] getBody() {
        return body;
    }

    public String getContentType() {
        return contentType;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    /** Writes the bytes to a file. */
    public void saveTo(String path) throws IOException {
        Files.write(Path.of(path), body);
    }
}
