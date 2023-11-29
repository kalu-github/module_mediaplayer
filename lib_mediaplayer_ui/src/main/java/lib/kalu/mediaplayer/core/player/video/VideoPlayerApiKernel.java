package lib.kalu.mediaplayer.core.player.video;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import org.json.JSONArray;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactoryManager;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface VideoPlayerApiKernel extends VideoPlayerApiListener,
        VideoPlayerApiBuriedEvent,
        VideoPlayerApiComponent,
        VideoPlayerApiRender,
        VideoPlayerApiDevice {

    default void setData(@NonNull String data) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_data, data);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => setData => " + e.getMessage());
        }
    }

    default String getData() {
        try {
            Object tag = ((View) this).getTag(R.id.module_mediaplayer_id_player_data);
            if (null == tag)
                throw new Exception("tag error: null");
            return (String) tag;
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => getData => " + e.getMessage());
            return null;
        }
    }

    default void start(@NonNull String url) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        StartBuilder build = builder.build();
        start(build, url, false);
    }


    default void start(@NonNull StartBuilder builder, @NonNull String playUrl) {
        start(builder, playUrl, false);
    }

    @Override
    default void start(@NonNull StartBuilder startBuilder, @NonNull String playUrl, @NonNull boolean retryBuffering) {
        try {
            if (null == playUrl || playUrl.length() == 0)
                throw new Exception("playUrl error: " + playUrl);
            if (retryBuffering) {
                callPlayerEvent(PlayerType.StateType.STATE_INIT_RETEY_BUFFERING);
            } else {
                callPlayerEvent(PlayerType.StateType.STATE_INIT);
            }
            // 1
            PlayerBuilder playerBuilder = PlayerManager.getInstance().getConfig();
            MPLogUtil.setLogger(playerBuilder);
            //
            // 3
            createVideoKernel(startBuilder, playerBuilder);
            // 4
            createVideoRender();
            // 5
            initVideoKernel(startBuilder, playUrl);
            // 6
            attachVideoRender();
            // 7
            updatePlayerData(startBuilder, playUrl);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => start => " + e.getMessage());
        }
    }

    default void updatePlayerData(@NonNull StartBuilder data, @NonNull String playUrl) {
        try {
            if (null == data)
                throw new Exception("data error: null");
            ((View) this).setTag(R.id.module_mediaplayer_id_player_url, playUrl);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_looping, data.isLooping());
            ((View) this).setTag(R.id.module_mediaplayer_id_player_looping_release, data.isLoopingRelease());
            ((View) this).setTag(R.id.module_mediaplayer_id_player_window_visibility_changed_release, data.isWindowVisibilityChangedRelease());
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => updatePlayerData => " + e.getMessage());
        }
    }

    default StartBuilder getStartBuilder() {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0) throw new Exception();
            StartBuilder.Builder builder = new StartBuilder.Builder();
            builder.setMax(getMax());
            builder.setSeek(getSeek());
            builder.setLooping(isLooping());
            builder.setLoopingRelease(isLoopingRelease());
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
            VideoKernelApi kernel = getVideoKernel();
            long duration = kernel.getDuration();
            if (duration < 0L) {
                duration = 0L;
            }
            return duration;
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => getDuration => " + e.getMessage());
            return 0L;
        }
    }

    default long getPosition() {
        try {
            VideoKernelApi kernel = getVideoKernel();
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
            MPLogUtil.log("VideoPlayerApiKernel => setVolume => left = " + left + ", right = " + right);
            VideoKernelApi kernel = getVideoKernel();
            kernel.setVolume(left, right);
        } catch (Exception e) {
        }
    }

    default void setMute(boolean enable) {
        try {
            MPLogUtil.log("VideoPlayerApiKernel => setMute => enable = " + enable);
            VideoKernelApi kernel = getVideoKernel();
            kernel.setMute(enable);
        } catch (Exception e) {
        }
    }

    default void setLooping(boolean looping) {
        try {
            MPLogUtil.log("VideoPlayerApiKernel => setLooping => looping = " + looping);
            VideoKernelApi kernel = getVideoKernel();
            kernel.setLooping(looping);
        } catch (Exception e) {
        }
    }


    default void release() {
        release(true, true, true, true);
    }

    default void release(@NonNull boolean releaseTag, boolean isFromUser) {
        release(true, releaseTag, isFromUser, true);
    }

    default void release(@NonNull boolean releaseTag, boolean isFromUser, boolean isMainThread) {
        release(true, releaseTag, isFromUser, isMainThread);
    }

    default void release(@NonNull boolean clearListener, @NonNull boolean releaseTag, boolean isFromUser, boolean isMainThread) {
        try {
            checkVideoKernel();
            if (releaseTag) {
                setData(null);
                releaseTag();
            }
            releaseVideoRender();
            releaseVideoKernel(isFromUser, isMainThread);
            if (clearListener) {
                removeOnPlayerChangeListener();
            }
            callPlayerEvent(PlayerType.StateType.STATE_RELEASE);
        } catch (Exception e) {
            callPlayerEvent(PlayerType.StateType.STATE_RELEASE_EXCEPTION);
            MPLogUtil.log("VideoPlayerApiKernel => release => " + e.getMessage());
        }
    }

    default void toggle() {
        toggle(false);
    }

    default void toggle(boolean ignore) {
        try {
            MPLogUtil.log("VideoPlayerApiKernel => toggle => ignore = " + ignore);
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
        restart(0, false);
    }

    default void restart(@NonNull long seek, @NonNull boolean retryBuffering) {
        try {
            String url = getUrl();
            if (null == url || url.length() == 0)
                throw new Exception("url error: " + url);
            setSeek(seek);
            StartBuilder builder = getStartBuilder();
            if (null == builder)
                throw new Exception("builder error: null");
            callPlayerEvent(PlayerType.StateType.STATE_RESTAER);
            start(builder, url, retryBuffering);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => restart => " + e.getMessage());
        }
    }

    default void resume() {
        resume(false);
    }

    default void resume(boolean ignore) {
        setPlayWhenReady(true);
        try {
            MPLogUtil.log("VideoPlayerApiKernel => resume => ignore = " + ignore);
            checkVideoKernel();
            resumeVideoKernel(ignore);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => resume => " + e.getMessage());
        }
    }

    default boolean isLooping() {
        try {
            VideoKernelApi kernel = getVideoKernel();
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
            VideoKernelApi kernel = getVideoKernel();
            Object tag = ((View) this).getTag(R.id.module_mediaplayer_id_player_position);
            if (null != tag) {
                kernel.setSeek((Long) tag);
            }
            return kernel.getSeek();
        } catch (Exception e) {
            return 0L;
        }
    }

    default void setSeek(@NonNull long position) {
        try {
            if (position < 0)
                throw new Exception("position error: " + position);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_position, position);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => setSeek => " + e.getMessage());
        }
    }

    default void refeshSeek() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("kernel error: null");
            long position = kernel.getPosition();
            if (position < 0)
                throw new Exception("position error: " + position);
            setSeek(position);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => setSeek => " + e.getMessage());
        }
    }

    default long getMax() {
        try {
            VideoKernelApi kernel = getVideoKernel();
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
        builder.setLooping(isLooping());
        builder.setLoopingRelease(isLoopingRelease());
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

    default void seekTo(@NonNull boolean force, @NonNull long seek, @NonNull long max, @NonNull boolean looping) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        builder.setMax(max);
        builder.setSeek(seek);
        builder.setLooping(looping);
        builder.setLoopingRelease(isLoopingRelease());
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
            VideoKernelApi kernel = getVideoKernel();
            return kernel.isLive();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isPlaying() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            return kernel.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isPrepared() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            return kernel.isPrepared();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isMute() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            return kernel.isMute();
        } catch (Exception e) {
            return false;
        }
    }

    default void setSpeed(float speed) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            kernel.setSpeed(speed);
        } catch (Exception e) {
        }
    }

    default float getSpeed() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            return kernel.getSpeed();
        } catch (Exception e) {
            return 1F;
        }
    }

    /*********************/

    default void checkVideoKernel() throws Exception {
        VideoKernelApi kernel = getVideoKernel();
        if (null == kernel)
            throw new Exception("check kernel is null");
    }

    default void resumeVideoKernel(@NonNull boolean ignore) {
        setPlayWhenReady(true);
        try {
            // 1
            checkVideoKernel();
            // 2
            VideoKernelApi kernel = getVideoKernel();
            kernel.start();
            setScreenKeep(true);
            if (ignore) {
                callPlayerEvent(PlayerType.StateType.STATE_RESUME_IGNORE);
            } else {
                callPlayerEvent(PlayerType.StateType.STATE_RESUME);
                callPlayerEvent(PlayerType.StateType.STATE_KERNEL_RESUME);
            }
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => resumeVideoKernel => " + e.getMessage());
        }
    }

    default void stopVideoKernel(@NonNull boolean call, @NonNull boolean isMainThread) {
        try {
            // 1
            checkVideoKernel();
            // 2
            VideoKernelApi kernel = getVideoKernel();
            kernel.stop(isMainThread);
            setScreenKeep(false);
            if (!call) return;
            callPlayerEvent(PlayerType.StateType.STATE_KERNEL_STOP);
            callPlayerEvent(PlayerType.StateType.STATE_CLOSE);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => stopVideoKernel => " + e.getMessage());
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
            refeshSeek();
            // 5
            setScreenKeep(false);
            callPlayerEvent(ignore ? PlayerType.StateType.STATE_PAUSE_IGNORE : PlayerType.StateType.STATE_PAUSE);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => pauseVideoKernel => " + e.getMessage());
        }
    }

    default void releaseVideoKernel(boolean isFromUser, boolean isMainThread) {
        try {
            // 1
            checkVideoKernel();
            // 2
            VideoKernelApi kernel = getVideoKernel();
            kernel.releaseDecoder(isFromUser, isMainThread);
            setVideoKernel(null);
            setScreenKeep(false);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => releaseVideoKernel => " + e.getMessage());
        }
    }

    default void seekToVideoKernel(long milliSeconds) {
        try {
            // 1
            checkVideoKernel();
            // 2
            VideoKernelApi kernel = getVideoKernel();
            kernel.seekTo(milliSeconds);
            setScreenKeep(true);
            if (milliSeconds <= 0)
                return;
            callPlayerEvent(PlayerType.StateType.STATE_INIT_SEEK);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => seekToVideoKernel => " + e.getMessage());
        }
    }

    default void updateVideoKernel(@NonNull StartBuilder builder) {
        try {
            // 1
            checkVideoKernel();
            // 2
            VideoKernelApi kernel = getVideoKernel();
            long seek = builder.getSeek();
            long max = builder.getMax();
            boolean loop = builder.isLooping();
            kernel.update(seek, max, loop);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => updateVideoKernel => " + e.getMessage());
        }
    }

    default void initVideoKernel(@NonNull StartBuilder bundle, @NonNull String playUrl) {
        try {
            // 1
            checkVideoKernel();
            // 2
            VideoKernelApi kernel = getVideoKernel();
            kernel.initDecoder(getBaseContext(), playUrl, bundle);
            setScreenKeep(true);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => initVideoKernel => " + e.getMessage());
        }
    }

    default void playEnd() {
        hideReal();
        setScreenKeep(false);
    }

    /***************************/

    default void setVideoKernel(@PlayerType.KernelType.Value int v) {
        try {
            PlayerManager.getInstance().setKernel(v);
        } catch (Exception e) {
        }
    }

    default void createVideoKernel(@NonNull StartBuilder builder, @NonNull PlayerBuilder playerBuilder) {

        // 1
        try {
            checkVideoKernel();
            release(false, false);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => createKernel => keenel warning: null");
        }

        // 2
        try {
            int type = PlayerManager.getInstance().getConfig().getKernel();
            VideoKernelApi kernel = VideoKernelFactoryManager.getKernel((VideoPlayerApi) this, type, new VideoKernelApiEvent() {

                @Override
                public void onUpdateTimeMillis(@NonNull boolean isLooping, @NonNull long max, @NonNull long seek, @NonNull long position, @NonNull long duration) {
                    try {
                        if (max <= 0 || position <= max)
                            throw new Exception("time warning: max = " + max + ", position = " + position + ", seek = " + seek + ", duration = " + duration + ", isLooping = " + isLooping);
                        callPlayerEvent(PlayerType.StateType.STATE_TRY_COMPLETE);
                        // 试看1
                        if (isLooping) {
                            boolean seekHelp = playerBuilder.isSeekHelp();
                            if (seekHelp) {
                                seekToVideoKernel(1);
                            } else {
                                seekTo(true);
                            }
                        }
                        // 试看2
                        else {
                            pause(true);
                            playEnd();
                        }
                    } catch (Exception e) {
                        MPLogUtil.log("VideoPlayerApiKernel => onUpdateTimeMillis => " + e.getMessage());
                        callUpdateTimeMillis(seek, position, duration, max);
                        callProgressListener(position, duration);
                    }
                }

                @Override
                public void onEvent(int kernel, int event) {
                    MPLogUtil.log("VideoPlayerApiKernel => onEvent = " + kernel + ", event = " + event);
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
                            startCheckBuffering();
                            break;
                        // 缓冲结束
                        case PlayerType.EventType.EVENT_BUFFERING_STOP:
                            callPlayerEvent(PlayerType.StateType.STATE_BUFFERING_STOP);
                            stopCheckBuffering();
                            break;
                        // 播放开始
                        case PlayerType.EventType.EVENT_VIDEO_START_903:
                            // step1
                            showVideoReal();
                            // step2
                            checkVideoReal();
                            break;
                        // 播放开始
                        case PlayerType.EventType.EVENT_VIDEO_START_RETRY:
                            callPlayerEvent(PlayerType.StateType.STATE_START_RETRY);
                            // step2
                            showVideoReal();
                            // step3
                            checkVideoReal();
                            // step5
                            MPLogUtil.log("VideoPlayerApiKernel => onEvent => event_video_start_retry => playWhenReady = " + isPlayWhenReady());
                            break;
                        // 播放开始-快进
                        case PlayerType.EventType.EVENT_VIDEO_START_SEEK:
                            // 试看
                            if (null != builder) {
                                long max = builder.getMax();
                                if (max > 0) {
                                    callPlayerEvent(PlayerType.StateType.STATE_TRY_BEGIN);
                                }
                            }
                            // step1
                            callPlayerEvent(PlayerType.StateType.STATE_START_SEEK);
                            // step2
                            showVideoReal();
                            // step4
                            resume(true);
                            // step6
                            boolean playWhenReady1 = isPlayWhenReady();
                            MPLogUtil.log("VideoPlayerApiKernel => onEvent => event_video_start_seek => playWhenReady = " + playWhenReady1);
                            if (!playWhenReady1) {
                                pause();
                                setPlayWhenReady(true);
                                callPlayerEvent(PlayerType.StateType.STATE_START_PLAY_WHEN_READY_PAUSE);
                            }
                            break;
                        // 播放开始-默认
                        case PlayerType.EventType.EVENT_VIDEO_START:
//                        case PlayerType.EventType.EVENT_VIDEO_SEEK_RENDERING_START: // 视频开始渲染
//            case PlayerType.MediaType.MEDIA_INFO_AUDIO_SEEK_RENDERING_START: // 视频开始渲染
                            // 埋点
                            onBuriedEventPlaying();
                            // 试看
                            if (null != builder) {
                                long max = builder.getMax();
                                if (max > 0) {
                                    callPlayerEvent(PlayerType.StateType.STATE_TRY_BEGIN);
                                }
                            }
                            callPlayerEvent(PlayerType.StateType.STATE_START);
                            // step2
                            showVideoReal();
                            // step3
                            checkVideoReal();
                            // step4
                            boolean playWhenReady2 = isPlayWhenReady();
                            MPLogUtil.log("VideoPlayerApiKernel => onEvent => event_video_start_seek => playWhenReady = " + playWhenReady2);
                            if (!playWhenReady2) {
                                pause();
                                setPlayWhenReady(true);
                                callPlayerEvent(PlayerType.StateType.STATE_START_PLAY_WHEN_READY_PAUSE);
                            }
                            break;
                        // 播放错误
                        case PlayerType.EventType.EVENT_ERROR_IGNORE:
                            // 埋点
                            onBuriedEventError();
                            setScreenKeep(false);
                            callPlayerEvent(PlayerType.StateType.STATE_ERROR_IGNORE);
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
                            MPLogUtil.log("VideoPlayerApiKernel => onEvent = 播放结束 => looping = " + looping + ", loopingRelease = " + loopingRelease);
                            // loop1
                            if (looping && loopingRelease) {
                                release(false, false);
                                restart();
                            }
                            // loop2
                            else if (looping) {
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
                    MPLogUtil.log("VideoPlayerApiKernel => onMeasure = kernel = " + kernel + ", videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", rotation = " + rotation);
                    setVideoSize(videoWidth, videoHeight);
                    setVideoRotation(rotation);
                }
            });
            // 4
            setVideoKernel(kernel);
            // 5
            createVideoDecoder(playerBuilder);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => createKernel => " + e.getMessage());
        }
    }

    default void attachVideoRender() {
        try {
            VideoRenderApi render = getVideoRender();
            VideoKernelApi kernel = getVideoKernel();
            render.setKernel(kernel);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => attachRender => " + e.getMessage());
        }
    }

    default void createVideoDecoder(@NonNull PlayerBuilder config) {
        try {
            checkVideoKernel();
            VideoKernelApi kernel = getVideoKernel();
            boolean log = config.isLog();
            int seekParameters = config.getExoSeekParameters();
            kernel.createDecoder(getBaseContext(), log, seekParameters);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => createVideoDecoder => " + e.getMessage());
        }
    }

    default JSONArray getTrackInfo() {
        try {
            checkVideoKernel();
            VideoKernelApi kernel = getVideoKernel();
            JSONArray trackInfo = kernel.getTrackInfo();
            if (null == trackInfo)
                throw new Exception("trackInfo error: null");
            return trackInfo;
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => getTrackInfo => " + e.getMessage());
            return null;
        }
    }

    default boolean switchTrack(@NonNull int trackId) {
        try {
            checkVideoKernel();
            VideoKernelApi kernel = getVideoKernel();
            kernel.switchTrack(trackId);
            return true;
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => switchTrack => " + e.getMessage());
            return false;
        }
    }

//    default String getScreenshotPath() {
//        try {
//            String url = getUrl();
//            MPLogUtil.log("VideoPlayerApiKernel => getScreenshotPath => url => " + url);
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
//            MPLogUtil.log("VideoPlayerApiKernel => getScreenshotPath => " + e.getMessage());
//            return null;
//        }
//    }

    /*******************/


    Handler[] mHandlerBuffering = new Handler[1];

    default void startCheckBuffering() {
        try {
            stopCheckBuffering();
            int bufferingTimeoutSeconds = PlayerManager.getInstance().getConfig().getBufferingTimeoutSeconds();
            if (bufferingTimeoutSeconds <= 0)
                throw new Exception("bufferingTimeoutSeconds warning: " + bufferingTimeoutSeconds);
            boolean bufferingTimeoutRetry = PlayerManager.getInstance().getConfig().getBufferingTimeoutRetry();
            mHandlerBuffering[0] = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 7677) {
                        if (bufferingTimeoutRetry) {
                            long seek;
                            try {
                                if (isLive()) {   // 直播
                                    seek = 0;
                                } else {  // 点播
                                    seek = getPosition();
                                }
                            } catch (Exception e) {
                                seek = 0;
                                MPLogUtil.log("VideoPlayerApiKernel => startCheckBuffering => handleMessage => " + e.getMessage());
                            }
                            if (seek < 0) {
                                seek = 0;
                            }
                            MPLogUtil.log("VideoPlayerApiKernel => startCheckBuffering => handleMessage => what = " + msg.what + ", seek = " + seek);

                            release(false, false, false, false);
                            restart(seek, true);
                            callPlayerEvent(PlayerType.StateType.STATE_BUFFERING_START);
                        } else {
                            callPlayerEvent(PlayerType.StateType.STATE_BUFFERING_STOP);
                            callPlayerEvent(PlayerType.StateType.STATE_BUFFERING_TIMEOUT);
                            release(false, false, false, false);
                        }
                    }
                }
            };
            mHandlerBuffering[0].sendEmptyMessageDelayed(7677, bufferingTimeoutSeconds * 1000);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => startCheckBuffering => " + e.getMessage());
        }
    }

    default void stopCheckBuffering() {
        try {
            if (null == mHandlerBuffering[0])
                throw new Exception("mHandler warning: null");
            mHandlerBuffering[0].removeMessages(7677);
            mHandlerBuffering[0].removeCallbacksAndMessages(null);
            mHandlerBuffering[0] = null;
            MPLogUtil.log("VideoPlayerApiKernel => stopCheckBuffering => succ");
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => stopCheckBuffering => " + e.getMessage());
        }
    }
}
