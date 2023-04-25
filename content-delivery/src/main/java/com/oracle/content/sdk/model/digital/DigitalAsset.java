/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.sdk.model.digital;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.oracle.content.sdk.ContentClient;
import com.oracle.content.sdk.model.Asset;
import com.oracle.content.sdk.model.AssetType;
import com.oracle.content.sdk.model.item.AssetFields;
import com.oracle.content.sdk.model.field.ContentField;
import com.oracle.content.sdk.model.field.FieldType;

/**
 * Representation of a digital asset.  This extends {@link Asset} but contains
 * some methods and models specific to digital asset.
 * Most methods in this call will only work on full digital assets and not references,
 * so if {@link Asset#isReferenceOnly()} returns true will need to get
 * the full Digital asset item to get results from some methods.
 * Even for references, the {@link Asset#getId()} field will always be set.
 */
@SuppressWarnings({"unused"})
public class DigitalAsset extends Asset {

    // "native" rendition term for digital asset pathName
    public static final String NATIVE_RENDITION = "native";

    // list of non-custom asset types
    private static List<String> STANDARD_ASSET_TYPES = new ArrayList<>(
            Arrays.asList(AssetType.TYPE_ASSET_FILE,
                    AssetType.TYPE_ASSET_IMAGE,
                    AssetType.TYPE_ASSET_VIDEO,
                    AssetType.TYPE_ASSET_VIDEO_PLUS));

    // "fields" will be deserialized to this
    transient private DigitalAssetFields digitalAssetFields = null;

    /**
     * Get the digital asset fields structure, which contains all of metadata/rendition information for the digital asset.
     *
     * @return Digital asset fields
     */
    public DigitalAssetFields getAssetFields() {

        if (digitalAssetFields == null && fields != null) {
            String jsonString = ContentClient.gson().toJson(fields);
            digitalAssetFields = ContentClient.gson().fromJson(jsonString, DigitalAssetFields.class);
        }
        // don't allow "null" value for digital asset fields, just return empty class
        if (digitalAssetFields == null) {
            digitalAssetFields = new DigitalAssetFields();
        }
        return digitalAssetFields;
    }

    // is this a custom asset type with attributes?
    public boolean isCustomAssetType() {
        return !STANDARD_ASSET_TYPES.contains(getType());
    }

    /**
     * A generic method that will return the custom digital asset attribute field given the specified type.
     * For example, you could call it like this to get a the field for a content date:
     * {@code
     * ContentFieldDate dateField = item.getFieldFromType("date_field", FieldType.DATE);
     * }
     *
     * @param fieldName field name to match
     * @param type The type of field expected
     * @param <T> the specific ContentField class
     * @return Specific ContentField derived class or null if it doesn't exist
     */
    @SuppressWarnings("unchecked")
    public <T extends ContentField> T getCustomFieldFromType(String fieldName, FieldType type) {
        if (fields == null)
            return null;

        Object field = fields.get(fieldName);
        if (field == null)
            return null;

        ContentField contentField = AssetFields.getFieldFromValue(field, type);
        return (T)contentField;
    }

    /**
     * Get size in bytes for this digital asset or null if size not available
     *
     * @return size in bytes or null
     */
    public Integer getSize() {
        return getAssetFields().getSize();
    }

    /**
     * Get version (if applicable) for the item/asset as a string.
     *
     * @return Version for the item (may be null)
     */
    public String getVersion() {
        return getAssetFields().getVersion();
    }

    /**
     * Does this refer to an image?  If it's not known whether this is an image
     * because this is only an item reference, it will return null.
     *
     * @return true if image reference or null if not known
     */
    public Boolean isImage() {
        return getAssetFields().isImage();
    }


    /**
     * Get the fully qualified url to download the 'native' digital asset.  This will return
     * null if this is a reference and not a full digital asset.
     *
     * @return Fully qualified url which can be used to download the asset.
     */
    public String getNativeDownloadUrl() {
        return getAssetFields().getNativeDownloadUrl();
    }

    /**
     * In general use {@link #getRenditionUrl(RenditionType)} unless you are trying to get the name of a custom rendition
     * in which case you can use this method to pass in the raw rendition name.
     *
     * @param rendition the rendition name
     * @return The matching rendition url or null if no match
     */
    public String getRenditionUrl(String rendition) {
        return getAssetFields().getRenditionUrl(rendition);
    }

    /**
     * Get the fully qualified url for the given rendition type, which could be native.  If this returns null,
     * the full digital asset properties have not been retrieved or there is no matching rendition.
     *
     * @param rendition The desired {@link RenditionType}
     * @return The matching rendition url or null if no match
     */
    public DigitalAssetRendition getRendition(String rendition) {
        return getAssetFields().getRendition(rendition);
    }

    /**
     * Get the fully qualified url for the given rendition type, which could be native.  If this returns null,
     * the full digital asset properties have not been retrieved or there is no matching rendition.
     *
     * @param rendition The desired {@link RenditionType}
     * @return The matching rendition url or null if no match
     */
    public String getRenditionUrl(RenditionType rendition) {
        return getAssetFields().getRenditionUrl(rendition);
    }

    /**
     * Get full list of renditions available in the digital asset or null if not available.
     *
     * @return list of renditions
     */
    public List<DigitalAssetRendition> getRenditionsList() {
        return getAssetFields().getRenditions();
    }

    /**
     * Will search through the renditions to find a matching rendition based on {@link DigitalAssetPreferredRenditionCriteria}.
     * This is a convenience method to assist in finding a rendition based on specific criteria.  If a RenditionFormat is returned,
     * call {@link DigitalAssetRendition.RenditionFormat#getDownloadUrl()} to get the url to use for download.
     *
     * @param renditionCriteria criteria for finding a rendition
     * @return the best matching {@link DigitalAssetRendition.RenditionFormat} or null if no renditions
     */
    public DigitalAssetRendition.RenditionFormat getPreferredRendition(DigitalAssetPreferredRenditionCriteria renditionCriteria) {
        return getAssetFields().getPreferredRendition(renditionCriteria);
    }

    public AdvancedVideoInfo getAdvancedVideoInfo() {
        return getAssetFields().getAdvancedVideoInfo();
    }

    public AdvancedVideoInfoProperties getAdvancedVideoInfoProperties() {
        AdvancedVideoInfo info = getAdvancedVideoInfo();
        return info!=null?info.getProperties():null;
    }

    public String getVideoToken() {
        AdvancedVideoInfoProperties properties = getAdvancedVideoInfoProperties();
        return properties!=null?properties.getVideoToken():null;
    }

    public boolean isAdvancedVideo() {
        return getAdvancedVideoInfo() != null;
    }

}
