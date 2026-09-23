package com.dashamail.sdk.resources;

import com.dashamail.sdk.DashaMailResponse;
import com.dashamail.sdk.HttpTransport;

import java.util.Map;

/**
 * One-off transactional emails: send, status, log, stats.
 * Requires a verified sending domain — see {@link Account#addDomain(String)}.
 * https://dashamail.ru/api/transactional/
 */
public class Transactional extends BaseResource {

    public Transactional(HttpTransport client) {
        super(client);
    }

    /**
     * POST /transactional/messages
     *
     * @param to        A single address (String), or a List/Map describing multiple recipients.
     * @param fromEmail Verified sending address.
     * @param message   HTML body.
     */
    public DashaMailResponse send(Object to, String fromEmail, String message) {
        return send(to, fromEmail, message, null);
    }

    /**
     * @param params from_name, subject, plain_text, message_id, cc, bcc, headers,
     *               attachments, inline, delivery_time, domain, stat_domain...
     */
    public DashaMailResponse send(Object to, String fromEmail, String message, Map<String, Object> params) {
        Map<String, Object> body = with(with(with(params, "to", to), "from_email", fromEmail), "message", message);
        return client.request("POST", "/transactional/messages", null, body);
    }

    /** GET /transactional/messages/{transactionId} — delivery status of one message. */
    public DashaMailResponse check(String transactionId) {
        return client.request("GET", "/transactional/messages/" + HttpTransport.encodePathSegment(transactionId), null, null);
    }

    /** GET /transactional/log */
    public DashaMailResponse log() {
        return log(null);
    }

    public DashaMailResponse log(Map<String, Object> params) {
        return client.request("GET", "/transactional/log", params, null);
    }

    /** GET /transactional/stats */
    public DashaMailResponse stats() {
        return stats(null);
    }

    public DashaMailResponse stats(Map<String, Object> params) {
        return client.request("GET", "/transactional/stats", params, null);
    }
}
