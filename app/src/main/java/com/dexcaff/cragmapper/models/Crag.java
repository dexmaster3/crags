package com.dexcaff.cragmapper.models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    public Crag(String name, String imageURI) {
        this.name = name;
        this.imageURI = imageURI;
    }
}
