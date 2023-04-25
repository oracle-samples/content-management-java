/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.request;

import com.google.gson.JsonElement;
import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.model.taxonomy.TaxonomyCategoryList;
import com.oracle.content.sdk.request.core.PaginatedListRequest;
import retrofit2.Call;

/**
 * Get a list of publish channels in the system
 */
public class GetTaxonomyCategoriesRequest extends PaginatedListRequest<GetTaxonomyCategoriesRequest, TaxonomyCategoryList> {

    final String taxonomyId;

    /**
     * Construct request to get list of publish channels
     *
     * @param client Valid delivery client
     */
    public GetTaxonomyCategoriesRequest(ContentDeliveryClient client, String taxonomyId) {
        super(client, TaxonomyCategoryList.class);
        this.taxonomyId = taxonomyId;
    }

    /**
     * Get retrofit call to use for publish channel request
     */
    @Override
    public Call<JsonElement> getCall() {

        return client.getApi().getTaxonomyCategories(
                taxonomyId,
                expand,
                links,
                limit,
                offset,
                includeTotalCount);

    }


}
