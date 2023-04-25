/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk;


import org.jetbrains.annotations.NotNull;

/**
 * Main entry point for creating a delivery client using {@link #createDeliveryClient(String, String)}
 * or for setting the global logging options such as {@link #setLogLevel(ContentLogging.LogLevel)}.
 */
@SuppressWarnings("WeakerAccess")
final public class ContentSDK {

    /**
     * Creates a {@link ContentDeliveryClient} given a valid server url and channel token.  This is the
     * simplest way to create a client as it uses all of the defaults for settings.  This method will throw
     * a {@link ContentException} if the server or channel token are null or empty.
     *
     * @param contentServer    the URL to a server instance
     * @param channelToken     channel token required for authentication
     * @return new {@link ContentDeliveryClient}
     */
    public static @NotNull
    ContentDeliveryClient createDeliveryClient(
            String contentServer,
            String channelToken) {
        return createDeliveryClient(contentServer, channelToken, new ContentSettings());
    }

    /**
     * Creates a {@link ContentDeliveryClient} given a valid server url and channel token, and provide
     * additional settings such as enabling caching or changing the timeout.  See {@link ContentSettings}
     *
     * @param contentServer the URL to a server instance
     * @param channelToken channel token required for authentication
     * @param settings Settings for the SDK
     * @return new {@link ContentDeliveryClient}
     */
    public static @NotNull
    ContentDeliveryClient createDeliveryClient(
            String contentServer,
            String channelToken,
            @NotNull ContentSettings settings) {


        if (channelToken == null || channelToken.isEmpty()) {
            throw new ContentException(ContentException.REASON.invalidServerUrl, "Channel token is empty");
        }

        return createDeliveryClient(contentServer, new AuthenticationChannelTokenPolicy(channelToken), settings);
    }

    /**
     * Creates a {@link ContentDeliveryClient} given a valid server url and authentication policy, and provide
     * additional settings such as enabling caching or changing the timeout.  See {@link ContentSettings}
     *
     * @param contentServer the URL to a server instance
     * @param authenticationPolicy authentication policy to use for SDK
     * @param settings Settings for the SDK
     * @return new {@link ContentDeliveryClient}
     */
    public static @NotNull
    ContentDeliveryClient createDeliveryClient(
            String contentServer,
            AuthenticationPolicy authenticationPolicy,
            @NotNull ContentSettings settings) {

        if (contentServer == null || contentServer.isEmpty()) {
            throw new ContentException(ContentException.REASON.invalidServerUrl, "Content server url is empty");
        }
        // create the new delivery client
        return new ContentDeliveryClient(contentServer, authenticationPolicy, settings);

    }

    /**
     * Set the logging level globally for all SDK clients.  This must be called prior to any calls
     * to create the delivery client.
     *
     * @param logLevel Specify the desired level of logging using {@link ContentLogging.LogLevel}
     */
    public static void setLogLevel(ContentLogging.LogLevel logLevel) {
        setLoggingPolicy(new ContentLogging(logLevel));
    }

    /**
     * Specifies a custom logging policy.  Use this method if you want to intercept the SDK log output
     * for your own use.  See {@link ContentLogging}
     *
     * @param contentLogging Valid {@link ContentLogging}
     */
    public static void setLoggingPolicy(@NotNull ContentLogging contentLogging) {
        ContentClient.sContentLogging = contentLogging;
    }


}
