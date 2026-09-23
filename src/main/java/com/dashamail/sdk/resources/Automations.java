package com.dashamail.sdk.resources;

import com.dashamail.sdk.DashaMailResponse;
import com.dashamail.sdk.HttpTransport;

import java.util.Map;

/**
 * Event-triggered emails (subscribe, add, open, click, field change...).
 * https://dashamail.ru/api/automations/
 */
public class Automations extends BaseResource {

    public Automations(HttpTransport client) {
        super(client);
    }

    /** GET /automations/events — reference of available trigger events. */
    public DashaMailResponse events() {
        return client.request("GET", "/automations/events", null, null);
    }

    /** GET /automations */
    public DashaMailResponse all() {
        return all(null);
    }

    public DashaMailResponse all(Map<String, Object> params) {
        return client.request("GET", "/automations", params, null);
    }

    /** POST /automations — requires list_id, subject, from_email, from_name; see the API docs for the rest. */
    public DashaMailResponse create(Map<String, Object> params) {
        return client.request("POST", "/automations", null, params);
    }

    /** PUT /automations/{campaignId} */
    public DashaMailResponse update(Object campaignId, Map<String, Object> params) {
        return client.request("PUT", "/automations/" + campaignId, null, params);
    }

    /** DELETE /automations/{campaignId} */
    public DashaMailResponse delete(Object campaignId) {
        return client.request("DELETE", "/automations/" + campaignId, null, null);
    }

    /** POST /automations/{campaignId}/trigger — force-run for one subscriber (fails with code 37 before moderation). */
    public DashaMailResponse trigger(Object campaignId, String email) {
        return trigger(campaignId, email, null);
    }

    public DashaMailResponse trigger(Object campaignId, String email, Map<String, Object> params) {
        return client.request("POST", "/automations/" + campaignId + "/trigger", null, with(params, "email", email));
    }
}
