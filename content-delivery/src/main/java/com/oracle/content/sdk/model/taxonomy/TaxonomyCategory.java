/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.taxonomy;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.oracle.content.sdk.model.AssetLinksObject;
import com.oracle.content.sdk.model.AssetObject;

/**
 * Category model within a Taxonomy.
 */
@SuppressWarnings("unused")
public class TaxonomyCategory extends AssetLinksObject {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("nodes")
    @Expose
    private List<Node> nodes = null;

    public static class Node extends AssetObject {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Node> getNodes() {
        return nodes;
    }

}
