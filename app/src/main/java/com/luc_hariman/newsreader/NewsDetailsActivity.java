package com.luc_hariman.newsreader;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by luc on 04.02.17.
 */
public class NewsDetailsActivity extends AppCompatActivity {

    public static final String NEWS_TITLE = "NEWS_TITLE";
    public static final String POST_TITLE = "POST_TITLE";
    public static final String POST_THUMBNAIL = "POST_THUMBNAIL";
    public static final String POST_CONTENT = "POST_CONTENT";
    public static final String POST_LINK = "POST_LINK";
    public static final String NEWS_ID = "NEWS_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        setContentView(R.layout.activity_news_details);
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        TextView titleTextview = (TextView) findViewById(R.id.text_title);
        TextView primaryTextView = (TextView) findViewById(R.id.text_primary);
        TextView secondaryTextView = (TextView) findViewById(R.id.text_secondary);
        titleTextview.setText(getIntent().getStringExtra(NEWS_TITLE));
        primaryTextView.setText(getIntent().getStringExtra(POST_TITLE));
        secondaryTextView.setText(getIntent().getStringExtra(POST_CONTENT));
        Picasso.with(this).load(getIntent().getStringExtra(POST_THUMBNAIL)).into(imageView);
        notificationManager.cancel(Long.valueOf(getIntent().getLongExtra(NEWS_ID, 0)).intValue());
    }

    public void onMoreClick(View v) {
        String url = getIntent().getStringExtra(POST_LINK);
        startActivity(new Intent(this, WebViewActivity.class)
            .putExtra(WebViewActivity.POST_URL, url)
            .putExtra(WebViewActivity.TITLE, getIntent().getStringExtra(NEWS_TITLE)));
        finish();
    }
}
