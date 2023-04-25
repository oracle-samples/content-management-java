/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.sdk.model.field;

import com.oracle.content.sdk.request.core.SearchQueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeration of the standard reserved field names that can be used in expressions, such
 * as "id", "type", "name".  These standard field names will not have a "fields."
 * prefix when used in a query (q) expression for an SDK method generated using {@link SearchQueryBuilder}
 * Note that some of these reserved field names are specific to the management SDK and not used in the delivery SDK
 * queries but are still considered reserved names.
 */
@SuppressWarnings({"unused"})
public enum FieldName {
    ID("id"),
    TYPE("type"),
    TYPE_CATEGORY("typeCategory"),
    NAME("name"),
    DESCRIPTION("description"),
    SLUG("slug"),
    TRANSLATABLE("translatable"),
    LANGUAGE("language"),
    STATUS("status"),
    PARENTID("parentId"),
    CREATED_BY("createdBy"),
    CREATED_DATE("createdDate"),
    UPDATED_BY("updatedBy"),
    UPDATED_DATE("updatedDate"),
    LINKS("links"),
    REPOSITORY_ID("repositoryId"),
    PUBLISH_INFO("publishinfo"),
    EXTERNAL_FILE("externalFile"),
    APPROVAL_STATUS("status"),
    CHANNELS("channels"),
    TAXONOMIES("taxonomies"),
    FILE_GROUP("fileGroup"),
    TAXONOMY_CATEGORY_NODES_ID("taxonomies.categories.nodes.id"),
    ALL("all");

    final private String value;

    FieldName(String value) {
        this.value = value;
    }

    // stores a list of all the name values
    public static List<String> sAllNames;

    static {
        sAllNames = new ArrayList<>();
        for (FieldName field : FieldName.values()) {
            sAllNames.add(field.value);
        }
    }

    /**
     * Does a string match a value in this enum?
     *
     * @param s string to match
     * @return true if the string matches an enum value
     */
    public static boolean isReservedFieldName(String s) {
        return sAllNames.contains(s);
    }

    @Override
    public String toString() { return this.value; }

    public String getValue() { return this.value; }

}
