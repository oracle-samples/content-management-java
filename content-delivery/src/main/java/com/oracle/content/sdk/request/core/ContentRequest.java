/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.request.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.MalformedJsonException;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.core.Single;

import com.oracle.content.sdk.ContentCallback;
import com.oracle.content.sdk.ContentClient;
import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.ContentErrorString;
import com.oracle.content.sdk.ContentException;
import com.oracle.content.sdk.ContentResponse;
import com.oracle.content.sdk.model.AssetObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

/**
 * Base class for SDK requests, including methods commons to all request such as the ability
 * to disable the cache for a single call {@link #noCache()} or specify common options
 * such as {@link #linksNone()}.
 *
 * <p> There are various methods used to make the SDK call as well as the code to deserialize the response
 * into a result object.  See the various methods for initiating the request such as
 * {@link #fetch()}, {@link #fetchAsync(ContentCallback)}, and {@link #observable()}
 *
 * @param <T> The Request class (e.g. SearchContentItemsRequest)
 * @param <C> The result object to deserialize (e.g. ContentSearchResult)
 */
@SuppressWarnings({"unused,WeakerAccess"})
public abstract class ContentRequest<T extends ContentRequest, C extends AssetObject> {

    // Class type to use for deserialization, must match the generic type C
    final protected Class objectClass;

    // delivery client for requests
    final protected ContentDeliveryClient client;

    // value for no links
    static final String NO_LINKS = "none";

    // set boolean to true to disable cache for a single call
    private boolean noCache = false;

    // "links" parameter for SDK request
    protected String links = null;

    protected ContentRequest(
            @NotNull ContentDeliveryClient client,
            @NotNull Class objectClass
    ) {
        this.objectClass = objectClass;
        this.client = client;

    }

    // override to do custom deserialization
    @SuppressWarnings("unchecked")
    protected C deserializeObject(JsonElement jsonElement) {
        return (C) ContentClient.gson().fromJson(jsonElement, objectClass);
    }



    /**
     * Get the retrofit call to make for this request..
     *
     * @return return the retrofit call object to use for this request
     */
    public abstract Call<JsonElement> getCall();




    /**
     * This is a synchronous method to make the SDK request but differs from the
     * method {@link #fetch()} as it will return the expected object directly
     * instead of the containing {@link ContentResponse} object.  If an error occurs
     * a {@link ContentException} will be thrown.  You can use this method if you
     * just want the object result directly and prefer to have a try/catch block to
     * handle any errors.
     * Example to get a content item:
     * <pre>
     *    GetContentItemRequest request = new GetContentItemRequest(deliveryClient, itemID);
     *    try {
     *       ContentItem result = request.fetchResult();
     *    } catch (ContentException e) {
     *      // handle error
     *    }
     * </pre>
     *
     * @return The result object from the response.
     *
     * @throws ContentException if there is an error
     */
    public C fetchResult() throws ContentException {
        ContentResponse<C> response = fetch();
        if (response.isSuccess()) {
            return response.getResult();
        } else {
            throw response.getException();
        }
    }

    /**
     * Transform the retrofit response into a ContentResponse, de-serializing to the
     * appropriate object.
     *
     * @param restResponse retrofit response to get json from
     * @return content response with de-serialized result object
     */
    private ContentResponse<C> transformResponse(Response<JsonElement> restResponse) {

        // constructing the response will parse errors returned by SDK
        ContentResponse<C> response = new ContentResponse<>(restResponse);
        try {
            if (response.isSuccess()) {
                // deserialize the response into the object
                response.setResult(deserializeObject(response.getAsJson()));
            } else {
                ContentClient.log("[ContentRequest]", "response not successful");
            }
        } catch (ContentException ce) {
            throw ce;
        } catch (Exception e) {
            // unknown error during deserialization
            response.setException(new ContentException(
                    ContentException.REASON.dataConversionFailed,
                    ContentErrorString.DESERIALIZATION_ERROR + objectClass.getName(),
                    e));
        }
        return response;
    }

    /**
     * This is a synchronous method to make the SDK request that will return with a
     * {@link ContentResponse} object.  This method will not throw an exception but
     * if there is an exception it will be contained in the response.  Use this method
     * if you want the full {@link ContentResponse} object or prefer a method that
     * does not throw exceptions.  See also {@link #fetchResult()}
     * Example to get a content item:
     * <pre>
     *    GetContentItemRequest request = new GetContentItemRequest(deliveryClient, itemID);
     *
     *    ContentResponse response = request.fetch();
     *    if (response.isSuccess()) {
     *      ContentItem result = response.getResult()
     *    } else {
     *      // handle error
     *    }
     * </pre>
     *
     * @return returns the {@link ContentResponse} after completion of the call
     */
    public ContentResponse<C> fetch() {

        // synchronous version of the call
        try {
            // setup the call
            Call<JsonElement> call = getCall();
            // execute the REST call synchronously and wait for the response.
            return transformResponse(call.execute());
        } catch (ContentException ce) {
            throw ce;
        } catch (Exception e) {
            ContentException.REASON reason = ContentException.REASON.generalError;
            if (e instanceof MalformedJsonException) {
                reason = ContentException.REASON.dataConversionFailed;
            }
            ContentException contentException = ContentClient.getContentException(reason,
                    e, ContentErrorString.SDK_RESPONSE_ERROR, null);
            return new ContentResponse<>(contentException);
        }
    }


    /**
     * This makes an asynchronous SDK request on a separate thread and will return the
     * resulting {@link ContentResponse} in a {@link ContentCallback} method.
     * Example to get a content item:
     * <pre>{@code
     *
     *   GetContentItemRequest request = new GetContentItemRequest(clientAPI, itemID);
     *
     *   request.fetchAsync(response -> {
     *       if (response.isSuccess()) {
     *          ContentItem item = response.getResult();
     *       } else {
     *          // handle error
     *       }
     *
     *      });
     * }</pre>
     *
     * @param callback The callback method to call after completion of the call.
     */
    public void fetchAsync(ContentCallback<C> callback) {

        // get the REST call we'll make
        Call<JsonElement> call = getCall();

        // make the REST call now via retrofit
        call.enqueue(new Callback<JsonElement>() {

            // retrofit callback when REST call is successful
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                callback.onResponse(transformResponse(response));
            }

            // retrofit callback when REST call failed
            @Override
            @EverythingIsNonNull
            public void onFailure(Call<JsonElement> call, Throwable t) {
                ContentException exception = ContentClient.getContentException(
                        ContentException.REASON.networkError,
                        t.getCause() != null ? t.getCause() : t,
                        ContentErrorString.SDK_RESPONSE_ERROR,
                        null);
                // callback with error response
                callback.onResponse(new ContentResponse<>(exception));
            }
        });
    }

    /**
     * Create an RxJava Single observable object which can then be subscribed on to get the result object as part
     * of the response.  If the full {@link ContentResponse} object is needed, use {@link #observable()}}.
     * Example code to make a blocking (synchronous) call to get a content item:
     *
     * <pre>{@code
     *     GetContentItemRequest request = new GetContentItemRequest(clientAPI, itemID);
     *     ContentItem item = request.observableResult().blockingGet();
     * }</pre>
     *
     * @return RxJava observable object to subscribe to for the result object
     */
    public Single<C> observableResult() {
        return Single.create((emitter) -> {
            // synchronous call to SDK
            ContentResponse<C> response = fetch();

            if (response.isSuccess()) {
                // call success with the result object
                emitter.onSuccess(response.getResult());
            } else {
                // error from the response
                emitter.onError(response.getException());
            }
        });

    }


    /**
     * Create an RxJava Single observable object which can then be subscribed to to get a {@link ContentResponse}.
     * If only the result object is desired, the {@link #observableResult()} method can be used instead
     * to directly observe the result of the object.
     *
     * Example code to make an asynchronous call to get a content item:
     *
     * <pre>{@code
     *     GetContentItemRequest request = new GetContentItemRequest(clientAPI, itemID);
     *
     *     request.observable().
     *          .subscribeOn(Schedulers.io())
     *          .observeOn(AndroidSchedulers.mainThread())
     *          .subscribe(
     *              response -> {
     *                  // valid response, get content item in result
     *                  ContentItem item = response.getResult();
     *              },
     *              error -> {
     *                 // handle error
     *
     *              }
     *              );
    *
     * }</pre>
     *
     * @return RxJava observable object to subscribe to for the {@link ContentResponse}
     */
    public Single<ContentResponse<C>> observable() {
        return Single.create((emitter) -> {
            // synchronous call to SDK
            ContentResponse<C> response = fetch();

            if (response.isSuccess() || (response.getException() != null && response.getException().getContentError() != null)) {
                // call success with the response
                emitter.onSuccess(response);
            } else {
                // error from the response
                emitter.onError(response.getException());
            }
        });
    }



    /**
     * Override to return this.  Solution for the unchecked cast warning.
     *
     * @return this
     **/
    @SuppressWarnings("unchecked")
    protected T getThis() { return (T)this; }

    /**
     * Optional override of cache policy which will force the call to get the data
     * from the network and not use the cache.
     *
     * @return this
     */
    @SuppressWarnings("UnusedReturnValue")
    public T noCache() {
        this.noCache = true;
        return getThis();
    }


    /**
     * Cache-control string to use (will be set to no-cache if noCache is set)
     *
     * @return cache-control header string
     */
    protected String getCacheControl() {
        return noCache ? ContentClient.NO_CACHE : null;
    }


    /**
     * Specify which links should be returned in result.  If you don't care about the "links", just
     * use {@link #linksNone()}
     *
     * @param links A comma delimited list of links to include.
     * @return this
     */
    public T links(String links) {
        this.links = links;
        return getThis();
    }

    /**
     * Same as a call to {@link #links} specifying "none", so no links will be returned
     *
     * @return this
     */
    public T linksNone() {
        this.links = NO_LINKS;
        return getThis();
    }

    /**
     * The gson object to use for deserialization
     * @return gson instance
     */
    protected static Gson gson() {
        //  use the client gson instance
        return ContentClient.gson();
    }

}
