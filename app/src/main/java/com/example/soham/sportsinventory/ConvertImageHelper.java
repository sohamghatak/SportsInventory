package com.example.soham.sportsinventory;

/**
 * Created by soham on 12/2/18.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Helper class to help image conversion from Bitmap to BLOB and vice versa
 **/
public class ConvertImageHelper {
    /**
     * converts an image bitmap into a sql blob-compatible array
     *
     * @param bitmapImage the input image
     * @return byte[] of the compressed image
     */
    public static final byte[] convertBitmapToBlob(Bitmap bitmapImage) {

        byte[] byteArray;

        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, byteOutputStream);
            byteArray = byteOutputStream.toByteArray();
        } catch (Exception e) {
            Log.e("EditorActivity", "convertBitmapToBlob exception: " + e);
            return null;
        }

        return byteArray;
    }

    /**
     * converts an image bitmap into a sql blob-compatible array
     *
     * @param imageByteArray byte array of the image
     * @return compressed image
     */
    public static final Bitmap convertBlobToBitmap(byte[] imageByteArray) {
        if (imageByteArray == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
    }
}
