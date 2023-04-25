/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.mobile.content.sdk;

/**
 * Global config settings for all unit tests
 */
@SuppressWarnings("unused")
public class Config {

    /**
     * The idea behind this setting is that if .json files have already been generated for mock responses
     * it can just be set to MOCK_TEST.  If there is an active server that has the sdk unit test seeded
     * data, LIVE_TEST_CAPTURE_JSON can be used to generate the .json files to be used for the MOCK_TEST.
     */
    enum MODE {
        LIVE_TEST,                  // run against a live server
        LIVE_TEST_CAPTURE_JSON,     // run against a live server and generate .json files for mock test
        MOCK_TEST                   // run locally using mock server responses from LIVE_TEST_CAPTURE_JSON
    }

    // --- DO NOT change the TEST_MODE.  Only MOCK_TEST is publicly supported ---
    static MODE TEST_MODE = MODE.MOCK_TEST;

    static class TestServer {
        TestServer(String url, String channelToken) {
            this.url = url;
            this.channelToken = channelToken;
        }
        final String url;
        final String channelToken;
    }

    static TestServer MOCK_SERVER =
            new TestServer(
            "http://mockserver",
            "mock_channel_token");

    // servers to use for tests
    static TestServer TEAM_SERVER = MOCK_SERVER;
    static TestServer TEAM_SERVER_CDA = MOCK_SERVER;

    static TestServer DAILY_MASTER = MOCK_SERVER;

    static TestServer BILLING_SERVER = MOCK_SERVER;

    static TestServer BLOG_SAMPLE_SERVER = MOCK_SERVER;

    static TestServer CDA_SERVER = MOCK_SERVER;
    static TestServer TEST_SERVER = TEAM_SERVER;

    // creds used for getting channel token from server in MiscTests
    static String USER_NAME = "test_user";
    static String PASSWORD = "test_password";

    // published channel name for the SDK unit tests
    static String CHANNEL_NAME = "test_channel";

    static {
        System.out.println("INIT BLOCK:" + TEST_MODE);
    }
}


