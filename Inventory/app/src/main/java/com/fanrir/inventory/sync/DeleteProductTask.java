package com.fanrir.inventory.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.fanrir.inventory.R;
import com.fanrir.inventory.data.InventoryContract;
import com.fanrir.inventory.data.InventoryDbHelper;
import com.fanrir.inventory.ui.DetailActivity;
import com.fanrir.inventory.ui.DetailFragment;

/**
 * Created by Eisdrachl on 04.07.2016.
 */
public class DeleteProductTask extends AsyncTask<String, Void, Void> {
    public final String LOG_TAG = DeleteProductTask.class.getSimpleName();

    private static final int DETAIL_LOADER = 0;

    private final Context mContext;
    private String mName;

    public DeleteProductTask(Context context, String name) {
        mContext = context;
        this.mName = name;
    }

    /**
     * Helper method to handle update of a product in the inventory database.
     */
    void deleteProduct(String name) {
        InventoryDbHelper mDbHelper = new InventoryDbHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String selection = InventoryContract.InventoryEntry.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = {name};

        // delete inventory data from the database.
        db.delete(
                InventoryContract.InventoryEntry.TABLE_NAME,
                selection,
                selectionArgs
        );
    }

    @Override
    protected Void doInBackground(String... params) {
        deleteProduct(mName);
        Log.i(LOG_TAG, mContext.getString(R.string.product_selling_done));
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try {
            // Reload current fragment
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DetailFragment.class.getSimpleName());
            df.deletedProduct();
        } catch (Exception e) {
            Log.v(LOG_TAG, "DetailFragment not reloaded");
        }
        Toast.makeText(mContext, mName + mContext.getString(R.string.deleted), Toast.LENGTH_SHORT).show();
    }

    private FragmentManager getSupportFragmentManager() {
        try {
            final DetailActivity activity = (DetailActivity) mContext;

            // Return the fragment manager
            return activity.getSupportFragmentManager();

        } catch (ClassCastException e) {
            Log.d(LOG_TAG, mContext.getString(R.string.error_cant_get_fragment));
        }
        return null;
    }
}