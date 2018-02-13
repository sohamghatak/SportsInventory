package com.example.soham.sportsinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.soham.sportsinventory.data.SportsContract.SportsEntry;

/**
 * Created by soham on 7/2/18.
 */

public class SportsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "inventory.db";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
            SportsEntry.TABLE_NAME
            + "("
            + SportsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + SportsEntry.COLUMN_SPORTS_NAME + " TEXT NOT NULL,"
            + SportsEntry.COLUMN_SPORTS_QUANTITY + " INTEGER NOT NULL DEFAULT 0,"
            + SportsEntry.COLUMN_SPORTS_PRICE + " INTEGER NOT NULL DEFAULT 0,"
            + SportsEntry.COLUMN_SPORTS_IMAGE + " BLOB"
            + ");";
    private static final String SQL_DELETE_ENTRIES = "DROP IF TABLE EXISTS " + SportsEntry.TABLE_NAME;

    public SportsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Create Entries in the database table
     **/
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("SQL", "SQL" + SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    /**
     * Delete table if the version is upgraded and then call onCreate to create entries
     **/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
