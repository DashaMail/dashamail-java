package com.dashamail.sdk.resources;

import com.dashamail.sdk.DashaMailResponse;
import com.dashamail.sdk.HttpTransport;

import java.util.List;
import java.util.Map;

/**
 * Address lists (address books), their subscribers and merge fields.
 * https://dashamail.ru/api/lists/
 */
public class Lists extends BaseResource {

    public Lists(HttpTransport client) {
        super(client);
    }

    /** GET /lists — all address lists, newest first. */
    public DashaMailResponse all() {
        return all(null);
    }

    public DashaMailResponse all(Map<String, Object> params) {
        return client.request("GET", "/lists", params, null);
    }

    /** GET /lists/{listId} */
    public DashaMailResponse get(Object listId) {
        return get(listId, null);
    }

    public DashaMailResponse get(Object listId, Map<String, Object> params) {
        return client.request("GET", "/lists/" + listId, params, null);
    }

    /** POST /lists */
    public DashaMailResponse create(String name) {
        return create(name, null);
    }

    public DashaMailResponse create(String name, Map<String, Object> params) {
        return client.request("POST", "/lists", null, with(params, "name", name));
    }

    /** PUT /lists/{listId} */
    public DashaMailResponse update(Object listId, Map<String, Object> params) {
        return client.request("PUT", "/lists/" + listId, null, params);
    }

    /** DELETE /lists/{listId} */
    public DashaMailResponse delete(Object listId) {
        return client.request("DELETE", "/lists/" + listId, null, null);
    }

    // -- Members ------------------------------------------------------------

    /** GET /lists/{listId}/members — start, limit, order, state, email, member_id, segment_id */
    public DashaMailResponse members(Object listId) {
        return members(listId, null);
    }

    public DashaMailResponse members(Object listId, Map<String, Object> params) {
        return client.request("GET", "/lists/" + listId + "/members", params, null);
    }

    /** GET /lists/{listId}/members/{email} */
    public DashaMailResponse getMember(Object listId, String email) {
        return client.request("GET", "/lists/" + listId + "/members/" + HttpTransport.encodePathSegment(email), null, null);
    }

    /** POST /lists/{listId}/members — merge_1..merge_N and the rest go in params. */
    public DashaMailResponse addMember(Object listId, String email) {
        return addMember(listId, email, null);
    }

    public DashaMailResponse addMember(Object listId, String email, Map<String, Object> params) {
        return client.request("POST", "/lists/" + listId + "/members", null, with(params, "email", email));
    }

    /** POST /lists/{listId}/members/batch — batch is a list of member maps, each at least {"email": ...}. */
    public DashaMailResponse addMembersBatch(Object listId, List<Object> batch) {
        return addMembersBatch(listId, batch, null);
    }

    public DashaMailResponse addMembersBatch(Object listId, List<Object> batch, Map<String, Object> params) {
        return client.request("POST", "/lists/" + listId + "/members/batch", null, with(params, "batch", batch));
    }

    /** POST /lists/{listId}/members/import — import subscribers from a file. */
    public DashaMailResponse importMembers(Object listId, String email, String type) {
        return importMembers(listId, email, type, null);
    }

    public DashaMailResponse importMembers(Object listId, String email, String type, Map<String, Object> params) {
        Map<String, Object> body = with(with(params, "email", email), "type", type);
        return client.request("POST", "/lists/" + listId + "/members/import", null, body);
    }

    /** GET /lists/{listId}/members/import — result of the last import job. */
    public DashaMailResponse getImportResult(Object listId) {
        return client.request("GET", "/lists/" + listId + "/members/import", null, null);
    }

    /** GET /lists/{listId}/import-history */
    public DashaMailResponse getImportHistory(Object listId) {
        return getImportHistory(listId, null);
    }

    public DashaMailResponse getImportHistory(Object listId, Map<String, Object> params) {
        return client.request("GET", "/lists/" + listId + "/import-history", params, null);
    }

    /** PUT /lists/{listId}/members/{email} */
    public DashaMailResponse updateMember(Object listId, String email, Map<String, Object> params) {
        return client.request("PUT", "/lists/" + listId + "/members/" + HttpTransport.encodePathSegment(email), null, params);
    }

    /** DELETE /lists/{listId}/members/{email} */
    public DashaMailResponse deleteMember(Object listId, String email, Object memberId) {
        Map<String, Object> body = with(null, "member_id", memberId);
        return client.request("DELETE", "/lists/" + listId + "/members/" + HttpTransport.encodePathSegment(email), null, body);
    }

    /** GET /members — find a subscriber address across every list in the account. */
    public DashaMailResponse findMember(String email) {
        return client.request("GET", "/members", with(null, "email", email), null);
    }

    /** POST /lists/{listId}/members/{email}/unsubscribe */
    public DashaMailResponse unsubscribeMember(Object listId, String email) {
        return unsubscribeMember(listId, email, null);
    }

    public DashaMailResponse unsubscribeMember(Object listId, String email, Map<String, Object> params) {
        return client.request("POST", "/lists/" + listId + "/members/" + HttpTransport.encodePathSegment(email) + "/unsubscribe", null, params);
    }

    /** POST /lists/{listId}/members/{email}/move — move a subscriber to another list. */
    public DashaMailResponse moveMember(Object listId, String email, Object toListId, Object memberId) {
        Map<String, Object> body = with(with(null, "to_list_id", toListId), "member_id", memberId);
        return client.request("POST", "/lists/" + listId + "/members/" + HttpTransport.encodePathSegment(email) + "/move", null, body);
    }

    /** POST /lists/{listId}/members/{email}/copy — copy a subscriber to another list. */
    public DashaMailResponse copyMember(Object listId, String email, Object toListId, Object memberId) {
        Map<String, Object> body = with(with(null, "to_list_id", toListId), "member_id", memberId);
        return client.request("POST", "/lists/" + listId + "/members/" + HttpTransport.encodePathSegment(email) + "/copy", null, body);
    }

    /** GET /lists/{listId}/members/{email}/activity */
    public DashaMailResponse memberActivity(Object listId, String email) {
        return memberActivity(listId, email, null);
    }

    public DashaMailResponse memberActivity(Object listId, String email, Map<String, Object> params) {
        return client.request("GET", "/lists/" + listId + "/members/" + HttpTransport.encodePathSegment(email) + "/activity", params, null);
    }

    /** GET /lists/{listId}/last-status — current subscription state of an address. */
    public DashaMailResponse lastStatus(Object listId, String email) {
        return client.request("GET", "/lists/" + listId + "/last-status", with(null, "email", email), null);
    }

    /** GET /lists/{listId}/check-email — validate an address before subscribing it. */
    public DashaMailResponse checkEmail(Object listId, String email) {
        return client.request("GET", "/lists/" + listId + "/check-email", with(null, "email", email), null);
    }

    /** POST /lists/{listId}/clean — purge bounced/complained/unsubscribed members. */
    public DashaMailResponse clean(Object listId) {
        return clean(listId, null);
    }

    public DashaMailResponse clean(Object listId, Map<String, Object> params) {
        return client.request("POST", "/lists/" + listId + "/clean", null, params);
    }

    /** GET /lists/{listId}/unsubscribed */
    public DashaMailResponse unsubscribed(Object listId) {
        return unsubscribed(listId, null);
    }

    public DashaMailResponse unsubscribed(Object listId, Map<String, Object> params) {
        return client.request("GET", "/lists/" + listId + "/unsubscribed", params, null);
    }

    /** GET /lists/{listId}/complaints */
    public DashaMailResponse complaints(Object listId) {
        return complaints(listId, null);
    }

    public DashaMailResponse complaints(Object listId, Map<String, Object> params) {
        return client.request("GET", "/lists/" + listId + "/complaints", params, null);
    }

    // -- Merge fields ---------------------------------------------------------

    /** POST /lists/{listId}/fields — type is one of the merge field types, e.g. "text", "choice". */
    public DashaMailResponse addField(Object listId, String type) {
        return addField(listId, type, null);
    }

    public DashaMailResponse addField(Object listId, String type, Map<String, Object> params) {
        return client.request("POST", "/lists/" + listId + "/fields", null, with(params, "type", type));
    }

    /** PUT /lists/{listId}/fields/{mergeId} */
    public DashaMailResponse updateField(Object listId, Object mergeId) {
        return updateField(listId, mergeId, null);
    }

    public DashaMailResponse updateField(Object listId, Object mergeId, Map<String, Object> params) {
        return client.request("PUT", "/lists/" + listId + "/fields/" + mergeId, null, with(params, "merge_id", mergeId));
    }

    /** DELETE /lists/{listId}/fields/{mergeId} */
    public DashaMailResponse deleteField(Object listId, Object mergeId) {
        return client.request("DELETE", "/lists/" + listId + "/fields/" + mergeId, null, null);
    }
}
