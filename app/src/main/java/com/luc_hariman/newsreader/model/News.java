package com.luc_hariman.newsreader.model;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

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

    private final String url;
    private String title;
    private String description;
    private Uri link;
    private List<RSSItem> posts = new ArrayList<>();

    public News(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Uri getLink() {
        return link;
    }

    public void load(@NonNull final ResultListener resultListener) {
        new AsyncTask<Object, Object, Object>() {

            @Override
            protected Object doInBackground(Object... params) {
                RSSReader reader = new RSSReader();
                try {
                    RSSFeed feed = reader.load(url);
                    return feed;
                } catch (RSSReaderException e) {
                    return e;
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

    public interface ResultListener {

        void onSuccess(News news);
        void onError(Throwable t);

    }
}
