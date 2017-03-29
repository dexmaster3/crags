package com.dexcaff.cragmapper.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;

import com.dexcaff.cragmapper.db.DbHelper;

import java.util.HashMap;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2016.12.19
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.models
 */

public class Node implements BaseColumns {
    public static final String TAG = "models/Node";
    
    public static final String EXTRA_TAG = "node_id";
    public HashMap<String, Object> properties;

    public static final String TABLE_NAME = "nodes";
    public static final String KEY_CRAG_ID = "crag_id";
    public static final String KEY_X_COORD = "x_coord";
    public static final String KEY_Y_COORD = "y_coord";

    public static String[] getColumns() {
        return new String[]{
                _ID,
                KEY_CRAG_ID,
                KEY_X_COORD,
                KEY_Y_COORD
        };
    }

    public static String createTable() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                KEY_CRAG_ID + " INTEGER," +
                KEY_X_COORD + " REAL," +
                KEY_Y_COORD + " REAL," +
                "FOREIGN KEY(" + KEY_CRAG_ID + ") REFERENCES " + Crag.TABLE_NAME + "(" + Crag._ID + "))";
    }

    public Node(long id, long crag_id, float x_coord, float y_coord) {
        this.properties = new HashMap<>();
        this.properties.put(_ID, id);
        this.properties.put(KEY_CRAG_ID, crag_id);
        this.properties.put(KEY_X_COORD, x_coord);
        this.properties.put(KEY_Y_COORD, y_coord);
    }

    public Bundle nodeToBundle() {
        Bundle bundle = new Bundle();
        bundle.putLong(_ID, (long) this.properties.get(_ID));
        bundle.putLong(KEY_CRAG_ID, (long) this.properties.get(KEY_CRAG_ID));
        bundle.putFloat(KEY_X_COORD, (float) this.properties.get(KEY_X_COORD));
        bundle.putFloat(KEY_Y_COORD, (float) this.properties.get(KEY_Y_COORD));
        return bundle;
    }

    public ContentValues nodeToContentValues() {
        Bundle bundle = this.nodeToBundle();
        return Node.bundleToContentValues(bundle);
    }

    private static ContentValues bundleToContentValues(Bundle node) {
        ContentValues values = new ContentValues();
        values.put(KEY_CRAG_ID, node.getLong(KEY_CRAG_ID, -1));
        values.put(KEY_X_COORD, node.getFloat(KEY_X_COORD, -1));
        values.put(KEY_Y_COORD, node.getFloat(KEY_Y_COORD, -1));
        return values;
    }

    public Node addNode(Context context) throws Exception {
        if ((long) this.properties.get(_ID) > -1) {
            return updateNode(context);
        }
        ContentValues values = this.nodeToContentValues();
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(TABLE_NAME, null, values);
        if (rowId == -1) {
            throw new Exception("Add Node sql insert failed");
        } else {
            this.properties.put(_ID, rowId);
        }
        return this;
    }

    public Node updateNode(Context context) throws Exception {
        ContentValues values = this.nodeToContentValues();
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.update(
                TABLE_NAME,
                values,
                _ID + " = ?",
                new String[]{Long.toString((long) this.properties.get(_ID))}
        );
        if (rowId == -1) {
            throw new Exception("Add Node sql update failed");
        } else if (rowId == 0) {
            Log.d(TAG, "updateNode: zero rows updated");
        }
        return this;
    }

    //ToDo always have this crag assoc Crag Data?
    public static Node getNodeById(Context context, long id) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = getColumns();
        Cursor cursor = db.query(
                TABLE_NAME,
                columns,
                _ID + " = ?",
                new String[]{Long.toString(id)},
                null,
                null,
                null,
                "1");
        if (cursor.moveToNext()) {
            Node node = new Node(
                    id,
                    cursor.getLong(cursor.getColumnIndex(KEY_CRAG_ID)),
                    cursor.getFloat(cursor.getColumnIndex(KEY_X_COORD)),
                    cursor.getFloat(cursor.getColumnIndex(KEY_Y_COORD))
            );
            cursor.close();
            db.close();
            return node;
        } else {
            return new Node(0, 0, 0, 0);
        }
    }

    public static HashMap<String, Node> getAllNodesByCragId(Context context, long cragId) {
        String[] reqColumns = getColumns();
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query(
                TABLE_NAME,
                reqColumns,
                KEY_CRAG_ID + " = ?",
                new String[] {Long.toString(cragId)},
                null, null, null);

        HashMap<String, Node> nodeList = new HashMap<>();
        while (c.moveToNext()) {
            long nodeId = c.getLong(c.getColumnIndex(_ID));
            Node node = new Node(
                    nodeId,
                    c.getLong(c.getColumnIndex(KEY_CRAG_ID)),
                    c.getFloat(c.getColumnIndex(KEY_X_COORD)),
                    c.getFloat(c.getColumnIndex(KEY_Y_COORD))
            );
            nodeList.put(Long.toString(nodeId), node);
        }
        c.close();
        db.close();
        return nodeList;
    }

    public static int deleteAllNodesByCragId(Context context, long cragId) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                TABLE_NAME,
                KEY_CRAG_ID + "=" + cragId,
                null
        );
    }
}
