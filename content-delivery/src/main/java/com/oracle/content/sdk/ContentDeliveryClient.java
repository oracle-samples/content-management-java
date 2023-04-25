/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk;

import com.oracle.content.sdk.model.digital.DigitalAsset;
import com.oracle.content.sdk.model.digital.RenditionType;
import com.oracle.content.sdk.request.core.ContentRequest;
import com.oracle.content.sdk.request.core.RestApiInterfaceV1;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import okhttp3.HttpUrl;

/**
 * This the core class to use for making delivery SDK calls.  It must be created by calling
 * {@link ContentSDK#createDeliveryClient}.
 *
 * <h3>Example for creating a {@code ContentDeliveryClient}</h3>
 *
 * It is best to create the delivery client object once and reuse it in your app.  The
 * only reason you would need more than one delivery client is if you were making calls
 * to multiple servers.  To change any of the settings, it is necessary to re-create a new
 * content delivery client.
 *
 * <pre>   {@code
 *
 * // The simplest way to create a delivery client is to pass
 * // in a server url and channel token like this:
 *
 * // create client API we'll use to make SDK calls
 * contentDeliveryClient = ContentSDK.createDeliveryClient(
 *      serverUrl,          // server url that will service sdk requests
 *      channelToken,      // channel token (required)
 * );
 *
 * // To enable the cache (recommended), provide a cache dir as an additional parameter)
 *
 * } * contentDeliveryClient = ContentSDK.createDeliveryClient(
 *  *      serverUrl,            // server url that will service sdk requests
 *  *      channelToken,         // channel token (required)
 *  *      context.getHttpCacheDir() // enables all caches
 *  * );</pre>
 *
 * <h3>Build search request and make SDK Call</h3>
 *
 * This example would make a call to search for items based on a type string<pre>.
 * See {@link ContentRequest} for the various
 * types of calls available including synchronous, async, and observables using RxJava.
 *
 * // this shows how to build a search request
 * SearchContentItemsRequest searchRequest = new SearchContentItemsRequest(deliveryClient)
 *        .type(searchType);   // search for items matching this type
 *
 * // make an asynchronous SDK call to get a list of content items based on the criteria
 * searchRequest.fetchAsync(::searchCallback);
 *
 * // another alternative that makes a synchronous version of the same call
 * searchCallback( searchRequest.fetch() );
 * }</pre>
 *
 * <h3>Handle the callback response</h3>
 *
 * <p>The callback method specified in will be called when the SDK operation is complete
 * or an error occurred.
 *
 * </p>Here is an example of a callback method to handle the search call from above: {@code
 *
 * void searchCallback(ContentResponse<ContentSearchResult> response) {
 *
 *   // if there was an error, handle that exception
 *   if (!response.isSuccess()) {
 *     handleContentException(response.getException());
 *   } else {
 *    // success, so get the ContentSearchResult object from the response
 *    ContentSearchResult result = response.getResult();
 *
 *    // now we have a list of ContentItems to go through from the response
 *    for (ContentItem item : result.getItems()) {
 *      // process each content item
 *      }
 *   }
 * }}
 *
 */
public class ContentDeliveryClient extends ContentClient {

    // delivery SDK interface (v1.1)
    final private RestApiInterfaceV1 apiInterfaceV1;

    /**
     * Constructor for content delivery client.  {@link ContentSDK#createDeliveryClient}
     * is the how this should be created.  See that method for more detail on the parameters.
     */
    /*package*/ ContentDeliveryClient(
            String serverUrl,
            AuthenticationPolicy authenticationPolicy,
            ContentSettings settings) {

        super(serverUrl, authenticationPolicy, settings);

        // create the interface class for REST calls
        apiInterfaceV1 = retrofit.create(RestApiInterfaceV1.class);

    }

    /**
     * Get the retrofit api interface used internally to make SDK calls.
     *
     * @return retrofit interface
     */
    public RestApiInterfaceV1 getApi() {return apiInterfaceV1;}

    /**
     * When a DigitalAsset has been fully retrieved with all properties, the method
     * {@link DigitalAsset#getNativeDownloadUrl()} ()} should be used to get the download url
     * as defined on the server.  If you only have a reference to a DigitalAsset,
     * this method can be used to manually build a download url using the DigitalAsset id.
     * The returned url will also include any authentication parameters such as the channel token.
     * <p>Although more efficient than having to download the full digital asset, manually
     * building the pathName may not work in all cases so if the url returned from this
     * method is not working, get the full digital asset object and call
     * {@link DigitalAsset#getNativeDownloadUrl()} ()}.
     * </p>
     *
     * @param digitalAssetId the Digital asset id to use for building a download url
     * @return URL to download the asset
     */
    @Override
    public String buildDigitalAssetDownloadUrl(@NotNull String digitalAssetId) {
        return assetUrlBuilder(digitalAssetId, RenditionType.Native.getName());
    }

    /**
     * Builds the thumbnail url.  See {@link #buildDigitalAssetDownloadUrl(String)} for more detail.
     * @param digitalAssetId digital asset to use for building url
     * @return URL to render the thumbnail image
     */
    public String buildDigitalAssetThumbnailUrl(@NotNull String digitalAssetId) {
        // "Thumbnail" must be "thumbnail" in construction of the url
        return assetUrlBuilder(digitalAssetId, RenditionType.Thumbnail.getName().toLowerCase(Locale.US));
    }

    // builds asset path
    private String assetUrlBuilder(String digitalAssetId, String rendition) {
        if (digitalAssetId == null)
            return null;

        // construct pathName to digital asset rendition (v 1.1)
        HttpUrl.Builder builder = baseUrl.newBuilder()
                .addPathSegments(RestApiInterfaceV1.DIGITAL_ASSET_PATH)
                .addPathSegment(digitalAssetId)
                .addPathSegment(rendition);

        // add on query parameters for authentication
        authenticationPolicy.addQueryParameters(builder);

        return builder.build().toString();
    }






}
