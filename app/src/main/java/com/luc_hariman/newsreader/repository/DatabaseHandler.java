package com.luc_hariman.newsreader.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by luc on 04.02.17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "news_reader_db";
    private static final int DATABASE_VERSION = 4;

    private static DatabaseHandler mInstance;

    public static DatabaseHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return mInstance;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE News (id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT UNIQUE, title TEXT, notify TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS News");
        onCreate(db);
    }
}
