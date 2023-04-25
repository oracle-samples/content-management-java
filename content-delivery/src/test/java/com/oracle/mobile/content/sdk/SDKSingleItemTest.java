/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.mobile.content.sdk;

import org.junit.Before;

import com.oracle.content.sdk.model.Asset;
import com.oracle.content.sdk.model.AssetSearchResult;
import com.oracle.content.sdk.request.SearchAssetsRequest;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Base class for any set of tests that just uses the single item.
 */
public abstract class SDKSingleItemTest extends SDKBaseTest {

    static String DEFAULT_TYPE = "sdk_test_all_fields";
    static String DEFAULT_NAME = "sdktest";

    // default values for single item (can be overriden)
    String singleItemType = DEFAULT_TYPE;
    String singleItemName = DEFAULT_NAME;

    static final String SINGLE_ASSERT_REF = "sorbet.jpg";
    static final String[] LIST_ASSERT_REF = {"croissant.jpg", "yogurt.jpg"};

    // the single item ID to use in derived class
    String itemID = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        // seed the item ID if in live mode

        if (testMode != Config.MODE.MOCK_TEST) {
            try {
                getTestItemBySearch();
            } catch (Exception e) {
                fail();
            }
        } else {
            // doesn't matter
            itemID = "mock";
        }

    }

    // search to get the item we need
    private void getTestItemBySearch()  {

        if (itemID != null)
            return;

        // get list of ContentItems  based on the type
        AssetSearchResult searchResult = makeSearchRequest(
                new SearchAssetsRequest(clientAPI)
                        .type(singleItemType)
                        .name(singleItemName)
                        .noCache()
        );


        int totalResults = searchResult.getCount();
        assertEquals(1, totalResults);

        // get first item in search list
        Asset item = searchResult.getItems().get(0);

        assertEquals(singleItemName, item.getName());

        itemID = item.getId();

    }

}
