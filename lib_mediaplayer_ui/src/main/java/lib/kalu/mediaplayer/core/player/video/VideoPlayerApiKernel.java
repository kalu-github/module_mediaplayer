package lib.kalu.mediaplayer.core.player.video;

import android.content.Context;
import android.view.View;

import androidx.annotation.FloatRange;

import org.json.JSONArray;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactoryManager;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoPlayerApiKernel extends VideoPlayerApiListener,
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

    @Override
    default void start(StartArgs args) {
        try {
            if (null == args)
                throw new Exception("error: args null");
            String url = args.getUrl();
            if (null == url || url.isEmpty())
                throw new Exception("error: url null");
            // 1
            boolean log = args.isLog();
            LogUtil.setLogger(log);
            // 2
            boolean initRelease = args.isInitRelease();
            if (initRelease) {
                release(false, true, false);
            } else {
                stop(false, true);
            }
            // 3
            setTags(args);
            setScreenKeep(true);
            callEvent(PlayerType.EventType.INIT);
            long trySeeDuration = args.getTrySeeDuration();
            if (trySeeDuration > 0L) {
                callEvent(PlayerType.EventType.TRY_SEE_START);
            }
            // 4
            checkKernelNull(args, false);
            // 5
            checkRenderNull(args, false);
            // 6
            attachRenderKernel();
            // 7
            setKernelEvent(args);
            // 8
            initDecoder(args);
        } catch (Exception e) {
            callEvent(PlayerType.EventType.ERROR);
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

    default void resume() {
        resume(true);
    }

    default void resume(boolean callEvent) {
        setScreenKeep(true);
        resumeKernel(callEvent);
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
        stopRenderUpdateProgress();
        stopKernel(callEvent, clearTag);
    }

    default void release() {
        release(true, true, true);
    }

    default void release(boolean callEvent, boolean clearTag, boolean isFromUser) {
        try {
            clearOnPlayerListener();
            releaseRender();
            releaseKernel(clearTag, isFromUser);
            if (!callEvent)
                throw new Exception("warning: callEvent false");
            callEvent(PlayerType.EventType.RELEASE);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => release => " + e.getMessage());
        }
    }

    default void restart() {
        restart(0L, true);
    }

    default void restart(boolean clearTag) {
        restart(0L, clearTag);
    }

    default void restart(long position) {
        restart(position, true);
    }

    default void restart(long position, boolean cleatTag) {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            String url = args.getUrl();
            if (null == url)
                throw new Exception("error: url null");
            if (cleatTag) {
                setTags(null);
            }
            StartArgs newArgs = args.newBuilder().setPlayWhenReadySeekToPosition(position).build();
            start(newArgs);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => restart => " + e.getMessage());
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

    default long getPlayWhenReadySeekToPosition() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            return kernel.getPlayWhenReadySeekToPosition();
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
            // 心跳
            startRenderUpdateProgress();
            // 播放
            Context context = getBaseContext();
            kernel.initDecoder(context, args);
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
            callEvent(PlayerType.EventType.STOP);
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
            // 心跳
            stopRenderUpdateProgress();
            // 执行
            kernel.pause();
            if (!callEvent)
                throw new Exception("warning: callEvent false");
            callEvent(PlayerType.EventType.PAUSE);
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
            // 心跳
            startRenderUpdateProgress();
            // 执行
            kernel.start();
            setScreenKeep(true);
            if (!callEvent)
                throw new Exception("warning: callEvent false");
            callEvent(PlayerType.EventType.RESUME);
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

    default void checkKernelNull(StartArgs args, boolean release) {
        try {
            if (release) {
                releaseKernel(false, true);
            }
            VideoKernelApi videoKernel = getVideoKernel();
            if (null != videoKernel)
                throw new Exception("warning: null != videoKernel");
            Context context = getBaseContext();
            int kernelType = args.getKernelType();
            VideoKernelApi kernelApi = VideoKernelFactoryManager.getKernel(kernelType);
            kernelApi.createDecoder(context, args);
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
//                        LogUtil.log("VideoPlayerApiKernel => setKernelEvent => onUpdateProgress => TRY_SEE_FINISH");
                        stop(false, false);
                        callEvent(PlayerType.EventType.TRY_SEE_FINISH);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onEvent(@PlayerType.KernelType.Value int kernel, @PlayerType.EventType.Value int event) {
//                    LogUtil.log("VideoPlayerApiKernel => setKernelEvent => onEvent = " + kernel + ", event = " + event);

                    // 透传
                    callEvent(event);

                    switch (event) {
                        // 缓冲开始
                        case PlayerType.EventType.BUFFERING_START:
                            // 埋点
                            onBuriedBufferingStart();
                            break;
                        // 缓冲结束
                        case PlayerType.EventType.BUFFERING_STOP:
                            // 埋点
                            onBuriedBufferingStop();
                            break;
                        // 视频首帧
                        case PlayerType.EventType.VIDEO_RENDERING_START:
                            // 埋点
                            onBuriedVideoRenderingStart();
                            break;
                        // 播放开始-默认
                        case PlayerType.EventType.START:
                            // 埋点
                            onBuriedStart();
                            // ijk需要刷新RenderView
                            initRenderView();
//                          // 检查View是否可见
                            checkVideoVisibility();
                            break;
                        // 快进
                        case PlayerType.EventType.SEEK_START_FORWARD:
                            // 埋点
                            onBuriedSeekStartForward();
                            break;
                        // 快退
                        case PlayerType.EventType.SEEK_START_REWIND:
                            // 埋点
                            onBuriedSeekStartRewind();
                            break;
                        // 快进
                        case PlayerType.EventType.SEEK_FINISH:
                            // 埋点
                            onBuriedSeekFinish();
                            break;
                        // 播放错误
                        case PlayerType.EventType.ERROR:
                            // 埋点
                            onBuriedError(event);
                            // 执行
                            setScreenKeep(false);
                            break;
                        // 播放结束
                        case PlayerType.EventType.COMPLETE:
                            // 埋点
                            onBuriedComplete();
                            // 关闭屏幕常亮
                            setScreenKeep(false);
                            // 多剧集
                            int episodeItemCount = args.getEpisodeItemCount();
                            OnPlayerEpisodeListener onPlayerEpisodeListener = getOnPlayerEpisodeListener();
                            if (episodeItemCount > 0 && null != onPlayerEpisodeListener) {
                                int episodePlayingIndex = args.getEpisodePlayingIndex();
                                int nextPlayIndex = episodePlayingIndex + 1;
                                if (nextPlayIndex >= episodeItemCount) {
                                    onPlayerEpisodeListener.onEnd();
                                } else {
                                    onPlayerEpisodeListener.onEpisode(nextPlayIndex);
                                }
                            }
                            // 单剧集
                            else {
                                boolean looping = args.isLooping();
                                if (looping) {
                                    restart();
                                }
                            }
                            break;
                    }
                }

                @Override
                public void onVideoFormatChanged(int kernel, int rotation, int scaleType, int width, int height, int bitrate) {
//                    LogUtil.log("VideoPlayerApiKernel => setKernelEvent => onVideoFormatChanged => kernel = " + kernel + ", videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", rotation = " + ", scaleType = " + scaleType);
                    setVideoFormat(kernel, rotation, scaleType, width, height, bitrate);
                }
            });
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => setKernelEvent => " + e.getMessage());
        }
    }

    default boolean setTrackInfo(int groupId, int trackId) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            return kernel.setTrackInfo(groupId, trackId);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => setTrackInfo => " + e.getMessage());
            return false;
        }
    }

    default JSONArray getAllTrackInfo() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            JSONArray trackInfo = kernel.getAllTrackInfo();
            if (null == trackInfo)
                throw new Exception("trackInfo error: null");
            return trackInfo;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => getAllTrackInfo => " + e.getMessage());
            return null;
        }
    }

    default JSONArray getVideoTrackInfo() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            JSONArray trackInfo = kernel.getVideoTrackInfo();
            if (null == trackInfo)
                throw new Exception("trackInfo error: null");
            return trackInfo;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => getVideoTrackInfo => " + e.getMessage());
            return null;
        }
    }

    default JSONArray getAudioTrackInfo() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            JSONArray trackInfo = kernel.getAudioTrackInfo();
            if (null == trackInfo)
                throw new Exception("trackInfo error: null");
            return trackInfo;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => getAudioTrackInfo => " + e.getMessage());
            return null;
        }
    }

    default JSONArray getTextTrackInfo() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            JSONArray trackInfo = kernel.getTextTrackInfo();
            if (null == trackInfo)
                throw new Exception("trackInfo error: null");
            return trackInfo;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiKernel => getTextTrackInfo => " + e.getMessage());
            return null;
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
