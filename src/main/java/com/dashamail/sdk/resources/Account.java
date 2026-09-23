package com.dashamail.sdk.resources;

import com.dashamail.sdk.DashaMailResponse;
import com.dashamail.sdk.HttpTransport;

import java.util.Map;

/**
 * Account balance and limits, confirmed senders, sending domains, webhooks.
 * https://dashamail.ru/api/account/
 */
public class Account extends BaseResource {

    public Account(HttpTransport client) {
        super(client);
    }

    /** GET /account/balance */
    public DashaMailResponse balance() {
        return client.request("GET", "/account/balance", null, null);
    }

    /** GET /account/senders — confirmed From: addresses. */
    public DashaMailResponse senders() {
        return client.request("GET", "/account/senders", null, null);
    }

    /** POST /account/senders/confirm — confirm a sender address with the code emailed to it. */
    public DashaMailResponse confirmSender(String email, String code) {
        Map<String, Object> body = with(with(null, "email", email), "code", code);
        return client.request("POST", "/account/senders/confirm", null, body);
    }

    /** GET /account/domains — sending domains. */
    public DashaMailResponse domains() {
        return domains(null);
    }

    public DashaMailResponse domains(Map<String, Object> params) {
        return client.request("GET", "/account/domains", params, null);
    }

    /** POST /account/domains — add a sending domain. */
    public DashaMailResponse addDomain(String domain) {
        return addDomain(domain, null);
    }

    public DashaMailResponse addDomain(String domain, Map<String, Object> params) {
        return client.request("POST", "/account/domains", null, with(params, "domain", domain));
    }

    /** GET /account/domains/check — check DNS (DKIM/SPF) validity of sending domains. */
    public DashaMailResponse checkDomains() {
        return checkDomains(null);
    }

    public DashaMailResponse checkDomains(Map<String, Object> params) {
        return client.request("GET", "/account/domains/check", params, null);
    }

    /** DELETE /account/domains/{domain} */
    public DashaMailResponse deleteDomain(String domain) {
        return deleteDomain(domain, null);
    }

    public DashaMailResponse deleteDomain(String domain, Map<String, Object> params) {
        return client.request("DELETE", "/account/domains/" + HttpTransport.encodePathSegment(domain), null, params);
    }

    /** GET /account/webhooks — bulk-campaign webhooks. */
    public DashaMailResponse webhooks() {
        return webhooks(null);
    }

    public DashaMailResponse webhooks(Map<String, Object> params) {
        return client.request("GET", "/account/webhooks", params, null);
    }

    /** POST /account/webhooks — event is one of: open, click, hard, spam, unsub, subscribe, confirm. */
    public DashaMailResponse addWebhook(String event, String url) {
        return addWebhook(event, url, null);
    }

    public DashaMailResponse addWebhook(String event, String url, Map<String, Object> params) {
        Map<String, Object> body = with(with(params, "event", event), "url", url);
        return client.request("POST", "/account/webhooks", null, body);
    }

    /** DELETE /account/webhooks/{eventName} */
    public DashaMailResponse deleteWebhook(String eventName) {
        return client.request("DELETE", "/account/webhooks/" + HttpTransport.encodePathSegment(eventName), null, null);
    }

    /** GET /account/webhooks/transactional */
    public DashaMailResponse transactionalWebhooks() {
        return transactionalWebhooks(null);
    }

    public DashaMailResponse transactionalWebhooks(Map<String, Object> params) {
        return client.request("GET", "/account/webhooks/transactional", params, null);
    }

    /** POST /account/webhooks/transactional — event is one of: send, delivered, dropped, open, click, hard, spam, unsub. */
    public DashaMailResponse addTransactionalWebhook(String event, String url) {
        return addTransactionalWebhook(event, url, null);
    }

    public DashaMailResponse addTransactionalWebhook(String event, String url, Map<String, Object> params) {
        Map<String, Object> body = with(with(params, "event", event), "url", url);
        return client.request("POST", "/account/webhooks/transactional", null, body);
    }

    /** DELETE /account/webhooks/transactional/{eventName} */
    public DashaMailResponse deleteTransactionalWebhook(String eventName) {
        return client.request("DELETE", "/account/webhooks/transactional/" + HttpTransport.encodePathSegment(eventName), null, null);
    }
}
