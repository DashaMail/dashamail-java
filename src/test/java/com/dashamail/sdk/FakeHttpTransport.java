package com.dashamail.sdk;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * An HttpTransport that never touches the network: execute() is
 * overridden to return a canned response queued up front, so resource
 * classes can be tested against realistic API payloads.
 */
public class FakeHttpTransport extends HttpTransport {

    public static final class RecordedCall {
        public final String method;
        public final String url;
        public final Map<String, String> headers;
        public final String body;

        RecordedCall(String method, String url, Map<String, String> headers, String body) {
            this.method = method;
            this.url = url;
            this.headers = headers;
            this.body = body;
        }
    }

    private final Queue<QueuedResponse> queue = new LinkedList<>();
    private final List<RecordedCall> calls = new ArrayList<>();

    public FakeHttpTransport() {
        super("test-key");
    }

    public FakeHttpTransport(String apiKey) {
        super(apiKey);
    }

    public FakeHttpTransport queueResponse(int status, String jsonBody) {
        return queueResponse(status, jsonBody, "application/json");
    }

    public FakeHttpTransport queueResponse(int status, String jsonBody, String contentType) {
        byte[] bytes = jsonBody != null ? jsonBody.getBytes(StandardCharsets.UTF_8) : new byte[0];
        queue.add(new QueuedResponse(status, bytes, contentType));
        return this;
    }

    public List<RecordedCall> getCalls() {
        return calls;
    }

    @Override
    protected RawResponse execute(String method, String url, Map<String, String> headers, byte[] body) {
        String bodyText = body != null ? new String(body, StandardCharsets.UTF_8) : null;
        calls.add(new RecordedCall(method, url, headers, bodyText));

        QueuedResponse next = queue.poll();
        if (next == null) {
            throw new IllegalStateException("FakeHttpTransport: no queued response for " + method + " " + url);
        }
        return new RawResponse(next.status, next.body, next.contentType);
    }

    private static final class QueuedResponse {
        final int status;
        final byte[] body;
        final String contentType;

        QueuedResponse(int status, byte[] body, String contentType) {
            this.status = status;
            this.body = body;
            this.contentType = contentType;
        }
    }
}
