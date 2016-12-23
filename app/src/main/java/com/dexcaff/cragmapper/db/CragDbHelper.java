package com.dexcaff.cragmapper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2016.12.15
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.db
 */

public class CragDbHelper extends SQLiteOpenHelper
{
    public static final String DB_NAME = "com.dexcaff.todolist.db";
    public static final int DB_VERSION = 2;

    public CragDbHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + CragContract.CragEntry.TABLE_NAME + " (" +
                CragContract.CragEntry._ID + " INTEGER PRIMARY KEY," +
                CragContract.CragEntry.COLUMN_NAME_TITLE + " TEXT," +
                CragContract.CragEntry.COLUMN_NAME_RATING + " REAL," +
                CragContract.CragEntry.COLUMN_NAME_IMAGE + " TEXT )");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + CragContract.CragEntry.TABLE_NAME);
        onCreate(db);
    }
}
