package com.cybor.studhelper;

import android.app.Application;

import io.realm.Realm;
import okhttp3.OkHttpClient;


public class StudHelperApplication extends Application
{
    public static OkHttpClient httpClient;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Realm.init(this);
        httpClient = new OkHttpClient();
    }
}
