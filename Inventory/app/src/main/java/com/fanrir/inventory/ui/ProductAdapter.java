package com.fanrir.inventory.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fanrir.inventory.R;
import com.fanrir.inventory.Utility;
import com.fanrir.inventory.sync.AddProductTask;
import com.fanrir.inventory.sync.SellProductTask;

/**
 * Created by Eisdrachl on 04.07.2016.
 */
public class ProductAdapter extends CursorAdapter {
    public static final String LOG_TAG = ProductAdapter.class.getSimpleName();

    public ProductAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_product, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        final String productName = cursor.getString(InventoryFragment.COL_INVENTORY_NAME);;
        TextView nameTextView = (TextView) view.findViewById(R.id.name_text_view);
        nameTextView.setText(productName.toUpperCase());

        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_text_view);
        quantityTextView.setText(Integer.toString(cursor.getInt(InventoryFragment.COL_INVENTORY_QUANTITY)));

        TextView priceTextView = (TextView) view.findViewById(R.id.price_text_view);
        priceTextView.setText(context.getString(R.string.dollar_sign) + cursor.getString(InventoryFragment.COL_INVENTORY_PRICE));

        Button sellBtn = (Button) view.findViewById(R.id.sell_button);
        sellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start sell task for one of the product
                int quantitySold = 1;
                SellProductTask sellProductTask = new SellProductTask(context, productName, quantitySold);
                sellProductTask.execute();
            }
        });
    }

}
