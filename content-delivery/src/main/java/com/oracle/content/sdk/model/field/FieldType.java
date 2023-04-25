/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.sdk.model.field;

/**
 * Enumeration of the field type for content item fields, including the
 * associated ContentField class.
 */
@SuppressWarnings({"unused"})
public enum FieldType {
    // single value fields
    TEXT("text", ContentFieldText.class),
    LARGE_TEXT("largetext", ContentFieldLargeText.class),
    DATE("datetime", ContentFieldDate.class),
    INTEGER("number", ContentFieldInteger.class),
    DECIMAL("decimal", ContentFieldDecimal.class),
    BOOLEAN("boolean", ContentFieldBoolean.class),
    CONTENT_ITEM("reference", ContentFieldItemReference.class),
    DIGITAL_ASSET("reference", ContentFieldAssetReference.class),
    // list fields
    TEXT_LIST("text", ContentFieldTextList.class, true),
    LARGE_TEXT_LIST("largetext", ContentFieldTextList.class, true),
    REFERENCE_LIST("reference", ContentFieldReferenceList.class, true),
    JSON("json", ContentFieldJson.class, false),
    // unknown type
    UNKNOWN("?", ContentFieldUnknown.class);

    // the name as it appears in the json data
    final public String jsonName;

    // the class that models the field type
    final public Class<? extends ContentField> fieldClass;

    // is this a list type?
    final public boolean isList;

    FieldType(String jsonName, Class<? extends ContentField> fieldClass, boolean isList) {
        this.fieldClass = fieldClass;
        this.jsonName = jsonName;
        this.isList = isList;
    }

    FieldType(String name, Class<? extends ContentField> fieldClass) {
        this(name, fieldClass, false);
    }

    public static FieldType getFieldType(Class fieldClass) {

        for(FieldType type : FieldType.values()) {
            if (fieldClass == type.fieldClass)
                return type;
        }
        return UNKNOWN;
    }



    /**
     * Given a field name type and other info such as list, asset, find a matching type.
     *
     * @param typename type as it appears in json
     * @param isList whether the type is a list
     * @param isAsset whether the reference type is a digital asset
     * @return matching FieldType or null
     */
    public static FieldType getFieldType(String typename, boolean isList, boolean isAsset) {

        for(FieldType type : FieldType.values()) {
            if (type.jsonName.equals(typename) && (type.isList == isList)) {
                // special check for digital asset type
                if (type == FieldType.CONTENT_ITEM && isAsset) {
                    return FieldType.DIGITAL_ASSET;
                } else{
                    return type;
                }
            }
        }
        return null;
    }

    /**
     * Return true if this type is a reference type
     *
     * @return true if reference
     */
    public boolean isReference() {
        return (this == REFERENCE_LIST) || (this == CONTENT_ITEM) || (this == DIGITAL_ASSET);
    }

}
