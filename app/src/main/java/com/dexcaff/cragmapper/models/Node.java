package com.dexcaff.cragmapper.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.dexcaff.cragmapper.db.NodeContract;
import com.dexcaff.cragmapper.db.NodeDbHelper;

import java.util.HashMap;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2016.12.19
 *          Project: CragMapper
 *          Package: com.dexcaff.cragmapper.models
 */

public class Node {
    public static final String TAG = "models/Node";
    public static final String EXTRA_TAG = "node_id";
    public HashMap<String, Object> properties;

    public Node(long id, long crag_id, float x_coord, float y_coord) {
        this.properties = new HashMap<>();
        this.properties.put(NodeContract.NodeEntry._ID, id);
        this.properties.put(NodeContract.NodeEntry.COLUMN_NAME_CRAG_ID, crag_id);
        this.properties.put(NodeContract.NodeEntry.COLUMN_NAME_X_COORD, x_coord);
        this.properties.put(NodeContract.NodeEntry.COLUMN_NAME_Y_COORD, y_coord);
    }

    public Bundle nodeToBundle() {
        Bundle bundle = new Bundle();
        bundle.putLong(NodeContract.NodeEntry._ID, (long) this.properties.get(NodeContract.NodeEntry._ID));
        bundle.putLong(NodeContract.NodeEntry.COLUMN_NAME_CRAG_ID, (long) this.properties.get(NodeContract.NodeEntry.COLUMN_NAME_CRAG_ID));
        bundle.putFloat(NodeContract.NodeEntry.COLUMN_NAME_X_COORD, (float) this.properties.get(NodeContract.NodeEntry.COLUMN_NAME_X_COORD));
        bundle.putFloat(NodeContract.NodeEntry.COLUMN_NAME_Y_COORD, (float) this.properties.get(NodeContract.NodeEntry.COLUMN_NAME_Y_COORD));
        return bundle;
    }

    public ContentValues nodeToContentValues() {
        Bundle bundle = this.nodeToBundle();
        return Node.bundleToContentValues(bundle);
    }

    private static ContentValues bundleToContentValues(Bundle node) {
        ContentValues values = new ContentValues();
        values.put(NodeContract.NodeEntry.COLUMN_NAME_CRAG_ID, node.getLong(NodeContract.NodeEntry.COLUMN_NAME_CRAG_ID, -1));
        values.put(NodeContract.NodeEntry.COLUMN_NAME_X_COORD, node.getFloat(NodeContract.NodeEntry.COLUMN_NAME_X_COORD, -1));
        values.put(NodeContract.NodeEntry.COLUMN_NAME_Y_COORD, node.getFloat(NodeContract.NodeEntry.COLUMN_NAME_Y_COORD, -1));
        return values;
    }

    public Node addNode(Context context) throws Exception {
        if ((long) this.properties.get(NodeContract.NodeEntry._ID) > -1) {
            return updateNode(context);
        }
        ContentValues values = this.nodeToContentValues();
        NodeDbHelper dbHelper = new NodeDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(NodeContract.NodeEntry.TABLE_NAME, null, values);
        if (rowId == -1) {
            throw new Exception("Add Node sql insert failed");
        } else {
            this.properties.put(NodeContract.NodeEntry._ID, rowId);
        }
        return this;
    }

    public Node updateNode(Context context) throws Exception {
        ContentValues values = this.nodeToContentValues();
        NodeDbHelper dbHelper = new NodeDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.update(
                NodeContract.NodeEntry.TABLE_NAME,
                values,
                NodeContract.NodeEntry._ID + " = ?",
                new String[]{Long.toString((long) this.properties.get(NodeContract.NodeEntry._ID))}
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
        NodeDbHelper dbHelper = new NodeDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = NodeContract.getColumns();
        Cursor cursor = db.query(
                NodeContract.NodeEntry.TABLE_NAME,
                columns,
                NodeContract.NodeEntry._ID + " = ?",
                new String[]{Long.toString(id)},
                null,
                null,
                null,
                "1");
        if (cursor.moveToNext()) {
            Node node = new Node(
                    id,
                    cursor.getLong(cursor.getColumnIndex(NodeContract.NodeEntry.COLUMN_NAME_CRAG_ID)),
                    cursor.getFloat(cursor.getColumnIndex(NodeContract.NodeEntry.COLUMN_NAME_X_COORD)),
                    cursor.getFloat(cursor.getColumnIndex(NodeContract.NodeEntry.COLUMN_NAME_Y_COORD))
            );
            cursor.close();
            db.close();
            return node;
        } else {
            return new Node(0, 0, 0, 0);
        }
    }

    public static HashMap<String, Node> getAllNodesByCragId(Context context, long cragId) {
        String[] reqColumns = NodeContract.getColumns();
        NodeDbHelper dbHelper = new NodeDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query(
                NodeContract.NodeEntry.TABLE_NAME,
                reqColumns,
                NodeContract.NodeEntry.COLUMN_NAME_CRAG_ID + " = ?",
                new String[] {Long.toString(cragId)},
                null, null, null);

        HashMap<String, Node> nodeList = new HashMap<>();
        while (c.moveToNext()) {
            long nodeId = c.getLong(c.getColumnIndex(NodeContract.NodeEntry._ID));
            Node node = new Node(
                    nodeId,
                    c.getLong(c.getColumnIndex(NodeContract.NodeEntry.COLUMN_NAME_CRAG_ID)),
                    c.getFloat(c.getColumnIndex(NodeContract.NodeEntry.COLUMN_NAME_X_COORD)),
                    c.getFloat(c.getColumnIndex(NodeContract.NodeEntry.COLUMN_NAME_Y_COORD))
            );
            nodeList.put(Long.toString(nodeId), node);
        }
        c.close();
        db.close();
        return nodeList;
    }
}
