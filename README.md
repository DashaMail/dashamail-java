# dashamail-java

Official Java SDK for the [DashaMail REST API v2](https://dashamail.ru/api/) — address
lists, campaigns, automations, transactional email, reports, dialogs, inbound mail routing
and image optimization, from a single client with no dependencies beyond the JDK
(`java.net.http.HttpClient`). Since the JDK ships no JSON library, this SDK includes a small
dependency-free JSON codec (`com.dashamail.sdk.internal.Json`) rather than pulling in
Jackson/Gson/org.json.

## Requirements

- Java 11 or newer (needs `java.net.http.HttpClient`, built in since Java 11)

## Installation

Maven:

```xml
<dependency>
    <groupId>com.dashamail</groupId>
    <artifactId>dashamail-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

Gradle:

```groovy
implementation 'com.dashamail:dashamail-java:1.0.0'
```

## Getting an API key

Личный кабинет → Аккаунт → API и интеграции. Treat it like a password: it acts on behalf
of the whole account.

## Quickstart

```java
import com.dashamail.sdk.DashaMailClient;
import com.dashamail.sdk.DashaMailResponse;

DashaMailClient dashamail = new DashaMailClient("YOUR_API_KEY");

DashaMailResponse balance = dashamail.account().balance();
System.out.println(balance.get("balance"));

DashaMailResponse created = dashamail.lists().create("Newsletter");
Map<String, Object> fields = new HashMap<>();
fields.put("merge_1", "Иван");
dashamail.lists().addMember(created.get("list_id"), "subscriber@example.com", fields);

Map<String, Object> params = new HashMap<>();
params.put("subject", "Добро пожаловать");
dashamail.transactional().send(
    "subscriber@example.com",
    "sender@yourdomain.com",
    "<p>Спасибо за подписку!</p>",
    params
);
```

See [`examples/QuickStart.java`](examples/QuickStart.java) for a fuller walkthrough, and
[dashamail.ru/api](https://dashamail.ru/api/) for the full parameter reference of every
endpoint — the SDK mirrors it method-for-method. Optional parameters are passed as a
trailing `Map<String, Object>` matching the API's field names (snake_case, e.g. `merge_1`,
`from_email`); every method that has one also has an overload without it.

## Resources

`DashaMailClient` exposes one getter per section of the API, all under the
`com.dashamail.sdk.resources` package. Every method call is synchronous (blocking) and
returns a [`DashaMailResponse`](src/main/java/com/dashamail/sdk/DashaMailResponse.java)
(wraps the `data` payload, iterable when it's a list) or throws a `DashaMailApiException`.

| Getter               | Class                          | Covers |
|----------------------|-----------------------------------|--------|
| `.lists()`           | `resources.Lists`         | Address lists, subscribers, merge fields, imports |
| `.segments()`        | `resources.Segments`      | Saved subscriber segments |
| `.campaigns()`       | `resources.Campaigns`     | Bulk campaigns: draft, launch, pause, A/B tests, attachments, folders |
| `.automations()`     | `resources.Automations`   | Event-triggered emails |
| `.workflows()`       | `resources.Workflows`     | Visual-builder automation scenarios |
| `.templates()`       | `resources.Templates`     | Saved HTML templates and templated campaigns |
| `.reports()`         | `resources.Reports`       | Campaign statistics, events, click/bounce/geo breakdowns, A/B results |
| `.transactional()`   | `resources.Transactional` | One-off transactional email: send, status, log, stats |
| `.account()`         | `resources.Account`       | Balance, senders, sending domains, webhooks |
| `.dialogs()`         | `resources.Dialogs`       | Subscriber replies to campaigns |
| `.router()`          | `resources.Router`        | Inbound mail: domains, routing rules, stored messages |
| `.images()`          | `resources.Images`        | Resize/recompress an image before using it in a campaign |

**Naming note:** public types are prefixed with `DashaMail` where the bare name would be
too generic and likely to collide with other libraries in a consuming project —
`DashaMailClient`, `DashaMailResponse`, `DashaMailBinaryResponse`, `DashaMailApiException`
and its subclasses. The lower-level HTTP layer is `HttpTransport`, and resource classes
(`Lists`, `Campaigns`, ...) live in the `com.dashamail.sdk.resources` package instead,
since they're normally reached through `DashaMailClient` rather than named directly.

## Working with responses

`DashaMailResponse.getData()` holds the parsed payload as plain JDK types — a
`Map<String, Object>` for an object, a `List<Object>` for a list, or a scalar — since the
API's shape isn't known statically:

```java
DashaMailResponse members = dashamail.lists().members(listId, Map.of("limit", 100));

for (Object member : members) {                // DashaMailResponse is Iterable
    Map<?, ?> dict = (Map<?, ?>) member;
    System.out.println(dict.get("email"));
}

members.getData();                              // the raw Map/List, if you'd rather not iterate
members.hasMore();                              // true if there's another page (see Pagination below)
```

## Pagination

List endpoints (`lists().members()`, `lists().unsubscribed()`, `transactional().log()`,
...) don't return a total count — DashaMail tells you instead whether there's another page:

```java
int start = 0;
while (true) {
    DashaMailResponse page = dashamail.lists().members(listId, Map.of("start", start, "limit", 100));
    for (Object member : page) { /* ... */ }
    start += page.getLimit() != null ? page.getLimit() : 0;
    if (!page.hasMore()) break;
}
```

## Error handling

Every non-2xx response throws a subclass of `DashaMailApiException`, chosen by HTTP status:

| Exception                          | HTTP status |
|-------------------------------------|------|
| `DashaMailAuthenticationException`  | 401 |
| `DashaMailPaymentRequiredException` | 402 |
| `DashaMailAuthorizationException`   | 403 |
| `DashaMailNotFoundException`        | 404 |
| `DashaMailConflictException`        | 409 |
| `DashaMailPayloadTooLargeException` | 413 |
| `DashaMailValidationException`      | 422 |
| `DashaMailRateLimitException`       | 429 |
| `DashaMailServerException`          | 5xx |

All exceptions are unchecked (`RuntimeException`), so calling code isn't forced to declare
or catch them.

```java
try {
    dashamail.campaigns().create(Map.of(
        "list_id", 1, "subject", "Hi", "from_email", "a@b.com", "from_name", "A"
    ));
} catch (DashaMailRateLimitException e) {
    Thread.sleep((e.getRetryAfter() != null ? e.getRetryAfter() : 60) * 1000L);
} catch (DashaMailApiException e) {
    // e.getApiCode()     — DashaMail's own stable error code (see https://dashamail.ru/api/errors/)
    // e.getHttpStatus()  — the HTTP status of the response
    // e.getDetails()     — any extra structured context the API attached
    System.err.println(e.getApiCode() + ": " + e.getMessage());
}
```

A request that never got an HTTP response at all (DNS, TLS, timeout...) throws
`DashaMailNetworkException` instead.

## Images

`POST /images/optimize` is the one endpoint that isn't plain JSON in and out:

```java
// From bytes already in memory:
byte[] raw = Files.readAllBytes(Path.of("banner.png"));
DashaMailResponse result = dashamail.images().optimize(raw, Map.of("max_width", 1600));
Files.write(Path.of("banner-optimized.png"), Base64.getDecoder().decode((String) result.get("image")));

// Straight from disk, without loading it into a byte[] first:
DashaMailResponse result2 = dashamail.images().optimizeFile("/path/to/banner.png");

// Same, but skip the base64 round-trip and get raw bytes back:
DashaMailBinaryResponse binary = dashamail.images().optimizeFileBinary("/path/to/banner.png");
binary.saveTo("/path/to/banner-optimized.png");
```

## Advanced configuration

```java
DashaMailClient dashamail = new DashaMailClient(
    "YOUR_API_KEY",
    "https://api.dashamail.com/v2",  // baseUrl override, e.g. for testing/proxying
    60,                              // timeoutSeconds, default 30
    "my-app/1.0 (+dashamail-java)",  // userAgent override
    myHttpClient                     // reuse your own java.net.http.HttpClient instead of creating one
);

// Escape hatch for an endpoint the SDK doesn't wrap yet:
dashamail.client().request("GET", "/some/new/endpoint", Map.of("foo", "bar"), null);
```

## Testing

```bash
mvn test
```

## License

MIT, see [LICENSE](LICENSE).
