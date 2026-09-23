package com.dashamail.sdk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTransportTest {

    private static String okBody(Object data) {
        return okBody(data, null);
    }

    private static String okBody(Object data, Map<String, Object> meta) {
        Map<String, Object> msg = new LinkedHashMap<>();
        msg.put("err_code", 0);
        msg.put("text", "OK");
        msg.put("type", "message");
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("msg", msg);
        response.put("data", data);
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("response", response);
        if (meta != null) {
            envelope.put("meta", meta);
        }
        return com.dashamail.sdk.internal.Json.stringify(envelope);
    }

    @Test
    void successfulRequestUnwrapsResponseData() {
        FakeHttpTransport client = new FakeHttpTransport();
        client.queueResponse(200, okBody(Arrays.asList(new LinkedHashMap<>(Map.of("id", 1, "name", "Клиенты")))));

        DashaMailResponse result = client.request("GET", "/lists", null, null);

        assertInstanceOf(List.class, result.getData());
        assertEquals("OK", result.getMessage());
    }

    @Test
    void paginationMetaIsExposed() {
        FakeHttpTransport client = new FakeHttpTransport();
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("has_more", true);
        meta.put("limit", 3);
        client.queueResponse(200, okBody(Arrays.asList(1, 2, 3), meta));

        DashaMailResponse result = client.request("GET", "/lists/1/members", null, null);

        assertTrue(result.hasMore());
        assertEquals(3, result.getLimit().intValue());
    }

    @Test
    void noContentReturnsNull() {
        FakeHttpTransport client = new FakeHttpTransport();
        client.queueResponse(204, "");

        DashaMailResponse result = client.request("DELETE", "/lists/1", null, null);

        assertNull(result);
    }

    @Test
    void authorizationHeaderIsSent() {
        FakeHttpTransport client = new FakeHttpTransport("secret-key-123");
        client.queueResponse(200, okBody(List.of()));

        client.request("GET", "/lists", null, null);

        assertEquals("Bearer secret-key-123", client.getCalls().get(0).headers.get("Authorization"));
    }

    @Test
    void jsonBodyIsEncodedAndContentTypeSet() {
        FakeHttpTransport client = new FakeHttpTransport();
        client.queueResponse(201, okBody(Map.of("list_id", 5)));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", "Тест");
        client.request("POST", "/lists", null, body);

        FakeHttpTransport.RecordedCall call = client.getCalls().get(0);
        assertEquals("application/json", call.headers.get("Content-Type"));
        assertTrue(call.body.contains("Тест"));
    }

    static Stream<Arguments> errorStatuses() {
        return Stream.of(
            Arguments.of(401, DashaMailAuthenticationException.class),
            Arguments.of(404, DashaMailNotFoundException.class),
            Arguments.of(422, DashaMailValidationException.class),
            Arguments.of(429, DashaMailRateLimitException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("errorStatuses")
    void errorStatusesMapToExceptionTypes(int status, Class<? extends DashaMailApiException> expectedType) {
        FakeHttpTransport client = new FakeHttpTransport();
        client.queueResponse(status, "{\"error\":{\"code\":999,\"message\":\"boom\",\"details\":{}}}");

        DashaMailApiException error = assertThrows(DashaMailApiException.class, () -> client.request("GET", "/whatever", null, null));
        assertInstanceOf(expectedType, error);
    }

    @Test
    void rateLimitExposesRetryAfterFromDetails() {
        FakeHttpTransport client = new FakeHttpTransport();
        client.queueResponse(429, "{\"error\":{\"code\":58,\"message\":\"Limit\",\"details\":{\"limit_per_minute\":120,\"retry_after\":60}}}");

        DashaMailRateLimitException error = assertThrows(DashaMailRateLimitException.class, () -> client.request("GET", "/lists", null, null));

        assertEquals(60, error.getRetryAfter().intValue());
        assertEquals(58, error.getApiCode());
    }

    @Test
    void queryParamsAreAppendedToUrl() {
        FakeHttpTransport client = new FakeHttpTransport();
        client.queueResponse(200, okBody(List.of()));

        client.request("GET", "/lists", Map.of("state", "active"), null);

        assertTrue(client.getCalls().get(0).url.contains("state=active"));
    }
}
