package com.example.soham.sportsinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soham.sportsinventory.data.SportsContract.SportsEntry;

/**
 * Created by soham on 8/2/18.
 */

public class SportsCursorAdapter extends CursorAdapter {


    public SportsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    /**
     * Using view holder class to reuse views
     **/
    static class ViewHolder {
        TextView vItemName;
        TextView vItemQuantity;
        TextView vItemPrice;
        Button vSaleButton;
        ImageView vProductImage;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.vItemName = view.findViewById(R.id.sports_item_name);
        viewHolder.vItemQuantity = view.findViewById(R.id.sports_item_quantity);
        viewHolder.vItemPrice = view.findViewById(R.id.sports_item_price);
        viewHolder.vSaleButton = view.findViewById(R.id.sale_button);
        viewHolder.vProductImage = view.findViewById(R.id.product_image);

        int id = cursor.getColumnIndex(SportsEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(SportsEntry.COLUMN_SPORTS_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(SportsEntry.COLUMN_SPORTS_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(SportsEntry.COLUMN_SPORTS_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(SportsEntry.COLUMN_SPORTS_IMAGE);

        int mId = cursor.getInt(id);
        final String mName = cursor.getString(nameColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        int price = cursor.getInt(priceColumnIndex);
        byte[] imageAray = cursor.getBlob(imageColumnIndex);
        /**Store image only if the array is not null**/
        if (imageAray != null) {
            Bitmap productImage = ConvertImageHelper.convertBlobToBitmap(imageAray);
            viewHolder.vProductImage.setImageBitmap(productImage);
        } else {
            /**else only show a dummy image**/
            viewHolder.vProductImage.setImageResource(R.drawable.no_image);
        }
        /**Create an uri for the product which was clicked on**/
        final Uri currentUri = ContentUris.withAppendedId(SportsEntry.CONTENT_URI, mId);

        viewHolder.vItemName.setText(mName);
        viewHolder.vItemQuantity.setText(String.valueOf(quantity));
        viewHolder.vItemPrice.setText(String.valueOf(price));

        /**Set on click listener for Sale button**/
        viewHolder.vSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saleItem(quantity, context, currentUri, mName);
            }

        });


    }

    /**
     * Helper method to respond to sale button click
     *
     * @param quantity current quantity of the item
     * @param context  current context
     * @param uri      current item uri
     * @param name     of the item
     **/
    private void saleItem(int quantity, Context context, Uri uri, String name) {
        if (quantity > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SportsEntry.COLUMN_SPORTS_QUANTITY, quantity - 1);

            int rowsAffected = context.getContentResolver().update(uri,
                    contentValues,
                    null,
                    null);

            if (rowsAffected == 0) {
                Toast.makeText(context, context.getString(R.string.item_sale_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "One " + name + " sold successfully",
                        Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(context, context.getString(R.string.item_no_stock),
                    Toast.LENGTH_SHORT).show();

        }
    }

}
