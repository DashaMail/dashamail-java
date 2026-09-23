package com.dashamail.sdk;

import com.dashamail.sdk.internal.Json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Thin HTTP layer over the DashaMail REST API v2 (https://dashamail.ru/api/).
 *
 * <p>Talks JSON over {@link java.net.http.HttpClient} (built into the JDK
 * since Java 11) using the hand-written {@link Json} codec — no third-party
 * dependencies. Resource classes ({@code com.dashamail.sdk.resources.*})
 * build on top of {@link #request}/{@link #requestMultipart}; most
 * applications should go through {@link DashaMailClient} rather than use
 * this class directly.</p>
 */
public class HttpTransport {

    public static final String DEFAULT_BASE_URL = "https://api.dashamail.com/v2";
    public static final String VERSION = "1.0.0";

    private static final Map<String, String> MIME_TYPES = new HashMap<>();
    static {
        MIME_TYPES.put(".jpg", "image/jpeg");
        MIME_TYPES.put(".jpeg", "image/jpeg");
        MIME_TYPES.put(".png", "image/png");
        MIME_TYPES.put(".gif", "image/gif");
        MIME_TYPES.put(".webp", "image/webp");
        MIME_TYPES.put(".bmp", "image/bmp");
        MIME_TYPES.put(".svg", "image/svg+xml");
    }

    private final String apiKey;
    private final String baseUrl;
    private final String userAgent;
    private final int timeoutSeconds;
    private final HttpClient httpClient;

    public HttpTransport(String apiKey) {
        this(apiKey, null, 30, null, null);
    }

    /**
     * @param apiKey         Account API key — Личный кабинет → Аккаунт → API и интеграции.
     * @param baseUrl        Override the API origin, e.g. for a proxy or a mock server.
     * @param timeoutSeconds Request timeout in seconds. Default 30.
     * @param userAgent      Override the User-Agent header.
     * @param httpClient     Supply your own {@link HttpClient}; one is created otherwise.
     */
    public HttpTransport(String apiKey, String baseUrl, int timeoutSeconds, String userAgent, HttpClient httpClient) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("DashaMail API key must be a non-empty string.");
        }
        this.apiKey = apiKey;
        this.baseUrl = trimTrailingSlashes(baseUrl != null ? baseUrl : DEFAULT_BASE_URL);
        this.timeoutSeconds = timeoutSeconds;
        this.userAgent = userAgent != null ? userAgent : "dashamail-java/" + VERSION;
        this.httpClient = httpClient != null ? httpClient : HttpClient.newHttpClient();
    }

    /**
     * JSON request. Body is sent as application/json; on 2xx the decoded
     * response.data is returned wrapped in a {@link DashaMailResponse}, on
     * error a {@link DashaMailApiException} subclass is thrown. Returns
     * null for a 204 No Content.
     */
    public DashaMailResponse request(String method, String path, Map<String, Object> query, Map<String, Object> body) {
        String url = buildUrl(path, query);
        byte[] payload = null;
        Map<String, String> headers = baseHeaders();
        if (body != null) {
            payload = Json.stringify(body).getBytes(StandardCharsets.UTF_8);
            headers.put("Content-Type", "application/json");
        }

        RawResponse raw = execute(method, url, headers, payload);
        return parseJsonResponse(raw.status, raw.body);
    }

    /** multipart/form-data request with a single file field — used by POST /images/optimize. */
    public DashaMailResponse requestMultipart(String method, String path, Map<String, Object> query, Map<String, Object> fields, String fileFieldName, String filePath, String fileName, String mimeType) {
        MultipartBody multipart = buildMultipart(fields, fileFieldName, filePath, fileName, mimeType);
        Map<String, String> headers = baseHeaders();
        headers.put("Content-Type", multipart.contentType);

        RawResponse raw = execute(method, buildUrl(path, query), headers, multipart.body);
        return parseJsonResponse(raw.status, raw.body);
    }

    /** Same as {@link #requestMultipart}, but returns a {@link DashaMailBinaryResponse} instead of decoding JSON. */
    public DashaMailBinaryResponse requestMultipartBinary(String method, String path, Map<String, Object> query, Map<String, Object> fields, String fileFieldName, String filePath, String fileName, String mimeType) {
        MultipartBody multipart = buildMultipart(fields, fileFieldName, filePath, fileName, mimeType);
        Map<String, String> headers = baseHeaders();
        headers.put("Content-Type", multipart.contentType);

        RawResponse raw = execute(method, buildUrl(path, query), headers, multipart.body);
        return parseBinaryResponse(raw.status, raw.body, raw.contentType);
    }

    /** Same as {@link #request}, but returns a {@link DashaMailBinaryResponse} instead of decoding JSON. */
    public DashaMailBinaryResponse requestBinary(String method, String path, Map<String, Object> query, Map<String, Object> body) {
        String url = buildUrl(path, query);
        byte[] payload = null;
        Map<String, String> headers = baseHeaders();
        if (body != null) {
            payload = Json.stringify(body).getBytes(StandardCharsets.UTF_8);
            headers.put("Content-Type", "application/json");
        }

        RawResponse raw = execute(method, url, headers, payload);
        return parseBinaryResponse(raw.status, raw.body, raw.contentType);
    }

    private Map<String, String> baseHeaders() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Accept", "application/json");
        headers.put("User-Agent", userAgent);
        return headers;
    }

    private String buildUrl(String path, Map<String, Object> query) {
        StringBuilder url = new StringBuilder(baseUrl);
        url.append('/').append(path.startsWith("/") ? path.substring(1) : path);
        if (query != null && !query.isEmpty()) {
            StringBuilder qs = new StringBuilder();
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                if (qs.length() > 0) {
                    qs.append('&');
                }
                qs.append(encodePathSegment(entry.getKey())).append('=').append(encodePathSegment(String.valueOf(entry.getValue())));
            }
            if (qs.length() > 0) {
                url.append('?').append(qs);
            }
        }
        return url.toString();
    }

    /** Percent-encodes a single path segment or query value/key (RFC 3986 style: space becomes %20, not +). */
    public static String encodePathSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static String trimTrailingSlashes(String value) {
        int end = value.length();
        while (end > 0 && value.charAt(end - 1) == '/') {
            end--;
        }
        return value.substring(0, end);
    }

    private static String guessMimeType(String filePath) {
        int dot = filePath.lastIndexOf('.');
        String ext = dot >= 0 ? filePath.substring(dot).toLowerCase() : "";
        return MIME_TYPES.getOrDefault(ext, "application/octet-stream");
    }

    private static final class MultipartBody {
        final byte[] body;
        final String contentType;

        MultipartBody(byte[] body, String contentType) {
            this.body = body;
            this.contentType = contentType;
        }
    }

    private MultipartBody buildMultipart(Map<String, Object> fields, String fileFieldName, String filePath, String fileName, String mimeType) {
        Path path = Path.of(filePath);
        if (!Files.isRegularFile(path) || !Files.isReadable(path)) {
            throw new IllegalArgumentException("File not readable: " + filePath);
        }

        String resolvedName = fileName != null ? fileName : path.getFileName().toString();
        String resolvedMime = mimeType != null ? mimeType : guessMimeType(filePath);
        byte[] fileContent;
        try {
            fileContent = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file: " + filePath, e);
        }

        String boundary = "----DashaMailFormBoundary" + randomHex(16);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            if (fields != null) {
                for (Map.Entry<String, Object> entry : fields.entrySet()) {
                    if (entry.getValue() == null) {
                        continue;
                    }
                    out.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
                    out.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                    out.write(String.valueOf(entry.getValue()).getBytes(StandardCharsets.UTF_8));
                    out.write("\r\n".getBytes(StandardCharsets.UTF_8));
                }
            }
            out.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
            out.write(("Content-Disposition: form-data; name=\"" + fileFieldName + "\"; filename=\"" + resolvedName + "\"\r\n").getBytes(StandardCharsets.UTF_8));
            out.write(("Content-Type: " + resolvedMime + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            out.write(fileContent);
            out.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return new MultipartBody(out.toByteArray(), "multipart/form-data; boundary=" + boundary);
    }

    private static String randomHex(int numBytes) {
        byte[] bytes = new byte[numBytes];
        new SecureRandom().nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private DashaMailResponse parseJsonResponse(int status, byte[] rawBody) {
        if (status == 204 || rawBody == null || rawBody.length == 0) {
            return null;
        }

        Object decoded;
        try {
            decoded = Json.parse(new String(rawBody, StandardCharsets.UTF_8));
        } catch (Json.JsonException e) {
            String preview = new String(rawBody, StandardCharsets.UTF_8);
            if (preview.length() > 500) {
                preview = preview.substring(0, 500);
            }
            throw new DashaMailNetworkException("DashaMail API returned a non-JSON response (HTTP " + status + "): " + preview, e);
        }

        Map<String, Object> decodedMap = decoded instanceof Map ? (Map<String, Object>) decoded : null;

        if (status >= 200 && status < 300) {
            Object response = decodedMap != null && decodedMap.containsKey("response") ? decodedMap.get("response") : decoded;
            Map<String, Object> responseMap = response instanceof Map ? (Map<String, Object>) response : null;
            Object data = responseMap != null && responseMap.containsKey("data") ? responseMap.get("data") : response;

            String message = null;
            if (responseMap != null && responseMap.get("msg") instanceof Map) {
                Object text = ((Map<String, Object>) responseMap.get("msg")).get("text");
                message = text != null ? text.toString() : null;
            }

            Map<String, Object> meta = null;
            if (decodedMap != null && decodedMap.get("meta") instanceof Map) {
                meta = (Map<String, Object>) decodedMap.get("meta");
            }

            return new DashaMailResponse(data, meta, message);
        }

        Map<String, Object> error = decodedMap != null && decodedMap.get("error") instanceof Map
            ? (Map<String, Object>) decodedMap.get("error")
            : defaultError(status);
        throw DashaMailApiException.fromError(status, error);
    }

    @SuppressWarnings("unchecked")
    private DashaMailBinaryResponse parseBinaryResponse(int status, byte[] rawBody, String contentType) {
        if (status >= 200 && status < 300) {
            return new DashaMailBinaryResponse(rawBody, contentType != null ? contentType : "application/octet-stream", status);
        }

        Map<String, Object> decodedMap = null;
        try {
            Object decoded = Json.parse(new String(rawBody, StandardCharsets.UTF_8));
            if (decoded instanceof Map) {
                decodedMap = (Map<String, Object>) decoded;
            }
        } catch (Json.JsonException e) {
            // fall through to the default error below
        }

        Map<String, Object> error = decodedMap != null && decodedMap.get("error") instanceof Map
            ? (Map<String, Object>) decodedMap.get("error")
            : defaultError(status);
        throw DashaMailApiException.fromError(status, error);
    }

    private static Map<String, Object> defaultError(int status) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", status);
        error.put("message", "Unknown DashaMail API error");
        return error;
    }

    /** A raw HTTP result: status code, body bytes, and (for binary responses) the response Content-Type. */
    protected static final class RawResponse {
        final int status;
        final byte[] body;
        final String contentType;

        public RawResponse(int status, byte[] body, String contentType) {
            this.status = status;
            this.body = body;
            this.contentType = contentType;
        }
    }

    /**
     * Sends the request over the network. Kept as its own method, taking
     * and returning plain types (not {@link HttpRequest}/{@link HttpResponse}),
     * so tests can stub it out without a real connection — subclass
     * HttpTransport and override {@link #execute}.
     */
    protected RawResponse execute(String method, String url, Map<String, String> headers, byte[] body) {
        HttpRequest.BodyPublisher publisher = body != null
            ? HttpRequest.BodyPublishers.ofByteArray(body)
            : HttpRequest.BodyPublishers.noBody();

        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
            .timeout(Duration.ofSeconds(timeoutSeconds))
            .method(method, publisher);
        for (Map.Entry<String, String> header : headers.entrySet()) {
            builder.header(header.getKey(), header.getValue());
        }

        HttpResponse<byte[]> response;
        try {
            response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException e) {
            throw new DashaMailNetworkException("Network error: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DashaMailNetworkException("Network error: request interrupted", e);
        }

        String contentType = response.headers().firstValue("content-type").orElse(null);
        return new RawResponse(response.statusCode(), response.body(), contentType);
    }
}
