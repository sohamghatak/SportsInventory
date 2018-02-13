package com.example.soham.sportsinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.soham.sportsinventory.data.SportsContract.SportsEntry;

/**
 * Created by soham on 7/2/18.
 */

public class SportsProvider extends ContentProvider {

    private static final String LOG_TAG = SportsProvider.class.getSimpleName();
    /**
     * ID to be used by the content provider to identify
     * the action to be done on complete table or a single record
     **/
    private static final int SPORTS = 100;
    private static final int SPORTS_ID = 101;

    private SportsDbHelper mSportsDbHelper;

    /**
     * Create Uri Matcher for matching the uri's passed in
     **/
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**Create static uri**/
    static {
        sUriMatcher.addURI(SportsContract.CONTENT_AUTHORITY, SportsContract.PATH_SPORTS, SPORTS);
        sUriMatcher.addURI(SportsContract.CONTENT_AUTHORITY, SportsContract.PATH_SPORTS + "/#", SPORTS_ID);
    }

    @Override
    public boolean onCreate() {
        mSportsDbHelper = new SportsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        /**Get instance of readable database**/
        SQLiteDatabase sqLiteDatabase = mSportsDbHelper.getReadableDatabase();
        Cursor cursor;
        /**Match the passed in uri**/
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SPORTS:
                cursor = sqLiteDatabase.query(SportsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case SPORTS_ID:
                selection = SportsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(SportsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SPORTS:
                return SportsEntry.CONTENT_LIST_TYPE;
            case SPORTS_ID:
                return SportsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SPORTS:
                return insertItem(uri, values);
            default:
                throw new IllegalArgumentException("Insert item failed for uri : " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = mSportsDbHelper.getWritableDatabase();
        switch (match) {
            case SPORTS:
                rowsDeleted = sqLiteDatabase.delete(SportsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case SPORTS_ID:
                selection = SportsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = sqLiteDatabase.delete(SportsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for uri : " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case SPORTS:
                return updateItem(uri, values, selection, selectionArgs);
            case SPORTS_ID:
                selection = SportsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Helper method to insert new item in the database table
     *
     * @param uri           passed in Uri
     * @param contentValues passed in values to insert
     **/
    private Uri insertItem(Uri uri, ContentValues contentValues) {

        /** Check that the name is not null**/
        String name = contentValues.getAsString(SportsEntry.COLUMN_SPORTS_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }
        /**Check that quantity is not null**/
        Integer quantity = contentValues.getAsInteger(SportsEntry.COLUMN_SPORTS_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Item requires a valid quantity");
        }
        /**Check that price is not null**/
        Integer price = contentValues.getAsInteger(SportsEntry.COLUMN_SPORTS_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Item requires a valid price");
        }
        /**Get writable database**/
        SQLiteDatabase sqLiteDatabase = mSportsDbHelper.getWritableDatabase();

        long rowID = sqLiteDatabase.insert(SportsEntry.TABLE_NAME, null, contentValues);

        if (rowID == -1) {
            Log.e(LOG_TAG, "Failed to insert item " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, rowID);
    }

    /**
     * Helper method to update an item in the database table
     *
     * @param uri           of the item to be updated
     * @param contentValues new values to be updated
     * @param selection     selection tag
     * @param selectionArgs selection args
     **/
    private int updateItem(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        /**Check if name is valid**/
        if (contentValues.containsKey(SportsEntry.COLUMN_SPORTS_NAME)) {
            String name = contentValues.getAsString(SportsEntry.COLUMN_SPORTS_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }

        /**Check if quantity is valid**/
        if (contentValues.containsKey(SportsEntry.COLUMN_SPORTS_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(SportsEntry.COLUMN_SPORTS_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Item requires a valid positive quantity value");
            }
        }

        /**Check if price is valid**/
        if (contentValues.containsKey(SportsEntry.COLUMN_SPORTS_PRICE)) {
            Integer price = contentValues.getAsInteger(SportsEntry.COLUMN_SPORTS_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Item requires a valid positive price value");
            }
        }

        /**Return 0 if size of the Content values is zero**/
        if (contentValues.size() == 0) {
            return 0;
        }
        SQLiteDatabase sqLiteDatabase = mSportsDbHelper.getWritableDatabase();
        int rowsUpdated = sqLiteDatabase.update(SportsEntry.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
