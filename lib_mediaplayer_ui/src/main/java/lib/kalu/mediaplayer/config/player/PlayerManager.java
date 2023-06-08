package lib.kalu.mediaplayer.config.player;

import android.app.Application;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.util.LinkedHashMap;

import lib.kalu.mediaplayer.util.MPLogUtil;

/**
 * @description: 视频播放器管理器，管理当前正在播放的VideoView，以及播放器配置
 * * 你也可以用来保存常驻内存的VideoView，但是要注意通过Application Context创建，
 * * 以免内存泄漏
 * @date: 2021-05-12 14:43
 */
@Keep
public class PlayerManager {

    /**
     * 保存VideoView的容器
     */
//    private LinkedHashMap<String, PlayerView> mVideoViews = new LinkedHashMap<>();

    /**
     * 是否在移动网络下直接播放视频
     */
    private boolean mPlayOnMobileNetwork;

    /**
     * VideoViewManager实例
     */
    private static volatile PlayerManager sInstance;

    /**
     * VideoViewConfig实例
     */
    private PlayerBuilder mPlayerConfig;

    private PlayerManager() {
        mPlayerConfig = new PlayerBuilder.Builder().build();
        mPlayOnMobileNetwork = mPlayerConfig.isCheckMobileNetwork();
    }

    private static class Holder {
        private static final PlayerManager INSTANCE = new PlayerManager();
    }

    public static PlayerManager getInstance() {
        return PlayerManager.Holder.INSTANCE;
    }

    public final void setConfig(@NonNull PlayerBuilder config) {
        this.mPlayerConfig = config;
    }

//    /**
//     * 设置VideoViewConfig
//     */
//    public static void setConfig(PlayerConfig config) {
//        if (sConfig == null) {
//            synchronized (PlayerConfig.class) {
//                if (sConfig == null) {
//                    sConfig = config == null ? PlayerConfig.newBuilder().build() : config;
//                }
//            }
//        }
//    }
//
//    /**
//     * 获取VideoViewConfig
//     */
//    public static PlayerConfig getConfig() {
//        setConfig(null);
//        return sConfig;
//    }

    /**
     * 获取VideoViewConfig
     */
    public final PlayerBuilder getConfig() {
//        setConfig(null);
        return mPlayerConfig;
    }

    /**
     * 获取是否在移动网络下直接播放视频配置
     */
    public boolean playOnMobileNetwork() {
        return mPlayOnMobileNetwork;
    }

    /**
     * 设置是否在移动网络下直接播放视频
     */
    public void setPlayOnMobileNetwork(boolean playOnMobileNetwork) {
        mPlayOnMobileNetwork = playOnMobileNetwork;
    }

    public static PlayerManager instance() {
        if (sInstance == null) {
            synchronized (PlayerManager.class) {
                if (sInstance == null) {
                    sInstance = new PlayerManager();
                }
            }
        }
        return sInstance;
    }

//    /**
//     * 添加VideoView
//     *
//     * @param tag 相同tag的VideoView只会保存一个，如果tag相同则会release并移除前一个
//     */
//    public void add(PlayerView videoView, String tag) {
//        if (!(videoView.getContext() instanceof Application)) {
//            MPLogUtil.log("The Context of this VideoView is not an Application Context," +
//                    "you must remove it after release,or it will lead to memory leek.");
//        }
//        PlayerView old = get(tag);
//        if (old != null) {
//            old.release();
//            remove(tag);
//        }
//        mVideoViews.put(tag, videoView);
//    }
//
//    public PlayerView get(String tag) {
//        return mVideoViews.get(tag);
//    }
//
//    public void remove(String tag) {
//        mVideoViews.remove(tag);
//    }
//
//    public void removeAll() {
//        mVideoViews.clear();
//    }
//
//    /**
//     * 释放掉和tag关联的VideoView，并将其从VideoViewManager中移除
//     */
//    public void releaseByTag(String tag) {
//        releaseByTag(tag, true);
//    }
//
//    public void releaseByTag(String tag, boolean isRemove) {
//        PlayerView videoView = get(tag);
//        if (videoView != null) {
//            videoView.release();
//            if (isRemove) {
//                remove(tag);
//            }
//        }
//    }
//
////    public boolean onBackPress(String tag) {
////        VideoLayout videoView = get(tag);
////        if (videoView == null) {
////            return false;
////        }
////        return videoView.onBackPressed();
////    }

}
