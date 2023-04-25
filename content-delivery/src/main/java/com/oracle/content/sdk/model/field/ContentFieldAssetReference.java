/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.field;

import com.oracle.content.sdk.model.digital.DigitalAsset;


/**
 *  Field is a reference to another content item or digital asset.
 */
@SuppressWarnings({"unused"})
public class ContentFieldAssetReference extends ContentFieldReference<DigitalAsset> {

    public ContentFieldAssetReference() {
        this(null);
    }

    public ContentFieldAssetReference(DigitalAsset value) {
        super(value, FieldType.DIGITAL_ASSET);
    }

}
