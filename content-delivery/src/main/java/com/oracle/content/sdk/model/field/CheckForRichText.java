/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.field;

/**
 * For fields that might have rich text content to check, implement this interface.
 */
public interface CheckForRichText {
     boolean isRichText();
}

