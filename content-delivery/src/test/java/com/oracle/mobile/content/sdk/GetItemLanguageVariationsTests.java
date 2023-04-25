/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.mobile.content.sdk;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.oracle.content.sdk.ContentResponse;
import com.oracle.content.sdk.model.item.ContentItem;
import com.oracle.content.sdk.model.AssetLanguageVariations;
import com.oracle.content.sdk.request.GetItemLanguageVariationsRequest;
import com.oracle.content.sdk.request.core.ContentRequestById;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Set of tests for item language variations
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("unchecked")
public class GetItemLanguageVariationsTests extends SDKSingleItemTest {


    private static String langId;
    private static String slugId;

    @Before
    public void setUp() throws Exception {

        // override the item to use for single item
        singleItemType = "sdk_menu_item";
        singleItemName = "Chocolate Supreme";

        // get get the item
        super.setUp();
    }

    @Test
    public void getItemLanguageVariations(){

        assertNotNull(clientAPI);
        GetItemLanguageVariationsRequest request = new GetItemLanguageVariationsRequest(clientAPI, itemID);

        // make request
        ContentResponse<AssetLanguageVariations> response = makeSDKReqeust(request);
        assertTrue(response.isSuccess());

        // deserialize
        AssetLanguageVariations variations = response.getResult();

        // verify result
        verifyResult(variations);
    }


    public static void verifyResult(AssetLanguageVariations variations) {
        assertNotNull(variations);

        assertEquals(3, variations.getItems().size());
        assertNotNull(variations.getSetId());
        assertNotNull(variations.getMasterItem());
        // save "fr" lang id for next test.
        for(AssetLanguageVariations.Item item : variations.getItems()) {
            if (item.getValue().equals("fr")) {
                langId = item.getId();
            }
        }
    }

    @Test
    public void testTranslatedItem() {

        // make call to get the item by id
        ContentItem item = getContentItem(langId);

        assertEquals("fr", item.getLanguage());
        assertEquals("Chocolat suprême", item.getName());

        // save for use in next test
        slugId = item.getSlug();
    }


    @Test
    public void zTestGetVariationsBySlug() {

        GetItemLanguageVariationsRequest request =
                new GetItemLanguageVariationsRequest(clientAPI, slugId, ContentRequestById.IdType.SLUG);

        // make request
        ContentResponse<AssetLanguageVariations> response = makeSDKReqeust(request);
        assertTrue(response.isSuccess());

        // deserialize
        AssetLanguageVariations variations = response.getResult();

        // verify result
        verifyResult(variations);
    }

}
