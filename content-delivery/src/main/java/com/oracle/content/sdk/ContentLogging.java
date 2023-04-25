/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk;


import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Specify how the SDK should handle logging.  Logging level can be NONE,
 * INFO, or HTTP (which includes full http traffic). An optional LogCallback can
 * be specified to intercept logging messages for your own use.
 */
@SuppressWarnings({"WeakerAccess","unused"})
public class ContentLogging {


    public enum LogLevel {
        NONE,      // no logging
        INFO,      // SDK info (does not include http traffic)
        HTTP       // complete clogging includes http traffic in addition to INFO
    }

    /**
     * To provide your own handling of logging implement this interface
     */
    public interface LogCallback {
        void log(Level priority, String tag, String message);
    }

    // callback to use to handle logs
    private LogCallback logCallback = new DefaultLogger();

    // general logging
    private final LogLevel logLevel;

    /**
     * Set specific logging logLevel and use a custom logger
     *
     * @param logLevel {@link LogLevel} for logging
     * @param customLogger Specified LogCallback
     */
    public ContentLogging(LogLevel logLevel, @NotNull LogCallback customLogger) {
        this.logLevel = logLevel;
        logCallback = customLogger;
    }

    /**
     * Creating a logging policy with specified level NONE, INFO, or HTTP.
     *
     * @param logLevel {@link LogLevel} for logging
     */
    public ContentLogging(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * Default logger just uses standard Android logging
     */
    public static class DefaultLogger implements LogCallback {
        @Override
        public void log(Level priority, String tag, String message) {
            Logger.getLogger(tag).log(priority, message);
        }
    }


    /**
     * Get the current log level in use
     * @return level for logging
     */
    public LogLevel getLogLevel() {
        return logLevel;
    }

    public boolean isEnabled() {
        return logLevel != null;
    }

    public boolean isHttpEnabled() {
        return logLevel == LogLevel.HTTP;
    }

    public void log(Level priority, String tag, String message) {
        if (isEnabled()) {
            logCallback.log( priority, tag, message);
        }
    }
}
