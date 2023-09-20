package lib.kalu.mediaplayer.core.player.audio;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.context.PlayerProvider;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApi;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelFactoryManager;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactoryManager;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface AudioPlayerApiKernel extends AudioPlayerApiListener {

    default void setData(@NonNull String data) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_data, data);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => setData => " + e.getMessage());
        }
    }

    default String getData() {
        try {
            Object tag = ((View) this).getTag(R.id.module_mediaplayer_id_player_data);
            if (null == tag)
                throw new Exception("tag error: null");
            return (String) tag;
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => getData => " + e.getMessage());
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
            // 5
            createAudioKernel(startBuilder, playerBuilder, retryBuffering);
            // 6
            initAudioKernel(startBuilder, playUrl);
            // 8
            updatePlayerData(startBuilder, playUrl);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => start => " + e.getMessage());
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
            MPLogUtil.log("AudioPlayerApiKernel => updatePlayerData => " + e.getMessage());
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
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("audioKernel error: null");
            long duration = audioKernel.getDuration();
            if (duration < 0L) {
                duration = 0L;
            }
            return duration;
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => getDuration => " + e.getMessage());
            return 0L;
        }
    }

    default long getPosition() {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("audioKernel error: null");
            long position = audioKernel.getPosition();
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
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("audioKernel error: null");
            audioKernel.setVolume(left, right);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => setVolume => " + e.getMessage());
        }
    }

    default void setMute(boolean enable) {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("audioKernel error: null");
            audioKernel.setMute(enable);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => setMute => " + e.getMessage());
        }
    }

    default void setLooping(boolean looping) {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("audioKernel error: null");
            audioKernel.setLooping(looping);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => setLooping => " + e.getMessage());
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
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            if (releaseTag) {
                setData(null);
                releaseTag();
            }
            releaseVideoKernel(isFromUser, isMainThread);
            if (clearListener) {
                cleanPlayerChangeListener();
            }
            callPlayerEvent(PlayerType.StateType.STATE_RELEASE);
        } catch (Exception e) {
            callPlayerEvent(PlayerType.StateType.STATE_RELEASE_EXCEPTION);
            MPLogUtil.log("AudioPlayerApiKernel => release => " + e.getMessage());
        }
    }

    default void toggle() {
        toggle(false);
    }

    default void toggle(boolean ignore) {
        try {
            MPLogUtil.log("AudioPlayerApiKernel => toggle => ignore = " + ignore);
            boolean playing = isPlaying();
            if (playing) {
                pause(ignore);
            } else {
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
        stopVideoKernel(callEvent, isMainThread);
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
            MPLogUtil.log("AudioPlayerApiKernel => restart => " + e.getMessage());
        }
    }

    default void resume() {
        resume(false);
    }

    default void resume(boolean ignore) {
        setPlayWhenReady(true);
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            resumeVideoKernel(ignore);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => resume => " + e.getMessage());
        }
    }

    default boolean isLooping() {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            return audioKernel.isLooping();
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
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            Object tag = ((View) this).getTag(R.id.module_mediaplayer_id_player_position);
            if (null != tag) {
                audioKernel.setSeek((Long) tag);
            }
            return audioKernel.getSeek();
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
            MPLogUtil.log("AudioPlayerApiKernel => setSeek => " + e.getMessage());
        }
    }

    default void refeshSeek() {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            long position = audioKernel.getPosition();
            if (position < 0)
                throw new Exception("position error: " + position);
            setSeek(position);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => setSeek => " + e.getMessage());
        }
    }

    default long getMax() {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            return audioKernel.getMax();
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
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
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
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            return audioKernel.isLive();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isPlaying() {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            return audioKernel.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isPrepared() {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            return audioKernel.isPrepared();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isMute() {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            return audioKernel.isMute();
        } catch (Exception e) {
            return false;
        }
    }

    default void setSpeed(float speed) {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            audioKernel.setSpeed(speed);
        } catch (Exception e) {
        }
    }

    default float getSpeed() {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            return audioKernel.getSpeed();
        } catch (Exception e) {
            return 1F;
        }
    }

    /*********************/

    default void resumeVideoKernel(@NonNull boolean ignore) {
        setPlayWhenReady(true);
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            audioKernel.start();
            if (ignore) {
                callPlayerEvent(PlayerType.StateType.STATE_RESUME_IGNORE);
            } else {
                callPlayerEvent(PlayerType.StateType.STATE_RESUME);
                callPlayerEvent(PlayerType.StateType.STATE_KERNEL_RESUME);
            }
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => resumeVideoKernel => " + e.getMessage());
        }
    }

    default void stopVideoKernel(@NonNull boolean call, @NonNull boolean isMainThread) {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            audioKernel.stop(isMainThread);
            if (!call)
                throw new Exception("call warning: false");
            callPlayerEvent(PlayerType.StateType.STATE_KERNEL_STOP);
            callPlayerEvent(PlayerType.StateType.STATE_CLOSE);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => stopVideoKernel => " + e.getMessage());
        }
    }

    default void setKernelRetryBuffering(boolean retryBuffering) {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            audioKernel.setRetryBuffering(retryBuffering);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => setKernelRetryBuffering => " + e.getMessage());
        }
    }

    default void pauseVideoKernel(@NonNull boolean ignore) {
        setPlayWhenReady(false);
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            // 3
            audioKernel.pause();
            // 4
            refeshSeek();
            callPlayerEvent(ignore ? PlayerType.StateType.STATE_PAUSE_IGNORE : PlayerType.StateType.STATE_PAUSE);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => pauseVideoKernel => " + e.getMessage());
        }
    }

    default void releaseVideoKernel(boolean isFromUser, boolean isMainThread) {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            audioKernel.releaseDecoder(isFromUser, isMainThread);
            setAudioKernel(null);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => releaseVideoKernel => " + e.getMessage());
        }
    }

    default void seekToVideoKernel(long milliSeconds) {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            audioKernel.seekTo(milliSeconds);
            if (milliSeconds <= 0)
                throw new Exception("milliSeconds warning: " + milliSeconds);
            callPlayerEvent(PlayerType.StateType.STATE_INIT_SEEK);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => seekToVideoKernel => " + e.getMessage());
        }
    }

    default void updateVideoKernel(@NonNull StartBuilder builder) {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            long seek = builder.getSeek();
            long max = builder.getMax();
            boolean loop = builder.isLoop();
            audioKernel.update(seek, max, loop);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => updateVideoKernel => " + e.getMessage());
        }
    }

    default void initAudioKernel(@NonNull StartBuilder bundle, @NonNull String playUrl) {
        try {
            Context playerContext = PlayerProvider.getPlayerContext();
            if (null == playerContext)
                throw new Exception("playerContext error: null");
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            audioKernel.initDecoder(playerContext, playUrl, bundle);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => initAudioKernel => " + e.getMessage());
        }
    }

    default void playEnd() {
    }

    /***************************/

    default void setAudioKernel(@PlayerType.KernelType.Value int v) {
        PlayerManager.getInstance().setVideoKernel(v);
    }

    default void createAudioKernel(@NonNull StartBuilder builder, @NonNull PlayerBuilder playerBuilder, @NonNull boolean retryBuffering) {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            release(false, false);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => createKernel => keenel warning: null");
        }

        // 2
        try {
            int type = PlayerManager.getInstance().getConfig().getVideoKernel();
            AudioKernelApi kernel = AudioKernelFactoryManager.getKernel((AudioPlayerApi) this, retryBuffering, type, new AudioKernelApiEvent() {

                @Override
                public void onEvent(int kernel, int event) {
                    MPLogUtil.log("AudioPlayerApiKernel => onEvent = " + kernel + ", event = " + event);
                    switch (event) {
                        // 网络拉流开始
                        case PlayerType.EventType.EVENT_OPEN_INPUT:
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
                            break;
                        // 播放开始
                        case PlayerType.EventType.EVENT_VIDEO_START_RETRY:
                            callPlayerEvent(PlayerType.StateType.STATE_START_RETRY);
                            // step5
                            MPLogUtil.log("AudioPlayerApiKernel => onEvent => event_video_start_retry => playWhenReady = " + isPlayWhenReady());
                            break;
                        // 播放开始-快进
                        case PlayerType.EventType.EVENT_VIDEO_START_SEEK:
                            // step1
                            callPlayerEvent(PlayerType.StateType.STATE_START_SEEK);
                            // step4
                            resume(true);
                            // step6
                            boolean playWhenReady1 = isPlayWhenReady();
                            MPLogUtil.log("AudioPlayerApiKernel => onEvent => event_video_start_seek => playWhenReady = " + playWhenReady1);
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
                            callPlayerEvent(PlayerType.StateType.STATE_START);
                            // step4
                            boolean playWhenReady2 = isPlayWhenReady();
                            MPLogUtil.log("AudioPlayerApiKernel => onEvent => event_video_start_seek => playWhenReady = " + playWhenReady2);
                            if (!playWhenReady2) {
                                pause();
                                setPlayWhenReady(true);
                                callPlayerEvent(PlayerType.StateType.STATE_START_PLAY_WHEN_READY_PAUSE);
                            }
                            break;
                        // 播放错误
                        case PlayerType.EventType.EVENT_ERROR_IGNORE:
                            callPlayerEvent(PlayerType.StateType.STATE_ERROR_IGNORE);
                            break;
                        // 播放错误
                        case PlayerType.EventType.EVENT_ERROR_URL:
                        case PlayerType.EventType.EVENT_ERROR_RETRY:
                        case PlayerType.EventType.EVENT_ERROR_SOURCE:
                        case PlayerType.EventType.EVENT_ERROR_PARSE:
                        case PlayerType.EventType.EVENT_ERROR_NET:
                            callPlayerEvent(PlayerType.StateType.STATE_ERROR);
                            break;
                        // 播放结束
                        case PlayerType.EventType.EVENT_VIDEO_END:
                            callPlayerEvent(PlayerType.StateType.STATE_END);
                            boolean looping = isLooping();
                            boolean loopingRelease = isLoopingRelease();
                            MPLogUtil.log("AudioPlayerApiKernel => onEvent = 播放结束 => looping = " + looping + ", loopingRelease = " + loopingRelease);
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
            });
            // 4
            setAudioKernel(kernel);
            // 5
            createAudioDecoder(playerBuilder);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => createKernel => " + e.getMessage());
        }
    }

    default void createAudioDecoder(@NonNull PlayerBuilder config) {
        try {
            Context playerContext = PlayerProvider.getPlayerContext();
            if (null == playerContext)
                throw new Exception("playerContext error: null");
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("check kernel is null");
            boolean log = config.isLog();
            int seekParameters = config.getExoSeekParameters();
            audioKernel.createDecoder(playerContext, log, seekParameters);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => createAudioDecoder => " + e.getMessage());
        }
    }

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
                                MPLogUtil.log("AudioPlayerApiKernel => startCheckBuffering => handleMessage => " + e.getMessage());
                            }
                            if (seek < 0) {
                                seek = 0;
                            }
                            MPLogUtil.log("AudioPlayerApiKernel => startCheckBuffering => handleMessage => what = " + msg.what + ", seek = " + seek);

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
            MPLogUtil.log("AudioPlayerApiKernel => startCheckBuffering => " + e.getMessage());
        }
    }

    default void stopCheckBuffering() {
        try {
            if (null == mHandlerBuffering[0])
                throw new Exception("mHandler warning: null");
            mHandlerBuffering[0].removeMessages(7677);
            mHandlerBuffering[0].removeCallbacksAndMessages(null);
            mHandlerBuffering[0] = null;
            MPLogUtil.log("AudioPlayerApiKernel => stopCheckBuffering => succ");
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => stopCheckBuffering => " + e.getMessage());
        }
    }
}
