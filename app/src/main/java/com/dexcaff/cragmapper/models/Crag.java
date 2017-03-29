package com.dexcaff.cragmapper.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;

import com.dexcaff.cragmapper.db.DbHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2016.12.19
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.models
 */

public class Crag implements BaseColumns {
    public static final String TAG = "models/Crag";

    public static final String EXTRA_TAG = "crag_id";
    public HashMap<String, Object> properties;

    public static final String TABLE_NAME = "crags";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_RATING = "rating";

    public static String[] getColumns() {
        return new String[]{
                _ID,
                KEY_TITLE,
                KEY_IMAGE,
                KEY_RATING
        };
    }

    public static String createTable() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                KEY_TITLE + " TEXT," +
                KEY_RATING + " REAL," +
                KEY_IMAGE + " TEXT )";
    }

    //Todo multiple constructors?
    public Crag(long id, String name, String imageUri, Float rating) {
        this.properties = new HashMap<>();
        this.properties.put(_ID, id);
        this.properties.put(KEY_TITLE, name);
        this.properties.put(KEY_IMAGE, imageUri);
        this.properties.put(KEY_RATING, rating);
    }

    public Bundle cragToBundle() {
        Bundle bundle = new Bundle();
        bundle.putLong(_ID, (long) this.properties.get(_ID));
        bundle.putString(KEY_TITLE, (String) this.properties.get(KEY_TITLE));
        bundle.putString(KEY_IMAGE, (String) this.properties.get(KEY_IMAGE));
        bundle.putFloat(KEY_RATING, (float) this.properties.get(KEY_RATING));
        return bundle;
    }

    public ContentValues cragToContentValues() {
        Bundle bundle = this.cragToBundle();
        return Crag.bundleToContentValues(bundle);
    }

    private static ContentValues bundleToContentValues(Bundle crag) {
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, crag.getString(KEY_TITLE, ""));
        values.put(KEY_IMAGE, crag.getString(KEY_IMAGE, ""));
        values.put(KEY_RATING, crag.getFloat(KEY_RATING, 0));
        //values.put(_ID, crag.getInt(_ID, 0));
        //Don't see any reason to ever add in the AUTOINCREMENT UNIQUE ID
        return values;
    }

    public Crag addCrag(Context context) throws Exception {
        if ((long) this.properties.get(_ID) > -1) {
            return updateCrag(context);
        }
        ContentValues values = this.cragToContentValues();
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(TABLE_NAME, null, values);
        if (rowId == -1) {
            throw new Exception("Add Crag sql insert failed");
        } else {
            this.properties.put(_ID, rowId);
        }
        return this;
    }

    public Crag updateCrag(Context context) throws Exception {
        ContentValues values = this.cragToContentValues();
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.update(
                TABLE_NAME,
                values,
                _ID + " = ?",
                new String[]{Long.toString((long) this.properties.get(_ID))}
        );
        if (rowId == -1) {
            throw new Exception("Add Crag sql update failed");
        } else if (rowId == 0) {
            Log.d(TAG, "updateCrag: zero rows updated");
        }
        return this;
    }

    public static Crag getCragById(Context context, long id) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = getColumns();
        Crag crag = new Crag(0, "", "", (float) 0);
        //Get Crag DB pass
        Cursor cursor = db.query(
                TABLE_NAME,
                columns,
                _ID + " = ?",
                new String[]{Long.toString(id)},
                null, null, null, "1");
        if (cursor.moveToNext()) {
            crag = new Crag(
                    id,
                    cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                    cursor.getFloat(cursor.getColumnIndex(KEY_RATING))
            );
            cursor.close();
        }
        db.close();
        //Get Nodes DB pass
        crag.properties.put(Node.TABLE_NAME, Node.getAllNodesByCragId(context, id));
        return crag;
    }

    public static ArrayList<Crag> getAllCrags(Context context) {
        String[] reqColumns = getColumns();
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                TABLE_NAME,
                reqColumns,
                null,
                null,
                null,
                null,
                _ID + " ASC"
        );

        ArrayList<Crag> cragList = new ArrayList<>();
        while (c.moveToNext()) {
            Crag crag = new Crag(
                    c.getLong(c.getColumnIndex(_ID)),
                    c.getString(c.getColumnIndex(KEY_TITLE)),
                    c.getString(c.getColumnIndex(KEY_IMAGE)),
                    c.getFloat(c.getColumnIndex(KEY_RATING))
            );
            cragList.add(crag);
        }
        c.close();
        db.close();
        return cragList;
    }



    public static int deleteCragById(Context context, long cragId) {
        Node.deleteAllNodesByCragId(context, cragId);
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                TABLE_NAME,
                _ID + "=" + cragId,
                null
        );
    }
}
