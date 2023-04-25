/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.digital;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.oracle.content.sdk.model.AssetObject;

/**
 * Part of {@link AdvancedVideoInfo}
 */
public class AdvancedVideoInfoProperties extends AssetObject {

    public static class IdObject extends AssetObject {
        @SerializedName("id")
        @Expose
        private String id;

        public String getId() {
            return id;
        }
    }


    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("videoStripProperties")
    @Expose
    private String videoStripProperties;
    @SerializedName("extension")
    @Expose
    private String extension;
    @SerializedName("searchText")
    @Expose
    private String searchText;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("entryId")
    @Expose
    private String entryId;
    @SerializedName("endpoint")
    @Expose
    private String endpoint;
    @SerializedName("partner")
    @Expose
    private IdObject partner;
    @SerializedName("player")
    @Expose
    private IdObject player;

    // set from a separate call in GetAdvancedVideoAsset
    private String videoToken;

    public Integer getDuration() {
        return duration;
    }

    public String getVideoStripProperties() {
        return videoStripProperties;
    }

    public String getExtension() {
        return extension;
    }

    public String getSearchText() {
        return searchText;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public boolean isReady() {
        return "READY".equals(status);
    }

    public String getEntryId() {
        return entryId;
    }

    public String getEndpoint() { return endpoint; }

    public String getPlayerId() {
        return player!=null?player.id:null;
    }

    public String getPartnerId() {
        return partner!=null?partner.id:null;
    }

    public String getVideoToken() {
        return videoToken;
    }

    public void setVideoToken(String token) {
        this.videoToken = token;
    }
}
