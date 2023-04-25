/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.sdk.model.digital;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.oracle.content.sdk.model.AssetObject;


/**
 * The metadata for an asset (includes width and height).
 */
@SuppressWarnings({"WeakerAccess","unused"})
public class DigitalAssetMetadata extends AssetObject {

    @SerializedName("width")
    @Expose
    private String width;
    @SerializedName("height")
    @Expose
    private String height;

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    /**
     * Parse the string value of width into an integer.
     *
     * @return Integer value of width, or 0 if not a number or empty.
     */
    public Integer getWidthAsInteger() {
        return width!=null?Integer.parseInt(width):0;
    }

    /**
     * Parse the string value of height into an integer.
     *
     * @return Integer value of height, or 0 if not a number or empty.
     */
    public Integer getHeightAsIntger() {
        return height!=null?Integer.parseInt(height):0;
    }

}
