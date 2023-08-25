package lib.kalu.mediaplayer.core.kernel.video.exo2;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.analytics.DefaultAnalyticsCollector;
import com.google.android.exoplayer2.decoder.DecoderReuseEvaluation;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.video.VideoSize;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.base.BasePlayer;
import lib.kalu.mediaplayer.core.player.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

@Keep
public final class VideoExoPlayer2 extends BasePlayer {

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;
    private boolean mPlayWhenReady = true;
    private boolean mPrepared = false;
    private boolean mRetryBuffering = false; // 缓冲失败, 重试播放
    private PlaybackParameters mSpeedPlaybackParameters;

    private ExoPlayer mExoPlayer;
    private AnalyticsListener mAnalyticsListener;

    public VideoExoPlayer2(@NonNull PlayerApi playerApi, @NonNull KernelApiEvent eventApi, @NonNull boolean retryBuffering) {
        super(playerApi, eventApi, retryBuffering);
        setRetryBuffering(retryBuffering);
    }


    @NonNull
    @Override
    public ExoPlayer getPlayer() {
        return mExoPlayer;
    }

    @Override
    public void releaseDecoder(boolean isFromUser, boolean isMainThread) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            if (isFromUser) {
                setEvent(null);
            }
            release(isMainThread);
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer2 => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void initExoFFmpeg(@NonNull Context context, @NonNull ExoPlayer.Builder exoBuilder) {
        try {
            if (null == exoBuilder)
                throw new Exception("exoBuilder error: null");
            PlayerBuilder playerBuilder = PlayerManager.getInstance().getConfig();
            if (null == playerBuilder)
                throw new Exception("playerBuilder error: null");
            int exoFFmpeg = playerBuilder.getExoFFmpeg();
            // only_mediacodec
            if (exoFFmpeg == PlayerType.FFmpegType.EXO_RENDERER_ONLY_MEDIACODEC) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseOnlyMediaCodecRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseOnlyMediaCodecRenderersFactory");
                exoBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseOnlyMediaCodecRenderersFactory(context));
            }
            // only_mediacodec_audio
            else if (exoFFmpeg == PlayerType.FFmpegType.EXO_RENDERER_ONLY_MEDIACODEC_AUDIO) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseOnlyMediaCodecAudioRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseOnlyMediaCodecAudioRenderersFactory");
                exoBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseOnlyMediaCodecAudioRenderersFactory(context));
            }
            // only_mediacodec_video
            else if (exoFFmpeg == PlayerType.FFmpegType.EXO_RENDERER_ONLY_MEDIACODEC_VIDEO) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseOnlyMediaCodecVideoRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseOnlyMediaCodecVideoRenderersFactory");
                exoBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseOnlyMediaCodecVideoRenderersFactory(context));
            }
            // only_ffmpeg
            if (exoFFmpeg == PlayerType.FFmpegType.EXO_RENDERER_ONLY_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseOnlyFFmpegRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseOnlyFFmpegRenderersFactory");
                exoBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseOnlyFFmpegRenderersFactory(context));
            }
            // only_ffmpeg_audio
            else if (exoFFmpeg == PlayerType.FFmpegType.EXO_RENDERER_ONLY_FFMPEG_AUDIO) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseOnlyFFmpegAudioRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseOnlyFFmpegAudioRenderersFactory");
                exoBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseOnlyFFmpegAudioRenderersFactory(context));
            }
            // only_ffmpeg_video
            else if (exoFFmpeg == PlayerType.FFmpegType.EXO_RENDERER_ONLY_FFMPEG_VIDEO) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseOnlyFFmpegVideoRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseOnlyFFmpegVideoRenderersFactory");
                exoBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseOnlyFFmpegVideoRenderersFactory(context));
            }
            // video_mediacodec_audio_ffmpeg
            else if (exoFFmpeg == PlayerType.FFmpegType.EXO_RENDERER_VIDEO_MEDIACODEC_AUDIO_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseVideoMediaCodecAudioFFmpegRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseVideoMediaCodecAudioFFmpegRenderersFactory");
                exoBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseVideoMediaCodecAudioFFmpegRenderersFactory(context));
            }
            // video_ffmpeg_audio_mediacodec
            else if (exoFFmpeg == PlayerType.FFmpegType.EXO_RENDERER_VIDEO_FFMPEG_AUDIO_MEDIACODEC) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseVideoFFmpegAudioMediaCodecRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseVideoFFmpegAudioMediaCodecRenderersFactory");
                exoBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseVideoFFmpegAudioMediaCodecRenderersFactory(context));
            }
            throw new Exception("not find: config");
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer2 => initExoFFmpeg => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(@NonNull Context context, @NonNull boolean logger, @NonNull int seekParameters) {
        try {
            // 1
            releaseDecoder(false, true);
            // 2
            ExoPlayer.Builder builder = new ExoPlayer.Builder(context);
            builder.setAnalyticsCollector(new DefaultAnalyticsCollector(Clock.DEFAULT));
            builder.setBandwidthMeter(DefaultBandwidthMeter.getSingletonInstance(context));
            builder.setLoadControl(new DefaultLoadControl());
            builder.setMediaSourceFactory(new DefaultMediaSourceFactory(context));
            builder.setTrackSelector(new DefaultTrackSelector(context));

            // 3
            initExoFFmpeg(context, builder);

            mExoPlayer = builder.build();
            mExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
            setVolume(1F, 1F);
            // seek model
            if (seekParameters == PlayerType.SeekType.EXO_SEEK_CLOSEST_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.CLOSEST_SYNC);
            } else if (seekParameters == PlayerType.SeekType.EXO_SEEK_PREVIOUS_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.PREVIOUS_SYNC);
            } else if (seekParameters == PlayerType.SeekType.EXO_SEEK_NEXT_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.NEXT_SYNC);
            } else {
                mExoPlayer.setSeekParameters(SeekParameters.DEFAULT);
            }

            // log
            try {
                Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary");
                if (null != clazz) {
                    com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary.ffmpegLogger(logger);
                }
            } catch (Exception e) {
            }

            if (null != mAnalyticsListener) {
                mAnalyticsListener = null;
            }
            mAnalyticsListener = new AnalyticsListener() {
                @Override
                public void onPlayWhenReadyChanged(EventTime eventTime, boolean playWhenReady, int reason) {
//        MPLogUtil.log("VideoExoPlayer2 => onPlayWhenReadyChanged => playWhenReady = " + playWhenReady + ", reason = " + reason);
                }

                @Override
                public void onPlayerError(EventTime eventTime, PlaybackException error) {
                    MPLogUtil.log("VideoExoPlayer2 => onPlayerError => error = " + error.getMessage() + ", mExoPlayer = " + mExoPlayer + ", mRetryBuffering = " + mRetryBuffering);

                    try {
                        if (null == error)
                            throw new Exception("PlaybackException error: null");
                        if (!(error instanceof ExoPlaybackException))
                            throw new Exception("PlaybackException error: not instanceof ExoPlaybackException");
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_STOP);
                        if (mRetryBuffering) {
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_ERROR_IGNORE);
                        } else {
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_ERROR_SOURCE);
                        }
                    } catch (Exception e) {
                        MPLogUtil.log("VideoExoPlayer2 => onPlayerError => error = " + error.getMessage());
                    }
                }

                @Override
                public void onTimelineChanged(EventTime eventTime, int reason) {
                    MPLogUtil.log("VideoExoPlayer2 => onTimelineChanged => reason = " + reason + ", totalBufferedDurationMs = " + eventTime.totalBufferedDurationMs + ", realtimeMs = " + eventTime.realtimeMs);
                }

                @Override
                public void onEvents(Player player, Events events) {
//                    MediaLogUtil.log("VideoExoPlayer2 => onEvents => isPlaying = " + player.isPlaying());
                }

                @Override
                public void onVideoSizeChanged(EventTime eventTime, VideoSize videoSize) {
                    onMeasure(PlayerType.KernelType.EXO_V2, videoSize.width, videoSize.height, videoSize.unappliedRotationDegrees > 0 ? videoSize.unappliedRotationDegrees : PlayerType.RotationType.Rotation_0);
                }

                @Override
                public void onIsPlayingChanged(EventTime eventTime, boolean isPlaying) {
                    MPLogUtil.log("VideoExoPlayer2 => onIsPlayingChanged => isPlaying = " + isPlaying + ", mPlayWhenReady = " + mPlayWhenReady + ", mPrepared = " + mPrepared);
                }

                @Override
                public void onPlaybackStateChanged(EventTime eventTime, int state) {
                    MPLogUtil.log("VideoExoPlayer2 => onPlaybackStateChanged => state = " + state + ", mute = " + isMute());

                    // 播放错误
                    if (state == Player.STATE_IDLE) {
                        MPLogUtil.log("VideoExoPlayer2 => onPlaybackStateChanged[播放错误] =>");
                        if (mRetryBuffering) {
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_ERROR_IGNORE);
                        } else {
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_ERROR_SOURCE);
                        }
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_STOP);
                    }
                    // 播放结束
                    else if (state == Player.STATE_ENDED) {
                        MPLogUtil.log("VideoExoPlayer2 => onPlaybackStateChanged[播放结束] =>");
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_VIDEO_END);
                    }
                    // 播放开始
                    else if (state == Player.STATE_READY) {
                        MPLogUtil.log("VideoExoPlayer2 => onPlaybackStateChanged[播放开始] => mPrepared = " + mPrepared + ", mRetryBuffering = " + mRetryBuffering);
                        try {
                            if (mPrepared)
                                throw new Exception("mPrepared warning: true");
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_STOP);
                            if (mRetryBuffering) {
                                mRetryBuffering = false;
                                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_BUFFERING_STOP);
                                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_VIDEO_START_RETRY);
                            } else {
                                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_VIDEO_START);
//                                if (!mPlayWhenReady) {
//                                    mPlayWhenReady = true;
//                                    pause();
//                                }
                            }
                        } catch (Exception e) {
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_BUFFERING_STOP);
                        }
                        mPrepared = true;
                    }
                    // 播放缓冲
                    else if (state == Player.STATE_BUFFERING) {
                        MPLogUtil.log("VideoExoPlayer2 => onPlaybackStateChanged[播放缓冲] =>");
                        try {
                            if (!mPrepared)
                                throw new Exception("mPrepared warning: false");
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_BUFFERING_START);
                        } catch (Exception e) {
                            MPLogUtil.log("VideoExoPlayer2 => onPlaybackStateChanged => STATE_READY => " + e.getMessage());
                        }
                    }
                    // 未知??
                    else {
                        MPLogUtil.log("VideoExoPlayer2 => onPlaybackStateChanged[未知??] =>");
                    }
                }

                @Override
                public void onRenderedFirstFrame(EventTime eventTime, Object output, long renderTimeMs) {
                    MPLogUtil.log("VideoExoPlayer2 => onRenderedFirstFrame =>");
                    try {
                        long seek = getSeek();
                        if (seek <= 0)
                            throw new Exception("seek warning: " + seek);
                        seekTo(seek);
                    } catch (Exception e) {
                        MPLogUtil.log("VideoExoPlayer2 => onRenderedFirstFrame => " + e.getMessage());
                    }
                }

                @Override
                public void onVideoInputFormatChanged(EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
                    MPLogUtil.log("VideoExoPlayer2 => onVideoInputFormatChanged[出画面] =>");
                }

                @Override
                public void onAudioInputFormatChanged(EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
                    MPLogUtil.log("VideoExoPlayer2 => onAudioInputFormatChanged =>");
                }
            };
            mExoPlayer.addAnalyticsListener(mAnalyticsListener);

            if (mSpeedPlaybackParameters != null) {
                mExoPlayer.setPlaybackParameters(mSpeedPlaybackParameters);
            }
//        mIsPreparing = true;

            //播放器日志
//        if (mExoPlayer.getTrackSelector() instanceof MappingTrackSelector) {
//            mExoPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) mExoPlayer.getTrackSelector(), "ExoPlayer"));
//        }
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer2 => createDecoder => " + e.getMessage());
        }
    }

    @Override
    public void startDecoder(@NonNull Context context, @NonNull String url, @NonNull boolean prepareAsync) {
        MPLogUtil.log("VideoExoPlayer2 => startDecoder => mExoPlayer = " + mExoPlayer + ", url = " + url + ", prepareAsync = " + prepareAsync);
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            if (url == null || url.length() == 0)
                throw new Exception("url error: " + url);
            if (mRetryBuffering) {
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_START_IGNORE);
            } else {
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_START);
            }
            PlayerBuilder config = PlayerManager.getInstance().getConfig();
            int cacheType = config.getCacheType();
            int cacheMax = config.getCacheMax();
            String cacheDir = config.getCacheDir();
            if (isLive()) {
                cacheType = PlayerType.CacheType.NONE;
            }
//            mediaSource.addEventListener(new Handler(), new MediaSourceEventListener() {
//                @Override
//                public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
//                    MediaLogUtil.log("onEXOLoadStarted => ");
//                }
//
//                @Override
//                public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
//                    MediaLogUtil.log("onEXOLoadCompleted => ");
//                }
//            });
            mExoPlayer.setMediaSource(createMediaSource(context, url, null, cacheType, cacheMax, cacheDir));
            mExoPlayer.setPlayWhenReady(mPlayWhenReady);
            if (prepareAsync) {
                mExoPlayer.prepare();
            } else {
                mExoPlayer.prepare();
            }
        } catch (Exception e) {
            if (mRetryBuffering) {
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_ERROR_IGNORE);
            } else {
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_ERROR_URL);
            }
            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_STOP);
            MPLogUtil.log("VideoExoPlayer2 => startDecoder => " + e.getMessage());
        }
    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void setSurface(@NonNull Surface surface, int w, int h) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
//            if(null == surface)
//                throw new Exception("surface error: null");
            if (null == surface) {
                mExoPlayer.setVideoSurface(null);
                mExoPlayer.clearVideoSurface();
            } else {
                mExoPlayer.setVideoSurface(surface);
            }
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer2 => setSurface => " + e.getMessage());
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            int state = mExoPlayer.getPlaybackState();
            if (state == Player.STATE_BUFFERING || state == Player.STATE_READY) {
                return mExoPlayer.getPlayWhenReady();
            } else if (state == Player.STATE_IDLE || state == Player.STATE_ENDED) {
                return false;
            } else {
                throw new Exception("not find");
            }
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer2 => isPlaying => " + e.getMessage());
            return false;
        }
    }

    @Override
    public void seekTo(@NonNull long position) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.seekTo(position);
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer2 => seekTo => " + e.getMessage());
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            return mExoPlayer.getCurrentPosition();
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer2 => getPosition => " + e.getMessage());
            return -1L;
        }
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            long duration = mExoPlayer.getDuration();
            if (duration < 0) {
                duration = 0L;
            }
            return duration;
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer2 => getDuration => " + e.getMessage());
            return 0L;
        }
    }

    @Override
    public void setPlayWhenReady(boolean playWhenReady) {
        this.mPlayWhenReady = playWhenReady;
    }

    @Override
    public boolean isPlayWhenReady() {
        return mPlayWhenReady;
    }

    @Override
    public void setRetryBuffering(@NonNull boolean retryBuffering) {
        this.mRetryBuffering = retryBuffering;
    }

    /**
     * 设置播放速度
     */
    @Override
    public void setSpeed(float speed) {
        PlaybackParameters playbackParameters = new PlaybackParameters(speed);
        mSpeedPlaybackParameters = playbackParameters;
        if (mExoPlayer != null) {
            mExoPlayer.setPlaybackParameters(playbackParameters);
        }
    }

    /**
     * 获取播放速度
     */
    @Override
    public float getSpeed() {
        try {
            return mSpeedPlaybackParameters.speed;
        } catch (Exception e) {
            return 1f;
        }
    }

    @Override
    public void setVolume(float v1, float v2) {
        try {
            float value;
            boolean mute = isMute();
            MPLogUtil.log("VideoExoPlayer2 => setVolume => mute = " + mute);
            if (mute) {
                value = 0F;
            } else {
                value = Math.max(v1, v2);
                if (value > 1f) {
                    value = 1f;
                }
            }
            MPLogUtil.log("VideoExoPlayer2 => setVolume => value = " + value);
            mExoPlayer.setVolume(value);
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer2 => setVolume => " + e.getMessage());
        }
    }

    @Override
    public boolean isMute() {
        return mMute;
    }

    @Override
    public void setMute(boolean v) {
        mMute = v;
        setVolume(v ? 0f : 1f, v ? 0f : 1f);
    }

    @Override
    public long getSeek() {
        return mSeek;
    }

    @Override
    public void setSeek(long seek) {
        if (seek < 0)
            return;
        mSeek = seek;
    }

    @Override
    public long getMax() {
        return mMax;
    }

    @Override
    public void setMax(long max) {
        if (max < 0)
            return;
        mMax = max;
    }

    @Override
    public boolean isLive() {
        return mLive;
    }

    @Override
    public void setLive(@NonNull boolean live) {
        this.mLive = live;
    }

    @Override
    public void setLooping(boolean loop) {
        this.mLoop = loop;
    }

    @Override
    public boolean isLooping() {
        return mLoop;
    }

    @Override
    public boolean isPrepared() {
        return mPrepared;
    }

    @Override
    public void release(boolean isMainThread) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            if (null != mAnalyticsListener) {
                mExoPlayer.removeAnalyticsListener(mAnalyticsListener);
            }
            mAnalyticsListener = null;
            mSpeedPlaybackParameters = null;
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.setVideoSurface(null);
            mExoPlayer.release();
            mExoPlayer = null;
            mPrepared = false;
            mRetryBuffering = false;
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer2 => release => " + e.getMessage());
        }
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.play();
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer2 => start => " + e.getMessage());
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
            mExoPlayer.pause();
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer2 => pause => " + e.getMessage());
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop(boolean isMainThread) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.stop();
            mPrepared = false;
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer2 => stop => " + e.getMessage());
        }
    }

    /************************/

    public MediaSource createMediaSource(@NonNull Context context,
                                         @NonNull String mediaUrl,
                                         @Nullable String subtitleUrl,
                                         @PlayerType.CacheType int cacheType,
                                         @NonNull int cacheMax,
                                         @NonNull String cacheDir) {

        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => mediaUrl = " + mediaUrl);
        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => subtitleUrl = " + subtitleUrl);
        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => cacheType = " + cacheType);
        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => cacheMax = " + cacheMax);
        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => cacheDir = " + cacheDir);

        String scheme;
        Uri uri = Uri.parse(mediaUrl);
        try {
            scheme = uri.getScheme();
        } catch (Exception e) {
            scheme = null;
        }
        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => scheme = " + scheme);

        // rtmp
        if (PlayerType.SchemeType.RTMP.equals(scheme)) {
            // log
            try {
                Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.rtmp.RtmpDataSource");
                if (null == clazz)
                    throw new Exception();
                MediaItem mediaItem = MediaItem.fromUri(uri);
                return new ProgressiveMediaSource.Factory(new com.google.android.exoplayer2.ext.rtmp.RtmpDataSource.Factory()).createMediaSource(mediaItem);
            } catch (Exception e) {
                return null;
            }
        }
        // rtsp
        else if (PlayerType.SchemeType.RTSP.equals(scheme)) {
            MediaItem mediaItem = MediaItem.fromUri(uri);
            return new RtspMediaSource.Factory().createMediaSource(mediaItem);
        }
        // other
        else {
            // 1
            int contentType;
            try {
                String s = mediaUrl.toLowerCase();
                if (s.contains(PlayerType.SchemeType._MPD)) {
                    contentType = C.CONTENT_TYPE_DASH;
                } else if (s.contains(PlayerType.SchemeType._M3U)) {
                    contentType = C.CONTENT_TYPE_HLS;
                } else if (s.contains(PlayerType.SchemeType._M3U8)) {
                    contentType = C.CONTENT_TYPE_HLS;
                } else if (s.matches(PlayerType.SchemeType._MATCHES)) {
                    contentType = C.CONTENT_TYPE_SS;
                } else {
                    contentType = C.CONTENT_TYPE_OTHER;
                }
            } catch (Exception e) {
                contentType = C.CONTENT_TYPE_OTHER;
            }
            MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => contentType = " + contentType);

            // 2
            MediaItem.Builder builder = new MediaItem.Builder();
            builder.setUri(Uri.parse(mediaUrl));
            if (null != subtitleUrl && subtitleUrl.length() > 0) {
                MediaItem.SubtitleConfiguration.Builder subtitle = new MediaItem.SubtitleConfiguration.Builder(Uri.parse(mediaUrl));
                subtitle.setMimeType(MimeTypes.APPLICATION_SUBRIP);
                subtitle.setLanguage("en");
                subtitle.setSelectionFlags(C.SELECTION_FLAG_AUTOSELECT); // C.SELECTION_FLAG_DEFAULT
                builder.setSubtitleConfigurations(Arrays.asList(subtitle.build()));

//            MediaItem.SubtitleConfiguration.Builder builder = new MediaItem.SubtitleConfiguration.Builder(srtUri);
//            builder.setMimeType(MimeTypes.APPLICATION_SUBRIP);
//            builder.setMimeType(MimeTypes.TEXT_VTT);
//            builder.setLanguage("en");
//            builder.setSelectionFlags(C.SELECTION_FLAG_DEFAULT);
//            MediaItem.SubtitleConfiguration subtitle = builder.build();
//            MediaSource textMediaSource = new SingleSampleMediaSource.Factory(factory).createMediaSource(subtitle, C.TIME_UNSET);
//            textMediaSource.getMediaItem().mediaMetadata.subtitle.toString();
//            MediaLogUtil.log("SRT => " + subtitle);
//            return new MergingMediaSource(mediaSource, srtSource);
            }

            // head
//            refreshHeaders(httpFactory, headers);

            boolean useOkhttp = PlayerManager.getInstance().getConfig().isExoUseOkhttp();
            MPLogUtil.log("VideoExoPlayer2 => createMediaSource => useOkhttp = " + useOkhttp);
            DataSource.Factory dataSourceFactory;
            if (useOkhttp) {
                int okhttpTimeoutSeconds = PlayerManager.getInstance().getConfig().getExoUseOkhttpTimeoutSeconds();
                MPLogUtil.log("VideoExoPlayer2 => createMediaSource => okhttpTimeoutSeconds = " + okhttpTimeoutSeconds);
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .readTimeout(okhttpTimeoutSeconds, TimeUnit.SECONDS)
                        .writeTimeout(okhttpTimeoutSeconds, TimeUnit.SECONDS)
                        .callTimeout(okhttpTimeoutSeconds, TimeUnit.SECONDS)
                        .connectionPool(new ConnectionPool(10, 60, TimeUnit.MINUTES))
                        .retryOnConnectionFailure(true)
                        .proxySelector(new ProxySelector() { // 禁止抓包
                            @Override
                            public List<Proxy> select(URI uri) {
                                return Collections.singletonList(Proxy.NO_PROXY);
                            }

                            @Override
                            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                            }
                        })
                        .build();
                OkHttpDataSource.Factory okHttpFactory = new OkHttpDataSource.Factory(okHttpClient);
                okHttpFactory.setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY);
                dataSourceFactory = okHttpFactory;
            } else {
                DefaultHttpDataSource.Factory httpFactory = new DefaultHttpDataSource.Factory();
                httpFactory.setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY);
                httpFactory.setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS);
                httpFactory.setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS);
                httpFactory.setAllowCrossProtocolRedirects(true);
                httpFactory.setKeepPostFor302Redirects(true);
                dataSourceFactory = httpFactory;
            }

            DataSource.Factory dataSource;
            if (cacheType == PlayerType.CacheType.NONE) {
                dataSource = new DefaultDataSource.Factory(context, dataSourceFactory);
            } else {
                dataSource = new DefaultDataSource.Factory(context, dataSourceFactory);
//                CacheDataSource.Factory cacheFactory = new CacheDataSource.Factory();
//                SimpleCache cache = VideoExoPlayer2Cache.getSimpleCache(context, cacheMax, cacheDir);
//                cacheFactory.setCache(cache);
//                cacheFactory.setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
//                cacheFactory.setUpstreamDataSourceFactory(dataSourceFactory);
//                dataSource = cacheFactory;
            }

            // 3
            MediaItem mediaItem = builder.build();
            switch (contentType) {
                case C.CONTENT_TYPE_DASH:
                    return new DashMediaSource.Factory(dataSource).createMediaSource(mediaItem);
                case C.CONTENT_TYPE_SS:
                    return new SsMediaSource.Factory(dataSource).createMediaSource(mediaItem);
                case C.CONTENT_TYPE_HLS:
                    return new HlsMediaSource.Factory(dataSource).createMediaSource(mediaItem);
                default:
                    DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                    extractorsFactory.setConstantBitrateSeekingEnabled(true);
                    return new ProgressiveMediaSource.Factory(dataSource, extractorsFactory).createMediaSource(mediaItem);
            }
        }
    }
}
