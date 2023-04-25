/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model;

import com.oracle.content.sdk.model.digital.DigitalAsset;
import com.oracle.content.sdk.model.item.ContentItem;
import com.oracle.content.sdk.request.core.ContentAssetRequest;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import com.oracle.content.sdk.request.SearchAssetsRequest;


/**
 *  Content search response from delivery SDK for {@link SearchAssetsRequest }
 */
public class AssetSearchResult extends PaginatedListResult<Asset> {

    @Override
    protected Asset deserializeObject(JsonElement jsonElement) {
        return ContentAssetRequest.deserializeContentBaseItem(jsonElement);
    }

    /**
     * Get the list of deserialized list of items from the response as a list
     * of {@link ContentItem} objects, so this will filter out any digital assets
     *
     * @return List of content item objects
     */
    public List<ContentItem> getContentItems() {
        List<ContentItem> contentItems = new ArrayList<>();
        for(Asset item : getItems()) {
            if (item instanceof ContentItem) {
                contentItems.add((ContentItem)item);
            }
        }
        return contentItems;
    }


    /**
     * Get the list of deserialized list of items from the response as a list
     * of {@link DigitalAsset} objects, so this will filter out any content items
     *
     * @return List of digital assets objects
     */
    public List<DigitalAsset> getDigitalAssets() {
        List<DigitalAsset> digitalAssets = new ArrayList<>();
        for(Asset item : getItems()) {
            if (item instanceof DigitalAsset) {
                digitalAssets.add((DigitalAsset)item);
            }
        }
        return digitalAssets;
    }





}
