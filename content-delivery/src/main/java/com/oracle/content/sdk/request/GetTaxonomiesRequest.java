/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.request;

import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.model.taxonomy.TaxonomyList;
import com.oracle.content.sdk.request.core.PaginatedListRequest;
import com.google.gson.JsonElement;

import retrofit2.Call;


/**
 * Request class used to get a list of published taxonomies
 */
@SuppressWarnings({"unused"})
public class GetTaxonomiesRequest extends PaginatedListRequest<GetTaxonomiesRequest, TaxonomyList> {

    /**
     * Construct request to get list of publish channels
     *
     * @param client Valid delivery client
     */
    public GetTaxonomiesRequest(ContentDeliveryClient client) {
        super(client, TaxonomyList.class);
    }

    /**
     * Get retrofit call to use for publish channel request
     */
    @Override
    public Call<JsonElement> getCall() {

        return client.getApi().getTaxonomies(
                expand,
                links,
                limit,
                offset,
                includeTotalCount);

    }
}

