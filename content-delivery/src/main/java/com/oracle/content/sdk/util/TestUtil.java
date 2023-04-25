/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.util;

import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.ContentSDK;
import com.oracle.content.sdk.ContentSettings;

/**
 * Used internally for test purposes and may change in the future.
 */
public class TestUtil {

    /**
     * Uses an authenticated management SDK call to get the channel token
     * for a given channel name on a server.  <b>This is for test purposes only
     * and may change in future releases.</b>
     *
     * @param serverUrl valid server
     * @param userName basic auth user
     * @param userPassword basic auth password
     * @param channelName channel name to find token for
     * @return channel token if found or null
     */
    public static String getChannelToken(
            String serverUrl,
            String userName,
            String userPassword,
            String channelName) {

        String channelToken = null;

        // create client authenticated using basic auth
        ContentDeliveryClient client = ContentSDK.createDeliveryClient(
                serverUrl,
                new AuthenticationBasicAuth(userName, userPassword),
                new ContentSettings()
        );


        // get list of channels
        GetPublishChannelsRequest request =
                new GetPublishChannelsRequest(client)
                .fieldsAll()
                .linksNone();

        PublishChannelList channelList = request.fetchResult();

        // if we got a proper match based on the name we should
        // have just one result
        if (channelList != null && channelList.getItems().size() > 0) {
            for (PublishChannel channel : channelList.getItems()) {
                // find matching channel
                if (channelName.equals(channel.name)) {
                    // extract token
                    channelToken = channel.getDeliveryChannelToken();
                    break;
                }
            }
        }

        return channelToken;
    }
}
