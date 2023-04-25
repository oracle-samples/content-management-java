/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Generic handler for "items"
 * @param <T> type
 */
public class ItemList<T> extends AssetObject {

    @SerializedName("items")
    @Expose
    private List<T> items = null;

    public List<T> getItems() {
        return items;
    }

    /**
     * Get first item in the list or null if empty
     * @return first item in the list
     */
    public T getFirstItem() {
        if (items != null && items.size() > 0) {
            return items.get(0);
        } else {
            return null;
        }
    }

}