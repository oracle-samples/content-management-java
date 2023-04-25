/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.request;

import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.model.AssetLanguageVariations;
import com.oracle.content.sdk.request.core.ContentRequestById;
import com.google.gson.JsonElement;

import retrofit2.Call;

/**
 * Request class used to get the item language variations for a published
 * content item that has language variations.
 *
 * <pre>   {@code
 *
 * // create request to get item language variations for a content item id
 * GetItemLanguageVariationsRequest request =
 *      new GetItemLanguageVariationsRequest(deliveryClient, contentItemId);
 *
 * } </pre>
 */
@SuppressWarnings({"WeakerAccess","unused"})
public class GetItemLanguageVariationsRequest extends ContentRequestById<GetItemLanguageVariationsRequest, AssetLanguageVariations> {

    /**
     * Construct request to get item language variations
     *
     * @param client delivery client to use
     * @param id content item id
     * @param idType id type either ID or slug
     */
    public GetItemLanguageVariationsRequest(ContentDeliveryClient client, String id, IdType idType) {
        super(client, AssetLanguageVariations.class, id, idType);
    }

    public GetItemLanguageVariationsRequest(ContentDeliveryClient client, String id) {
        this(client, id, IdType.ID);
    }

    @Override
    public Call<JsonElement> getCall() {
        return (idType==IdType.SLUG)?
                client.getApi().getItemLanguageVariationsBySlug(id, links) :
                client.getApi().getItemLanguageVariations(id, links);
    }

}

