/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.field;


/**
 * "text" type of text field.  In the delivery SDK all text fields
 * will be identified as "text" (even if "largetext" type) because
 * there is no way to definitively determine without the content type definition.
 */
public class ContentFieldText extends ContentField<String> implements CheckForRichText {

    public ContentFieldText(String value) {
        super(value, FieldType.TEXT);
    }

    public ContentFieldText(String value, FieldType fieldType) {
        super(value, fieldType);
    }

    /**
     * Does the field contained rich text?
     *
     * @return true if rich text content
     */
    public boolean isRichText() {
        return ContentFieldLargeText.isRichText(value);
    }

}
