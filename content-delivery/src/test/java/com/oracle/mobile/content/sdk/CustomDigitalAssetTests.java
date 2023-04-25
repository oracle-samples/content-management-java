/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.mobile.content.sdk;

import static junit.framework.Assert.assertEquals;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.oracle.content.sdk.model.digital.DigitalAsset;
import com.oracle.content.sdk.model.digital.DigitalAssetFields;

/**
 * testing custom digital assets
 */
public class CustomDigitalAssetTests extends SDKSingleItemTest {

    final String TEXT1_FIELD = "sdk_digital_text_1";
    final String NUM1_FIELD = "sdk_number_1";
    final String BOOL1_FIELD = "sdk_cda_bool1";
    final String CDA_TYPE = "sdk_custom_digital";

    final String TEXT1_VALUE = "text1 value";
    final int NUM1_VALUE = 12345;
    final boolean BOOL1_VALUE = true;

    final String ITEM1_NAME = "redwood.jpeg";
    final String FILE_EXT = "jpeg";
    final String MIME_TYPE = "image/jpeg";
    final String FILE_GROUP = "Images";

    // can't be a live test because not part of seed data
    final Config.MODE TEST_MODE = Config.MODE.MOCK_TEST;

    private static String  assetId;

    @Before
    public void setUp() throws Exception {

        // overrides for CDA tests which are outside normal tests
        singleItemType = CDA_TYPE;
        singleItemName = ITEM1_NAME;
        Config.TEST_MODE = TEST_MODE;
        testMode = TEST_MODE;


        // use alternative channel token
        channelToken = Config.TEAM_SERVER_CDA.channelToken;

        // get get the item
        super.setUp();
    }


    // test for standard and custom fields for digital asset
    @Test
    public void testDigitalAssetFields() {

        // get as a digital asset
        DigitalAsset asset = getDigitalAssetRequest(itemID);
        assertEquals(ITEM1_NAME, asset.getName());
        assertTrue(asset.getContentType().isDigitalAsset());
        assertEquals(CDA_TYPE, asset.getType());
        assertEquals(FILE_EXT, asset.getFileExtension());
        assertEquals(FILE_GROUP, asset.getFileGroup());
        assertEquals(MIME_TYPE, asset.getMimeType());

        // test digital asset fields
        DigitalAssetFields digitalAssetFields = asset.getAssetFields();
        assertEquals("jpeg", digitalAssetFields.getFileType());
        assertEquals(397747, digitalAssetFields.getSize().intValue());
        assertEquals(900, digitalAssetFields.getMetadata().getHeightAsIntger().intValue());
        assertEquals(600, digitalAssetFields.getMetadata().getWidthAsInteger().intValue());

        assertEquals(TEXT1_VALUE, asset.getTextField(TEXT1_FIELD));
        assertEquals(NUM1_VALUE, asset.getIntegerField(NUM1_FIELD).intValue());
        assertEquals(BOOL1_VALUE, asset.getBooleanField(BOOL1_FIELD).booleanValue());

    }

}
