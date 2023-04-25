/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.field;


import java.util.List;

import com.oracle.content.sdk.model.Asset;


/**
 * Represents a list of item references
 */
@SuppressWarnings({"unused"})
public class ContentFieldReferenceList<T extends Asset> extends ContentField<List<ContentFieldReference<T>>> {

    public ContentFieldReferenceList() {this (null);}

    public ContentFieldReferenceList(List<ContentFieldReference<T>> value) {
        super(value, FieldType.REFERENCE_LIST);
    }
}
