/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.digital;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.oracle.content.sdk.model.AssetObject;

/**
 * Part of DigitalAssetFields
 */
public class AdvancedVideoInfo extends AssetObject {

    @SerializedName("provider")
    @Expose
    private String provider;
    @SerializedName("properties")
    @Expose
    private AdvancedVideoInfoProperties properties;

    public String getProvider() {
        return provider;
    }

    public AdvancedVideoInfoProperties getProperties() {
        return properties;
    }

}
