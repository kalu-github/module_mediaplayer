package lib.kalu.mediaplayer.core.player;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import java.util.HashMap;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.kernel.video.KernelApi;
import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.KernelFactoryManager;
import lib.kalu.mediaplayer.core.render.RenderApi;
import lib.kalu.mediaplayer.util.BitmapUtil;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface PlayerApiKernel extends PlayerApiListener,
        PlayerApiBuriedEvent,
        PlayerApiComponent,
        PlayerApiRender,
        PlayerApiDevice {

    default void setData(@NonNull String data) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_data, data);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => setData => " + e.getMessage());
        }
    }

    default String getData() {
        try {
            Object tag = ((View) this).getTag(R.id.module_mediaplayer_id_player_data);
            if (null == tag)
                throw new Exception("tag error: null");
            return (String) tag;
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => getData => " + e.getMessage());
            return null;
        }
    }

    default void start(@NonNull String url) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        StartBuilder build = builder.build();
        start(build, url);
    }

    default void start(@NonNull StartBuilder builder, @NonNull String playUrl) {
        try {
            if (null == playUrl || playUrl.length() == 0)
                throw new Exception("playUrl error: " + playUrl);
            MPLogUtil.log("PlayerApiKernel => start = > playUrl = " + playUrl);
            callPlayerEvent(PlayerType.StateType.STATE_INIT);
            // 1
            PlayerBuilder config = PlayerManager.getInstance().getConfig();
            MPLogUtil.setLogger(config);
            // 5
            createVideoKernel(builder, config);
            // 4
            createVideoRender();
            // 6
            initVideoKernel(builder, playUrl);
            // 7
            attachVideoRender();
            // 8
            updatePlayerData(builder, playUrl);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => start => " + e.getMessage());
        }
    }

    default void updatePlayerData(@NonNull StartBuilder data, @NonNull String playUrl) {
        try {
            if (null == data)
                throw new Exception("data error: null");
            ((View) this).setTag(R.id.module_mediaplayer_id_player_url, playUrl);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_enable, data.isExternalEnable());
            ((View) this).setTag(R.id.module_mediaplayer_id_player_looping, data.isLoop());
            ((View) this).setTag(R.id.module_mediaplayer_id_player_looping_release, data.isLoopRelease());
            ((View) this).setTag(R.id.module_mediaplayer_id_player_window_visibility_changed_release, data.isWindowVisibilityChangedRelease());
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => updatePlayerData => " + e.getMessage());
        }
    }

    default StartBuilder getStartBuilder() {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0) throw new Exception();
            StartBuilder.Builder builder = new StartBuilder.Builder();
            builder.setMax(getMax());
            builder.setSeek(getSeek());
            builder.setLoop(isLooping());
            builder.setLive(isLive());
            builder.setMute(isMute());
            builder.setWindowVisibilityChangedRelease(isWindowVisibilityChangedRelease());
            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }

    default long getDuration() {
        try {
            KernelApi kernel = getVideoKernel();
            long duration = kernel.getDuration();
            if (duration < 0L) {
                duration = 0L;
            }
            return duration;
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => getDuration => " + e.getMessage());
            return 0L;
        }
    }

    default long getPosition() {
        try {
            KernelApi kernel = getVideoKernel();
            long position = kernel.getPosition();
            if (position < 0L) {
                position = 0L;
            }
            return position;
        } catch (Exception e) {
            return 0L;
        }
    }

    default void setVolume(@FloatRange(from = 0f, to = 1f) float left, @FloatRange(from = 0f, to = 1f) float right) {
        try {
            MPLogUtil.log("PlayerApiKernel => setVolume => left = " + left + ", right = " + right);
            KernelApi kernel = getVideoKernel();
            kernel.setVolume(left, right);
        } catch (Exception e) {
        }
    }

    default void setMute(boolean enable) {
        try {
            MPLogUtil.log("PlayerApiKernel => setMute => enable = " + enable);
            KernelApi kernel = getVideoKernel();
            kernel.setMute(enable);
        } catch (Exception e) {
        }
    }

    default void setLooping(boolean looping) {
        try {
            MPLogUtil.log("PlayerApiKernel => setLooping => looping = " + looping);
            KernelApi kernel = getVideoKernel();
            kernel.setLooping(looping);
        } catch (Exception e) {
        }
    }


    default void release() {
        release(true, true, true);
    }

    default void release(@NonNull boolean releaseTag, boolean isFromUser) {
        release(releaseTag, isFromUser, true);
    }

    default void release(@NonNull boolean releaseTag, boolean isFromUser, boolean isMainThread) {
        try {
            checkVideoKernel();
            if (releaseTag) {
                setData(null);
                releaseTag();
            }
            releaseVideoRender();
            releaseVideoKernel(isFromUser, isMainThread);
            cleanPlayerChangeListener();
            callPlayerEvent(PlayerType.StateType.STATE_RELEASE);
        } catch (Exception e) {
            callPlayerEvent(PlayerType.StateType.STATE_RELEASE_EXCEPTION);
            MPLogUtil.log("PlayerApiKernel => release => " + e.getMessage());
        }
    }

    default void toggle() {
        toggle(false);
    }

    default void toggle(boolean ignore) {
        try {
            MPLogUtil.log("PlayerApiKernel => toggle => ignore = " + ignore);
            boolean playing = isPlaying();
            if (playing) {
                // 埋点
                onBuriedEventPause();
                pause(ignore);
            } else {
                // 埋点
                onBuriedEventResume();
                resume();
            }
        } catch (Exception e) {
        }
    }

    default void pause() {
        pause(false);
    }

    default void pause(boolean ignore) {
        setPlayWhenReady(false);
        pauseVideoKernel(ignore);
    }

    default void stop(boolean callEvent, boolean isMainThread) {
        try {
            stopVideoKernel(callEvent, isMainThread);
        } catch (Exception e) {
        }
    }

    default void restart() {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0)
                throw new Exception("url error: " + url);
            StartBuilder builder = getStartBuilder();
            if (null == builder)
                throw new Exception("builder error: null");
            callPlayerEvent(PlayerType.StateType.STATE_RESTAER);
            start(builder, url);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => restart => " + e.getMessage());
        }
    }

    default void resume() {
        resume(false);
    }

    default void resume(boolean ignore) {
        setPlayWhenReady(true);
        try {
            MPLogUtil.log("PlayerApiKernel => resume => ignore = " + ignore);
            checkVideoKernel();
            resumeVideoKernel(ignore);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => resume => " + e.getMessage());
        }
    }

    default boolean isLooping() {
        try {
            KernelApi kernel = getVideoKernel();
            return kernel.isLooping();
        } catch (Exception e) {
            return (boolean) ((View) this).getTag(R.id.module_mediaplayer_id_player_looping);
        }
    }

    default boolean isLoopingRelease() {
        try {
            return (Boolean) ((View) this).getTag(R.id.module_mediaplayer_id_player_looping_release);
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isWindowVisibilityChangedRelease() {
        try {
            return (Boolean) ((View) this).getTag(R.id.module_mediaplayer_id_player_window_visibility_changed_release);
        } catch (Exception e) {
            return false;
        }
    }

    default long getSeek() {
        try {
            KernelApi kernel = getVideoKernel();
            Object tag = ((View) this).getTag(R.id.module_mediaplayer_id_player_position);
            if (null != tag) {
                kernel.setSeek((Long) tag);
            }
            return kernel.getSeek();
        } catch (Exception e) {
            return 0L;
        }
    }

    default void updateSeek() {
        try {
            KernelApi kernel = getVideoKernel();
            long position = kernel.getPosition();
            ((View) this).setTag(R.id.module_mediaplayer_id_player_position, position);
        } catch (Exception e) {
        }
    }

    default long getMax() {
        try {
            KernelApi kernel = getVideoKernel();
            return kernel.getMax();
        } catch (Exception e) {
            return 0L;
        }
    }

    default String getUrl() {
        try {
            return (String) ((View) this).getTag(R.id.module_mediaplayer_id_player_url);
        } catch (Exception e) {
            return null;
        }
    }

    default void releaseTag() {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_url, null);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_position, null);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_looping, null);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_looping_release, null);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_window_visibility_changed_release, null);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_music_url, null);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_music_looping, null);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_music_seek, null);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_music_play_when_ready, null);
        } catch (Exception e) {
        }
    }

    default void seekTo(@NonNull boolean force) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setMax(getMax());
        builder.setSeek(getSeek());
        builder.setLoop(isLooping());
        builder.setLive(isLive());
        builder.setMute(isMute());
        builder.setWindowVisibilityChangedRelease(isWindowVisibilityChangedRelease());
        StartBuilder build = builder.build();
        seekTo(force, build);
    }

    default void seekTo(@NonNull long seek) {
        seekTo(false, seek, getMax(), isLooping());
    }

    default void seekTo(@NonNull long seek, @NonNull long max) {
        seekTo(false, seek, max, isLooping());
    }

    default void seekTo(@NonNull boolean force, @NonNull long seek) {
        seekTo(force, seek, getMax(), isLooping());
    }

    default void seekTo(@NonNull boolean force, @NonNull long seek, @NonNull long max, @NonNull boolean loop) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setMax(max);
        builder.setSeek(seek);
        builder.setLoop(loop);
        builder.setLive(isLive());
        builder.setWindowVisibilityChangedRelease(isWindowVisibilityChangedRelease());
        StartBuilder build = builder.build();
        seekTo(force, build);
    }

    default void seekTo(@NonNull boolean force, @NonNull StartBuilder builder) {

        try {
            checkVideoKernel();
            if (force) {
                updateVideoKernel(builder);
            }
            long seek = builder.getSeek();
            seekToVideoKernel(seek);
        } catch (Exception e) {
        }
    }

    default boolean isLive() {
        try {
            KernelApi kernel = getVideoKernel();
            return kernel.isLive();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isPlaying() {
        try {
            KernelApi kernel = getVideoKernel();
            return kernel.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isMute() {
        try {
            KernelApi kernel = getVideoKernel();
            return kernel.isMute();
        } catch (Exception e) {
            return false;
        }
    }

    default void setSpeed(float speed) {
        try {
            KernelApi kernel = getVideoKernel();
            kernel.setSpeed(speed);
        } catch (Exception e) {
        }
    }

    default float getSpeed() {
        try {
            KernelApi kernel = getVideoKernel();
            return kernel.getSpeed();
        } catch (Exception e) {
            return 1F;
        }
    }

    /*********************/

    default void checkVideoKernel() throws Exception {
        KernelApi kernel = getVideoKernel();
        if (null == kernel)
            throw new Exception("check kernel is null");
    }

    default void resumeVideoKernel(@NonNull boolean ignore) {
        setPlayWhenReady(true);
        try {
            // 1
            checkVideoKernel();
            // 2
            KernelApi kernel = getVideoKernel();
            kernel.start();
            setScreenKeep(true);
            if (ignore) {
                callPlayerEvent(PlayerType.StateType.STATE_RESUME_IGNORE);
            } else {
                callPlayerEvent(PlayerType.StateType.STATE_RESUME);
                callPlayerEvent(PlayerType.StateType.STATE_KERNEL_RESUME);
            }
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => resumeVideoKernel => " + e.getMessage());
        }
    }

    default void stopVideoKernel(@NonNull boolean call, @NonNull boolean isMainThread) {
        try {
            // 1
            checkVideoKernel();
            // 2
            KernelApi kernel = getVideoKernel();
            kernel.stop(isMainThread);
            setScreenKeep(false);
            if (!call) return;
            callPlayerEvent(PlayerType.StateType.STATE_KERNEL_STOP);
            callPlayerEvent(PlayerType.StateType.STATE_CLOSE);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => stopVideoKernel => " + e.getMessage());
        }
    }

    default void pauseVideoKernel(@NonNull boolean ignore) {
        setPlayWhenReady(false);
        try {
            // 1
            checkVideoKernel();
            // 3
            getVideoKernel().pause();
            // 4
            updateSeek();
            // 5
            setScreenKeep(false);
            callPlayerEvent(ignore ? PlayerType.StateType.STATE_PAUSE_IGNORE : PlayerType.StateType.STATE_PAUSE);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => pauseVideoKernel => " + e.getMessage());
        }
    }

    default void releaseVideoKernel(boolean isFromUser, boolean isMainThread) {
        try {
            // 1
            checkVideoKernel();
            // 2
            KernelApi kernel = getVideoKernel();
            kernel.releaseDecoder(isFromUser, isMainThread);
            setVideoKernel(null);
            setScreenKeep(false);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => releaseVideoKernel => " + e.getMessage());
        }
    }

    default void seekToVideoKernel(long milliSeconds) {
        try {
            // 1
            checkVideoKernel();
            // 2
            KernelApi kernel = getVideoKernel();
            kernel.seekTo(milliSeconds, false);
            setScreenKeep(true);
            if (milliSeconds <= 0)
                return;
            callPlayerEvent(PlayerType.StateType.STATE_INIT_SEEK);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => seekToVideoKernel => " + e.getMessage());
        }
    }

    default void updateVideoKernel(@NonNull StartBuilder builder) {
        try {
            // 1
            checkVideoKernel();
            // 2
            KernelApi kernel = getVideoKernel();
            long seek = builder.getSeek();
            long max = builder.getMax();
            boolean loop = builder.isLoop();
            kernel.update(seek, max, loop);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => updateVideoKernel => " + e.getMessage());
        }
    }

    default void initVideoKernel(@NonNull StartBuilder bundle, @NonNull String playUrl) {
        try {
            // 1
            checkVideoKernel();
            // 2
            KernelApi kernel = getVideoKernel();
            kernel.initDecoder(getBaseContext(), playUrl, bundle);
            setScreenKeep(true);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => initVideoKernel => " + e.getMessage());
        }
    }

    default void playEnd() {
        hideReal();
        setScreenKeep(false);
    }

    /***************************/

    default void setVideoKernel(@PlayerType.KernelType.Value int v) {
        try {
            PlayerManager.getInstance().setVideoKernel(v);
        } catch (Exception e) {
        }
    }

    default void createVideoKernel(@NonNull StartBuilder builder, @NonNull PlayerBuilder config) {

        // 1
        try {
            checkVideoKernel();
            release(false, false);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => createKernel => keenel warning: null");
        }

        // 2
        try {
            int type = PlayerManager.getInstance().getConfig().getVideoKernel();
            KernelApi kernel = KernelFactoryManager.getKernel((PlayerApi) this, type, new KernelApiEvent() {

                @Override
                public void onUpdateTimeMillis(@NonNull boolean isLooping, @NonNull long max, @NonNull long seek, @NonNull long position, @NonNull long duration) {

                    boolean reset;
                    if (max > 0) {
                        long playTime = (position - seek);
                        if (playTime > max) {
                            reset = true;
                        } else {
                            reset = false;
                        }
                    } else {
                        reset = false;
                    }

                    // end
                    if (reset) {
                        // loop
                        if (isLooping) {
                            // 1
                            hideReal();
                            // 2
                            boolean seekHelp = config.isSeekHelp();
                            if (seekHelp) {
                                seekToVideoKernel(1);
                            } else {
                                seekTo(true);
                            }
                        }
                        // stop
                        else {
                            // step1
                            pause(true);
                            // step2
                            playEnd();
                        }
                    }
                    // next
                    else {

                        // 1
                        callUpdateTimeMillis(seek, position, duration);
                        // 2
                        callProgressListener(position, duration);
                    }
                }

                @Override
                public void onEvent(int kernel, int event) {
                    MPLogUtil.log("PlayerApiKernel => onEvent = " + kernel + ", event = " + event);
                    switch (event) {
                        // 网络拉流开始
                        case PlayerType.EventType.EVENT_OPEN_INPUT:
                            hideReal();
                            break;
                        // 初始化开始 => loading start
                        case PlayerType.EventType.EVENT_LOADING_START:
                            callPlayerEvent(PlayerType.StateType.STATE_LOADING_START);
                            break;
                        // 初始化完成 => loading stop
                        case PlayerType.EventType.EVENT_LOADING_STOP:
                            callPlayerEvent(PlayerType.StateType.STATE_LOADING_STOP);
                            break;
                        // 缓冲开始
                        case PlayerType.EventType.EVENT_BUFFERING_START:
                            callPlayerEvent(PlayerType.StateType.STATE_BUFFERING_START);
                            break;
                        // 缓冲结束
                        case PlayerType.EventType.EVENT_BUFFERING_STOP:
                            callPlayerEvent(PlayerType.StateType.STATE_BUFFERING_STOP);
                            break;
                        // 播放开始-快进
                        case PlayerType.EventType.EVENT_VIDEO_START_SEEK:
                            // step1
                            callPlayerEvent(PlayerType.StateType.STATE_START_SEEK);
                            // step2
                            showVideoReal();
                            // step4
                            resume(true);
                            // step6
                            MPLogUtil.log("PlayerApiKernel => onEvent => event_video_start_seek => playWhenReady = " + isPlayWhenReady());
                            if (!isPlayWhenReady()) {
                                pause();
                            }
                            break;
                        // 播放开始
                        case PlayerType.EventType.EVENT_VIDEO_START_903:
                            // step1
                            showVideoReal();
                            // step2
                            checkVideoReal();
                            break;
                        // 播放开始
                        case PlayerType.EventType.EVENT_VIDEO_START:
//                        case PlayerType.EventType.EVENT_VIDEO_SEEK_RENDERING_START: // 视频开始渲染
//            case PlayerType.MediaType.MEDIA_INFO_AUDIO_SEEK_RENDERING_START: // 视频开始渲染

                            // 埋点
                            onBuriedEventPlaying();
                            callPlayerEvent(PlayerType.StateType.STATE_START);
                            // step2
                            showVideoReal();
                            // step3
                            checkVideoReal();
                            // step5
                            MPLogUtil.log("PlayerApiKernel => onEvent => event_video_start_seek => playWhenReady = " + isPlayWhenReady());
                            if (!isPlayWhenReady()) {
                                pause();
                            }
                            break;
                        // 播放错误
                        case PlayerType.EventType.EVENT_ERROR_URL:
                        case PlayerType.EventType.EVENT_ERROR_RETRY:
                        case PlayerType.EventType.EVENT_ERROR_SOURCE:
                        case PlayerType.EventType.EVENT_ERROR_PARSE:
                        case PlayerType.EventType.EVENT_ERROR_NET:

                            // 埋点
                            onBuriedEventError();

                            setScreenKeep(false);
                            callPlayerEvent(PlayerType.StateType.STATE_ERROR);

                            break;
                        // 播放结束
                        case PlayerType.EventType.EVENT_VIDEO_END:

                            // 埋点
                            onBuriedEventCompletion();
                            callPlayerEvent(PlayerType.StateType.STATE_END);

                            boolean looping = isLooping();
                            boolean loopingRelease = isLoopingRelease();
                            MPLogUtil.log("PlayerApiKernel => onEvent = 播放结束 => looping = " + looping + ", loopingRelease = " + loopingRelease);
                            // loop1
                            if (looping && loopingRelease) {
                                release(false, false);
                                restart();
                            }
                            // loop2
                            else if (looping) {
                                hideReal();
                                seekTo(true, builder);
                            }
                            // sample
                            else {
                                playEnd();
                            }

                            break;
                    }
                }

                @Override
                public void onMeasure(int kernel, int videoWidth, int videoHeight, @PlayerType.RotationType.Value int rotation) {
                    MPLogUtil.log("PlayerApiKernel => onMeasure = kernel = " + kernel + ", videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", rotation = " + rotation);
                    setVideoSize(videoWidth, videoHeight);
                    setVideoRotation(rotation);
                }
            });
            // 4
            setVideoKernel(kernel);
            // 5
            createVideoDecoder(config);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => createKernel => " + e.getMessage());
        }
    }

    default void attachVideoRender() {
        try {
            RenderApi render = getVideoRender();
            KernelApi kernel = getVideoKernel();
            render.setKernel(kernel);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => attachRender => " + e.getMessage());
        }
    }

    default void createVideoDecoder(@NonNull PlayerBuilder config) {
        try {
            checkVideoKernel();
            KernelApi kernel = getVideoKernel();
            boolean log = config.isLog();
            int seekParameters = config.getExoSeekParameters();
            kernel.createDecoder(getBaseContext(), log, seekParameters);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiKernel => createVideoDecoder => " + e.getMessage());
        }
    }

//    default String getScreenshotPath() {
//        try {
//            String url = getUrl();
//            MPLogUtil.log("PlayerApiKernel => getScreenshotPath => url => " + url);
//            if (null == url || url.length() == 0)
//                throw new Exception("url error: " + url);
//            long position = getPosition();
//            if (position < 0)
//                throw new Exception("position error: " + position);
//            long duration = getDuration();
//            if (duration < 0)
//                throw new Exception("duration error: " + duration);
//            // android 2.3及其以上版本使用
//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
////            retriever.setDataSource(getBaseContext(), Uri.parse(url));
//            retriever.setDataSource(url, new HashMap<String, String>());
//            // 这一句是必须的
//            String timeString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//            // 获取总长度,这一句也是必须的
//            long titalTime = Long.parseLong(timeString) * 1000;
//            // 通过这个计算出想截取的画面所在的时间
//            long videoPosition = titalTime * position / duration;
//            if (videoPosition < 0)
//                throw new Exception("videoPosition error: " + videoPosition);
//            Bitmap bitmap = retriever.getFrameAtTime(videoPosition, MediaMetadataRetriever.OPTION_CLOSEST);
//            return BitmapUtil.saveScreenshot(getBaseContext(), bitmap);
//        } catch (Exception e) {
//            MPLogUtil.log("PlayerApiKernel => getScreenshotPath => " + e.getMessage());
//            return null;
//        }
//    }
}
