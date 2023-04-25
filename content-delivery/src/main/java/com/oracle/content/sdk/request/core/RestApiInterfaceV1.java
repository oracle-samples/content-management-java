/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.request.core;


import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * The interface used for defining the retrofit endpoints for all SDK REST API calls. - V1.1
 */
public interface RestApiInterfaceV1 {


    // digital asset pathName API to use when manually creating asset urls
    String DIGITAL_ASSET_PATH = "content/published/api/v1.1/assets";

    /***
     * REST call to get content item from delivery SDK.
     *
     * @param ID  Id of the content item.
     * @param links links value (comma-delimited list)
     * @param expand String value of expand parameter (e.g. "expand=all")
     * @param cacheControl Override cache-control heading
     * @return retrofit call object
     */
    @GET("content/published/api/v1.1/items/{ID}")
    Call<JsonElement> getContentItem(
            @Path("ID") String ID,
            @Query("links") String links,
            @Query("expand") String expand,
            @Header("Cache-Control") String cacheControl);

    /***
     * REST call to get content item from delivery SDK using a "slug".
     *
     * @param slugId  slug id of the content item.
     * @param links links value (comma-delimited list)
     * @param expand String value of expand parameter (e.g. "expand=all")
     * @param cacheControl Override cache-control heading
     * @return retrofit call object
     */
    @GET("content/published/api/v1.1/items/.by.slug/{slugId}")
    Call<JsonElement> getContentItemBySlug(
            @Path("slugId") String slugId,
            @Query("links") String links,
            @Query("expand") String expand,
            @Header("Cache-Control") String cacheControl);

    /***
     * REST call to get item language variations from delivery SDK.
     *
     * @param ID  Id of the content item.
     * @param links links value (comma-delimited list)
     * @return retrofit call object
     */
    @GET("content/published/api/v1.1/items/{ID}/variations/language")
    Call<JsonElement> getItemLanguageVariations(
            @Path("ID") String ID,
            @Query("links") String links);

    /***
     * REST call to get item language variations from delivery SDK.
     *
     * @param slugId  slug id of the content item.
     * @param links links value (comma-delimited list)
     * @return retrofit call object
     */
    @GET("content/published/api/v1.1/items/.by.slug/{slugId}/variations/language")
    Call<JsonElement> getItemLanguageVariationsBySlug(
            @Path("slugId") String slugId,
            @Query("links") String links);


    /**
     * REST call to search for content item based on a SCIM query string.
     *
     * @param query query string (e.g. "type cq 'mytype'")
     * @param fields specific fields to retrieve
     * @param links links value (comma-delimited list)
     * @param defaultQuery default query string
     * @param limit maximum number of items to request
     * @param offset offset to start getting items from
     * @param orderBy field to order results by
     * @param totalResults true to get the total count of items
     * @param cacheControl Override cache-control heading
     * @return retrofit call object
     */
    @GET("content/published/api/v1.1/items")
    Call<JsonElement> searchBySCIMQuery(
            @Query("q") String query,
            @Query("fields") String fields,
            @Query("links") String links,
            @Query("default") String defaultQuery,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("orderBy") String orderBy,
            @Query("totalResults") Boolean totalResults,
            @Header("Cache-Control") String cacheControl);


    // REST call to get list of taxonomies
    @GET("/content/published/api/v1.1/taxonomies")
    Call<JsonElement> getTaxonomies(
            @Query("expand") String expand,
            @Query("links") String links,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("totalResults") Boolean totalResults);

    // REST call to get list of taxonomy categories
    @GET("/content/published/api/v1.1/taxonomies/{ID}/categories")
    Call<JsonElement> getTaxonomyCategories(
            @Path("ID") String ID,
            @Query("expand") String expand,
            @Query("links") String links,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("totalResults") Boolean totalResults);


    /***
     * REST call to get API info.
     *
     * @return retrofit call object
     */
    @GET("content/published/api/v1.1")
    Call<JsonElement> getApiInfo();

     // management SDK call used to get list of publish channels.
     // This is only for internal testing and requires authentication.
    @Headers("X-Requested-With: XMLHttpRequest")
    @GET("/content/management/api/v1.1/channels")
    Call<JsonElement> getPublishChannels(
            @Query("fields") String fields,
            @Query("links") String links,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("totalResults") Boolean totalResults);


}
