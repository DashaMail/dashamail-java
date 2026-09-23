package com.dashamail.sdk.resources;

import com.dashamail.sdk.DashaMailResponse;
import com.dashamail.sdk.HttpTransport;

import java.util.Map;

/**
 * Saved markup: legacy HTML-template store (/templates) plus saved campaigns
 * in TEMPLATE status (/templates/saved), which is where the account UI keeps them.
 * https://dashamail.ru/api/templates/
 */
public class Templates extends BaseResource {

    public Templates(HttpTransport client) {
        super(client);
    }

    /** GET /templates — legacy HTML templates. */
    public DashaMailResponse all() {
        return all(null);
    }

    public DashaMailResponse all(Map<String, Object> params) {
        return client.request("GET", "/templates", params, null);
    }

    /** GET /templates/{id} */
    public DashaMailResponse get(Object id) {
        return client.request("GET", "/templates/" + id, null, null);
    }

    /** POST /templates — template is HTML markup, body is the plain-text markup. */
    public DashaMailResponse create(String name, String template, String body) {
        return create(name, template, body, null);
    }

    public DashaMailResponse create(String name, String template, String body, Map<String, Object> params) {
        Map<String, Object> payload = with(with(with(params, "name", name), "template", template), "body", body);
        return client.request("POST", "/templates", null, payload);
    }

    /** PUT /templates/{id} */
    public DashaMailResponse update(Object id, Map<String, Object> params) {
        return client.request("PUT", "/templates/" + id, null, params);
    }

    /** DELETE /templates/{id} */
    public DashaMailResponse delete(Object id) {
        return client.request("DELETE", "/templates/" + id, null, null);
    }

    /** GET /templates/saved — campaigns saved as reusable templates (status TEMPLATE). */
    public DashaMailResponse saved() {
        return saved(null);
    }

    public DashaMailResponse saved(Map<String, Object> params) {
        return client.request("GET", "/templates/saved", params, null);
    }
}
