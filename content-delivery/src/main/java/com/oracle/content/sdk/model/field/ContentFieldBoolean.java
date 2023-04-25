/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.field;


/**
 * This field is a boolean value (true or false).
 */
public class ContentFieldBoolean extends ContentField<Boolean> {

    public ContentFieldBoolean( Boolean value) {
        super(value, FieldType.BOOLEAN);
    }

}
