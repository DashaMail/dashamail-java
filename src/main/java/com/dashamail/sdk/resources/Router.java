package com.dashamail.sdk.resources;

import com.dashamail.sdk.DashaMailResponse;
import com.dashamail.sdk.HttpTransport;

import java.util.List;
import java.util.Map;

/**
 * Inbound mail processing: receiving domains, routing rules, stored
 * messages, webhook delivery log.
 * https://dashamail.ru/api/router/
 */
public class Router extends BaseResource {

    public Router(HttpTransport client) {
        super(client);
    }

    // -- Domains --------------------------------------------------------------

    /** GET /router/domains */
    public DashaMailResponse domains() {
        return client.request("GET", "/router/domains", null, null);
    }

    /** POST /router/domains — connect your own inbound domain (needs an MX record). */
    public DashaMailResponse createDomain(String domain) {
        return client.request("POST", "/router/domains", null, with(null, "domain", domain));
    }

    /** POST /router/domains/{domainId}/verify — check the MX record. */
    public DashaMailResponse verifyDomain(Object domainId) {
        return client.request("POST", "/router/domains/" + domainId + "/verify", null, null);
    }

    /** DELETE /router/domains/{domainId} */
    public DashaMailResponse deleteDomain(Object domainId) {
        return client.request("DELETE", "/router/domains/" + domainId, null, null);
    }

    /** GET /router/domains/mx — DNS records to configure. */
    public DashaMailResponse mxInstructions() {
        return client.request("GET", "/router/domains/mx", null, null);
    }

    // -- Routes -----------------------------------------------------------------

    /** GET /router/routes */
    public DashaMailResponse routes() {
        return client.request("GET", "/router/routes", null, null);
    }

    /** GET /router/routes/{routeId} */
    public DashaMailResponse getRoute(Object routeId) {
        return client.request("GET", "/router/routes/" + routeId, null, null);
    }

    /** POST /router/routes — actions is 1..5 action maps, each with a "type" key (webhook, store, forward, stop). */
    public DashaMailResponse createRoute(List<Object> actions) {
        return createRoute(actions, null);
    }

    public DashaMailResponse createRoute(List<Object> actions, Map<String, Object> params) {
        return client.request("POST", "/router/routes", null, with(params, "actions", actions));
    }

    /** PUT /router/routes/{routeId} */
    public DashaMailResponse updateRoute(Object routeId, Map<String, Object> params) {
        return client.request("PUT", "/router/routes/" + routeId, null, params);
    }

    /** DELETE /router/routes/{routeId} */
    public DashaMailResponse deleteRoute(Object routeId) {
        return client.request("DELETE", "/router/routes/" + routeId, null, null);
    }

    /** POST /router/routes/{routeId}/rekey — reissue the webhook signing key. */
    public DashaMailResponse rekeyRoute(Object routeId) {
        return client.request("POST", "/router/routes/" + routeId + "/rekey", null, null);
    }

    /** POST /router/routes/reorder — order is a list of route ids in the desired priority order. */
    public DashaMailResponse reorderRoutes(List<Object> order) {
        return client.request("POST", "/router/routes/reorder", null, with(null, "order", order));
    }

    // -- Stored messages ------------------------------------------------------

    /** GET /router/messages */
    public DashaMailResponse messages() {
        return messages(null);
    }

    public DashaMailResponse messages(Map<String, Object> params) {
        return client.request("GET", "/router/messages", params, null);
    }

    /** GET /router/messages/{messageId} */
    public DashaMailResponse getMessage(Object messageId) {
        return client.request("GET", "/router/messages/" + messageId, null, null);
    }

    /** DELETE /router/messages/{messageId} */
    public DashaMailResponse deleteMessage(Object messageId) {
        return client.request("DELETE", "/router/messages/" + messageId, null, null);
    }

    /** GET /router/messages/{messageId}/attachments/{attachmentId} */
    public DashaMailResponse getMessageAttachment(Object messageId, Object attachmentId) {
        return client.request("GET", "/router/messages/" + messageId + "/attachments/" + attachmentId, null, null);
    }

    // -- Webhook delivery log ---------------------------------------------------

    /** GET /router/deliveries */
    public DashaMailResponse deliveries() {
        return deliveries(null);
    }

    public DashaMailResponse deliveries(Map<String, Object> params) {
        return client.request("GET", "/router/deliveries", params, null);
    }

    /** GET /router/deliveries/{deliveryId} */
    public DashaMailResponse getDelivery(Object deliveryId) {
        return client.request("GET", "/router/deliveries/" + deliveryId, null, null);
    }

    // -- Settings -----------------------------------------------------------------

    /** GET /router/settings */
    public DashaMailResponse settings() {
        return client.request("GET", "/router/settings", null, null);
    }

    /** PUT /router/settings — pass_autoreply, pass_list_mail */
    public DashaMailResponse updateSettings(Map<String, Object> params) {
        return client.request("PUT", "/router/settings", null, params);
    }
}
