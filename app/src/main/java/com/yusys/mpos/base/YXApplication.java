package com.yusys.mpos.base;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.landicorp.android.mpos.newReader.LandiReader;

/**
 * Application
 *
 * @author yuanshuai (marshall.yuan@foxmail.com)
 * @since 2016-03-16
 */
public class YXApplication extends Application {

    private static YXApplication instance;
    private static RequestQueue queue;// 请求队列
    public static LandiReader landiReader;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        queue = Volley.newRequestQueue(getApplicationContext());
        landiReader = LandiReader.getInstance(getApplicationContext());
    }

    public static YXApplication getInstance() {
        return instance;
    }

    public static RequestQueue getHttpQueue() {
        return queue;
    }
}