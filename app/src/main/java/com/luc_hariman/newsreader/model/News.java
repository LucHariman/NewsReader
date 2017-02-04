package com.luc_hariman.newsreader.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.luc_hariman.newsreader.AlarmReceiver;
import com.luc_hariman.newsreader.MainActivity;
import com.luc_hariman.newsreader.SettingsActivity;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luc on 04.02.17.
 */

public class News {

    private Long id;
    private final String url;
    private String title;
    private String description;
    private Uri link;
    private List<RSSItem> posts = new ArrayList<>();
    private Integer notificationHour;
    private Integer notificationMinute;

    public News(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public Uri getLink() {
        return link;
    }

    public List<RSSItem> getPosts() {
        return posts;
    }

    public Integer getNotificationHour() {
        return notificationHour;
    }

    public void setNotificationHour(Integer notificationHour) {
        this.notificationHour = notificationHour;
    }

    public Integer getNotificationMinute() {
        return notificationMinute;
    }

    public void setNotificationMinute(Integer notificationMinute) {
        this.notificationMinute = notificationMinute;
    }

    public boolean isNotificationEnabled() {
        return notificationHour != null && notificationMinute != null;
    }

    public void load(@NonNull final ResultListener resultListener) {
        new AsyncTask<Object, Object, Object>() {

            @Override
            protected Object doInBackground(Object... params) {
                RSSReader reader = new RSSReader();
                try {
                    RSSFeed feed = reader.load(url);
                    return feed;
                } catch (Throwable t) {
                    return t;
                }
            }

            @Override
            protected void onPostExecute(Object result) {
                if (result instanceof RSSFeed) {
                    RSSFeed feed = (RSSFeed) result;
                    title = feed.getTitle();
                    description = feed.getDescription();
                    link = feed.getLink();
                    posts.clear();
                    posts.addAll(feed.getItems());
                    resultListener.onSuccess(News.this);
                } else {
                    resultListener.onError((Throwable) result);
                }
            }
        }.execute();
    }

    public void removeAlarm(Context context) {
        if (isNotificationEnabled()) {
            notificationHour = null;
            notificationMinute = null;
            updateAlarm(context);
        }
    }

    public void updateAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.NEWS_ID, id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        if (isNotificationEnabled()) {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    (notificationHour * 60 + notificationMinute) * 60000,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }

    public interface ResultListener {

        void onSuccess(News news);
        void onError(Throwable t);

    }
}
