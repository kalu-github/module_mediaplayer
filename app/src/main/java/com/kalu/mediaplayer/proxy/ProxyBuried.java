package com.kalu.mediaplayer.proxy;

import android.util.Log;

import lib.kalu.mediaplayer.args.StartArgs;

public final class ProxyBuried implements lib.kalu.mediaplayer.bean.proxy.ProxyBuried {
    @Override
    public void onCall(String name, StartArgs startArgs, long position, long duration) {
        Log.e("ProxyBuried", "onCall => name = " + name + ", position = " + position + ", duration = " + duration + ", url = " + startArgs.getUrl());
    }
}
