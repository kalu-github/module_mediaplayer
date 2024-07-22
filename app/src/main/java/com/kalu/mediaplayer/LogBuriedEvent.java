package com.kalu.mediaplayer;

import android.util.Log;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.buried.BuriedEvent;

public class LogBuriedEvent implements BuriedEvent {

    @Override
    public void onRenderFirstFrame(@NonNull StartArgs args, long position, long duration) {
        Log.e("LogBuriedEvent", "onRenderFirstFrame => position = " + position + ", duration = " + duration + ", url = " + args.getUrl());
    }

    @Override
    public void onStart(@NonNull StartArgs args, long position, long duration) {
        Log.e("LogBuriedEvent", "onStart => position = " + position + ", duration = " + duration + ", url = " + args.getUrl());
    }

    @Override
    public void onError(@NonNull StartArgs args, long position, long duration, int code) {
        Log.e("LogBuriedEvent", "onError => position = " + position + ", duration = " + duration + ", url = " + args.getUrl());
    }

    @Override
    public void onPause(@NonNull StartArgs args, long position, long duration) {
        Log.e("LogBuriedEvent", "onPause => position = " + position + ", duration = " + duration + ", url = " + args.getUrl());
    }

    @Override
    public void onResume(@NonNull StartArgs args, long position, long duration) {
        Log.e("LogBuriedEvent", "onResume => position = " + position + ", duration = " + duration + ", url = " + args.getUrl());
    }

    @Override
    public void onComplete(@NonNull StartArgs args, long position, long duration) {
        Log.e("LogBuriedEvent", "onComplete => position = " + position + ", duration = " + duration + ", url = " + args.getUrl());
    }

    @Override
    public void onStop(@NonNull StartArgs args, long position, long duration) {
        Log.e("LogBuriedEvent", "onStop => position = " + position + ", duration = " + duration + ", url = " + args.getUrl());
    }

    @Override
    public void onBufferingStart(@NonNull StartArgs args, long position, long duration) {
        Log.e("LogBuriedEvent", "onBufferingStart => position = " + position + ", duration = " + duration + ", url = " + args.getUrl());
    }

    @Override
    public void onBufferingStop(@NonNull StartArgs args, long position, long duration) {
        Log.e("LogBuriedEvent", "onBufferingStop => position = " + position + ", duration = " + duration + ", url = " + args.getUrl());
    }

    @Override
    public void onSeekStartForward(@NonNull StartArgs args, long position, long duration) {
        Log.e("LogBuriedEvent", "onSeekStartForward => position = " + position + ", duration = " + duration + ", url = " + args.getUrl());
    }

    @Override
    public void onSeekStartRewind(@NonNull StartArgs args, long position, long duration) {
        Log.e("LogBuriedEvent", "onSeekStartRewind => position = " + position + ", duration = " + duration + ", url = " + args.getUrl());
    }

    @Override
    public void onSeekFinish(@NonNull StartArgs args, long position, long duration) {
        Log.e("LogBuriedEvent", "onSeekFinish => position = " + position + ", duration = " + duration + ", url = " + args.getUrl());
    }

    @Override
    public void onWindow(@NonNull StartArgs args, long position, long duration, int type) {
        Log.e("LogBuriedEvent", "onWindow => position = " + position + ", duration = " + duration + ", url = " + args.getUrl());
    }
}
