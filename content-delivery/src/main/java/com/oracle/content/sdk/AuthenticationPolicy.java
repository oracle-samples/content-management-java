/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.sdk;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Abstract Authentication policy defines the authentication information used
 * for the SDK.  For the {@link ContentDeliveryClient}, there is currently
 * only {@link AuthenticationChannelTokenPolicy} used for authentication.
 */
@SuppressWarnings({"WeakerAccess","unused"})
public abstract class AuthenticationPolicy {


    // authorization header
    private static final String AUTHORIZATION_HEADER = "Authorization";

    // User-Agent header
    private static final String USER_AGENT_HEADER = "User-Agent";
    protected String userAgentHeader;


    // interceptor to handle SDK authentication
    private final Interceptor authInterceptor = new Interceptor() {
        /**
         * okhttp interceptor to add specified query parameter (e.g. channelToken=[value])
         * to every sdk request.
         *
         * @param chain Chain request
         * @return response
         * @throws IOException if error
         */
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            HttpUrl.Builder urlBuilder = request.url().newBuilder();
            // add query parameters as required by auth type
            addQueryParameters(urlBuilder);

            // new request builder with added params
            Request.Builder builder = request.newBuilder().url(urlBuilder.build());

            // add auth header?
            if (getAuthHeader() != null) {
                builder.addHeader(AUTHORIZATION_HEADER, getAuthHeader());
            }

            if (userAgentHeader != null) {
                builder.addHeader(USER_AGENT_HEADER, userAgentHeader);
            }

            return chain.proceed(builder.build());
        }
    };

    /**
     * Get the okhttp interceptor to use for authentication
     *
     * @return interceptor
     */
    protected Interceptor getInterceptor() { return authInterceptor; }

    /**
     * Returns the authorization header to add for each request. By default returns null
     * unless overridden.
     *
     * @return null, unless overridden
     */
    public String getAuthHeader() {
        // by default, no auth headers added
        return null;
    }

    /**
     * Set the User-Agent header to send for each request
     * @param userAgentHeader User-Agent header value
     */
    public void setUserAgentHeader(String userAgentHeader) {
        this.userAgentHeader = userAgentHeader;
    }

    /**
     * Pass in the builder to add query parameters to every request.  By default
     * will not add any query parameters unless overridden.
     *
     * @param builder Builder class to add query parameters
     */
    public void addQueryParameters(HttpUrl.Builder builder) {
        // by default, no query parameters are added
    }

}
