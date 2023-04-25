/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.item;

import com.oracle.content.sdk.model.Asset;
import com.oracle.content.sdk.model.field.FieldType;

/**
 * Represents a ContentItem type of Asset.  Contains methods specific
 * only to ContentItems.
 */
@SuppressWarnings("unused")
public class ContentItem extends Asset {

    // value used in mimeType, fileGroup and fileExtension for content items
    public static final String CONTENT_ITEM = "contentItem";

    /**
     * This method will go through and "parse" all of the field data
     * in a ContentField map, guessing at the field types based on the field values.
     * This method is not a reliable way to determine the exact field types, but
     * could be useful as a way to inspect the full set of content item fields available.
     *
     * In most cases, since the field type is known methods such as {@link #getFieldFromType(String, FieldType)}
     * and related convenience methods such as {@link #getTextField(String)} should be used.
     *
     * @return {@link AssetFields} class of all fields
     */
    public AssetFields parseContentItemFields() {
        return AssetFields.parseItemFields(fields);
    }
}
