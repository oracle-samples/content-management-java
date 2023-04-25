/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk;

/**
 * Hard-coded strings that are returned as messages in exceptions and in logs.  As these
 * are only for internal use, they should not need translation.
 */
@SuppressWarnings({"unused","WeakerAccess"})
public final class ContentErrorString {

    // parsing url failed
    public static String INVALID_CLOUD_URL = "Invalid cloudURL specified on createDeliveryClient";

    //Exception while building retrofit client
    public static String RETROFIT_ERROR = "Error building Retrofit client";

    //Error from response
    public static String SDK_RESPONSE_ERROR = "SDK response result not successful";

    //SDK invalid
    public static String SDK_INVALID_REQUEST = "Invalid SDK request - check error detail";

    // digital asset field errors;
    public static String GET_ASSET_FIELD_NO_DATA = "getDigitalAssetBitmapForField failed: no data item fields for the content item";
    public static String GET_ASSET_FIELD_NOT_FOUND = "getDigitalAssetBitmapForField failed: the specified field was found but is not a digital asset:";

    // invalid SDK parameters
    public static String INVALID_SDK_PARAMETERS = "Invalid parameters to SDK:";

    // invalid content server URL specified
    // invalid SDK parameters
    public static String INVALID_SERVER_URL = "The server URL is invalid:";

    // invalid digital asset specified
    public static String INVALID_DIGITAL_ASSET = "downloadDigitalAsset failed: invalid digital asset specified";
    // problem downloading asset
    public static String ERROR_DOWNLOADING_ASSET = "downloadDigitalAsset failed with IOException";


    // request not supported for version
    public static String UNSUPPORTED_VERSION_REQUEST = "SDK version not implemented for request:";

    public static String RESPONSE_ERROR = "SDK request returned an error, check the response code";

    public static String DESERIALIZATION_ERROR = "Error while deserializing for request:";


}
