/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.taxonomy;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.oracle.content.sdk.model.AssetLinksObject;
import com.oracle.content.sdk.model.ItemList;

/**
 * Taxonomy model for a content item.
 */
@SuppressWarnings("unused")
public class Taxonomy extends AssetLinksObject {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("shortName")
    @Expose
    private String shortName;
    @SerializedName("categories")
    @Expose
    private ItemList<TaxonomyCategory> categories;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public ItemList<TaxonomyCategory> getCategories() {
        return categories;
    }

}