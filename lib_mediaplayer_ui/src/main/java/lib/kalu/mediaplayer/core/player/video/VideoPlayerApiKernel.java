package lib.kalu.mediaplayer.core.player.video;

import android.view.View;

import androidx.annotation.FloatRange;

import org.json.JSONArray;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactoryManager;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

interface VideoPlayerApiKernel extends VideoPlayerApiListener,
        VideoPlayerApiBuried,
        VideoPlayerApiComponent,
        VideoPlayerApiRender,
        VideoPlayerApiDevice {

    default boolean isDoWindowing() {
        try {
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel)
                throw new Exception("error: videoKernel null");
            return videoKernel.isDoWindowing();
        } catch (Exception e) {
            return false;
        }
    }

    default void setDoWindowing(boolean v) {
        try {
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel)
                throw new Exception("error: videoKernel null");
            videoKernel.setDoWindowing(v);
        } catch (Exception e) {
        }
    }

    default void setTags(StartArgs args) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_startargs, args);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => setTags => " + e.getMessage());
        }
    }

    default StartArgs getStartArgs() {
        try {
            Object args = ((View) this).getTag(R.id.module_mediaplayer_id_startargs);
            if (null == args)
                throw new Exception("warning: args null");
            return (StartArgs) args;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => getStartArgs => " + e.getMessage());
            return null;
        }
    }

    default void start(String url) {
        StartArgs build = new StartArgs.Builder().setUrl(url).build();
        start(build);
    }

    @Override
    default void start(StartArgs args) {
        try {
            if (null == args)
                throw new Exception("error: args null");
            String url = args.getUrl();
            if (null == url)
                throw new Exception("error: url null");
            // 1
            boolean log = args.isLog();
            LogUtil.setLogger(log);
            // 2
            boolean initRelease = args.isInitRelease();
            if (initRelease) {
                release(true, false, false);
            } else {
                stop(true, false);
            }
            // 3
            setTags(args);
            setScreenKeep(true);
            callEvent(PlayerType.StateType.INIT);
            // 4
            int kernelType = args.getKernelType();
            checkKernelNull(kernelType, false);
            // 5
            int renderType = args.getRenderType();
            checkRenderNull(renderType, false);
            // 6
            attachRenderKernel();
            // 7
            setKernelEvent(args);
            // 8
            initDecoder(args);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => start => " + e.getMessage());
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


    default void toggle() {
        toggle(true);
    }

    default void toggle(boolean callEvent) {
        try {
            boolean playing = isPlaying();
            if (playing) {
                pause(callEvent);
            } else {
                resume(callEvent);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => toggle => " + e.getMessage());
        }
    }

    default void pause() {
        pause(true);
    }

    default void pause(boolean callEvent) {
        setScreenKeep(false);
        pauseKernel(callEvent);
    }

    default void stop() {
        stop(true, true);
    }

    default void stop(boolean callEvent, boolean clearTag) {
        setScreenKeep(false);
        stopRender();
        stopKernel(callEvent, clearTag);
    }

    default void release() {
        release(true, true, true);
    }

    default void release(boolean callEvent, boolean clearTag, boolean isFromUser) {
        try {
            releaseRender();
            releaseKernel(clearTag, isFromUser);
            if (!callEvent)
                throw new Exception("warning: callEvent false");
            callEvent(PlayerType.StateType.RELEASE);
        } catch (Exception e) {
            callEvent(PlayerType.StateType.RELEASE_EXCEPTION);
            LogUtil.log("VideoPlayerApiKernel => release => " + e.getMessage());
        }
    }

    default void restart() {
        restart(0L, true);
    }

    default void restart(long position, boolean callEvent) {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            String url = args.getUrl();
            if (null == url)
                throw new Exception("error: url null");
            if (callEvent) {
                callEvent(PlayerType.StateType.RESTAER);
            }
            StartArgs build = args.newBuilder().setSeek(position).build();
            start(build);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => restart => " + e.getMessage());
        }
    }

    default void resume() {
        resume(true);
    }

    default void resume(boolean callEvent) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            resumeKernel(callEvent);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => resume => " + e.getMessage());
        }
    }

    default long getTrySeeDuration() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            return kernel.getTrySeeDuration();
        } catch (Exception e) {
            return 0L;
        }
    }

    default long getSeek() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            return kernel.getSeek();
        } catch (Exception e) {
            return 0L;
        }
    }

    default void seekTo(long position) {
        try {
            if (position < 0) {
                position = 0;
            }
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            kernel.seekTo(position);
            setScreenKeep(true);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => seekToVideoKernel => " + e.getMessage());
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

    default void setSpeed(@PlayerType.SpeedType.Value int speed) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            kernel.setSpeed(speed);
        } catch (Exception e) {
        }
    }

    @PlayerType.SpeedType.Value
    default int getSpeed() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            return kernel.getSpeed();
        } catch (Exception e) {
            return PlayerType.SpeedType.DEFAULT;
        }
    }

    /*********************/

    default void initDecoder(StartArgs args) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            kernel.initDecoder(getBaseContext(), args);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => initDecoder => " + e.getMessage());
        }
    }

    default void stopKernel(boolean callEvent, boolean clearTag) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            // 埋点
            boolean prepared = isPrepared();
            if (prepared) {
                onBuriedStop();
            }
            // 执行
            kernel.stop();
            // 数据
            if (clearTag) {
                setTags(null);
            }
            if (!callEvent)
                throw new Exception("warning: callEvent false");
            callEvent(PlayerType.StateType.KERNEL_STOP);
            callEvent(PlayerType.StateType.CLOSE);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => stopKernel => " + e.getMessage());
        }
    }

    default void pauseKernel(boolean callEvent) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            boolean prepared = isPrepared();
            if (!prepared)
                throw new Exception("warning: prepared false");
            boolean playing = isPlaying();
            if (!playing)
                throw new Exception("warning: playing false");
            // 埋点
            onBuriedPause();
            // 执行
            kernel.pause();
            if (!callEvent)
                throw new Exception("warning: callEvent false");
            callEvent(PlayerType.StateType.PAUSE);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => pauseKernel => " + e.getMessage());
        }
    }

    default void resumeKernel(boolean callEvent) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            boolean prepared = isPrepared();
            if (!prepared)
                throw new Exception("warning: prepared false");
            boolean playing = isPlaying();
            if (playing)
                throw new Exception("warning: playing true");
            // 埋点
            onBuriedResume();
            // 执行
            kernel.start();
            setScreenKeep(true);
            if (callEvent) {
                callEvent(PlayerType.StateType.RESUME);
                callEvent(PlayerType.StateType.KERNEL_RESUME);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => resumeKernel => " + e.getMessage());
        }
    }

    /***************************/

    default void releaseKernel(boolean clearTag, boolean isFromUser) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            kernel.releaseDecoder(isFromUser);
            setVideoKernel(null);
            setScreenKeep(false);
            if (clearTag) {
                setTags(null);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => releaseKernel => " + e.getMessage());
        }
    }

    default void checkKernelNull(int kernelType, boolean release) {
        try {
            if (release) {
                releaseKernel(false, true);
            }
            VideoKernelApi videoKernel = getVideoKernel();
            if (null != videoKernel)
                throw new Exception("warning: null != videoKernel");
            VideoKernelApi kernelApi = VideoKernelFactoryManager.getKernel(kernelType);
            kernelApi.createDecoder(getBaseContext());
            setVideoKernel(kernelApi);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => checkKernelNull => " + e.getMessage());
        }
    }

    default void setKernelEvent(StartArgs args) {

        try {
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel)
                throw new Exception("");
            videoKernel.setPlayerApi((VideoPlayerApi) this);
            videoKernel.setKernelApi(new VideoKernelApiEvent() {

                @Override
                public void onUpdateProgress(long position, long duration) {

                    try {
                        long trySeeDuration = getTrySeeDuration();
                        callProgress(trySeeDuration, position, duration);

                        if (trySeeDuration <= 0L)
                            throw new Exception("waning: trySeeDuration<=0L");
                        if (position < 0L)
                            throw new Exception("waning: position<0L");
                        if (position < trySeeDuration)
                            throw new Exception("waning: position<trySeeDuration");
                        // 试看结束
                        LogUtil.log("VideoPlayerApiKernel => setKernelEvent => onUpdateProgress => TRY_SEE_FINISH");
                        stop(false, true);
                        callEvent(PlayerType.StateType.TRY_SEE_FINISH);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onEvent(int kernel, int event) {
                    LogUtil.log("VideoPlayerApiKernel => setKernelEvent => onEvent = " + kernel + ", event = " + event);
                    switch (event) {
                        // 网络拉流开始
//                        case PlayerType.EventType.OPEN_INPUT:
//                            hideVideoView();
//                            break;
                        // 初始化开始 => loading start
                        case PlayerType.EventType.LOADING_START:
                            callEvent(PlayerType.StateType.LOADING_START);
                            break;
                        // 初始化完成 => loading stop
                        case PlayerType.EventType.LOADING_STOP:
                            callEvent(PlayerType.StateType.LOADING_STOP);
                            break;
                        // 缓冲开始
                        case PlayerType.EventType.BUFFERING_START:
                            // 埋点
                            onBuriedBufferingStart();
                            // 执行
                            callEvent(PlayerType.StateType.BUFFERING_START);
                            // 0: url
                            // 1: connentTimeout
                            // 2: log
                            // 3: seekParams
                            // 4: bufferingTimeoutRetry
                            // 5: kernelAlwaysRelease
                            // 6: live
//                            boolean bufferingTimeoutRetry = (boolean) o[4];
//                            int connentTimeout = (int) o[1];
//                            getVideoKernel().startCheckBufferingTimeout(bufferingTimeoutRetry, connentTimeout);
                            break;
                        // 缓冲结束
                        case PlayerType.EventType.BUFFERING_STOP:
                            // 埋点
                            onBuriedBufferingStop();
                            // 执行
                            callEvent(PlayerType.StateType.BUFFERING_STOP);
                            break;
                        // 视频首帧
                        case PlayerType.EventType.VIDEO_RENDERING_START:
                            // 埋点
                            onBuriedRendering();
                            // 执行
                            callEvent(PlayerType.StateType.VIDEO_RENDERING_START);
                            break;
                        // 播放开始-默认
                        case PlayerType.EventType.VIDEO_START:
//                        case PlayerType.EventType.VIDEO_SEEK_RENDERING_START: // 视频开始渲染
//            case PlayerType.MediaType.MEDIA_INFO_AUDIO_SEEK_RENDERING_START: // 视频开始渲染
                            // 埋点
                            onBuriedStart();
                            // 执行
                            callEvent(PlayerType.StateType.START);
                            // ijk需要刷新RenderView
                            int kernelType = args.getKernelType();
                            int renderType = args.getRenderType();
                            initRenderView(kernelType, renderType);
//                            // step3
                            checkVideoView();
                            // step4
                            boolean playWhenReady = args.isPlayWhenReady();
                            if (!playWhenReady) {
                                pause();
                                callEvent(PlayerType.StateType.START_PLAY_WHEN_READY_PAUSE);
                            }
                            break;
                        // 快进
                        case PlayerType.EventType.SEEK_START:
                            // 埋点
                            onBuriedSeekStart();
                            // 执行
                            callEvent(PlayerType.StateType.SEEK_START);
                            break;
                        // 快进
                        case PlayerType.EventType.SEEK_FINISH:
                            // 埋点
                            onBuriedSeekFinish();
                            // 执行
                            callEvent(PlayerType.StateType.SEEK_FINISH);
                            break;
                        // 续播
                        case PlayerType.EventType.VIDEO_RENDERING_START_SEEK:
                            callEvent(PlayerType.StateType.VIDEO_RENDERING_START_SEEK);
                            break;
                        // 播放错误
                        case PlayerType.EventType.ERROR_URL:
                        case PlayerType.EventType.ERROR_RETRY:
                        case PlayerType.EventType.ERROR_SOURCE:
                        case PlayerType.EventType.ERROR_PARSE:
                        case PlayerType.EventType.ERROR_NET:
                        case PlayerType.EventType.ERROR_BUFFERING_TIMEOUT:
                            // 埋点
                            onBuriedError(event);
                            // 执行
                            setScreenKeep(false);
                            callEvent(PlayerType.StateType.ERROR);
                            break;
                        // 播放结束
                        case PlayerType.EventType.VIDEO_END:
                            // 埋点
                            onBuriedComplete();
                            // 执行
                            callEvent(PlayerType.StateType.END);

                            boolean looping = args.isLooping();
                            if (looping) {
                                restart();
                            } else {
                                setScreenKeep(false);
                            }
                            break;
                    }
                }

                @Override
                public void onUpdateSizeChanged(int kernel, int videoWidth, int videoHeight, int rotation, int scaleType) {
                    LogUtil.log("VideoPlayerApiKernel => setKernelEvent => onUpdateSizeChanged => kernel = " + kernel + ", videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", rotation = " + ", scaleType = " + scaleType);
                    setVideoFormat(videoWidth, videoHeight, rotation, scaleType);
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
