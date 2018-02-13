package com.example.soham.sportsinventory;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soham.sportsinventory.data.SportsContract.SportsEntry;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * EditText field to enter the item name
     */
    private EditText mItemName;

    /**
     * EditText field to enter the item quantity
     */
    private TextView mItemQuantity;

    /**
     * EditText field to enter the item price
     */
    private EditText mItemPrice;

    /**
     * Button to add item
     */
    private Button mIncreaseButton;

    /**
     * Button to reduce item
     */
    private Button mDecreaseButton;

    /**
     * Button to order from supplier
     */
    private Button mOrderButton;

    /**
     * Button to delete item
     */
    private Button mDeleteButton;

    /**
     * Quantity text
     */
    int mQuantity = 0;

    Uri currentUri;

    /**
     * Boolean to check if an item have been changed
     **/
    private boolean mItemHasChanged = false;
    /**
     * Static image code for the activity to identify the result callback
     **/
    private static final int PICK_IMAGE_CODE = 5;
    /**
     * Image view for product image
     **/
    private ImageView mProductImage;

    /**
     * Byte array to store image array
     **/
    private byte[] mImageByteArray;

    /**
     * Byte array to store image array
     **/
    private TextView mAddImageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mItemName = findViewById(R.id.edit_item_name);
        mItemQuantity = findViewById(R.id.edit_item_quantity);
        mItemPrice = findViewById(R.id.edit_item_price);
        mIncreaseButton = findViewById(R.id.increase_button);
        mDecreaseButton = findViewById(R.id.decrease_button);
        mOrderButton = findViewById(R.id.order_supplier);
        mDeleteButton = findViewById(R.id.delete_item);
        mProductImage = findViewById(R.id.editor_add_image);
        mAddImageText = findViewById(R.id.add_image_text);

        /**Click listener for Increase button**/
        mIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity();
            }
        });

        /**Click listener for Decrease button**/
        mDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity();
            }
        });

        /**Click listener for Delete button**/
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        /**Click listener for Order from supplier button**/
        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });

        /**Click listener for add image button**/
        mProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        Intent intent = getIntent();
        currentUri = intent.getData();

        if (currentUri == null) {
            setTitle(R.string.add_item);
            mOrderButton.setVisibility(View.INVISIBLE); /**Remove order to supplier button when adding new item**/
            mDeleteButton.setVisibility(View.INVISIBLE);/**Remove delete item button when adding new item**/
            mItemQuantity.setText("0");
            mAddImageText.setText(R.string.add_an_image);
            mProductImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            setTitle(R.string.edit_item);
            getSupportLoaderManager().initLoader(0, null, this);
        }

        /**Se on touch listener for text items**/
        mItemName.setOnTouchListener(mTouchListener);
        mItemQuantity.setOnTouchListener(mTouchListener);
        mItemPrice.setOnTouchListener(mTouchListener);
        mProductImage.setOnTouchListener(mTouchListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {SportsEntry._ID,
                SportsEntry.COLUMN_SPORTS_NAME,
                SportsEntry.COLUMN_SPORTS_QUANTITY,
                SportsEntry.COLUMN_SPORTS_PRICE,
                SportsEntry.COLUMN_SPORTS_IMAGE,

        };
        return new CursorLoader(this,
                currentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(SportsEntry.COLUMN_SPORTS_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(SportsEntry.COLUMN_SPORTS_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(SportsEntry.COLUMN_SPORTS_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(SportsEntry.COLUMN_SPORTS_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);

            /**Get Image from database**/
            byte[] imageArray = cursor.getBlob(imageColumnIndex);
            /***Load image only if array is returned from database*/
            if (imageArray != null) {
                Bitmap productImage = ConvertImageHelper.convertBlobToBitmap(imageArray);
                mProductImage.setImageBitmap(productImage);
            } else {
                /**else just add option to update image again**/
                mProductImage.setImageResource(R.drawable.ic_add_a_photo_black_36dp);
                mAddImageText.setText(R.string.add_an_image);
                mProductImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
            mItemName.setText(name);
            mItemQuantity.setText(String.valueOf(quantity));
            mItemPrice.setText(String.valueOf(price));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemName.setText("");
        mItemQuantity.setText(String.valueOf(""));
        mItemPrice.setText(String.valueOf(""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /**Save item**/
            case R.id.action_save:
                boolean result = saveItem();
                if (result) {
                    /**Finish activity only if the user has entered valid values**/
                    finish();
                }
                return true;
            /**User presses back arrow in menu bar**/
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                /**Show dialog for unsaved changes.**/
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * OnTouchListener that listens for any user touches on a View,
     * implying that user is modifying the view,
     * and we change the mItemHasChanged boolean to true.
     **/
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    /**
     * Handle the back press button event
     **/
    public void onBackPressed() {
        /** If the item hasn't changed, continue with handling back button press**/
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        /** Otherwise if there are unsaved changes, setup a dialog to warn the user.**/
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /** User clicked "Discard" button, close the current activity.**/
                        finish();
                    }
                };

        /** Show dialog that there are unsaved changes**/
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null) {

            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                mAddImageText.setText("");
                mProductImage.setImageBitmap(selectedImage);
                /**Temp array to store image**/
                mImageByteArray = ConvertImageHelper.convertBitmapToBlob(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_CODE) {
            Toast.makeText(EditorActivity.this, getString(R.string.no_image_picked), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Method to show unsaved changes dialog to user
     **/
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Method to show dialog confirmation to the user
     **/
    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /**Delete item if user selects positive button*/
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /** User clicked the "Cancel" button, so dismiss the dialog**/
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        /** Create and show the AlertDialog**/
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Helper method to save an item
     **/
    private boolean saveItem() {

        String itemName = mItemName.getText().toString().trim();
        String quantity = mItemQuantity.getText().toString().trim();
        String price = mItemPrice.getText().toString().trim();

        if (TextUtils.isEmpty(itemName) && (Integer.parseInt(quantity) == 0) && TextUtils.isEmpty(price)) {
            Toast.makeText(this, getString(R.string.item_not_saved),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validateInput(itemName, quantity, price)) {
            return false;
        }

        int itemQuantity = 0;
        int itemPrice = 0;

        if (!TextUtils.isEmpty(itemName)) {
            itemQuantity = Integer.parseInt(quantity);
        }

        if (!TextUtils.isEmpty(quantity)) {
            itemQuantity = Integer.parseInt(quantity);
        }

        if (!TextUtils.isEmpty(price)) {
            if (price.matches("[0-9]+")) {
                itemPrice = Integer.parseInt(price);
            }
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put(SportsEntry.COLUMN_SPORTS_NAME, itemName);
        contentValues.put(SportsEntry.COLUMN_SPORTS_QUANTITY, itemQuantity);
        contentValues.put(SportsEntry.COLUMN_SPORTS_PRICE, itemPrice);
        /**Store image only if byte array is not null**/
        if (mImageByteArray != null) {
            contentValues.put(SportsEntry.COLUMN_SPORTS_IMAGE, mImageByteArray);
        }

        if (currentUri == null) {

            Uri newUri = getContentResolver().insert(SportsEntry.CONTENT_URI, contentValues);

            if (newUri == null) {
                /** If the new content URI is null, then there was an error with insertion.**/
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                /** Otherwise, the insertion was successful and we can display a toast.**/
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        } else {

            int rowsAffected = getContentResolver().update(currentUri, contentValues, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_item_failed),
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(this, getString(R.string.editor_update_item_successful),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        }
    }

    /**
     * Helper method to delete one item
     **/
    private void deleteItem() {
        if (currentUri != null) {
            int rowsDeleted = getContentResolver().delete(currentUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    /**
     * Helper method to send email of order to supplier
     **/
    private void sendMail() {

        String itemName = mItemName.getText().toString().trim();
        String subject = getString(R.string.email_subject) + " " + itemName;
        String body = getString(R.string.email_body) + " " + itemName;

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:")); /**only email app should handle this**/
        emailIntent.setType("*/*");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }
    }

    /**
     * Helper method to increase quantity on "+" button click in editor activity
     **/
    private void increaseQuantity() {

        if (currentUri != null) {
            String quantity = mItemQuantity.getText().toString().trim();
            mQuantity = Integer.parseInt(quantity);
            if (mQuantity < 50) {
                mQuantity++;
                mItemQuantity.setText(String.valueOf(mQuantity));
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.limit_quantity_warning), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (mQuantity < 50) {
                mQuantity++;
                mItemQuantity.setText(String.valueOf(mQuantity));
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.limit_quantity_warning), Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * Helper method to decrease quantity on "-" button click in editor activity
     **/
    private void decreaseQuantity() {

        if (currentUri != null) {
            String quantity = mItemQuantity.getText().toString().trim();
            mQuantity = Integer.parseInt(quantity);
            if (mQuantity != 0) {
                mQuantity--;
                mItemQuantity.setText(String.valueOf(mQuantity));
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.negative_quantity_warning), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (mQuantity != 0) {
                mQuantity--;
                mItemQuantity.setText(String.valueOf(mQuantity));
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.negative_quantity_warning), Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * Helper method to validate inputs entered
     *
     * @param name     of the product
     * @param quantity of the product
     * @param price    of the product
     */
    private boolean validateInput(String name, String quantity, String price) {

        /**Set this value to true if any of the parameters are empty**/
        boolean someInvalidValue = false;
        /**String value to hold field name which is empty**/
        String wrongEntry = null;
        if (TextUtils.isEmpty(name)) {
            wrongEntry = "name";
            someInvalidValue = true;
        } else if (Integer.parseInt(quantity) == 0) {
            wrongEntry = "quantity";
            someInvalidValue = true;
        } else if (TextUtils.isEmpty(price)) {
            wrongEntry = "price";
            someInvalidValue = true;
        }
        /**Set a toast message asking user to enter values correctly**/
        if (someInvalidValue) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.no_value_text) + " " + wrongEntry + ".\n" + getString(R.string.no_value_text_end),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * Helper method to send an intent to get the image from memory/sd card
     **/
    private void selectImage() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.pick_an_image)), PICK_IMAGE_CODE);
    }
}
