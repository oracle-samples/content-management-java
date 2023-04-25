/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.mobile.content.sdk;


import org.junit.Assume;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import com.oracle.content.sdk.ContentResponse;
import com.oracle.content.sdk.model.Asset;
import com.oracle.content.sdk.model.item.ContentItem;
import com.oracle.content.sdk.model.AssetType;
import com.oracle.content.sdk.model.field.ContentField;
import com.oracle.content.sdk.model.field.ContentFieldDecimal;
import com.oracle.content.sdk.model.field.FieldType;
import com.oracle.content.sdk.model.AssetSearchResult;
import com.oracle.content.sdk.request.GetContentItemRequest;
import com.oracle.content.sdk.request.SearchAssetsRequest;
import com.oracle.content.sdk.request.core.SearchQueryBuilder;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Set of tests for testing various SDK search options against menu data.
 */
public class SearchContentItemsMenuTests extends SDKBaseTest {

    static String MENU_ITEM_TYPE = "sdk_menu_item";
    static String SORT_BY_NAME = "name";
    static String MENU_FIELD_ITEM_TYPE = "sdk_menu_item_type";
    static String MENU_FIELD_IMAGE = "sdk_menu_item_image";
    static String MENU_FIELD_PRICE = "sdk_menu_item_price";
    static String MENU_FIELD_CALORIES = "sdk_menu_item_calories";

    static String DEFAULT_LANG = "en-US";


    static int TOTAL_COUNT_ALL_MENUITEMS = 30;
    static int TOTAL_COUNT_ALL_DIGITAL_ASSETS = 22;

    static int PAGINATION_VALUE = 5;

    static String TYPE_DRINKS = "drinks";
    static String TYPE_LUNCH = "lunch";
    static String TYPE_DESSERT = "dessert";
    static String TYPE_BREAKFAST = "breakfast";

    // name of first item
    static String FIRST_ITEM_NAME = "Avocado Smoothie";
    static String FIRST_ITEM_DESC = "Have a healthy avocado drink for lunch";
    static String FIRST_ITEM_TYPE = TYPE_LUNCH;

    // name of last item
    static String LAST_ITEM_NAME = "Yogurt";
    static double LAST_ITEM_PRICE = 6.75;
    static int LAST_ITEM_CALORIES = 845;

    static String FIRST_ITEM_BREAKFAST = "Breakfast Muffin";
    static String FIRST_ITEM_DRINKS_BREAKFAST = "Berry Smoothie";
    static String LAST_ITEM_FIRST5 = "Breakfast Sandwich";

    static String FIRST_ITEM_MIDDLE5 = "Breakfast Smoothie";
    static String LAST_ITEM_MIDDLE5 = "Greens Smoothie";

    static String FIRST_ITEM_RANGE_TEST = "Bagel Sandwich";

    static String testItemID = null;



    @Test
    public void testSearchByTypeOnly() throws Exception {

        // get list of ContentItems  based on the type
        AssetSearchResult searchResult = makeSearchRequest(
                new SearchAssetsRequest(clientAPI).type(MENU_ITEM_TYPE)
        );

        int totalResults = searchResult.getCount();
        assertEquals(TOTAL_COUNT_ALL_MENUITEMS, totalResults);

        assertEquals(TOTAL_COUNT_ALL_MENUITEMS, searchResult.getContentItems().size());
    }

    @Test
    public void testSearchByTypeOnly2() throws Exception {

        SearchAssetsRequest request =
                new SearchAssetsRequest(clientAPI).idList(Arrays.asList("1","2","3"));
        request.clearFilter();
        request.type(MENU_ITEM_TYPE);
        request.noCache();

        // get list of ContentItems  based on the type
        AssetSearchResult searchResult = makeSearchRequest(request);

        int totalResults = searchResult.getCount();
        assertEquals(TOTAL_COUNT_ALL_MENUITEMS, totalResults);

        assertEquals(TOTAL_COUNT_ALL_MENUITEMS, searchResult.getContentItems().size());
    }

    @Test
    public void testSearchByTypeOnlyRxJava() throws Exception {

        Assume.assumeTrue(testMode == Config.MODE.LIVE_TEST);

        // get list of ContentItems  based on the type
        SearchAssetsRequest searchRequest = new SearchAssetsRequest(clientAPI).type(MENU_ITEM_TYPE);
        AssetSearchResult searchResult = searchRequest.observableResult().blockingGet();

        int totalResults = searchResult.getCount();
        assertEquals(TOTAL_COUNT_ALL_MENUITEMS, totalResults);

        assertEquals(TOTAL_COUNT_ALL_MENUITEMS, searchResult.getContentItems().size());
    }

    @Test
    public void testSearchByTypeDigitalAssets() throws Exception {

        // get list of ContentItems  based on the type
        AssetSearchResult searchResult = makeSearchRequest(
                new SearchAssetsRequest(clientAPI).type(AssetType.TYPE_ASSET_IMAGE)
        );

        int totalResults = searchResult.getCount();
        assertEquals(searchResult.getDigitalAssets().size(), totalResults);

        assertTrue(totalResults >= TOTAL_COUNT_ALL_DIGITAL_ASSETS);
    }



    @Test
    public void testSortByNameDescending() throws Exception {

        // sort descending
        AssetSearchResult searchResult = makeSearchRequest(
                new SearchAssetsRequest(clientAPI)
                        .type(MENU_ITEM_TYPE)
                        .sortByField(SORT_BY_NAME)
                        .sortOrderDescending(true)
                 );

        int totalResults = searchResult.getCount();
        assertEquals(TOTAL_COUNT_ALL_MENUITEMS, totalResults);

        // get first item in search list
        Asset firstItem = searchResult.getItems().get(0);

        assertEquals(LAST_ITEM_NAME, firstItem.getName());

        Asset lastItem = searchResult.getItems().get(totalResults - 1);
        assertNotNull(lastItem);

        // check last item name and fields
        assertEquals(FIRST_ITEM_NAME, lastItem.getName());

        // setup for next test
        testItemID = lastItem.getId();
    }

    @Test
    public void testSortByNameAscending1() throws Exception {

        // create search request to return only items by type, ordered by 'name' descending
        // get list of ContentItems  based on the type
        AssetSearchResult searchResult = makeSearchRequest(
                new SearchAssetsRequest(clientAPI)
                .type(MENU_ITEM_TYPE)
                .sortByField(SORT_BY_NAME));


        int totalResults = searchResult.getCount();
        assertEquals(TOTAL_COUNT_ALL_MENUITEMS, totalResults);

        // get first item in search list
        Asset firstItem = searchResult.getItems().get(0);

        assertEquals(FIRST_ITEM_NAME, firstItem.getName());

        assertEquals(FIRST_ITEM_DESC, firstItem.getDescription());

        Asset lastItem = searchResult.getItems().get(totalResults - 1);
        assertNotNull(lastItem);

        // check last item name and fields
        assertEquals(LAST_ITEM_NAME, lastItem.getName());

        // setup for next test
        testItemID = lastItem.getId();
    }

    @Test
    public void testSortByNameAscending2()  {

        // get item by ID used in last test.
        assertNotNull(testItemID);

        // make call to get the last item by id
        ContentResponse<ContentItem> response = makeSDKReqeust(new GetContentItemRequest(clientAPI, testItemID));
        assertTrue(response.isSuccess());
        ContentItem lastItem = response.getResult();
        assertNotNull(lastItem);

        // price field test
        ContentField priceField = lastItem.getFieldFromValue(MENU_FIELD_PRICE);
        assertNotNull(priceField);
        assertEquals(FieldType.DECIMAL, priceField.getType());
        Double priceValue = ((ContentFieldDecimal)priceField).getValue();
        assertEquals(LAST_ITEM_PRICE, priceValue);

        // calories field test
        Integer calsField = lastItem.getIntegerField(MENU_FIELD_CALORIES);
        assertNotNull(calsField);
        assertEquals(LAST_ITEM_CALORIES, calsField.intValue());

    }


    @Test
    public void testFieldsAll() throws Exception {

        // create search request to return only items by type, ordered by 'name'
        // TEST: that "fields=HTTP" will return field info along with search results

        // get list of ContentItems  based on the type
        AssetSearchResult searchResult = makeSearchRequest(new SearchAssetsRequest(clientAPI)
                .type(MENU_ITEM_TYPE)
                .fieldsAll()
                .sortByField(SORT_BY_NAME));

        int totalResults = searchResult.getCount();
        assertEquals(TOTAL_COUNT_ALL_MENUITEMS, totalResults);

        // get first item in search list
        ContentItem firstItem = (ContentItem)searchResult.getItems().get(0);

        assertEquals(FIRST_ITEM_NAME, firstItem.getName());

        assertEquals(FIRST_ITEM_DESC, firstItem.getDescription());

        // get the "menu type" field from fields
        ContentField fieldType = firstItem.getFieldFromValue(MENU_FIELD_ITEM_TYPE);

        assertNotNull(fieldType);

        // make sure it matches "lunch"
        assertEquals(FIRST_ITEM_TYPE, fieldType.getValueAsString());
    }

    @Test
    public void testFieldsSpecific() throws Exception {

        // create search request to return only items by type, ordered by 'name'
        // TEST: that "fields=fields.type" will return field info along with search results

        // get list of ContentItems  based on the type
        AssetSearchResult searchResult = makeSearchRequest(new SearchAssetsRequest(clientAPI)
                .type(MENU_ITEM_TYPE)
                .fields(SearchQueryBuilder.getFieldName(MENU_FIELD_ITEM_TYPE))
                .sortByField(SORT_BY_NAME));

        int totalResults = searchResult.getCount();
        assertEquals(TOTAL_COUNT_ALL_MENUITEMS, totalResults);

        // get first item in search list
        ContentItem firstItem = (ContentItem)searchResult.getItems().get(0);

        // get the "menu type" field from fields
        ContentField fieldType = firstItem.getFieldFromValue(MENU_FIELD_ITEM_TYPE);

        assertNotNull(fieldType);

        // make sure it matches "lunch"
        assertEquals(FIRST_ITEM_TYPE, fieldType.getValueAsString());
    }

    @Test
    public void testFieldsSpecific2() throws Exception {

        // create search request to return only items by type, ordered by 'name'
        // TEST: that "fields=fields.type" will return field info along with search results

        List<String> fieldList = Arrays.asList(MENU_FIELD_ITEM_TYPE, "name", "description");

        // get list of ContentItems  based on the type
        AssetSearchResult searchResult = makeSearchRequest(new SearchAssetsRequest(clientAPI)
                .type(MENU_ITEM_TYPE)
                .fields(fieldList)
                .sortByField(SORT_BY_NAME));

        int totalResults = searchResult.getCount();
        assertEquals(TOTAL_COUNT_ALL_MENUITEMS, totalResults);

        // get first item in search list
        ContentItem firstItem = (ContentItem)searchResult.getItems().get(0);

        // get the "menu type" field from fields
        ContentField fieldType = firstItem.getFieldFromValue(MENU_FIELD_ITEM_TYPE);

        assertNotNull(fieldType);

        // make sure it matches "lunch"
        assertEquals(FIRST_ITEM_TYPE, fieldType.getValueAsString());
    }


    @Test
    public void testPaginationBeginning() throws Exception {

        // create search request to return only items by type with specified limit
        // get list of ContentItems  based on the type with limit of 5
        AssetSearchResult searchResult = makeSearchRequest(new SearchAssetsRequest(clientAPI)
                .type(MENU_ITEM_TYPE)
                .sortByField(SORT_BY_NAME)
                .limit(PAGINATION_VALUE)
                .totalResults(true));

        // but number of items returned should just match pagination value
        int count = searchResult.getCount();
        assertEquals(PAGINATION_VALUE, count);

        // total count all menu items
        assertEquals(TOTAL_COUNT_ALL_MENUITEMS, searchResult.getTotalResults().intValue());

        assertTrue(searchResult.hasMore());

        // get first item in search list
        Asset firstItem = searchResult.getItems().get(0);

        assertEquals(FIRST_ITEM_NAME, firstItem.getName());

        Asset lastItem = searchResult.getItems().get(count - 1);
        assertNotNull(lastItem);

        assertEquals(LAST_ITEM_FIRST5, lastItem.getName());
    }

    @Test
    public void testPaginationMiddle() throws Exception {

        // now do another search for next 5 in list

        AssetSearchResult nextResult = makeSearchRequest(new SearchAssetsRequest(clientAPI)
                .type(MENU_ITEM_TYPE)
                .sortByField(SORT_BY_NAME)
                .offset(PAGINATION_VALUE)
                .language(DEFAULT_LANG)
                .limit(PAGINATION_VALUE));

        assertNotNull(nextResult);
        assertEquals(PAGINATION_VALUE, nextResult.getOffset().intValue());
        assertEquals(20, nextResult.getLimit().intValue());

        // get first item in search list
        Asset firstItem = nextResult.getItems().get(0);
        assertNotNull(firstItem);

        assertEquals(FIRST_ITEM_MIDDLE5, firstItem.getName());

        assertNotNull(nextResult.getItems());
        assertTrue(nextResult.getCount() > 0);

        Asset lastItem = nextResult.getItems().get(nextResult.getCount() - 1);
        assertNotNull(lastItem);

        assertEquals(LAST_ITEM_MIDDLE5, lastItem.getName());
    }

    @Test
    public void testPaginationEnd() throws Exception {
        // search for last 4 in list

        AssetSearchResult lastResult  = makeSearchRequest(new SearchAssetsRequest(clientAPI)
                .type(MENU_ITEM_TYPE)
                .sortByField(SORT_BY_NAME)
                .limit(PAGINATION_VALUE)
                .language(DEFAULT_LANG)
                .offset(15));

        assertNotNull(lastResult);
        assertNotNull(lastResult.getItems());
        assertTrue(lastResult.getCount() > 0);

        Asset finalItem = lastResult.getItems().get(lastResult.getCount()-1);
        assertNotNull(finalItem);

        // check last item name and fields
        assertEquals(LAST_ITEM_NAME, finalItem.getName());

        assertFalse(lastResult.hasMore());

    }

    @Test
    public void testQueryLanguage() throws Exception {

        // get list of ContentItems  based on the type and language (french)
        AssetSearchResult searchResult = makeSearchRequest(new SearchAssetsRequest(clientAPI)
                .type(MENU_ITEM_TYPE)
                .language("fr")
                .fieldsAll()
                .sortByField(SORT_BY_NAME));


        int totalResults = searchResult.getCount();
        // there should be 5 lunch items
        assertEquals(5, totalResults);


        // get first item in search list
        Asset firstItem = searchResult.getItems().get(0);

        assertEquals("Chocolat suprême", firstItem.getName());
        assertEquals("Gâteau au chocolat garni de glace", firstItem.getDescription());

    }

    @Test
    public void testQueryANDField() throws Exception {

        // search by type and for field that matches "Lunch"\
        String searchQuery =
                new SearchQueryBuilder(MENU_ITEM_TYPE).andField(
                        MENU_FIELD_ITEM_TYPE, SearchQueryBuilder.QueryOperator.EQUALS, TYPE_LUNCH).build();

        // get list of ContentItems  based on the type
        AssetSearchResult searchResult = makeSearchRequest(new SearchAssetsRequest(clientAPI)
                .filter(searchQuery)
                .fieldsAll()
                .sortByField(SORT_BY_NAME));


        int totalResults = searchResult.getCount();
        // there should be 5 lunch items
        assertEquals(5, totalResults);


        // get first item in search list
        Asset firstItem = searchResult.getItems().get(0);

        assertEquals(FIRST_ITEM_NAME, firstItem.getName());
        assertEquals(FIRST_ITEM_DESC, firstItem.getDescription());

    }

    @Test
    public void testQueryFieldEquals() throws Exception {

        // get list of ContentItems  based on the type
        AssetSearchResult searchResult = makeSearchRequest(
                new SearchAssetsRequest(clientAPI)
                        .type(MENU_ITEM_TYPE)
                        .fieldEquals(MENU_FIELD_ITEM_TYPE, TYPE_BREAKFAST)
                        .sortByField(SORT_BY_NAME));

        int totalResults = searchResult.getCount();
        // there should be 10 breakfast and snack items
        assertEquals(5, totalResults);

        //logSearchResults(searchResult);

        // get first item in search list
        Asset firstItem = searchResult.getItems().get(0);

        assertEquals(FIRST_ITEM_BREAKFAST, firstItem.getName());

        Asset lastItem = searchResult.getItems().get(totalResults - 1);

        assertEquals(LAST_ITEM_NAME, lastItem.getName());
    }

    @Test
    public void testQueryORField() throws Exception {

        // search by type and for field that matches "Breakfast" OR "drinks"
        String searchQuery =
                new SearchQueryBuilder(MENU_ITEM_TYPE)
                .startGroup(SearchQueryBuilder.QueryOperator.AND)
                .orField(
                    MENU_FIELD_ITEM_TYPE, SearchQueryBuilder.QueryOperator.EQUALS, TYPE_BREAKFAST)
                .orField(
                    MENU_FIELD_ITEM_TYPE, SearchQueryBuilder.QueryOperator.EQUALS, TYPE_DRINKS)
                .endGroup()
                .build();



        // get list of ContentItems  based on the type
        AssetSearchResult searchResult = makeSearchRequest(
                new SearchAssetsRequest(clientAPI)
                .filter(searchQuery)
                .sortByField(SORT_BY_NAME));

        int totalResults = searchResult.getCount();
        // there should be 10 breakfast and snack items
        assertEquals(10, totalResults);

        //logSearchResults(searchResult);

        // get first item in search list
        Asset firstItem = searchResult.getItems().get(0);

        assertEquals(FIRST_ITEM_DRINKS_BREAKFAST, firstItem.getName());

        Asset lastItem = searchResult.getItems().get(totalResults - 1);

        assertEquals(LAST_ITEM_NAME, lastItem.getName());
    }

    @Test
    public void testSearchTypeList() throws Exception {

        String[] typeList= {MENU_ITEM_TYPE, SDKSingleItemTest.DEFAULT_TYPE};

        // get list of ContentItems  based on the type
        AssetSearchResult searchResult = makeSearchRequest(
                new SearchAssetsRequest(clientAPI)
                        .typeList(Arrays.asList(typeList))
                        .sortByField(SORT_BY_NAME));

        int totalResults = searchResult.getCount();
        assertTrue(totalResults >= TOTAL_COUNT_ALL_MENUITEMS);
    }


    @Test
    public void testQueryNumberRange() throws Exception {

        // search by type and for field for calorie fields between range
        String searchQuery =
                new SearchQueryBuilder(MENU_ITEM_TYPE)
                        .startGroup(SearchQueryBuilder.QueryOperator.AND)
                        .andField(
                                MENU_FIELD_CALORIES, SearchQueryBuilder.QueryOperator.GREATER_OR_EQUAL, "700")
                        .andField(
                                MENU_FIELD_CALORIES, SearchQueryBuilder.QueryOperator.LESS_OR_EQUAL, "900")
                        .endGroup()
                        .build();

        // get list of ContentItems  based on the type
        AssetSearchResult searchResult = makeSearchRequest(
                new SearchAssetsRequest(clientAPI)
                .filter(searchQuery)
                .sortByField(SORT_BY_NAME));

        int totalResults = searchResult.getCount();
        // there should be 6 total menu items between the range of 700 to 900 calories
        assertEquals(6, totalResults);

        //logSearchResults(searchResult);

        // get first item in search list
        Asset firstItem = searchResult.getItems().get(0);
        assertEquals(FIRST_ITEM_RANGE_TEST, firstItem.getName());

    }


    static void logSearchResults(AssetSearchResult searchResult) {
        int i = 1;
        for(Asset item : searchResult.getItems()) {
            System.err.println("[" + i++ + "]" + item.toString());
        }
    }
}
