/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk;

import com.oracle.content.sdk.model.AssetObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Encapsulates the content error structure that is returned from the sdk.
 */
@SuppressWarnings({"unused"})
public class ContentError extends AssetObject {

    @SerializedName("detail")
    @Expose
    private String detail;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("o:errorCode")
    @Expose
    private String oracleErrorCode;

    public String getDetail() {
        return detail;
    }

    public String getTitle() {
        return title;
    }

    public Integer getStatus() { return status; }

    public String getType() {
        return type;
    }

    public String getOracleErrorCode() { return oracleErrorCode; }


}