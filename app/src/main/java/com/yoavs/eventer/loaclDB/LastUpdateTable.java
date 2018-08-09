package com.yoavs.eventer.loaclDB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * @author yoavs
 */

public final class LastUpdateTable {

    public static final String SQL_CREATE_LAST_UPDATE =
            "CREATE TABLE " + LastUpdateEntry.TABLE_NAME + " (" +
                    LastUpdateEntry.NAME + " TEXT," +
                    LastUpdateEntry.KEY + " TEXT, " +
                    LastUpdateEntry.DATE + " DATE," +
                    "PRIMARY KEY (" + LastUpdateEntry.NAME + ", " + LastUpdateEntry.KEY + ") " +
                    ")";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LastUpdateEntry.TABLE_NAME;

    public LastUpdateTable() {
    }

    public static void deleteLastUpdate(String tableName, String key, Context context) {
        SQLiteDatabase db = new LocalDB(context).getWritableDatabase();

        String selection = LastUpdateEntry.NAME + " LIKE ? AND " + LastUpdateEntry.KEY + " LIKE ? ";

        String[] selectionArgs = {tableName, key};

        db.delete(LastUpdateEntry.TABLE_NAME, selection, selectionArgs);
    }

    public static void setLastUpdate(String tableName, String key, Long lastUpdate, Context context) {
        SQLiteDatabase db = new LocalDB(context).getWritableDatabase();

        Long currentDate = getLastUpdate(tableName, key, context);

        if (currentDate == null) {
            String insertSql = "INSERT or replace INTO " + LastUpdateEntry.TABLE_NAME + " (" + LastUpdateEntry.KEY + ", " + LastUpdateEntry.NAME + ", " + LastUpdateEntry.DATE + ") " +
                    " VALUES(" + key + ", " + tableName + ", " + lastUpdate + ") ";

            db.execSQL(insertSql);

        } else {

            String updateSql = "UPDATE " + LastUpdateEntry.TABLE_NAME + " SET " + LastUpdateEntry.DATE + " = " + lastUpdate +
                    " WHERE " + LastUpdateEntry.KEY + " = " + key + " AND " + LastUpdateEntry.NAME + " = " + tableName;

            db.execSQL(updateSql);
        }
    }

    public static Long getLastUpdate(String tableName, String key, Context context) {
        SQLiteDatabase db = new LocalDB(context).getReadableDatabase();

        String[] projection = {
                LastUpdateEntry.DATE
        };

        String selection = LastUpdateEntry.NAME + " = ? AND " + LastUpdateEntry.KEY + " = ?";
        String[] selectionArgs = {tableName, key};


        Cursor cursor = db.query(
                LastUpdateEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Long lastUpdate = null;

        while (cursor.moveToNext()) {
            lastUpdate = cursor.getLong(
                    cursor.getColumnIndexOrThrow(GroupMembersTable.GroupMembersTableEntry.USER_KEY)
            );
        }

        cursor.close();

        return lastUpdate;
    }

    static class LastUpdateEntry implements BaseColumns {
        static final String TABLE_NAME = "lastUpdate";
        static final String NAME = "name";
        static final String KEY = "key";
        static final String DATE = "date";

    }
}
