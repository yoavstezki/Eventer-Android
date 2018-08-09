package com.yoavs.eventer.loaclDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yoavs
 */

public final class GroupMembersTable {

    public static final String SQL_CREATE_GROUP_MEMBERS =
            "CREATE TABLE " + GroupMembersTableEntry.TABLE_NAME + " (" +
                    GroupMembersTableEntry.USER_KEY + " TEXT," +
                    GroupMembersTableEntry.GROUP_KEY + " TEXT, " +
                    "PRIMARY KEY (" + GroupMembersTableEntry.USER_KEY + ", " + GroupMembersTableEntry.GROUP_KEY + ") " +
                    ")";
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LastUpdateTable.LastUpdateEntry.TABLE_NAME;


    public GroupMembersTable() {
    }

    public static List<String> getUserKeysByGroupKey(String groupKey, Context context) {
        SQLiteDatabase db = new LocalDB(context).getReadableDatabase();

        String[] projection = {
                GroupMembersTableEntry.USER_KEY
        };

        String selection = GroupMembersTableEntry.GROUP_KEY + " = ?";
        String[] selectionArgs = {groupKey};


        Cursor cursor = db.query(
                GroupMembersTableEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        List<String> userKeys = new ArrayList<>();

        while (cursor.moveToNext()) {
            String userKey = cursor.getString(
                    cursor.getColumnIndexOrThrow(GroupMembersTableEntry.USER_KEY)
            );
            userKeys.add(userKey);
        }

        cursor.close();

        return userKeys;
    }

    public static void updateUserKeys(List<String> userKeys, String groupKey, Context context) {

        SQLiteDatabase db = new LocalDB(context).getWritableDatabase();

        removeUsersFromGroup(groupKey, db);

        for (String userKey : userKeys) {
            addUserToGroup(userKey, groupKey, db);
        }

        db.close();
    }

    private static void addUserToGroup(String userKey, String groupKey, SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put(GroupMembersTableEntry.USER_KEY, userKey);
        values.put(GroupMembersTableEntry.GROUP_KEY, groupKey);

        long newRowId = db.insert(GroupMembersTableEntry.TABLE_NAME, null, values);
    }

    private static void removeUsersFromGroup(String groupKey, SQLiteDatabase db) {
        String selection = GroupMembersTableEntry.GROUP_KEY + " LIKE ?";

        String[] selectionArgs = {groupKey};

        db.delete(GroupMembersTableEntry.TABLE_NAME, selection, selectionArgs);
    }

    public static class GroupMembersTableEntry implements BaseColumns {
        public static final String TABLE_NAME = "groupMembers";
        static final String USER_KEY = "userKey";
        static final String GROUP_KEY = "groupKey";
    }
}
