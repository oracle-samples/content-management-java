/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.digital;


/**
 * For use in {@link DigitalAsset#getPreferredRendition(DigitalAssetPreferredRenditionCriteria)} when
 * searching for preferred renditions with given criteria.
 */
public class DigitalAssetPreferredRenditionCriteria {

    // preferred matching format
    String desiredFormat = "jpg";

    // to search for smallest (default true)
    boolean searchForSmallest = true;

    // to search for largest (default false)
    boolean searchForLargest = false;

    // preferred min width/height
    int minWidth = 0;
    int minHeight = 0;

    // preferred max width/height
    int maxWidth = 0;
    int maxHeight = 0;

    /**
     * Set the desired format string to search for in renditions.  Default is "jpg"
     *
     * @param format Desired format string to search for.
     */
    public void setDesiredFormat(String format) {
        desiredFormat = format;
    }

    /**
     * Call this to search for the smallest rendition that is larger than preferred
     * minimum width and height.  If set to 0, it will return the smallest size.
     *
     * @param preferredMinWidth Search for smallest rendition, but preferred width about this minimum.
     * @param preferredMinHeight Search for smallest rendition, but preferred height above this minimum.
     */
    public void searchForSmallest(int preferredMinWidth, int preferredMinHeight) {
        searchForSmallest = true;
        searchForLargest = false;
        minWidth = preferredMinWidth;
        minHeight = preferredMinHeight;
    }

    /**
     * Call this to search for the largest rendition that is smaller than preferred
     * maximum width and height.  If set to 0, it will return the largest size.
     *
     * @param preferredMaxWidth Search for largest rendition, but preferred width below this maximum.
     * @param preferredMaxHeight Search for largest rendition, but preferred height below this maximum.
     */
    public void searchForLargest(int preferredMaxWidth, int preferredMaxHeight) {
        searchForLargest = true;
        searchForSmallest = false;
        maxWidth = preferredMaxWidth;
        maxHeight = preferredMaxHeight;
    }


}
