package com.luc_hariman.newsreader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.luc_hariman.newsreader.model.News;
import com.luc_hariman.newsreader.repository.NewsRepository;

import org.mcsoxford.rss.RSSItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getCanonicalName();
    private Toolbar mToolbar;
    private Menu mMenu;
    private DrawerLayout mDrawerLayout;
    private List<NewsMenuHolder> newsMenu = new ArrayList<>();
    private NewsMenuHolder currentNewsMenu = null;
    private NewsRepository mNewsRepository;
    private List<RSSItem> postList = new ArrayList<>();
    private PostListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNewsRepository = new NewsRepository(this);

        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        mMenu = navigationView.getMenu();

        navigationView.setNavigationItemSelectedListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        ListView listView = (ListView) findViewById(R.id.list_view);
        mAdapter = new PostListAdapter(this, postList);
        listView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNewsMenu();
    }

    private void refreshNewsMenu() {
        for (NewsMenuHolder menuHolder : newsMenu) {
            mMenu.removeItem(menuHolder.getMenuItem().getItemId());
        }

        newsMenu.clear();

        List<News> newsList = mNewsRepository.getAll();

        int itemId = 1001;
        for (News news : newsList) {
            String title = news.getTitle();
            if (TextUtils.isEmpty(title)) {
                title = getString(R.string.unknown);
            }
            MenuItem menuItem = mMenu.add(R.id.menu_news_list, itemId++, Menu.NONE, title);
            NewsMenuHolder holder = new NewsMenuHolder(menuItem, news);
            newsMenu.add(holder);
            if (currentNewsMenu == null) {
                setCurrentNewsMenu(holder);
            }
        }

    }

    private void setCurrentNewsMenu(NewsMenuHolder holder) {
        if (!holder.equals(currentNewsMenu)) {
            currentNewsMenu = holder;
            postList.clear();
            mAdapter.notifyDataSetChanged();
        }

        News selectedNews = currentNewsMenu.getNews();
        String title = selectedNews.getTitle();
        if (TextUtils.isEmpty(title)) {
            title = getString(R.string.app_name);
        }
        setTitle(title);
        selectedNews.load(new News.ResultListener() {
            @Override
            public void onSuccess(News news) {
                postList.clear();
                postList.addAll(news.getPosts());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                Snackbar.make(findViewById(android.R.id.content), R.string.error_failed_to_load_news, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.drawer_item_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else {
            if (currentNewsMenu != null) {
                currentNewsMenu.getMenuItem().setChecked(false);
            }
            item.setChecked(true);
            for (NewsMenuHolder holder : newsMenu) {
                if (holder.getMenuItem().equals(item)) {
                    setCurrentNewsMenu(holder);
                    break;
                }
            }
        }
        mDrawerLayout.closeDrawers();
        return true;
    }

    private static class NewsMenuHolder {

        private MenuItem menuItem;
        private News news;

        NewsMenuHolder(MenuItem menuItem, News news) {
            this.menuItem = menuItem;
            this.news = news;
        }

        MenuItem getMenuItem() {
            return menuItem;
        }

        News getNews() {
            return news;
        }

    }

    private static class PostListAdapter extends BaseAdapter {

        private final Context mContext;
        private List<RSSItem> mPostList;

        PostListAdapter(Context context, List<RSSItem> postList) {
            mContext = context;
            mPostList = postList;
        }

        @Override
        public int getCount() {
            return mPostList.size();
        }

        @Override
        public Object getItem(int position) {
            return mPostList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_post, null);
            }
            RSSItem item = mPostList.get(position);
            TextView primaryTextView = (TextView) convertView.findViewById(R.id.text_primary);
            primaryTextView.setText(item.getTitle());
            return convertView;
        }
    }
}
