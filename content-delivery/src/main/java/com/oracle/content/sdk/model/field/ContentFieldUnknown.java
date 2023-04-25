/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.field;


/**
 * Unknown type will simply contain the raw json value.  Typically, the
 * type will be unknown if the "value" for the item is null.
 */
public class ContentFieldUnknown extends ContentField<String> {

    public ContentFieldUnknown(String value) {
        super(value, FieldType.UNKNOWN);
    }

}
