/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.mobile.content.sdk;

import org.junit.Ignore;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.oracle.content.sdk.model.date.ContentDate;
import com.oracle.content.sdk.model.date.ContentDateDisplayType;
import com.oracle.content.sdk.model.date.ContentDateParser;
import com.oracle.content.sdk.model.date.ContentDateParserCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests for date field parsing based on actual test cases.
 * Description is always the expected "display" value.
 * None of these tests require a server.
 */

public class ContentDateFieldTests {

    private static String getDisplayString(ContentDate date, ContentDateDisplayType type) {
        ContentDateParser parser = date.getDateParser();

        return parser.getDisplayString(type);
    }


    @Test
    public void calendarLegacyDateTest()  {

        ContentDate contentDate = new ContentDate(
                "2019-02-19T00:00:00.000-08:00",
                "America/Los_Angeles",
                "2/19/2019");

        ContentDateParser parser = contentDate.getLegacyDateParser();
        String displayResult = parser.getDisplayString(ContentDateDisplayType.Date);
        System.out.println(displayResult);
        assertEquals(contentDate.getDescription(), displayResult);
        assertNotNull(((ContentDateParserCalendar)parser).getAsDate());
        assertNotEquals(0, parser.getTimeInMilliseconds().doubleValue());

        //assertEquals((ContentDateDisplayType.Date),ContentDateDisplayType.getDisplayType("datepicker"));

    }

    @Test
    public void calendarLegacyDateTestParseError()  {

        ContentDate contentDate = new ContentDate(
                "2019-009309",
                "America/Los_Angeles",
                "2/19/2019");

        ContentDateParser parser = contentDate.getLegacyDateParser();
        String displayResult = parser.getDisplayString(ContentDateDisplayType.Date);
        assertNull(displayResult);

    }


    @Test
    public void dateTest1()  {


        ContentDate contentDate = new ContentDate(
                "2019-02-19T00:00:00.000-08:00",
                "America/Los_Angeles",
                "2/19/2019");


        String displayResult = getDisplayString(contentDate, ContentDateDisplayType.Date);
        System.out.println(displayResult);
        assertEquals(contentDate.getDescription(), displayResult);

    }

    @Test
    public void dateTest2()  {


        ContentDate contentDate = new ContentDate(
                "2019-02-04T13:37:22.229-05:00",
                "America/Montreal",
                "2/4/2019");


        String displayResult = getDisplayString(contentDate, ContentDateDisplayType.Date);
        System.out.println(displayResult);
        assertEquals(contentDate.getDescription(), displayResult);

    }

    @Test
    public void dateTest3()  {


        ContentDate contentDate = new ContentDate(
                "2019-02-04T13:37:22.229-05:00",
                "America/Montreal",
                "2/4/2019");

        ContentDateParser parser = contentDate.getDateParser();

        String displayResult = parser.getDisplayString(ContentDateDisplayType.Date, "MMMM d, yyyy");
        System.out.println(displayResult);
        assertEquals("February 4, 2019", displayResult);

    }

    @Test
    public void dateTestParseError()  {


        ContentDate contentDate = new ContentDate(
                "2019-34343",
                "America/Los_Angeles",
                "2/19/2019");


        String displayResult = getDisplayString(contentDate, ContentDateDisplayType.Date);
        assertNull(displayResult);
    }

    @Test
    @Ignore // retrofit/java warning
    public void dateTestTime1() {


        ContentDate contentDate = new ContentDate(
                "2019-02-19T10:00:00.000-08:00",
                "America/Los_Angeles",
                "2/19/2019 10:00 AM");

        String displayResult = getDisplayString(contentDate, ContentDateDisplayType.DateTime);
        System.out.println(displayResult);
        assertEquals(contentDate.getDescription(), displayResult);
    }

    @Test
    @Ignore // retrofit/java warning
    public void dateTestTime2()  {


        ContentDate contentDate = new ContentDate(
                "2019-02-04T13:37:22.229-05:00",
                "America/Montreal",
                "2/4/2019 01:37 PM");

        String displayResult = getDisplayString(contentDate, ContentDateDisplayType.DateTime);
        System.out.println(displayResult);
        assertEquals(contentDate.getDescription(), displayResult);
    }


    @Test
    @Ignore // retrofit/java warning
    public void dateTestTimeZone1()  {


        ContentDate contentDate = new ContentDate(
                "2019-02-19T10:00:00.000+04:00",
                "Indian/Mauritius",
                "2/19/2019 10:00 AM Indian/Mauritius");

        String displayResult = getDisplayString(contentDate, ContentDateDisplayType.DateTimeZone);
        System.out.println(displayResult);
        assertEquals(contentDate.getDescription(), displayResult);

    }

    @Test
    @Ignore // retrofit/java warning
    public void dateTestTimeZone2()  {


        ContentDate contentDate = new ContentDate(
                "2019-02-04T13:37:28.612-05:00",
                "America/Montreal",
                "2/4/2019 01:37 PM America/Montreal");

        String displayResult = getDisplayString(contentDate, ContentDateDisplayType.DateTimeZone);
        System.out.println(displayResult);
        assertEquals(contentDate.getDescription(), displayResult);

    }

    // no assertions, just for playing with methods
    @Test
    public void dateTestMilliseconds()  {

        ContentDate contentDate = new ContentDate(
                "2019-02-25T20:50:30.361Z",
                "UTC",
                "");

        ContentDateParser parser = contentDate.getDateParser();
        Long time = parser.getTimeInMilliseconds();
        if (time != null) {
            long difference = System.currentTimeMillis() - time;
            System.out.println("Minutes since now:" + difference/1000/60);
        } else {
            System.out.println("Time error!");
        }

        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
        System.out.println("now:" + now.toInstant().toEpochMilli());
        System.out.println("snow:" + System.currentTimeMillis());

    }

    @Test
    public void displayTypeMatch() {
        ContentDateDisplayType type = ContentDateDisplayType.getDisplayType("datepicker");
        assertEquals(ContentDateDisplayType.Date, type);
    }


}

