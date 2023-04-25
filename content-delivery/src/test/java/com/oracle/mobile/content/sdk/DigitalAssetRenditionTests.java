/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.mobile.content.sdk;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.oracle.content.sdk.model.item.ContentItem;
import com.oracle.content.sdk.model.digital.DigitalAsset;
import com.oracle.content.sdk.model.digital.DigitalAssetPreferredRenditionCriteria;
import com.oracle.content.sdk.model.digital.DigitalAssetRendition;
import com.oracle.content.sdk.model.digital.RenditionType;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for digital asset renditions
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DigitalAssetRenditionTests extends SDKSingleItemTest {

    private static String  assetId;


    // first get content item
    @Test
    public void aGetContentItem() {

        ContentItem item = getContentItem(itemID);

        // digital asset reference
        final DigitalAsset digitalAsset = item.getDigitalAssetField("sdk-test-asset-ref");
        assertNotNull(digitalAsset);

        // for a reference, we basically only have id
        assertNotNull(digitalAsset.getId());
        assertFalse(digitalAsset.getId().isEmpty());
        assertTrue(digitalAsset.getContentType().isDigitalAsset());

        if (testMode.equals(Config.MODE.MOCK_TEST)) {
            assetId = "mock";
        } else {
            assetId = digitalAsset.getId();
        }


    }

    // then get the digital asset and test the rendition code
    @Test
    public void getDigitalAssetTestRenditions()  {

        // make call get to get digital asset
        DigitalAsset asset = getDigitalAssetRequest( assetId);
        assertEquals( asset.getRenditionUrl(RenditionType.Thumbnail.name()), asset.getRenditionUrl(RenditionType.Thumbnail));
        assertNotNull(asset.getAssetFields());
        assertEquals(56348, asset.getSize().intValue());

        assertNotNull(asset.getAssetFields().getFileType());

        // log all rendition info
        logAllRenditionInfo(asset);

        // verify rendition info
        DigitalAssetRendition thumbnailRendition = asset.getRendition(RenditionType.Thumbnail.getName());
        assertEquals(RenditionType.Thumbnail.getName(), thumbnailRendition.getName());
        DigitalAssetRendition.RenditionFormat format = thumbnailRendition.getBestMatchingFormat("jpg");
        assertEquals(150, (int)format.getWidth());
        assertEquals(103, (int)format.getHeight());
        assertEquals("150", format.getMetadata().getWidth());
        assertNotNull(format.getMimeType());

        assertNotNull(thumbnailRendition.getFormats());
        assertEquals(RenditionType.Thumbnail, thumbnailRendition.getRendition());

        DigitalAssetRendition mediumlRendition = asset.getRendition(RenditionType.Medium.getName());
        assertEquals(RenditionType.Medium.getName(), mediumlRendition.getName());

        DigitalAssetRendition.RenditionFormat formatMedium = mediumlRendition.getBestMatchingFormat("jpg");
        assertEquals(500, (int)formatMedium.getWidth());
        assertEquals(346, (int)formatMedium.getHeight());


        assertEquals("500", asset.getAssetFields().getMetadata().getWidth());
        assertEquals("346", asset.getAssetFields().getMetadata().getHeight());

        assertEquals(56348, (int)formatMedium.getSize());
        assertNotNull(asset.getMimeType());

        // search for smallest asset
        DigitalAssetPreferredRenditionCriteria criteria = new DigitalAssetPreferredRenditionCriteria();
        criteria.searchForSmallest(0, 0);
        // get matching "small" rendition
        DigitalAssetRendition.RenditionFormat smallFormat = asset.getPreferredRendition(criteria);
        assertEquals(RenditionType.Small.getName(), smallFormat.getRenditionName());
        assertEquals(300, (int)smallFormat.getWidth());
        assertEquals(207, (int)smallFormat.getHeight());

        // search for largest asset
        DigitalAssetPreferredRenditionCriteria criteriaLarge = new DigitalAssetPreferredRenditionCriteria();
        criteria.searchForLargest(500, 500);
        criteriaLarge.setDesiredFormat("jpg");
        // get matching "small" rendition
        DigitalAssetRendition.RenditionFormat small2Format = asset.getPreferredRendition(criteria);
        assertEquals(RenditionType.Small.getName(), small2Format.getRenditionName());

        assertEquals(RenditionType.Unknown, RenditionType.getRenditionFromName("bogus"));

    }

    private static void log(String s) {
        System.out.println(s);
    }

    private static void logAllRenditionInfo(DigitalAsset digitalAsset) {
        // log all information about the digital asset
        log("native url:" + digitalAsset.getNativeDownloadUrl());
        log("version:" + digitalAsset.getVersion());
        for(DigitalAssetRendition rendition : digitalAsset.getRenditionsList()) {
            log("--------------");
            log("rendition:" + rendition.getName());
            log("type:" +rendition.getType());
            DigitalAssetRendition.RenditionFormat format = rendition.getBestMatchingFormat("jpg");
            log("size:" + format.getSize());
            log("width:" + format.getWidth());
            log("height:" + format.getHeight());
            log("downloadUrl:" + format.getDownloadUrl());
        }

        DigitalAssetPreferredRenditionCriteria criteria = new DigitalAssetPreferredRenditionCriteria();
        criteria.searchForSmallest(0, 0);
        // get matching "small" rendition
        DigitalAssetRendition.RenditionFormat smallFormat = digitalAsset.getPreferredRendition(criteria);
        log("SMALL FORMAT:" + smallFormat.getRenditionName());

        criteria.searchForSmallest(300, 300);
        // get matching "medium" rendition
        DigitalAssetRendition.RenditionFormat mediumFormat = digitalAsset.getPreferredRendition(criteria);
        log("MEDIUM FORMAT:" + mediumFormat.getRenditionName());


    }
}
