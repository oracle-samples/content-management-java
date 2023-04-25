/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model;

import com.oracle.content.sdk.model.date.ContentDate;
import com.oracle.content.sdk.model.digital.DigitalAsset;
import com.oracle.content.sdk.model.field.ContentField;
import com.oracle.content.sdk.model.field.ContentFieldAssetReference;
import com.oracle.content.sdk.model.field.ContentFieldBoolean;
import com.oracle.content.sdk.model.field.ContentFieldDate;
import com.oracle.content.sdk.model.field.ContentFieldDecimal;
import com.oracle.content.sdk.model.field.ContentFieldInteger;
import com.oracle.content.sdk.model.field.ContentFieldItemReference;
import com.oracle.content.sdk.model.field.ContentFieldJson;
import com.oracle.content.sdk.model.field.ContentFieldLargeText;
import com.oracle.content.sdk.model.field.ContentFieldReference;
import com.oracle.content.sdk.model.field.ContentFieldReferenceList;
import com.oracle.content.sdk.model.field.ContentFieldText;
import com.oracle.content.sdk.model.field.FieldType;
import com.oracle.content.sdk.model.item.AssetFields;
import com.oracle.content.sdk.model.item.ContentItem;
import com.oracle.content.sdk.model.taxonomy.Taxonomy;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Common base class for {@link ContentItem} and {@link DigitalAsset}.
 */
@SuppressWarnings("unused")
public abstract class Asset extends AssetLinksObject {

    @SerializedName("id")
    @Expose
    protected String id;

    @SerializedName("name")
    @Expose
    protected String name;

    @SerializedName("type")
    @Expose
    protected String type;
    @SerializedName("typeCategory")
    @Expose
    protected String typeCategory;

    @SerializedName("description")
    @Expose
    protected String description;

    @SerializedName("createdDate")
    @Expose
    protected ContentDate createdDate;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("updatedDate")
    @Expose
    protected ContentDate updatedDate;
    @SerializedName("taxonomies")
    @Expose
    private ItemList<Taxonomy> taxonomies;

    @SerializedName("mimeType")
    @Expose
    private String mimeType;
    @SerializedName("fileGroup")
    @Expose
    private String fileGroup;
    @SerializedName("fileExtension")
    @Expose
    private String fileExtension;


    @SerializedName("language")
    @Expose
    protected String language;

    @SerializedName("translatable")
    @Expose
    private Boolean translatable;

    @SerializedName("fields")
    @Expose
    protected Map<String, Object> fields;


    public ItemList<Taxonomy> getTaxonomies() {
        return taxonomies;
    }

    /**
     * Get raw value of the "type" value.  To interpret this value in general
     * it is better to use {@link #getContentType()}
     *
     * @return content type as a string
     */
    public String getType() {
        return type;
    }

    /**
     * Get raw value of the "typeCategory".  To interpret this value in general
     * it is better to use {@link #getContentType()}
     *
     * @return content type category as a string
     */
    public String getTypeCategory() {
        return typeCategory;
    }

    /**
     * A representation of content type data which includes both "type" and "typeCategory" fields.
     * Use methods on {@link AssetType} to determine more info.
     *
     * @return content type representation
     */
    public AssetType getContentType() {
        return new AssetType(type, typeCategory);
    }

    /**
     * Get description for this item.
     *
     * @return description for item
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get created date for item.
     *
     * @return Created date for item.
     */
    public ContentDate getCreatedDate() {
        return createdDate;
    }

    /**
     * Get updated date for item.
     *
     * @return Update date for item.
     */

    public ContentDate getUpdatedDate() {
        return updatedDate;
    }

    /*
     * If the item is only a reference it will contain basic fields like name, id, and
     * lack the full set of fields such as date.
     *
     * @return true if this is a reference
     */
    public boolean isReferenceOnly() {
        // if date is empty, assume this is a reference
        return (createdDate == null);
    }

    /**
     * Get the content item Id
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Get the name of the content item
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the slug for the item.
     *
     * @return item slug
     */
    public String getSlug() { return slug; }

    /**
     * This method checks the type field and will return true if it is a DigitalAsset type.
     * Another method would be check if this object is an instanceof {@link DigitalAsset}
     *
     * @return true if type field is a "DigitalAsset"
     */
    public boolean isDigitalAsset() {
        return getContentType().isDigitalAsset();
    }

    /**
     *  @return file extension for digital assets or "contentItem"
     */
    public String getFileExtension() { return fileExtension; }

    /**
     *  @return mimetype for digital assets or "contentItem"
     */
    public String getMimeType() { return mimeType; }

    /**
     *  @return fileGroup for digital assets (e.g. "Images") or "contentItem"
     */
    public String getFileGroup() { return fileGroup; }

    /**
     * If applicable, a string representing the language.
     * @return string for language or null if not set
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Get whether the item is defined as translatable
     * @return true if translatable
     */
    public Boolean getTranslatable() {
        return translatable;
    }

    /**
     * This method will "guess" the field type based on the value of the field.
     * If you know the type of the field, you should always use {@link #getFieldFromType(String, FieldType)}
     * or the convenience methods that return specific values such as {@link #getTextField(String)}
     *
     * @param fieldName Field name in content item.
     * @return ContentField if exists, else null
     */
    public ContentField getFieldFromValue(String fieldName) {
        Object value = fields.get(fieldName);
        return AssetFields.getFieldFromValue(value, null);
    }

    /**
     * A generic method that will return the content field given the specified type.
     * For example, you could call it like this to get a the field for a content date:
     * {@code
     * ContentFieldDate dateField = item.getFieldFromType("date_field", FieldType.DATE);
     * }
     * To get the value directly for common fields, use the convenience methods below.
     * For example, {@link #getDateField(String)}
     *
     *
     * @param fieldName field name to match
     * @param type The type of field expected
     * @param <T> the specific ContentField class
     * @return Specific ContentField derived class.
     */
    @SuppressWarnings("unchecked")
    public <T extends ContentField> T getFieldFromType(String fieldName, FieldType type) {
        ContentField field = AssetFields.getFieldFromValue(fields.get(fieldName), type);
        return (T)field;
    }

    /**
     * Get a Digital Asset if it is contained as a field in this content item, or
     * null if there is no matching digital asset field.
     *
     * @param fieldName field name to a digital asset
     * @return {@link DigitalAsset} object or null if not found
     */
    public DigitalAsset getDigitalAssetField(String fieldName) {
        ContentFieldAssetReference asset = getFieldFromType(fieldName, FieldType.DIGITAL_ASSET);
        return asset != null ? asset.getValue() : null;
    }

    /**
     * Get a Content Item reference if it is contained as a field in this content item, or
     * null if there is no matching content item field.
     *
     * @param fieldName field name to a content item
     * @return {@link ContentItem} object or null if not found
     */
    public ContentItem getContentItemField(String fieldName) {
        ContentFieldItemReference item = getFieldFromType(fieldName, FieldType.CONTENT_ITEM);
        return item != null ? item.getValue() : null;
    }

    public Boolean getBooleanField(String fieldName) {
        ContentFieldBoolean field = getFieldFromType(fieldName, FieldType.BOOLEAN);
        return field != null ? field.getValue() : null;
    }

    public Integer getIntegerField(String fieldName) {
        ContentFieldInteger field = getFieldFromType(fieldName, FieldType.INTEGER);
        return field != null ? field.getValue() : null;
    }

    public Double getDecimalField(String fieldName) {
        ContentFieldDecimal field = getFieldFromType(fieldName, FieldType.DECIMAL);
        return field != null ? field.getValue() : null;
    }

    public String getTextField(String fieldName) {
        ContentFieldText field = getFieldFromType(fieldName, FieldType.TEXT);
        return field != null ? field.getValue() : null;
    }

    public String getLargeTextField(String fieldName) {
        ContentFieldLargeText field = getFieldFromType(fieldName, FieldType.LARGE_TEXT);
        return field != null ? field.getValue() : null;
    }

    public ContentDate getDateField(String fieldName) {
        ContentFieldDate field = getFieldFromType(fieldName, FieldType.DATE);
        return field != null ? field.getValue() : null;
    }

    public String getJsonField(String fieldName) {
        ContentFieldJson field = getFieldFromType(fieldName, FieldType.JSON);
        return field != null ? field.getValue() : null;
    }

    public List<ContentFieldReference<Asset>> getReferenceListField(String fieldName) {
        ContentFieldReferenceList<Asset> field =
                getFieldFromType(fieldName, FieldType.REFERENCE_LIST);
        return field != null ? field.getValue() : null;
    }

    public List<String> getReferenceListIds(String fieldName) {
        List<ContentFieldReference<Asset>> referenceList = getReferenceListField(fieldName);
        List<String> ids = new ArrayList<>();
        if (referenceList != null && referenceList.size() > 0) {
            for(ContentFieldReference<Asset> reference : referenceList) {
                ids.add(reference.getValue().getId());
            }
        }

        return ids;
    }



}
