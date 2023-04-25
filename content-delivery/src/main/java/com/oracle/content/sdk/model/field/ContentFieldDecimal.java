/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.field;

/**
 * Represent a decimal value as a Double.
 */
public class ContentFieldDecimal extends ContentField<Double> {
    public ContentFieldDecimal(Double value) {
        super(value, FieldType.DECIMAL);
    }

    @Override
    public String getValueAsString() {
        String s = super.getValueAsString();
        // remove .0 if it's a whole value
        if (s != null && s.endsWith(".0")) {
            s = s.substring(0, s.length()-2);
        }
        return s;
    }
}
