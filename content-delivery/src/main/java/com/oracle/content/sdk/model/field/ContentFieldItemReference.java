/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.field;

import com.oracle.content.sdk.model.item.ContentItem;


/**
 *  Field is a reference to another content item or digital asset.
 */
@SuppressWarnings({"unused"})
public class ContentFieldItemReference extends ContentFieldReference<ContentItem> {

    public ContentFieldItemReference() {
        this(null);
    }

    public ContentFieldItemReference(ContentItem value) {
        super(value, FieldType.CONTENT_ITEM);
    }



}
