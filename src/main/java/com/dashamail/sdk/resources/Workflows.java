package com.dashamail.sdk.resources;

import com.dashamail.sdk.DashaMailResponse;
import com.dashamail.sdk.HttpTransport;

import java.util.Map;

/**
 * Visual-builder automation scenarios.
 * https://dashamail.ru/api/automations/#workflows
 */
public class Workflows extends BaseResource {

    public Workflows(HttpTransport client) {
        super(client);
    }

    /** GET /workflows */
    public DashaMailResponse all() {
        return client.request("GET", "/workflows", null, null);
    }

    /** GET /workflows/{workflowId} */
    public DashaMailResponse get(Object workflowId) {
        return client.request("GET", "/workflows/" + workflowId, null, null);
    }

    /** DELETE /workflows/{workflowId} */
    public DashaMailResponse delete(Object workflowId) {
        return client.request("DELETE", "/workflows/" + workflowId, null, null);
    }

    /** POST /workflows/{workflowId}/copy */
    public DashaMailResponse copy(Object workflowId) {
        return copy(workflowId, null);
    }

    public DashaMailResponse copy(Object workflowId, Map<String, Object> params) {
        return client.request("POST", "/workflows/" + workflowId + "/copy", null, params);
    }
}
