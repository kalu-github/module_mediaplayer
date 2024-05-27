package lib.kalu.mediaplayer.core.player.video;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.FloatRange;

import org.json.JSONArray;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactoryManager;
import lib.kalu.mediaplayer.util.LogUtil;

interface VideoPlayerApiKernel extends VideoPlayerApiListener,
        VideoPlayerApiBuriedEvent,
        VideoPlayerApiComponent,
        VideoPlayerApiRender,
        VideoPlayerApiDevice {

    default void setData(String data) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_data, data);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => setData => " + e.getMessage());
        }
    }

    default String getData() {
        try {
            Object tag = ((View) this).getTag(R.id.module_mediaplayer_id_player_data);
            if (null == tag)
                throw new Exception("tag error: null");
            return (String) tag;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => getData => " + e.getMessage());
            return null;
        }
    }

    default void start(String url) {
        StartBuilder.Builder builder = new StartBuilder.Builder();
        StartBuilder build = builder.build();
        start(build, url, false);
    }


    default void start(StartBuilder builder, String playUrl) {
        start(builder, playUrl, false);
    }

    @Override
    default void start(StartBuilder startBuilder, String playUrl, boolean retryBuffering) {
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
            LogUtil.setLogger(playerBuilder);
            // 2
            setScreenKeep(true);
            // 3
            boolean kernelReset = playerBuilder.isReset();
            if (!kernelReset) {
                stop(false);
            }
            // 4
            int kernelType = playerBuilder.getKernel();
            boolean log = playerBuilder.isLog();
            int seekParameters = playerBuilder.getExoSeekParameters();
            int connectTimeout = playerBuilder.getConnectTimeout();
            checkKernelNull(kernelReset, log, kernelType, seekParameters, connectTimeout);
            // 5
            int renderType = playerBuilder.getRender();
            checkRenderNull(kernelReset, renderType);
            // 6
            attachRenderKernel();
            // 7
            boolean retryTimeout = playerBuilder.getBufferingTimeoutRetry();
            setKernelEvent(startBuilder, kernelType, kernelReset, retryTimeout, connectTimeout);
            // 4
            startDecoder(startBuilder, playUrl, kernelReset, connectTimeout);
            // 8
            updatePlayerData(startBuilder, playUrl);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => start => " + e.getMessage());
        }
    }

    default void updatePlayerData(StartBuilder data, String playUrl) {
        try {
            if (null == data)
                throw new Exception("data error: null");
            ((View) this).setTag(R.id.module_mediaplayer_id_player_url, playUrl);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_looping, data.isLooping());
            ((View) this).setTag(R.id.module_mediaplayer_id_player_looping_release, data.isLoopingRelease());
            ((View) this).setTag(R.id.module_mediaplayer_id_player_window_visibility_changed_release, data.isWindowVisibilityChangedRelease());
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => updatePlayerData => " + e.getMessage());
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
            LogUtil.log("VideoPlayerApiKernel => getDuration => " + e.getMessage());
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
            LogUtil.log("VideoPlayerApiKernel => setVolume => left = " + left + ", right = " + right);
            VideoKernelApi kernel = getVideoKernel();
            kernel.setVolume(left, right);
        } catch (Exception e) {
        }
    }

    default void setMute(boolean enable) {
        try {
            LogUtil.log("VideoPlayerApiKernel => setMute => enable = " + enable);
            VideoKernelApi kernel = getVideoKernel();
            kernel.setMute(enable);
        } catch (Exception e) {
        }
    }

    default void setLooping(boolean looping) {
        try {
            LogUtil.log("VideoPlayerApiKernel => setLooping => looping = " + looping);
            VideoKernelApi kernel = getVideoKernel();
            kernel.setLooping(looping);
        } catch (Exception e) {
        }
    }


    default void release() {
        release(true, true, true);
    }

    default void release(boolean releaseTag, boolean isFromUser) {
        release(true, releaseTag, isFromUser);
    }

    default void release(boolean clearListener, boolean releaseTag, boolean isFromUser) {
        try {
            if (releaseTag) {
                setData(null);
                releaseTag();
            }
            releaseRender();
            releaseKernel(isFromUser);
            if (clearListener) {
                removeOnPlayerEventListener();
                removeOnPlayerProgressListener();
                removeOnPlayerWindowListener();
            }
            callPlayerEvent(PlayerType.StateType.STATE_RELEASE);
        } catch (Exception e) {
            callPlayerEvent(PlayerType.StateType.STATE_RELEASE_EXCEPTION);
            LogUtil.log("VideoPlayerApiKernel => release => " + e.getMessage());
        }
    }

    default void toggle() {
        toggle(false);
    }

    default void toggle(boolean ignore) {
        try {
            LogUtil.log("VideoPlayerApiKernel => toggle => ignore = " + ignore);
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
        pause(true);
    }

    default void pause(boolean callEvent) {
        setScreenKeep(false);
        pauseKernel(callEvent);
    }

    default void stop(boolean callEvent) {
        releaseTag();
        releaseSeek();
        setPlayWhenReady(false);
        setScreenKeep(false);
        stopKernel(callEvent);
    }

    default void restart() {
        restart(0, false);
    }

    default void restart(long seek, boolean retryBuffering) {
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
            LogUtil.log("VideoPlayerApiKernel => restart => " + e.getMessage());
        }
    }

    default void resume() {
        resume(false);
    }

    default void resume(boolean callEvent) {
        setPlayWhenReady(true);
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            resumeVideoKernel(callEvent);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => resume => " + e.getMessage());
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

    default void setSeek(long position) {
        try {
            if (position < 0)
                throw new Exception("position error: " + position);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_position, position);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => setSeek => " + e.getMessage());
        }
    }

    default void releaseSeek() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("kernel error: null");
            long position = kernel.getPosition();
            if (position < 0)
                throw new Exception("position error: " + position);
            setSeek(position);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => releaseSeek => " + e.getMessage());
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

    default void seekTo(boolean force) {
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

    default void seekTo(long seek) {
        seekTo(false, seek, getMax(), isLooping());
    }

    default void seekTo(long seek, long max) {
        seekTo(false, seek, max, isLooping());
    }

    default void seekTo(boolean force, long seek) {
        seekTo(force, seek, getMax(), isLooping());
    }

    default void seekTo(boolean force, long seek, long max, boolean looping) {
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

    default void seekTo(boolean force, StartBuilder builder) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
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

    default void resumeVideoKernel(boolean callEvent) {
        setPlayWhenReady(true);
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            kernel.start();
            setScreenKeep(true);
            if (callEvent) {
                callPlayerEvent(PlayerType.StateType.STATE_RESUME);
                callPlayerEvent(PlayerType.StateType.STATE_KERNEL_RESUME);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => resumeVideoKernel => " + e.getMessage());
        }
    }

    default void stopKernel(boolean callEvent) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            kernel.stop();
            if (!callEvent)
                throw new Exception("warning: callEvent false");
            callPlayerEvent(PlayerType.StateType.STATE_KERNEL_STOP);
            callPlayerEvent(PlayerType.StateType.STATE_CLOSE);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => stopKernel => " + e.getMessage());
        }
    }

    default void pauseKernel(boolean callEvent) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            kernel.pause();
            if (!callEvent)
                throw new Exception("warning: callEvent false");
            callPlayerEvent(PlayerType.StateType.STATE_PAUSE);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => pauseKernel => " + e.getMessage());
        }
    }

    default void seekToVideoKernel(long milliSeconds) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            kernel.seekTo(milliSeconds);
            setScreenKeep(true);
            if (milliSeconds <= 0)
                return;
            callPlayerEvent(PlayerType.StateType.STATE_INIT_SEEK);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => seekToVideoKernel => " + e.getMessage());
        }
    }

    default void updateVideoKernel(StartBuilder builder) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            long seek = builder.getSeek();
            long max = builder.getMax();
            boolean loop = builder.isLooping();
            kernel.update(seek, max, loop);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => updateVideoKernel => " + e.getMessage());
        }
    }

    default void startDecoder(StartBuilder bundle, String playUrl, boolean reset, int connectTimeout) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            kernel.initDecoder(getBaseContext(), reset, connectTimeout, playUrl, bundle);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => startDecoder => " + e.getMessage());
        }
    }

    default void playEnd() {
        setScreenKeep(false);
    }

    /***************************/

    default void releaseKernel(boolean isFromUser) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            kernel.releaseDecoder(isFromUser);
            setVideoKernel(null);
            setScreenKeep(false);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => releaseKernel => " + e.getMessage());
        }
    }

    default void checkKernelNull(boolean reset, boolean enableLogger, int kernelType, int seekParameters, int connectTimeout) {
        try {
            if (reset) {
                releaseKernel(false);
            }
            VideoKernelApi videoKernel = getVideoKernel();
            if (null != videoKernel)
                throw new Exception("warning: null != videoKernel");
            VideoKernelApi kernelApi = VideoKernelFactoryManager.getKernel(kernelType);
            kernelApi.createDecoder(getBaseContext(), enableLogger, seekParameters, connectTimeout);
            setVideoKernel(kernelApi);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => checkKernelNull => " + e.getMessage());
        }
    }

    default void setKernelEvent(StartBuilder builder, int kernelType, boolean kernelReset, boolean retryTimeout, int connectTimeout) {

        try {
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel)
                throw new Exception("");
            videoKernel.setPlayerApi((VideoPlayerApi) this);
            videoKernel.setKernelApi(new VideoKernelApiEvent() {

                @Override
                public void onUpdateProgress(boolean isLooping, long max, long seek, long position, long duration) {
//                    MPLogUtil.log("VideoPlayerApiKernel => onUpdateProgress => isLooping = " + isLooping + ", max = " + max + ", seek = " + seek + ", position = " + position + ", duration = " + duration);

                    callUpdateProgressComponent(max, seek, position, duration);
                    callUpdateProgressPlayer(position, duration);
                    try {
                        if (max <= 0 || position <= max)
                            throw new Exception("time warning: max = " + max + ", position = " + position + ", seek = " + seek + ", duration = " + duration + ", isLooping = " + isLooping);
                        callPlayerEvent(PlayerType.StateType.STATE_TRY_COMPLETE);
                        // 试看1
                        if (isLooping) {
                            seekTo(true);
                        }
                        // 试看2
                        else {
                            pause(true);
                            playEnd();
                        }
                    } catch (Exception e) {
//                        MPLogUtil.log("VideoPlayerApiKernel => onUpdateTimeMillis => " + e.getMessage());
                    }
                }

                @SuppressLint("StaticFieldLeak")
                @Override
                public void onEvent(int kernel, int event) {
                    LogUtil.log("VideoPlayerApiKernel => setKernelEvent => onEvent = " + kernel + ", event = " + event);
                    switch (event) {
                        // 网络拉流开始
//                        case PlayerType.EventType.EVENT_OPEN_INPUT:
//                            hideVideoView();
//                            break;
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
                            getVideoKernel().startCheckLoadBufferingTimeout(kernelReset, retryTimeout, connectTimeout);
                            break;
                        // 缓冲结束
                        case PlayerType.EventType.EVENT_BUFFERING_STOP:
                            callPlayerEvent(PlayerType.StateType.STATE_BUFFERING_STOP);
                            getVideoKernel().stopCheckLoadBufferingTimeout();
                            break;
//                        // 播放开始
//                        case PlayerType.EventType.EVENT_VIDEO_START_903:
//                            // step1
//                            showVideoReal();
//                            // step2
//                            checkVideoReal();
//                            break;
//                        // 播放开始
//                        case PlayerType.EventType.EVENT_VIDEO_START_RETRY:
//                            callPlayerEvent(PlayerType.StateType.STATE_START_RETRY);
//                            // step2
//                            showVideoReal();
//                            // step3
//                            checkVideoReal();
//                            // step5
//                            MPLogUtil.log("VideoPlayerApiKernel => onEvent => event_video_start_retry => playWhenReady = " + isPlayWhenReady());
//                            break;
//                        // 播放开始-快进
//                        case PlayerType.EventType.EVENT_VIDEO_START_SEEK:
//                            // 试看
//                            if (null != builder) {
//                                long max = builder.getMax();
//                                if (max > 0) {
//                                    callPlayerEvent(PlayerType.StateType.STATE_TRY_BEGIN);
//                                }
//                            }
//                            // step1
//                            callPlayerEvent(PlayerType.StateType.STATE_START_SEEK);
//                            // step2
//                            showVideoReal();
//                            // step4
//                            resume(true);
//                            // step6
//                            boolean playWhenReady1 = isPlayWhenReady();
//                            MPLogUtil.log("VideoPlayerApiKernel => onEvent => event_video_start_seek => playWhenReady = " + playWhenReady1);
//                            if (!playWhenReady1) {
//                                pause();
//                                setPlayWhenReady(true);
//                                callPlayerEvent(PlayerType.StateType.STATE_START_PLAY_WHEN_READY_PAUSE);
//                            }
//                            break;
                        // 视频首帧
                        case PlayerType.EventType.EVENT_VIDEO_RENDERING_START:
                            callPlayerEvent(PlayerType.StateType.STATE_VIDEO_RENDERING_START);
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
                            // ijk需要刷新RenderView
                            resetRenderView(kernelType);
//                            // step3
                            checkVideoView();
                            // step4
                            boolean playWhenReady2 = isPlayWhenReady();
                            if (!playWhenReady2) {
                                pause();
                                setPlayWhenReady(true);
                                callPlayerEvent(PlayerType.StateType.STATE_START_PLAY_WHEN_READY_PAUSE);
                            }
                            break;
                        // 播放错误
                        case PlayerType.EventType.EVENT_ERROR_SEEK_TIME:
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
                public void onUpdateSizeChanged(int kernel, int videoWidth, int videoHeight, @PlayerType.RotationType.Value int rotation) {
                    LogUtil.log("VideoPlayerApiKernel => setKernelEvent => onUpdateSizeChanged = kernel = " + kernel + ", videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", rotation = " + rotation);
                    setVideoFormat(videoWidth, videoHeight, rotation);
                }
            });
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => setKernelEvent => " + e.getMessage());
        }
    }

    default JSONArray getTrackInfo() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            JSONArray trackInfo = kernel.getTrackInfo();
            if (null == trackInfo)
                throw new Exception("trackInfo error: null");
            return trackInfo;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => getTrackInfo => " + e.getMessage());
            return null;
        }
    }

    default boolean switchTrack(int trackId) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            kernel.switchTrack(trackId);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => switchTrack => " + e.getMessage());
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
}
