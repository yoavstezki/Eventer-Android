package com.yoavs.eventer.loaclDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author yoavs
 */

public class LocalDB extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "Eventer.db";

    public LocalDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LastUpdateTable.SQL_CREATE_LAST_UPDATE);
        db.execSQL(GroupMembersTable.SQL_CREATE_GROUP_MEMBERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(LastUpdateTable.SQL_DELETE_ENTRIES);
    }
}
