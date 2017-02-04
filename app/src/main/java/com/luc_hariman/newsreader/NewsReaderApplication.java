package com.luc_hariman.newsreader;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.luc_hariman.newsreader.model.News;

import org.mcsoxford.rss.RSSItem;

/**
 * Created by luc on 04.02.17.
 */
public class NewsReaderApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    public void notifyNews(News news) {
        if (news.getPosts().isEmpty()) {
            return;
        }
        RSSItem item = news.getPosts().get(0);
        Intent intent = new Intent(this, NewsDetailsActivity.class);
        intent.putExtra(NewsDetailsActivity.NEWS_TITLE, news.getTitle());
        intent.putExtra(NewsDetailsActivity.POST_TITLE, item.getTitle());
        intent.putExtra(NewsDetailsActivity.POST_THUMBNAIL, item.getThumbnails().isEmpty() ? null : item.getThumbnails().get(0).toString());
        intent.putExtra(NewsDetailsActivity.POST_CONTENT, item.getDescription());
        intent.putExtra(NewsDetailsActivity.POST_LINK, item.getLink().toString());
        intent.putExtra(NewsDetailsActivity.NEWS_ID, news.getId());

        if (currentActivity == null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(news.getTitle())
                    .setContentText(item.getTitle())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .build();
            notificationManager.notify(news.getId().intValue(), notification);
        } else {
            currentActivity.startActivity(intent);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        currentActivity = null;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
