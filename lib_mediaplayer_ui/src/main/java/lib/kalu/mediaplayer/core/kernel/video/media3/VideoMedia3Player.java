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
    private ExoPlayer.Builder mExoPlayerBuilder;

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
            release();
        } catch (Exception e) {
            LogUtil.log("VideoMedia3Player => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(Context context) {
        try {
            if (null != mExoPlayer)
                throw new Exception("warning: null != mExoPlayer");
            // 2
            mExoPlayerBuilder = new ExoPlayer.Builder(context);
//            if (mSpeedPlaybackParameters != null) {
//                mExoPlayer.setPlaybackParameters(mSpeedPlaybackParameters);
//            }
//        mIsPreparing = true;

            //播放器日志
//        if (mExoPlayer.getTrackSelector() instanceof MappingTrackSelector) {
//            mExoPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) mExoPlayer.getTrackSelector(), "ExoPlayer"));
//        }
        } catch (Exception e) {
            LogUtil.log("VideoMedia3Player => createDecoder => " + e.getMessage());
        }
    }

    @Override
    public void startDecoder(Context context) {
        try {
            if (null == mExoPlayerBuilder)
                throw new Exception("error: mExoPlayerBuilder null");
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            String url = args.getUrl();
            if (null == url)
                throw new Exception("error: url null");
            mExoPlayerBuilder.setAnalyticsCollector(new DefaultAnalyticsCollector(Clock.DEFAULT));
            mExoPlayerBuilder.setBandwidthMeter(DefaultBandwidthMeter.getSingletonInstance(context));
            mExoPlayerBuilder.setLoadControl(new DefaultLoadControl());
            mExoPlayerBuilder.setMediaSourceFactory(new DefaultMediaSourceFactory(context));
            mExoPlayerBuilder.setTrackSelector(new DefaultTrackSelector(context));

            int decoderType = args.getDecoderType();
            boolean useFFmpeg = args.isExoUseFFmpeg();
            LogUtil.log("VideoMedia3Player => startDecoder => decoderType = " + decoderType);
            // only_mediacodec
            if (decoderType == PlayerType.DecoderType.ALL_CODEC) {
                Class<?> clazz = Class.forName("lib.kalu.media3.renderers.VideoCodecAudioCodecRenderersFactory");
                if (null == clazz)
                    throw new Exception("warning: not lib.kalu.media3.renderers.VideoCodecAudioCodecRenderersFactory");
                LogUtil.log("VideoMedia3Player => startDecoder => ALL_CODEC");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                mExoPlayerBuilder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_mediacodec_audio
            else if (decoderType == PlayerType.DecoderType.ONLY_AUDIO_CODEC) {
                Class<?> clazz = Class.forName("lib.kalu.media3.renderers.OnlyAudioCodecRenderersFactory");
                if (null == clazz)
                    throw new Exception("warning: not lib.kalu.media3.renderers.OnlyAudioCodecRenderersFactory");
                LogUtil.log("VideoMedia3Player => startDecoder => ONLY_AUDIO_CODEC");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                mExoPlayerBuilder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_mediacodec_video
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_CODEC) {
                Class<?> clazz = Class.forName("lib.kalu.media3.renderers.OnlyVideoCodecRenderersFactory");
                if (null == clazz)
                    throw new Exception("warning: not lib.kalu.media3.renderers.OnlyVideoCodecRenderersFactory");
                LogUtil.log("VideoMedia3Player => startDecoder => ONLY_VIDEO_CODEC");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                mExoPlayerBuilder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_ffmpeg
            else if (useFFmpeg && decoderType == PlayerType.DecoderType.ALL_FFMPEG) {
                Class<?> clazz = Class.forName("");
                if (null == clazz)
                    throw new Exception("warning: not lib.kalu.media3.renderers.VideoFFmpegAudioFFmpegRenderersFactory");
                LogUtil.log("VideoMedia3Player => startDecoder => ALL_FFMPEG");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                mExoPlayerBuilder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_ffmpeg_audio
            else if (useFFmpeg && decoderType == PlayerType.DecoderType.ONLY_AUDIO_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.media3.renderers.OnlyAudioFFmpegRenderersFactory");
                if (null == clazz)
                    throw new Exception("warning: not lib.kalu.media3.renderers.OnlyAudioFFmpegRenderersFactory");
                LogUtil.log("VideoMedia3Player => startDecoder => ONLY_AUDIO_FFMPEG");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                mExoPlayerBuilder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_ffmpeg_video
            else if (useFFmpeg && decoderType == PlayerType.DecoderType.ONLY_VIDEO_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.media3.renderers.OnlyVideoFFmpegRenderersFactory");
                if (null == clazz)
                    throw new Exception("warning: not lib.kalu.media3.renderers.OnlyVideoFFmpegRenderersFactory");
                LogUtil.log("VideoMedia3Player => startDecoder => ONLY_VIDEO_FFMPEG");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                mExoPlayerBuilder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // video_mediacodec_audio_ffmpeg
            else if (useFFmpeg && decoderType == PlayerType.DecoderType.VIDEO_CODEC_AUDIO_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.media3.renderers.VideoCodecAudioFFmpegRenderersFactory");
                if (null == clazz)
                    throw new Exception("warning: not lib.kalu.media3.renderers.VideoCodecAudioFFmpegRenderersFactory");
                LogUtil.log("VideoMedia3Player => startDecoder => VIDEO_CODEC_AUDIO_FFMPEG");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                mExoPlayerBuilder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // video_ffmpeg_audio_mediacodec
            else if (useFFmpeg && decoderType == PlayerType.DecoderType.VIDEO_FFMPEG_AUDIO_CODEC) {
                Class<?> clazz = Class.forName("lib.kalu.media3.renderers.VideoFFmpegAudioCodecRenderersFactory");
                if (null == clazz)
                    throw new Exception("warning: not lib.kalu.media3.renderers.VideoFFmpegAudioCodecRenderersFactory");
                LogUtil.log("VideoMedia3Player => startDecoder => VIDEO_FFMPEG_AUDIO_CODEC");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                mExoPlayerBuilder.setRenderersFactory((RenderersFactory) newInstance);
            }
        } catch (Exception e) {
            LogUtil.log("VideoMedia3Player => startDecoder => Exception " + e.getMessage());
        }

        try {
            if (null == mExoPlayerBuilder)
                throw new Exception("warning: mExoPlayerBuilder null");
            if (null != mExoPlayer)
                throw new Exception("warning: mExoPlayer not null");
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            String url = args.getUrl();
            if (null == url)
                throw new Exception("error: url null");
            mExoPlayer = mExoPlayerBuilder.build();
        } catch (Exception e) {
        }

        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            String url = args.getUrl();
            if (null == url)
                throw new Exception("error: url null");
            mExoPlayer.setRepeatMode(androidx.media3.exoplayer.ExoPlayer.REPEAT_MODE_OFF);
            mExoPlayer.addAnalyticsListener(mAnalyticsListener);
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
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR_URL);
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.LOADING_STOP);
            LogUtil.log("VideoMedia3Player => startDecoder => " + e.getMessage());
        }
    }

    @Override
    public void initOptions(Context context) {
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            int seekParameters = args.getExoSeekType();
            // seek model
            if (seekParameters == PlayerType.ExoSeekType.CLOSEST_SYNC) {
                mExoPlayer.setSeekParameters(androidx.media3.exoplayer.SeekParameters.CLOSEST_SYNC);
            } else if (seekParameters == PlayerType.ExoSeekType.PREVIOUS_SYNC) {
                mExoPlayer.setSeekParameters(androidx.media3.exoplayer.SeekParameters.PREVIOUS_SYNC);
            } else if (seekParameters == PlayerType.ExoSeekType.NEXT_SYNC) {
                mExoPlayer.setSeekParameters(androidx.media3.exoplayer.SeekParameters.NEXT_SYNC);
            } else {
                mExoPlayer.setSeekParameters(androidx.media3.exoplayer.SeekParameters.DEFAULT);
            }
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => initOptionsExo => " + e.getMessage());
        }

        // log-jni
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            Class<?> clazz = Class.forName("androidx.media3.decoder.ffmpeg.FfmpegLibrary");
            if (null == clazz)
                throw new Exception("warning: androidx.media3.decoder.ffmpeg.FfmpegLibrary not find");
            boolean log = args.isLog();
//            androidx.media3.decoder.ffmpeg.FfmpegLibrary.ffmpegLogger(log);
        } catch (Exception e) {
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
    public void release() {
        clear();

        try {
            if (null != mExoPlayerBuilder) {
                mExoPlayerBuilder = null;
            }
        } catch (Exception e) {
        }

        try {
            if (null != mExoPlayer) {
                mExoPlayer.removeAnalyticsListener(mAnalyticsListener);
                mExoPlayer.setPlaybackParameters(null);
                mExoPlayer.setPlayWhenReady(false);
                mExoPlayer.setVideoSurface(null);
                mExoPlayer.release();
                mExoPlayer = null;
            }
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
                if (null == Class.forName("okhttp3.OkHttpClient"))
                    throw new Exception("warning: not okhttp3.OkHttpClient");
                Class<?> clazz = Class.forName("androidx.media3.datasource.okhttp.OkHttpDataSource.Factory");
                if (null == clazz)
                    throw new Exception("warning: not androidx.media3.datasource.okhttp.OkHttpDataSource.Factory");
                boolean useOkhttp = isExoUseOkhttp();
                LogUtil.log("VideoMedia3Player => createMediaSource => useOkhttp = " + useOkhttp);
                if (!useOkhttp)
                    throw new Exception();
                long connectTimeout = args.getConnectTimout();
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
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

                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(okHttpClient);
                ((androidx.media3.datasource.okhttp.OkHttpDataSource.Factory) newInstance).setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + MediaLibraryInfo.VERSION_SLASHY);
                dataSourceFactory = (DataSource.Factory) newInstance;
            } catch (Exception e) {
                DefaultHttpDataSource.Factory httpFactory = new DefaultHttpDataSource.Factory();
                httpFactory.setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + MediaLibraryInfo.VERSION_SLASHY);
                httpFactory.setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS);
                httpFactory.setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS);
                httpFactory.setAllowCrossProtocolRedirects(true);
                httpFactory.setKeepPostFor302Redirects(true);
                dataSourceFactory = httpFactory;
            }

            int cacheType = args.getExoCacheType();
            boolean live = args.isLive();
            if (live) {
                cacheType = PlayerType.CacheType.NONE;
            }

            DataSource.Factory dataSource;
            if (cacheType == PlayerType.CacheType.NONE) {
                dataSource = new DefaultDataSource.Factory(context, dataSourceFactory);
            } else if (null == scheme || scheme.startsWith("file")) {
                dataSource = new DefaultDataSource.Factory(context, dataSourceFactory);
            } else {
//                dataSource = new DefaultDataSource.Factory(context, dataSourceFactory);

//                // a
                int cacheMax = args.getExoCacheMax();
                String cacheDir = args.getExoCacheDir();
                CacheDataSource.Factory dataSource1 = new CacheDataSource.Factory();
                SimpleCache simpleCache1 = VideoMedia3PlayerSimpleCache.getSimpleCache(context, cacheMax, cacheDir);
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
