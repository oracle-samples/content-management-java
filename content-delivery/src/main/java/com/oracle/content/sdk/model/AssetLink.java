/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Link structure used in many sdk models
 */
@SuppressWarnings({"unused"})
public class AssetLink extends AssetObject {

    @SerializedName("href")
    @Expose
    private String href;
    @SerializedName("rel")
    @Expose
    private String rel;
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("mediaType")
    @Expose
    private String mediaType;

    public String getHref() {
        return href;
    }

    public String getRel() {
        return rel;
    }

    public String getMethod() {
        return method;
    }

    public String getMediaType() {
        return mediaType;
    }

}
