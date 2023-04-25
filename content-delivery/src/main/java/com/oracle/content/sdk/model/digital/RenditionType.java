/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.model.digital;

/**
 * Common digital asset rendition types.
 */
public enum RenditionType {

    // the native file, also referenced as "Original"
    Native("native"),

    // common rendition values for the name field
    Thumbnail("Thumbnail"),
    Small("Small"),
    Medium("Medium"),
    Large("Large"),
    Strip("Strip"),
    Unknown("");

    // name representation for the rendition
    final private String name;

    RenditionType(String name) {
        this.name = name;
    }

    /**
     * Returns the string representation for the rendition name.
     *
     * @return string for rendition name
     */
    public String getName() {
        return name;
    }

    /**
     * Given a rendition name from a string value, return the equivalent enum.
     * If the rendition is not a common/known enum value this will return type Unknown.
     *
     * @param name Raw rendition string to match to a known enum value
     * @return matching rendition or Uknown
     */
    public static RenditionType getRenditionFromName(String name) {

        for(RenditionType rendition : RenditionType.values()) {
            if (rendition.name.equals(name)) {
                return rendition;
            }
        }

        return Unknown;
    }

}
