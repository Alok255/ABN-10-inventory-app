package com.fanrir.inventory.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fanrir.inventory.R;
import com.fanrir.inventory.sync.AddProductTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddProductActivity extends AppCompatActivity {

    /** Defines the constants for picking the product image */
    private static final int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    /** This view shows the choosen product image */
    private ImageView mProductImage;

    /** This are the EditTexts the user has to fill out to add a new product */
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierMailEditText;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            //Check user input
            if (isEmpty(mNameEditText) || isEmpty(mPriceEditText) || isEmpty(mQuantityEditText) || isEmpty(mSupplierNameEditText) || isEmpty(mSupplierMailEditText)) {
                Toast.makeText(this, R.string.empty_text_view_error, Toast.LENGTH_SHORT).show();
                return false;
            }
            if (isPriceBadEntry()) {
                mPriceEditText.setError(getString(R.string.error_bad_price_entered));
                return false;
            }
            if (isSupplierMailBadEntry()) {
                mSupplierMailEditText.setError(getString(R.string.error_bad_mail_address_entered));
                return false;
            }

            //Add new product
            String name = mNameEditText.getText().toString();
            byte[] image = getByteArrayFromDrawableResource(mProductImage.getDrawable());
            String price = mPriceEditText.getText().toString();
            int quantity = Integer.parseInt(mQuantityEditText.getText().toString());
            int sold = 0;
            String supplierName = mSupplierNameEditText.getText().toString();
            String supplierMail = mSupplierMailEditText.getText().toString();
            int available = quantity > 0 ? 1 : 0;

            AddProductTask addProductTask = new AddProductTask(this, name, image, price, quantity, sold, supplierName, supplierMail, available);
            addProductTask.execute();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mProductImage = (ImageView) findViewById(R.id.product_image_view);

        Button addImageButton = (Button) findViewById(R.id.add_image_button);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        mNameEditText = (EditText) findViewById(R.id.name_edit_text);
        mPriceEditText = (EditText) findViewById(R.id.price_edit_text);
        mQuantityEditText = (EditText) findViewById(R.id.quantity_edit_text);
        mSupplierNameEditText = (EditText) findViewById(R.id.supplier_name_edit_text);
        mSupplierMailEditText = (EditText) findViewById(R.id.supplier_mail_edit_text);

    }

    /**
     * This method allows to pick an image from camera or gallery
     */
    private void pickImage() {
        final CharSequence[] items = { getString(R.string.camera), getString(R.string.gallery),
                getString(R.string.cancel) };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_an_image));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.camera))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals( getString(R.string.gallery))) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                }
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // set codes when image has been choosen
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    /**
     * Sets the chosen image to the product ImageView
     * @param data the image chosen from gallery
     */
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        mProductImage.setImageBitmap(bm);
    }

    /**
     * This method sets the captured image from camera to the product ImageView
     * @param data the image taken by camera
     **/
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mProductImage.setImageBitmap(thumbnail);
    }

    /**
     * Converts a drawable to an byte array
     * @param drawable The drawable to concert
     * @return the drawable as byte array
     */
    private byte[] getByteArrayFromDrawableResource(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Checks if EditText is empty
     * @param editText the EditText to check
     * @return true if EditText empty else false
     */
    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    /**
     * Checks if mail address contains '@' and '.'
     * @return true if no mail address
     */
    private boolean isSupplierMailBadEntry() {
        String userEntryString = mSupplierMailEditText.getText().toString();
        return !(userEntryString.contains("@") && userEntryString.contains("."));
    }

    /**
     * Checks if price consists only of numbers separated by one '.'
     * @return true if no price
     */
    private boolean isPriceBadEntry() {
        String userEntryString = mPriceEditText.getText().toString();
        String[] parts = userEntryString.split("\\.", 2);
        if (parts.length != 2) {
            return true;
        }
        if (!(parts[0].matches("[0-9]+") && parts[1].matches("[0-9]+") && parts[1].length() == 2)) {
            return true;
        }
        return false;
    }

}
