package com.luc_hariman.newsreader;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by luc on 04.02.17.
 */
public class NewsReaderApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        currentActivity = null;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
