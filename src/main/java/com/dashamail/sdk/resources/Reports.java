package com.dashamail.sdk.resources;

import com.dashamail.sdk.DashaMailResponse;
import com.dashamail.sdk.HttpTransport;

import java.util.List;
import java.util.Map;

/**
 * Campaign statistics: sent/delivered/opened/clicked/bounced, click and bounce
 * breakdowns, geography, mail clients, event feed, A/B test results.
 * https://dashamail.ru/api/reports/
 */
public class Reports extends BaseResource {

    public Reports(HttpTransport client) {
        super(client);
    }

    /** GET /reports/{campaignId}/summary */
    public DashaMailResponse summary(Object campaignId) {
        return summary(campaignId, null);
    }

    public DashaMailResponse summary(Object campaignId, Map<String, Object> params) {
        return client.request("GET", "/reports/" + campaignId + "/summary", params, null);
    }

    /** GET /reports/{campaignId}/timeline — metric values bucketed over time. */
    public DashaMailResponse timeline(Object campaignId) {
        return timeline(campaignId, null);
    }

    public DashaMailResponse timeline(Object campaignId, Map<String, Object> params) {
        return client.request("GET", "/reports/" + campaignId + "/timeline", params, null);
    }

    /** GET /reports/{campaignId}/variants — A/B test results. */
    public DashaMailResponse ab(Object campaignId) {
        return client.request("GET", "/reports/" + campaignId + "/variants", null, null);
    }

    /** POST /reports/compare — compare metrics across periods/campaigns/lists. */
    public DashaMailResponse compare(List<Object> periods) {
        return compare(periods, null);
    }

    public DashaMailResponse compare(List<Object> periods, Map<String, Object> params) {
        return client.request("POST", "/reports/compare", null, with(params, "periods", periods));
    }

    /** GET /reports/{campaignId}/{metric} — recipient list for one event. */
    public DashaMailResponse metric(Object campaignId, String metric) {
        return metric(campaignId, metric, null);
    }

    public DashaMailResponse metric(Object campaignId, String metric, Map<String, Object> params) {
        return client.request("GET", "/reports/" + campaignId + "/" + metric, params, null);
    }

    /** GET /reports/{campaignId}/sent */
    public DashaMailResponse sent(Object campaignId) {
        return metric(campaignId, "sent");
    }

    /** GET /reports/{campaignId}/delivered */
    public DashaMailResponse delivered(Object campaignId) {
        return metric(campaignId, "delivered");
    }

    /** GET /reports/{campaignId}/opened */
    public DashaMailResponse opened(Object campaignId) {
        return metric(campaignId, "opened");
    }

    /** GET /reports/{campaignId}/clicked */
    public DashaMailResponse clicked(Object campaignId) {
        return metric(campaignId, "clicked");
    }

    /** GET /reports/{campaignId}/bounced */
    public DashaMailResponse bounced(Object campaignId) {
        return metric(campaignId, "bounced");
    }

    /** GET /reports/{campaignId}/complained */
    public DashaMailResponse complained(Object campaignId) {
        return metric(campaignId, "complained");
    }

    /** GET /reports/{campaignId}/unsubscribed */
    public DashaMailResponse unsubscribed(Object campaignId) {
        return metric(campaignId, "unsubscribed");
    }

    /** GET /reports/{campaignId}/events — full event feed with filters. */
    public DashaMailResponse events(Object campaignId) {
        return events(campaignId, null);
    }

    public DashaMailResponse events(Object campaignId, Map<String, Object> params) {
        return client.request("GET", "/reports/" + campaignId + "/events", params, null);
    }

    /** GET /reports/{campaignId}/clickstat — clicks broken down by link. */
    public DashaMailResponse clickstat(Object campaignId) {
        return client.request("GET", "/reports/" + campaignId + "/clickstat", null, null);
    }

    /** GET /reports/{campaignId}/userclicks — who clicked a specific link. */
    public DashaMailResponse userclicks(Object campaignId, String url) {
        return client.request("GET", "/reports/" + campaignId + "/userclicks", with(null, "url", url), null);
    }

    /** GET /reports/{campaignId}/bouncestat — bounces broken down by SMTP code. */
    public DashaMailResponse bouncestat(Object campaignId) {
        return client.request("GET", "/reports/" + campaignId + "/bouncestat", null, null);
    }

    /** GET /reports/{campaignId}/domains — metrics broken down by recipient mail domain. */
    public DashaMailResponse domains(Object campaignId) {
        return domains(campaignId, null);
    }

    public DashaMailResponse domains(Object campaignId, Map<String, Object> params) {
        return client.request("GET", "/reports/" + campaignId + "/domains", params, null);
    }

    /** GET /reports/{campaignId}/geo — geography of opens. */
    public DashaMailResponse geo(Object campaignId) {
        return client.request("GET", "/reports/" + campaignId + "/geo", null, null);
    }

    /** GET /reports/{campaignId}/clients — mail clients and devices. */
    public DashaMailResponse clients(Object campaignId) {
        return client.request("GET", "/reports/" + campaignId + "/clients", null, null);
    }

    /** GET /reports/{campaignId}/codes — confirmation codes. */
    public DashaMailResponse codes(Object campaignId) {
        return codes(campaignId, null);
    }

    public DashaMailResponse codes(Object campaignId, Map<String, Object> params) {
        return client.request("GET", "/reports/" + campaignId + "/codes", params, null);
    }
}
