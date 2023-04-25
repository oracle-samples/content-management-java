/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk;

import java.io.File;

/**
 * For finer control over the SDK settings, such as enabling the cache or changing the connection timeout.
 */
@SuppressWarnings({"WeakerAccess","unused"})
public class ContentSettings {

    // set to override the default timeout value
    private Integer connectionTimeoutSeconds;

    // cache settings, will default to null (no cache)
    private CacheSettings cacheSettings = new CacheSettings(null);

    // User-Agent header to send with requests
    private String userAgentHeader = null;

    /**
     * Construct general settings, using defaults (no cache, default timeout)
     */
    public ContentSettings() {
    }

    /**
     * Enable the cache using the specified folder for the cache.  This will
     * enable the both http and asset caches and use all defaults.  For more specific cache settings,
     * use {@link #setCacheSettings(CacheSettings)}
     *
     * @param cacheDir folder to use for cache data
     * @return this
     */
    public ContentSettings enableCache(File cacheDir) {
        this.cacheSettings = new CacheSettings(cacheDir);
        return this;
    }

    /**
     * Set the "User-Agent" header to be sent with each SDK request.
     *
     * @param userAgentHeader User-Agent header value
     * @return this
     */
    public ContentSettings setUserAgentHeader(String userAgentHeader) {
        this.userAgentHeader = userAgentHeader;
        return this;
    }


    /**
     * Set more specific set of cache settings.  See {@link CacheSettings}
     *
     * @param settings Cache settings
     * @return this
     */
    public ContentSettings setCacheSettings(CacheSettings settings) {
        this.cacheSettings = settings;
        return this;
    }

    public ContentSettings setTimeoutSeconds(int connectionTimeoutSeconds) {
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;
        return this;
    }

    public Integer getConnectionTimeoutSeconds() {
        return connectionTimeoutSeconds;
    }

    public CacheSettings getCacheSettings() {
        return cacheSettings;
    }

    public String getUserAgentHeader() { return userAgentHeader; }
}