/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.util;

import com.oracle.content.sdk.request.core.PaginatedListRequest;
import com.google.gson.JsonElement;

import com.oracle.content.sdk.ContentDeliveryClient;

import retrofit2.Call;


/**
 * Request class used to get a list of publish channels.
 * <b>This uses the management SDK and is for internal testing.  It may change in a future release</b>
 */
@SuppressWarnings({"unused"})
public class GetPublishChannelsRequest extends PaginatedListRequest<GetPublishChannelsRequest, PublishChannelList> {

    /**
     * Construct request to get list of publish channels
     *
     * @param client Valid delivery client
     */
    public GetPublishChannelsRequest(ContentDeliveryClient client) {
        super(client, PublishChannelList.class);
    }

    /**
     * Get retrofit call to use for publish channel request
     */
    @Override
    public Call<JsonElement> getCall() {

        return client.getApi().getPublishChannels(
                fields,
                links,
                limit,
                offset,
                includeTotalCount);

    }
}

