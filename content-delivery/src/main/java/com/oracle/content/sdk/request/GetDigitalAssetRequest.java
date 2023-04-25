/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.request;

import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.model.digital.DigitalAsset;
import com.oracle.content.sdk.request.core.ContentAssetRequest;

/**
 * Request class used to request a single digital asset based on a specified id.
 * The id is a required parameter that must be specified when creating the request.
 * <pre>   {@code
 *
 * // create request for single digital asset based on the id
 * GetDigitalAssetRequest request = new GetDigitalAssetRequest(deliveryClient, assetId);
 *
 * } </pre>
 */
public class GetDigitalAssetRequest extends ContentAssetRequest<GetDigitalAssetRequest, DigitalAsset> {

    /**
     * Create request to get a digital asset for the given id.
     *
     * @param client A valid delivery client
     * @param id A valid content item id
     */
    public GetDigitalAssetRequest(ContentDeliveryClient client, String id) {
        super(client, DigitalAsset.class, id, IdType.ID);
    }


}

