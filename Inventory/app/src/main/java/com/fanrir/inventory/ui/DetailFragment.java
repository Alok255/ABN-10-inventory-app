package com.fanrir.inventory.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanrir.inventory.R;
import com.fanrir.inventory.Utility;
import com.fanrir.inventory.data.InventoryContract;
import com.fanrir.inventory.sync.DeleteProductTask;
import com.fanrir.inventory.sync.ReceiveShipmentTask;
import com.fanrir.inventory.sync.SellProductTask;

/**
 * Created by Eisdrachl on 04.07.2016.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final int DETAIL_LOADER = 0;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                INVENTORY_COLUMNS,
                null,
                null,
                null
        );
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) {
            return;
        }

        ImageView imageView = (ImageView) getView().findViewById(R.id.product_image);
        byte[] image_bytes = data.getBlob(DetailFragment.COL_INVENTORY_IMAGE);
        Utility.loadBitmap(imageView, image_bytes, 300, 300);

        final String productName = data.getString(DetailFragment.COL_INVENTORY_NAME);
        TextView nameTextView = (TextView) getView().findViewById(R.id.name_text_view);
        nameTextView.setText(data.getString(DetailFragment.COL_INVENTORY_NAME).toUpperCase());

        TextView supplierTextView = (TextView) getView().findViewById(R.id.supplier_text_view);
        supplierTextView.setText(data.getString(DetailFragment.COL_INVENTORY_SUPPLIER_NAME));

        TextView priceTextView = (TextView) getView().findViewById(R.id.price_text_view);
        priceTextView.setText(getString(R.string.dollar_sign) + data.getString(DetailFragment.COL_INVENTORY_PRICE));

        TextView quantityTextView = (TextView) getView().findViewById(R.id.quantity_text_view);
        quantityTextView.setText(Integer.toString(data.getInt(DetailFragment.COL_INVENTORY_QUANTITY)));

        TextView soldTextView = (TextView) getView().findViewById(R.id.sold_text_view);
        soldTextView.setText("Sold " + Integer.toString(data.getInt(DetailFragment.COL_INVENTORY_SOLD)));

        Button sellBtn = (Button) getView().findViewById(R.id.minus_button);
        sellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start sell task for one of the product
                int quantitySold = 1;
                SellProductTask sellProductTask = new SellProductTask(getActivity(), productName, quantitySold);
                sellProductTask.execute();
            }
        });

        Button receiveBtn = (Button) getView().findViewById(R.id.plus_button);
        receiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start receive task for one of the product
                int quantityReceived = 1;
                ReceiveShipmentTask receiveProductTask = new ReceiveShipmentTask(getActivity(), productName, quantityReceived);
                receiveProductTask.execute();
            }
        });

        Button orderBtn = (Button) getView().findViewById(R.id.order_button);
        final String[] mailAddress = new String[]{data.getString(DetailFragment.COL_INVENTORY_SUPPLIER_MAIL)};
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Email the supplier for an order
                String subject = getActivity().getString(R.string.order_of) + productName;

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, mailAddress);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        Button deleteBtn = (Button) getView().findViewById(R.id.delete_button);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DeleteAlertDialogStyle);
                builder.setMessage(R.string.dialog_delete_message)
                        .setTitle(R.string.dialog_delete_title);

                // Add the buttons
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked YES button
                        // Start delete task for the product
                        DeleteProductTask deleteProductTask = new DeleteProductTask(getActivity(), productName);
                        deleteProductTask.execute();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void deletedProduct() {
        getActivity().onBackPressed();
    }
}
