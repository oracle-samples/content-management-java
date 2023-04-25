/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.field;


/**
 * Represents a number value as an integer.
 */
public class ContentFieldInteger extends ContentField<Integer> {

    public ContentFieldInteger(Integer value) {
        super(value, FieldType.INTEGER);
    }

}
