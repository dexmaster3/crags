package com.dexcaff.cragmapper.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.dexcaff.cragmapper.db.CragContract;
import com.dexcaff.cragmapper.db.CragDbHelper;

import java.util.ArrayList;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2016.12.19
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.models
 */

public class Crag {
    public String name;
    public String imageURI;

    public Crag(String name, String imageURI) {
        this.name = name;
        this.imageURI = imageURI;
    }

    public static Crag addCrag(Context context, Crag crag) throws Exception {
        CragDbHelper dbHelper = new CragDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CragContract.CragEntry.COLUMN_NAME_TITLE, crag.name);
        values.put(CragContract.CragEntry.COLUMN_NAME_IMAGE, crag.imageURI);
        long rowId = db.insert(CragContract.CragEntry.TABLE_NAME, null, values);
        if (rowId == -1) {
            throw new Exception("Add Crag sql insert failed");
        }
        return crag;
    }

    public static Bundle getCragById(Context context, int id) {
        CragDbHelper dbHelper = new CragDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = CragContract.getColumns();
        Cursor cursor = db.query(
                CragContract.CragEntry.TABLE_NAME,
                columns,
                CragContract.CragEntry._ID + " = ?",
                new String[]{Integer.toString(id)},
                null,
                null,
                null,
                "1");
        Bundle crag = new Bundle();
        crag.putString(CragContract.CragEntry.COLUMN_NAME_TITLE, cursor.getString(cursor.getColumnIndex(CragContract.CragEntry.COLUMN_NAME_TITLE)));
        crag.putString(CragContract.CragEntry.COLUMN_NAME_IMAGE, cursor.getString(cursor.getColumnIndex(CragContract.CragEntry.COLUMN_NAME_IMAGE)));

        return crag;
    }

    public static ArrayList<Crag> getAllCrags(Context context) {
        String[] reqColumns = {
                CragContract.CragEntry._ID,
                CragContract.CragEntry.COLUMN_NAME_TITLE,
                CragContract.CragEntry.COLUMN_NAME_IMAGE
        };
        CragDbHelper dbHelper = new CragDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                CragContract.CragEntry.TABLE_NAME,
                reqColumns,
                null,
                null,
                null,
                null,
                CragContract.CragEntry.COLUMN_NAME_TITLE + " ASC"
        );

        ArrayList<Crag> cragList = new ArrayList<Crag>();
        while (c.moveToNext()) {
            String title = c.getString(c.getColumnIndex(CragContract.CragEntry.COLUMN_NAME_TITLE));
            String uri = c.getString(c.getColumnIndex(CragContract.CragEntry.COLUMN_NAME_IMAGE));
            cragList.add(new Crag(title, uri));
        }
        c.close();
        db.close();
        return cragList;
    }
}
