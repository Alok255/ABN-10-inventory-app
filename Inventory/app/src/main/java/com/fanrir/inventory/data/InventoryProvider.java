package com.fanrir.inventory.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.fanrir.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by Eisdrachl on 04.07.2016.
 */
public class InventoryProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private InventoryDbHelper mOpenHelper;

    static final int INVENTORY = 100;
    static final int INVENTORY_WITH_NAME = 101;

    private static final SQLiteQueryBuilder sInventoryByNameQueryBuilder;

    static{
        sInventoryByNameQueryBuilder = new SQLiteQueryBuilder();

        sInventoryByNameQueryBuilder.setTables(
                InventoryEntry.TABLE_NAME
        );
    }

    //inventory.name = ?
    private static final String sInventoryNameSelection =
            InventoryEntry.TABLE_NAME+
                    "." + InventoryEntry.COLUMN_NAME + " = ? ";

    private Cursor getInventoryByName(Uri uri, String[] projection, String sortOrder) {
        String name = InventoryEntry.getNameFromUri(uri);

        String[] selectionArgs = new String[]{name};
        String selection = sInventoryNameSelection;

        return sInventoryByNameQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = InventoryContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, InventoryContract.PATH_INVENTORY, INVENTORY);
        matcher.addURI(authority, InventoryContract.PATH_INVENTORY + "/*", INVENTORY_WITH_NAME);

        return matcher;
    }

    /*
        We just create a new InventoryDbHelper for later use here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new InventoryDbHelper(getContext());
        return true;
    }

    //Here's where you'll code the getType function that uses the UriMatcher.
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case INVENTORY:
                return InventoryEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "inventory"
            case INVENTORY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "inventory/*"
            case INVENTORY_WITH_NAME: {
                retCursor = getInventoryByName(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case INVENTORY: {
                long _id = db.insert(InventoryEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = InventoryEntry.buildInventoryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case INVENTORY:
                rowsDeleted = db.delete(
                        InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case INVENTORY:
                rowsUpdated = db.update(InventoryEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
