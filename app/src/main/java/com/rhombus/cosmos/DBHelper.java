package com.rhombus.cosmos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Preetham on 5/21/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String LOG = DBHelper.class.getName();

    public DBHelper(Context context) {
        super(context, DBContract.DATABASE_NAME, null, DBContract.DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.CalibrationEntry.CREATE_TABLE);
        db.execSQL(DBContract.LocationEntry.CREATE_TABLE);
    }

    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.CalibrationEntry.DELETE_TABLE);
        db.execSQL(DBContract.LocationEntry.DELETE_TABLE);
        onCreate(db);
    }

    public List<Location> getLocations() {
        List<Location> returnList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DBContract.LocationEntry.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                String location = c.getString(c.getColumnIndex(DBContract.LocationEntry.COLUMN_NAME_LOCATION));
                double p1 = c.getDouble(c.getColumnIndex(DBContract.LocationEntry.COLUMN_NAME_P1));
                double p2 = c.getDouble(c.getColumnIndex(DBContract.LocationEntry.COLUMN_NAME_P2));
                double p3 = c.getDouble(c.getColumnIndex(DBContract.LocationEntry.COLUMN_NAME_P3));
                String action = c.getString(c.getColumnIndex(DBContract.LocationEntry.COLUMN_NAME_ACTION));

                // adding to list
                returnList.add(new Location(location, p1, p2, p3, action));
            } while (c.moveToNext());
        }
        c.close();
        return returnList;
    }

    public long addLocation(Location location) {
        ContentValues values = new ContentValues();
        double position[] = location.getPosition();
        values.put(DBContract.LocationEntry.COLUMN_NAME_LOCATION, location.getLocation());
        values.put(DBContract.LocationEntry.COLUMN_NAME_P1, position[0]);
        values.put(DBContract.LocationEntry.COLUMN_NAME_P2, position[1]);
        values.put(DBContract.LocationEntry.COLUMN_NAME_P3, position[2]);
        values.put(DBContract.LocationEntry.COLUMN_NAME_ACTION, location.getAction());
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(DBContract.LocationEntry.TABLE_NAME, null, values);
        return id;
    }

    public long addCalibration(Calibration c) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DBContract.CalibrationEntry.DELETE_TABLE);
        db.execSQL(DBContract.CalibrationEntry.CREATE_TABLE);
        ContentValues values = new ContentValues();
        for(int i=0; i < c.calibration.length; i++) {
            values.put(DBContract.CalibrationEntry.COLUMN_NAME_BECON_NUM, i);
            values.put(DBContract.CalibrationEntry.COLUMN_NAME_P1, c.calibration[i][0]);
            values.put(DBContract.CalibrationEntry.COLUMN_NAME_P2, c.calibration[i][1]);
        }
        long id = db.insert(DBContract.CalibrationEntry.TABLE_NAME, null, values);
        return id;
    }
}
