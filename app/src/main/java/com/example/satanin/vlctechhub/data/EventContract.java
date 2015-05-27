package com.example.satanin.vlctechhub.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by satanin on 26/5/15.
 */
public class EventContract {
    /* Inner class that defines the table contents of the weather table */

    public static final String CONTENT_AUTHORITY = "com.example.satanin.vlctechub.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_EVENT = "tech_events";

    public static final class EventEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;

        public static final String TABLE_NAME = "event";

        public static final String COLUMN_EVENT_ID = "event_id";

        public static final String COLUMN_DESCRIPTION = "description";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_LINK = "link";

        public static Uri buildEventUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
