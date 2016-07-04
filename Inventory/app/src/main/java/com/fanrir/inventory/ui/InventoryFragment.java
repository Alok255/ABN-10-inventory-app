package com.fanrir.inventory.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fanrir.inventory.R;
import com.fanrir.inventory.data.InventoryContract;

/**
 * Created by Eisdrachl on 04.07.2016.
 */
public class InventoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 0;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] INVENTORY_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            InventoryContract.InventoryEntry.TABLE_NAME + "." + InventoryContract.InventoryEntry._ID,
            InventoryContract.InventoryEntry.COLUMN_NAME,
            InventoryContract.InventoryEntry.COLUMN_IMAGE,
            InventoryContract.InventoryEntry.COLUMN_PRICE,
            InventoryContract.InventoryEntry.COLUMN_QUANTITY,
            InventoryContract.InventoryEntry.COLUMN_SOLD,
            InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME,
            InventoryContract.InventoryEntry.COLUMN_SUPPLIER_MAIL,
            InventoryContract.InventoryEntry.COLUMN_AVAILABLE
    };

    // These indices are tied to INVENTORY_COLUMNS.  If INVENTORY_COLUMNS changes, these
    // must change.
    static final int COL_INVENTORY_ID = 0;
    static final int COL_INVENTORY_NAME = 1;
    static final int COL_INVENTORY_IMAGE = 2;
    static final int COL_INVENTORY_PRICE = 3;
    static final int COL_INVENTORY_QUANTITY = 4;
    static final int COL_INVENTORY_SOLD = 5;
    static final int COL_INVENTORY_SUPPLIER_NAME = 6;
    static final int COL_INVENTORY_SUPPLIER_MAIL = 7;
    static final int COL_INVENTORY_AVAILABLE = 8;

    /**
     * Adapter for the products
     */
    private ProductAdapter mProductAdapter;

    public InventoryFragment() {
    }

    /**
     * List that stores the products
     */
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.inventory, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            // Create a new intent to open the {@link AddProductActivity}
            Intent addProductIntent = new Intent(getActivity(), AddProductActivity.class);

            // Start the new activity
            startActivity(addProductIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inventory, container, false);

        //create Adapter for product list
        mProductAdapter = new ProductAdapter(getActivity(), null, 0);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.products_list_view);
        View emptyView = rootView.findViewById(R.id.products_empty);
        mListView.setEmptyView(emptyView);
        mListView.setAdapter(mProductAdapter);

        // clicking on an inventory item opens details of the product
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    // Create a new intent to open the {@link DetailActivity}
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(InventoryContract.InventoryEntry.buildInventoryUri(cursor.getString(COL_INVENTORY_NAME)));
                    // Start the new activity
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // Refresh after Deletion of Product from DetailActivity
    @Override
    public void onResume() {
        getLoaderManager().restartLoader(INVENTORY_LOADER, null, this);
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri inventoryUri = InventoryContract.InventoryEntry.buildInventoryUri();

        return new CursorLoader(getActivity(),
                inventoryUri,
                INVENTORY_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mProductAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mProductAdapter.swapCursor(null);
    }
}
