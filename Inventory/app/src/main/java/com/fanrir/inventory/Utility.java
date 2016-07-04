package com.fanrir.inventory;

import android.widget.ImageView;

import com.fanrir.inventory.sync.BitmapWorkerTask;

/**
 * Created by Eisdrachl on 04.07.2016.
 */
public class Utility {

    public static void loadBitmap(ImageView imageView, byte[] image, int reqWidth, int reqHeight) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView, image);
        task.execute(reqWidth, reqHeight);
    }
}
