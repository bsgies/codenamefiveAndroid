package com.itridtechnologies.codenamefive.utils;

import android.app.Application;

public class CNF extends Application {
    private static CNF _instance;

    public static CNF getAppContext() {
        return _instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
    }
}
