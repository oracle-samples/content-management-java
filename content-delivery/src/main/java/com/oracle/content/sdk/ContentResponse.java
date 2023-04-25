/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk;

import com.oracle.content.sdk.model.AssetObject;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.logging.Level;

import okhttp3.Headers;
import retrofit2.Response;

/**
 * Encapsulates an SDK response, including the deserialized result object and raw json.
 * <p>
 * Although the SDK provides a de-serialized java object ({@link #getResult()}, you can deserialize
 * the result yourself by using the raw json from {@link #getAsJson()}.
 * </p>You can also get the {@link CacheState} for the call to determine
 * if the result came from the cache or network.
 */
@SuppressWarnings({"WeakerAccess","unused"})
public class ContentResponse<C extends AssetObject> implements Serializable {

    static final long serialVersionUID = 3454990396571L;

    final private static String TAG = "ContentResponse";

    /**
     * The state of the response, such as whether it came from network or cached.
     */
    public enum CacheState {
        NETWORK, // resultObject is from network (non-cached)
        CACHED, // resultObject is from cache
        ERROR // there is no resultObject due to error
    }

    // response body in json form (or null if there was an error)
    @Expose(serialize = false)
    transient private JsonElement body;

    // deserialized resultObject
    @Expose(serialize = false)
    transient private C resultObject = null;

    // response headers (used when there is no body)
    final transient private Headers headers;

    // content exception (or null if no exception)
    private  ContentException exception;

    // stored cached state
    private CacheState cacheState = CacheState.NETWORK;

    // http code from response
    private int code = HttpURLConnection.HTTP_OK;

    /**
     * Construct the response resultObject
     *
     * @param response Retrofit response
     */
    public ContentResponse(Response<JsonElement> response) {
        this.body = response.body();
        this.headers = response.headers();
        this.code = response.code();
        if (!response.isSuccessful()) {
            this.cacheState = CacheState.ERROR;
            ContentException.REASON reason = ContentException.REASON.responseError;
            String message = ContentErrorString.SDK_RESPONSE_ERROR;
            if (400 == code) {
                reason = ContentException.REASON.invalidRequest;
                message = ContentErrorString.SDK_INVALID_REQUEST;
            }
            this.exception = ContentClient.getContentException(
                    reason, null, message, response);
            // set "body" of response to error
            if (exception.getContentError() != null) {
                this.body = ContentClient.gson().toJsonTree(exception.getContentError());
            }
        }

        updateCacheInfo(response.raw());
    }

    /**
     * Construct a response with a custom set of headers
     * @param headers to use
     */
    public ContentResponse(Headers headers) {
        this.body = null;
        this.exception = null;
        this.headers = headers;
    }

    // set the deseralized resultObject
    public void setResult(C resultObject) {
        this.resultObject = resultObject;
    }

    // get the resultObject
    public C getResult() {
        return this.resultObject;
    }

    public void setException(ContentException exception) {
        this.exception = exception;
    }

    /**
     * Construct the REST response resultObject (error case)
     *
     * @param exception {@link ContentException} because the call failed
     */
    public ContentResponse(ContentException exception) {
        this.body = null;
        this.exception = exception;
        this.cacheState = CacheState.ERROR;
        this.headers = null;
    }


    /**
     * Get the current cache state of this resultObject, for example whether
     * is from the network or cache.
     *
     * @return cache state of the returned resultObject
     */
    public CacheState getCacheState() {
        return cacheState;
    }
    /**
     * Get HTTP code for the response
     *
     * @return httpCode such as 404
     */
    public int getHttpCode() {
        return code;
    }

    /**
     * Get response headers
     *
     * @return return headers from the response
     */
    public Headers getHeaders() {
        return this.headers;
    }

    /**
     * Get response body as a JsonElement, or null in the case of an error
     *
     * @return return body of response as json element
     */
    public JsonElement getAsJson() {
        return this.body;
    }

    /**
     * Is the response the result of a successful request?
     * If not, then get more detailed errors from {@link #getException()}
     *
     * @return true if call was successful
     */
    public boolean isSuccess() {
        return this.exception == null;
    }

    /**
     * Get the ContentException condition in the case there was an error
     *
     * @return {@link ContentException}
     */
    public ContentException getException() {
        return this.exception;
    }

    /**
     * Set cache and response values for this resultObject
     *
     * @param response response from the rest request
     */
    void updateCacheInfo(okhttp3.Response response) {

        // is the response cached?
        if (response != null && response.cacheResponse() != null) {
            ContentClient.log(Level.INFO, TAG , "cached response");
            this.cacheState = CacheState.CACHED;
        }


    }
}
