package com.dexcaff.cragmapper.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.dexcaff.cragmapper.db.CragContract;
import com.dexcaff.cragmapper.db.CragDbHelper;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2016.12.19
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.models
 */

public class Crag {
    public static final String EXTRA_TAG = "crag_id";
    public HashMap<String, Object> properties;

    public Crag(long id, String name, String imageUri, Float rating) {
        this.properties = new HashMap<>();
        this.properties.put(CragContract.CragEntry._ID, id);
        this.properties.put(CragContract.CragEntry.COLUMN_NAME_TITLE, name);
        this.properties.put(CragContract.CragEntry.COLUMN_NAME_IMAGE, imageUri);
        this.properties.put(CragContract.CragEntry.COLUMN_NAME_RATING, rating);
    }

    public Bundle cragToBundle() {
        Bundle bundle = new Bundle();
        bundle.putLong(CragContract.CragEntry._ID, (long) this.properties.get(CragContract.CragEntry._ID));
        bundle.putString(CragContract.CragEntry.COLUMN_NAME_TITLE, (String) this.properties.get(CragContract.CragEntry.COLUMN_NAME_TITLE));
        bundle.putString(CragContract.CragEntry.COLUMN_NAME_IMAGE, (String) this.properties.get(CragContract.CragEntry.COLUMN_NAME_IMAGE));
        bundle.putFloat(CragContract.CragEntry.COLUMN_NAME_RATING, (float) this.properties.get(CragContract.CragEntry.COLUMN_NAME_RATING));
        return bundle;
    }

    public ContentValues cragToContentValues() {
        Bundle bundle = this.cragToBundle();
        return Crag.bundleToContentValues(bundle);
    }

    private static ContentValues bundleToContentValues(Bundle crag) {
        ContentValues values = new ContentValues();
        values.put(CragContract.CragEntry.COLUMN_NAME_TITLE, crag.getString(CragContract.CragEntry.COLUMN_NAME_TITLE, ""));
        values.put(CragContract.CragEntry.COLUMN_NAME_IMAGE, crag.getString(CragContract.CragEntry.COLUMN_NAME_IMAGE, ""));
        values.put(CragContract.CragEntry.COLUMN_NAME_RATING, crag.getFloat(CragContract.CragEntry.COLUMN_NAME_RATING, 0));
        //values.put(CragContract.CragEntry._ID, crag.getInt(CragContract.CragEntry._ID, 0));
        //Don't see any reason to ever add in the AUTOINCREMENT UNIQUE ID
        return values;
    }

    public Crag addCrag(Context context) throws Exception {
        if ((long) this.properties.get(CragContract.CragEntry._ID) > -1) {
            return updateCrag(context);
        }
        ContentValues values = this.cragToContentValues();
        CragDbHelper dbHelper = new CragDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(CragContract.CragEntry.TABLE_NAME, null, values);
        if (rowId == -1) {
            throw new Exception("Add Crag sql insert failed");
        }
        return this;
    }

    public Crag updateCrag(Context context) throws Exception {
        ContentValues values = this.cragToContentValues();
        CragDbHelper dbHelper = new CragDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.update(
                CragContract.CragEntry.TABLE_NAME,
                values,
                CragContract.CragEntry._ID + " = ?",
                new String[]{Long.toString((long) this.properties.get(CragContract.CragEntry._ID))}
        );
        if (rowId == -1) {
            throw new Exception("Add Crag sql update failed");
        } else if (rowId == 0) {
            Log.d(TAG, "updateCrag: zero rows updated");
        }
        return this;
    }

    public static Crag getCragById(Context context, long id) {
        CragDbHelper dbHelper = new CragDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = CragContract.getColumns();
        Cursor cursor = db.query(
                CragContract.CragEntry.TABLE_NAME,
                columns,
                CragContract.CragEntry._ID + " = ?",
                new String[]{Long.toString(id)},
                null,
                null,
                null,
                "1");
        if (cursor.moveToNext()) {
            Crag crag = new Crag(
                    id,
                    cursor.getString(cursor.getColumnIndex(CragContract.CragEntry.COLUMN_NAME_TITLE)),
                    cursor.getString(cursor.getColumnIndex(CragContract.CragEntry.COLUMN_NAME_IMAGE)),
                    cursor.getFloat(cursor.getColumnIndex(CragContract.CragEntry.COLUMN_NAME_RATING))
            );
            cursor.close();
            db.close();
            return crag;
        } else {
            return new Crag(0, "", "", (float) 0);
        }
    }

    public static ArrayList<Crag> getAllCrags(Context context) {
        String[] reqColumns = CragContract.getColumns();
        CragDbHelper dbHelper = new CragDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                CragContract.CragEntry.TABLE_NAME,
                reqColumns,
                null,
                null,
                null,
                null,
                CragContract.CragEntry._ID + " ASC"
        );

        ArrayList<Crag> cragList = new ArrayList<>();
        while (c.moveToNext()) {
            Crag crag = new Crag(
                    c.getLong(c.getColumnIndex(CragContract.CragEntry._ID)),
                    c.getString(c.getColumnIndex(CragContract.CragEntry.COLUMN_NAME_TITLE)),
                    c.getString(c.getColumnIndex(CragContract.CragEntry.COLUMN_NAME_IMAGE)),
                    c.getFloat(c.getColumnIndex(CragContract.CragEntry.COLUMN_NAME_RATING))
            );
            cragList.add(crag);
        }
        c.close();
        db.close();
        return cragList;
    }
}
