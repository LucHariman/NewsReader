package com.luc_hariman.newsreader;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
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
    private NotificationManager mNotificationManager;
    private ImageView mImageView;
    private TextView mTitleTextview;
    private TextView mPrimaryTextView;
    private TextView mSecondaryTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        setContentView(R.layout.activity_news_details);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mTitleTextview = (TextView) findViewById(R.id.text_title);
        mPrimaryTextView = (TextView) findViewById(R.id.text_primary);
        mSecondaryTextView = (TextView) findViewById(R.id.text_secondary);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTitleTextview.setText(getIntent().getStringExtra(NEWS_TITLE));
        mPrimaryTextView.setText(getIntent().getStringExtra(POST_TITLE));
        mSecondaryTextView.setText(getIntent().getStringExtra(POST_CONTENT));
        Picasso.with(this).load(getIntent().getStringExtra(POST_THUMBNAIL)).into(mImageView);
        mNotificationManager.cancel(Long.valueOf(getIntent().getLongExtra(NEWS_ID, 0)).intValue());
    }

    public void onMoreClick(View v) {
        String url = getIntent().getStringExtra(POST_LINK);
        TaskStackBuilder.create(this)
                .addParentStack(WebViewActivity.class)
                .addNextIntent(new Intent(this, WebViewActivity.class)
                        .putExtra(WebViewActivity.POST_URL, url)
                        .putExtra(WebViewActivity.TITLE, getIntent().getStringExtra(NEWS_TITLE)))
                .startActivities();
        finish();
    }
}
