package com.dashamail.sdk.resources;

import com.dashamail.sdk.DashaMailResponse;
import com.dashamail.sdk.HttpTransport;

import java.util.Map;

/**
 * Bulk campaigns: draft, build, launch, pause, A/B tests, attachments, folders.
 * https://dashamail.ru/api/campaigns/
 */
public class Campaigns extends BaseResource {

    public Campaigns(HttpTransport client) {
        super(client);
    }

    /** GET /campaigns */
    public DashaMailResponse all() {
        return all(null);
    }

    public DashaMailResponse all(Map<String, Object> params) {
        return client.request("GET", "/campaigns", params, null);
    }

    /** GET /campaigns/{campaignId} */
    public DashaMailResponse get(Object campaignId) {
        return get(campaignId, null);
    }

    public DashaMailResponse get(Object campaignId, Map<String, Object> params) {
        return client.request("GET", "/campaigns/" + campaignId, params, null);
    }

    /** POST /campaigns — requires list_id, subject, from_email, from_name; see the API docs for the rest. */
    public DashaMailResponse create(Map<String, Object> params) {
        return client.request("POST", "/campaigns", null, params);
    }

    /** PUT /campaigns/{campaignId} */
    public DashaMailResponse update(Object campaignId, Map<String, Object> params) {
        return client.request("PUT", "/campaigns/" + campaignId, null, params);
    }

    /** DELETE /campaigns/{campaignId} */
    public DashaMailResponse delete(Object campaignId) {
        return client.request("DELETE", "/campaigns/" + campaignId, null, null);
    }

    /** POST /campaigns/{campaignId}/copy */
    public DashaMailResponse copy(Object campaignId) {
        return copy(campaignId, null);
    }

    public DashaMailResponse copy(Object campaignId, Map<String, Object> params) {
        return client.request("POST", "/campaigns/" + campaignId + "/copy", null, params);
    }

    /** POST /campaigns/{campaignId}/pause */
    public DashaMailResponse pause(Object campaignId) {
        return client.request("POST", "/campaigns/" + campaignId + "/pause", null, null);
    }

    /** POST /campaigns/{campaignId}/resume */
    public DashaMailResponse resume(Object campaignId) {
        return client.request("POST", "/campaigns/" + campaignId + "/resume", null, null);
    }

    /** POST /campaigns/{campaignId}/schedule */
    public DashaMailResponse schedule(Object campaignId, String deliveryTime) {
        return schedule(campaignId, deliveryTime, null);
    }

    public DashaMailResponse schedule(Object campaignId, String deliveryTime, Map<String, Object> params) {
        return client.request("POST", "/campaigns/" + campaignId + "/schedule", null, with(params, "delivery_time", deliveryTime));
    }

    /** POST /campaigns/{campaignId}/send — send a draft right now. */
    public DashaMailResponse send(Object campaignId) {
        return client.request("POST", "/campaigns/" + campaignId + "/send", null, null);
    }

    /** POST /campaigns/{campaignId}/unschedule — pull a scheduled campaign back to DRAFT. */
    public DashaMailResponse unschedule(Object campaignId) {
        return client.request("POST", "/campaigns/" + campaignId + "/unschedule", null, null);
    }

    /** POST /campaigns/{campaignId}/test — send a test copy to your own address. */
    public DashaMailResponse test(Object campaignId, String email) {
        return client.request("POST", "/campaigns/" + campaignId + "/test", null, with(null, "email", email));
    }

    /** GET /campaigns/{campaignId}/preview — browser preview link. */
    public DashaMailResponse preview(Object campaignId) {
        return client.request("GET", "/campaigns/" + campaignId + "/preview", null, null);
    }

    /** GET /campaigns/{campaignId}/estimate — how many emails would be sent. */
    public DashaMailResponse estimate(Object campaignId) {
        return client.request("GET", "/campaigns/" + campaignId + "/estimate", null, null);
    }

    /** POST /campaigns/{campaignId}/resend — resend to recipients who did not open. */
    public DashaMailResponse resend(Object campaignId) {
        return resend(campaignId, null);
    }

    public DashaMailResponse resend(Object campaignId, Map<String, Object> params) {
        return client.request("POST", "/campaigns/" + campaignId + "/resend", null, params);
    }

    /** GET /campaigns/{campaignId}/attachments */
    public DashaMailResponse getAttachments(Object campaignId) {
        return client.request("GET", "/campaigns/" + campaignId + "/attachments", null, null);
    }

    /** POST /campaigns/{campaignId}/attachments — attach a file by URL. */
    public DashaMailResponse addAttachment(Object campaignId, String url) {
        return addAttachment(campaignId, url, null);
    }

    public DashaMailResponse addAttachment(Object campaignId, String url, Map<String, Object> params) {
        return client.request("POST", "/campaigns/" + campaignId + "/attachments", null, with(params, "url", url));
    }

    /** DELETE /campaigns/{campaignId}/attachments/{id} */
    public DashaMailResponse deleteAttachment(Object campaignId, Object attachmentId) {
        return client.request("DELETE", "/campaigns/" + campaignId + "/attachments/" + attachmentId, null, null);
    }

    /** GET /campaigns/folders */
    public DashaMailResponse getFolders() {
        return getFolders(null);
    }

    public DashaMailResponse getFolders(Map<String, Object> params) {
        return client.request("GET", "/campaigns/folders", params, null);
    }

    /** POST /campaigns/{campaignId}/move — move a campaign into a folder. */
    public DashaMailResponse moveToFolder(Object campaignId, Object folderId) {
        return client.request("POST", "/campaigns/" + campaignId + "/move", null, with(null, "folder_id", folderId));
    }

    // -- A/B testing ------------------------------------------------------------

    /** POST /campaigns/{campaignId}/ab — turn a draft into an A/B test. */
    public DashaMailResponse createAb(Object campaignId) {
        return createAb(campaignId, null);
    }

    public DashaMailResponse createAb(Object campaignId, Map<String, Object> params) {
        return client.request("POST", "/campaigns/" + campaignId + "/ab", null, params);
    }

    /** GET /campaigns/{campaignId}/ab */
    public DashaMailResponse getAb(Object campaignId) {
        return client.request("GET", "/campaigns/" + campaignId + "/ab", null, null);
    }

    /** PUT /campaigns/{campaignId}/ab */
    public DashaMailResponse updateAb(Object campaignId, Map<String, Object> params) {
        return client.request("PUT", "/campaigns/" + campaignId + "/ab", null, params);
    }

    /** DELETE /campaigns/{campaignId}/ab — dismantle the A/B test back into a plain campaign. */
    public DashaMailResponse deleteAb(Object campaignId) {
        return client.request("DELETE", "/campaigns/" + campaignId + "/ab", null, null);
    }

    /** POST /campaigns/{campaignId}/ab/winner — pick the winning variant and schedule the rest. */
    public DashaMailResponse abWinner(Object campaignId, Object variantId, String deliveryTime) {
        Map<String, Object> body = with(with(null, "variant_id", variantId), "delivery_time", deliveryTime);
        return client.request("POST", "/campaigns/" + campaignId + "/ab/winner", null, body);
    }

    /** DELETE /campaigns/{campaignId}/ab/winner — cancel a previously chosen winner. */
    public DashaMailResponse cancelAbWinner(Object campaignId) {
        return client.request("DELETE", "/campaigns/" + campaignId + "/ab/winner", null, null);
    }
}
