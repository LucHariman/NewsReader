package com.luc_hariman.newsreader;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.luc_hariman.newsreader.model.News;
import com.luc_hariman.newsreader.repository.NewsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        final CheckBox notifyCheckBox = (CheckBox) dialogView.findViewById(R.id.checkBox_notify);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker);
        timePicker.setEnabled(false);
        timePicker.setEnabled(false);
        notifyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                timePicker.setEnabled(isChecked);
            }
        });
        int hour = 9, minute = 0;

        if (newsToEdit != null) {
            urlTextView.setText(newsToEdit.getUrl());
            urlTextView.setEnabled(false);
            if (newsToEdit.isNotificationEnabled()) {
                hour = newsToEdit.getNotificationHour();
                minute = newsToEdit.getNotificationMinute();
                notifyCheckBox.setChecked(true);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        } else {
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
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
                        Integer hour = null, minute = null;
                        if (notifyCheckBox.isChecked()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                hour = timePicker.getHour();
                                minute = timePicker.getMinute();
                            } else {
                                hour = timePicker.getCurrentHour();
                                minute = timePicker.getCurrentMinute();
                            }
                        }
                        news.setNotificationHour(hour);
                        news.setNotificationMinute(minute);
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

        NewsListAdapter(Context context, List<News> newsList) {
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
            View notificationView = convertView.findViewById(R.id.view_notification);
            TextView notificationTimeTextView = (TextView) convertView.findViewById(R.id.text_notification_time);
            primaryTextView.setText(item.getTitle());
            secondaryTextView.setText(item.getUrl());
            if (item.isNotificationEnabled()) {
                notificationView.setVisibility(View.VISIBLE);
                int hour = item.getNotificationHour();
                int minute = item.getNotificationMinute();
                boolean isPm = hour >= 12;
                hour %= 12;
                if (hour == 0) {
                    hour = 12;
                }
                notificationTimeTextView.setText(String.format(
                        Locale.getDefault(),
                        "%d:%02d %s",
                        hour,
                        minute,
                        isPm ? "PM" : "AM"
                ));
            } else {
                notificationView.setVisibility(View.GONE);
            }
            return convertView;
        }
    }


}
