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

public class NodeDbHelper extends SQLiteOpenHelper
{
    public static final String DB_NAME = "com.dexcaff.todolist.db";
    public static final int DB_VERSION = 1;

    public NodeDbHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + NodeContract.NodeEntry.TABLE_NAME + " (" +
                NodeContract.NodeEntry._ID + " INTEGER PRIMARY KEY," +
                NodeContract.NodeEntry.COLUMN_NAME_CRAG_ID + " INTEGER," +
                "FOREIGN KEY(" + NodeContract.NodeEntry.COLUMN_NAME_CRAG_ID + ") REFERENCES " + CragContract.CragEntry.TABLE_NAME + "(" + CragContract.CragEntry._ID + ")," +
                NodeContract.NodeEntry.COLUMN_NAME_X_COORD + " REAL," +
                NodeContract.NodeEntry.COLUMN_NAME_Y_COORD + " REAL )");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + NodeContract.NodeEntry.TABLE_NAME);
        onCreate(db);
    }
}
