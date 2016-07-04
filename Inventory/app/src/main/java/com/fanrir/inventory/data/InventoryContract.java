package com.fanrir.inventory.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Eisdrachl on 04.07.2016.
 */
public class InventoryContract {
    public static final String CONTENT_AUTHORITY = "com.fanrir.inventory.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.fanrir.inventory.app/inventory/ is a valid path for
    // looking at inventory.
    public static final String PATH_INVENTORY = "inventory";

    /* Inner class that defines the table contents of the inventory table */
    public static final class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INVENTORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        // Table name
        public static final String TABLE_NAME = "inventory";

        // The product name string is what will define the product
        public static final String COLUMN_NAME = "name";

        // The product image will illustrate the product
        public static final String COLUMN_IMAGE = "image";

        // Shows how much a product costs
        public static final String COLUMN_PRICE = "price";

        // Shows how much times this product is available
        public static final String COLUMN_QUANTITY = "quantity";

        // Shows how often the product has been sold
        public static final String COLUMN_SOLD = "sold";

        // The supplier name
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";

        // The supplier mail
        public static final String COLUMN_AVAILABLE = "supplier_mail";

        // Shows if the product is available: 0 not available, 1 available
        public static final String COLUMN_SUPPLIER_MAIL = "available";


        public static Uri buildInventoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildInventoryUri() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildInventoryUri(String name) {
            return CONTENT_URI.buildUpon().appendPath(name).build();
        }

        public static String getNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
}
