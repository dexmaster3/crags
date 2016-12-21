package com.dexcaff.cragmapper.db;

import android.provider.BaseColumns;

import java.lang.reflect.Field;

/**
 * @author Dexter <code@dexcaff.com>
 * @version 2016.12.15
 * Project: CragMapper
 * Package: com.dexcaff.cragmapper.db
 */

public final class CragContract {
    private CragContract() {}

    public static class CragEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "crags";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_RATING = "rating";
    }

    public static String[] getColumns() {
        Field[] fields = String.class.getDeclaredFields();
        String[] strings = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            strings[i] = fields[i].toString();
        }
        return strings;
    }
}
