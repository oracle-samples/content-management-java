/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.util;

import org.apache.commons.codec.binary.Base64;

import com.oracle.content.sdk.AuthenticationPolicy;

/**
 * Authentication using basic auth.  For internal testing.
 */
public class AuthenticationBasicAuth extends AuthenticationPolicy {

    // basic auth header
    final private String authHeader;


    public AuthenticationBasicAuth(String userName, String userPassword) {
        this.authHeader = getBasicAuthHeader(userName, userPassword);
    }

    @Override
    public String getAuthHeader() {
        return authHeader;
    }

    /**
     * Get basic auth header value to use for "Authorization"
     *
     * @return basic auth header value
     */
    private String getBasicAuthHeader(String userName, String userPassword) {

        String authString = userName + ":" + userPassword;
        byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
        String authStringEnc = new String(authEncBytes);
        //System.out.println("Base64 encoded auth string: " + authStringEnc);
        return "Basic " + authStringEnc;
    }

}

