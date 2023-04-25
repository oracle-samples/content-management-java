/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.request.core;

import java.util.List;

import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.model.AssetObject;
import com.oracle.content.sdk.request.SearchAssetsRequest;


/**
 * Base request for any paginated list request, such as {@link SearchAssetsRequest}
 */
@SuppressWarnings({"unused","WeakerAccess"})
public abstract class PaginatedListRequest<T extends ContentRequest, C extends AssetObject> extends ContentRequest<T, C> {

    static final String ALL_FIELDS = "all";

    // maximum number of items to return (will be a number if specified)
    protected Integer limit = null;

    // offset for items to return (will be a number if specified)
    protected Integer offset = null;

    // sort field
    protected String sortByField = null;

    // to sort descending
    protected Boolean sortOrderDescending = null;

    // to include total count in results
    protected Boolean includeTotalCount = null;

    // "fields" parameter for SDK request
    protected String fields = ALL_FIELDS;

    // expand field (default to null, not used in all requests)
    protected String expand = null;



    /**
     * Construct request to search for content items.
     *
     * @param client Valid delivery client
     * @param objectClass class for result object to deserialize
     */
    public PaginatedListRequest(ContentDeliveryClient client, Class objectClass) {
        super(client, objectClass);
    }




    /**
     * Maximum number of items that can come from the request.
     *
     * @param limit number of items to return
     * @return this
     */
    public T limit(int limit) {
        this.limit = limit;
        return getThis();
    }

    /**
     * Accepts a Boolean value. Setting it to true displays the total results field in the response
     * The default is false.
     *
     * @param includeTotalCount true to include total result count in query
     * @return this
     */
    public T totalResults(boolean includeTotalCount) {
        this.includeTotalCount = includeTotalCount;
        return getThis();
    }

    /**
     * Starting offset to request items from
     *
     * @param offset Starting offset to request items
     * @return this
     */
    public T offset(int offset) {
        this.offset = offset;
        return getThis();
    }

    /**
     * The server will sort the results based on the field which can either by "name"
     * or one of the custom data fields on the content item.
     *
     * @param sortField The string field to use for sorting on the server
     * @return this
     */
    public T sortByField(String sortField) {
        this.sortByField = sortField;
        return getThis();
    }

    /**
     * Sort order, specified true for descending or false for
     * ascending order.  Use in conjunction with sortByField.
     * Default sort order if not specified is ascending.
     *
     * @param descending set to true to set sort order to descending
     * @return this
     */
    public T sortOrderDescending(boolean descending) {
        this.sortOrderDescending = descending;
        return getThis();
    }

    /**
     * Restrict the results to fields that match.  This list can be a comma
     * separated list of fields.  It is recommended to use {@link #fields(List)} to
     * generate this list as it will prefix non-reserved field names with "fields."
     * Note that "id" is always returned.
     *
     * @param fields Specify the fields to return in the query
     * @return this
     */
    public T fields(String fields) {
        this.fields = fields;
        return getThis();
    }

    /**
     * Will generate the comma-delimited list of fields based on a list of string field values.
     *
     * @param fieldList list of fields
     * @return this
     */
    public T fields(List<String> fieldList) {
        this.fields = SearchQueryBuilder.getFieldList(fieldList);
        return getThis();
    }

    /**
     * Same as a call to {@link #fields} specifying "HTTP", so all fields will be returned
     * in search results.
     *
     * @return this
     */
    public T fieldsAll() {
        this.fields = ALL_FIELDS;
        return getThis();
    }

    /**
     * Specify whether to expand fields.  Can be a value
     * such as "fields.field_name" to expand a specific item reference.
     * Note that by default this is not set.  See also {@link #expandAll()}
     *
     * @param field expand field value (e.g. "all") or null to not expand references
     * @return Builder object
     */
    public T expand(String field) {
        this.expand = SearchQueryBuilder.getFieldName(field);
        return getThis();
    }

    /**
     * Specifies a list of fields to expand during the request.
     *
     * @param expandFields list of string fields to expand
     * @return Builder object
     */
    public T expand(List<String> expandFields) {
        this.expand = SearchQueryBuilder.getFieldList(expandFields);
        return getThis();
    }

    /**
     * Just like calling {@link #expand} with "all" as the parameter.  Will expand all item reference fields.
     * @return Builder object.
     */
    public T expandAll() {
        this.expand = ALL_FIELDS;
        return getThis();
    }

    //Helper to get orderBy parameter of form field.name:des
    protected String getOrderByParam(String sortByField) {
        if (sortByField == null)
            return null;

        // first get tht field
        String field = SearchQueryBuilder.getFieldName(sortByField);
        // if descending is set, add ":des";
        if (sortOrderDescending != null && sortOrderDescending) {
            field += ":des";
        }
        return field;

    }


}

