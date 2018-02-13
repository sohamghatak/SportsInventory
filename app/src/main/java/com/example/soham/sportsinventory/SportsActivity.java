package com.example.soham.sportsinventory;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.soham.sportsinventory.data.SportsContract.SportsEntry;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class SportsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    SportsCursorAdapter mSportsCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

        /**Floating Action Button to add a new item to the database**/
        FloatingActionButton fab = findViewById(R.id.floating_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editorIntent = new Intent(SportsActivity.this, EditorActivity.class);
                startActivity(editorIntent);
            }
        });

        /**Populate sports list view with the inventory data**/
        ListView sportsListView = findViewById(R.id.sports_list_view);

        /** Find and set empty view on the ListView, so that it only shows when the list has 0 items.**/
        View emptyView = findViewById(R.id.empty_view);
        sportsListView.setEmptyView(emptyView);

        /**Set custom adapter to inflate the list_item**/
        mSportsCursorAdapter = new SportsCursorAdapter(this, null);
        sportsListView.setAdapter(mSportsCursorAdapter);

        getSupportLoaderManager().initLoader(0, null, this);

        /**Set onClickListener for list item and pass intent to Editor Activity**/
        sportsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editorIntent = new Intent(SportsActivity.this, EditorActivity.class);
                Uri currentUri = ContentUris.withAppendedId(SportsEntry.CONTENT_URI, id);
                editorIntent.setData(currentUri);
                startActivity(editorIntent);
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                SportsEntry._ID,
                SportsEntry.COLUMN_SPORTS_NAME,
                SportsEntry.COLUMN_SPORTS_QUANTITY,
                SportsEntry.COLUMN_SPORTS_PRICE,
                SportsEntry.COLUMN_SPORTS_IMAGE
        };
        return new CursorLoader(this,
                SportsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSportsCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSportsCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /**Inflate the menu options from the res/menu/menu_catalog.xml file.
         This adds menu items to the app bar.**/
        getMenuInflater().inflate(R.menu.menu_sports, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                try {
                    insertDummyData();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.delete_all_items:
                /**show dialog confirmation to delete all items**/
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to insert dummy data in the table
     **/
    private void insertDummyData() throws FileNotFoundException {

        ContentValues contentValues = new ContentValues();

        /** Get Uri for example photo from drawable resource**/
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.raw.example)
                + '/' + getResources().getResourceTypeName(R.raw.example) + '/'
                + getResources().getResourceEntryName(R.raw.example));
        /**Open the image input stream from the image uri**/
        InputStream imageStream = getContentResolver().openInputStream(imageUri);
        /**Convert to Bitmap image**/
        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        /**Get byte image array**/
        byte[] imageArray = ConvertImageHelper.convertBitmapToBlob(selectedImage);

        contentValues.put(SportsEntry.COLUMN_SPORTS_NAME, "Football");
        contentValues.put(SportsEntry.COLUMN_SPORTS_QUANTITY, "5");
        contentValues.put(SportsEntry.COLUMN_SPORTS_PRICE, "50");
        contentValues.put(SportsEntry.COLUMN_SPORTS_IMAGE, imageArray);

        Uri uri = getContentResolver().insert(SportsEntry.CONTENT_URI, contentValues);
    }

    /**
     * Helper method to delete all items from the table
     **/
    private void deleteAllItems() {

        int rowDeleted = getContentResolver().delete(SportsEntry.CONTENT_URI, null, null);

        if (rowDeleted == 0) {
            Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Method to show delete all items dialog confirmation to the user
     **/
    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_items_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /**Delete all items if user selects positive button*/
                deleteAllItems();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /** User clicked the "Cancel" button, so dismiss the dialog**/
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}

