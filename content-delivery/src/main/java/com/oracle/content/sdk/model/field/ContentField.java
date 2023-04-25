/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.field;

import com.oracle.content.sdk.model.AssetObject;

/**
 * Abstract base class for all field types.  T is the type (Boolean, String) of the value of the field.
 */
@SuppressWarnings({"WeakerAccess","unused"})
public abstract class ContentField<T> extends AssetObject {

    final protected T value;

    final protected FieldType fieldType;

    protected ContentField(T value, FieldType fieldType) {
        this.value = value;
        this.fieldType = fieldType;
    }

    /**
     * Return the type for the field.
     *
     * @return type
     */
    public FieldType getType() {
        return fieldType;
    }

    /**
     * Return a single string representation of the field value,
     * regardless of the type.  This works to get a string representation
     * of primitive types like integer, decimal, text as a string but
     * will only return the name of more complex types like references.
     *
     * @return String representation of value for field
     */
    public String getValueAsString() {
        return value.toString();
    }

    /**
     * Get the java object representation of the field.  For example, text
     * would be String, number would be an Integer,  Date would a ContentDate.
     *
     * @return String representation of value for field
     */
    public T getValue() {
        return value;
    }

    /**
     * String representation of the field for debugging purposes,
     * which includes the type.
     *
     * @return string value of the field
     */
    public String toString() {
        return getType() + "::" + getValueAsString();
    }


}
