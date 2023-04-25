/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.request.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.model.Asset;
import com.oracle.content.sdk.model.AssetType;
import com.oracle.content.sdk.model.item.ContentItem;
import com.oracle.content.sdk.model.digital.DigitalAsset;
import com.oracle.content.sdk.request.GetContentItemRequest;
import com.oracle.content.sdk.request.GetDigitalAssetRequest;
import retrofit2.Call;

/**
 * Base request class for {@link GetContentItemRequest} and {@link GetDigitalAssetRequest}
 */
@SuppressWarnings({"WeakerAccess","unused"})
public abstract class ContentAssetRequest<T extends ContentAssetRequest, C extends Asset> extends ContentRequestById<T, C> {

    // Value for "All" when using expand
    final public static String EXPAND_ALL="all";

    // expand field (default to null)
    protected String expand = null;


    public ContentAssetRequest(ContentDeliveryClient client, Class<C> objectClass, String id, IdType idType) {
        super(client, objectClass, id, idType);
    }

    /**
     * Specify whether to expand fields in the content item.  Can be a value
     * such as "fields.field_name" to expand a specific item reference.
     * Note that by default this is not set.  See also {@link #expandAll()}
     *
     * @param field expand field value (e.g. "all") or null to not expand references
     * @return Builder object
     */
    public T expand(String field) {
        this.expand = SearchQueryBuilder.getFieldName(field);
        return getThis();
    }

    /**
     * Specifies a list of fields to expand when requesting the item.
     *
     * @param expandFields list of string fields to expand
     * @return Builder object
     */
    public T expand(List<String> expandFields) {
        this.expand = SearchQueryBuilder.getFieldList(expandFields);
        return getThis();
    }

    /**
     * Just like calling {@link #expand} with "all" as the parameter.  Will expand all item reference fields.
     * @return Builder object.
     */
    public T expandAll() {
        this.expand = EXPAND_ALL;
        return getThis();
    }

    /**
     * Utility method to deserialize response to a {@link Asset}.  If this is a digital asset, it will
     * deserialize into a {@link DigitalAsset}, otherwise a {@link ContentItem}
     * @param jsonElement Root json element for content item
     * @return Either a ContentItem or DigitalAsset depending on the type
     */
    public static Asset deserializeContentBaseItem(JsonElement jsonElement) {

        // get the type to determine whether the item is actually a digital asset
        JsonObject obj = jsonElement.getAsJsonObject();


        Asset item;

        // is this a digital asset?
        if (new AssetType(jsonElement.getAsJsonObject()).isDigitalAsset()) {
            // deserialize as a DigitalAsset
            item = gson().fromJson(jsonElement, DigitalAsset.class);
            // manually set the "built" download url for the class
        } else {
            // deserialize the whole thing to a ContentItem.
            item = gson().fromJson(jsonElement, ContentItem.class);
        }


        return item;
    }

    public String getExpand() {
        return expand;
    }

    @Override
    public Call<JsonElement> getCall() {

        if (idType == IdType.SLUG) {
            return client.getApi().getContentItemBySlug(
                    id,
                    links,
                    expand,
                    getCacheControl());
        } else {
            return client.getApi().getContentItem(
                    id,
                    links,
                    expand,
                    getCacheControl());
        }

    }

}

