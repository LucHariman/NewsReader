package com.luc_hariman.newsreader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * Created by luc on 04.02.17.
 */
public class WebViewActivity extends AppCompatActivity {

    public static final String POST_URL = "POST_URL";
    public static final String TITLE = "TITLE";
    private WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        mWebView = (WebView) findViewById(R.id.web_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.loadUrl(getIntent().getStringExtra(POST_URL));
        setTitle(getIntent().getStringExtra(TITLE));
    }
}
