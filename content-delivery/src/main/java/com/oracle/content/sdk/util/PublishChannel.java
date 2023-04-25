/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.util;

import com.oracle.content.sdk.model.date.ContentDate;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.oracle.content.sdk.model.AssetLinksObject;

/**
 * This is an abbreviated model for published channels as it's only
 * used for getting the publish channel token for a given name.
 */
public class PublishChannel extends AssetLinksObject {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("name")
    @Expose
    String name;


    @SerializedName("channelTokens")
    @Expose
    List<ChannelToken> channelTokens;

    public static class ChannelToken {
        @SerializedName("name")
        @Expose
        String name;
        @SerializedName("token")
        @Expose
        String token;
        @SerializedName("expirationDate")
        @Expose
        ContentDate expirationDate;

        public String getName() {
            return name;
        }

        public String getToken() {
            return token;
        }

        public ContentDate getExpirationDate() {
            return expirationDate;
        }

    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<ChannelToken> getChannelTokens() {
        return channelTokens;
    }

    /**
     * Gets the delivery channel token for this channel.  Assumes
     * it is the first token.
     *
     * @return token or null if there are no channel tokens
     */
    public String getDeliveryChannelToken() {
        if (channelTokens != null && channelTokens.size() > 0) {
            // assume first one
            return channelTokens.get(0).token;
        } else {
            return null;
        }
    }
}
