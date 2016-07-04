package com.fanrir.inventory.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fanrir.inventory.R;

import com.fanrir.inventory.data.InventoryContract.InventoryEntry;
import com.fanrir.inventory.ui.MainActivity;

/**
 * Created by Eisdrachl on 04.07.2016.
 */
public class AddProductTask extends AsyncTask<String, Void, Void> {
    public final String LOG_TAG = AddProductTask.class.getSimpleName();

    private final Context mContext;
    private String mName;
    private byte[] mImage;
    private String mPrice;
    private int mQuantity;
    private int mSold;
    private String mSupplierName;
    private String mSupplierMail;
    private int mAvailable;

    public AddProductTask(Context context, String name, byte[] image, String price, int quantity, int sold, String supplierName, String supplierMail, int available) {
        mContext = context;
        this.mName = name;
        this.mImage = image;
        this.mPrice = price;
        this.mQuantity = quantity;
        this.mSold = sold;
        this.mSupplierName = supplierName;
        this.mSupplierMail = supplierMail;
        this.mAvailable = available;
    }

    /**
     * Helper method to handle insertion of a new product in the inventory database.
     */
    long addProduct(String name, byte[] image, String price, int quantity, int sold, String supplierName, String supplierMail, int available) {
        long inventoryId;

        // First, check if the product with this name exists in the db
        Cursor inventoryCursor = mContext.getContentResolver().query(
                InventoryEntry.CONTENT_URI,
                new String[]{InventoryEntry._ID},
                InventoryEntry.COLUMN_NAME + " = ?",
                new String[]{name},
                null);

        if (inventoryCursor.moveToFirst()) {
            int locationIdIndex = inventoryCursor.getColumnIndex(InventoryEntry._ID);
            inventoryId = inventoryCursor.getLong(locationIdIndex);
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues productValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            productValues.put(InventoryEntry.COLUMN_NAME, name);
            productValues.put(InventoryEntry.COLUMN_IMAGE, image);
            productValues.put(InventoryEntry.COLUMN_PRICE, price);
            productValues.put(InventoryEntry.COLUMN_QUANTITY, quantity);
            productValues.put(InventoryEntry.COLUMN_SOLD, sold);
            productValues.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierName);
            productValues.put(InventoryEntry.COLUMN_SUPPLIER_MAIL, supplierMail);
            productValues.put(InventoryEntry.COLUMN_AVAILABLE, available);

            // Finally, insert inventory data into the database.
            Uri insertedUri = mContext.getContentResolver().insert(
                    InventoryEntry.CONTENT_URI,
                    productValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            inventoryId = ContentUris.parseId(insertedUri);
        }

        return inventoryId;
    }

    @Override
    protected Void doInBackground(String... params) {
        addProduct(mName, mImage, mPrice, mQuantity, mSold, mSupplierName, mSupplierMail, mAvailable);
        Log.i(LOG_TAG, mContext.getString(R.string.info_database_add) + mName);
        return null;
    }

}

