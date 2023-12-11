package lib.kalu.mediaplayer.core.kernel.video.exo2;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.FloatRange;
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

import org.json.JSONArray;

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
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

@Keep
public final class VideoExo2Player extends VideoBasePlayer {

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;
    private boolean mPlayWhenReady = true;
    private boolean mPrepared = false;
//    private PlaybackParameters mSpeedPlaybackParameters;

    private ExoPlayer mExoPlayer;
    private AnalyticsListener mAnalyticsListener;

    public VideoExo2Player(@NonNull VideoPlayerApi playerApi, @NonNull VideoKernelApiEvent eventApi) {
        super(playerApi, eventApi);
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
            MPLogUtil.log("VideoExo2Player => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void initOptionsExo(@NonNull Context context, @NonNull ExoPlayer.Builder exoBuilder) {
        try {
            if (null == exoBuilder)
                throw new Exception("exoBuilder error: null");
            PlayerBuilder playerBuilder = PlayerManager.getInstance().getConfig();
            if (null == playerBuilder)
                throw new Exception("playerBuilder error: null");
            int exoFFmpeg = playerBuilder.getExoFFmpeg();
            // only_mediacodec
            if (exoFFmpeg == PlayerType.FFmpegType.EXO_RENDERER_ONLY_MEDIACODEC) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseVideoMediaCodecAudioMediaCodecRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseVideoMediaCodecAudioMediaCodecRenderersFactory");
                exoBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseVideoMediaCodecAudioMediaCodecRenderersFactory(context));
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
            else if (exoFFmpeg == PlayerType.FFmpegType.EXO_RENDERER_ONLY_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseVideoFFmpegAudioFFmpegRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseVideoFFmpegAudioFFmpegRenderersFactory");
                exoBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseVideoFFmpegAudioFFmpegRenderersFactory(context));
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
            MPLogUtil.log("VideoExo2Player => initOptionsExo => " + e.getMessage());
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
            initOptionsExo(context, builder);

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
//        MPLogUtil.log("VideoExo2Player => onPlayWhenReadyChanged => playWhenReady = " + playWhenReady + ", reason = " + reason);
                }

                @Override
                public void onPlayerError(EventTime eventTime, PlaybackException error) {
                    try {
                        if (null == error)
                            throw new Exception("PlaybackException error: null");
                        if (!(error instanceof ExoPlaybackException))
                            throw new Exception("PlaybackException error: not instanceof ExoPlaybackException");
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_STOP);
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_ERROR_SOURCE);
                    } catch (Exception e) {
                        MPLogUtil.log("VideoExo2Player => onPlayerError => error = " + error.getMessage());
                    }
                }

                @Override
                public void onTimelineChanged(EventTime eventTime, int reason) {
                    MPLogUtil.log("VideoExo2Player => onTimelineChanged => reason = " + reason + ", totalBufferedDurationMs = " + eventTime.totalBufferedDurationMs + ", realtimeMs = " + eventTime.realtimeMs);
                }

                @Override
                public void onEvents(Player player, Events events) {
//                    MediaLogUtil.log("VideoExo2Player => onEvents => isPlaying = " + player.isPlaying());
                }

                @Override
                public void onVideoSizeChanged(EventTime eventTime, VideoSize videoSize) {
                    onMeasure(PlayerType.KernelType.EXO_V2, videoSize.width, videoSize.height, videoSize.unappliedRotationDegrees > 0 ? videoSize.unappliedRotationDegrees : PlayerType.RotationType.Rotation_0);
                }

                @Override
                public void onIsPlayingChanged(EventTime eventTime, boolean isPlaying) {
                    MPLogUtil.log("VideoExo2Player => onIsPlayingChanged => isPlaying = " + isPlaying + ", mPlayWhenReady = " + mPlayWhenReady + ", mPrepared = " + mPrepared);
                }

                @Override
                public void onPlaybackStateChanged(EventTime eventTime, int state) {
                    MPLogUtil.log("VideoExo2Player => onPlaybackStateChanged => state = " + state + ", mute = " + isMute());

                    // 播放错误
                    if (state == Player.STATE_IDLE) {
                        MPLogUtil.log("VideoExo2Player => onPlaybackStateChanged[播放错误] =>");
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_ERROR_SOURCE);
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_STOP);
                    }
                    // 播放结束
                    else if (state == Player.STATE_ENDED) {
                        MPLogUtil.log("VideoExo2Player => onPlaybackStateChanged[播放结束] =>");
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_VIDEO_END);
                    }
                    // 播放开始
                    else if (state == Player.STATE_READY) {
                        try {
                            if (mPrepared)
                                throw new Exception("mPrepared warning: true");
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_STOP);
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_VIDEO_START);
//                                if (!mPlayWhenReady) {
//                                    mPlayWhenReady = true;
//                                    pause();
//                                }
                        } catch (Exception e) {
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_BUFFERING_STOP);
                        }
                        mPrepared = true;
                    }
                    // 播放缓冲
                    else if (state == Player.STATE_BUFFERING) {
                        MPLogUtil.log("VideoExo2Player => onPlaybackStateChanged[播放缓冲] =>");
                        try {
                            if (!mPrepared)
                                throw new Exception("mPrepared warning: false");
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_BUFFERING_START);
                        } catch (Exception e) {
                            MPLogUtil.log("VideoExo2Player => onPlaybackStateChanged => STATE_READY => " + e.getMessage());
                        }
                    }
                    // 未知??
                    else {
                        MPLogUtil.log("VideoExo2Player => onPlaybackStateChanged[未知??] =>");
                    }
                }

                @Override
                public void onRenderedFirstFrame(EventTime eventTime, Object output, long renderTimeMs) {
                    MPLogUtil.log("VideoExo2Player => onRenderedFirstFrame =>");
                    try {
                        long seek = getSeek();
                        if (seek <= 0)
                            throw new Exception("seek warning: " + seek);
                        seekTo(seek);
                    } catch (Exception e) {
                        MPLogUtil.log("VideoExo2Player => onRenderedFirstFrame => " + e.getMessage());
                    }
                }

                @Override
                public void onVideoInputFormatChanged(EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
                    MPLogUtil.log("VideoExo2Player => onVideoInputFormatChanged[出画面] =>");
                }

                @Override
                public void onAudioInputFormatChanged(EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
                    MPLogUtil.log("VideoExo2Player => onAudioInputFormatChanged =>");
                }
            };
            mExoPlayer.addAnalyticsListener(mAnalyticsListener);

//            if (mSpeedPlaybackParameters != null) {
//                mExoPlayer.setPlaybackParameters(mSpeedPlaybackParameters);
//            }
//        mIsPreparing = true;

            //播放器日志
//        if (mExoPlayer.getTrackSelector() instanceof MappingTrackSelector) {
//            mExoPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) mExoPlayer.getTrackSelector(), "ExoPlayer"));
//        }
        } catch (Exception e) {
            MPLogUtil.log("VideoExo2Player => createDecoder => " + e.getMessage());
        }
    }

    @Override
    public void startDecoder(@NonNull Context context, @NonNull String url, @NonNull boolean prepareAsync) {
        MPLogUtil.log("VideoExo2Player => startDecoder => mExoPlayer = " + mExoPlayer + ", url = " + url + ", prepareAsync = " + prepareAsync);
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            if (url == null || url.length() == 0)
                throw new Exception("url error: " + url);
            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_START);
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

            MediaSource mediaSource = buildMediaSource(context, url, null, cacheType, cacheMax, cacheDir);
            mExoPlayer.setMediaSource(mediaSource);
            mExoPlayer.setPlayWhenReady(mPlayWhenReady);
            if (prepareAsync) {
                mExoPlayer.prepare();
            } else {
                mExoPlayer.prepare();
            }
        } catch (Exception e) {
            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_ERROR_URL);
            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.EVENT_LOADING_STOP);
            MPLogUtil.log("VideoExo2Player => startDecoder => " + e.getMessage());
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
            MPLogUtil.log("VideoExo2Player => setSurface => " + e.getMessage());
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        try {
            if (!mPrepared)
                throw new Exception("mPrepared warning: false");
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
            MPLogUtil.log("VideoExo2Player => isPlaying => " + e.getMessage());
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
            MPLogUtil.log("VideoExo2Player => seekTo => " + e.getMessage());
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        try {
            if (!mPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            long currentPosition = mExoPlayer.getCurrentPosition();
            if (currentPosition < 0)
                throw new Exception("currentPosition warning: " + currentPosition);
            return currentPosition;
        } catch (Exception e) {
            MPLogUtil.log("VideoExo2Player => getPosition => " + e.getMessage());
            return -1L;
        }
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        try {
            if (!mPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            long duration = mExoPlayer.getDuration();
            if (duration <= 0)
                throw new Exception("duration warning: " + duration);
            return duration;
        } catch (Exception e) {
            MPLogUtil.log("VideoExo2Player => getDuration => " + e.getMessage());
            return -1L;
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

    /**
     * 设置播放速度
     */
    @Override
    public boolean setSpeed(@FloatRange(from = 1F, to = 4F) float speed) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mMediaPlayer error: null");
            PlaybackParameters playbackParameters = mExoPlayer.getPlaybackParameters();
            if (null != playbackParameters) {
                playbackParameters = playbackParameters.withSpeed(speed);
            } else {
                playbackParameters = new PlaybackParameters(speed);
            }
            mExoPlayer.setPlaybackParameters(playbackParameters);
            return true;
        } catch (Exception e) {
            MPLogUtil.log("VideoExo2Player => setSpeed => " + e.getMessage());
            return false;
        }
    }

    @Override
    @FloatRange(from = 1F, to = 4F)
    public float getSpeed() {
        try {
            if (null == mExoPlayer)
                throw new Exception("mMediaPlayer error: null");
            PlaybackParameters playbackParameters = mExoPlayer.getPlaybackParameters();
            if (null == playbackParameters)
                throw new Exception("playbackParameters error: null");
            float speed = playbackParameters.speed;
            if (speed < 1f)
                throw new Exception("speed error: " + speed);
            return speed;
        } catch (Exception e) {
            MPLogUtil.log("VideoExo2Player => getSpeed => " + e.getMessage());
            return 1F;
        }
    }

    @Override
    public void setVolume(float v1, float v2) {
        try {
            float value;
            boolean mute = isMute();
            MPLogUtil.log("VideoExo2Player => setVolume => mute = " + mute);
            if (mute) {
                value = 0F;
            } else {
                value = Math.max(v1, v2);
                if (value > 1f) {
                    value = 1f;
                }
            }
            MPLogUtil.log("VideoExo2Player => setVolume => value = " + value);
            mExoPlayer.setVolume(value);
        } catch (Exception e) {
            MPLogUtil.log("VideoExo2Player => setVolume => " + e.getMessage());
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
            mExoPlayer.setPlaybackParameters(null);
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.setVideoSurface(null);
            mExoPlayer.release();
            mExoPlayer = null;
            mPrepared = false;
        } catch (Exception e) {
            MPLogUtil.log("VideoExo2Player => release => " + e.getMessage());
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
            MPLogUtil.log("VideoExo2Player => start => " + e.getMessage());
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
            if (!mPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer)
                throw new Exception("mMediaPlayer error: null");
            mExoPlayer.pause();
        } catch (Exception e) {
            MPLogUtil.log("VideoExo2Player => pause => " + e.getMessage());
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
            MPLogUtil.log("VideoExo2Player => stop => " + e.getMessage());
        }
    }

    @Override
    public JSONArray getTrackInfo() {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            return null;
        } catch (Exception e) {
            MPLogUtil.log("VideoExo2Player => getTrackInfo => " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean switchTrack(@NonNull int trackId) {
        return super.switchTrack(trackId);
    }

    /************************/

    public MediaSource buildMediaSource(@NonNull Context context,
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
            MPLogUtil.log("VideoExo2Player => createMediaSource => useOkhttp = " + useOkhttp);
            DataSource.Factory dataSourceFactory;
            if (useOkhttp) {
                int okhttpTimeoutSeconds = PlayerManager.getInstance().getConfig().getExoUseOkhttpTimeoutSeconds();
                MPLogUtil.log("VideoExo2Player => createMediaSource => okhttpTimeoutSeconds = " + okhttpTimeoutSeconds);
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
            if (cacheType == PlayerType.CacheType.NONE || mediaUrl.startsWith("file")) {
                dataSource = new DefaultDataSource.Factory(context, dataSourceFactory);
            } else {
//                dataSource = new DefaultDataSource.Factory(context, dataSourceFactory);

//                // a
                CacheDataSource.Factory dataSource1 = new CacheDataSource.Factory();
                SimpleCache simpleCache1 = VideoExo2PlayerSimpleCache.getSimpleCache(context, cacheMax, cacheDir);
                dataSource1.setCache(simpleCache1);
                dataSource1.setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
                dataSource1.setUpstreamDataSourceFactory(dataSourceFactory);
                dataSource = dataSource1;

//                // b
//                VideoExoplayer2CacheDataSource.Factory dataSource1 = new VideoExoplayer2CacheDataSource.Factory();
//                dataSource1.setUpstreamDataSourceFactory(dataSourceFactory);
//                dataSource1.setFlags(VideoExoplayer2CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
//                SimpleCache simpleCache1 = VideoExoPlayer2Cache.getSimpleCache(context, cacheMax, cacheDir);
//                dataSource1.setCache(simpleCache1);
//                CacheDataSink.Factory sinkFactory1 = new CacheDataSink.Factory().setCache(simpleCache1).setFragmentSize(C.LENGTH_UNSET);
//                dataSource1.setCacheWriteDataSinkFactory(sinkFactory1);
//                dataSource = dataSource1;
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
