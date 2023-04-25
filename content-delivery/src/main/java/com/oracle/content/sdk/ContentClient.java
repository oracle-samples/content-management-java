/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The base class for the {@link ContentDeliveryClient}.  Contains core SDK information such as the base url and policy classes.
 * In the future this will be used as the basis for other SDK clients.
 */
@SuppressWarnings("unused,WeakerAccess")
abstract public class ContentClient {

    final private static String TAG = "ContentClient";

    // no-cache header value
    public static String NO_CACHE = "no-cache";

    /**
     * The logging policy is in effect for the SDK globally
     */
    static protected ContentLogging sContentLogging = new ContentLogging(ContentLogging.LogLevel.NONE);

    // retrofit instance associated with the baseURL and channel token we will use for the calls
    final protected Retrofit retrofit;

    // base url for REST calls
    final HttpUrl baseUrl;

    // settings
    final private ContentSettings settings;

    // the okhttp client
    OkHttpClient okHttpClient;

    // the authentication policy in use for this client
    final AuthenticationPolicy authenticationPolicy;

    // gson converter to use for converting json from SDK responses to objects
    protected static Gson gson;

    static {
        // gson used to deserialize model objects

        GsonBuilder gsonBuilder = new GsonBuilder();

        gson = gsonBuilder.create();
    }

    /**
     * Get the Gson instance to use for deserialization, which includes any necessary custom
     * deserialization classes.
     *
     * @return gson instance
     */
    public static Gson gson() {
        return gson;
    }

    protected ContentClient(
            @NotNull String contentServer,
            @NotNull AuthenticationPolicy authenticationPolicy,
            @NotNull ContentSettings settings
    ) {

        this.settings = settings;

        // base url into HttpUrl
        this.baseUrl = HttpUrl.parse(contentServer);
        if (this.baseUrl == null) {
            throw getContentException(
                    ContentException.REASON.invalidServerUrl,
                    null,
                    ContentErrorString.INVALID_SERVER_URL+contentServer, null);
        }
        // store User-Agent header value
        authenticationPolicy.setUserAgentHeader(settings.getUserAgentHeader());
        this.authenticationPolicy = authenticationPolicy;

        try {
            // create the http client
            this.okHttpClient = createOkHttpClient();

            // create retrofit instance we'll use to make REST requests
            this.retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(contentServer)
                    .client(okHttpClient)
                    .build();

        } catch (IllegalArgumentException e) {
            throw getContentException(
                    ContentException.REASON.invalidServerUrl,
                    e,
                    ContentErrorString.RETROFIT_ERROR, null);
        }


    }

    /**
     * Called internally to log messages
     *
     * @param priority log priority (e.g. Log.DEBUG)
     * @param tag Logging tag (e.g. class name)
     * @param message Message to log
     */
    public static void log(Level priority, String tag, String message) {
        if (sContentLogging.isEnabled()) {
            sContentLogging.log(priority, tag, message);
        }
    }

    /**
     * For more detail, see {@link ContentDeliveryClient#buildDigitalAssetDownloadUrl(String)}
     * This is defined in the base class for future use in a management client.
     *
     * @param digitalAssetId digital asset to use for building url
     * @return url that can be used to get/download digital assets
     */
    public abstract String buildDigitalAssetDownloadUrl(String digitalAssetId);

    /**
     * The base URL for the server hosting SDK calls.
     *
     *  @return Base url for the server
     */
    public HttpUrl getBaseUrl() {
        return baseUrl;
    }

    /**
     * Get the current authentication policy in use
     *
     * @return Current authentication policy
     */
    public AuthenticationPolicy getAuthenticationPolicy() {
        return this.authenticationPolicy;
    }


    private static boolean isNoCacheHeader(Request request) {
        String cacheHeader = request.header("Cache-Control");
        return (cacheHeader != null && cacheHeader.equals(NO_CACHE));
    }


    /**
     * Cache interceptor for responses
     *
     * @param cacheSettings The cache policy in use
     */
    private static Interceptor responseCacheInterceptor(final CacheSettings cacheSettings) {

        return chain -> {
            okhttp3.Response response = chain.proceed(chain.request());

            // skip cache if no cache specified
            if (isNoCacheHeader(chain.request())) {
                return response;
            } else {

                // get cache expiration value
                CacheSettings.Expiration expiration = cacheSettings.cacheExpiration;

                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(expiration.time, expiration.timeUnit)
                        .build();

                return response.newBuilder()
                        .header("Cache-Control", cacheControl.toString())
                        .build();
            }
        };
    }

    /**
     * Offline request cache interceptor to provide offline mode
     *
     * @param cacheSettings The cache policy in use
     */
    private static Interceptor offlineCacheInterceptor(final CacheSettings cacheSettings) {

        return chain -> {

            Request request = chain.request();

            if (!isNoCacheHeader(chain.request())) {

                // get cache expiration value
                CacheSettings.Expiration expiration = cacheSettings.offlineCacheExpiration;

                // how long to keep offline cached data
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(expiration.time, expiration.timeUnit)
                        .build();

                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();

            }

            return chain.proceed(request);
        };
    }


    /**
     * The OkHttp client to use.  Add custom interceptors for logging, caching.
     */
    private OkHttpClient createOkHttpClient() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // authentication policy (this shouldn't be null)
        builder.addInterceptor(authenticationPolicy.getInterceptor());

        CacheSettings cacheSettings = settings.getCacheSettings();
        // use cache?
        if (cacheSettings != null && cacheSettings.isEnabled()) {
            log(TAG, "cacheSettings enabled");

            // add cache and interceptors to handle caching
            builder.cache(new Cache( cacheSettings.cacheDir, cacheSettings.httpCacheSize ));

            builder.addNetworkInterceptor(responseCacheInterceptor(cacheSettings));

            // add interceptors to handle offline cache
            if (cacheSettings.isOfflineCacheEnabled()) {
                log(TAG, "offline cache enable!");
                builder.addInterceptor(offlineCacheInterceptor(cacheSettings));
            }

        }

        // timeout for connection
        if (settings.getConnectionTimeoutSeconds() != null) {
            int secondsTimeout = settings.getConnectionTimeoutSeconds();

            log(TAG, "override connection timeout value to " + secondsTimeout + "  seconds");
            builder.connectTimeout(secondsTimeout, TimeUnit.SECONDS);
            builder.readTimeout(secondsTimeout, TimeUnit.SECONDS);
            builder.callTimeout(secondsTimeout, TimeUnit.SECONDS);
        }

        // is http logging enabled?
        if (sContentLogging.isHttpEnabled()) {
            log(TAG, "http logging enabled!");
            // add logging interceptor to log all http traffic
            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        } else {
            log(TAG, "http logging disabled");
        }

        return builder.build();
    }


    /**
     * Helper method to Log messages using the logging policy
     *
     * @param tag logging tag
     * @param message logging message
     */
    static public void log(String tag, String message) {
        log(Level.INFO, tag, message);
    }

    /**
     * Helper method to generate a ContentException (used internally).
     *
     * @param reason     If you know the reason, specify it here
     * @param cause      If there is an exception that caused this, include it here (may be null)
     * @param logMessage Logging message to describe the general problem
     * @param response   If you have a response, include it here
     * @return content exception
     */
    static public ContentException getContentException(
            ContentException.REASON reason,
            Throwable cause,
            String logMessage,
            Response<JsonElement> response) {

        // see if there is error structure coming back
        ContentError contentError = null;
        int responseCode = 0;
        if (response != null) {
            ResponseBody errorBody = response.errorBody();
            if (errorBody != null) {
                try {
                    contentError = gson.fromJson(errorBody.string(), ContentError.class);
                } catch (Exception e) {
                    log(TAG, "error parsing json response:");
                    log(TAG, errorBody.toString());
                }
            } else {
                log(TAG, "response error body is empty");
            }
            // was able to parse the error?
            if (contentError != null && contentError.getStatus() != null) {
                responseCode = contentError.getStatus();
                log(TAG, "contentError.detail=" + contentError.getDetail());
                log(TAG, "contentError.title=" + contentError.getTitle());
                log(TAG, "contentError.status=" + contentError.getStatus());
                if (contentError.getStatus() == 404) {
                    reason = ContentException.REASON.itemNotFound;
                }
            } else {
                responseCode = response.code();
            }
        }
        // construct the ContentException values
        return new ContentException(reason, cause, logMessage, contentError, responseCode);
    }


}
