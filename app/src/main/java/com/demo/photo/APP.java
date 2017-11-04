package com.demo.photo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * description: 初始化操作
 * created by kalu on 2016/11/9 16:39
 */
public class APP extends Application {

    private final String TAG = "Application";

    private static APP mApplication;

    public static APP getInstance() {
        return mApplication;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

    }
}