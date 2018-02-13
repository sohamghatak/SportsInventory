package com.example.soham.sportsinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by soham on 7/2/18.
 */

public final class SportsContract {

    public static final String CONTENT_AUTHORITY = "com.example.soham.sportsinventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SPORTS = "sports";

    private SportsContract() {
    }

    public static abstract class SportsEntry implements BaseColumns {

        public static final String TABLE_NAME = "sports";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_SPORTS_NAME = "name";
        public static final String COLUMN_SPORTS_QUANTITY = "quantity";
        public static final String COLUMN_SPORTS_PRICE = "price";
        public static final String COLUMN_SPORTS_IMAGE = "image";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SPORTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a the complete data in table.
         */

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SPORTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SPORTS;


    }
}
