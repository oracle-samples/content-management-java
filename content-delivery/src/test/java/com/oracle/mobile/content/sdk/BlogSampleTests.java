/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.mobile.content.sdk;

import org.junit.Before;
import org.junit.Test;

import com.oracle.content.sdk.model.item.ContentItem;
import com.oracle.content.sdk.model.digital.DigitalAsset;
import com.oracle.content.sdk.model.digital.RenditionType;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

// specific asset id to test against

/**
 * Set of tests against the blog sample server (used by Blog Sample application)
 */
public class BlogSampleTests extends SDKBaseTest {

    private static String ITEM_ID = "CORE25C2DBCBA880492391986D85C398F2AD";
    private static String ASSET_ID = "CONT182BF086A70341F3AC90F81F2621903A";
    private static String ASSET_NAME = "Blog_4_Header_1440x540px.jpg";

    private static String BASE_PATH =
            "/content/published/api/v1.1/assets/";

    @Before
    public void setUp() throws Exception {

        // MOCK TESTS tied to blog server
        testMode = Config.MODE.MOCK_TEST;
        super.setUp();
    }

    @Test
    public void getAssetFromField() {

        ContentItem item = getContentItem(ITEM_ID);

        DigitalAsset digitalAsset = item.getDigitalAssetField("thumbnail");
        assertNotNull(digitalAsset);

        assertNull(digitalAsset.getNativeDownloadUrl());
        assertNull(digitalAsset.getRenditionUrl(RenditionType.Thumbnail));

        String downloadUrl = clientAPI.buildDigitalAssetDownloadUrl(digitalAsset.getId());
        System.out.println("downloadUrl=" + downloadUrl);
        assertTrue(downloadUrl.contains(BASE_PATH+digitalAsset.getId()+"/native"));

        String thumbnailUrl = clientAPI.buildDigitalAssetThumbnailUrl(digitalAsset.getId());
        System.out.println("thumbnailUrl=" + thumbnailUrl);
        assertTrue(thumbnailUrl.contains(BASE_PATH+digitalAsset.getId()+"/thumbnail"));

    }


    @Test
    public void getAssetWithRenidtions() {

        DigitalAsset digitalAsset = getDigitalAssetRequest(ASSET_ID);

        assertEquals(ASSET_ID, digitalAsset.getId());

        assertEquals(ASSET_NAME, digitalAsset.getName());

        String downloadUrl = digitalAsset.getNativeDownloadUrl();
        System.out.println("downloadUrl=" + downloadUrl);
        assertTrue(downloadUrl.contains(BASE_PATH+digitalAsset.getId()+"/native"));
        String nativeUrl = digitalAsset.getRenditionUrl(RenditionType.Native);
        System.out.println("nativeUrl=" + nativeUrl);
        assertEquals(nativeUrl, downloadUrl);

        String smallUrl = digitalAsset.getRenditionUrl(RenditionType.Small);
        System.out.println("smallUrl=" + nativeUrl);
        assertNotNull(smallUrl);

    }




}
