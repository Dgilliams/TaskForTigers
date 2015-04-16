package com.damosdesigns.taskfortigers.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by damosdesigns on 4/8/15.
 */
public class TaskTable {

    static public final String TASK_TABLE = "task";
    static public final String COLUMN_ID = "_id";
    static public final String COLUMN_PRIORITY = "priority";
    static public final String COLUMN_DESCRIPTION = "description";
    static public final String COLUMN_SUMMARY = "summary";

    static private final String SQL_DB_CREATE
            = "CREATE TABLE " + TASK_TABLE
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PRIORITY + " text not null, "
            + COLUMN_DESCRIPTION + " text not null, "
            + COLUMN_SUMMARY + "text not null"
            + ");";

    static public void onCreate( SQLiteDatabase db ){
        db.execSQL( SQL_DB_CREATE );
    }

    static public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v( TaskTable.class.getName(), "Upgrading from version " + oldVersion + " to " + newVersion + " (All data destroyed) ");
        db.execSQL( "DROP TABLE IF EXISTS " + TASK_TABLE );
        onCreate( db );
    }


}
