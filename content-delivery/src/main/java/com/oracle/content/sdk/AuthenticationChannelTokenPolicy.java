/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.sdk;

import okhttp3.HttpUrl;

/**
 * Authentication policy for published content, which requires a channel token
 * associated with the published channel that was used to publish the data
 * that the delivery SDK will request.
 **/
public class AuthenticationChannelTokenPolicy extends AuthenticationPolicy {

    // channel token parameter name
    private static final String CHANNEL_TOKEN_PARAM = "channelToken";

    // stored channel token value
    private final String channelToken;

    /**
     * Construct the authentication policy using the specified channel token.
     *
     * @param channelToken The channel token value for the published channel
     */
    public AuthenticationChannelTokenPolicy(String channelToken) {
        this.channelToken = channelToken;
    }

    /**
     * The published channel policy overrides this method to add the query
     * parameter channelToken=[value] to each request.
     *
     * @param builder Builder class to add query parameters
     */
    @Override
    public void addQueryParameters(HttpUrl.Builder builder) {
        // add channel token as query parameter
        builder.addQueryParameter(CHANNEL_TOKEN_PARAM, channelToken);
    }


}
