package com.luc_hariman.newsreader.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.luc_hariman.newsreader.service.NotificationService;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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

    public void load(final ResultListener resultListener) {
        new AsyncTask<Object, Object, Throwable>() {

            @Override
            protected Throwable doInBackground(Object... params) {
                try {
                    loadSync();
                } catch (Throwable t) {
                    return t;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Throwable e) {
                if (resultListener != null) {
                    if (e == null) {
                        resultListener.onSuccess(News.this);
                    } else {
                        resultListener.onError(e);
                    }
                }
            }
        }.execute();
    }

    public void loadSync() throws Throwable {
        RSSReader reader = new RSSReader();
        RSSFeed feed = reader.load(url);
        title = feed.getTitle();
        description = feed.getDescription();
        link = feed.getLink();
        posts.clear();
        posts.addAll(feed.getItems());
        Collections.sort(posts, new Comparator<RSSItem>() {
            @Override
            public int compare(RSSItem o1, RSSItem o2) {
                return o2.getPubDate().compareTo(o1.getPubDate());
            }
        });
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
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra(NotificationService.NEWS_ID, id);
        PendingIntent pendingIntent = PendingIntent.getService(context, id.intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (isNotificationEnabled()) {

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, notificationHour);
            cal.set(Calendar.MINUTE, notificationMinute);
            cal.set(Calendar.SECOND, 0);
            if (cal.before(Calendar.getInstance())) {
                cal.add(Calendar.DATE, 1);
            }

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(),
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
