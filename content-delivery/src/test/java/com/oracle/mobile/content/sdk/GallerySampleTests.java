/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.mobile.content.sdk;

import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.ContentLogging;
import com.oracle.content.sdk.ContentSDK;
import com.oracle.content.sdk.ContentSettings;
import com.oracle.content.sdk.model.taxonomy.Taxonomy;
import com.oracle.content.sdk.model.taxonomy.TaxonomyCategory;
import com.oracle.content.sdk.model.taxonomy.TaxonomyCategoryList;
import com.oracle.content.sdk.model.taxonomy.TaxonomyList;
import com.oracle.content.sdk.request.GetTaxonomiesRequest;
import com.oracle.content.sdk.request.GetTaxonomyCategoriesRequest;

import org.junit.Ignore;
import org.junit.Test;

// specific to gallery sample testing
public class GallerySampleTests {

    static String GALLERY_SERVER = "https://headless.mycontentdemo.com";
    static String GALLERY_CHANNEL = "e0b6421e73454818948de7b1eaddb091";

    @Test
    @Ignore
    public void getTaxonomies() {

        ContentSDK.setLogLevel(ContentLogging.LogLevel.HTTP);
        ContentDeliveryClient deliveryClient =
                ContentSDK.createDeliveryClient(GALLERY_SERVER, GALLERY_CHANNEL, new ContentSettings());
        GetTaxonomiesRequest request = new GetTaxonomiesRequest(deliveryClient)
                .sortByField("name");

        TaxonomyList list = request.fetch().getResult();
        for(Taxonomy taxonomy : list.getItems()) {
            System.out.println("T: " + taxonomy.getName());
            GetTaxonomyCategoriesRequest requestCategories = new GetTaxonomyCategoriesRequest(deliveryClient, taxonomy.getId())
                    .sortByField("name");

            TaxonomyCategoryList categoryList = requestCategories.fetchResult();
            for(TaxonomyCategory taxonomyCategory : categoryList.getItems()) {
                System.out.println("C: " + taxonomyCategory.getName());
            }
        }


    }

    /* expand not supported for taxonomies yet
    @Test
    public void getTaxonomiesExpand() {

        ContentSDK.setLogLevel(ContentLogging.LogLevel.HTTP);
        ContentDeliveryClient deliveryClient =
                ContentSDK.createDeliveryClient(GALLERY_SERVER, GALLERY_CHANNEL, new ContentSettings());
        GetTaxonomiesRequest request = new GetTaxonomiesRequest(deliveryClient)
                .sortByField("name")
                .expand("all");

        TaxonomyList list = request.fetch().getResult();
        for(Taxonomy taxonomy : list.getItems()) {
            System.out.println("T: " + taxonomy.getName());
            for(TaxonomyCategory taxonomyCategory : taxonomy.getCategories().getItems()) {
                System.out.println("C: " + taxonomyCategory.getName());
            }
        }


    }
*/

}
