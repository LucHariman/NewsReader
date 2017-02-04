package com.luc_hariman.newsreader.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.luc_hariman.newsreader.model.News;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luc on 04.02.17.
 */

public class NewsRepository {

    private static final String TABLE_NAME = "news";
    private final DatabaseHandler mDatabaseHandler;

    public NewsRepository(Context context) {
        mDatabaseHandler = DatabaseHandler.getInstance(context);
    }

    public List<News> getAll() {
        SQLiteDatabase db = mDatabaseHandler.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "title");
        List<News> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                News news = new News(cursor.getString(cursor.getColumnIndex("url")));
                news.setId(cursor.getLong(cursor.getColumnIndex("id")));
                news.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                result.add(news);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }

    public void save(News news) {
        ContentValues values = new ContentValues();
        values.put("url", news.getUrl());
        values.put("title", news.getTitle());
        SQLiteDatabase db = mDatabaseHandler.getWritableDatabase();
        Long id = news.getId();
        if (id == null) {
            id = db.insert(TABLE_NAME, null, values);
            news.setId(id);
        } else {
            db.update(TABLE_NAME, values, "id = ?", new String[] { String.valueOf(id) });
        }
        db.close();
    }

    public void delete(News news) {
        SQLiteDatabase db = mDatabaseHandler.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(news.getId()) });
        db.close();
    }
}
