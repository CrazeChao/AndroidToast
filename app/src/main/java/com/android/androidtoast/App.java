package com.android.androidtoast;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * Created by lizhichao on 2021/10/11
 */
public class App extends Application {
    private static final String TAG = "com.android.androidtoast => App";

    public static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("onConfigurationChanged","Changed");
    }
}