package com.luc_hariman.newsreader.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.luc_hariman.newsreader.model.News;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
                result.add(newsFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }

    public News getById(Long newsId) {
        SQLiteDatabase db = mDatabaseHandler.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "id = ?", new String[] { String.valueOf(newsId) }, null, null, null);
        News result = null;
        if (cursor.moveToFirst()) {
            result = newsFromCursor(cursor);
        }
        cursor.close();
        db.close();
        return result;
    }

    private News newsFromCursor(Cursor cursor) {
        News result = new News(cursor.getString(cursor.getColumnIndex("url")));
        result.setId(cursor.getLong(cursor.getColumnIndex("id")));
        result.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        String notify = cursor.getString(cursor.getColumnIndex("notify"));
        Integer notificationHour = null, notificationMinute = null;
        if (!TextUtils.isEmpty(notify)) {
            String[] time = notify.split(":");
            notificationHour = Integer.valueOf(time[0]);
            notificationMinute = Integer.valueOf(time[1]);
        }
        result.setNotificationHour(notificationHour);
        result.setNotificationMinute(notificationMinute);
        return result;
    }

    public void save(News news) {
        ContentValues values = new ContentValues();
        values.put("url", news.getUrl());
        values.put("title", news.getTitle());
        String notify = null;
        if (news.isNotificationEnabled()) {
            notify = String.format(Locale.getDefault(), "%d:%d", news.getNotificationHour(), news.getNotificationMinute());
        }
        values.put("notify", notify);
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
