/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A ContentType can be represented in different ways, and this will centralize the handling
 * of the possible values for a content type and how it can be used to determine whether an asset
 * is an digital asset, content item, or custom digital asset.
 */
public class AssetType extends AssetObject {

    // *** known string values for type **

    // the value of the type field for digital assets - used for "legacy" assets
    public static final String TYPE_DIGITAL_ASSET = "DigitalAsset";

    // different digital asset types (for newer assets)
    public static final String TYPE_ASSET_FILE = "File";
    public static final String TYPE_ASSET_IMAGE = "Image";
    public static final String TYPE_ASSET_VIDEO = "Video";
    public static final String TYPE_ASSET_VIDEO_PLUS = "Video-Plus";

    // ** known strings values for type category **

    public static final String TYPE_CATEGORY_DIGITAL_ASSET = "DigitalAssetType";
    public static final String TYPE_CATEGORY_CONTENT_ITEM = "ContentItemType";


    // should always be set to the type name
    private final String type;

    // may be empty, may be set to values such as DigitalAssetType or ContentItem
    private final String typeCategory;

    public AssetType(String type, String typeCategory) {
        this.type = type;
        this.typeCategory = typeCategory;
    }

    // extract "type" and "typeCategory" fields directly from json
    public AssetType(JsonObject jsonObject) {
        JsonElement typeElem = jsonObject.get("type");
        JsonElement typeCategoryElem = jsonObject.get("typeCategory");
        type = typeElem != null?typeElem.getAsString():null;
        typeCategory = typeCategoryElem != null?typeCategoryElem.getAsString():null;
    }


    /**
     * Is this type a Digital Asset.
     *
     * @return true if Digital asset or false if Content item
     */
    public boolean isDigitalAsset() {
        // if type category is set, use that to determine whether this is a digital asset
        if (typeCategory != null) {
            return TYPE_CATEGORY_DIGITAL_ASSET.equals(typeCategory);
        } else {
            // otherwise use type
            return TYPE_DIGITAL_ASSET.equals(type);
        }
    }
}
