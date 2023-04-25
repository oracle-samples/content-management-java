/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk;

import java.io.File;
import java.util.concurrent.TimeUnit;


/**
 * The {@code CacheSettings} specifies settings for the cache behavior of the SDK
 * The cacheDir is required to enable the cache.
 *     Here is an example of creating a CacheSettings:
 * <pre>   {@code
 *
 *  CacheSettings cacheSettings = new CacheSettings(context.getCacheDir());
 * }</pre>
 * If not specified, there are default values for cache size (10MB), as well
 * as in-memory cache expiration (2 minutes) and offline cache (7 days).
 * The offline cache will allow you to request cached SDK results even if
 * there is no network access.
 *
 */
@SuppressWarnings({"WeakerAccess","unused"})
public final class CacheSettings {

    public static final long MB = 1024*1024;

    // default http cache size is 10MB
    private static final long DEFAULT_HTTP_CACHE_SIZE = 10 * MB;

    // default asset cache size is 20MG
    private static final long DEFAULT_ASSET_CACHE_SIZE = 20 * MB;

    // the cache location
    final File cacheDir;

    // the asset cache location
    File assetCacheDir;

    // if cache is enabled, is offline cache enabled?
     boolean offlineCacheEnabled = true;

    // the asset cache size
    long httpCacheSize = DEFAULT_HTTP_CACHE_SIZE;

    // the asset cache size
    long assetCacheSize = DEFAULT_ASSET_CACHE_SIZE;

    // general cache expiration (default 2 minutes)
    Expiration cacheExpiration = new Expiration(2, TimeUnit.MINUTES);

    // offline cache expiration (default 7 days)
    Expiration offlineCacheExpiration = new Expiration(7, TimeUnit.DAYS);

    /**
     * Expiration time class to tweak the expiration values for cache.
     */
    public final static class Expiration {
        final int time;
        final TimeUnit timeUnit;

        /**
         * Specific the cache expiration values
         *
         * @param time     number
         * @param timeUnit time unit that the number represents
         */
        public Expiration(int time, TimeUnit timeUnit) {
            this.time = time;
            this.timeUnit = timeUnit;
        }
    }

    /**
     * Construct cache config specifying a cache dir to enable it.  Both
     * http and asset caches are enabled by default.  If set to null, cache is disabled.
     *
     * @param cacheDir directory location to store cache
     */
    public CacheSettings(File cacheDir) {
        this.cacheDir = cacheDir;
        if (cacheDir != null) {
            // default asset cache dir to subfolder /assets
            this.assetCacheDir = new File(cacheDir.toPath() + "/assets");
        }
    }

    /**
     * Is the cache enabled?
     *
     * @return true if the cache is enabled
     */
    public boolean isEnabled() {
        return (cacheDir != null);
    }

    /**
     * Optionally override the cache folder to use for assets.
     * Set to null to effectively turn off cache for assets.
     *
     * @param dir the asset cache dir
     * @return this
     */
    public CacheSettings setAssetCacheDir(File dir) {
        assetCacheDir = dir;
        return this;
    }

    /**
     * Set the cache size in bytes.
     *
     * @param size cache size in bytes
     * @return this
     */
    public CacheSettings setHttpCacheSize(long size) {
        this.httpCacheSize = size;
        return this;
    }

    /**
     * Override the maximum asset cache size.
     *
     * @param size Size in bytes
     * @return this
     */
    public CacheSettings setAssetCacheSize(long size) {
        assetCacheSize = size;
        return this;
    }

    /**
     * Is offline cache enabled?
     *
     * @return true if offline cache is enabled
     */
    public boolean isOfflineCacheEnabled() {
        return isEnabled() && offlineCacheEnabled;
    }

    /**
     * By default, if the cache is enabled the offline cache is enabled.  Call
     * this method with 'false' to disable just offline cache.
     *
     * @param enable true to enable offline cache (default) or false to disable
     * @return this
     */
    public CacheSettings enableOfflineCache(boolean enable) {
        offlineCacheEnabled = enable;
        return this;
    }


    /**
     * Set the general cache expiration value
     * @param expiration general cache expiration
     */
    public void setCacheExpiration(Expiration expiration) {
        this.cacheExpiration = expiration;
    }

    /**
     * Set the offline cache expiration value
     * @param expiration general cache expiration
     */
    public void setOfflineCacheExpiration(Expiration expiration) {
        this.offlineCacheExpiration = expiration;
    }


}
