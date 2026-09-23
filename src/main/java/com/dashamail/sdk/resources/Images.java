package com.dashamail.sdk.resources;

import com.dashamail.sdk.DashaMailBinaryResponse;
import com.dashamail.sdk.DashaMailResponse;
import com.dashamail.sdk.HttpTransport;

import java.util.Base64;
import java.util.Map;

/**
 * Resize (&gt;1600px wide) and recompress an image the same way DashaMail's own
 * file manager does, without changing its format or storing anything.
 * https://dashamail.ru/api/images/
 */
public class Images extends BaseResource {

    public Images(HttpTransport client) {
        super(client);
    }

    /**
     * Optimize an image already loaded in memory (JSON body, base64-encoded).
     *
     * @param binaryData Raw image bytes (JPEG/PNG/GIF).
     * @param params     resize (bool, default true), max_width (int), lossy (bool).
     * @return a DashaMailResponse whose data map's "image" entry is the base64-encoded result.
     */
    public DashaMailResponse optimize(byte[] binaryData) {
        return optimize(binaryData, null);
    }

    public DashaMailResponse optimize(byte[] binaryData, Map<String, Object> params) {
        String base64 = Base64.getEncoder().encodeToString(binaryData);
        return client.request("POST", "/images/optimize", null, with(params, "image", base64));
    }

    /** Optimize an image already sitting on disk, uploaded as multipart/form-data. */
    public DashaMailResponse optimizeFile(String filePath) {
        return optimizeFile(filePath, null);
    }

    public DashaMailResponse optimizeFile(String filePath, Map<String, Object> params) {
        return client.requestMultipart("POST", "/images/optimize", null, params, "file", filePath, null, null);
    }

    /** Same as {@link #optimizeFile}, but returns raw optimized bytes (?response=binary). */
    public DashaMailBinaryResponse optimizeFileBinary(String filePath) {
        return optimizeFileBinary(filePath, null);
    }

    public DashaMailBinaryResponse optimizeFileBinary(String filePath, Map<String, Object> params) {
        Map<String, Object> query = with(null, "response", "binary");
        return client.requestMultipartBinary("POST", "/images/optimize", query, params, "file", filePath, null, null);
    }

    /** Same as {@link #optimize(byte[])}, but returns raw optimized bytes (?response=binary). */
    public DashaMailBinaryResponse optimizeBinary(byte[] binaryData) {
        return optimizeBinary(binaryData, null);
    }

    public DashaMailBinaryResponse optimizeBinary(byte[] binaryData, Map<String, Object> params) {
        String base64 = Base64.getEncoder().encodeToString(binaryData);
        Map<String, Object> query = with(null, "response", "binary");
        return client.requestBinary("POST", "/images/optimize", query, with(params, "image", base64));
    }
}
