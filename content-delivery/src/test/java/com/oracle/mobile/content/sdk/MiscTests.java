/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.mobile.content.sdk;

import junit.framework.TestCase;

import org.junit.Assume;
import org.junit.Test;

import com.oracle.content.sdk.AuthenticationChannelTokenPolicy;
import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.ContentResponse;
import com.oracle.content.sdk.ContentSDK;
import com.oracle.content.sdk.ContentSettings;
import com.oracle.content.sdk.model.ApiInfo;
import com.oracle.content.sdk.request.GetApiInfoRequest;
import com.oracle.content.sdk.request.SearchAssetsRequest;
import com.oracle.content.sdk.util.AuthenticationBasicAuth;
import com.oracle.content.sdk.util.GetPublishChannelsRequest;
import com.oracle.content.sdk.util.PublishChannel;
import com.oracle.content.sdk.util.PublishChannelList;
import com.oracle.content.sdk.util.TestUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Misc. testings most of which are "live" tests
 */
@SuppressWarnings("unchecked")
public class MiscTests extends SDKBaseTest {

    // get channel token
    @Test
    public void testGetChannelToken() {
        // live-test only as we want the actual token value
        Assume.assumeTrue(testMode == Config.MODE.LIVE_TEST);

        String channelToken = TestUtil.getChannelToken(
                serverUrl,
                Config.USER_NAME,
                Config.PASSWORD,
                Config.CHANNEL_NAME);

        assertNotNull(channelToken);

        System.out.println(">>>>>> CHANNEL TOKEN = " + channelToken);

    }

    // tests the utii method to get list of channels
    @Test
    public void testChannelListCall() {
        Assume.assumeTrue(testMode == Config.MODE.LIVE_TEST);

        // create client authenticated using basic auth
        ContentDeliveryClient client = ContentSDK.createDeliveryClient(
                serverUrl,
                new AuthenticationBasicAuth(Config.USER_NAME, Config.PASSWORD),
                new ContentSettings()
        );


        // get list of channels
        GetPublishChannelsRequest request =
                new GetPublishChannelsRequest(client)
                        .fieldsAll()
                        .linksNone();

        ContentResponse<PublishChannelList> response = makeSDKReqeust(request);
        assertNotNull(response);
        assertTrue(response.isSuccess());

        PublishChannelList channelList = response.getResult();

        assertNotNull(channelList);
        assertTrue(channelList.getItems().size() > 0);

        for (PublishChannel channel : channelList.getItems()) {
            assertNotNull(channel);
            assertNotNull(channel.getName());
            assertNotNull(channel.getId());
            assertNotNull(channel.getChannelTokens());
            assertTrue(channel.getChannelTokens().size() > 0);
            System.out.println(">>> " + channel.getName());
            for (PublishChannel.ChannelToken token : channel.getChannelTokens()) {
                assertNotNull(token.getName());
                assertNotNull(token.getToken());
                assertNotNull(token.getExpirationDate());
                System.out.println("    " + token.getName() + " >>> " + token.getToken());
            }
        }

    }


    @Test
    public void testGetApiInfo() {
        GetApiInfoRequest request = new GetApiInfoRequest(clientAPI);
        ApiInfo info = (ApiInfo)makeSDKReqeust(request).getResult();
        assertNotNull(info);
        assertEquals("v1.1", info.getVersion());
        assertEquals("active", info.getLifecycle());
        TestCase.assertTrue(info.isLatest());
        assertNotNull(info.getCatalog());
        assertNotNull(info.getCatalog().getLinks());
        assertNotNull(info.getLinks());
    }

    // tests that user agent header can be set
    @Test
    public void testUserAgentHeader() {
        Assume.assumeTrue(testMode == Config.MODE.LIVE_TEST);

        final String USER_AGENT = "SDKUnitTest/1.0";

        // create client authenticated using basic auth
        ContentDeliveryClient client = ContentSDK.createDeliveryClient(
                serverUrl,
                new AuthenticationChannelTokenPolicy(channelToken),
                new ContentSettings().setUserAgentHeader(USER_AGENT)
        );

        // get list of ContentItems  based on the type
        ContentResponse response = makeSDKReqeust(
                new SearchAssetsRequest(client).type(SearchContentItemsMenuTests.MENU_ITEM_TYPE)
        );

        assertTrue(response.isSuccess());
        // nothing to verify as User-Agent not returned in the response
    }

}
