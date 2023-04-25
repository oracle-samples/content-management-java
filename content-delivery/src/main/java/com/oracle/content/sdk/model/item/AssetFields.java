/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.item;

import com.oracle.content.sdk.ContentClient;
import com.oracle.content.sdk.model.Asset;
import com.oracle.content.sdk.model.digital.DigitalAsset;
import com.oracle.content.sdk.model.field.ContentFieldAssetReference;
import com.oracle.content.sdk.model.field.ContentFieldBoolean;
import com.oracle.content.sdk.model.field.ContentFieldInteger;
import com.oracle.content.sdk.model.field.ContentFieldJson;
import com.oracle.content.sdk.model.field.ContentFieldText;
import com.oracle.content.sdk.model.field.ContentFieldTextList;
import com.oracle.content.sdk.model.field.FieldType;
import com.oracle.content.sdk.request.core.ContentAssetRequest;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.oracle.content.sdk.model.field.ContentFieldReference;
import com.oracle.content.sdk.model.field.ContentFieldUnknown;
import com.oracle.content.sdk.model.date.ContentDate;
import com.oracle.content.sdk.model.field.ContentField;
import com.oracle.content.sdk.model.field.ContentFieldDate;
import com.oracle.content.sdk.model.field.ContentFieldDecimal;
import com.oracle.content.sdk.model.field.ContentFieldItemReference;
import com.oracle.content.sdk.model.field.ContentFieldLargeText;
import com.oracle.content.sdk.model.field.ContentFieldReferenceList;

/**
 * Represents the custom fields of a {@link Asset}
 */
@SuppressWarnings({"unused"})
public class AssetFields {

    private static final String TAG = "AssetFields";

    // Map of name/value pairs
    private Map<String, ContentField> map = new LinkedHashMap<>();

    private AssetFields() {}

    /**
     * Return the map of ContentField files
     *
     * @return Map of string keys to ContentField objects
     */
    public Map<String, ContentField> getFieldsMap() {
        return map;
    }

    /**
     * Get the list of name/value pairs with the associated content type
     *
     * @return string representation of the data fields
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Map.Entry<String, ContentField> entry : map.entrySet()) {
            s.append("{").append(entry.getKey()).append(", ").append(entry.getValue().getClass().getName()).append("}\n");
        }
        return s.toString();
    }

    /**
     * Custom deserializer for field data.  This will parse the structure of the json coming
     * back and attempt to parse them into content ContentField fields for the map by "guessing"
     * the type of the field based on the value.  This is useful if you want to inspect all the fields
     * and don't know what the types are.
     *
     * @param fields map of fields to iterate through
     * @return ContentItemFields parsed from the fields map.
     */
    static AssetFields parseItemFields(Map<String, Object> fields){

        // go through the json manually to create the list of content types
        AssetFields itemFields = new AssetFields();

        JsonObject jsonObject = new JsonObject();

        // traverse all the fields, guessing at the type based on the data
        for (Map.Entry<String, Object> field: fields.entrySet()) {
            String fieldName = field.getKey();
            Object fieldValue = field.getValue();
            itemFields.map.put(fieldName, getFieldFromValue(fieldValue, null));
        }

        return itemFields;
    }


    /**
     * This method will transfer a field object value into a ContentField object based on the
     * {@link FieldType} based in as a parameter.  If the type passed in is null, the method will
     * "guess" at the field based on the object value.  This is less reliable and might return
     * the incorrect content field type in some cases.
     *
     * @param value Object value, such as ContentDate, Integer, etc.
     * @param type The type that is expected for this value, or null to "guess" the type
     * @return the ContentField for the value
     */
    public static ContentField getFieldFromValue(Object value, FieldType type) {
        ContentField field = null;
        if (value == null) {
            // reference could have null value
            if (type != null && type.isReference()) {
                try {
                    // create the desired reference type as null
                    Constructor<ContentField> constructor = (Constructor<ContentField>) type.fieldClass.getDeclaredConstructor();
                    field = constructor.newInstance();
                    // OLD CODE (deprecated in Java 9) field = type.fieldClass.newInstance();
                } catch (Exception e) {
                    ContentClient.log(TAG, "Error creating instance of " + type);
                    field = null;
                }
            } else {
                field = new ContentFieldUnknown("null");
            }
        } else if (value instanceof Boolean) {
            field = new ContentFieldBoolean((Boolean)value);
        } else if (value instanceof Double) {

            Double num = (Double)value;
            /// json will come back as Double even for an integer, so
            // if the type passed in is Integer, cast value to that
            if (type == FieldType.INTEGER) {
                field = new ContentFieldInteger(num.intValue());
            } else {
                field = new ContentFieldDecimal((Double) value);
            }
        } else if (value instanceof Integer) {
            // note: numbers never come back as integers (only doubles) in gson
            // so this code is unlikely to be triggered
            field = new ContentFieldInteger((Integer) value);
        } else if (value instanceof String) {
            // if desired type is Large text, use that
            if (type == FieldType.LARGE_TEXT) {
                field = new ContentFieldLargeText((String)value);
            } else {
                field = new ContentFieldText((String) value);
            }
        } else if (value instanceof ArrayList) {
            field = getArrayField((ArrayList)value);
        } else if (value instanceof LinkedTreeMap) {
            LinkedTreeMap objectMap = (LinkedTreeMap)value;
            // look for timezone field as clue this is a date
            if (objectMap.containsKey("timezone")) {
                field = getDateField(objectMap);
            } else if (objectMap.containsKey("id") && objectMap.containsKey("type")){
                // if both id and type are present, this should be a reference
                field = getItemReference(objectMap);
            } else {
                // otherwise assume this is a json type field
                field = getJsonField(objectMap);
            }
        }

        if (type != null && field != null) {
            // verify that the field type matches what was passed in
            if (type.fieldClass != field.getClass()) {
                ContentClient.log(TAG, "Expected fields did not match for " + type);
                return null;
            }
        }

        return field;

    }

    static private ContentFieldJson getJsonField(LinkedTreeMap objectMap) {
        JsonObject jsonObject = ContentClient.gson().toJsonTree(objectMap).getAsJsonObject();
        return new ContentFieldJson(jsonObject.toString());
    }

    static private ContentFieldDate getDateField(LinkedTreeMap objectMap) {
        JsonObject jsonObject = ContentClient.gson().toJsonTree(objectMap).getAsJsonObject();
        ContentDate date = ContentClient.gson().fromJson(jsonObject, ContentDate.class);
        return new ContentFieldDate(date);
    }

    static private ContentFieldReference getItemReference(LinkedTreeMap objectMap) {
        JsonObject jsonObject = ContentClient.gson().toJsonTree(objectMap).getAsJsonObject();
        Asset item = ContentAssetRequest.deserializeContentBaseItem(jsonObject);

        if (item instanceof DigitalAsset) {
            return new ContentFieldAssetReference((DigitalAsset)item);
        } else {
            return new ContentFieldItemReference((ContentItem)item);
        }
    }

    @SuppressWarnings({"unchecked"})
    static private ContentField getArrayField(ArrayList arrayList) {
        if (arrayList == null || arrayList.isEmpty()) {
            return new ContentFieldUnknown("unknown array type");
        }

        // either a text or reference list

        Object firstValue = arrayList.get(0);
        if (firstValue instanceof String) {
            // process as text array
            List<String> stringList = new ArrayList<>();
            for(Object value : arrayList) {
                stringList.add((String)value);
            }
            return new ContentFieldTextList(stringList);
        }


        // assume we are dealing with reference list
        List<ContentFieldReference> fieldList = new ArrayList<>();
        boolean isDigitalAsset = false;
        for(Object value : arrayList) {
            if (value instanceof LinkedTreeMap) {
                ContentFieldReference reference = getItemReference((LinkedTreeMap)value);
                fieldList.add(reference);
            }
        }

        return new ContentFieldReferenceList(fieldList);
    }

}
