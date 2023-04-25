/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.field;


import java.util.List;

/**
 * This field consists of a list of values (strings or numbers stored as strings).
 */
public class ContentFieldTextList extends ContentField<List<String>> implements CheckForRichText {

    public ContentFieldTextList(List<String> value) {
        super(value, isRichText(value)?FieldType.LARGE_TEXT_LIST : FieldType.TEXT_LIST);
    }

    /**
     * Does this text list contain rich text?
     * @return true if the list contains rich text
     */
    public boolean isRichText() {
        return isRichText(value);
    }

    // utility method to determine if list has rich text content
    private static boolean isRichText(List<String> value) {
        return value != null && value.size() > 0 && ContentFieldLargeText.isRichText(value.get(0));
    }

}
