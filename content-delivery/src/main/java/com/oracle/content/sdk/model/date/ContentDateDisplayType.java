/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.date;

/**
 * Enum to represent the display types for a ContentDate.
 */
public enum ContentDateDisplayType {
    // unknown display type
    Unknown("", "M/d/yyyy"),
    // display date only
    Date("datepicker", "M/d/yyyy"),
    // display date and time
    DateTime("datetimepicker", "M/d/yyyy hh:mm a"),
    // display date time and time zone
    DateTimeZone("datetimepickertz", "M/d/yyyy hh:mm a");

    // name, corresponds to editorType in content type for easy matching
    final String name;

    // associated output format
    final String outputPattern;


    ContentDateDisplayType(String name, String outputPattern) {
        this.name = name;
        this.outputPattern = outputPattern;
    }

    /**
     * Attempt to find matching display type based on name.
     * If no match is found, use the specified default.
     *
     * @param name to match
     * @return matching DisplayType or Unknown if no match
     */
    static public ContentDateDisplayType getDisplayType(String name) {

        if (name != null) {
            // see if any of the values match
            for (ContentDateDisplayType type : values()) {
                if (type.name.equals(name)) {
                    return type;
                }
            }
        }

        // if not match, return Unknown
        return Unknown;
    }
}
