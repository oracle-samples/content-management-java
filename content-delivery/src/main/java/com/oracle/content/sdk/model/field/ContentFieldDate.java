/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.field;


import com.oracle.content.sdk.model.date.ContentDate;
import com.oracle.content.sdk.model.date.ContentDateParser;
import com.oracle.content.sdk.model.date.ContentDateDisplayType;

/**
 * This field consists of a date value, represented by a ContentDate object.
 */
public class ContentFieldDate extends ContentField<ContentDate> {

    public ContentFieldDate( ContentDate value) {
        super(value, FieldType.DATE);
    }

    /**
     * Return a single string representation of the field value,
     * regardless of the type.  This works to get a string representation
     * of primitive types like integer, decimal, text as a string but
     * will only return the name of more complex types like references.
     *
     * @return String representation of value for field
     */
    @Override
    public String getValueAsString() {
        // parse date using date parser for display
        ContentDateParser parser = getValue().getDateParser();
        return parser.getDisplayString(ContentDateDisplayType.Date);

    }

}
