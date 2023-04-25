# About Oracle Content Management - Content SDK (Java/Android)

The Java Content SDK for Oracle Content Management is a Java package that interacts with the Content REST APIs. This read-only SDK retrieves structured content, digital assets and content layouts that are managed in Oracle Content Management.

The SDK will work in stand-alone Java applications as it does not contain any direct dependencies on the Android SDK; however, most examples are shown as they would be coded in an Android application as it is assumed that is the primary target platform.  This SDK will also work for Android apps written in Kotlin.

## Installation

You can either include the source directly as a module within your project or publish to your local maven repository by running ''gradle publishToMavenLocal'' and then consume it in your project using a gradle import such as:

```javascript
implementation "com.oracle.content:content-delivery:1.0"
```

## Documentation

- [Developing for Oracle Content Management](https://docs.oracle.com/en/cloud/paas/content-cloud/developer/content-sdk.html)
- [Java SDK](https://docs.oracle.com/en/cloud/paas/content-cloud/content-sdk-java/)


## Examples

### Initializing with a server URL

The SDK must be initialized with the URL of your content service.  
The URL uses the pattern `https://<service-name>-<account-name>.cec.ocp.oraclecloud.com` and can be given to you by your Oracle Content Management service administrator.

The code below shows how to initialize and obtain a <code>ContentDeliveryClient</code> which will be used to interact with the SDK.

```java
// settings for the content SDK - see javadocs for custom options
ContentSettings settings = new ContentSettings();

try {
   // create the Content delivery client object used to make SDK requests
   ContentDeliveryClient deliveryClient=ContentSDK.createDeliveryClient(
        "https://your.service.cec.ocp.oraclecloud.com",
        "channel token string",
        settings);

} catch (ContentException e) {
	// handle exception    
}
```

### Making an asynchronous request to the SDK (using Android and RxJava)

This example shows how the SDK could be used in an Android application to make SDK calls using RxJava.

```java
// Create a search request to search for all the content items with type
// limited to 20 items, and sorted by the "name" field
SearchAssetsRequest searchRequest = new SearchContentItemsRequest(deliveryClient)
     .limit(20)                 // maximum results to return
     .sortByField("name")       // field used to sort results
     .type("mycontenttype");    // content type to search for

// use RxJava to make the asynchronous request - make sdk call on network thread
// and then result comes back on Android main thread in onSuccess method below...
Disposable d = searchRequest.observable()
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(this::onSuccess, this::onError);

....

// method called after successful search request
public void onSuccess(ContentResponse<AssetSearchResult> response) {

    // normally the onError method should be called but 
    // the response can also be checked here
    if (!response.isSuccess()) {
        // see javadocs for more info on response errors
    }


    // get search result from the response
    AssetSearchResult searchResult = response.getResult();

    // get list of all content items from the resul
    List<ContentItem> contentItemList = searchResult.getContentItems();

    // iterate through all of the content items
    for (ContentItem item: contentItems) {

        // print out the name of the content item
        System.out.println("name:" + item.getName());

        // and the value of the "text_field" field (which must exist in type)
        String textField = item.getTextField("text_field");
        System.out.println("text_field=" + textField);
    }
}
```

### Making a synchronous request to the SDK (Java)

This is an example to get the metadata for a specific digital asset based on the id.

```java
// Setup request to get digital asset info by id
// this is a hard-coded example id
GetDigitalAssetRequest request = 
	new GetDigitalAssetRequest(deliveryClient, "CONT932EA7A6994A4A00A602A12F59AD0E27");

try {
     // synchronous request to get digital asset
     ContentResponse<DigitalAsset> response = request.fetch();

     // if successfully found matching asset id
     if (response.isSuccess()) {
         DigitalAsset digitalAsset = response.getResult();

         // get size of the digital asset
         int size = asset.getSize();

         // is the digital asset an image type?
         if (asset.isImage()) {

            // get the download url from the "small" image rendition
            String smallImageUrl = digitalAsset.getRenditionUrl(RenditionType.Small);

            // use your preferred 3rd party image library to download
            // and render the image (e.g. Glide)

         }
         
     }

} catch (ContentException e) {
    // handle SDK exception getting data
}
```

## Contributing

This project welcomes contributions from the community. Before submitting a pull
request, please [review our contribution guide](./CONTRIBUTING.md).

## Security

Please consult the [security guide](./SECURITY.md) for our responsible security
vulnerability disclosure process.

## License 

Copyright (c) 2023, Oracle and/or its affiliates.

Released under the Universal Permissive License v1.0 as shown at
<https://oss.oracle.com/licenses/upl/>.
