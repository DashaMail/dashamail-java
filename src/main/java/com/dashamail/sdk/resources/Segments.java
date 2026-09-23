package com.dashamail.sdk.resources;

import com.dashamail.sdk.DashaMailResponse;
import com.dashamail.sdk.HttpTransport;

import java.util.Map;

/**
 * Saved subscriber segments: condition sets, field/operator reference, size counting.
 * https://dashamail.ru/api/segments/
 */
public class Segments extends BaseResource {

    public Segments(HttpTransport client) {
        super(client);
    }

    /** GET /segments */
    public DashaMailResponse all() {
        return all(null);
    }

    public DashaMailResponse all(Map<String, Object> params) {
        return client.request("GET", "/segments", params, null);
    }

    /** GET /segments/{segmentId} */
    public DashaMailResponse get(Object segmentId) {
        return client.request("GET", "/segments/" + segmentId, null, null);
    }

    /** POST /segments — esegment is a condition tree, e.g. {"match": "all", "c": [...]}. */
    public DashaMailResponse create(Object listId, String name, Map<String, Object> esegment) {
        return create(listId, name, esegment, null);
    }

    public DashaMailResponse create(Object listId, String name, Map<String, Object> esegment, Map<String, Object> params) {
        Map<String, Object> body = with(with(with(params, "list_id", listId), "name", name), "esegment", esegment);
        return client.request("POST", "/segments", null, body);
    }

    /** PUT /segments/{segmentId} */
    public DashaMailResponse update(Object segmentId, Map<String, Object> params) {
        return client.request("PUT", "/segments/" + segmentId, null, params);
    }

    /** DELETE /segments/{segmentId} */
    public DashaMailResponse delete(Object segmentId) {
        return client.request("DELETE", "/segments/" + segmentId, null, null);
    }

    /** POST /segments/count — recompute a segment's size, by id or by passing list_id + esegment directly. */
    public DashaMailResponse count(Map<String, Object> params) {
        return client.request("POST", "/segments/count", null, params);
    }

    /** GET /segments/fields — which fields/operators are available for a list's segments. */
    public DashaMailResponse fields(Object listId) {
        return client.request("GET", "/segments/fields", with(null, "list_id", listId), null);
    }
}
