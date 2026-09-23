package com.dashamail.sdk.resources;

import com.dashamail.sdk.DashaMailResponse;
import com.dashamail.sdk.HttpTransport;

import java.util.Map;

/**
 * Subscriber replies to campaigns, threaded per subscriber.
 * https://dashamail.ru/api/dialogs/
 */
public class Dialogs extends BaseResource {

    public Dialogs(HttpTransport client) {
        super(client);
    }

    /** GET /dialogs */
    public DashaMailResponse all() {
        return all(null);
    }

    public DashaMailResponse all(Map<String, Object> params) {
        return client.request("GET", "/dialogs", params, null);
    }

    /** GET /dialogs/{dialogId} */
    public DashaMailResponse get(Object dialogId) {
        return client.request("GET", "/dialogs/" + dialogId, null, null);
    }

    /** GET /dialogs/{dialogId}/messages */
    public DashaMailResponse messages(Object dialogId) {
        return messages(dialogId, null);
    }

    public DashaMailResponse messages(Object dialogId, Map<String, Object> params) {
        return client.request("GET", "/dialogs/" + dialogId + "/messages", params, null);
    }

    /** POST /dialogs/{dialogId}/reply — reply as the campaign's sender. */
    public DashaMailResponse reply(Object dialogId, String bodyText) {
        return reply(dialogId, bodyText, null);
    }

    public DashaMailResponse reply(Object dialogId, String bodyText, Map<String, Object> params) {
        return client.request("POST", "/dialogs/" + dialogId + "/reply", null, with(params, "body_text", bodyText));
    }

    /** POST /dialogs/{dialogId}/read */
    public DashaMailResponse markRead(Object dialogId) {
        return markRead(dialogId, null);
    }

    public DashaMailResponse markRead(Object dialogId, Map<String, Object> params) {
        return client.request("POST", "/dialogs/" + dialogId + "/read", null, params);
    }

    /** POST /dialogs/{dialogId}/unread */
    public DashaMailResponse markUnread(Object dialogId) {
        return client.request("POST", "/dialogs/" + dialogId + "/unread", null, null);
    }

    /** POST /dialogs/{dialogId}/close */
    public DashaMailResponse close(Object dialogId) {
        return client.request("POST", "/dialogs/" + dialogId + "/close", null, null);
    }

    /** POST /dialogs/{dialogId}/open */
    public DashaMailResponse open(Object dialogId) {
        return client.request("POST", "/dialogs/" + dialogId + "/open", null, null);
    }

    /** GET /dialogs/unread-count */
    public DashaMailResponse unreadCount() {
        return client.request("GET", "/dialogs/unread-count", null, null);
    }

    /** GET /dialogs/attachments/{attachmentId} — link to a reply's attachment. */
    public DashaMailResponse attachment(Object attachmentId) {
        return client.request("GET", "/dialogs/attachments/" + attachmentId, null, null);
    }
}
