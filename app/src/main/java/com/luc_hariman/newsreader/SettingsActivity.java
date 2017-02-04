package com.luc_hariman.newsreader;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.luc_hariman.newsreader.model.News;
import com.luc_hariman.newsreader.repository.NewsRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luc on 04.02.17.
 */
public class SettingsActivity extends AppCompatActivity {

    private ListView mListView;
    private NewsRepository mNewsRepository;
    private List<News> newsList = new ArrayList<>();
    private NewsListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewsRepository = new NewsRepository(this);
        setContentView(R.layout.activity_settings);
        mListView = (ListView) findViewById(R.id.list_view);
        mAdapter = new NewsListAdapter(this, newsList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openNewsSettingsDialog(newsList.get(position));
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final News news = newsList.get(position);
                new AlertDialog.Builder(SettingsActivity.this)
                        .setMessage(R.string.confirm_subscription_deletion)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mNewsRepository.delete(news);
                                refreshList();
                            }
                        })
                        .setNeutralButton(android.R.string.no, null)
                        .create().show();
                return true;
            }
        });

    }

    public void onAddClick(View v) {
        openNewsSettingsDialog(null);
    }

    private void openNewsSettingsDialog(final News newsToEdit) {
        View dialogView = this.getLayoutInflater().inflate(R.layout.alert_edit_news, null);
        final TextView urlTextView = (TextView) dialogView.findViewById(R.id.edit_url);
        if (newsToEdit != null) {
            urlTextView.setText(newsToEdit.getUrl());
            urlTextView.setEnabled(false);
        }
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle(newsToEdit == null ? getString(R.string.add_subscription) : newsToEdit.getTitle())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = urlTextView.getText().toString();
                        if (!URLUtil.isValidUrl(url)) {
                            return;
                        }

                        News news = newsToEdit == null ? new News(url) : newsToEdit;
                        // TODO: Set other properties
                        mNewsRepository.save(news);
                        refreshList();
                        news.load(new News.ResultListener() {
                            @Override
                            public void onSuccess(News news) {
                                mNewsRepository.save(news);
                                refreshList();
                            }

                            @Override
                            public void onError(Throwable t) {
                                t.printStackTrace();
                            }
                        });
                    }
                })
                .setNeutralButton(android.R.string.cancel, null)
                .create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        newsList.clear();
        newsList.addAll(mNewsRepository.getAll());
        mAdapter.notifyDataSetChanged();
    }

    private class NewsListAdapter extends BaseAdapter {

        private final List<News> mNewsList;
        private final Context mContext;

        public NewsListAdapter(Context context, List<News> newsList) {
            super();
            mContext = context;
            mNewsList = newsList;
        }

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_news, null);
            }
            News item = mNewsList.get(position);
            TextView primaryTextView = (TextView) convertView.findViewById(R.id.text_primary);
            TextView secondaryTextView = (TextView) convertView.findViewById(R.id.text_secondary);
            primaryTextView.setText(item.getTitle());
            secondaryTextView.setText(item.getUrl());
            return convertView;
        }
    }


}
