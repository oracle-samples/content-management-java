/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.taxonomy;

import com.google.gson.JsonElement;
import com.oracle.content.sdk.ContentClient;
import com.oracle.content.sdk.model.PaginatedListResult;

public class TaxonomyCategoryList extends PaginatedListResult<TaxonomyCategory> {

    @Override
    protected TaxonomyCategory deserializeObject(JsonElement jsonElement) {
        return ContentClient.gson().fromJson(jsonElement, TaxonomyCategory.class);
    }

}
