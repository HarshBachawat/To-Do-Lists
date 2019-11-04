package com.example.ekene.blogzone;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_LIST = "List",TABLE_TASK = "Task";

    // Database Information
    static final String DB_NAME = "Lists.db";

    // database version
    static final int DB_VERSION = 2;

    // Creating table query
    private static final String CREATE_LIST = "create table " + TABLE_LIST +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, list_name text not null, description text, image BLOB, hasLocation boolean, hasTimestamp boolean)",
    CREATE_TASK = "create table " + TABLE_TASK +
            "(_id integer primary key autoincrement, task_name text not null, description text, image BLOB, lat double, lng double, list_id int not null, timestamp text)";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LIST);
        db.execSQL(CREATE_TASK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        onCreate(db);
    }
}
