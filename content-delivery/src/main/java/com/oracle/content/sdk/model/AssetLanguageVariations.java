/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model;

import com.oracle.content.sdk.request.GetItemLanguageVariationsRequest;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Item language variations response for {@link GetItemLanguageVariationsRequest}
 */
@SuppressWarnings("unused")
public class AssetLanguageVariations extends AssetLinksObject {

    /**
     * Id and value (i.e. language) for an individual item variation.
     */
    public static class Item extends AssetLinksObject {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("value")
        @Expose
        private String value;

        /**
         * Returns the item Id for this language variation
         * @return Item id for the language variation
         */
        public String getId() {
            return id;
        }

        /**
         * Returns the value (i.e. language) for this variation.
         * @return language value such as "en-US", "fr", "ja", etc.
         */
        public String getValue() {
            return value;
        }

    }

    @SerializedName("setId")
    @Expose
    private String setId;
    @SerializedName("masterItem")
    @Expose
    private String masterItem;
    @SerializedName("items")
    @Expose
    private List<AssetLanguageVariations.Item> items = null;

    // In this context, varType will always be "language"
    @SerializedName("varType")
    @Expose
    private String varType;

    /**
     * Returns the "set id" for the content item
     * @return set id
     */
    public String getSetId() {
        return setId;
    }

    /**
     * Returns the content item id for the master item
     * @return master item content id
     */
    public String getMasterItem() {
        return masterItem;
    }


    /**
     * Gets a list of all the item language variations for this item.
     * @return list of item language variations
     */
    public List<AssetLanguageVariations.Item > getItems() {
        return items;
    }


}

