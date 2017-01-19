package com.dexcaff.cragmapper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dexcaff.cragmapper.models.Crag;
import com.dexcaff.cragmapper.models.Node;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2017.01.19
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.db
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();
    private static final String DB_NAME = "com.dexcaff.cragmapper.db";
    private static final int DB_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Crag.createTable());
        db.execSQL(Node.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("SQLiteDB.onUpgrade(%d -> %d)", oldVersion, newVersion));

        db.execSQL("DROP TABLE IF EXISTS " + Crag.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Node.TABLE_NAME);
        onCreate(db);
    }
}
