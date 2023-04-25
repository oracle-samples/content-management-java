/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.mobile.content.sdk;

import org.junit.Assume;
import org.junit.Test;

import com.oracle.content.sdk.CacheSettings;
import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.ContentError;
import com.oracle.content.sdk.ContentException;
import com.oracle.content.sdk.ContentResponse;
import com.oracle.content.sdk.ContentSDK;
import com.oracle.content.sdk.ContentSettings;
import com.oracle.content.sdk.model.AssetSearchResult;
import com.oracle.content.sdk.request.GetContentItemRequest;
import com.oracle.content.sdk.request.SearchAssetsRequest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static com.oracle.content.sdk.ContentException.REASON.invalidRequest;
import static com.oracle.content.sdk.ContentException.REASON.itemNotFound;
import static com.oracle.content.sdk.ContentException.REASON.responseError;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Set of tests for handling error conditions from the SDK, all of which are "live tests"
 */
@SuppressWarnings("unchecked")
public class ErrorTests extends SDKSingleItemTest {

    /*
    @Before
    public void verifyTestModeLive() {
        // test only if live test
        Assume.assumeTrue(testMode == Config.MODE.LIVE_TEST);
    }
*/

    @Test
    public void testGetItemBadId() {

        // make call to get the item by id with an invalid id
        GetContentItemRequest request = new GetContentItemRequest(clientAPI, itemID + "INVALID");

        // make call to get the item by id
        ContentResponse response = makeSDKReqeust(request);

        // we expect this to fail
        assertFalse(response.isSuccess());
        assertEquals(404, response.getHttpCode());
        assertEquals(ContentResponse.CacheState.ERROR, response.getCacheState());
        ContentException e = response.getException();
        assertNotNull(e);
        assertNotNull( e.getVerboseErrorMessage());
        assertEquals(itemNotFound, e.getReason());

    }

    @Test
    public void testGetItemInvalidParam() {

        // make call to get the item by id with an invalid id
        GetContentItemRequest request = new GetContentItemRequest(clientAPI, (itemID));

        // simulate a bad value for a parameter
        request.expand("BAD!!!");

        // make call to get the item by id
        ContentResponse response = makeSDKReqeust(request);

        // we expect this to fail
        assertFalse(response.isSuccess());
        // we expect a bad request
        assertEquals(400, response.getHttpCode());
        assertEquals(ContentResponse.CacheState.ERROR, response.getCacheState());
        ContentException e = response.getException();
        assertNotNull(e);
        assertNotNull( e.getVerboseErrorMessage());
        assertEquals(invalidRequest, e.getReason());

        // test that content error structure comes back
        ContentError contentError = e.getContentError();
        assertEquals(e.getDetail(), contentError.getDetail());
        assertEquals(response.getHttpCode(), contentError.getStatus().intValue());
        assertNotNull(contentError.getTitle());
        assertNotNull(contentError.getType());
        assertNotNull(contentError.getStatus());

    }

    @Test
    public void testSearchNotFound() {
        SearchAssetsRequest searchRequest =
                new SearchAssetsRequest(clientAPI).type("unknown");

        ContentResponse<AssetSearchResult> response = makeSDKReqeust(searchRequest);
        assertFalse(response.isSuccess());
        ContentError error = response.getException().getContentError();
        // SDK treats this as an "invalid request"
        assertEquals(400, error.getStatus().intValue());
        assertTrue(error.getDetail().contains("not found"));
        assertEquals(ContentException.REASON.invalidRequest, response.getException().getReason());
    }

    // called with invalid url
    @Test
    public void testInvalidServerUrl() {

        Assume.assumeTrue(testMode == Config.MODE.LIVE_TEST);


        // create client with an invalid url
        String INVALID_URL = "http://unknownhost.foobar";
        ContentDeliveryClient api = ContentSDK.createDeliveryClient(INVALID_URL, channelToken);

        assertNotNull( api.getBaseUrl());


        GetContentItemRequest request = new GetContentItemRequest(api, itemID);
        // we excpect this to fail
        ContentResponse response = request.fetch();

        assertFalse(response.isSuccess());
        ContentException e = response.getException();
        assertNotNull(e);
        assertNotNull( e.getVerboseErrorMessage());
        assertEquals(responseError, e.getReason());
        assertEquals(ContentResponse.CacheState.ERROR, response.getCacheState());

    }

    // called with invalid channel token
    @Test
    public void testInvalidChannelToken() {


        // this can only be run as a live test
        Assume.assumeTrue(testMode == Config.MODE.LIVE_TEST);

        String badToken = "982349823498";

        // create client with an invalid url
        ContentDeliveryClient api = ContentSDK.createDeliveryClient(serverUrl, badToken);      //invalid token


        GetContentItemRequest request = new GetContentItemRequest(api, itemID);
        // we excpect this to fail
        ContentResponse response =  makeSDKReqeust(request);

        assertFalse(response.isSuccess());
        ContentException e = response.getException();
        assertNotNull(e);
        assertEquals(403, response.getHttpCode());
        assertEquals(403, e.getResponseCode());
        assertEquals(responseError, e.getReason());
        assertNotNull( e.getLogMessage());
        assertNotNull( e.getVerboseErrorMessage());

        // test that content error structure comes back
        ContentError contentError = e.getContentError();
        assertEquals(e.getDetail(), contentError.getDetail());
        assertEquals(response.getHttpCode(), contentError.getStatus().intValue());
        assertNotNull(contentError.getTitle());
        assertNotNull(contentError.getType());

    }


    @Test
    public void testConnectTimeout() {

        // this can only be run as a live test
        Assume.assumeTrue(testMode == Config.MODE.LIVE_TEST);

        ContentSettings settings = new ContentSettings().setTimeoutSeconds(3).setCacheSettings(new CacheSettings(cacheDir));

        // create client with a valid url, detect timeout
        ContentDeliveryClient api = ContentSDK.createDeliveryClient("http://www.oracle.com", channelToken, settings);

        GetContentItemRequest request = new GetContentItemRequest(api, itemID);
        // we excpect this to fail
        ContentResponse response = request.fetch();

        assertFalse(response.isSuccess());
        ContentException e = response.getException();
        assertNotNull(e);
        assertNotNull( e.getVerboseErrorMessage());
    }


    @Test
    public void testItemNotFound() {

        // make call to get the item by id with an invalid id
        GetContentItemRequest request = new GetContentItemRequest(clientAPI, "ORE4F6C3FF470C047A99939DD907AB04887");

        // make call to get the item by id
        ContentResponse response = makeSDKReqeust(request);

        // we expect this to fail
        assertFalse(response.isSuccess());
        assertEquals(itemNotFound, response.getException().getReason());
        assertEquals(404, response.getHttpCode());

    }

}
