package com.cybor.studhelper;

import android.app.Application;

import io.realm.Realm;


public class StudHelperApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Realm.init(this);
    }
}
