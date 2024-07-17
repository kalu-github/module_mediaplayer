package com.kalu.mediaplayer;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.type.PlayerType;

public class App extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
