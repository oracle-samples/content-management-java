/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.request;

import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.model.ApiInfo;
import com.oracle.content.sdk.request.core.ContentRequest;
import com.google.gson.JsonElement;

import retrofit2.Call;

/**
 * Request to get API info.  This request could be useful as a quick
 * "test" to determine if the server is valid and available as it doesn't
 * require a valid channel token or any data on the server.
 */
@SuppressWarnings("unused")
public class GetApiInfoRequest extends ContentRequest<GetApiInfoRequest, ApiInfo> {

    /**
     * Create request to get api information.
     *
     * @param client A valid delivery client
     */
    public GetApiInfoRequest(ContentDeliveryClient client) {
        super(client, ApiInfo.class);
    }

    /**
     * Get retrofit call to use for search request
     */
    @Override
    public Call<JsonElement> getCall() {

        return client.getApi().getApiInfo();

    }
}

