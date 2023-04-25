/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.request.core;

import org.jetbrains.annotations.NotNull;

import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.model.AssetObject;

/**
 * Base class for SDK requests that involve a request by id or slug id.
 */
@SuppressWarnings({"unused,WeakerAccess"})
public abstract class ContentRequestById<T extends ContentRequestById, C extends AssetObject> extends ContentRequest<T, C>{


    // what the id represents (item id or slug id)
    public enum IdType{ ID, SLUG}

    // type of id request
    final protected IdType idType;

    // id for item
    final protected String id;


    protected ContentRequestById(
            @NotNull ContentDeliveryClient client,
            @NotNull Class objectClass,
            @NotNull String id,
            IdType idType

    ) {
        super(client, objectClass);
        this.id = id;
        this.idType = idType;
    }
}
