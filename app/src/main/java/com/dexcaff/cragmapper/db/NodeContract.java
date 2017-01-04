package com.dexcaff.cragmapper.db;

import android.provider.BaseColumns;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2016.12.15
 * Project: CragMapper
 * Package: com.dexcaff.cragmapper.db
 */

public final class NodeContract {
    private NodeContract() {}

    public static class NodeEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "nodes";
        public static final String COLUMN_NAME_CRAG_ID = "crag_id";
        public static final String COLUMN_NAME_X_COORD = "x_coord";
        public static final String COLUMN_NAME_Y_COORD = "y_coord";
    }

    public static String[] getColumns() {
        return new String[] {
                NodeEntry._ID,
                NodeEntry.COLUMN_NAME_CRAG_ID,
                NodeEntry.COLUMN_NAME_X_COORD,
                NodeEntry.COLUMN_NAME_Y_COORD
        };
    }
}
