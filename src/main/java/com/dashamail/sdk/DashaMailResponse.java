package com.dashamail.sdk;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Wraps the {@code data} payload of a successful API call together with its
 * {@code meta} (pagination info) and the human-readable {@code msg.text}
 * DashaMail sent.
 *
 * <p>{@link #getData()} holds the parsed payload as plain JDK types — a
 * {@code Map<String, Object>} for an object, a {@code List<Object>} for a
 * list, or a scalar. The response is iterable when the payload is a list:</p>
 *
 * <pre>{@code
 * DashaMailResponse members = dashamail.lists().members(listId);
 * for (Object member : members) {
 *     Map<?, ?> dict = (Map<?, ?>) member;
 *     System.out.println(dict.get("email"));
 * }
 * }</pre>
 *
 * <p>Paginated endpoints ({@code members()} and friends) additionally expose
 * {@link #hasMore()}/{@link #getLimit()} taken from the {@code meta} object
 * DashaMail returns instead of a total count.</p>
 */
public class DashaMailResponse implements Iterable<Object> {

    private final Object data;
    private final Map<String, Object> meta;
    private final String message;

    public DashaMailResponse(Object data, Map<String, Object> meta, String message) {
        this.data = data;
        this.meta = meta != null ? meta : Collections.emptyMap();
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public String getMessage() {
        return message;
    }

    /** True when a paginated listing has more rows beyond the returned page. */
    public boolean hasMore() {
        Object value = meta.get("has_more");
        return value instanceof Boolean && (Boolean) value;
    }

    /** The effective page size DashaMail used to answer a paginated listing. */
    public Integer getLimit() {
        Object value = meta.get("limit");
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }

    /** Indexes into {@link #getData()} when it is an object (a Map). */
    @SuppressWarnings("unchecked")
    public Object get(String key) {
        if (data instanceof Map) {
            return ((Map<String, Object>) data).get(key);
        }
        return null;
    }

    /** Indexes into {@link #getData()} when it is a list. */
    public Object get(int index) {
        if (data instanceof List) {
            return ((List<?>) data).get(index);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Object> iterator() {
        if (data instanceof List) {
            return ((List<Object>) data).iterator();
        }
        return Collections.emptyIterator();
    }

    @Override
    public String toString() {
        return "DashaMailResponse{data=" + data + "}";
    }
}
