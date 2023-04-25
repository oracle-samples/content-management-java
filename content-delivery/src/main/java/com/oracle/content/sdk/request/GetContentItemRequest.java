/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.request;

import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.model.item.ContentItem;
import com.oracle.content.sdk.request.core.ContentAssetRequest;

/**
 * Request class used to request a single content item based on a specified id.
 * The id is a required parameter that must be specified when creating the request.
 *
 * <pre>   {@code
 *
 * // create request for single content item based on the id and expand all fields
 * GetContentItemRequest request =
 *      new GetContentItemRequest(itemId).expandAll();
 *
 * } </pre>
 */
@SuppressWarnings("unused")
public class GetContentItemRequest extends ContentAssetRequest<GetContentItemRequest, ContentItem> {

    /**
     * Create request to get a content item for the given id.
     *
     * @param client A valid delivery client
     * @param id A valid content item id
     * @param idType type of id request (id or slug)
     */
    public GetContentItemRequest(ContentDeliveryClient client, String id, IdType idType) {
        super(client, ContentItem.class, id, idType);
    }

    // same as above but defaults to ID
    public GetContentItemRequest(ContentDeliveryClient client, String id) {
        this(client, id, IdType.ID);
    }


}

