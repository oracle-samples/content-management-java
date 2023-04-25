/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.mobile.content.sdk;

import org.junit.Assume;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.concurrent.TimeUnit;

import com.oracle.content.sdk.CacheSettings;
import com.oracle.content.sdk.ContentResponse;
import com.oracle.content.sdk.request.GetContentItemRequest;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Set of tests for cache behavior in SDK (requires Live connection)
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CacheTestsLive extends SDKSingleItemTest {


    @Before
    public void setUp() throws Exception {
        // test only if live test
        Assume.assumeTrue(testMode == Config.MODE.LIVE_TEST);
        useCache = true;
        super.setUp();
    }

    @Test
    public void getItemWithCache() {

        // make call to get the item by id
        GetContentItemRequest request = new GetContentItemRequest(clientAPI, itemID).expandAll().noCache();

        // make call to get the item by id
        ContentResponse response = makeSDKReqeust(request);

        // first call should come from network
        assertTrue(response.isSuccess());
        assertEquals(ContentResponse.CacheState.NETWORK, response.getCacheState());

        request = new GetContentItemRequest(clientAPI, itemID).expandAll();
        // for 2nd call, it should come from cache
        response = makeSDKReqeust(request);
        assertTrue(response.isSuccess());
        assertEquals(ContentResponse.CacheState.CACHED, response.getCacheState());
    }

    @Test
    public void getItemWithNoCache() {

        // test only if live test
        Assume.assumeTrue(testMode == Config.MODE.LIVE_TEST);

        // make call to get the item by id
        GetContentItemRequest request = new GetContentItemRequest(clientAPI, itemID).expandAll();

        // make call to get the item by id
        ContentResponse response = makeSDKReqeust(request.noCache());

        // first call should come from network
        assertTrue(response.isSuccess());
        assertEquals(ContentResponse.CacheState.NETWORK, response.getCacheState());

        // for 2nd call, force it to come from the network
        response = makeSDKReqeust(request.noCache());
        assertTrue(response.isSuccess());
        assertEquals(ContentResponse.CacheState.NETWORK, response.getCacheState());
    }

    @Test
    public void cacheConfigTests() {

        CacheSettings cacheSettings = new CacheSettings(cacheDir);
        assertTrue(cacheSettings.isEnabled());
        cacheSettings.setCacheExpiration(new CacheSettings.Expiration(1, TimeUnit.DAYS));
        cacheSettings.setOfflineCacheExpiration(new CacheSettings.Expiration(7, TimeUnit.DAYS));
        // set to 5MB
        cacheSettings.setHttpCacheSize(5*CacheSettings.MB);
        cacheSettings.setAssetCacheSize(10*CacheSettings.MB);
        cacheSettings.setAssetCacheDir(cacheDir);
        cacheSettings.enableOfflineCache(false);
    }


}
