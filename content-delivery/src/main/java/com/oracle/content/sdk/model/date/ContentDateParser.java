/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.date;

/**
 * Get an object for this interface by calling {@link ContentDate#getDateParser()}
 */
abstract public class ContentDateParser {

    /**
     * Return a formatted display string by parsing the date values and formatting
     * according to the DisplayType, specifying the output format.
     *
     * @param displayType Format to use
     * @param outputFormat Use a custom output format (e.g. "M/d/yyyy" or "MMMMM d, yyyy")
     * @return Display value or ""
     */
    abstract public String getDisplayString(ContentDateDisplayType displayType, String outputFormat);

    /**
     * Return a formatted display string by parsing the date values and formatting
     * according to the DisplayType, using the default output pattern.
     *
     * @param displayType Format to use
     * @return Display value or ""
     */
    public String getDisplayString(ContentDateDisplayType displayType ) {
        return getDisplayString(displayType, displayType.outputPattern);
    }


    /**
     * Parse the server string value and return the number of milliseconds
     * since "epoch".
     *
     * @return Time im milliseconds or null for parsing error
     */
    abstract public Long getTimeInMilliseconds();

}
