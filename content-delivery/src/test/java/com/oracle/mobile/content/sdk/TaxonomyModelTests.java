/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.mobile.content.sdk;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

import com.oracle.content.sdk.ContentResponse;
import com.oracle.content.sdk.model.Asset;
import com.oracle.content.sdk.model.item.ContentItem;
import com.oracle.content.sdk.model.AssetSearchResult;
import com.oracle.content.sdk.model.taxonomy.TaxonomyList;
import com.oracle.content.sdk.model.digital.DigitalAsset;
import com.oracle.content.sdk.request.GetTaxonomiesRequest;
import com.oracle.content.sdk.request.SearchAssetsRequest;
import com.oracle.content.sdk.model.taxonomy.TaxonomyCategory;
import com.oracle.content.sdk.model.ItemList;
import com.oracle.content.sdk.model.taxonomy.Taxonomy;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Tests of the taxonomy/category models, mock-only for now
 * based on captured seed data manually created.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaxonomyModelTests extends SDKSingleItemTest {

    // use this same block of constants in mgmt sdk seed file
    private static String TAXONOMY_NAME = "Mobile SDK Taxonomy";
    private static String TAXONOMY_SHORT_NAME = "MOB";
    private static String ITEM_TYPE = "sdk_menu_item";
    private static String ITEM_NAME = "Croissant";
    private static String ITEM_ASSET_REF = "sdk_menu_item_image";
    private static String ITEM_ASSET_NAME = "croissant.jpg";
    private static String[] NODE_NAMES = {"Menu", "Breakfast"};
    private static String[] ITEM_CATEGORY_NAMES = {"Breakfast"};
    private static String[] ASSET_CATEGORY_NAMES = {"Image", "Breakfast"};

    private static String  assetId;

    // save nodeIds to use in search test
    static private List<String> nodeIds = new ArrayList<>();

    @Before
    public void setUp() throws Exception {

        // only run in mock-mode
        testMode = Config.MODE.MOCK_TEST;

        // override the item to use for single item
        singleItemType = ITEM_TYPE;
        singleItemName = ITEM_NAME;

        // get get the item
        super.setUp();
    }


    @Test
    public void a1TestContentItemTaxonomy() {
        ContentItem item = getContentItemExpandAll(itemID);

        // digital asset reference
        final DigitalAsset digitalAsset = item.getDigitalAssetField(ITEM_ASSET_REF);
        assertNotNull(digitalAsset);

        // for a reference, we basically only have id
        assertNotNull(digitalAsset.getId());
        assertFalse(digitalAsset.getId().isEmpty());

        if (testMode.equals(Config.MODE.MOCK_TEST)) {
            assetId = "mock";
        } else {
            assetId = digitalAsset.getId();
        }

        // test taxonomies and categories

        Taxonomy taxonomy = verifyTaxonomy(item.getTaxonomies());

        verifyCategories(ITEM_CATEGORY_NAMES, taxonomy.getCategories().getItems());

        verifyNodes(NODE_NAMES, taxonomy.getCategories().getFirstItem().getNodes());
    }

    @Test
    public void a2TestDigitalAssetTaxonomy(){
        // make call get to get digital asset
        DigitalAsset asset = getDigitalAssetRequest( assetId);
        assertEquals(ITEM_ASSET_NAME, asset.getName());

        Taxonomy taxonomy = verifyTaxonomy(asset.getTaxonomies());

        assertNotNull(taxonomy);
        assertNotNull(taxonomy.getCategories());
        assertEquals(2, taxonomy.getCategories().getItems().size());

        verifyCategories(ASSET_CATEGORY_NAMES, taxonomy.getCategories().getItems());

    }

    // search by node ids "menu" and "breakfast"
    @Test
    public void b1TestSearchByNodeIds(){
        SearchAssetsRequest request =
                new SearchAssetsRequest(clientAPI)
                .taxonomyCategoryNodeIds(nodeIds)
                .fieldsAll();   // required to get taxonomies

        AssetSearchResult result = makeSearchRequest(request);
        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        // verify the taxonomy nodes for all items returned
        for(Asset item : result.getItems()) {
            // assume one taxonomy with one ategory
            assertNotNull(item.getTaxonomies());
            TaxonomyCategory category = item.getTaxonomies().getFirstItem().getCategories().getFirstItem();
            List<TaxonomyCategory.Node> nodeList = category.getNodes();
            //assertEquals(nodeIds.size(), nodeList.size());
            //assertEquals(nodeIds.get(0), nodeList.get(0).getId());
        }
    }

    private static void verifyNodes(String[] nodeNames, List<TaxonomyCategory.Node> nodes) {
        assertNotNull(nodes);
        assertEquals(nodeNames.length, nodes.size());
        for(int i = 0; i < nodeNames.length; i++) {
            TaxonomyCategory.Node node = nodes.get(i);
            assertEquals(nodeNames[i], node.getName());
            assertNotNull(node.getId());
            // save ids to use in search
            nodeIds.add(node.getId());
        }
    }

    private static void verifyCategories(String[] categoryNames, List<TaxonomyCategory> categories) {
        assertNotNull(categories);
        assertEquals(categoryNames.length, categories.size());
        for(int i = 0; i < categoryNames.length; i++) {
            TaxonomyCategory category = categories.get(i);
            //assertEquals(categoryNames[i], category.getName());
            assertNotNull(category.getId());
        }
    }

    private static Taxonomy verifyTaxonomy(ItemList<Taxonomy> taxonomies) {
        assertNotNull(taxonomies);
        assertEquals(1, taxonomies.getItems().size());
        Taxonomy taxonomy = taxonomies.getFirstItem();
        assertEquals(TAXONOMY_NAME, taxonomy.getName());
        assertEquals(TAXONOMY_SHORT_NAME, taxonomy.getShortName());
        assertNotNull(taxonomy.getId());
        return taxonomy;
    }

    @Test
    public void cTestGetTaxonomies() {
        GetTaxonomiesRequest request = new GetTaxonomiesRequest(clientAPI);

        ContentResponse<TaxonomyList> response = makeSDKReqeust(request);
        assertNotNull(response);
        assertTrue(response.isSuccess());

        TaxonomyList taxonomyList = response.getResult();
        assertTrue(taxonomyList.getCount() > 0);
        boolean foundTaxonomy=false;
        // make sure we find our taxonomy in the list
        for(Taxonomy taxonomy : taxonomyList.getItems()) {
            assertNotNull(taxonomy.getName());
            assertNotNull(taxonomy.getShortName());
            System.out.println("Taxonomy=" + taxonomy.getName() + "("
                    + taxonomy.getShortName() + ")");
            if (taxonomy.getName().equals(TAXONOMY_NAME)) {
                foundTaxonomy = taxonomy.getShortName().equals(TAXONOMY_SHORT_NAME);
            }
        }

        assertTrue(foundTaxonomy);
    }

}
