/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.mobile.content.sdk;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;

import com.oracle.content.sdk.ContentResponse;
import com.oracle.content.sdk.model.item.ContentItem;
import com.oracle.content.sdk.model.AssetLink;
import com.oracle.content.sdk.request.GetContentItemRequest;
import com.oracle.content.sdk.request.core.ContentRequestById;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Test for different types of parameters like "links" and "expand"
 */
@SuppressWarnings("unchecked")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GetContentItemParameterTests extends SDKSingleItemTest {


    // values used for slug test
    static String slugId = null;
    static String itemName = null;


    @Test
    public void testGetItemLinks()  {

        // make call to get the item by id
        ContentItem item = getContentItemExpandAll(itemID);

        assertNotNull(item.getLinks());
        AssetLink link = item.getLinks().get(0);
        assertNotNull(link.getHref());
        assertEquals("self", link.getRel());
        assertEquals("GET", link.getMethod());
        assertEquals("application/json", link.getMediaType());

        slugId = item.getSlug();
        itemName = item.getName();

    }

    // test that specifying "links=none" does not return links
    @Test
    public void testGetItemNoLinks()  {


        // make call to get the item by id
        GetContentItemRequest request = new GetContentItemRequest(clientAPI, itemID).linksNone();


        ContentResponse<ContentItem> response = makeSDKReqeust(request);
        assertTrue(response.isSuccess());

        ContentItem item = response.getResult();

        // we expect no links to be returned
        assertTrue(item.getLinks().isEmpty());
    }

    // test that specifying expand with a list of fields
    @Test
    public void testGetItemExpandFieldList()  {

        String[] fieldList = {"sdk-test-menuitem-ref","sdk-test-item-ref-list"};

        // make call to get the item by id
        GetContentItemRequest request =
                new GetContentItemRequest(clientAPI, itemID)
                        .expand(Arrays.asList(fieldList))
                        .linksNone();

        ContentResponse<ContentItem> response = makeSDKReqeust(request);
        assertTrue(response.isSuccess());

        ContentItem item = response.getResult();

        // we expect no links to be returned
        assertTrue(item.getLinks().isEmpty());
    }

    // test that specifying "links=self" does not return links
    @Test
    public void testGetItemLinksSingle() {

        // make call to get the item by id
        GetContentItemRequest request = new GetContentItemRequest(clientAPI, itemID).links("self");

        ContentResponse<ContentItem> response = makeSDKReqeust(request);
        assertTrue(response.isSuccess());

        ContentItem item = response.getResult();

        // expect 1 link to be returned
        assertEquals(1, item.getLinks().size());
    }

    @Test
    public void testZGetItemBySlug() {

        assertNotNull(slugId);

        // make call to get the item by id
        GetContentItemRequest request =
                new GetContentItemRequest(clientAPI, slugId, ContentRequestById.IdType.SLUG);

        ContentResponse<ContentItem> response = makeSDKReqeust(request);
        assertTrue(response.isSuccess());

        ContentItem item = response.getResult();

        // expect id to match
        //assertEquals( itemID, item.getId());
        assertEquals( itemName, item.getName());
        assertEquals( slugId, item.getSlug());
    }



}
