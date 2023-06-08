package com.kalu.mediaplayer;

import android.util.Log;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.buried.BuriedEvent;

public class LogBuriedEvent implements BuriedEvent {

    @Override
    public void onPlaying(@NonNull String url, @NonNull long position, @NonNull long duration) {
        Log.e("LogBuriedEvent", "onPlaying => position = " + position + ", duration = " + duration + ", url = " + url);
    }

    @Override
    public void onExit(@NonNull String url, @NonNull long position, @NonNull long duration) {

    }

    @Override
    public void onCompletion(@NonNull String url, @NonNull long position, @NonNull long duration) {
        Log.e("LogBuriedEvent", "onCompletion => position = " + position + ", duration = " + duration + ", url = " + url);
    }

    @Override
    public void onError(@NonNull String url, @NonNull long position, @NonNull long duration) {
        Log.e("LogBuriedEvent", "onError => position = " + position + ", duration = " + duration + ", url = " + url);
    }

    @Override
    public void onPause(@NonNull String url, @NonNull long position, @NonNull long duration) {

    }

    @Override
    public void onResume(@NonNull String url, @NonNull long position, @NonNull long duration) {

    }

    @Override
    public void onSeek(@NonNull String url, @NonNull long position, @NonNull long duration) {

    }

    @Override
    public void onWindowFull(@NonNull String url, @NonNull long position, @NonNull long duration) {

    }

    @Override
    public void onWindowFloat(@NonNull String url, @NonNull long position, @NonNull long duration) {

    }

    @Override
    public void onWindowSimple(@NonNull String url, @NonNull long position, @NonNull long duration) {

    }
}
