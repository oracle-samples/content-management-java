/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.util;

import com.oracle.content.sdk.model.field.ContentField;
import com.oracle.content.sdk.model.field.FieldType;
import com.oracle.content.sdk.model.item.ContentItem;
import com.oracle.content.sdk.model.item.CustomContentField;
import com.oracle.content.sdk.model.item.CustomContentType;
import com.oracle.content.sdk.request.GetCustomContentItemRequest;

import java.lang.reflect.Field;
import java.util.logging.Level;

import com.oracle.content.sdk.ContentClient;

/**
 * Utility class for dealing with custom annotations on a class object.  This
 * is used by {@link GetCustomContentItemRequest}
 * This is experimental code.
 */
public class CustomItemAnnotation<C extends ContentItem> {
    private Class objectClass;

    public CustomItemAnnotation(Class objectClass) {
        this.objectClass = objectClass;
    }

    /**
     * Returns the type value specified in the @CustomContentType or null
     * @return type value or null
     */
    public String getCustomType() {
        String typeValue = null;
        if (objectClass.isAnnotationPresent(CustomContentType.class)) {
            CustomContentType typeAnnotation = (CustomContentType) objectClass.getAnnotation(CustomContentType.class);
            typeValue = typeAnnotation.value();
        }
        return typeValue;
    }

    /**
     * Verify any custom type matches the specified type.  If it doesn't match, will log error.
     *
     * @param type type from server
     * @return true if matches annotation, else false.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean verifyTypeMatch(String type) {
        String annotationType = getCustomType();
        if (!type.equals(annotationType)) {
            ContentClient.log(Level.SEVERE, "[custom]", CustomContentType.class.getSimpleName() +
                    " type + '" + annotationType +
                    "' does not match server type:" + type);
            return false;
        }

        return true;
    }

    /**
     * Parse each annotation in the class and retrieve/assign fields.
     *
     * @param item item to parse
     */
    public void parseAnnotationFields(C item) {
        for (Field field: objectClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(CustomContentField.class)) {
                CustomContentField fieldAnnotation = field.getAnnotation(CustomContentField.class);
                Class fieldClass = field.getType();
                String fieldName = fieldAnnotation.value();
                try {
                    FieldType fieldType = FieldType.getFieldType(fieldClass);
                    // if the field specified is a contentfield object...
                    if (fieldType != FieldType.UNKNOWN) {
                        // then we can extract the value directly to the field
                        ContentField fieldObject = item.getFieldFromType(fieldName, fieldType);
                        field.set(item, fieldObject);
                    } else {
                        // if explicit field not specified, "guess" the type
                        ContentField guessField = item.getFieldFromValue(fieldName);
                        if (guessField == null || guessField.getValueAsString().equals("null")) {
                            field.set(item, null);
                        } else {
                            if (fieldClass.getSimpleName().endsWith("String")) {
                                field.set(item, guessField.getValueAsString());
                            } else {
                                ContentClient.log(Level.SEVERE,
                                        "[custom]", "Could not process field:" + fieldName);
                            }
                        }
                    }

                } catch (Exception e) {
                    ContentClient.log(Level.SEVERE,
                            "[custom]", "Exception:" + e);
                }
            }
        }

    }

}
