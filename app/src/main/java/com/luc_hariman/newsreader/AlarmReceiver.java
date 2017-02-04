package com.luc_hariman.newsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.luc_hariman.newsreader.model.News;
import com.luc_hariman.newsreader.repository.NewsRepository;

/**
 * Created by luc on 04.02.17.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final String NEWS_ID = "NEWS_ID";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Long newsId = intent.getLongExtra(NEWS_ID, 0);
        NewsRepository newsRepository = new NewsRepository(context);
        News news = newsRepository.getById(newsId);
        final NewsReaderApplication app = (NewsReaderApplication) context.getApplicationContext();
        if (news == null)
            return;
        news.load(new News.ResultListener() {
            @Override
            public void onSuccess(News news) {
                app.notifyNews(news);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }
        });

    }

}
