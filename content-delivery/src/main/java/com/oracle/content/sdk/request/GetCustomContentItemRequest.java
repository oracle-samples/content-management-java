/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */

package com.oracle.content.sdk.request;

import com.oracle.content.sdk.ContentDeliveryClient;
import com.oracle.content.sdk.model.field.ContentField;
import com.oracle.content.sdk.model.item.ContentItem;
import com.oracle.content.sdk.request.core.ContentAssetRequest;
import com.oracle.content.sdk.util.CustomItemAnnotation;
import com.google.gson.JsonElement;

/**
 * Request class used to request a single content item based on a specified id,
 * ane will parse the custom fields into a class with annotated fields.  For
 * example, you could define a custom model that extends from {@link ContentItem}
 * and annotate each field with the custom field value.  The type of the field
 * should match the content item type.  Here is an example of a custom class
 * with annotated fields:
 *  <pre>   {@code
 *     {@literal @}CustomContentType("content_type")
 *     public class CustomContentItem extends ContentItem {
 *
 *         {@literal @}CustomContentField("text-field")
 *         String text;
 *
 *         {@literal @}CustomContentField("date-field")
 *         ContentFieldDate dateField;
 *
 *         {@literal @}CustomContentField("decimal-field")
 *         ContentFieldDecimal decimalField;
 *        }
 * }</pre>
 *
 * Each field in the custom class should be either a String value or a ContentField type that
 * extends from {@link ContentField}.  Then you can
 * reference the class as shown in the example below to get the item and populate the custom
 * class model.
 *
 * <pre>   {@code
 *
 * // make call to get the item by id with the custom class
 *  GetCustomContentItemRequest request =
 *        new GetCustomContentItemRequest(clientAPI, CustomContentItem.class, itemID);
 *
 * // synchronous call to fetch the item
 *  ContentResponse<CustomContentItem> response = request.fetch();
 *
 * if (response.isSuccess()) {
 *     // custom content item from result
 *     CustomContentItem item = response.getResult();
 * }
 *
 * } </pre>
 */
@SuppressWarnings({"WeakerAccess","unused","unchecked"})
public class GetCustomContentItemRequest<C extends ContentItem> extends ContentAssetRequest<GetCustomContentItemRequest, C> {

    /**
     * Create request to get a content item for the given id.
     *
     * @param client A valid delivery client
     * @param objectClass object class for custom model
     * @param id A valid content item id
     * @param idType id or slug
     */
    public GetCustomContentItemRequest(ContentDeliveryClient client, Class objectClass, String id, IdType idType) {
        super(client, objectClass, id, idType);
    }

    // same as above but defaults to ID
    public GetCustomContentItemRequest(ContentDeliveryClient client, Class objectClass, String id) {
        this(client, objectClass, id, IdType.ID);
    }

    // override to do custom deserialization
    @SuppressWarnings("unchecked")
    protected C deserializeObject(JsonElement jsonElement) {

        C item =  super.deserializeObject(jsonElement);

        CustomItemAnnotation<C> itemAnnotation =
                new CustomItemAnnotation(objectClass);

        // verify the type matches any annotation
        itemAnnotation.verifyTypeMatch(item.getType());

        // parse the annotation fields into the 'item'
        itemAnnotation.parseAnnotationFields(item);

        return item;
    }

}

