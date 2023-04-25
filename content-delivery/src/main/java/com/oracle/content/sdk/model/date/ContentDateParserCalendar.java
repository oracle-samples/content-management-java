/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;

import com.oracle.content.sdk.ContentClient;

/**
 * This implementation of {@link ContentDateParser} uses theCalendar methods for parsing a ContentDate
 * and is compatible with older Java (7) and Android versions less than 26.  Included for legacy puproses
 */
@SuppressWarnings({"WeakerAccess","unused"})
public class ContentDateParserCalendar extends ContentDateParser {

    final ContentDate date;

    // the format to parse the date
    static final String PARSE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    static final String PARSE_DATE_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    ContentDateParserCalendar(ContentDate date) {
        this.date = date;
    }


    /**
     * Parse the server string value and return as a "Date" object.
     *
     * @return ZonedDate object if value or null
     */
    public Date getAsDate() {
        if (date.value == null || date.value.isEmpty())
            return null;

        SimpleDateFormat format = new SimpleDateFormat(PARSE_DATE_FORMAT, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone(ContentDate.DEFAULT_TIMEZONE));
        try {
            // parse string coming from server
            return format.parse(date.value);
        } catch (ParseException e) {
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

        Date date = getAsDate();

        if (date != null) {
            return date.getTime();
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
        Date dateValue = getAsDate();
        if (dateValue == null)
            return null;

        SimpleDateFormat newFormat = new SimpleDateFormat(outputPattern, Locale.US);
        String display =  newFormat.format(dateValue);
        // add time zone at end (this matches what the web UI does)
        if (displayType == ContentDateDisplayType.DateTimeZone) {
            return display + " " +  date.timezone;
        } else {
            return display;
        }

    }
}
