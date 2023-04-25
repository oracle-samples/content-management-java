/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.mobile.content.sdk;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

import com.oracle.content.sdk.ContentClient;
import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.ContentError;
import com.oracle.content.sdk.ContentLogging;
import com.oracle.content.sdk.ContentResponse;
import com.oracle.content.sdk.ContentSDK;
import com.oracle.content.sdk.ContentSettings;
import com.oracle.content.sdk.model.item.ContentItem;
import com.oracle.content.sdk.model.AssetSearchResult;
import com.oracle.content.sdk.model.digital.DigitalAsset;
import com.oracle.content.sdk.request.core.ContentRequest;
import com.oracle.content.sdk.request.GetContentItemRequest;
import com.oracle.content.sdk.request.GetDigitalAssetRequest;
import com.oracle.content.sdk.request.SearchAssetsRequest;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


/**
 * Base class for all SDK tests.  The unit tests are all based on .json file responses that were
 * captured against a live server.  The tests are configured by default or un in MOCK_TEST mode,
 * using the previously captured .json responses againast a mock server.
 */
@SuppressWarnings("unchecked")
public class SDKBaseTest {

    // initialize/override the test mode if specified as command-line param
    static {
        String testMode = System.getProperty("unitTestMode");
        if (testMode != null) {
            System.out.println(">>>>>>> Override unitTestMode = " + testMode);
            if (testMode.contains("Mock")) {
                Config.TEST_MODE = Config.MODE.MOCK_TEST;
            } else if (testMode.contains("Live")) {
                Config.TEST_MODE = Config.MODE.LIVE_TEST;
            }
        }
    }

    // could be overriden by individual tests
    Config.MODE testMode = Config.TEST_MODE;

    // live server data values
    String serverUrl = Config.TEST_SERVER.url;
    String channelToken = Config.TEST_SERVER.channelToken;

    protected ContentDeliveryClient clientAPI;

    // mock server
    private MockWebServer server;


    boolean useCache = false;
    static File cacheDir = null;

    // this will be used for generating the .json file names as it will always
    // be set to the current test name in progress
    private static String currentTestName;

    // current method name, which is used in cases where the method is making
    // a unique call but can be overridden for method using the same call each time.
    private static String currentMethodName;

    // this will get called as each test is run so we can extract the name for .json files
    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            currentTestName = description.getTestClass().getSimpleName();
            currentMethodName = description.getMethodName();
            System.out.println("Starting test: " + currentTestName + "." + currentMethodName);
        }
    };

    // the name of the json file to generate for the test
    private String getJsonFileName() {
        return currentTestName + "." + currentMethodName + ".json";
    }

    @Before
    public void setUp() throws Exception {

        ContentSDK.setLogLevel(ContentLogging.LogLevel.HTTP);

        // mock server mode?
        if (testMode == Config.MODE.MOCK_TEST) {
            initializeMockClient();
        } else {

            // or live server mode?
            initializeLiveDeliveryClient();
        }
    }

    // temporary folder for cache dir
    @ClassRule
    static public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Initialize the mock server for running unit tests against .json files in /resources folder
     */
    private void initializeMockClient() {
        try {
            server = new MockWebServer();
            server.start();
            String mockBaseUrl = server.url("/").url().toString();

            System.out.println("mockServerUrl=" + mockBaseUrl);

            // create instance of client delivery SDK to use for testing
            clientAPI = ContentSDK.createDeliveryClient(mockBaseUrl, channelToken);


        } catch (IOException e) {
            System.out.println("Error staring mock web server!:" + e);
        }

    }


    /**
     * Initial the client against a live server for running test tests.
     */
    private void initializeLiveDeliveryClient() {
        // already initialized?
        if (clientAPI != null)
            return;
        try {

            ContentSettings settings = new ContentSettings().setTimeoutSeconds(5);

            if (useCache) {

                cacheDir = new File(System.getProperty("java.io.tmpdir"));
                //String basePath = temporaryFolder.getRoot().getPath();

                settings.enableCache(cacheDir);

                System.out.println("cacheDir ENABLED=" + cacheDir.toString());
            }

            // create client API we'll use to make calls
            clientAPI = ContentSDK.createDeliveryClient(serverUrl, channelToken, settings);

        } catch (Exception e) {
            System.out.println("error creating delivery client" + e);
        }
    }


    private String getResourceFilePath(String fileName) {
        return "./src/test/resources/" + fileName;
    }




    AssetSearchResult makeSearchRequest(SearchAssetsRequest searchRequest) {
        ContentResponse<AssetSearchResult> response = makeSDKReqeust(searchRequest);
        assertTrue(response.isSuccess());
        return response.getResult();
    }


    // use this to shared the same generated .json for the same id
    ContentItem getContentItem(String id) {
        return getContentItemSharedRequest(new GetContentItemRequest(clientAPI, id));
    }

    ContentItem getContentItemExpandAll(String id) {
        return getContentItemSharedRequest(new GetContentItemRequest(clientAPI, id).expandAll());
    }

    // to avoid dozens of duplicate calls, calling using this method normalized
    // the calls into the same name

    ContentItem getContentItemSharedRequest(GetContentItemRequest request) {

        String sharedMethodName = "getContentItem";
        if (request.getExpand() != null) {
            sharedMethodName += "_" + request.getExpand();
        }

        // don't get from cache for tests
        request.noCache();

        // since we always request the same id, reuse the same name so we
        // don't generate a bunch of identical json files.  If different itemIDs are
        // used in tests, this won't work and should be commented out.
        currentMethodName = sharedMethodName;

        // make call to get the item by id

        ContentResponse<ContentItem> response = makeSDKReqeust(request);
        if (!response.isSuccess()) {
            throw response.getException();
        }

        return response.getResult();
    }


    DigitalAsset getDigitalAssetRequest(String itemID) {
        assertNotNull(itemID);

        GetDigitalAssetRequest request = new GetDigitalAssetRequest(clientAPI, itemID).expandAll();
        request.noCache();

        // since we always request the same id, reuse the same name so we
        // don't generate a bunch of identical json files.  If different itemIDs are
        // used in tests, this won't work and should be commented out.
        currentMethodName = "getDigitalAsset";

        // make call to get the item by id
        ContentResponse<DigitalAsset> response = makeSDKReqeust(request);
        assertTrue(response.isSuccess());
        DigitalAsset item = response.getResult();
        assertNotNull(item);
        return item;
    }

    // default is always synchronous, override in test class for other types like asynch/rxjava
    protected ContentResponse fetchRequest(ContentRequest request) {
        return request.fetch();
    }

    /**
     * Every test that makes an SDK call should call this method with the request.  Depending on the
     * mode it will either make a call to a live server, make live server call and save response in a file,
     * or load file for mockserver response prior to making call.
     */
    @SuppressWarnings("uncheked")
    ContentResponse makeSDKReqeust(ContentRequest request) {
        ContentResponse response = null;

        if (testMode == Config.MODE.MOCK_TEST) {
            // load mock .json file prior to doing call
            try {
                // get body from file
                String responseBody = getResourceFileAsString(getJsonFileName());

                assertNotNull(responseBody);
                int responseCode = 200;
                // check for possible "error" mock response
                if (responseBody.contains("\"detail\"")) {
                    try {
                        ContentError error = ContentClient.gson().fromJson(responseBody, ContentError.class);
                        responseCode = error.getStatus();
                    } catch (Exception e) {
                        System.out.println("Exception =" +e);
                    }
                }

                // setup our mock response
                server.enqueue(new MockResponse()
                        .setResponseCode(responseCode)
                        .setBody(responseBody));

                response = fetchRequest(request);

            } catch (Exception e) {
                System.out.println("Error making mock test call:" + e);
            }
        } else {
            // live test
            response = fetchRequest(request);

            // capture the json output as well for use in future mock tests
            if (testMode == Config.MODE.LIVE_TEST_CAPTURE_JSON) {
                try (FileOutputStream out = new FileOutputStream(getResourceFilePath(getJsonFileName()))) {
                    String output = removeHostReferences(response.getAsJson().toString());
                    out.write( output.getBytes());
                } catch (IOException e) {
                    System.out.println("exception creating mock json file:" + e);
                }
            }
        }

        return response;
    }

    /**
     * Remove any host references in "mock" json.  Just replace with "mock-host".
     */
    private String removeHostReferences(String s) {
        return StringUtils.replace(s, serverUrl, "mock-host");
    }



    /**
     * Helper method to read file data into a string
     */
    private String getResourceFileAsString(String fileName)  {
        try (Source source = Okio.source(new File(getResourceFilePath(fileName)));
            BufferedSource bufferedSource = Okio.buffer(source)) {
            return bufferedSource.readString(Charset.defaultCharset());
        } catch (IOException e) {
            System.out.println("failing getting cached bytes:" + e);
            return null;
        }
    }



    /**
     * Get resource file as byte array (e.g. testing digital assets)
     */
    @SuppressWarnings("SameParameterValue")
    byte[] getResourceFileBytes(String fileName) {

        try (Source source = Okio.source(new File(getResourceFilePath(fileName)));
             BufferedSource bufferedSource = Okio.buffer(source)) {
             return bufferedSource.readByteArray();
        } catch (IOException e) {
            System.out.println("failing getting cached bytes:" + e);
            return null;
        }

    }



    @After
    public void tearDown() throws Exception {
        if (server != null) {
            server.shutdown();
        }
    }
}
