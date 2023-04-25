/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.field;


/**
 * When a field has been explicitly identified as being largetext.
 */
@SuppressWarnings({"unused"})
public class ContentFieldLargeText extends ContentFieldText {

    public ContentFieldLargeText(String value) {
        super(value, FieldType.LARGE_TEXT);
    }

    /**
     * Utility method - Does the field contained rich text?
     *
     * @param s string to test for rich text
     * @return true if rich text content
     */
    static public boolean isRichText(String s) {
        return s != null && s.startsWith("<!DOCTYPE html>");
    }
}
