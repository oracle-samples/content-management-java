/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model;

import com.oracle.content.sdk.request.GetApiInfoRequest;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Class to represent Api info returns from {@link GetApiInfoRequest}
 */
@SuppressWarnings("unused")
public class ApiInfo extends AssetLinksObject {

    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("lifecycle")
    @Expose
    private String lifecycle;
    @SerializedName("isLatest")
    @Expose
    private Boolean isLatest;
    @SerializedName("catalog")
    @Expose
    private Catalog catalog;

    public static class Catalog extends AssetLinksObject {}

    /**
     * Get api version, should be "v1.1"
     * @return Api version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get status of api, should be "active"
     * @return lifecycle status
     */
    public String getLifecycle() {
        return lifecycle;
    }

    /**
     * Is this is the latest API?  Should be true
     *
     * @return true if latest
     */
    public Boolean isLatest() {
        return isLatest;
    }

    public Catalog getCatalog() {
        return catalog;
    }


}
