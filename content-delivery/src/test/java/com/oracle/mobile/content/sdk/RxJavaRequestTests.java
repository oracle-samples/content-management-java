/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.mobile.content.sdk;

import org.junit.Before;
import org.junit.Test;

import com.oracle.content.sdk.ContentException;
import com.oracle.content.sdk.ContentResponse;
import com.oracle.content.sdk.model.Asset;
import com.oracle.content.sdk.model.AssetLanguageVariations;
import com.oracle.content.sdk.request.GetContentItemRequest;
import com.oracle.content.sdk.request.GetItemLanguageVariationsRequest;
import com.oracle.content.sdk.request.core.ContentRequest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static com.oracle.content.sdk.ContentException.REASON.itemNotFound;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Live tests for the various methods that can be used for making "requests", RxJava.
 */
public class RxJavaRequestTests extends SDKSingleItemTest {

    private String ITEM_NAME = "Chocolate Supreme";

    @Before
    public void setUp() throws Exception {

        // override the item to use for single item
        singleItemType = "sdk_menu_item";
        singleItemName = ITEM_NAME;

        // get get the item
        super.setUp();
    }

    // override fetch to make rxjava call
    @Override
    protected ContentResponse fetchRequest(ContentRequest request) {

        Object object = request.observable().blockingGet();
        assert(object instanceof ContentResponse);

        return (ContentResponse)object;
    }

    // search to get the item we need (RxJava version)
    @Test
    public void testRxJavaCall() {

        GetContentItemRequest request = new GetContentItemRequest(clientAPI, itemID);

        ContentResponse<Asset> response = makeSDKReqeust(request);
        assertTrue(response.isSuccess());
        assertEquals(ITEM_NAME, response.getResult().getName());
    }


    // RxJava version (synchronous)
    @Test
    public void getItemLanguageVariationsRxJava() {
        GetItemLanguageVariationsRequest request = new GetItemLanguageVariationsRequest(clientAPI, itemID);
        ContentResponse<AssetLanguageVariations> response = makeSDKReqeust(request);
        GetItemLanguageVariationsTests.verifyResult(response.getResult());
    }

    // RxJava error tests
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void testRxJavaCallWithError(){

        GetContentItemRequest request = new GetContentItemRequest(clientAPI, itemID+"3");

        ContentResponse response = makeSDKReqeust(request);
        assertFalse(response.isSuccess());
        assertEquals(404, response.getHttpCode());
        assertEquals(ContentResponse.CacheState.ERROR, response.getCacheState());
        ContentException e = response.getException();
        assertNotNull(e);
        assertNotNull( e.getVerboseErrorMessage());
        assertEquals(itemNotFound, e.getReason());


    }
}
