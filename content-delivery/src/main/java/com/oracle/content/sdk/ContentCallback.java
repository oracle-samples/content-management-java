/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk;

import com.oracle.content.sdk.model.AssetObject;
import com.oracle.content.sdk.request.core.ContentRequest;

/**
 * Callback interface to be implemented by the app to handle the callback when an SDK operation completes.
 * For use in the method {@link ContentRequest#fetchAsync(ContentCallback)}
 */

public interface ContentCallback<C extends AssetObject> {
    /**
     * Implement to handle the response to the SDK call.  See {@link ContentResponse} for
     * more detail.
     *
     * @param response - The {@link ContentResponse} object containing results of call
     */
    void onResponse(ContentResponse<C> response);
}
