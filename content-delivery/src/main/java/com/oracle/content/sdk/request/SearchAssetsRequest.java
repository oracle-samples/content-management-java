/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.request;

import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.model.AssetSearchResult;
import com.oracle.content.sdk.model.field.FieldName;
import com.oracle.content.sdk.request.core.PaginatedListRequest;
import com.oracle.content.sdk.request.core.SearchQueryBuilder;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;


/**
 * Request class used to search for a content item request based on specific search criteria.
 * Below is an example that searches for items up to 'searchLimit' number of items that match
 * the content type 'searchType' and sorts the results by the 'name' field.
 * <pre>   {@code
 *
 * // this shows how to build a search request
 * SearchAssetsRequest searchRequest = new SearchAssetsRequest(deliveryClient)
 *        .limit(20)                 //  maximum results to return
 *        .sortByField("name")       // field used to sort results
 *        .type("mycontenttype");    // content type to search for
 * }</pre>
 *
 */
@SuppressWarnings({"WeakerAccess","unused"})
public class SearchAssetsRequest extends PaginatedListRequest<SearchAssetsRequest, AssetSearchResult> {


    // search filter string
    protected String filter = null;

    // list of filter criteria to put together in the filter string prior to SDK Call
    private List<String> filterList = new ArrayList<>();

    // "default" query string
    private String defaultQuery = null;

    /**
     * Construct request to search for content items.
     *
     * @param client Valid delivery client
     */
    public SearchAssetsRequest(ContentDeliveryClient client) {
        super(client, AssetSearchResult.class);
    }

    /**
     * Specify the search filter to apply when searching for items with a SCIM string.
     * For example: {@code type eq "Blog"} would only return items
     * that match a type of "Blog".  In general the most common methods
     * for filtering should be provided by methods such as {@link #type}, etc.
     * But if a query filter is not provided you can pass in your own as a string.
     * It is recommended to use the helper
     * builder {@link SearchQueryBuilder} to construct the string.
     *
     * @param filter Search criteria filter
     * @return this
     */
    public SearchAssetsRequest filter(String filter) {
        filterList.add(filter);
        return getThis();
    }

    /**
     * Forms a simple filter that searches by type that matches the "type" parameter.
     *
     * @param type TypeName to match
     * @return this
     */
    public SearchAssetsRequest type(String type) {
        filterList.add( new SearchQueryBuilder(type).build());
        return getThis();
    }

    /**
     * The "default" query string specified for the REST call
     *
     * @param defaultQuery default query string
     * @return this
     */
    public SearchAssetsRequest defaultQuery(String defaultQuery) {
        this.defaultQuery = defaultQuery;
        return getThis();
    }


    /**
     * Filter results to list of content types.
     *
     * @param contentTypeList list of content type names to filter by
     * @return this
     */
    public SearchAssetsRequest typeList(List<String> contentTypeList) {
        return fieldMatchesList(FieldName.TYPE.getValue(), contentTypeList);
    }

    /**
     * Filter results to list of specific ids
     *
     * @param idList list of ids to filter by
     * @return this
     */
    public SearchAssetsRequest idList(List<String> idList) {
        return fieldMatchesList(FieldName.ID.getValue(), idList);
    }

    /**
     * Filter results to list of specific names
     *
     * @param nameList list of names to filter by
     * @return this
     */
    public SearchAssetsRequest nameList(List<String> nameList) {
        return fieldMatchesList(FieldName.NAME.getValue(), nameList);
    }

    /**
     * Add filter criteria for field and value list.  For example, orFieldList("name", {"a","b","c"})
     * would add a filter in the form of (a OR b OR c)
     *
     * @param fieldName field name to match
     * @param list list of values to use
     * @return this
     */
    public SearchAssetsRequest fieldMatchesList(String fieldName, List<String> list) {
        // construct the search filter type1 OR type2, etc.
        String listFilter = SearchQueryBuilder.matchIdList(fieldName, list, true);
        // add filter to list
        filterList.add(listFilter);
        return getThis();
    }

    /**
     * Clear all filters.
     *
     * @return this
     */
    public SearchAssetsRequest clearFilter() {
        filterList = new ArrayList<>();
        return getThis();
    }

    /**
     * Filter results to specified item name
     *
     * @param name Name to search for
     * @return this
     */
    public SearchAssetsRequest name(String name) {
        return fieldEquals(FieldName.NAME.getValue(), name);
    }

    /**
     * Filter results to specified field equal to the value
     *
     * @param fieldName Field name to test
     * @param fieldValue Field value expected
     * @return this
     */
    public SearchAssetsRequest fieldEquals(String fieldName, String fieldValue) {
        String searchFilter =
                new SearchQueryBuilder().
                        startExpression(fieldName,
                                SearchQueryBuilder.QueryOperator.EQUALS, fieldValue).build();

        filterList.add( searchFilter );
        return getThis();
    }

    /**
     * Filter results to specified language
     *
     * @param language Language (e.g. "fr", "en-US") filter
     * @return this
     */
    public SearchAssetsRequest language(String language) {
        String searchFilter =
                new SearchQueryBuilder().
                        startExpression(FieldName.LANGUAGE.getValue(),
                                SearchQueryBuilder.QueryOperator.EQUALS, language).build();

        filterList.add( searchFilter );
        return getThis();
    }

    /**
     * Filter results to list of taxonomy category node ids.
     *
     * @param nodeIdList list of taxonomy category node ids
     * @return this
     */
    public SearchAssetsRequest taxonomyCategoryNodeIds(List<String> nodeIdList) {
        // construct the search filter node1 OR node2, etc.
        String nodeIdFilter = SearchQueryBuilder.matchIdList(FieldName.TAXONOMY_CATEGORY_NODES_ID.getValue(), nodeIdList, true);
        // add filter to list
        filterList.add(nodeIdFilter);
        return getThis();
    }

    /**
     * Filter results to a specific category node id.
     *
     * @param nodeId specific category node to match
     * @return this
     */
    public SearchAssetsRequest taxonomyCategoryNodeId(String nodeId) {
        return taxonomyCategoryNodeIds(Collections.singletonList(nodeId));
    }


    /**
     * Construct filter strings from filterLIst
     *
     * @return filter string to use in SDK Call
     */
    private String constructFilter() {
        String filter = null;
        if (filterList.size() > 0) {
            SearchQueryBuilder builder = new SearchQueryBuilder();
            // start expression with first filter
            builder .startExpression(filterList.get(0));
            // then iterate through all filters 1+
            for(String expression : filterList.subList(1, filterList.size())) {
                builder.addExpression(expression);
            }
            filter = builder.build();
        }
        return  filter;

    }

    /**
     * Get retrofit call to use for search request
     */
    @Override
    public Call<JsonElement> getCall() {

        return client.getApi().searchBySCIMQuery(
                constructFilter(),
                fields,
                links,
                defaultQuery,
                limit,
                offset,
                getOrderByParam(sortByField),
                includeTotalCount,
                getCacheControl());

    }
}

