package com.fanrir.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fanrir.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by Eisdrachl on 04.07.2016.
 */
public class InventoryDbHelper extends SQLiteOpenHelper {

    // Database Version
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // Database Name
    static final String DATABASE_NAME = "inventory.db";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table create statement
        // Create a table to hold products.
        final String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " (" +
                InventoryEntry._ID + " INTEGER PRIMARY KEY," +
                InventoryEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                InventoryEntry.COLUMN_IMAGE + " BLOB NOT NULL, " +
                InventoryEntry.COLUMN_PRICE + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                InventoryEntry.COLUMN_SOLD + " INTEGER NOT NULL, " +
                InventoryEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_SUPPLIER_MAIL + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_AVAILABLE + " INTEGER NOT NULL " +
                " );";

        // creating table
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // updates the schema without wiping data
        db.execSQL("DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME);
        onCreate(db);
    }
}
