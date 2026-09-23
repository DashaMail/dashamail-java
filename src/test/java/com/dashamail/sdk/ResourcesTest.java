package com.dashamail.sdk;

import com.dashamail.sdk.resources.Images;
import com.dashamail.sdk.resources.Lists;
import com.dashamail.sdk.resources.Transactional;
import org.junit.jupiter.api.Test;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourcesTest {

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
    void listsCreateSendsNameInBody() {
        FakeHttpTransport client = new FakeHttpTransport();
        client.queueResponse(201, okBody(Map.of("list_id", 42)));
        Lists lists = new Lists(client);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("company", "ООО Ромашка");
        DashaMailResponse result = lists.create("Клиенты", params);

        assertEquals(42L, ((Number) result.get("list_id")).longValue());
        FakeHttpTransport.RecordedCall call = client.getCalls().get(0);
        assertEquals("POST", call.method);
        assertTrue(call.url.endsWith("/lists"));
        Map<?, ?> decoded = (Map<?, ?>) com.dashamail.sdk.internal.Json.parse(call.body);
        assertEquals("Клиенты", decoded.get("name"));
        assertEquals("ООО Ромашка", decoded.get("company"));
    }

    @Test
    void listsGetMemberEncodesEmailInPath() {
        FakeHttpTransport client = new FakeHttpTransport();
        client.queueResponse(200, okBody(Map.of("email", "a+b@example.com")));
        Lists lists = new Lists(client);

        lists.getMember(1, "a+b@example.com");

        String url = client.getCalls().get(0).url;
        assertTrue(url.contains(HttpTransport.encodePathSegment("a+b@example.com")));
    }

    @Test
    void listsMoveMemberSendsRequiredFields() {
        FakeHttpTransport client = new FakeHttpTransport();
        client.queueResponse(200, okBody(null));
        Lists lists = new Lists(client);

        lists.moveMember(1, "a@example.com", 2, 555);

        Map<?, ?> decoded = (Map<?, ?>) com.dashamail.sdk.internal.Json.parse(client.getCalls().get(0).body);
        assertEquals(2L, ((Number) decoded.get("to_list_id")).longValue());
        assertEquals(555L, ((Number) decoded.get("member_id")).longValue());
    }

    @Test
    void listsFindMemberHitsAccountWideEndpoint() throws Exception {
        FakeHttpTransport client = new FakeHttpTransport();
        client.queueResponse(200, okBody(java.util.List.of()));
        Lists lists = new Lists(client);

        lists.findMember("a@example.com");

        String url = client.getCalls().get(0).url;
        assertTrue(url.contains("/members?"));
        assertTrue(URLDecoder.decode(url, StandardCharsets.UTF_8).contains("email=a@example.com"));
    }

    @Test
    void transactionalSendBuildsBody() {
        FakeHttpTransport client = new FakeHttpTransport();
        client.queueResponse(201, okBody(Map.of("transaction_id", "abc")));
        Transactional transactional = new Transactional(client);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("subject", "Hello");
        DashaMailResponse result = transactional.send("to@example.com", "from@yourdomain.com", "<p>Hi</p>", params);

        assertEquals("abc", result.get("transaction_id"));
        Map<?, ?> decoded = (Map<?, ?>) com.dashamail.sdk.internal.Json.parse(client.getCalls().get(0).body);
        assertEquals("to@example.com", decoded.get("to"));
        assertEquals("from@yourdomain.com", decoded.get("from_email"));
        assertEquals("<p>Hi</p>", decoded.get("message"));
        assertEquals("Hello", decoded.get("subject"));
    }

    @Test
    void imagesOptimizeBase64EncodesBinaryData() {
        FakeHttpTransport client = new FakeHttpTransport();
        byte[] raw = "raw-bytes".getBytes(StandardCharsets.UTF_8);
        client.queueResponse(200, okBody(Map.of("image", java.util.Base64.getEncoder().encodeToString(raw))));
        Images images = new Images(client);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("max_width", 800);
        images.optimize(raw, params);

        Map<?, ?> decoded = (Map<?, ?>) com.dashamail.sdk.internal.Json.parse(client.getCalls().get(0).body);
        assertEquals(java.util.Base64.getEncoder().encodeToString(raw), decoded.get("image"));
        assertEquals(800L, ((Number) decoded.get("max_width")).longValue());
    }

    @Test
    void paginatedMembersExposesHasMore() {
        FakeHttpTransport client = new FakeHttpTransport();
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("has_more", true);
        meta.put("limit", 100);
        client.queueResponse(200, okBody(java.util.List.of(Map.of("email", "a@example.com")), meta));
        Lists lists = new Lists(client);

        DashaMailResponse result = lists.members(1);

        assertTrue(result.hasMore());
        assertEquals(100, result.getLimit().intValue());
    }
}
