/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.field;

import com.oracle.content.sdk.model.Asset;

public class ContentFieldReference<T extends Asset> extends ContentField<T> {

    ContentFieldReference(T value, FieldType type) {
        super(value, type);
    }

}
