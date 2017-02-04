package com.luc_hariman.newsreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luc_hariman.newsreader.model.News;
import com.luc_hariman.newsreader.repository.NewsRepository;
import com.squareup.picasso.Picasso;

import org.mcsoxford.rss.RSSItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PostListAdapter(postList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new PostListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RSSItem item) {
                startActivity(new Intent(MainActivity.this, WebViewActivity.class)
                        .putExtra(WebViewActivity.POST_URL, item.getLink().toString())
                        .putExtra(WebViewActivity.TITLE, getTitle()));
            }
        });

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
        if (currentNewsMenu != null) {
            currentNewsMenu.getMenuItem().setChecked(false);
        }

        if (!holder.equals(currentNewsMenu)) {
            currentNewsMenu = holder;
            postList.clear();
            mAdapter.notifyDataSetChanged();
            currentNewsMenu.getMenuItem().setChecked(true);
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
                Collections.sort(postList, new Comparator<RSSItem>() {
                    @Override
                    public int compare(RSSItem o1, RSSItem o2) {
                        return o2.getPubDate().compareTo(o1.getPubDate());
                    }
                });
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

    private static class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {

        private List<RSSItem> mPostList;
        private OnItemClickListener mOnItemClickListener;

        static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final ImageView imageView;
            private final TextView primaryTextView;
            private final TextView secondaryTextView;
            private final TextView tertiaryTextView;
            private RSSItem mItem;
            private OnItemClickListener mOnItemClickListener;

            ViewHolder(View v) {
                super(v);
                primaryTextView = (TextView) v.findViewById(R.id.text_primary);
                secondaryTextView = (TextView) v.findViewById(R.id.text_secondary);
                tertiaryTextView = (TextView) v.findViewById(R.id.text_tertiary);
                imageView = (ImageView) v.findViewById(R.id.image_view);
                v.setOnClickListener(this);
            }

            void bind(RSSItem item, OnItemClickListener onItemClickListener) {
                this.mItem = item;
                this.mOnItemClickListener = onItemClickListener;
                primaryTextView.setText(item.getTitle());
                secondaryTextView.setText(item.getDescription());
                tertiaryTextView.setText(DateUtils.getRelativeTimeSpanString(
                        item.getPubDate().getTime(),
                        new Date().getTime(),
                        DateUtils.MINUTE_IN_MILLIS));
                Uri thumbnailUri = null;
                if (!item.getThumbnails().isEmpty()) {
                    thumbnailUri = item.getThumbnails().get(0).getUrl();
                }
                if (thumbnailUri != null) {
                    Picasso.with(imageView.getContext()).load(thumbnailUri).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.default_background);
                }
            }

            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null && mItem != null) {
                    mOnItemClickListener.onItemClick(mItem);
                }
            }
        }

        interface OnItemClickListener {
            void onItemClick(RSSItem item);
        }

        PostListAdapter(List<RSSItem> postList) {
            mPostList = postList;
        }

        void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        @Override
        public PostListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.item_post, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public int getItemCount() {
            return mPostList.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            RSSItem item = mPostList.get(position);
            holder.bind(item, mOnItemClickListener);
        }

    }
}
