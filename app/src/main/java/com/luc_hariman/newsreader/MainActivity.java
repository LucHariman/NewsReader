package com.luc_hariman.newsreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.luc_hariman.newsreader.model.News;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getCanonicalName();
    private Toolbar mToolbar;
    private Menu mMenu;
    private DrawerLayout mDrawerLayout;
    private List<NewsMenuHolder> newsMenu = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        int itemId = 1001;
        for (String title : new String[] { "News 1", "News 2" }) {
            MenuItem menuItem = mMenu.add(R.id.menu_news_list, itemId++, Menu.NONE, title);
            newsMenu.add(new NewsMenuHolder(menuItem));
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.drawer_item_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else {
            item.setChecked(true);
            // TODO: Find selected news
        }
        mDrawerLayout.closeDrawers();
        return true;
    }

    private static class NewsMenuHolder {

        private MenuItem menuItem;

        NewsMenuHolder(MenuItem menuItem) {
            this.menuItem = menuItem;
        }

        public MenuItem getMenuItem() {
            return menuItem;
        }

    }
}
