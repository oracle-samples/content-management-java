/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.mobile.content.sdk;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.oracle.content.sdk.ContentException;
import com.oracle.content.sdk.ContentResponse;
import com.oracle.content.sdk.model.Asset;
import com.oracle.content.sdk.request.GetContentItemRequest;
import com.oracle.content.sdk.request.core.ContentRequest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static com.oracle.content.sdk.ContentException.REASON.itemNotFound;
import static org.junit.Assert.fail;

/**
 * Live tests for the various methods that can be used for making "requests",
 * including asychronous calls.
 */
public class AsyncRequestTests extends SDKSingleItemTest {

    private Asset itemResult = null;
    private ContentResponse responseResult = null;

    // override fetch to make asynch call
    @Override
    protected ContentResponse fetchRequest(ContentRequest request) {
        // will need to wait for asynchronous call to complete
        CountDownLatch countDownLatch = new CountDownLatch(1);

        request.fetchAsync(response -> {
            responseResult =response;
            if (response.isSuccess()) {
                assertTrue(response.getResult() instanceof Asset);
                itemResult = (Asset) response.getResult();
            }
            countDownLatch.countDown();
        });

        try {
            // wait for up to 5 seconds
            countDownLatch.await(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            fail("Exception:" + e);
        }

        return responseResult;

    }

    // test the asynch version of the call
    @Test
    public void testAsynchCall() throws Exception {

        // make call to get the item by id
        GetContentItemRequest request = new GetContentItemRequest(clientAPI, itemID);
        ContentResponse response = makeSDKReqeust(request);

        assertTrue(response.isSuccess());
        assertNotNull(itemResult);
        assertEquals("sdktest", itemResult.getName());
    }

    // test the asynch version of the call with error
    @Test
    public void testAsynchCallWithError() throws Exception {

        // make call to get the item by id
        GetContentItemRequest request = new GetContentItemRequest(clientAPI, itemID+"3");
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

}
