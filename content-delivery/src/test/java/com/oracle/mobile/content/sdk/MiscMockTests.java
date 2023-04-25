/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.mobile.content.sdk;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.oracle.content.sdk.ContentError;
import com.oracle.content.sdk.ContentException;
import com.oracle.content.sdk.ContentLogging;
import com.oracle.content.sdk.ContentResponse;
import com.oracle.content.sdk.ContentSDK;
import com.oracle.content.sdk.model.item.ContentItem;
import com.oracle.content.sdk.model.AssetType;
import com.oracle.content.sdk.model.digital.DigitalAsset;
import com.oracle.content.sdk.model.field.ContentFieldInteger;
import com.oracle.content.sdk.model.field.ContentFieldText;
import com.oracle.content.sdk.model.field.FieldType;
import com.oracle.content.sdk.request.GetContentItemRequest;
import com.oracle.content.sdk.request.GetDigitalAssetRequest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * General tests that don't require any connection
 */
public class MiscMockTests extends SDKBaseTest {

    @Before
    public void setUp() throws Exception {

        // only run in mock-mode
        testMode = Config.MODE.MOCK_TEST;
        super.setUp();
    }

    // test to make sure ContentItem is serializable
    @Test
    public void testContentItemSerializable() throws Exception {
        ContentItem item = getContentItem("mock");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(item);

        //De-serialization of object
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bis);
        ContentItem copied = (ContentItem) in.readObject();

        assertEquals(item.getId(), copied.getId());
        assertEquals(item.getName(), copied.getName());
        assertEquals(item.parseContentItemFields().getFieldsMap().size(), copied.parseContentItemFields().getFieldsMap().size());

    }

    // logging configuration
    @Test
    public void loggingPolicyTest() {
        ContentLogging contentLogging =
                new ContentLogging(ContentLogging.LogLevel.INFO,
                        (priority, tag, message) -> System.out.println("[sdk log]" + tag + message));

        assertEquals(ContentLogging.LogLevel.INFO, contentLogging.getLogLevel());
        ContentSDK.setLoggingPolicy(contentLogging);
    }


    // called with invalid or blank server url or channel token
    @Test
    public void testNullServer() {

        try {
            ContentSDK.createDeliveryClient(null, "token");
            fail();
        } catch (ContentException exception) {
            assertEquals(ContentException.REASON.invalidServerUrl, exception.getReason());
            assertNotNull(exception.getVerboseErrorMessage());
        }


    }

    @Test
    public void testEmptyToken() {
        try {

            ContentSDK.createDeliveryClient(serverUrl, "");
            fail();
        } catch(ContentException exception) {
            assertEquals(ContentException.REASON.invalidServerUrl, exception.getReason());
            assertNotNull(exception.getVerboseErrorMessage());
        }

    }

    @Test
    public void testInvalidUrl() {
        try {

            ContentSDK.createDeliveryClient(serverUrl.substring(3), channelToken);
            fail();
        } catch(ContentException exception) {
            assertEquals(ContentException.REASON.invalidServerUrl, exception.getReason());
            assertNotNull(exception.getVerboseErrorMessage());
        }

    }

    // when server has "billing limits" set
    @Test
    public void testBillingLimitError() {
        // make call to get the item will billing limits set
        GetContentItemRequest request = new GetContentItemRequest(clientAPI, "CONTCB861C1A4B694ED6BDECBA2472909C4C");
        ContentResponse response = makeSDKReqeust(request);
        assertFalse(response.isSuccess());
        assertEquals(403, response.getHttpCode());
        ContentError error = response.getException().getContentError();
        assertNotNull(error);
        assertEquals("CEC-CONTENT-001009", error.getOracleErrorCode());
        assertEquals("Limit exceeded", error.getTitle());
        assertTrue(error.getDetail().contains("outbound bandwidth limit"));
        assertEquals(403, error.getStatus().intValue());
    }

    @Test
    public void testDeserializationError() {

        // make call to get the item with malformed response
        GetContentItemRequest request = new GetContentItemRequest(clientAPI, "CORE4F6C3FF470C047A99939DD907AB04887");

        // make call to get the item by id
        ContentResponse response = makeSDKReqeust(request);

        // we expect this to fail
        Assert.assertFalse(response.isSuccess());
        assertEquals(ContentException.REASON.dataConversionFailed, response.getException().getReason());

    }

    // get item that has json field
    @Test
    public void testGetItemWithJsonField() {
        // make call to get the item will billing limits set
        GetContentItemRequest request = new GetContentItemRequest(clientAPI, "COREA3F094D438514BCCAAD53F5A8BF83271");
        ContentResponse response = makeSDKReqeust(request);
        assertTrue(response.isSuccess());

        ContentItem item = (ContentItem)response.getResult();
        assertFalse(item.isDigitalAsset());
        assertEquals("text field contents", item.getTextField("sdk_text_field"));

        // json test
        String json = item.getJsonField("sdk_json_field");
        assertTrue(json.contains("href"));
        System.out.println(item.getJsonField("sdk_json_field").toString());
    }


    // for published custom digital asset
    @Test
    public void testCustomDigitalAsset() {
        GetDigitalAssetRequest request = new GetDigitalAssetRequest(clientAPI, "CONT932EA7A6994A4A00A602A12F59AD0E27");
        ContentResponse response = makeSDKReqeust(request);
        assertTrue(response.isSuccess());

        DigitalAsset asset = (DigitalAsset)response.getResult();
        assert(asset.isDigitalAsset());
        assertEquals("testw", asset.getType());
        assertEquals(AssetType.TYPE_CATEGORY_DIGITAL_ASSET, asset.getTypeCategory());
        assertTrue(asset.getContentType().isDigitalAsset());
        assert(asset.getAssetFields().isImage());
        assertEquals(172677, asset.getSize().intValue());
    }


    // for published CDA with custom attributes
    @Test
    public void testCustomDigitalAssetAttributes() {
        GetDigitalAssetRequest request = new GetDigitalAssetRequest(clientAPI, "CONT2443E2AF0E8A459982420C10CF9AF6BA");
        ContentResponse response = makeSDKReqeust(request);
        assertTrue(response.isSuccess());

        DigitalAsset asset = (DigitalAsset)response.getResult();
        assert(asset.isDigitalAsset());
        assertEquals("Screenshots", asset.getType());
        assertEquals(AssetType.TYPE_CATEGORY_DIGITAL_ASSET, asset.getTypeCategory());
        assertTrue(asset.getContentType().isDigitalAsset());
        assert(asset.getAssetFields().isImage());
        assertTrue(asset.isCustomAssetType());

        ContentFieldText textField = asset.getCustomFieldFromType("text_field_1", FieldType.TEXT);
        assertNotNull(textField);
        assertEquals("text", textField.getValue());

        ContentFieldInteger numberField = asset.getCustomFieldFromType( "number_field_1", FieldType.INTEGER);
        assertNotNull(numberField);
        assertEquals(33, numberField.getValue().intValue());

        // test for field that doesn't exist
        ContentFieldText doesNotExist = asset.getCustomFieldFromType("missing", FieldType.TEXT);
        assertNull(doesNotExist);


    }



}
