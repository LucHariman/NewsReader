package com.luc_hariman.newsreader.service;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;

import com.luc_hariman.newsreader.NewsDetailsActivity;
import com.luc_hariman.newsreader.NewsReaderApplication;
import com.luc_hariman.newsreader.R;
import com.luc_hariman.newsreader.model.News;
import com.luc_hariman.newsreader.repository.NewsRepository;

import org.mcsoxford.rss.RSSItem;

/**
 * Created by luc on 05.02.17.
 */

public class NotificationService extends IntentService {

    public static final String NEWS_ID = "NEWS_ID";

    private NewsRepository mNewsRepository;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNewsRepository = new NewsRepository(this);
    }

    @Override
    protected void onHandleIntent(Intent data) {
        Long newsId = data.getLongExtra(NEWS_ID, 0);
        News news = mNewsRepository.getById(newsId);
        if (news == null) {
            return;
        }

        try {
            news.loadSync();
        } catch (Throwable t) {
            t.printStackTrace();
            return;
        }

        if (news.getPosts().isEmpty()) {
            return;
        }

        NewsReaderApplication app = (NewsReaderApplication) getApplicationContext();
        Activity currentActivity = app.getCurrentActivity();
        RSSItem item = news.getPosts().get(0);
        Intent intent = new Intent(this, NewsDetailsActivity.class)
                .putExtra(NewsDetailsActivity.NEWS_TITLE, news.getTitle())
                .putExtra(NewsDetailsActivity.POST_TITLE, item.getTitle())
                .putExtra(NewsDetailsActivity.POST_THUMBNAIL, item.getThumbnails().isEmpty() ? null : item.getThumbnails().get(0).toString())
                .putExtra(NewsDetailsActivity.POST_CONTENT, item.getDescription())
                .putExtra(NewsDetailsActivity.POST_LINK, item.getLink().toString())
                .putExtra(NewsDetailsActivity.NEWS_ID, news.getId());

        if (currentActivity == null || currentActivity instanceof NewsDetailsActivity) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(news.getTitle())
                    .setContentText(item.getTitle())
                    .setSmallIcon(R.drawable.ic_rss_feed_black_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setVibrate(new long[] { 1000, 1000 } )
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .build();
            notificationManager.notify(news.getId().intValue(), notification);
        } else {
            currentActivity.startActivity(intent);
        }
    }
}
