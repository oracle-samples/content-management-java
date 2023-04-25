/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.oracle.content.sdk.model.AssetObject;

/**
 * Represents content date as represented by the SDK.  Use {@link ContentDateParser} to parse the string values.
 */
@SuppressWarnings({"WeakerAccess","unused"})
public class ContentDate extends AssetObject {


    public static String DEFAULT_TIMEZONE = "UTC";

    /**
     * Create Content Date field.
     *
     * @param value Date/time string (e.g. "2018-08-21T19:09:47.106")
     * @param timezone Timezone, can be null which will default to "UTC"
     * @param description Optional description (can be null)
     */
    public ContentDate(String value, String timezone, String description) {
        this.value = value;
        this.timezone = timezone==null?DEFAULT_TIMEZONE:timezone;
        this.description = description;
    }

    @SerializedName("value")
    @Expose
    String value;
    @SerializedName("timezone")
    @Expose
    String timezone;
    @SerializedName("description")
    @Expose
    String description;

    /**
     * Get raw string value of the date  Use {@link ContentDateParser} to parse the value.
     * @return string value of date.
     */
    public String getValue() {
        return value;
    }

    /**
     * Get raw timezone string
     * @return timezone string
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * Get description field for date.
     * @return date description
     */
    public String getDescription() {
        return description;
    }


    /**
     * Gets the default date parser using ZonedDateTime available in Android API 26+.
     * If older OS support is needed, use {@link #getLegacyDateParser()}
     *
     * @return {@link ContentDateParser} to parse and display dates
     */
    public ContentDateParser getDateParser() {
        return new ContentDateParserZonedDateTime(this);
    }

    /**
     * Gets a date parser compatible with older OS versions (less than API 26).
     *
     * @return {@link ContentDateParser} to parse and display dates
     */
    public ContentDateParser getLegacyDateParser() {
        return new ContentDateParserCalendar(this);
    }





}
