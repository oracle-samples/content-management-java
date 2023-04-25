/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.mobile.content.sdk;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.oracle.content.sdk.model.Asset;
import com.oracle.content.sdk.model.AssetSearchResult;
import com.oracle.content.sdk.model.AssetType;
import com.oracle.content.sdk.model.digital.AdvancedVideoInfo;
import com.oracle.content.sdk.model.digital.AdvancedVideoInfoProperties;
import com.oracle.content.sdk.model.digital.DigitalAsset;
import com.oracle.content.sdk.model.digital.DigitalAssetRendition;
import com.oracle.content.sdk.model.digital.RenditionType;
import com.oracle.content.sdk.model.field.FieldName;
import com.oracle.content.sdk.request.SearchAssetsRequest;
import com.oracle.content.sdk.request.core.SearchQueryBuilder;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for advanced video, models, search, etc.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdvancedVideoTests extends SDKBaseTest {

    static String VIDEO_NAME = "big-buck-bunny_trailer.webm";

    private static String  assetId = null;

    @Before
    public void setUp() throws Exception {

        // only run in mock-mode
        testMode = Config.MODE.MOCK_TEST;
        super.setUp();
    }


    // search for all advanced videos and save the asset id
    @Test
    public void aSearchForAdvancedVideos() {

        SearchQueryBuilder build = new SearchQueryBuilder(AssetType.TYPE_DIGITAL_ASSET);
        build.andField(FieldName.NAME.getValue(), SearchQueryBuilder.QueryOperator.EQUALS, VIDEO_NAME);

        SearchAssetsRequest request= new SearchAssetsRequest(clientAPI).filter(build.build());
        request.fieldsAll();
        request.linksNone();

        // get list of ContentItems  based on the type
        AssetSearchResult searchResult = makeSearchRequest(request);
        assertTrue(searchResult.getCount() > 0);

        // get first item in search list
        for(Asset item : searchResult.getItems()) {
            System.out.println(item.getName());
            if (item.getName().equals(VIDEO_NAME)) {
                assetId = item.getId();
                assertTrue(item instanceof DigitalAsset);
                verifyAdvancedVideoInfo((DigitalAsset)item, false);
            }
        }

        assertNotNull(assetId);
    }

    // call to get the advanced video info
    @Test
    public void bGetAdvancedVideoAsset()  {

        assertNotNull(assetId);

        // make call get to get digital asset
        DigitalAsset asset = getDigitalAssetRequest( assetId);
        assertNotNull(asset);
        assertEquals(VIDEO_NAME, asset.getName());
        verifyAdvancedVideoInfo(asset, true);

        // renditions
        DigitalAssetRendition thumbnailRendition = asset.getRendition(RenditionType.Thumbnail.getName());
        assertNotNull(thumbnailRendition);
        assertEquals("advancedvideo", thumbnailRendition.getType());
        DigitalAssetRendition stripRendition = asset.getRendition(RenditionType.Strip.getName());
        assertNotNull(stripRendition);
        assertEquals("advancedvideo", stripRendition.getType());
    }

    public void verifyAdvancedVideoInfo(DigitalAsset digitalAsset, boolean extendedProps) {
        assertTrue(digitalAsset.isAdvancedVideo());
        AdvancedVideoInfo videoInfo = digitalAsset.getAdvancedVideoInfo();
        assertEquals("kaltura", videoInfo.getProvider());
        AdvancedVideoInfoProperties videoInfoProperties = digitalAsset.getAdvancedVideoInfoProperties();
        assertEquals(33, videoInfoProperties.getDuration().intValue());
        assertNotNull(videoInfoProperties.getVideoStripProperties());
        assertEquals("webm", videoInfoProperties.getExtension());
        assertEquals(VIDEO_NAME, videoInfoProperties.getSearchText().trim());
        assertEquals(VIDEO_NAME, videoInfoProperties.getName());
        assertTrue(videoInfoProperties.isReady());
        assertNotNull(videoInfoProperties.getEntryId());
        if (extendedProps) {
            assertEquals("2725152", videoInfoProperties.getPartnerId());
            assertEquals("45310622", videoInfoProperties.getPlayerId());
            assertNotNull(videoInfoProperties.getEndpoint());
        }

    }

}
