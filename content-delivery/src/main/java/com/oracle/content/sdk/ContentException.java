/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk;

import java.io.Serializable;

/**
 * Encapsulates the various error conditions and contained exceptions that can occur when
 * making SDK calls.
 */
@SuppressWarnings({"WeakerAccess","unused"})
public class ContentException extends RuntimeException implements Serializable {

    /**
     * Various reasons the exception may have occurred
     */
    public enum REASON {
        // Something is wrong the server url
        invalidServerUrl,

        // Error in the response- check the responseCode
        responseError,

        // Item not found (id does not match)
        itemNotFound,

        // SDK says the request is invalid (400), check detail
        invalidRequest,

        // Network error occurred when making the request
        networkError,

        // deserialization of the response data failed
        dataConversionFailed,

        // SDK version not supported condition
        invalidVersion,

        // error while downloading
        downloaderError,

        // general exception (see cause for more detail)
        generalError

    }

    // internal log message (not localized or intended for user display)
    private final String logMessage;

    // reason for the exception
    private final REASON reason;

    // content error returned by body
    private final ContentError contentError;

    // http response code (or 0 if not applicable)
    private final int responseCode;


    /**
     * Initializes a newly created {@code ContentException}
     * with the specified message and caused by the specified
     * {@code Throwable}
     *
     * @param reason     The {@link REASON} as set by the SDK
     * @param cause      The cause of the {@code ContentException}, may by null
     * @param logMessage The message to add to the exception for logging purposes
     * @param contentError content error if applicable
     * @param responseCode http response code if applicable
     */
    public ContentException(
            REASON reason,
            Throwable cause,
            String logMessage,
            ContentError contentError,
            int responseCode) {
        super(cause);
        this.reason = reason;
        this.logMessage = logMessage;
        this.contentError = contentError;
        this.responseCode = responseCode;
    }

    /**
     * Create a ContentException with just REASON and log message.
     *
     * @param reason     The {@link REASON} as set by the SDK
     * @param logMessage The message to add to the exception for logging purposes
     */
    public ContentException(REASON reason, String logMessage) {
        this(reason, null, logMessage, null, 0);
    }


    /**
     * Create a ContentException with just REASON, cause and log message.
     *
     * @param reason     The {@link REASON} as set by the SDK
     * @param logMessage The message to add to the exception for logging purposes
     * @param cause      The underlying cause
     */
    public ContentException(REASON reason, String logMessage, Throwable cause) {
        this(reason, cause, logMessage, null, 0);
    }

    /**
     * Returns the log message of the exception as set internally by the SDK.
     *
     * @return The exception's message
     */
    public String getLogMessage() {
        return logMessage;
    }

    /**
     * Get the reason for the exception as set by the SDK.
     *
     * @return REASON for the exception
     */
    public REASON getReason() {
        return reason;
    }

    /**
     * Get detail from ContentError or "" if not available
     *
     * @return Error detail string or empty string
     */
    public String getDetail() {
        if (contentError != null && contentError.getDetail() != null) {
            return contentError.getDetail();
        } else {
            return "";
        }
    }

    /**
     * If there was a response from the server this value will be set (e.g. 404, 401).
     * If the exception is not in the context of a response the value will be 0.
     *
     * @return http response code (or 0 if not applicable)
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Get detailed ContentError response that came back from the server.
     *
     * @return if applicable, the {@link ContentError} from the server
     */
    public ContentError getContentError() {
        return contentError;
    }

    /**
     * Generate a verbose debug message describing the error.  This is meant
     * for debugging purposes and not as a message that would be displayed to the user.
     *
     * @return verbose debug message describing the problem
     */
    public String getVerboseErrorMessage() {
        StringBuilder message = new StringBuilder();
        int responseCode = 0;
        // if we have detailed contentError message
        if (contentError != null) {
            message.append(contentError.getDetail());
            if (contentError.getStatus() != null) {
                responseCode = contentError.getStatus();
            }
        } else {
            if (reason == REASON.invalidServerUrl) {
                message.append(ContentErrorString.INVALID_SDK_PARAMETERS);
                message.append("\n");
                message.append(getLogMessage());
            } else {
                if (getLogMessage() != null) {
                    message.append(getLogMessage());
                }
                responseCode = getResponseCode();
                if (responseCode == 0) {
                    Throwable cause = getCause();
                    if (cause != null) {
                        message.append("\nexception=");
                        message.append(cause.getMessage());
                        message.append("\n:");
                        message.append(cause.getClass().getName());
                    }
                }
            }
        }
        if (responseCode != 0) {
            message.append("\nResponse Code=");
            message.append(responseCode);
        }
        return message.toString();
    }

}
