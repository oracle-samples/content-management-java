/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.mobile.content.sdk;

import com.google.gson.JsonObject;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.oracle.content.sdk.model.Asset;
import com.oracle.content.sdk.model.item.ContentItem;
import com.oracle.content.sdk.model.digital.DigitalAsset;
import com.oracle.content.sdk.model.date.ContentDate;
import com.oracle.content.sdk.model.field.ContentField;
import com.oracle.content.sdk.model.field.ContentFieldBoolean;
import com.oracle.content.sdk.model.field.ContentFieldDate;
import com.oracle.content.sdk.model.field.ContentFieldDecimal;
import com.oracle.content.sdk.model.field.ContentFieldInteger;
import com.oracle.content.sdk.model.field.ContentFieldJson;
import com.oracle.content.sdk.model.field.ContentFieldLargeText;
import com.oracle.content.sdk.model.field.ContentFieldItemReference;
import com.oracle.content.sdk.model.field.ContentFieldReference;
import com.oracle.content.sdk.model.field.ContentFieldReferenceList;
import com.oracle.content.sdk.model.field.ContentFieldText;
import com.oracle.content.sdk.model.field.ContentFieldTextList;
import com.oracle.content.sdk.model.field.ContentFieldUnknown;
import com.oracle.content.sdk.model.field.FieldType;
import com.oracle.content.sdk.model.AssetSearchResult;
import com.oracle.content.sdk.model.field.CheckForRichText;
import com.oracle.content.sdk.request.GetContentItemRequest;
import com.oracle.content.sdk.request.SearchAssetsRequest;
import com.oracle.content.sdk.request.core.SearchQueryBuilder;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Set of tests for identifying every possible field type in a content item.
 */
public class GetContentItemFieldTypeTests extends SDKSingleItemTest {

    static String FIELD_TEXT = "sdk-test-text";
    static String FIELD_LARGETEXT = "sdk-test-largetext";
    static String FIELD_LARGETEXT_RTE_LIST = "sdk-test-largetext-rte-list";
    static String FIELD_TEXT_LIST = "sdk-test-text-list";
    static String FIELD_DECIMAL = "sdk-test-decimal";
    static String FIELD_NUMBER = "sdk-test-number";
    static String FIELD_BOOLEAN = "sdk-test-boolean";
    static String FIELD_ASSET_REF = "sdk-test-asset-ref";
    static String FIELD_ASSET_REF_LIST = "sdk-test-asset-ref-list";
    static String FIELD_MENU_ITEM_REF = "sdk-test-menuitem-ref";
    static String FIELD_DATE = "sdk-test-datetime";


    static String LARGETEXT_FIELD_VALUE = "Large text field 1";

    @Test
    public void testGetItemFields() throws Exception {

        // make call to get the item by id
        ContentItem item = getContentItemExpandAll(itemID);

        assertNotNull(item.getCreatedDate());
        assertNotNull(item.getUpdatedDate());
        assertNotNull(item.getType());
        assertNotNull(item.getLanguage());
        assertNotNull(item.getTranslatable());
        assertNotNull(item.toString());
        assertNotNull(item.parseContentItemFields());
        assertNotNull(item.parseContentItemFields().toString());
        Map<String, ContentField> fieldsMap = item.parseContentItemFields().getFieldsMap();
        assertNotNull(fieldsMap);
    }


    @Test
    public void testGetItemTextFields() throws Exception {

        // make call to get the item by id
        ContentItem item = getContentItem(itemID);

        // regular text field
        ContentField field1 = item.getFieldFromValue(FIELD_TEXT);
        assertNotNull(field1);
        assertEquals(FieldType.TEXT, field1.getType());
        assertTrue(field1 instanceof ContentFieldText);
        assertTrue(field1 instanceof CheckForRichText);
        ContentFieldText textField = item.getFieldFromType(FIELD_TEXT, FieldType.TEXT);
        // verify it handls this correctly by returning null
        ContentFieldDate dateField = item.getFieldFromType(FIELD_TEXT, FieldType.DATE);
        assertNull(dateField);
        ContentFieldText textFieldBad = item.getFieldFromType(FIELD_TEXT+"x", FieldType.TEXT);
        assertNull(textFieldBad);
        assertEquals("text 1", textField.getValue());
        assertEquals(field1.getValueAsString(), textField.getValue());

        assertEquals(item.getTextField(FIELD_TEXT), textField.getValue());

        ContentFieldLargeText largeTextField = item.getFieldFromType(FIELD_LARGETEXT, FieldType.LARGE_TEXT);
        assertEquals(FieldType.LARGE_TEXT, largeTextField.getType());
        assertEquals(LARGETEXT_FIELD_VALUE, largeTextField.getValue());
        assertFalse(largeTextField.isRichText());
        assertTrue(largeTextField instanceof CheckForRichText);


        String largeTextValue = item.getLargeTextField(FIELD_LARGETEXT);
        assertEquals(LARGETEXT_FIELD_VALUE, largeTextValue);
    }

    @Test
    public void testGetItemFieldErrorConditions() throws Exception {
        // make call to get the item by id
        ContentItem item = getContentItem(itemID);
        // expect null
        assertNull(item.getBooleanField(FIELD_TEXT_LIST));
        assertNull(item.getIntegerField(FIELD_TEXT_LIST));
        assertNull(item.getTextField("BadField"));
    }


        @Test
    public void testGetItemUpdateLargeText() throws Exception {

        // make call to get the item by id
        ContentItem item = getContentItem(itemID);

        // now query for field again with new large text field
        ContentFieldLargeText largeTextField = item.getFieldFromType(FIELD_LARGETEXT, FieldType.LARGE_TEXT);
        assertEquals(FieldType.LARGE_TEXT, largeTextField.getType());
        assertEquals(LARGETEXT_FIELD_VALUE, largeTextField.getValue());
        assertEquals(largeTextField.getValueAsString(), largeTextField.getValue());

    }

    @Test
    public void testGetItemStringListField() throws Exception {
        // make call to get the item by id
        ContentItem item = getContentItem(itemID);

        // string list
        ContentField field3 = item.getFieldFromValue(FIELD_TEXT_LIST);
        assertNotNull(field3);
        assertEquals(FieldType.TEXT_LIST, field3.getType());
        assertTrue(field3 instanceof ContentFieldTextList);
        assertNotNull(field3.getValueAsString());

        ContentFieldTextList stringListField =
                item.getFieldFromType(FIELD_TEXT_LIST, FieldType.TEXT_LIST);
        assertTrue(stringListField instanceof CheckForRichText);
        assertEquals(FieldType.TEXT_LIST, stringListField.getType());
        int i = 1;
        for (String listValue : stringListField.getValue()) {
            String expectedValue = "list" + i;
            assertEquals(expectedValue, listValue);
            i++;
        }
    }

    @Test
    public void testGetItemLargeTextListField() throws Exception {
        // make call to get the item by id
        ContentItem item = getContentItem(itemID);

        // string list for large text list
        ContentField field3 = item.getFieldFromValue(FIELD_LARGETEXT_RTE_LIST);
        assertNotNull(field3);
        assertEquals(FieldType.LARGE_TEXT_LIST, field3.getType());
        assertTrue(field3 instanceof ContentFieldTextList);
        ContentFieldTextList stringListField = (ContentFieldTextList) field3;
        assertTrue(stringListField.isRichText());
        int i = 1;
        for (String listValue : stringListField.getValue()) {
            assertTrue(ContentFieldLargeText.isRichText(listValue));
            System.out.println(listValue);
            i++;
        }
    }



    @Test
    public void testGetItemNumberFields() throws Exception {

        // make call to get the item by id
        ContentItem item = getContentItem(itemID);

        // decimal field
        ContentField field1 = item.getFieldFromValue(FIELD_DECIMAL);
        assertNotNull(field1);
        assertEquals(FieldType.DECIMAL, field1.getType());
        assertTrue(field1 instanceof ContentFieldDecimal);
        assertNotNull(field1.getValueAsString());
        Double value = item.getDecimalField(FIELD_DECIMAL);

        assertEquals(12345.67, value);


        // number field“
        Integer integer = item.getIntegerField(FIELD_NUMBER);

        assertNotNull(integer);
        assertEquals(54321, integer.intValue());


    }

    @Test
    public void testGetItemDecimalAsWholeValue() throws Exception {

        // make call to get the item by id
        ContentItem item = getContentItem(itemID);

        ContentFieldInteger field1 = item.getFieldFromType("sdk-test-decimal2", FieldType.INTEGER);
        assertNotNull(field1);
        assertEquals(1234, field1.getValue().intValue());


    }

    @Test
    public void testGetItemBooleanField() throws Exception {

        // make call to get the item by id
        ContentItem item = getContentItem(itemID);

        // boolean field
        ContentField field3 = item.getFieldFromValue(FIELD_BOOLEAN);
        assertNotNull(field3);
        assertEquals(FieldType.BOOLEAN, field3.getType());
        assertTrue(field3 instanceof ContentFieldBoolean);
        assertNotNull(field3.getValueAsString());
        Boolean value = item.getBooleanField(FIELD_BOOLEAN);
        assertTrue(value);
    }

    @Test
    public void testGetItemJsonField() throws Exception {

        // make call to get the item by id
        ContentItem item = getContentItem(itemID);

        // json field
        ContentField field3 = item.getFieldFromValue("sdk-test-json");
        assertNotNull(field3);
        assertEquals(FieldType.JSON, field3.getType());
        assertTrue(field3 instanceof ContentFieldJson);
        ContentFieldJson jsonField = (ContentFieldJson)field3;
        JsonObject json = jsonField.getAsJsonObject();
        TestCase.assertTrue(json.get("method").getAsString().contains("GET"));
        TestCase.assertTrue(json.get("rel").getAsString().contains("self"));
    }

    @Test
    public void testGetItemDateField() throws Exception {
        // make call to get the item by id
        ContentItem item = getContentItem(itemID);

        // date field
        ContentField field1 = item.getFieldFromValue(FIELD_DATE);
        assertNotNull(field1);
        assertEquals(FieldType.DATE, field1.getType());
        assertTrue(field1 instanceof ContentFieldDate);

        ContentDate date = item.getDateField(FIELD_DATE);
        assertEquals("UTC",date.getTimezone());
        assertTrue(date.getValue().startsWith("2018-08-23T07:00:00.000"));
    }


    // expected to be part of every url
    static final String EXPECTED_URL_PART= "/content/published/api/v1.1/";

    @Test
    public void testGetItemDigitalAssetReference() throws Exception {
        // make call to get the item by id
        ContentItem item = getContentItem(itemID);

        // digital asset reference
        DigitalAsset digitalAsset = item.getDigitalAssetField(FIELD_ASSET_REF);
        assertNotNull(digitalAsset);

        // for a reference, we basically only have id
        assertNotNull(digitalAsset.getId());
        assertTrue(!digitalAsset.getId().isEmpty());
        assertTrue(digitalAsset.isReferenceOnly());


        String url = clientAPI.buildDigitalAssetDownloadUrl(digitalAsset.getId());
        assertNotNull(url);
        assertTrue(url.contains(EXPECTED_URL_PART));
        assertEquals(url, clientAPI.buildDigitalAssetDownloadUrl(digitalAsset.getId()));
        assertNull(clientAPI.buildDigitalAssetDownloadUrl(null));
    }

    @Test
    public void testGetItemDigitalAssetExpandAll() throws Exception {
        // make call to get the item by id
        ContentItem item = getContentItemExpandAll(itemID);

        // digital asset reference (full fields)
        DigitalAsset digitalAsset = item.getDigitalAssetField(FIELD_ASSET_REF);
        verifyFullAsset(digitalAsset);
    }

    @Test
    public void testGetItemDigitalAssetExpandOneField() throws Exception {
        // make call to get the item by id
        ContentItem item = getContentItemSharedRequest(
                new GetContentItemRequest(clientAPI, itemID).expand(FIELD_ASSET_REF));

        // digital asset reference (full fields)
        DigitalAsset digitalAsset = item.getDigitalAssetField(FIELD_ASSET_REF);
        verifyFullAsset(digitalAsset);
    }


    private void verifyFullAsset(DigitalAsset digitalAsset) {
        assertNotNull(digitalAsset);
        final String fileName = SINGLE_ASSERT_REF;
        assertEquals(fileName, digitalAsset.getName());
        assertTrue(digitalAsset.isImage());
        assertTrue(!digitalAsset.isReferenceOnly());
        assertEquals("image/jpeg", digitalAsset.getMimeType());
        String downloadUrl = digitalAsset.getNativeDownloadUrl();
        assertNotNull(downloadUrl);
        assertTrue(downloadUrl.contains(EXPECTED_URL_PART));
        assertTrue(downloadUrl.contains(fileName));
        assertTrue(downloadUrl.contains(digitalAsset.getId()));
    }


    @Test
    public void testGetItemDigitalAssetReferenceList() throws Exception {
        // make call to get the item by id
        ContentItem item = getContentItem(itemID);

        // list to digital asset references
        ContentField field = item.getFieldFromValue(FIELD_ASSET_REF_LIST);
        assertNotNull(field);
        assertEquals(FieldType.REFERENCE_LIST, field.getType());
        assertTrue(field instanceof ContentFieldReferenceList);
        ContentFieldReferenceList<Asset> referenceList =
                item.getFieldFromType(FIELD_ASSET_REF_LIST, FieldType.REFERENCE_LIST);
        assertNotNull(referenceList.toString());
        assertNotNull(referenceList.getValue());
        assertEquals(2, referenceList.getValue().size());
        List<String> storedIds = new ArrayList<>();
        for(ContentFieldReference<Asset> itemField : item.getReferenceListField(FIELD_ASSET_REF_LIST)) {
            assertNotNull(itemField);
            Asset contentItem = itemField.getValue();
            assertNotNull(contentItem);
            assertTrue(contentItem.isDigitalAsset());
            assertNotNull(contentItem.getId());
            storedIds.add(contentItem.getId());
        }

        // test reference list ids method
        List<String> listIds = item.getReferenceListIds(FIELD_ASSET_REF_LIST);
        assertEquals(referenceList.getValue().size(), listIds.size());
        for(String id : listIds) {
            assertTrue(storedIds.contains(id));
        }
    }



    @Test
    public void testGetItemDigitalAssetReferenceListExpandAll() throws Exception {
        // make call to get the item by id
        ContentItem item = getContentItemExpandAll(itemID);

        // list to digital asset references
        ContentField field = item.getFieldFromValue(FIELD_ASSET_REF_LIST);
        assertNotNull(field);
        assertEquals(FieldType.REFERENCE_LIST, field.getType());
        assertTrue(field instanceof ContentFieldReferenceList);
        ContentFieldReferenceList<DigitalAsset> referenceList = (ContentFieldReferenceList)field;
        assertEquals(2, referenceList.getValue().size());
        int i = 0;

        for(ContentFieldReference<DigitalAsset> itemField : referenceList.getValue()) {
            assertNotNull(itemField);
            Asset contentItem = itemField.getValue();
            assertNotNull(contentItem);
            assertTrue(contentItem.isDigitalAsset());
            assertTrue(!contentItem.isReferenceOnly());
            assertNotNull(itemField.getValue());
            assertNotNull(contentItem.getId());
            String imageName = LIST_ASSERT_REF[i++];
            assertEquals(imageName, contentItem.getName());

            DigitalAsset digitalAsset = itemField.getValue();
            assertNotNull(digitalAsset);
            assertTrue(digitalAsset.isImage());
            assertEquals("image/jpeg", digitalAsset.getMimeType());
            String downloadUrl = digitalAsset.getNativeDownloadUrl();
            assertNotNull(downloadUrl);
            assertTrue(downloadUrl.contains(EXPECTED_URL_PART));
            assertTrue(downloadUrl.contains(imageName));
            assertTrue(downloadUrl.contains(digitalAsset.getId()));
        }
    }

    @Test
    public void testGetItemContentItemReference() throws Exception {
        // make call to get the item by id
        ContentItem item = getContentItem(itemID);

        // content item reference
        ContentField field = item.getFieldFromValue(FIELD_MENU_ITEM_REF);
        assertNotNull(field);
        assertEquals(FieldType.CONTENT_ITEM, field.getType());
        assertTrue(field instanceof ContentFieldItemReference);
        ContentFieldItemReference fieldReference  =
                item.getFieldFromType(FIELD_MENU_ITEM_REF, FieldType.CONTENT_ITEM);

        ContentItem contentItem = fieldReference.getValue();

        assertEquals(SearchContentItemsMenuTests.MENU_ITEM_TYPE, contentItem.getType());
        assertNotNull(contentItem.getId());

        // alternative way to get item
        ContentItem dup = item.getContentItemField(FIELD_MENU_ITEM_REF);
        assertEquals(dup.getId(), contentItem.getId());

    }

    @Test
    public void testGetItemContentItemNullReference() throws Exception {
        // make call to get the item by id
        ContentItem item = getContentItem(itemID);

        // content item reference that is empty, so we expect "unknown"
        ContentField field = item.getFieldFromValue("sdk-test-anyitem-ref");
        assertNotNull(field);
        assertEquals(FieldType.UNKNOWN, field.getType());
        assertTrue(field instanceof ContentFieldUnknown);
        ContentFieldUnknown fieldReference  = (ContentFieldUnknown)field;
        assertNotNull(field.getValueAsString());

        assertEquals("null", fieldReference.getValueAsString());

        // we expect null for the reference if getting specific type
        ContentItem fieldRef = item.getContentItemField("sdk-test-anyitem-ref");
        assertNull(fieldRef);

    }

    @Test
    public void testGetItemContentItemExpandAll() throws Exception {
        // make call to get the item by id
        ContentItem item = getContentItemExpandAll(itemID);

        // content item reference
        ContentField field = item.getFieldFromValue(FIELD_MENU_ITEM_REF);
        assertNotNull(field);
        assertEquals(FieldType.CONTENT_ITEM, field.getType());
        assertTrue(field instanceof ContentFieldItemReference);
        assertNotNull(field.getValueAsString());

        ContentFieldItemReference fieldReference  = (ContentFieldItemReference)field;

        Asset contentItem = fieldReference.getValue();

        assertEquals(SearchContentItemsMenuTests.MENU_ITEM_TYPE, contentItem.getType());
        assertNotNull(contentItem.getId());
        assertTrue(!contentItem.isReferenceOnly());
        assertTrue(contentItem instanceof ContentItem);

        // as a full item, this should have fields
        assertNotNull(((ContentItem)contentItem).parseContentItemFields());
        assertEquals("Croissant", contentItem.getName());

    }

    @Test
    public void testGetItemContentItemReferenceList() throws Exception {
        // make call to get the item by id
        ContentItem item = getContentItem(itemID);

        // list to content item references
        ContentField field = item.getFieldFromValue("sdk-test-item-ref-list");
        assertNotNull(field);
        assertNotNull(field.getValueAsString());

        assertEquals(FieldType.REFERENCE_LIST, field.getType());
        assertTrue(field instanceof ContentFieldReferenceList);
        ContentFieldReferenceList<Asset> referenceList = (ContentFieldReferenceList)field;
        assertNotNull(referenceList.getValue());
        assertEquals(3, referenceList.getValue().size());
        for(ContentFieldReference<Asset> itemField : referenceList.getValue()) {
            assertNotNull(itemField);
            Asset contentItem = itemField.getValue();
            assertNotNull(contentItem);
            assertTrue(!contentItem.isDigitalAsset());
            assertNotNull(contentItem.getId());
        }
    }

    @Test
    public void testGetItemContentItemExpandAllList() throws Exception {
        // make call to get the item by id
        ContentItem item = getContentItemExpandAll(itemID);

        // list to content item references
        ContentField field = item.getFieldFromValue("sdk-test-item-ref-list");
        assertNotNull(field);
        assertEquals(FieldType.REFERENCE_LIST, field.getType());
        assertTrue(field instanceof ContentFieldReferenceList);
        ContentFieldReferenceList<ContentItem> referenceList = (ContentFieldReferenceList)field;
        assertNotNull(referenceList.getValue());
        assertEquals(3, referenceList.getValue().size());
        for(ContentFieldReference<ContentItem> itemField : referenceList.getValue()) {
            assertNotNull(itemField);
            // get content item and check all fields since this is full item (not reference)
            Asset contentItem = itemField.getValue();
            assertNotNull(contentItem);
            assertNotNull(contentItem.getId());
            assertNotNull(contentItem.getName());
            assertFalse(contentItem.getName().isEmpty());
            assertFalse(contentItem.getContentType().isDigitalAsset());
            assertEquals(SearchContentItemsMenuTests.MENU_ITEM_TYPE, contentItem.getType());
        }
    }

    // test that specifying specified fields when searching
    @Test
    public void testSearchItemSpecificFields() throws Exception {

        // restrict result to only name and field text field
        List<String> fieldList = Arrays.asList("name", FIELD_TEXT);
        String fields = SearchQueryBuilder.getFieldList(fieldList);

        SearchAssetsRequest request =
                new SearchAssetsRequest(clientAPI).type(singleItemType).linksNone().fields(fields);

        // search
        AssetSearchResult searchResult = makeSearchRequest(request);

        Asset baseItem = searchResult.getItems().get(0);
        ContentItem item = (ContentItem) baseItem;


        // these fields are expected
        assertNotNull(item.getFieldFromValue(FIELD_TEXT));
        assertNotNull(item.getFieldFromValue(FIELD_TEXT));
        // always get the id
        assertNotNull(item.getId());
        assertEquals(singleItemName, item.getName());

        // other fields are not
        assertNull(item.getDecimalField(FIELD_DECIMAL));
        assertNull(item.getDescription());
        assertNull(item.getCreatedDate());

        // and links are not
        assertTrue(item.getLinks().isEmpty());

    }

    @Test
    public void testFieldTypeMatch() {
        assertEquals(FieldType.TEXT, FieldType.getFieldType("text", false, false));
        assertEquals(FieldType.TEXT_LIST, FieldType.getFieldType("text", true, false));
        assertEquals(FieldType.DIGITAL_ASSET, FieldType.getFieldType("reference", false, true));
        assertEquals(FieldType.REFERENCE_LIST, FieldType.getFieldType("reference", true, false));

    }

}
