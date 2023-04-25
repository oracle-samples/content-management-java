/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Any model object which has a "links" field (which are most objects) extend from this class.
 */
public abstract class AssetLinksObject extends AssetObject {

    @SerializedName("links")
    @Expose
    protected List<AssetLink> links = null;

    /**
     * Get the list of {@link AssetLink}s for this object.  May be null.
     * @return list of content links
     */
    public List<AssetLink> getLinks() {
        return links;
    }

}
