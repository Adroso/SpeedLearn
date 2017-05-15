package com.example.adroso360.speedlearn;

import android.provider.BaseColumns;

/**
 * Created by Adroso360 on 15/5/17.
 */

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}


        /* Inner class that defines the table contents */
        public static class FeedEntry implements BaseColumns {
            public static final String TABLE_NAME = "scores";
            public static final String COLUMN_NAME_POINTS = "points";
            public static final String COLUMN_NAME_TIME = "time";
        }
    }

