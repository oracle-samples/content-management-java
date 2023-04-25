/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.sdk.model.digital;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.oracle.content.sdk.model.AssetLink;
import com.oracle.content.sdk.model.AssetLinksObject;

/**
 * The fixed "field" structure for a Digital Asset which contains info such
 * as size, metadata, renditions, etc.
 */
@SuppressWarnings({"WeakerAccess","unused"})
public class DigitalAssetFields extends AssetLinksObject {


    // string to look for in mime type to determine if image
    private final static String MIME_TYPE_IMAGE = "image";


    /**
     * This object only contains "links"
     */
    public static class NativeLinks extends AssetLinksObject {
    }

    @SerializedName("metadata")
    @Expose
    private DigitalAssetMetadata metadata;
    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("native")
    @Expose
    private NativeLinks nativeLinks;
    @SerializedName("renditions")
    @Expose
    private List<DigitalAssetRendition> renditions = null;
    @SerializedName("advancedVideoInfo")
    @Expose
    private AdvancedVideoInfo advancedVideoInfo;
    @SerializedName("mimeType")
    @Expose
    private String mimeType;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("fileType")
    @Expose
    private String fileType;

    public DigitalAssetMetadata getMetadata() {
        return metadata;
    }

    public Integer getSize() {
        return size;
    }

    public NativeLinks getNative() {
        return nativeLinks;
    }

    public List<DigitalAssetRendition> getRenditions() {
        return renditions;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getVersion() {
        return version;
    }

    public String getFileType() {
        return fileType;
    }

    public AdvancedVideoInfo getAdvancedVideoInfo() { return advancedVideoInfo; }


    /**
     * Determine whether it's an image by looking at mediaType
     *
     * @return true if image
     */
    public Boolean isImage() {
        return mimeType != null && mimeType.contains(MIME_TYPE_IMAGE);
    }

    public String getNativeDownloadUrl() {
        return getRenditionUrl(RenditionType.Native);
    }

    public String getRenditionUrl(RenditionType rendition) {
        return getRenditionUrl(rendition.getName());
    }

    /**
     * Get rendition url based on string value.
     * @param rendition Should match string value from {@link RenditionType}
     * @return null if no rendition found, else the url to the rending
     */
    public String getRenditionUrl(String rendition) {
        String downloadUrl = null;
        // if this is a full digital asset with fields, get the
        // download url from the "native" link

        // native rendition? - special case
        if (rendition.equals(RenditionType.Native.getName())) {
            DigitalAssetFields.NativeLinks nativeLinks = getNative();
            if (nativeLinks != null && nativeLinks.getLinks() != null) {
                // "native" just has a single link, use the first one
                AssetLink link = nativeLinks.getLinks().get(0);
                downloadUrl = link.getHref();
            }
        } else {
            DigitalAssetRendition digitalAssetRendition = getRendition(rendition);
            if (digitalAssetRendition != null) {
                // just use the first rendition or "jpg" if matches
                DigitalAssetRendition.RenditionFormat format =  digitalAssetRendition.getBestMatchingFormat("jpg");
                if (format != null) {
                    downloadUrl = format.getDownloadUrl();
                }
            }
        }

        return downloadUrl;
    }

    /**
     * Get rendition based on string value.
     * @param rendition Should match string value from {@link DigitalAssetRendition}
     * @return null if no rendition found, else the the rendition type
     */
    public DigitalAssetRendition getRendition(String rendition) {
        // if there are no renditions, just return null
        if (renditions == null || renditions.size() == 0) {
            return null;
        }

        // find matching rendition in the list of renditions
        for (DigitalAssetRendition assetRendition : renditions) {
            if (rendition.equals(assetRendition.getName())) {
                return assetRendition;
            }
        }
        return null;
    }


    /**
     * Search for a digital rendition based on critera.
     *
     * @param renditionCriteria critera to search for rendition
     * @return matching rendition or null
     */
    public DigitalAssetRendition.RenditionFormat getPreferredRendition(DigitalAssetPreferredRenditionCriteria renditionCriteria) {
        // if there are no renditions, just return null
        if (renditions == null || renditions.size() == 0) {
            return null;
        }

        DigitalAssetRendition.RenditionFormat renditionFormat = null;

        for (DigitalAssetRendition assetRendition : renditions) {
            DigitalAssetRendition.RenditionFormat format = assetRendition.getBestMatchingFormat(renditionCriteria.desiredFormat);
            // start with the first format
            if (renditionFormat == null) {
                renditionFormat = format;
            } else {
                int width = format.getWidth();
                int height = format.getHeight();

                // see if this rendition better matches the desired criteria
                if (renditionCriteria.searchForSmallest) {
                    boolean isSmaller = format.getWidth() <= width || format.getHeight() <= height;
                    // if the size of this format is smaller
                    if (renditionCriteria.minHeight == 0 && isSmaller) {
                        renditionFormat = format;
                    } else {
                        // it's smaller, but only use if above the min
                        if (width > renditionCriteria.minWidth && height > renditionCriteria.minHeight) {
                            renditionFormat = format;
                        }
                    }
                } else if (renditionCriteria.searchForLargest) {
                    boolean isLarger = format.getWidth() >= width || format.getHeight() >= height;
                    // if the size of this format is larger
                    if (renditionCriteria.maxHeight == 0 && isLarger) {
                        renditionFormat = format;
                    } else {
                        // it's larger, but only use if below the max
                        if (width < renditionCriteria.maxWidth && height < renditionCriteria.maxHeight) {
                            renditionFormat = format;
                        }
                    }

                }
            }
        }

        return renditionFormat;
    }

}
