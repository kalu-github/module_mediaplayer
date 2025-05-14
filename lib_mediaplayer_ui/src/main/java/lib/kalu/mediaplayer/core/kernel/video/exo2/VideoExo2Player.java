package lib.kalu.mediaplayer.core.kernel.video.exo2;

import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.view.Surface;

import androidx.media3.exoplayer.hls.playlist.DefaultHlsPlaylistTracker;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.analytics.DefaultAnalyticsCollector;
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider;
import com.google.android.exoplayer2.decoder.DecoderReuseEvaluation;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.hls.HlsManifest;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMultivariantPlaylist;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.CueGroup;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheEvictor;
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory;
import com.google.android.exoplayer2.upstream.cache.CacheSpan;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.video.VideoSize;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.args.TrackArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;


public final class VideoExo2Player extends VideoBasePlayer {

    private boolean mPlayWhenReadySeeking = false;
    private boolean mSeeking = false;
    private boolean isBuffering = false;


    private HlsManifest mHlsManifest;
    private SimpleCache mSimpleCache;
    private ExoPlayer mExoPlayer;

    @Override
    public ExoPlayer getPlayer() {
        return mExoPlayer;
    }

    @Override
    public void releaseDecoder(boolean isFromUser) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            if (isFromUser) {
                setEvent(null);
            }
            clear();
            unRegistListener();
            release();
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => releaseDecoder => " + e.getMessage());
        }
    }


    @Override
    public void createDecoder(Context context, StartArgs args) {
        try {
            if (null != mExoPlayer)
                throw new Exception("warning: null != mExoPlayer");
            if (null == args)
                throw new Exception("error: args null");

            ExoPlayer.Builder builder = new ExoPlayer.Builder(context)
                    // 播放器调试和诊断相关的配置项
                    .setUsePlatformDiagnostics(false)
                    // 创建渲染器工厂
                    .setRenderersFactory(new DefaultRenderersFactory(context) {
                        @Override
                        protected void buildTextRenderers(Context context, TextOutput textOutput, Looper looper, int i, ArrayList<Renderer> arrayList) {
                            super.buildTextRenderers(context, textOutput, looper, i, arrayList);
//                            TextRenderer textRenderer = new TextRenderer(textOutput, looper);
//                            textRenderer.experimentalSetLegacyDecodingEnabled(true);
//                            arrayList.arrayListadd(textRenderer);
                        }
                    })
                    .setMediaSourceFactory(new DefaultMediaSourceFactory(context)
//                            .experimentalParseSubtitlesDuringExtraction(true)
                    )
                    // 监听
                    .setAnalyticsCollector(new DefaultAnalyticsCollector(Clock.DEFAULT))
                    // 配置带宽测量器
                    .setBandwidthMeter(new DefaultBandwidthMeter.Builder(context)
                            // 初始带宽估算为5Mbps（5,000,000 bps）
//                            .setInitialBitrateEstimate(5_000_000)
                            .build())
                    // 缓冲缓存
                    .setLoadControl(new DefaultLoadControl.Builder()
                            // minBufferMs 最小缓冲时长的参数，单位为毫秒
                            // maxBufferMs 限制最大缓冲时长的参数，单位是毫秒
                            // bufferForPlaybackMs 如果设置 bufferForPlaybackMs 为 5000，那么播放器会在开始播放前先缓冲 5 秒钟的媒体数据
                            // bufferForPlaybackAfterRebufferMs 用于指定在播放过程中出现重新缓冲（Rebuffer）后，为了保证后续播放流畅，需要再次缓冲的时长，单位同样是毫秒
                            .setBufferDurationsMs(60_000, 60_000, 5_000, 5_000)
                            .build())
                    // 自适应码率
                    .setTrackSelector(new DefaultTrackSelector(context, DefaultTrackSelector.Parameters.getDefaults(context)
                            .buildUpon()
                            // 主字幕轨道
                            .setPreferredTextRoleFlags(C.ROLE_FLAG_MAIN)
                            // 主音频轨道
                            .setPreferredAudioRoleFlags(C.ROLE_FLAG_MAIN)
                            // 主视频轨道
                            .setPreferredVideoRoleFlags(C.ROLE_FLAG_MAIN)
                            // 音频禁止混合 MIME 类型切换（如视频+音频单独切换）
                            .setAllowAudioMixedMimeTypeAdaptiveness(true)
                            // 视频禁止混合 MIME 类型切换（如视频+音频单独切换）
                            .setAllowVideoMixedMimeTypeAdaptiveness(true)
                            // 音频禁止非无缝切换
                            // .setAllowAudioNonSeamlessAdaptiveness(false)
                            // 视频禁止非无缝切换
                            .setAllowVideoNonSeamlessAdaptiveness(true)
                            // 音频混合声道数量的自适应性
                            .setAllowAudioMixedChannelCountAdaptiveness(true)
                            // 音频混合采样率自适应
                            .setAllowAudioMixedSampleRateAdaptiveness(true)
                            // 音频混合时解码器支持自适应
                            .setAllowAudioMixedDecoderSupportAdaptiveness(true)
                            // 音频混合时解码器支持自适应
                            .setAllowVideoMixedDecoderSupportAdaptiveness(true)
                            .build(),
                            // 10000, 25000, 25000, 0.7F
                            new AdaptiveTrackSelection.Factory(
                                    10_000,// 至少 10 秒后才允许升码率
                                    25_000, // 最多 2.5 秒后允许降码率
                                    25_000, //
                                    0.7F))); //

            int decoderType = args.getDecoderType();
            LogUtil.log("VideoExo2Player => createDecoder => decoderType = " + decoderType);
            // only_ffmpeg
            if (decoderType == PlayerType.DecoderType.ONLY_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.VideoFFmpegAudioFFmpegRenderersFactory");
                LogUtil.log("VideoExo2Player => createDecoder => EXO_ALL_FFMPEG");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_ffmpeg_audio
            else if (decoderType == PlayerType.DecoderType.ONLY_AUDIO_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.OnlyAudioFFmpegRenderersFactory");
                LogUtil.log("VideoExo2Player => createDecoder => EXO_ONLY_AUDIO_FFMPEG");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_ffmpeg_video
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.OnlyVideoFFmpegRenderersFactory");
                LogUtil.log("VideoExo2Player => createDecoder => EXO_ONLY_VIDEO_FFMPEG");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // video_mediacodec_audio_ffmpeg
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_CODEC_AUDIO_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.VideoCodecAudioFFmpegRenderersFactory");
                LogUtil.log("VideoExo2Player => createDecoder => EXO_VIDEO_CODEC_AUDIO_FFMPEG");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // video_ffmpeg_audio_mediacodec
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_FFMPRG_AUDIO_CODEC) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.VideoFFmpegAudioCodecRenderersFactory");
                LogUtil.log("VideoExo2Player => createDecoder => EXO_VIDEO_FFMPEG_AUDIO_CODEC");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_mediacodec_audio
            else if (decoderType == PlayerType.DecoderType.ONLY_AUDIO_CODEC) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.OnlyAudioCodecRenderersFactory");
                LogUtil.log("VideoExo2Player => createDecoder => EXO_ONLY_AUDIO_CODEC");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_mediacodec_video
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_CODEC) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.OnlyVideoCodecRenderersFactory");
                LogUtil.log("VideoExo2Player => createDecoder => EXO_ONLY_VIDEO_CODEC");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_mediacodec (decoderType == PlayerType.DecoderType.EXO_ALL_CODEC)
            else {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.VideoCodecAudioCodecRenderersFactory");
                LogUtil.log("VideoExo2Player => createDecoder => EXO_ALL_CODEC");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((com.google.android.exoplayer2.RenderersFactory) newInstance);
            }

            mExoPlayer = builder.build();
            registListener();

//            if (mSpeedPlaybackParameters != null) {
//                mExoPlayer.setPlaybackParameters(mSpeedPlaybackParameters);
//            }
//        mIsPreparing = true;

            //播放器日志
//        if (mExoPlayer.getTrackSelector() instanceof MappingTrackSelector) {
//            mExoPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) mExoPlayer.getTrackSelector(), "ExoPlayer"));
//        }
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => createDecoder => " + e.getMessage());
        }
    }

    @Override
    public void startDecoder(Context context, StartArgs args) {
        try {
            if (null == mExoPlayer)
                throw new Exception("warning: mExoPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            String url = args.getUrl();
            if (null == url)
                throw new Exception("error: url null");

            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.INIT_READY);

            MediaSource mediaSource = buildSource(context, args);
            mExoPlayer.setMediaSource(mediaSource);
            mExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);

            boolean playWhenReady = args.isPlayWhenReady();
            mExoPlayer.setPlayWhenReady(playWhenReady);
            boolean prepareAsync = args.isPrepareAsync();
            if (prepareAsync) {
                mExoPlayer.prepare();
            } else {
                mExoPlayer.prepare();
            }
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => startDecoder => Exception " + e.getMessage());
        }
    }

    @Override
    public void initOptions(Context context, StartArgs args) {
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mMediaPlayer null");
            boolean mute = isMute();
            setVolume(mute ? 0L : 1L, mute ? 0L : 1L);
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => initOptions => Exception " + e.getMessage());
        }

        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer warning: null");
            if (null == args)
                throw new Exception("error: args null");
            int seekType = args.getSeekType();
            if (seekType == PlayerType.SeekType.EXO_CLOSEST_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.CLOSEST_SYNC);
            } else if (seekType == PlayerType.SeekType.EXO_PREVIOUS_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.PREVIOUS_SYNC);
            } else if (seekType == PlayerType.SeekType.EXO_NEXT_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.NEXT_SYNC);
            } else if (seekType == PlayerType.SeekType.EXO_EXACT) {
                mExoPlayer.setSeekParameters(SeekParameters.EXACT);
            } else {
                mExoPlayer.setSeekParameters(SeekParameters.DEFAULT);
            }
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => initOptions => " + e.getMessage());
        }

        // log
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            Class<?> clazz = Class.forName("lib.kalu.exoplayer2.util.ExoLogUtil");
            if (null == clazz)
                throw new Exception("warning: lib.kalu.exoplayer2.util.ExoLogUtil not find");
            boolean log = args.isLog();
            lib.kalu.exoplayer2.util.ExoLogUtil.setLogger(log);
        } catch (Exception e) {
        }

        // log-jni
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary");
            if (null == clazz)
                throw new Exception("warning: com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary not find");
            // boolean log = args.isLog();
            // com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary.ffmpegLogger(log);
        } catch (Exception e) {
        }
    }

    @Override
    public void setSurface(Surface surface, int w, int h) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.clearVideoSurface();
            if (null == surface)
                throw new Exception("surface error: null");
            mExoPlayer.setVideoSurface(surface);
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => setSurface => " + e.getMessage());
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        try {
            if (!isPrepared())
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
            LogUtil.log("VideoExo2Player => isPlaying => " + e.getMessage());
            return false;
        }
    }

    @Override
    public void seekTo(long seek) {
        try {
            if (seek < 0L)
                throw new Exception("error: seek<0");
            if (null == mExoPlayer)
                throw new Exception("error: mMediaPlayer null");
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");

            long duration = getDuration();
            if (duration > 0L && seek > duration) {
                seek = duration;
            }

            long position = getPosition();
            onEvent(PlayerType.KernelType.EXO_V2, seek < position ? PlayerType.EventType.SEEK_START_REWIND : PlayerType.EventType.SEEK_START_FORWARD);
            mSeeking = true;
            mExoPlayer.seekTo(seek);
            LogUtil.log("VideoExo2Player => seekTo =>");
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => seekTo => " + e.getMessage());
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        try {
            if (!isPrepared())
                throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            long currentPosition = mExoPlayer.getCurrentPosition();
            if (currentPosition < 0)
                throw new Exception("currentPosition warning: " + currentPosition);
            return currentPosition;
        } catch (Exception e) {
//            MPLogUtil.log("VideoExo2Player => getPosition => " + e.getMessage());
            return 0L;
        }
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        try {
            if (!isPrepared())
                throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            long duration = mExoPlayer.getDuration();
            if (duration <= 0)
                throw new Exception("duration warning: " + duration);
            return duration;
        } catch (Exception e) {
//            MPLogUtil.log("VideoExo2Player => getDuration => " + e.getMessage());
            return 0L;
        }
    }

    @Override
    public boolean setSpeed(float speed) {
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
            LogUtil.log("VideoExo2Player => setSpeed => " + e.getMessage());
            return false;
        }
    }

    /**
     * 设置播放速度
     */


    @Override
    public void setVolume(float v1, float v2) {
        try {
            float value;
            boolean mute = isMute();
            LogUtil.log("VideoExo2Player => setVolume => mute = " + mute);
            if (mute) {
                value = 0F;
            } else {
                value = Math.max(v1, v2);
                if (value > 1f) {
                    value = 1f;
                }
            }
            LogUtil.log("VideoExo2Player => setVolume => value = " + value);
            mExoPlayer.setVolume(value);
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => setVolume => " + e.getMessage());
        }
    }

    @Override
    public void registListener() {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.addAnalyticsListener(mAnalyticsListener);
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => registListener => Exception " + e.getMessage());
        }
    }

    @Override
    public void unRegistListener() {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.removeAnalyticsListener(mAnalyticsListener);
            mExoPlayer.setPlaybackParameters(null);
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => unRegistListener => Exception " + e.getMessage());
        }
    }

    @Override
    public void release() {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.setVideoSurface(null);
            mExoPlayer.release();
            mExoPlayer = null;
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => release => " + e.getMessage());
        }
    }

    @Override
    public boolean isBuffering() {
        return isBuffering;
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        LogUtil.log("VideoExo2Player => start");
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.play();
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => start => " + e.getMessage());
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
            if (!isPrepared())
                throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer)
                throw new Exception("mMediaPlayer error: null");
            mExoPlayer.pause();
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => pause => " + e.getMessage());
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        clear();
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.stop();
//            mExoPlayer.reset();
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => stop => " + e.getMessage());
        }
    }

    /************************/

    private MediaSource buildSource(Context context, StartArgs args) throws Exception {

        try {
            String url = args.getUrl();

            int contentType;
            String lowerCase = url.toLowerCase();
            // dash
            if (lowerCase.endsWith(PlayerType.SchemeType._MPD)) {
                contentType = C.CONTENT_TYPE_DASH;
            }
            // hls
            else if (lowerCase.endsWith(PlayerType.SchemeType._M3U) || lowerCase.endsWith(PlayerType.SchemeType._M3U8)) {
                contentType = C.CONTENT_TYPE_HLS;
            }
            // SmoothStreaming
            else if (lowerCase.matches(PlayerType.SchemeType._MATCHES)) {
                contentType = C.CONTENT_TYPE_SS;
            }
            // rtmp
            else if (lowerCase.startsWith(PlayerType.SchemeType.RTMP)) {
                contentType = -100;
            }
            // mp4
            else if (lowerCase.startsWith(PlayerType.SchemeType._MP4)) {
                contentType = -200;
            }
            // rtsp
            else if (lowerCase.startsWith(PlayerType.SchemeType.RTSP)) {
                contentType = C.CONTENT_TYPE_RTSP;
            }
            // other
            else {
                contentType = C.CONTENT_TYPE_OTHER;
            }

            // 2
            MediaItem.Builder builder = new MediaItem.Builder();
            builder.setUri(Uri.parse(url));


            DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                    .setUserAgent(ExoPlayerLibraryInfo.VERSION_SLASHY)
                    .setConnectTimeoutMs((int) args.getConnectTimout())
                    .setReadTimeoutMs((int) args.getConnectTimout())
                    .setAllowCrossProtocolRedirects(true)
                    .setKeepPostFor302Redirects(true);

            int cacheType = args.getCacheType();
            boolean live = args.isLive();
            if (live) {
                cacheType = PlayerType.CacheType.CLOSE;
            }

            // 关闭缓存
            DataSource.Factory dataSource;
            if (lowerCase.startsWith(PlayerType.SchemeType.FILE) || cacheType == PlayerType.CacheType.CLOSE) {
                dataSource = new DefaultDataSource.Factory(context, dataSourceFactory);
            }
            // 开启缓存
            else {


                if (null == mSimpleCache) {
                    @PlayerType.CacheLocalType.Value
                    int cacheLocalType = args.getCacheLocalType();
                    @PlayerType.CacheSizeType.Value
                    int cacheSizeType = args.getCacheSizeType();
                    String cacheDirName = args.getCacheDirName();

                    // 缓存大小
                    int size;
                    switch (cacheSizeType) {
                        case PlayerType.CacheSizeType._100:
                            size = 100;
                            break;
                        case PlayerType.CacheSizeType._200:
                            size = 200;
                            break;
                        case PlayerType.CacheSizeType._400:
                            size = 400;
                            break;
                        case PlayerType.CacheSizeType._800:
                            size = 800;
                            break;
                        default:
                            size = 1024;
                            break;
                    }


                    // 缓存位置
                    File dir;
                    if (cacheLocalType == PlayerType.CacheLocalType.EXTERNAL) {
                        File externalCacheDir = context.getExternalCacheDir();
                        dir = new File(externalCacheDir, cacheDirName);
                    } else {
                        File cacheDir = context.getCacheDir();
                        dir = new File(cacheDir, cacheDirName);
                    }

                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    mSimpleCache = new SimpleCache(dir,
                            //
                            new LeastRecentlyUsedCacheEvictor(size * 1024 * 1024),
                            //
                            new StandaloneDatabaseProvider(context)
                    );
                }

                dataSource = new CacheDataSource.Factory()
                        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                        .setCache(mSimpleCache)
                        // 网络请求工厂
                        .setUpstreamDataSourceFactory(dataSourceFactory)
                        // 缓存读取工厂
                        .setCacheReadDataSourceFactory(new FileDataSource.Factory())
                        // 写入数据到缓存
                        .setCacheWriteDataSinkFactory(new CacheDataSink.Factory()
                                .setFragmentSize(CacheDataSink.DEFAULT_FRAGMENT_SIZE)
                                .setCache(mSimpleCache))
                        // 自定义缓存键
                        .setCacheKeyFactory(new CacheKeyFactory() {
                            @Override
                            public String buildCacheKey(DataSpec dataSpec) {
                                String subUrl = dataSpec.uri.toString();
                                if (subUrl.endsWith(".m3u8")) {
                                    int i = subUrl.lastIndexOf("/");
                                    String playlistName = subUrl.substring(0, i);
                                    int hashCode = playlistName.hashCode();
                                    LogUtil.log("VideoExo2Player => buildSource => buildCacheKey m3u8 => position = " + dataSpec.position + ", absoluteStreamPosition = " + dataSpec.absoluteStreamPosition + ", length = " + dataSpec.length + ", hashCode = " + hashCode + ", playlistName = " + playlistName);
                                    return "hls_playlist_" + hashCode;
                                } else if (subUrl.endsWith(".ts")) {
                                    int i = subUrl.lastIndexOf("/");
                                    String playlistName = subUrl.substring(0, i);
                                    int playlistCode = playlistName.hashCode();
                                    String segmentName = subUrl.substring(i + 1);
                                    int segmentCode = segmentName.hashCode();
                                    LogUtil.log("VideoExo2Player => buildSource => buildCacheKey ts => position = " + dataSpec.position + ", absoluteStreamPosition = " + dataSpec.absoluteStreamPosition + ", length = " + dataSpec.length + ", segmentCode = " + segmentCode + ", segmentName = " + segmentName);
                                    return "hls_segment_" + playlistCode + "_" + segmentCode;
                                } else {
                                    return url;
                                }
                            }
                        })
                        // 监听
                        .setEventListener(new CacheDataSource.EventListener() {
                            @Override
                            public void onCachedBytesRead(long l, long l1) {
                            }

                            @Override
                            public void onCacheIgnored(int i) {
                            }
                        });
            }

            //
            List<MediaSource> mediaSources = new LinkedList<>();

            // rtmp
            if (contentType == -100) {
                Class<?> cls = Class.forName("com.google.android.exoplayer2.ext.rtmp.RtmpDataSource");
                DataSource.Factory factory = (DataSource.Factory) cls.newInstance();
                LogUtil.log("VideoExo2Player => buildSource => rtmp");
                ProgressiveMediaSource source = new ProgressiveMediaSource.Factory(factory).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .build());
                mediaSources.add(source);
            }
            // rtsp
            else if (contentType == C.CONTENT_TYPE_RTSP) {
                Class<?> cls = Class.forName("com.google.android.exoplayer2.source.rtsp.RtspMediaSource$Factory");
                Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                constructor.setAccessible(true);
                Object object = constructor.newInstance(dataSource);
                LogUtil.log("VideoExo2Player => buildSource => rtsp");
                MediaSource source = ((MediaSource.Factory) object).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .build());
                mediaSources.add(source);
            }
            // dash
            else if (contentType == C.CONTENT_TYPE_DASH) {
                Class<?> cls = Class.forName("com.google.android.exoplayer2.source.dash.DashMediaSource$Factory");
                Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                constructor.setAccessible(true);
                Object object = constructor.newInstance(dataSource);
                LogUtil.log("VideoExo2Player => buildSource => dash");
                MediaSource source = ((MediaSource.Factory) object).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .build());
                mediaSources.add(source);
            }
            // hls
            else if (contentType == C.CONTENT_TYPE_HLS) {
                Class<?> cls = Class.forName("com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory");
                Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                constructor.setAccessible(true);
                Object object = constructor.newInstance(dataSource);
                LogUtil.log("VideoExo2Player => buildSource => hls");

                MediaSource source = ((MediaSource.Factory) object)
                        .createMediaSource(new MediaItem.Builder()
                                .setUri(Uri.parse(url))
                                .build());
                mediaSources.add(source);
            }
            // SmoothStreaming
            else if (contentType == C.CONTENT_TYPE_SS) {
                Class<?> cls = Class.forName("exoplayer.smoothstreaming.SsMediaSource$Factory");
                Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                constructor.setAccessible(true);
                Object object = constructor.newInstance(dataSource);
                LogUtil.log("VideoExo2Player => buildSource => SmoothStreaming");
                MediaSource source = ((MediaSource.Factory) object).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .build());
                mediaSources.add(source);
            }
            // mp4
            else if (contentType == -200) {
                LogUtil.log("VideoExo2Player => buildSource => mp4");
                ProgressiveMediaSource source = new ProgressiveMediaSource.Factory(dataSource).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .build());
                mediaSources.add(source);
            }
            // other
            else {
                LogUtil.log("VideoExo2Player => buildSource => other");
                MediaSource source = new DefaultMediaSourceFactory(dataSource).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .build());
                mediaSources.add(source);
            }

            // 外挂字幕轨道
            List<TrackArgs> extraTrackSubtitle = args.getExtraTrackSubtitle();
            if (null != extraTrackSubtitle) {
                for (TrackArgs track : extraTrackSubtitle) {
                    int roleFlags = track.getRoleFlags();
                    if (roleFlags == -1)
                        continue;
                    String sutitleUrl = track.getUrl();
                    if (null == sutitleUrl)
                        continue;
                    if (sutitleUrl.isEmpty())
                        continue;
                    String mimeType = track.getMimeType();
                    if (null == mimeType)
                        continue;
                    if (mimeType.isEmpty())
                        continue;
                    String language = track.getLanguage();
                    if (null == language)
                        continue;
                    if (language.isEmpty())
                        continue;
                    MediaItem.SubtitleConfiguration subtitleConfig = new MediaItem.SubtitleConfiguration.Builder(Uri.parse(sutitleUrl))
                            .setSelectionFlags(C.SELECTION_FLAG_AUTOSELECT)
                            .setMimeType(mimeType) // 也可以用 MimeTypes.APPLICATION_SUBRIP
                            .setLanguage(language)
                            .setRoleFlags(roleFlags)
                            .setLabel(track.getLabel())
                            .setId(track.getId())
                            .setSelectionFlags(track.getSelectionFlags())
                            .build();
                    SingleSampleMediaSource source = new SingleSampleMediaSource.Factory(dataSource)
                            .createMediaSource(subtitleConfig, C.TIME_UNSET);
                    //
                    mediaSources.add(source);
                }
            }

            // 外挂音频轨道
            List<TrackArgs> extraTrackAudio = args.getExtraTrackAudio();
            if (null != extraTrackAudio) {
                for (TrackArgs track : extraTrackAudio) {
                    MediaSource source = new DefaultMediaSourceFactory(dataSourceFactory)
                            .createMediaSource(new MediaItem.Builder()
                                    .setUri(track.getUrl())
//                                    .setMimeType(track.getMimeType())
//                                    .setMediaId(track.getLabel())
                                    .build());
                    //
                    mediaSources.add(source);
                }
            }

            // 外挂视频轨道
            List<TrackArgs> extraTrackVideo = args.getExtraTrackVideo();
            if (null != extraTrackVideo) {
                for (TrackArgs track : extraTrackVideo) {
                    MediaSource source = new DefaultMediaSourceFactory(dataSourceFactory)
                            .createMediaSource(new MediaItem.Builder()
                                    .setUri(track.getUrl())
//                                    .setMimeType(track.getMimeType())
//                                    .setMediaId(track.getLabel())
                                    .build());
                    //
                    mediaSources.add(source);
                }
            }

            return new MergingMediaSource(mediaSources.toArray(new MediaSource[0]));


        } catch (Exception e) {
            throw e;
        }
    }

    private final AnalyticsListener mAnalyticsListener = new AnalyticsListener() {

        @Override
        public void onTimelineChanged(EventTime eventTime, int i) {
            Object manifest = mExoPlayer.getCurrentManifest();
            LogUtil.log("VideoExo2Player => onTimelineChanged => manifest = " + manifest);
            if (manifest instanceof HlsManifest) {
                mHlsManifest = (HlsManifest) manifest;
            }
        }

        @Override
        public void onPlayerErrorChanged(EventTime eventTime, PlaybackException error) {
            LogUtil.log("VideoExo2Player => onPlayerErrorChanged => message = " + error.getMessage(), error);
        }

        @Override
        public void onPlaybackStateChanged(AnalyticsListener.EventTime eventTime, int state) {
            // 播放错误
            if (state == Player.STATE_IDLE) {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_IDLE] = " + state);
            }
            // 播放完成
            else if (state == Player.STATE_ENDED) {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_ENDED] = " + state);
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.COMPLETE);
            }
            // 播放开始
            else if (state == Player.STATE_READY) {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_READY] = " + state);
                try {
                    if (!isPrepared())
                        throw new Exception("warning: isPrepared false");

                    // buffering
                    if (isBuffering) {
                        LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_READY] -> buffering");
                        isBuffering = false;
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.BUFFERING_STOP);
                    }
                    // seeking
                    else if (mSeeking) {
                        LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_READY] -> seeking");
                        mSeeking = false;
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.SEEK_FINISH);

                        // 起播快进
                        if (mPlayWhenReadySeeking) {
                            mPlayWhenReadySeeking = false;
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.START);
                            // 立即播放
                            boolean playWhenReady = isPlayWhenReady();
                            onEvent(PlayerType.KernelType.EXO_V2, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                            if (playWhenReady) {
                                boolean playing = isPlaying();
                                if (playing)
                                    throw new Exception("warning: isPlaying true");
                                start();
                            } else {
                                pause();
                                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.PAUSE);
                            }
                        }
                        // 正常快进&快退
                        else {

                        }
                    }
                    // start ready
                    else {
                        LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_READY] -> start ready ");

                        boolean playWhenReady = isPlayWhenReady();
                        LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_READY] -> playWhenReady = " + playWhenReady);
                        onEvent(PlayerType.KernelType.EXO_V2, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                        if (playWhenReady) {
                            boolean playing = isPlaying();
                            if (playing)
                                throw new Exception("warning: isPlaying true");
                            start();
                        } else {
                            pause();
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.PAUSE);
                        }
                    }

                } catch (Exception e) {
                    LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_READY] -> Exception " + e.getMessage());
                }
            }
            // 播放缓冲
            else if (state == Player.STATE_BUFFERING) {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_BUFFERING] = " + state);
                try {
                    if (!isPrepared())
                        throw new Exception("mPrepared warning: false");
                    isBuffering = true;
                    onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.BUFFERING_START);
                } catch (Exception e) {
                    LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_BUFFERING] -> Exception " + state);
                }
            }
            // ????
            else {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[????] = " + state);
            }
        }

        @Override
        public void onVideoInputFormatChanged(EventTime eventTime, Format format, DecoderReuseEvaluation decoderReuseEvaluation) {
            LogUtil.log("VideoExo2Player => onVideoInputFormatChanged[出画面] =>");

            // 视频信息
            try {
                StartArgs args = getStartArgs();
                if (null == args)
                    throw new Exception("error: args null");
                @PlayerType.ScaleType.Value
                int scaleType = args.getscaleType();
                int rotation = args.getRotation();
//                int rotation = (videoSize.unappliedRotationDegrees > 0 ? videoSize.unappliedRotationDegrees : PlayerType.RotationType.DEFAULT);
                onVideoFormatChanged(PlayerType.KernelType.EXO_V2, rotation, scaleType, format.width, format.height, format.bitrate);
            } catch (Exception e) {
                LogUtil.log("VideoExo2Player => onVideoInputFormatChanged => " + e.getMessage());
            }

            // 起播快进??
            try {
                if (isPrepared())
                    throw new Exception("warning: isPrepared true");
                setPrepared(true);
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.PREPARE);
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.VIDEO_RENDERING_START);
                long seek = getPlayWhenReadySeekToPosition();
                // 立即播放
                if (seek <= 0L) {
                    onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.START);
                }
                // 起播快进
                else {
                    onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.SEEK_START_FORWARD);
                    mPlayWhenReadySeeking = true;
                    seekTo(seek);
                }
            } catch (Exception e) {
                LogUtil.log("VideoExo2Player => onVideoInputFormatChanged => Exception " + e.getMessage());
            }
        }

        @Override
        public void onPlayerError(AnalyticsListener.EventTime eventTime, PlaybackException error) {
            LogUtil.log("VideoExo2Player => onPlayerError => error = " + error.getMessage());
            try {
                if (null == error)
                    throw new Exception("warning: null == error");
                if (!(error instanceof ExoPlaybackException))
                    throw new Exception("warning: error not instanceof ExoPlaybackException");
                stop();
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.STOP);
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.ERROR);
            } catch (Exception e) {
                LogUtil.log("VideoExo2Player => onPlayerError => " + e.getMessage());
            }
        }

        @Override
        public void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, VideoSize videoSize) {
        }

        @Override
        public void onLoadStarted(AnalyticsListener.EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            LogUtil.log("VideoExo2Player => onLoadStarted =>");
        }

        @Override
        public void onLoadCompleted(AnalyticsListener.EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            LogUtil.log("VideoExo2Player => onLoadCompleted =>");
        }

        @Override
        public void onLoadCanceled(AnalyticsListener.EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            LogUtil.log("VideoExo2Player => onLoadCanceled =>");
        }

        @Override
        public void onLoadError(AnalyticsListener.EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
            LogUtil.log("VideoExo2Player => onLoadError =>");
        }

        @Override
        public void onCues(EventTime eventTime, CueGroup cueGroup) {
            LogUtil.log("VideoExo2Player => onCues => cueGroup = " + cueGroup);
        }

        @Override
        public void onCues(EventTime eventTime, List<Cue> cues) {
            LogUtil.log("VideoExo2Player => onCues => cuesLength = " + cues.size());

            //
            String language = null;
            try {
                Tracks tracks = mExoPlayer.getCurrentTracks();
                ImmutableList<Tracks.Group> groups = tracks.getGroups();
                int groupCount = groups.size();
                for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
                    Tracks.Group group = groups.get(groupIndex);
                    if (null == group)
                        continue;

                    boolean isGroupSelected = group.isSelected();
                    if (!isGroupSelected)
                        continue;
                    int trackCount = group.length;
                    for (int trackIndex = 0; trackIndex < trackCount; trackIndex++) {
                        //
                        Format format = group.getTrackFormat(trackIndex);
                        // 字幕轨道
                        int trackType = group.getType();
                        if (trackType == C.TRACK_TYPE_TEXT) {
                            // 轨道是否被选中
                            boolean isTrackSelected = group.isTrackSelected(trackIndex);
                            if (isTrackSelected) {
                                language = format.language;
                                break;
                            }
                        }
                    }
                }
                if (null == language)
                    throw new Exception();
            } catch (Exception e) {
                language = "null";
            }

            try {
                if (null == cues)
                    throw new Exception();
                if (cues.size() == 0)
                    throw new Exception();
                for (Cue cue : cues) {
                    if (null != cue.text && cue.text.length() > 0) {
                        onUpdateSubtitle(PlayerType.KernelType.EXO_V2, language, cue.text);
                    }
                }
            } catch (Exception e) {
                onUpdateSubtitle(PlayerType.KernelType.EXO_V2, language, null);
            }
        }
    };

//    @Override
//    public boolean toggleTrackLanguageSubtitle(String language) {
//        try {
//            if (null == language)
//                throw new Exception("warning: language null");
//            if (language.isEmpty())
//                throw new Exception("warning: language empty");
//            if (null == mExoPlayer)
//                throw new Exception("error: mExoPlayer null");
//            TrackSelector trackSelector = mExoPlayer.getTrackSelector();
//            TrackSelectionParameters selectionParameters = trackSelector.getParameters()
//                    .buildUpon()
//                    .setPreferredTextLanguage(language)
//                    .build();
//            trackSelector.setParameters(selectionParameters);
//            return true;
//        } catch (Exception e) {
//            LogUtil.log("VideoExo2Player => toggleTrackLanguageSubtitle => " + e.getMessage());
//            return false;
//        }
//    }
//
//    @Override
//    public boolean toggleTrackLanguageAudio(String language) {
//        try {
//            if (null == language)
//                throw new Exception("warning: language null");
//            if (language.isEmpty())
//                throw new Exception("warning: language empty");
//            if (null == mExoPlayer)
//                throw new Exception("error: mExoPlayer null");
//            TrackSelector trackSelector = mExoPlayer.getTrackSelector();
//            TrackSelectionParameters selectionParameters = trackSelector.getParameters()
//                    .buildUpon()
//                    .setPreferredAudioLanguage(language)
//                    .build();
//            trackSelector.setParameters(selectionParameters);
//            return true;
//        } catch (Exception e) {
//            LogUtil.log("VideoExo2Player => toggleTrackLanguageAudio => " + e.getMessage());
//            return false;
//        }
//    }
//
//    @Override
//    public boolean toggleTrackRoleFlagSubtitle(int roleFlag) {
//        try {
//            if (roleFlag == -1)
//                throw new Exception("error: roleFlag == -1");
//            if (null == mExoPlayer)
//                throw new Exception("error: mExoPlayer null");
//            TrackSelector trackSelector = mExoPlayer.getTrackSelector();
//            TrackSelectionParameters selectionParameters = trackSelector.getParameters()
//                    .buildUpon()
//                    .setPreferredTextRoleFlags(roleFlag)
//                    .build();
//            trackSelector.setParameters(selectionParameters);
//            return true;
//        } catch (Exception e) {
//            LogUtil.log("VideoExo2Player => toggleTrackRoleFlagSubtitle => " + e.getMessage());
//            return false;
//        }
//    }
//
//    @Override
//    public boolean toggleTrackRoleFlagAudio(int roleFlag) {
//        try {
//            if (roleFlag == -1)
//                throw new Exception("error: roleFlag == -1");
//            if (null == mExoPlayer)
//                throw new Exception("error: mExoPlayer null");
//            TrackSelector trackSelector = mExoPlayer.getTrackSelector();
//            TrackSelectionParameters selectionParameters = trackSelector.getParameters()
//                    .buildUpon()
//                    .setPreferredAudioRoleFlags(roleFlag)
//                    .build();
//            trackSelector.setParameters(selectionParameters);
//            return true;
//        } catch (Exception e) {
//            LogUtil.log("VideoExo2Player => toggleTrackRoleFlagAudio => " + e.getMessage());
//            return false;
//        }
//    }
//
//    @Override
//    public boolean toggleTrackRoleFlagVideo(int roleFlag) {
//        try {
//            if (roleFlag == -1)
//                throw new Exception("error: roleFlag == -1");
//            if (null == mExoPlayer)
//                throw new Exception("error: mExoPlayer null");
//            TrackSelector trackSelector = mExoPlayer.getTrackSelector();
//            TrackSelectionParameters selectionParameters = trackSelector.getParameters()
//                    .buildUpon()
//                    .setPreferredVideoRoleFlags(roleFlag)
//                    .build();
//            trackSelector.setParameters(selectionParameters);
//            return true;
//        } catch (Exception e) {
//            LogUtil.log("VideoExo2Player => toggleTrackRoleFlagVideo => " + e.getMessage());
//            return false;
//        }
//    }

    @Override
    public boolean toggleTrack(TrackArgs trackArgs) {
        try {
            if (null == trackArgs)
                throw new Exception("error: trackArgs null");
            int groupIndex = trackArgs.getGroupIndex();
            if (groupIndex == -1)
                throw new Exception("error: groupIndex == -1");
            int trackIndex = trackArgs.getTrackIndex();
            if (trackIndex == -1)
                throw new Exception("error: trackIndex == -1");
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            Tracks tracks = mExoPlayer.getCurrentTracks();
            ImmutableList<Tracks.Group> tracksGroups = tracks.getGroups();
            TrackGroup trackGroup = tracksGroups.get(groupIndex).getMediaTrackGroup();

            TrackSelector trackSelector = mExoPlayer.getTrackSelector();
            TrackSelectionParameters selectionParameters = trackSelector.getParameters()
                    .buildUpon()
                    .setOverrideForType(new TrackSelectionOverride(trackGroup, trackIndex))
                    .build();
            trackSelector.setParameters(selectionParameters);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => toggleTrack => " + e.getMessage());
            return false;
        }
    }

    public List<TrackArgs> getTrackInfo(int type) {

        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");

            //
            LinkedList<TrackArgs> list = new LinkedList<>();

            //
            Tracks tracks = mExoPlayer.getCurrentTracks();
            com.google.common.collect.ImmutableList<Tracks.Group> groups = tracks.getGroups();
            int groupCount = groups.size();
            for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
                Tracks.Group group = groups.get(groupIndex);
                if (null == group)
                    continue;

                int trackType = group.getType();
                int trackCount = group.length;
                // 是否支持自适应播放
                boolean isGroupAdaptiveSupported = group.isAdaptiveSupported();
                boolean isGroupSelected = group.isSelected();
                boolean isGroupSupported = group.isSupported();
                for (int trackIndex = 0; trackIndex < trackCount; trackIndex++) {
                    //
                    Format format = group.getTrackFormat(trackIndex);
                    // 轨道是否支持
                    boolean isTrackSupported = group.isTrackSupported(trackIndex);
                    // 轨道是否被选中
                    boolean isTrackSelected = group.isTrackSelected(trackIndex);

                    TrackArgs trackArgs = new TrackArgs();

                    // 视频轨道
                    if (type == 1 && trackType == C.TRACK_TYPE_VIDEO) {


                        trackArgs.setBitrate(format.bitrate);
                        trackArgs.setWidth(format.width);
                        trackArgs.setHeight(format.height);

//                        object.put("frameRate", format.frameRate);
//                        object.put("rotationDegrees", format.rotationDegrees);
//                        object.put("pixelWidthHeightRatio", format.pixelWidthHeightRatio);
//                        object.put("projectionData", format.projectionData);
//                        object.put("stereoMode", format.stereoMode);
//                        object.put("colorInfo", format.colorInfo);
//                        object.put("maxSubLayers", format.maxSubLayers);
                    }
                    // 音频轨道
                    else if (type == 2 && trackType == C.TRACK_TYPE_AUDIO) {
//                        object.put("channelCount", format.channelCount);
//                        object.put("sampleRate", format.sampleRate);
//                        object.put("pcmEncoding", format.pcmEncoding);
//                        object.put("encoderDelay", format.encoderDelay);
//                        object.put("encoderPadding", format.encoderPadding);
                    }
                    // 字幕轨道
                    else if (type == 3 && trackType == C.TRACK_TYPE_TEXT) {
//                        object.put("accessibilityChannel", format.accessibilityChannel);
                        //  object.put("cueReplacementBehavior", format.cueReplacementBehavior);
                    }
                    // 媒体信息
                    else if (type == 4 && trackType == C.TRACK_TYPE_METADATA) {
                    }
                    // 视频轨道
                    else if (type == -1 && trackType == C.TRACK_TYPE_VIDEO) {

                        trackArgs.setBitrate(format.bitrate);
                        trackArgs.setWidth(format.width);
                        trackArgs.setHeight(format.height);

//                        object.put("frameRate", format.frameRate);
//                        object.put("rotationDegrees", format.rotationDegrees);
//                        object.put("pixelWidthHeightRatio", format.pixelWidthHeightRatio);
//                        object.put("projectionData", format.projectionData);
//                        object.put("stereoMode", format.stereoMode);
//                        object.put("colorInfo", format.colorInfo);
//                        object.put("maxSubLayers", format.maxSubLayers);
                    }
                    // 音频轨道
                    else if (type == -1 && trackType == C.TRACK_TYPE_AUDIO) {
//                        object.put("channelCount", format.channelCount);
//                        object.put("sampleRate", format.sampleRate);
//                        object.put("pcmEncoding", format.pcmEncoding);
//                        object.put("encoderDelay", format.encoderDelay);
//                        object.put("encoderPadding", format.encoderPadding);
                    }
                    // 字幕轨道
                    else if (type == -1 && trackType == C.TRACK_TYPE_TEXT) {
//                        object.put("accessibilityChannel", format.accessibilityChannel);
                        //   object.put("cueReplacementBehavior", format.cueReplacementBehavior);
                    }
                    // 媒体信息
                    else if (type == -1 && trackType == C.TRACK_TYPE_METADATA) {
                        // LogUtil.log("VideoExo2Player => getTrackInfo[C.TRACK_TYPE_METADATA] => groupCount = " + groupCount + ", groupIndex = " + groupIndex + ", trackCount = " + trackCount + ", trackIndex = " + trackIndex + ", trackType = " + trackType + ", isGroupAdaptiveSupported = " + isGroupAdaptiveSupported + ", isGroupSelected = " + isGroupSelected + ", isGroupSupported = " + isGroupSupported + ", isTrackSelected = " + isTrackSelected + ", isTrackSupported = " + isTrackSupported);
                        continue;
                    }
                    // 未知
                    else {
                        //  LogUtil.log("VideoExo2Player => getTrackInfo[Unknow] => groupCount = " + groupCount + ", groupIndex = " + groupIndex + ", trackCount = " + trackCount + ", trackIndex = " + trackIndex + ", trackType = " + trackType + ", isGroupAdaptiveSupported = " + isGroupAdaptiveSupported + ", isGroupSelected = " + isGroupSelected + ", isGroupSupported = " + isGroupSupported + ", isTrackSelected = " + isTrackSelected + ", isTrackSupported = " + isTrackSupported);
                        continue;
                    }


                    trackArgs.setGroupCount(groupCount);
                    trackArgs.setGroupIndex(groupIndex);
                    trackArgs.setTrackCount(trackCount);
                    trackArgs.setTrackIndex(trackIndex);
                    trackArgs.setTrackType(trackType);

                    trackArgs.setGroupAdaptiveSupported(isGroupAdaptiveSupported);
                    trackArgs.setGroupSupported(isGroupSupported);
                    trackArgs.setGroupSelected(isGroupSelected);
                    trackArgs.setTrackSupported(isTrackSupported);

                    // 自适应码率
                    if (isGroupAdaptiveSupported && trackType == androidx.media3.common.C.TRACK_TYPE_VIDEO) {
                        int videoWidth = getPlayerApi().getVideoRender().getVideoWidth();
                        int videoHeight = getPlayerApi().getVideoRender().getVideoHeight();
                        int videoBitrate = getPlayerApi().getVideoRender().getVideoBitrate();
                        boolean selected = (videoWidth == format.width && videoHeight == format.height && videoBitrate == format.bitrate);
                        if (selected) {
                            trackArgs.setTrackSelected(true);
                        } else {
                            trackArgs.setTrackSelected(false);
                        }
                    } else {
                        trackArgs.setTrackSelected(isTrackSelected);
                    }


                    trackArgs.setId(format.id);
                    trackArgs.setLabel(format.label);

//                    object.put("labels", format.labels);


                    trackArgs.setLanguage(format.language);
                    trackArgs.setRoleFlags(format.roleFlags);
                    trackArgs.setSelectionFlags(format.selectionFlags);
                    trackArgs.setSampleMimeType(format.sampleMimeType);
//                    object.put("averageBitrate", format.averageBitrate);
//                    object.put("peakBitrate", format.peakBitrate);
//                    object.put("codecs", format.codecs);
//                    object.put("metadata", format.metadata);
////                    object.put("customData", format.customData);
//                    // Container specific.
//                    object.put("containerMimeType", format.containerMimeType);
//                    object.put("maxInputSize", format.maxInputSize);
////                    object.put("maxNumReorderSamples", format.maxNumReorderSamples);
//                    object.put("initializationData", format.initializationData);
//                    object.put("drmInitData", format.drmInitData);
//                    object.put("subsampleOffsetUs", format.subsampleOffsetUs);
////                    object.put("hasPrerollSamples", format.hasPrerollSamples);

                    //   LogUtil.log("VideoExo2Player => getTrackInfo => groupCount = " + groupCount + ", groupIndex = " + groupIndex + ", trackCount = " + trackCount + ", trackIndex = " + trackIndex + ", trackType = " + trackType + ", isGroupAdaptiveSupported = " + isGroupAdaptiveSupported + ", isGroupSelected = " + isGroupSelected + ", isGroupSupported = " + isGroupSupported + ", isTrackSelected = " + isTrackSelected + ", isTrackSupported = " + isTrackSupported + ", isTrackMixed = " + isTrackMixed + ", isTrackMixedSelected = " + isTrackMixedSelected + ", format = " + object);

                    //
                    list.add(trackArgs);
                }
            }

            //
            if (list.isEmpty())
                throw new Exception("error: list empty");

            //   LogUtil.log("VideoExo2Player => getTrackInfo => type = " + type + ", list = " + list);
            return list;
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => getTrackInfo => Exception " + e.getMessage());
            return null;
        }
    }

    @Override
    public void testHls() {
        try {
            HlsMediaPlaylist mediaPlaylist = mHlsManifest.mediaPlaylist;
            String url = mediaPlaylist.baseUri;
            int lastIndexOf = url.lastIndexOf("/");
            String baseUrl = url.substring(0, lastIndexOf);
            LogUtil.log("VideoExo2Player => testHls => baseUrl = " + baseUrl + ", url = " + url);

            int playlistCode = baseUrl.hashCode();
//                String key = "hls_playlist_" + hashCode;
//                NavigableSet<CacheSpan> cachedSpans = mSimpleCache.getCachedSpans(key);
//                for (CacheSpan cacheSpan : cachedSpans) {
//                    LogUtil.log("VideoExo2Player => onTimelineChanged => baseUrl = " + baseUrl + ", url = " + url + ", isCached = " + cacheSpan.isCached);
//                }


            for (HlsMediaPlaylist.Segment segment : mediaPlaylist.segments) {

                String segmentUrl = baseUrl + "/" + segment.url;
                long relativeStartTimeUs = segment.relativeStartTimeUs;
                long durationUs = segment.durationUs;

                int segmentCode = segment.url.hashCode();
                String key = "hls_segment_" + playlistCode + "_" + segmentCode;
                NavigableSet<CacheSpan> cachedSpans = mSimpleCache.getCachedSpans(key);
                for (CacheSpan cacheSpan : cachedSpans) {
                    LogUtil.log("VideoExo2Player => testHls => relativeStartTimeUs = " + relativeStartTimeUs + ", durationUs = " + durationUs + ", segmentUrl = " + segmentUrl + ", isCached = " + cacheSpan.isCached + ", cachedSpans = " + cachedSpans.size() + ", key = " + key);
                }
            }
        } catch (Exception e) {
        }
    }
}
