package lib.kalu.mediaplayer.core.kernel.video.exo2;

import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.view.Surface;

import androidx.annotation.Nullable;

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
import com.google.android.exoplayer2.decoder.DecoderReuseEvaluation;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.CueGroup;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.video.VideoSize;
import com.google.common.collect.ImmutableList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.args.TrackArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;


public final class VideoExo2Player extends VideoBasePlayer {

    private boolean mSeeking = false;
    private boolean mBuffering = false;
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
                            .setInitialBitrateEstimate(5_000_000)
                            .build())
                    // 缓冲缓存
                    .setLoadControl(new DefaultLoadControl.Builder().setBufferDurationsMs(
                                    10_000, // 低带宽时最小缓冲10秒
                                    30_000, // 高带宽时最大缓冲30秒
                                    2500,
                                    5000
                            )
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
                            .setAllowAudioMixedMimeTypeAdaptiveness(false)
                            // 视频禁止混合 MIME 类型切换（如视频+音频单独切换）
                            .setAllowVideoMixedMimeTypeAdaptiveness(true)
                            // 音频禁止非无缝切换
                            // .setAllowAudioNonSeamlessAdaptiveness(false)
                            // 视频禁止非无缝切换
                            .setAllowVideoNonSeamlessAdaptiveness(false)
                            // 音频混合声道数量的自适应性
                            .setAllowAudioMixedChannelCountAdaptiveness(true)
                            // 音频混合采样率自适应
                            .setAllowAudioMixedSampleRateAdaptiveness(true)
                            // 音频混合时解码器支持自适应
                            .setAllowAudioMixedDecoderSupportAdaptiveness(true)
                            // 音频混合时解码器支持自适应
                            .setAllowVideoMixedDecoderSupportAdaptiveness(true)
                            .build(),
                            new AdaptiveTrackSelection.Factory(
                                    3000,// 至少 3 秒后才允许升码率
                                    1000, // 最多 1 秒后允许降码率
                                    25000, 0.7F)));

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

            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.PREPARE_START);
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
                cacheType = PlayerType.CacheType.EXO_CLOSE;
            }

            // 关闭缓存
            DataSource.Factory dataSource;
            if (lowerCase.startsWith(PlayerType.SchemeType.FILE) || cacheType == PlayerType.CacheType.EXO_CLOSE) {
                dataSource = new DefaultDataSource.Factory(context, dataSourceFactory);
            }
            // 开启缓存
            else {
                @PlayerType.CacheLocalType.Value
                int cacheLocalType = args.getCacheLocalType();
                @PlayerType.CacheSizeType.Value
                int cacheSizeType = args.getCacheSizeType();
                String cacheDirName = args.getCacheDirName();

                dataSource = new CacheDataSource.Factory()
                        .setCache(VideoExo2PlayerSimpleCache.getSimpleCache(context, cacheLocalType, cacheSizeType, cacheDirName))
//                        .setCacheWriteDataSinkFactory(new CacheDataSink.Factory().setCache(simpleCache1).setFragmentSize(C.LENGTH_UNSET))
                        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                        .setUpstreamDataSourceFactory(dataSourceFactory);
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

                MediaSource source = ((MediaSource.Factory) object).createMediaSource(new MediaItem.Builder()
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

            // 外挂字幕
            List<TrackArgs> extraTrackSubtitle = args.getExtraTrackSubtitle();
            if (null != extraTrackSubtitle) {
                for (TrackArgs track : extraTrackSubtitle) {
                    Uri subtitleUri = Uri.parse(track.getUrl());
                    MediaItem.SubtitleConfiguration subtitleConfig = new MediaItem.SubtitleConfiguration.Builder(subtitleUri)
                            .setSelectionFlags(C.SELECTION_FLAG_AUTOSELECT)
                            .setMimeType(track.getMimeType()) // 也可以用 MimeTypes.APPLICATION_SUBRIP
                            .setLanguage(track.getLanguage())
                            .setLabel(track.getLabel())
                            .build();
                    SingleSampleMediaSource source = new SingleSampleMediaSource.Factory(dataSource)
                            .createMediaSource(subtitleConfig, C.TIME_UNSET);
                    //
                    mediaSources.add(source);
                }
            }

            // 外挂音轨
            List<TrackArgs> extraTrackAudio = args.getExtraTrackAudio();
            if (null != extraTrackAudio) {
                for (TrackArgs track : extraTrackAudio) {
                    MediaSource source = new DefaultMediaSourceFactory(dataSourceFactory)
                            .createMediaSource(new MediaItem.Builder()
                                    .setUri(track.getUrl())
                                    .setMimeType(track.getMimeType())
                                    .setMediaId(track.getLabel())
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
        public void onPlayerErrorChanged(EventTime eventTime, @Nullable PlaybackException error) {
            LogUtil.log("VideoExo2Player => onPlayerErrorChanged => message = " + error.getMessage(), error);
        }

        @Override
        public void onPlaybackStateChanged(AnalyticsListener.EventTime eventTime, int state) {
            // 播放错误
            if (state == Player.STATE_IDLE) {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged => STATE_IDLE =>");
            }
            // 播放完成
            else if (state == Player.STATE_ENDED) {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged => STATE_ENDED =>");
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.COMPLETE);
            }
            // 播放开始
            else if (state == Player.STATE_READY) {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged => STATE_READY =>");
                try {
                    if (!isPrepared())
                        throw new Exception("warning: isPrepared false");

                    if (mBuffering) {
                        mBuffering = false;
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.BUFFERING_STOP);
                    }

                    if (mSeeking) {
                        mSeeking = false;
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.SEEK_FINISH);

                        try {
                            long seek = getPlayWhenReadySeekToPosition();
                            if (seek <= 0L)
                                throw new Exception("warning: seek<=0");
                            boolean playWhenReadySeekFinish = isPlayWhenReadySeekFinish();
                            if (playWhenReadySeekFinish)
                                throw new Exception("warning: playWhenReadySeekFinish true");
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.START);
                            // 立即播放
                            boolean playWhenReady = isPlayWhenReady();
                            onEvent(PlayerType.KernelType.EXO_V2, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                            if (!playWhenReady) {
                                pause();
                                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.PAUSE);
                            }
                        } catch (Exception e) {
                            LogUtil.log("VideoExo2Player => onPlaybackStateChanged => STATE_READY => Exception1 " + e.getMessage());
                        }

                        try {
//                            boolean prepared = isPrepared();
//                            if (prepared)
//                                throw new Exception("warning: prepared true");
                            boolean playing = isPlaying();
                            if (playing)
                                throw new Exception("warning: playing true");
                            start();
                        } catch (Exception e) {
                            LogUtil.log("VideoExo2Player => onPlaybackStateChanged => STATE_READY => Exception2 " + e.getMessage());
                        }
                    }

                } catch (Exception e) {
                    LogUtil.log("VideoExo2Player => onPlaybackStateChanged => STATE_READY => Exception3 " + e.getMessage());
                }
            }
            // 播放缓冲
            else if (state == Player.STATE_BUFFERING) {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged => STATE_BUFFERING =>");
                try {
                    if (!isPrepared())
                        throw new Exception("mPrepared warning: false");
                    mBuffering = true;
                    onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.BUFFERING_START);
                } catch (Exception e) {
                    LogUtil.log("VideoExo2Player => onPlaybackStateChanged => STATE_BUFFERING => Exception " + e.getMessage());
                }
            }
            // UNKNOW
            else {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged => UNKNOW => state = " + state);
            }
        }

        @Override
        public void onVideoInputFormatChanged(EventTime eventTime, Format format, DecoderReuseEvaluation decoderReuseEvaluation) {
            LogUtil.log("VideoExo2Player => onVideoInputFormatChanged[出画面] =>");

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
                LogUtil.log("VideoExo2Player => onVideoSizeChanged => " + e.getMessage());
            }


            try {
                if (isPrepared())
                    throw new Exception("warning: isPrepared true");
                setPrepared(true);
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.PREPARE_COMPLETE);
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.VIDEO_RENDERING_START);
                long seek = getPlayWhenReadySeekToPosition();
                if (seek <= 0L) {
                    onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.START);
                    // 立即播放
                    boolean playWhenReady = isPlayWhenReady();
                    onEvent(PlayerType.KernelType.EXO_V2, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                    if (!playWhenReady) {
                        pause();
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.PAUSE);
                    }
                } else {
                    // 起播快进
                    onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.SEEK_START_FORWARD);
                    setPlayWhenReadySeekFinish(true);
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

    @Override
    public boolean setTrackSubtitle(String language) {
        try {
            if (null == language)
                throw new Exception("warning: language null");
            if (language.isEmpty())
                throw new Exception("warning: language empty");
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            TrackSelector trackSelector = mExoPlayer.getTrackSelector();
            TrackSelectionParameters selectionParameters = trackSelector.getParameters()
                    .buildUpon()
                    .setPreferredTextLanguage(language)
                    .build();
            trackSelector.setParameters(selectionParameters);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => setTrackSubtitle => " + e.getMessage());
            return false;
        }
    }

    @Override
    public JSONArray getTrackInfo(int type) {


        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");

            //
            JSONArray result = null;

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

                    JSONObject object = null;

                    // 视频轨道
                    if (type == 1 && trackType == C.TRACK_TYPE_VIDEO) {
                        if (null == object) {
                            object = new JSONObject();
                        }
                        object.put("bitrate", format.bitrate);
                        object.put("width", format.width);
                        object.put("height", format.height);
                        object.put("frameRate", format.frameRate);
                        object.put("rotationDegrees", format.rotationDegrees);
                        object.put("pixelWidthHeightRatio", format.pixelWidthHeightRatio);
                        object.put("projectionData", format.projectionData);
                        object.put("stereoMode", format.stereoMode);
                        object.put("colorInfo", format.colorInfo);
//                        object.put("maxSubLayers", format.maxSubLayers);
                    }
                    // 音频轨道
                    else if (type == 2 && trackType == C.TRACK_TYPE_AUDIO) {
                        if (null == object) {
                            object = new JSONObject();
                        }
                        object.put("channelCount", format.channelCount);
                        object.put("sampleRate", format.sampleRate);
                        object.put("pcmEncoding", format.pcmEncoding);
                        object.put("encoderDelay", format.encoderDelay);
                        object.put("encoderPadding", format.encoderPadding);
                    }
                    // 字幕轨道
                    else if (type == 3 && trackType == C.TRACK_TYPE_TEXT) {
                        if (null == object) {
                            object = new JSONObject();
                        }
                        object.put("accessibilityChannel", format.accessibilityChannel);
                        //  object.put("cueReplacementBehavior", format.cueReplacementBehavior);
                    }
                    // 媒体信息
                    else if (type == 4 && trackType == C.TRACK_TYPE_METADATA) {
                    }
                    // 视频轨道
                    else if (type == -1 && trackType == C.TRACK_TYPE_VIDEO) {
                        if (null == object) {
                            object = new JSONObject();
                        }
                        object.put("bitrate", format.bitrate);
                        object.put("width", format.width);
                        object.put("height", format.height);
                        object.put("frameRate", format.frameRate);
                        object.put("rotationDegrees", format.rotationDegrees);
                        object.put("pixelWidthHeightRatio", format.pixelWidthHeightRatio);
                        object.put("projectionData", format.projectionData);
                        object.put("stereoMode", format.stereoMode);
                        object.put("colorInfo", format.colorInfo);
//                        object.put("maxSubLayers", format.maxSubLayers);
                    }
                    // 音频轨道
                    else if (type == -1 && trackType == C.TRACK_TYPE_AUDIO) {
                        if (null == object) {
                            object = new JSONObject();
                        }
                        object.put("channelCount", format.channelCount);
                        object.put("sampleRate", format.sampleRate);
                        object.put("pcmEncoding", format.pcmEncoding);
                        object.put("encoderDelay", format.encoderDelay);
                        object.put("encoderPadding", format.encoderPadding);
                    }
                    // 字幕轨道
                    else if (type == -1 && trackType == C.TRACK_TYPE_TEXT) {
                        if (null == object) {
                            object = new JSONObject();
                        }
                        object.put("accessibilityChannel", format.accessibilityChannel);
                        //   object.put("cueReplacementBehavior", format.cueReplacementBehavior);
                    }
                    // 媒体信息
                    else if (type == -1 && trackType == C.TRACK_TYPE_METADATA) {
                        LogUtil.log("VideoExo2Player => getTrackInfo[C.TRACK_TYPE_METADATA] => groupCount = " + groupCount + ", groupIndex = " + groupIndex + ", trackCount = " + trackCount + ", trackIndex = " + trackIndex + ", trackType = " + trackType + ", isGroupAdaptiveSupported = " + isGroupAdaptiveSupported + ", isGroupSelected = " + isGroupSelected + ", isGroupSupported = " + isGroupSupported + ", isTrackSelected = " + isTrackSelected + ", isTrackSupported = " + isTrackSupported);
                    }
                    // 未知
                    else {
                        LogUtil.log("VideoExo2Player => getTrackInfo[Unknow] => groupCount = " + groupCount + ", groupIndex = " + groupIndex + ", trackCount = " + trackCount + ", trackIndex = " + trackIndex + ", trackType = " + trackType + ", isGroupAdaptiveSupported = " + isGroupAdaptiveSupported + ", isGroupSelected = " + isGroupSelected + ", isGroupSupported = " + isGroupSupported + ", isTrackSelected = " + isTrackSelected + ", isTrackSupported = " + isTrackSupported);
                    }


                    if (null == object)
                        continue;
                    object.put("groupCount", groupCount);
                    object.put("groupIndex", groupIndex);
                    object.put("trackCount", trackCount);
                    object.put("trackIndex", trackIndex);
                    object.put("trackType", trackType);
                    object.put("isGroupAdaptiveSupported", isGroupAdaptiveSupported);
                    object.put("isGroupSupported", isGroupSupported);
                    object.put("isGroupSelected", isGroupSelected);
                    object.put("isTrackSupported", isTrackSupported);
                    object.put("isTrackSelected", isTrackSelected);

                    boolean isTrackMixed = trackCount > 1;
                    object.put("isTrackMixed", isTrackMixed);

                    // dash hls SmoothStreaming
                    boolean isTrackMixedSelected;
                    if (trackCount > 1 && trackType == C.TRACK_TYPE_VIDEO) {
                        int videoWidth = getPlayerApi().getVideoRender().getVideoWidth();
                        int videoHeight = getPlayerApi().getVideoRender().getVideoHeight();
                        int videoBitrate = getPlayerApi().getVideoRender().getVideoBitrate();
                        isTrackMixedSelected = (videoWidth == format.width && videoHeight == format.height && videoBitrate == format.bitrate);
                    } else {
                        isTrackMixedSelected = false;
                    }
                    object.put("isTrackMixedSelected", isTrackMixedSelected);

                    object.put("id", format.id);
                    object.put("label", format.label);
//                    object.put("labels", format.labels);
                    object.put("language", format.language);
                    object.put("selectionFlags", format.selectionFlags);
                    object.put("roleFlags", format.roleFlags);
                    object.put("averageBitrate", format.averageBitrate);
                    object.put("peakBitrate", format.peakBitrate);
                    object.put("codecs", format.codecs);
                    object.put("metadata", format.metadata);
//                    object.put("customData", format.customData);
                    // Container specific.
                    object.put("containerMimeType", format.containerMimeType);
                    // Sample specific.
                    object.put("sampleMimeType", format.sampleMimeType);
                    object.put("maxInputSize", format.maxInputSize);
//                    object.put("maxNumReorderSamples", format.maxNumReorderSamples);
                    object.put("initializationData", format.initializationData);
                    object.put("drmInitData", format.drmInitData);
                    object.put("subsampleOffsetUs", format.subsampleOffsetUs);
//                    object.put("hasPrerollSamples", format.hasPrerollSamples);

                    LogUtil.log("VideoExo2Player => getTrackInfo => groupCount = " + groupCount + ", groupIndex = " + groupIndex + ", trackCount = " + trackCount + ", trackIndex = " + trackIndex + ", trackType = " + trackType + ", isGroupAdaptiveSupported = " + isGroupAdaptiveSupported + ", isGroupSelected = " + isGroupSelected + ", isGroupSupported = " + isGroupSupported + ", isTrackSelected = " + isTrackSelected + ", isTrackSupported = " + isTrackSupported + ", isTrackMixed = " + isTrackMixed + ", isTrackMixedSelected = " + isTrackMixedSelected + ", format = " + object);

                    if (null == result) {
                        result = new JSONArray();
                    }
                    result.put(object);

                }
            }

            //
            if (null == result)
                throw new Exception("error: not find");

            LogUtil.log("VideoExo2Player => getTrackInfo => type = " + type + ", result = " + result);
            return result;
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => getTrackInfo => Exception " + e.getMessage());
            return null;
        }
    }
}
