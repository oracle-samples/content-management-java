/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.oracle.content.sdk.ContentClient;

/**
 * Common abstract base class for all sdk results that return a paginated response.
 * @param <T> the type of item returned in the result
 */
@SuppressWarnings({"WeakerAccess","unused"})
public abstract class PaginatedListResult<T> extends AssetLinksObject {

    final private static String TAG = "PaginatedListResult";

    @SerializedName("hasMore")
    @Expose
    private Boolean hasMore;
    @SerializedName("offset")
    @Expose
    private Integer offset;
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("limit")
    @Expose
    private Integer limit;
    @SerializedName("totalResults")
    @Expose
    private Integer totalResults;
    @SerializedName("items")
    @Expose
    protected List<JsonElement> items = null;

    // The actual deserialized list of items
    protected List<T> deserializedItems = null;

    /**
     * Are there more pages of results?
     * @return true if more results available
     */
    public Boolean hasMore() {
        return hasMore;
    }

    /**
     * Get current offset of paginated results
     * @return offset of results
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * Get count of current results.
     * @return count of item results
     */
    public Integer getCount() {
        return count;
    }

    /**
     * Get current maximum limit of paginated result
     *
     * @return maximum limit for result
     */
    public Integer getLimit() {
        return limit;
    }


    /**
     * Get total results (not just the paginated count).
     *
     * @return Total results available from search.
     */
    public Integer getTotalResults() {
        return totalResults;
    }

    /**
     * Get the list of deserialized list of items from the response as a list
     * of {@link Asset} objects that could be either content items or digital assets.
     *
     * @return List of content base item objects
     */
    public List<T> getItems() {
        // make sure they have been deserialized
        deserializeItemFields();

        // then return
        return deserializedItems;
    }

    /**
     * @return the first item in the list of items or null if there is no item
     */
    public T first() {
        return (isEmpty()?null:getItems().get(0));
    }

    /**
     * @return true if item list is empty
     */
    public boolean isEmpty() {
        List<T> items = getItems();
        return items == null || items.isEmpty();
    }


    /**
     * Override to deserialize the list objects
     * @param jsonElement element
     * @return object deserialized
     */
    protected abstract T deserializeObject(JsonElement jsonElement);

    /**
     * Go through and deserialize each json item into the deserializedItems.  This can be overriden
     * to provide custom deserialization.
     */
    public void deserializeItemFields() {
        // only deserialize if not already done
        if (deserializedItems == null && items != null) {
            deserializedItems = new ArrayList<>(items.size());
            for(JsonElement jsonElement : items) {
                try {
                    // deserialize each object in the list
                    T object = deserializeObject(jsonElement);
                    if (object != null) {
                        deserializedItems.add(object);
                    }
                } catch (Exception e) {
                    ContentClient.log(Level.SEVERE, TAG,"Error deserializing the response");
                }
            }
        }
    }
}
