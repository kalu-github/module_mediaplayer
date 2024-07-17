package lib.kalu.mediaplayer.core.kernel.video.media3;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;

import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaLibraryInfo;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.VideoSize;
import androidx.media3.common.util.Clock;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.SimpleCache;
import androidx.media3.exoplayer.DecoderReuseEvaluation;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.ExoPlaybackException;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.RenderersFactory;
import androidx.media3.exoplayer.analytics.AnalyticsListener;
import androidx.media3.exoplayer.analytics.DefaultAnalyticsCollector;
import androidx.media3.exoplayer.dash.DashMediaSource;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.rtsp.RtspMediaSource;
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.source.LoadEventInfo;
import androidx.media3.exoplayer.source.MediaLoadData;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;
import androidx.media3.extractor.DefaultExtractorsFactory;

import com.google.android.exoplayer2.ExoPlayerLibraryInfo;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

@UnstableApi
public final class VideoMedia3Player extends VideoBasePlayer {

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
            LogUtil.log("VideoMedia3Player => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(Context context, StartArgs args) {
        LogUtil.log("VideoMedia3Player => createDecoder =>");
        try {
            if (null != mExoPlayer)
                throw new Exception("warning: null != mExoPlayer");
            if (null == args)
                throw new Exception("error: args null");

            ExoPlayer.Builder builder = new ExoPlayer.Builder(context);
            builder.setAnalyticsCollector(new DefaultAnalyticsCollector(Clock.DEFAULT));
            builder.setBandwidthMeter(DefaultBandwidthMeter.getSingletonInstance(context));
            builder.setLoadControl(new DefaultLoadControl());
            builder.setMediaSourceFactory(new DefaultMediaSourceFactory(context));
            builder.setTrackSelector(new DefaultTrackSelector(context));

            int decoderType = args.getDecoderType();
            LogUtil.log("VideoMedia3Player => createDecoder => decoderType = " + decoderType);
            // all_ffmpeg
            if (decoderType == PlayerType.DecoderType.EXO_ALL_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.media3.renderers.VideoFFmpegAudioFFmpegRenderersFactory");
                LogUtil.log("VideoMedia3Player => createDecoder => EXO_ALL_FFMPEG");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_audio_ffmpeg
            else if (decoderType == PlayerType.DecoderType.EXO_ONLY_AUDIO_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.media3.renderers.OnlyAudioFFmpegRenderersFactory");
                LogUtil.log("VideoMedia3Player => createDecoder => EXO_ONLY_AUDIO_FFMPEG");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_ffmpeg
            else if (decoderType == PlayerType.DecoderType.EXO_ONLY_VIDEO_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.media3.renderers.OnlyVideoFFmpegRenderersFactory");
                LogUtil.log("VideoMedia3Player => createDecoder => EXO_ONLY_VIDEO_FFMPEG");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // video_codec_audio_ffmpeg
            else if (decoderType == PlayerType.DecoderType.EXO_VIDEO_CODEC_AUDIO_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.media3.renderers.VideoCodecAudioFFmpegRenderersFactory");
                LogUtil.log("VideoMedia3Player => createDecoder => EXO_VIDEO_CODEC_AUDIO_FFMPEG");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // video_ffmpeg_audio_codec
            else if (decoderType == PlayerType.DecoderType.EXO_VIDEO_FFMPEG_AUDIO_CODEC) {
                Class<?> clazz = Class.forName("lib.kalu.media3.renderers.VideoFFmpegAudioCodecRenderersFactory");
                LogUtil.log("VideoMedia3Player => createDecoder => EXO_VIDEO_FFMPEG_AUDIO_CODEC");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_audio_codec
            else if (decoderType == PlayerType.DecoderType.EXO_ONLY_AUDIO_CODEC) {
                Class<?> clazz = Class.forName("lib.kalu.media3.renderers.OnlyAudioCodecRenderersFactory");
                LogUtil.log("VideoMedia3Player => createDecoder => EXO_ONLY_AUDIO_CODEC");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_codec
            else if (decoderType == PlayerType.DecoderType.EXO_ONLY_VIDEO_CODEC) {
                Class<?> clazz = Class.forName("lib.kalu.media3.renderers.OnlyVideoCodecRenderersFactory");
                LogUtil.log("VideoMedia3Player => createDecoder => EXO_ONLY_VIDEO_CODEC");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // all_codec (decoderType == PlayerType.DecoderType.EXO_ALL_CODEC)
            else {
                Class<?> clazz = Class.forName("lib.kalu.media3.renderers.VideoCodecAudioCodecRenderersFactory");
                LogUtil.log("VideoMedia3Player => createDecoder => EXO_ALL_CODEC");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }

            mExoPlayer = builder.build();
            registListener();

            //播放器日志
//        if (mExoPlayer.getTrackSelector() instanceof MappingTrackSelector) {
//            mExoPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) mExoPlayer.getTrackSelector(), "ExoPlayer"));
//        }
        } catch (Exception e) {
            LogUtil.log("VideoMedia3Player => createDecoder => " + e.getMessage());
        }
    }

    @Override
    public void startDecoder(Context context, StartArgs args) {
        LogUtil.log("VideoMedia3Player => startDecoder =>");
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            if (null == args)
                throw new Exception("error: args null");
            String url = args.getUrl();
            if (null == url)
                throw new Exception("error: url null");
            mExoPlayer.setRepeatMode(androidx.media3.exoplayer.ExoPlayer.REPEAT_MODE_OFF);
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.LOADING_START);

            MediaSource mediaSource = buildMediaSource(context, args);
            mExoPlayer.setMediaSource(mediaSource);
            boolean playWhenReady = args.isPlayWhenReady();
            mExoPlayer.setPlayWhenReady(playWhenReady);
            boolean prepareAsync = args.isPrepareAsync();
            if (prepareAsync) {
                mExoPlayer.prepare();
            } else {
                mExoPlayer.prepare();
            }
        } catch (Exception e) {
            LogUtil.log("VideoMedia3Player => startDecoder => Exception " + e.getMessage());
        }
    }

    @Override
    public void initOptions(Context context, StartArgs args) {
        LogUtil.log("VideoMedia3Player => initOptions =>");

        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            boolean mute = isMute();
            setVolume(mute ? 0L : 1L, mute ? 0L : 1L);
        } catch (Exception e) {
            LogUtil.log("VideoMedia3Player => initOptions => Exception1 " + e.getMessage());
        }

        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            int seekParameters = args.getSeekType();
            // seek model
            if (seekParameters == PlayerType.SeekType.EXO_CLOSEST_SYNC) {
                mExoPlayer.setSeekParameters(androidx.media3.exoplayer.SeekParameters.CLOSEST_SYNC);
            } else if (seekParameters == PlayerType.SeekType.EXO_PREVIOUS_SYNC) {
                mExoPlayer.setSeekParameters(androidx.media3.exoplayer.SeekParameters.PREVIOUS_SYNC);
            } else if (seekParameters == PlayerType.SeekType.EXO_NEXT_SYNC) {
                mExoPlayer.setSeekParameters(androidx.media3.exoplayer.SeekParameters.NEXT_SYNC);
            } else if (seekParameters == PlayerType.SeekType.EXO_EXACT) {
                mExoPlayer.setSeekParameters(androidx.media3.exoplayer.SeekParameters.EXACT);
            } else {
                mExoPlayer.setSeekParameters(androidx.media3.exoplayer.SeekParameters.DEFAULT);
            }
        } catch (Exception e) {
            LogUtil.log("VideoMedia3Player => initOptions => Exception2 " + e.getMessage());
        }

        // log-jni
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            Class<?> clazz = Class.forName("androidx.media3.decoder.ffmpeg.FfmpegLibrary");
            if (null == clazz)
                throw new Exception("warning: androidx.media3.decoder.ffmpeg.FfmpegLibrary not find");
            boolean log = args.isLog();
//            androidx.media3.decoder.ffmpeg.FfmpegLibrary.ffmpegLogger(log);
        } catch (Exception e) {
            LogUtil.log("VideoMedia3Player => initOptions => Exception3 " + e.getMessage());
        }
    }

    @Override
    public void setSurface(Surface surface, int w, int h) {
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
            LogUtil.log("VideoMedia3Player => setSurface => " + e.getMessage());
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
            LogUtil.log("VideoMedia3Player => isPlaying => " + e.getMessage());
            return false;
        }
    }

    @Override
    public void seekTo(long seek) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            long duration = getDuration();
            if (seek > duration) {
                seek = duration;
            }
            mSeeking = true;
            LogUtil.log("VideoMedia3Player => seekTo => succ");
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.SEEK_START);
            mExoPlayer.seekTo(seek);
        } catch (Exception e) {
            LogUtil.log("VideoMedia3Player => seekTo => " + e.getMessage());
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
//            MPLogUtil.log("VideoMedia3Player => getPosition => " + e.getMessage());
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
//            MPLogUtil.log("VideoMedia3Player => getDuration => " + e.getMessage());
            return 0L;
        }
    }

    /**
     * 设置播放速度
     */
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
            LogUtil.log("VideoMedia3Player => setSpeed => " + e.getMessage());
            return false;
        }
    }

    @Override
    public void setVolume(float v1, float v2) {
        try {
            float value;
            boolean mute = isMute();
            LogUtil.log("VideoMedia3Player => setVolume => mute = " + mute);
            if (mute) {
                value = 0F;
            } else {
                value = Math.max(v1, v2);
                if (value > 1f) {
                    value = 1f;
                }
            }
            LogUtil.log("VideoMedia3Player => setVolume => value = " + value);
            mExoPlayer.setVolume(value);
        } catch (Exception e) {
            LogUtil.log("VideoMedia3Player => setVolume => " + e.getMessage());
        }
    }

    @Override
    public void registListener() {
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            mExoPlayer.addAnalyticsListener(mAnalyticsListener);
        } catch (Exception e) {
            LogUtil.log("VideoMedia3Player => registListener => Exception " + e.getMessage());
        }
    }

    @Override
    public void unRegistListener() {
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            mExoPlayer.removeAnalyticsListener(mAnalyticsListener);
            mExoPlayer.setPlaybackParameters(null);
        } catch (Exception e) {
            LogUtil.log("VideoMedia3Player => unRegistListener => Exception " + e.getMessage());
        }
    }

    @Override
    public void release() {
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            mExoPlayer.setVideoSurface(null);
            mExoPlayer.release();
            mExoPlayer = null;
        } catch (Exception e) {
            LogUtil.log("VideoMedia3Player => release => " + e.getMessage());
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
            LogUtil.log("VideoMedia3Player => start => " + e.getMessage());
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
            LogUtil.log("VideoMedia3Player => pause => " + e.getMessage());
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
            LogUtil.log("VideoMedia3Player => stop => " + e.getMessage());
        }
    }

    /************************/

    public MediaSource buildMediaSource(Context context, StartArgs args) {

        String mediaUrl = args.getUrl();
        String scheme;
        Uri uri = Uri.parse(mediaUrl);
        try {
            scheme = uri.getScheme();
        } catch (Exception e) {
            scheme = null;
        }
        LogUtil.log("VideoMedia3Player => createMediaSource => scheme = " + scheme);

        // rtmp
        if (PlayerType.SchemeType.RTMP.equals(scheme)) {
            // log
            try {
                Class<?> clazz = Class.forName("androidx.media3.datasource.rtmp.RtmpDataSource.Factory");
                if (null == clazz)
                    throw new Exception();
                MediaItem mediaItem = MediaItem.fromUri(uri);
                Object o = clazz.newInstance();
                return new ProgressiveMediaSource.Factory((DataSource.Factory) o).createMediaSource(mediaItem);
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
            LogUtil.log("VideoMedia3Player => createMediaSource => contentType = " + contentType);

            // 2
            MediaItem.Builder builder = new MediaItem.Builder();
            builder.setUri(Uri.parse(mediaUrl));
            String subtitleUrl = args.getSubtitleUrl();
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

            DataSource.Factory dataSourceFactory;
            try {
                int netType = getNetType();
                LogUtil.log("VideoMedia3Player => createMediaSource => netType = " + netType);
                if (netType != PlayerType.NetType.EXO_OKHTTP)
                    throw new Exception("warning: netType != PlayerType.NetType.EXO_OKHTTP");

                Class<?> okhttpClass = Class.forName("okhttp3.OkHttpClient.Builder");
                Class<?> factoryClass = Class.forName("androidx.media3.datasource.okhttp.OkHttpDataSource.Factory");
                Object o = okhttpClass.newInstance();
                long connectTimeout = args.getConnectTimout();

                ((okhttp3.OkHttpClient.Builder) o).connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
                ((okhttp3.OkHttpClient.Builder) o).connectionPool(new ConnectionPool(10, 60, TimeUnit.MINUTES));
                ((okhttp3.OkHttpClient.Builder) o).retryOnConnectionFailure(true);
                ((okhttp3.OkHttpClient.Builder) o).proxySelector(new ProxySelector() { // 禁止抓包
                    @Override
                    public List<Proxy> select(URI uri) {
                        return Collections.singletonList(Proxy.NO_PROXY);
                    }

                    @Override
                    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                    }
                });
                OkHttpClient httpClient = ((OkHttpClient.Builder) o).build();
                Object instance = factoryClass.getDeclaredConstructor(Context.class).newInstance(httpClient);
                ((androidx.media3.datasource.okhttp.OkHttpDataSource.Factory) instance).setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY);
                dataSourceFactory = (androidx.media3.datasource.okhttp.OkHttpDataSource.Factory) instance;
            } catch (Exception e) {
                DefaultHttpDataSource.Factory httpFactory = new DefaultHttpDataSource.Factory();
                httpFactory.setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + MediaLibraryInfo.VERSION_SLASHY);
                httpFactory.setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS);
                httpFactory.setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS);
                httpFactory.setAllowCrossProtocolRedirects(true);
                httpFactory.setKeepPostFor302Redirects(true);
                dataSourceFactory = httpFactory;
            }

            int cacheType = args.getCacheType();
            boolean live = args.isLive();
            if (live) {
                cacheType = PlayerType.CacheType.EXO_CLOSE;
            }

            DataSource.Factory dataSource;
            if (cacheType == PlayerType.CacheType.EXO_CLOSE) {
                dataSource = new DefaultDataSource.Factory(context, dataSourceFactory);
            } else if (null == scheme || scheme.startsWith("file")) {
                dataSource = new DefaultDataSource.Factory(context, dataSourceFactory);
            } else {
//                dataSource = new DefaultDataSource.Factory(context, dataSourceFactory);

                // 缓存
                @PlayerType.CacheLocalType.Value
                int cacheLocalType = args.getCacheLocalType();
                @PlayerType.CacheSizeType.Value
                int cacheSizeType = args.getCacheSizeType();
                String cacheDirName = args.getCacheDirName();

                CacheDataSource.Factory dataSource1 = new CacheDataSource.Factory();
                SimpleCache simpleCache1 = VideoMedia3PlayerSimpleCache.getSimpleCache(context, cacheLocalType, cacheSizeType, cacheDirName);
                dataSource1.setCache(simpleCache1);
                dataSource1.setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
                dataSource1.setUpstreamDataSourceFactory(dataSourceFactory);
                dataSource = dataSource1;

//                // b
//                VideoMedia3PlayerSimpleCacheDataSource.Factory dataSource1 = new VideoMedia3PlayerSimpleCacheDataSource.Factory();
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

    private final AnalyticsListener mAnalyticsListener = new AnalyticsListener() {
        @Override
        public void onPlayWhenReadyChanged(AnalyticsListener.EventTime eventTime, boolean playWhenReady, int reason) {
//        MPLogUtil.log("VideoMedia3Player => onPlayWhenReadyChanged => playWhenReady = " + playWhenReady + ", reason = " + reason);
        }

        @Override
        public void onPlayerError(AnalyticsListener.EventTime eventTime, PlaybackException error) {
            try {
                if (null == error)
                    throw new Exception("PlaybackException error: null");
                if (!(error instanceof ExoPlaybackException))
                    throw new Exception("PlaybackException error: not instanceof ExoPlaybackException");
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.LOADING_STOP);
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR_SOURCE);
            } catch (Exception e) {
                LogUtil.log("VideoMedia3Player => onPlayerError => error = " + error.getMessage());
            }
        }

        @Override
        public void onTimelineChanged(AnalyticsListener.EventTime eventTime, int reason) {
            LogUtil.log("VideoMedia3Player => onTimelineChanged => reason = " + reason + ", totalBufferedDurationMs = " + eventTime.totalBufferedDurationMs + ", realtimeMs = " + eventTime.realtimeMs);
        }

        @Override
        public void onEvents(Player player, AnalyticsListener.Events events) {
//                    MediaLogUtil.log("VideoMedia3Player => onEvents => isPlaying = " + player.isPlaying());
        }

        @Override
        public void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, VideoSize videoSize) {
            try {
                if (null == videoSize)
                    throw new Exception("error: videoSize null");
                int videoWidth = videoSize.width;
                if (videoWidth <= 0)
                    throw new Exception("error: videoWidth <=0");
                int videoHeight = videoSize.height;
                if (videoHeight <= 0)
                    throw new Exception("error: videoHeight <=0");
                boolean videoSizeChanged = isVideoSizeChanged();
                if (videoSizeChanged)
                    throw new Exception("warning: videoSizeChanged = true");
                StartArgs args = getStartArgs();
                if (null == args)
                    throw new Exception("error: args null");
                setVideoSizeChanged(true);
                @PlayerType.ScaleType.Value
                int scaleType = args.getscaleType();
                int rotation = args.getRotation();
//                int rotation = (videoSize.unappliedRotationDegrees > 0 ? videoSize.unappliedRotationDegrees : PlayerType.RotationType.DEFAULT);
                onUpdateSizeChanged(PlayerType.KernelType.MEDIA_V3, videoWidth, videoHeight, rotation, scaleType);
            } catch (Exception e) {
                LogUtil.log("VideoMedia3Player => onVideoSizeChanged => " + e.getMessage());
            }
        }

        @Override
        public void onIsPlayingChanged(AnalyticsListener.EventTime eventTime, boolean isPlaying) {
            LogUtil.log("VideoMedia3Player => onIsPlayingChanged => isPlaying = " + isPlaying);
        }

        @Override
        public void onLoadError(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException e, boolean b) {
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR_SOURCE);
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.LOADING_STOP);
        }

        @Override
        public void onPlaybackStateChanged(AnalyticsListener.EventTime eventTime, int state) {

            // 播放错误
            if (state == Player.STATE_IDLE) {
                LogUtil.log("VideoMedia3Player => onPlaybackStateChanged => STATE_IDLE =>");
            }
            // 播放完成
            else if (state == Player.STATE_ENDED) {
                LogUtil.log("VideoMedia3Player => onPlaybackStateChanged => STATE_ENDED =>");
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.VIDEO_END);
            }
            // 播放开始
            else if (state == Player.STATE_READY) {
                LogUtil.log("VideoMedia3Player => onPlaybackStateChanged => STATE_READY =>");
                try {
                    if (!isPrepared())
                        throw new Exception("warning: isPrepared false");

                    if (mBuffering) {
                        mBuffering = false;
                        onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.BUFFERING_STOP);
                    }

                    if (mSeeking) {
                        mSeeking = false;
                        onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.SEEK_FINISH);

                        try {
                            long seek = getSeek();
                            if (seek <= 0L)
                                throw new Exception("warning: seek<=0");
                            setSeek(0);
                            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.VIDEO_START);
                        } catch (Exception e) {
                            LogUtil.log("VideoMedia3Player => onPlaybackStateChanged => STATE_READY => Exception1 " + e.getMessage());
                        }

                        try {
                            boolean playing = isPlaying();
                            if (playing)
                                throw new Exception("warning: playing true");
                            start();
                        } catch (Exception e) {
                            LogUtil.log("VideoMedia3Player => onPlaybackStateChanged => STATE_READY => Exception2 " + e.getMessage());
                        }
                    }

                } catch (Exception e) {
                    LogUtil.log("VideoMedia3Player => onPlaybackStateChanged => STATE_READY => Exception3 " + e.getMessage());
                }
            }
            // 播放缓冲
            else if (state == Player.STATE_BUFFERING) {
                LogUtil.log("VideoMedia3Player => onPlaybackStateChanged => STATE_BUFFERING =>");
                try {
                    if (!isPrepared())
                        throw new Exception("mPrepared warning: false");
                    mBuffering = true;
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.BUFFERING_START);
                } catch (Exception e) {
                    LogUtil.log("VideoMedia3Player => onPlaybackStateChanged => STATE_BUFFERING => Exception " + e.getMessage());
                }
            }
            // UNKNOW
            else {
                LogUtil.log("VideoMedia3Player => onPlaybackStateChanged => UNKNOW => state = " + state);
            }
        }

        @Override
        public void onVideoInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
            LogUtil.log("VideoMedia3Player => onVideoInputFormatChanged[出画面] =>");
            try {
                if (isPrepared())
                    throw new Exception("warning: isPrepared true");
                setPrepared(true);
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.LOADING_STOP);
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.VIDEO_RENDERING_START);
                long seek = getSeek();
                if (seek <= 0L) {
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.VIDEO_START);
                } else {
                    // 起播快进
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.VIDEO_RENDERING_START_SEEK);
                    seekTo(seek);
                }
            } catch (Exception e) {
                LogUtil.log("VideoMedia3Player => onVideoInputFormatChanged => Exception " + e.getMessage());
            }
        }

        @Override
        public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime, Object output, long renderTimeMs) {
            LogUtil.log("VideoMedia3Player => onRenderedFirstFrame =>");
        }

        @Override
        public void onAudioInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
            LogUtil.log("VideoMedia3Player => onAudioInputFormatChanged =>");
        }

        @Override
        public void onSeekStarted(EventTime eventTime) {
            LogUtil.log("VideoMedia3Player => onSeekStarted =>");
        }

        @Override
        public void onSeekBackIncrementChanged(EventTime eventTime, long l) {
            LogUtil.log("VideoMedia3Player => onSeekBackIncrementChanged =>");
        }

        @Override
        public void onSeekForwardIncrementChanged(EventTime eventTime, long l) {
            LogUtil.log("VideoMedia3Player => onSeekForwardIncrementChanged =>");
        }
    };
}
