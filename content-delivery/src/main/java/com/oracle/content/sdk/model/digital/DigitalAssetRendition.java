/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.sdk.model.digital;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.oracle.content.sdk.model.AssetLinksObject;

/**
 * Information for a digital asset rendition, which can also have multiple formats.
 */
@SuppressWarnings({"WeakerAccess","unused"})
public class DigitalAssetRendition extends AssetLinksObject {

    /**
     * A format for a specific rendition.  Generally, these are different images types such as webp or jpg.
     */
    public static class RenditionFormat extends AssetLinksObject {

        @SerializedName("format")
        @Expose
        private String format;
        @SerializedName("size")
        @Expose
        private Integer size;
        @SerializedName("mimeType")
        @Expose
        private String mimeType;
        @SerializedName("metadata")
        @Expose
        private DigitalAssetMetadata metadata;

        /**
         * RenditionType name set by {@link #getBestMatchingFormat(String)}, not part of serialized data.
         */
        private String renditionName;

        /**
         * This will be a value such as "jpg" or "webp"
         *
         * @return the rendition format string
         */
        public String getFormatName() {
            return format;
        }

        // get the containing rendition
        public String getRenditionName() {
            return renditionName;
        }

        public Integer getSize() {
            return size;
        }

        public String getMimeType() {
            return mimeType;
        }

        public DigitalAssetMetadata getMetadata() {
            return metadata;
        }


        /**
         * Get width value (or null if not set).
         *
         * @return width if set
         */
        public Integer getWidth() {
            return metadata.getWidthAsInteger();
        }

        /**
         * Get height value (or null if not set).
         *
         * @return width if set
         */
        public Integer getHeight() {
            return metadata.getHeightAsIntger();
        }

        /**
         * Get the download url, which is first url in the "links" structure.
         *
         * @return download url, or null if not set.
         */
        public String getDownloadUrl() {
            if (links != null && links.size() > 0) {
                return links.get(0).getHref();
            }
            return null;
        }

    }


    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("formats")
    @Expose
    private List<RenditionFormat> formats = null;
    @SerializedName("type")
    @Expose
    private String type;

    /**
     * Get raw name string for the rendition.  See also {@link #getRendition()}
     * @return rendition name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the enum value for the rendition.  If not one of the common renditions, this will return RenditionType.Unknown and
     * {@link #getName()} can get used to get the custom name.
     *
     * @return enum rendition value
     */
    public RenditionType getRendition() {
        return RenditionType.getRenditionFromName(name);
    }

    public List<RenditionFormat> getFormats() {
        return formats;
    }

    /**
     * This will normally be a string such as "responsiveimage"
     *
     * @return rendition type such as "responsiveimage"
     */
    public String getType() {
        return type;
    }

    /**
     * Search for a specific format (e.g. "jpg") but will return the first
     * format in the list if there is not an exact match.
     *
     * @param formatName preferred format name.
     * @return first format in list or format that exactly matches formatName, or null if no formats
     */
    public RenditionFormat getBestMatchingFormat(String formatName) {
        RenditionFormat bestMatch = null;

        if (formats != null && formats.size() > 0) {
            // default to first format
            bestMatch = formats.get(0);

            // but search to see if there is an exact match
            for(RenditionFormat format : formats) {
                if (format.getFormatName().equals(formatName)) {
                    // found a perfect match, don't look through any more formats
                    bestMatch = format;
                    break;
                }
            }
        }

        if (bestMatch != null) {
            bestMatch.renditionName = name;
        }
        return bestMatch;
    }

}
