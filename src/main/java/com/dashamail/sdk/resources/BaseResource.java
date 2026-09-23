package com.dashamail.sdk.resources;

import com.dashamail.sdk.HttpTransport;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseResource {

    protected final HttpTransport client;

    protected BaseResource(HttpTransport client) {
        this.client = client;
    }

    /** Copies {@code params} (or starts empty) and sets/overrides one field — merges a required field into the caller's optional-params map. */
    protected static Map<String, Object> with(Map<String, Object> params, String key, Object value) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (params != null) {
            result.putAll(params);
        }
        result.put(key, value);
        return result;
    }
}
