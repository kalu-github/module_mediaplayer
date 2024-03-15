package com.kalu.mediaplayer;

import android.util.Log;

import lib.kalu.mediaplayer.buried.BuriedEvent;

public class LogBuriedEvent implements BuriedEvent {

    @Override
    public void onPlaying(String url, long position, long duration) {
        Log.e("LogBuriedEvent", "onPlaying => position = " + position + ", duration = " + duration + ", url = " + url);
    }

    @Override
    public void onExit(String url, long position, long duration) {

    }

    @Override
    public void onCompletion(String url, long position, long duration) {
        Log.e("LogBuriedEvent", "onCompletion => position = " + position + ", duration = " + duration + ", url = " + url);
    }

    @Override
    public void onError(String url, long position, long duration) {
        Log.e("LogBuriedEvent", "onError => position = " + position + ", duration = " + duration + ", url = " + url);
    }

    @Override
    public void onPause(String url, long position, long duration) {

    }

    @Override
    public void onResume(String url, long position, long duration) {

    }

    @Override
    public void onSeek(String url, long position, long duration) {

    }

    @Override
    public void onWindowFull(String url, long position, long duration) {

    }

    @Override
    public void onWindowFloat(String url, long position, long duration) {

    }

    @Override
    public void onWindowSimple(String url, long position, long duration) {

    }
}
