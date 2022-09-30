package com.union.musicplayer;

import android.app.Application;

import com.seed.network.SeedNetEngine;
import com.union.musicplayer.setup.LogSetup;

/**
 * name:chenqingru
 * data:
 * des:
 */
public class AppApplication extends Application {

    private static AppApplication instance;

    public static AppApplication getGlobalContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LogSetup.setupLog(instance);
        SeedNetEngine.ins().initRetrofit();
    }
}
