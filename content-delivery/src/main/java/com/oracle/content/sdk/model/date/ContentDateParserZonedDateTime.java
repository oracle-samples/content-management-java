/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.date;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;

import com.oracle.content.sdk.ContentClient;

/**
 * This implementation of {@link ContentDateParser} uses the newer Java 8/Android API 26+
 * date/time methods based around ZonedDateTime and is preferred if your minimum Android API is 26+.
 */
@SuppressWarnings({"WeakerAccess","unused"})
public class ContentDateParserZonedDateTime extends ContentDateParser {

    final ContentDate date;

    ContentDateParserZonedDateTime(ContentDate date) {
        this.date = date;
    }

    /**
     * Parse the server string value and return as a "ZonedDateTime" object.
     *
     * @return ZonedDate object if value or null
     */
    public ZonedDateTime getAsZonedDateTime() {
        if (date.value == null || date.value.isEmpty())
            return null;

        try {
            // parse string coming from server
            return ZonedDateTime.parse(date.value, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException e) {
            ContentClient.log(Level.WARNING, "[ContentDate]", "Parsing Error:" + e);
            return null;
        }

    }

    /**
     * Parse the server string value and return the number of milliseconds
     * since "epoch".
     *
     * @return Time im milliseconds or null for parsing error
     */
    @Override
    public Long getTimeInMilliseconds() {

        ZonedDateTime zonedDateTime = getAsZonedDateTime();

        if (zonedDateTime != null) {
            return zonedDateTime.toInstant().toEpochMilli();
        } else {
            return null;
        }
    }


    /**
     * Return a formatted display string by parsing the date values and formatting
     * according to the DisplayType.
     *
     * @param displayType Format to use
     * @param outputPattern Use a custom output format (e.g. "M/d/yyyy" or "MMMMM d, yyyy")
     * @return Display value or ""
     */
    @Override
    public String getDisplayString(ContentDateDisplayType displayType, String outputPattern) {
        // first parse string as a date
        ZonedDateTime ldt = getAsZonedDateTime();
        if (ldt == null)
            return null;

        // pattern to display
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(outputPattern);//.withZone(ZoneId.of(timezone));

        String dateTime = ldt.format(formatter);

        // add time zone at end (this matches what the web UI does)
        if (displayType == ContentDateDisplayType.DateTimeZone) {
            return dateTime + " " +  date.timezone;
        }

        return dateTime;

    }
}
