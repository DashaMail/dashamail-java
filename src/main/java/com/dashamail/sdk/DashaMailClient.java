package com.dashamail.sdk;

import com.dashamail.sdk.resources.Account;
import com.dashamail.sdk.resources.Automations;
import com.dashamail.sdk.resources.Campaigns;
import com.dashamail.sdk.resources.Dialogs;
import com.dashamail.sdk.resources.Images;
import com.dashamail.sdk.resources.Lists;
import com.dashamail.sdk.resources.Reports;
import com.dashamail.sdk.resources.Router;
import com.dashamail.sdk.resources.Segments;
import com.dashamail.sdk.resources.Templates;
import com.dashamail.sdk.resources.Transactional;
import com.dashamail.sdk.resources.Workflows;

import java.net.http.HttpClient;

/**
 * Entry point of the DashaMail Java SDK.
 *
 * <pre>{@code
 * DashaMailClient dashamail = new DashaMailClient("YOUR_API_KEY");
 * DashaMailResponse lists = dashamail.lists().all();
 * dashamail.transactional().send("user@example.com", "sender@yourdomain.com", "<p>Hi!</p>");
 * }</pre>
 */
public class DashaMailClient {

    private final HttpTransport client;
    private final Lists lists;
    private final Segments segments;
    private final Campaigns campaigns;
    private final Automations automations;
    private final Workflows workflows;
    private final Templates templates;
    private final Reports reports;
    private final Transactional transactional;
    private final Account account;
    private final Dialogs dialogs;
    private final Router router;
    private final Images images;

    public DashaMailClient(String apiKey) {
        this(apiKey, null, 30, null, null);
    }

    /**
     * @param apiKey         Account API key — Личный кабинет → Аккаунт → API и интеграции.
     * @param baseUrl        Override the API origin, e.g. for a proxy or a mock server.
     * @param timeoutSeconds Request timeout in seconds. Default 30.
     * @param userAgent      Override the User-Agent header.
     * @param httpClient     Supply your own {@link HttpClient}; one is created otherwise.
     */
    public DashaMailClient(String apiKey, String baseUrl, int timeoutSeconds, String userAgent, HttpClient httpClient) {
        this.client = new HttpTransport(apiKey, baseUrl, timeoutSeconds, userAgent, httpClient);

        this.lists = new Lists(client);
        this.segments = new Segments(client);
        this.campaigns = new Campaigns(client);
        this.automations = new Automations(client);
        this.workflows = new Workflows(client);
        this.templates = new Templates(client);
        this.reports = new Reports(client);
        this.transactional = new Transactional(client);
        this.account = new Account(client);
        this.dialogs = new Dialogs(client);
        this.router = new Router(client);
        this.images = new Images(client);
    }

    /** The underlying HTTP client, for calling an endpoint the SDK doesn't wrap yet. */
    public HttpTransport client() {
        return client;
    }

    public Lists lists() {
        return lists;
    }

    public Segments segments() {
        return segments;
    }

    public Campaigns campaigns() {
        return campaigns;
    }

    public Automations automations() {
        return automations;
    }

    public Workflows workflows() {
        return workflows;
    }

    public Templates templates() {
        return templates;
    }

    public Reports reports() {
        return reports;
    }

    public Transactional transactional() {
        return transactional;
    }

    public Account account() {
        return account;
    }

    public Dialogs dialogs() {
        return dialogs;
    }

    public Router router() {
        return router;
    }

    public Images images() {
        return images;
    }
}
