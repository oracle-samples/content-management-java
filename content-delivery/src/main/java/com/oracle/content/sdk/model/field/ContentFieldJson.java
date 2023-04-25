/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.field;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Json (embeeded object) type
 */
public class ContentFieldJson extends ContentField<String> {

    public ContentFieldJson(String json) {
        super(json, FieldType.JSON);
    }

    public JsonObject getAsJsonObject() {
        return new Gson().fromJson(value, JsonObject.class);
    }

}
