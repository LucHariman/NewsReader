package com.luc_hariman.newsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.luc_hariman.newsreader.model.News;
import com.luc_hariman.newsreader.repository.NewsRepository;

import java.util.List;

/**
 * Created by luc on 04.02.17.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        NewsRepository newsRepository = new NewsRepository(context);
        List<News> newsList = newsRepository.getAll();
        for (News news : newsList) {
            news.updateAlarm(context);
        }
    }

}
