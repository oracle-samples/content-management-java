/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.mobile.content.sdk;

import org.junit.Test;

import com.oracle.content.sdk.ContentResponse;
import com.oracle.content.sdk.model.item.ContentItem;
import com.oracle.content.sdk.model.item.CustomContentField;
import com.oracle.content.sdk.model.item.CustomContentType;
import com.oracle.content.sdk.model.digital.DigitalAsset;
import com.oracle.content.sdk.model.date.ContentDate;
import com.oracle.content.sdk.model.field.ContentFieldAssetReference;
import com.oracle.content.sdk.model.field.ContentFieldDate;
import com.oracle.content.sdk.model.field.ContentFieldDecimal;
import com.oracle.content.sdk.model.field.ContentFieldItemReference;
import com.oracle.content.sdk.model.field.ContentFieldLargeText;
import com.oracle.content.sdk.request.GetCustomContentItemRequest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;


/**
 * Test getting content items using annotation processor to process custom fields
 */
@SuppressWarnings("unchecked")
public class GetCustomContentItemTests extends SDKSingleItemTest {


    @CustomContentType("sdk_test_all_fields")
    public static class SdkTestAllFields extends ContentItem {

        @CustomContentField("sdk-test-text")
        String text;

        @CustomContentField("sdk-test-datetime")
        ContentFieldDate dateField;

        @CustomContentField("sdk-test-decimal")
        ContentFieldDecimal decimalField;

        @CustomContentField("sdk-test-decimal")
        String decimalFieldAsString;

        @CustomContentField("sdk-test-largetext")
        ContentFieldLargeText largeText;

        @CustomContentField("sdk-test-menuitem-ref")
        ContentFieldItemReference menuItemRef;

        @CustomContentField("sdk-test-asset-ref")
        ContentFieldAssetReference asset;

        @CustomContentField("does-not-exist")
        String expectedNull;

    }

    // get custom model and verify all fields
    @Test
    public void testGetCustomItem()  {

        // make call to get the item by id
        GetCustomContentItemRequest<SdkTestAllFields> request =
                new GetCustomContentItemRequest<>(clientAPI, SdkTestAllFields.class, itemID);
        request.linksNone();

        ContentResponse<SdkTestAllFields> response = makeSDKReqeust(request);
        assertTrue(response.isSuccess());

        SdkTestAllFields item = response.getResult();

        assertEquals(item.text, "text 1");

        assertNotNull(item.decimalField);
        assertEquals(12345.67, item.decimalField.getValue());

        assertNotNull(item.decimalFieldAsString);
        assertEquals("12345.67", item.decimalFieldAsString);

        assertNotNull(item.dateField);
        ContentDate date = item.dateField.getValue();
        assertEquals("UTC",date.getTimezone());
        assertTrue(date.getValue().startsWith("2018-08-23T07:00:00.000"));

        assertNull(item.expectedNull);

        assertNotNull(item.largeText);
        assertEquals("Large text field 1", item.largeText.getValue());


        assertNotNull(item.menuItemRef);
        ContentItem contentItem = item.menuItemRef.getValue();

        assertEquals(SearchContentItemsMenuTests.MENU_ITEM_TYPE, contentItem.getType());
        assertNotNull(contentItem.getId());

        DigitalAsset asset = item.asset.getValue();
        assertNotNull(asset);
        assertNotNull(asset.getId());
        assertEquals("sorbet.jpg", asset.getName());

    }


}
