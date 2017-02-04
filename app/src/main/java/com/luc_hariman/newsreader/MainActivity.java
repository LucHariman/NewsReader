package com.luc_hariman.newsreader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.luc_hariman.newsreader.model.News;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        News news = new News("http://feeds.bbci.co.uk/news/world/rss.xml");

        news.load(new News.ResultListener() {

            @Override
            public void onSuccess(News news) {
                setTitle(news.getTitle());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }
        });

    }
}
