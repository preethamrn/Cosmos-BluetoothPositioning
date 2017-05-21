package com.rhombus.cosmos;

import android.provider.BaseColumns;

/**
 * Created by Preetham on 5/21/2017.
 */

public final class DBContract {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "menudb.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INT";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String COMMA_SEP = ",";

    public DBContract() {
    }

    public static abstract class CalibrationEntry implements BaseColumns {
        public static final String TABLE_NAME = "calibration";
        public static final String COLUMN_NAME_BECON_NUM = "beacon_num";
        public static final String COLUMN_NAME_P1 = "p1";
        public static final String COLUMN_NAME_P2 = "p2";
        public static final String COLUMN_NAME_P3 = "p3";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_BECON_NUM + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_P1 + DOUBLE_TYPE + COMMA_SEP +
                COLUMN_NAME_P2 + DOUBLE_TYPE + COMMA_SEP +
                COLUMN_NAME_P3 + DOUBLE_TYPE + " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_P1 = "p1";
        public static final String COLUMN_NAME_P2 = "p2";
        public static final String COLUMN_NAME_P3 = "p3";
        public static final String COLUMN_NAME_ACTION = "action";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_P1 + DOUBLE_TYPE + COMMA_SEP +
                COLUMN_NAME_P2 + DOUBLE_TYPE + COMMA_SEP +
                COLUMN_NAME_P3 + DOUBLE_TYPE + COMMA_SEP +
                COLUMN_NAME_ACTION + TEXT_TYPE + " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}