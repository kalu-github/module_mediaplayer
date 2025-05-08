package lib.kalu.mediaplayer.core.kernel.video.exo2;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;

import androidx.media3.common.MimeTypes;
import androidx.media3.exoplayer.source.MergingMediaSource;
import androidx.media3.exoplayer.source.SingleSampleMediaSource;

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
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.analytics.DefaultAnalyticsCollector;
import com.google.android.exoplayer2.decoder.DecoderReuseEvaluation;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.video.VideoSize;
import com.google.common.collect.ImmutableList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;


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

            ExoPlayer.Builder builder = new ExoPlayer.Builder(context);
            builder.setAnalyticsCollector(new DefaultAnalyticsCollector(Clock.DEFAULT));
            builder.setBandwidthMeter(DefaultBandwidthMeter.getSingletonInstance(context));
            builder.setLoadControl(new DefaultLoadControl());
            builder.setMediaSourceFactory(new DefaultMediaSourceFactory(context));
            builder.setTrackSelector(new DefaultTrackSelector(context));

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
//            boolean log = args.isLog();
//            com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary.ffmpegLogger(log);
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
            // rtsp
            else if (lowerCase.startsWith(PlayerType.SchemeType.RTSP)) {
                contentType = C.CONTENT_TYPE_RTSP;
            }
            // mp4
            else if (lowerCase.startsWith(PlayerType.SchemeType._MP4)) {
                contentType = -200;
            }
            // other
            else {
                contentType = C.CONTENT_TYPE_OTHER;
            }

            // 2
            MediaItem.Builder builder = new MediaItem.Builder();
            builder.setUri(Uri.parse(url));


            // 字幕
            String subtitleUrl = args.getSubtitleUrl();
            if (null != subtitleUrl && subtitleUrl.length() > 0) {
//                MediaItem.SubtitleConfiguration.Builder subtitle = new MediaItem.SubtitleConfiguration.Builder(Uri.parse(mediaUrl));
//                subtitle.setMimeType(MimeTypes.APPLICATION_SUBRIP);
//                subtitle.setLanguage("en");
//                subtitle.setSelectionFlags(C.SELECTION_FLAG_AUTOSELECT); // C.SELECTION_FLAG_DEFAULT
//                builder.setSubtitleConfigurations(Arrays.asList(subtitle.build()));
//
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


//                Class<?> okhttpClass = Class.forName("okhttp3.OkHttpClient.Builder");
//                Class<?> factoryClass = Class.forName("com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource.Factory");
//                Object o = okhttpClass.newInstance();
//                long connectTimeout = args.getConnectTimout();
//                ((okhttp3.OkHttpClient.Builder) o).connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
//                ((okhttp3.OkHttpClient.Builder) o).connectionPool(new ConnectionPool(10, 60, TimeUnit.MINUTES));
//                ((okhttp3.OkHttpClient.Builder) o).retryOnConnectionFailure(true);
//                ((okhttp3.OkHttpClient.Builder) o).proxySelector(new ProxySelector() { // 禁止抓包
//                    @Override
//                    public List<Proxy> select(URI uri) {
//                        return Collections.singletonList(Proxy.NO_PROXY);
//                    }
//
//                    @Override
//                    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
//                    }
//                });
//                OkHttpClient httpClient = ((OkHttpClient.Builder) o).build();
//                Object instance = factoryClass.getDeclaredConstructor(Context.class).newInstance(httpClient);
//                ((OkHttpDataSource.Factory) instance).setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY);
//                dataSourceFactory = (OkHttpDataSource.Factory) instance;

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

                CacheDataSource.Factory dataSource1 = new CacheDataSource.Factory();
                SimpleCache simpleCache1 = VideoExo2PlayerSimpleCache.getSimpleCache(context, cacheLocalType, cacheSizeType, cacheDirName);
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

//            // test 增加音轨
//            MediaItem.SubtitleConfiguration.Builder subtitle = new MediaItem.SubtitleConfiguration.Builder(Uri.parse("http://36.138.99.117:8197/data_source/dub/c4741e9b50dc/2023/02/13/b71f0e9a-9a69-43f8-bf71-fb7feb3f4f9a.mp3"));
//            subtitle.setMimeType(MimeTypes.AUDIO_AC3);
//            subtitle.setLanguage("aaaaa");
//            subtitle.setSelectionFlags(C.SELECTION_FLAG_AUTOSELECT); // C.SELECTION_FLAG_DEFAULT
//            builder.setSubtitleConfigurations(Arrays.asList(subtitle.build()));

//            // test
//            ArrayList<MediaItem> mediaItems = new ArrayList<MediaItem>();
//            mediaItems.add(builder.build());
//
//            // test 增加音轨
//            MediaItem mediaItem = new MediaItem.Builder()
//                    .setUri("http://36.138.99.117:8197/data_source/dub/c4741e9b50dc/2023/02/13/b71f0e9a-9a69-43f8-bf71-fb7feb3f4f9a.mp3")
//                    .build();
//            mediaItems.add(mediaItem);
//
//            DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(new DefaultDataSource.Factory(context));
//
//            MediaSource[] mediaSources = new MediaSource[mediaItems.size()];
//            for (MediaItem o : mediaItems) {
//                int i = mediaItems.indexOf(o);
//                mediaSources[i] = mediaSourceFactory.createMediaSource(o);
//            }
//            // 多路流合并
//            return new MergingMediaSource(mediaSources);
//
//            //
//            List<androidx.media3.exoplayer.source.MediaSource> mediaSources = new LinkedList<>();
//
//            // rtmp
//            if (contentType == -100) {
//                Class<?> cls = Class.forName("com.google.android.exoplayer2.ext.rtmp.RtmpDataSource");
//                return new ProgressiveMediaSource.Factory((DataSource.Factory) cls.newInstance()).createMediaSource(builder.build());
//            }
//            // rtsp
//            else if (contentType == C.CONTENT_TYPE_RTSP) {
//                 return new RtspMediaSource.Factory().createMediaSource(builder.build());
//            }
//            // dash
//            else if (contentType == C.CONTENT_TYPE_DASH) {
//                return new DashMediaSource.Factory(dataSource).createMediaSource(builder.build());
//            }
//            // hls
//            else if (contentType == C.CONTENT_TYPE_HLS) {
//                return new HlsMediaSource.Factory(dataSource).createMediaSource(builder.build());
//            }
//            // SmoothStreaming
//            else if (contentType == C.CONTENT_TYPE_SS) {
//                return new SsMediaSource.Factory(dataSource).createMediaSource(builder.build());
//            }
//            // other
//            else {
//                DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//                extractorsFactory.setConstantBitrateSeekingEnabled(true);
//                return new ProgressiveMediaSource.Factory(dataSource, extractorsFactory).createMediaSource(builder.build());
//            }


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
                Class<?> cls = Class.forName("androidx.media3.exoplayer.smoothstreaming.SsMediaSource$Factory");
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

            /**
             *  2025-04-25 20:01:32.609 25895-25932 PlayerViewModel         com.yyt.zapptv                       D  streamsList = [# com.yyt.zapptv.model.proto.Playback$StreamTrack@37d8a524
             *     format: "hls"
             *     label: "HD"
             *     quality: "HD"
             *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/720p/index.m3u8", # com.yyt.zapptv.model.proto.Playback$StreamTrack@35014555
             *     format: "hls"
             *     label: "SD"
             *     quality: "SD"
             *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/480p/index.m3u8", # com.yyt.zapptv.model.proto.Playback$StreamTrack@5f9f9c57
             *     format: "hls"
             *     is_login: true
             *     label: "FULL HD"
             *     quality: "FHD"
             *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/1080p/index.m3u8"]
             *     2025-04-25 20:01:32.612 25895-25932 PlayerViewModel         com.yyt.zapptv                       D  subtitlesList = [# com.yyt.zapptv.model.proto.Playback$SubtitleTrack@b7ab61ea
             *     format: "vtt"
             *     label: "English"
             *     language: "en"
             *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/subtitles/English.vtt", # com.yyt.zapptv.model.proto.Playback$SubtitleTrack@6d66d2c6
             *     format: "vtt"
             *     label: "Espa\303\261ol"
             *     language: "es"
             *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/subtitles/Spanish.vtt", # com.yyt.zapptv.model.proto.Playback$SubtitleTrack@925f4fa3
             *     format: "vtt"
             *     label: "Portugu\303\252s (Brasil)"
             *     language: "pt"
             *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/subtitles/Portuguese.vtt"]
             *     2025-04-25 20:01:32.612 25895-25932 PlayerViewModel         com.yyt.zapptv                       D  audiosList = []
             */

            /**
             *  "x-ssa" -> MimeTypes.TEXT_SSA
             *         "vtt" -> MimeTypes.TEXT_VTT
             *         else -> MimeTypes.TEXT_UNKNOWN
             */

            List<String> subtitleUrls = Arrays.asList("https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/subtitles/English.vtt",
                    "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/subtitles/Spanish.vtt",
                    "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/subtitles/Portuguese.vtt");

            List<String> subtitleLabls = Arrays.asList("English", "Espa\303\261ol", "Portugu\303\252s (Brasil)");
            List<String> subtitleLanguages = Arrays.asList("en", "es", "pt");

            // 外挂字幕
            for (int i = 0; i < 3; i++) {
                Uri subtitleUri = Uri.parse(subtitleUrls.get(i));
                MediaItem.SubtitleConfiguration subtitleConfig = new MediaItem.SubtitleConfiguration.Builder(subtitleUri)
//                        .setMimeType(MimeTypes.APPLICATION_SUBRIP) // 也可以用 MimeTypes.APPLICATION_SUBRIP
                        .setMimeType(MimeTypes.TEXT_VTT) // 也可以用 MimeTypes.APPLICATION_SUBRIP
                        .setSelectionFlags(C.SELECTION_FLAG_AUTOSELECT)
                        .setLanguage(subtitleLanguages.get(i))
                        .setLabel(subtitleLabls.get(i))
                        .build();
                SingleSampleMediaSource source = new SingleSampleMediaSource.Factory(dataSource)
                        .createMediaSource(subtitleConfig, C.TIME_UNSET);
                //
                mediaSources.add(source);
            }

            return new MergingMediaSource(mediaSources.toArray(new MediaSource[0]));


        } catch (Exception e) {
            throw e;
        }
    }

    private final AnalyticsListener mAnalyticsListener = new AnalyticsListener() {

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
    };

    @Override
    public boolean setTrackInfo(int groupIndex, int trackIndex) {
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            LogUtil.log("VideoExo2Player => setTrackInfo => groupIndex = " + groupIndex+", trackIndex = "+trackIndex);

            // 获取 TrackSelector
            TrackSelector trackSelector = mExoPlayer.getTrackSelector();
            TrackSelectionParameters selectionParameters = trackSelector.getParameters()
                    .buildUpon()
                    .setPreferredTextLanguage("en")
                    .build();
            trackSelector.setParameters(selectionParameters);
//
//            Tracks tracks = mExoPlayer.getCurrentTracks();
//            ImmutableList<Tracks.Group> groups = tracks.getGroups();
//
//            TrackGroup trackGroup = groups.get(groupIndex).getMediaTrackGroup();
//            TrackSelectionOverride selectionOverride = new TrackSelectionOverride(trackGroup, trackIndex);
//
//            TrackSelectionParameters selectionParameters = mExoPlayer.getTrackSelectionParameters()
//                    .buildUpon()
////                    .setMaxVideoSizeSd()
////                    .setPreferredAudioLanguage("hu")
//                    .setOverrideForType(selectionOverride)
//                    .build();
//            mExoPlayer.setTrackSelectionParameters(selectionParameters);

//            DefaultTrackSelector trackSelector = (DefaultTrackSelector) mExoPlayer.getTrackSelector();
//            // 获取当前映射的轨道信息
//            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
////            // 获取字幕轨道组
//            TrackGroup trackGroup = mappedTrackInfo.getTrackGroups(rendererIndex).get(groupIndex);
////            // 选择轨道组中的第一个轨道
//            TrackSelectionOverride selectionOverride = new TrackSelectionOverride(trackGroup, trackIndex);
////            // 创建新的轨道选择参数
//            TrackSelectionParameters selectionParameters = mExoPlayer.getTrackSelectionParameters()
//                    .buildUpon()
////                    .setTrackTypeDisabled(C.TRACK_TYPE_VIDEO, false) // 确保视频轨道启用
////                    .clearVideoSizeConstraints()
////                    .clearOverrides()
//                    .setOverrideForType(selectionOverride)
//                    .build();
//            // 应用新的轨道选择参数
//            trackSelector.setParameters(selectionParameters);

            return true;
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => setTrackInfo => " + e.getMessage());
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
            ImmutableList<Tracks.Group> groups = tracks.getGroups();
            int size = groups.size();
            for (int i = 0; i < size; i++) {
                Tracks.Group group = groups.get(i);
                if (null == group)
                    continue;

                @androidx.media3.common.C.TrackType
                int trackType = group.getType();
                boolean trackInGroupIsSelected = group.isSelected();
                boolean trackInGroupIsSupported = group.isSupported();
                for (int j = 0; j < group.length; j++) {
                    boolean isSupported = group.isTrackSupported(j);
                    boolean isSelected = group.isTrackSelected(j);
                    Format format = group.getTrackFormat(j);

                    JSONObject object = new JSONObject();

                    object.put("groupIndex", i);
                    object.put("trackIndex", j);
                    object.put("isSupported", isSupported);
                    object.put("isSelected", isSelected);

                    object.put("id", format.id);
                    object.put("label", format.label);
                    object.put("language", format.language);
                    object.put("selectionFlags", format.selectionFlags);
                    object.put("roleFlags", format.roleFlags);
                    object.put("averageBitrate", format.averageBitrate);
                    object.put("peakBitrate", format.peakBitrate);
                    object.put("codecs", format.codecs);
                    object.put("metadata", format.metadata);
                    // Container specific.
                    object.put("containerMimeType", format.containerMimeType);
                    // Sample specific.
                    object.put("sampleMimeType", format.sampleMimeType);
                    object.put("maxInputSize", format.maxInputSize);
                    object.put("initializationData", format.initializationData);
                    object.put("drmInitData", format.drmInitData);
                    object.put("subsampleOffsetUs", format.subsampleOffsetUs);

                    // 视频轨道
                    if (trackType == androidx.media3.common.C.TRACK_TYPE_VIDEO) {
                        object.put("bitrate", format.bitrate);
                        object.put("width", format.width);
                        object.put("height", format.height);
                        object.put("frameRate", format.frameRate);
                        object.put("rotationDegrees", format.rotationDegrees);
                        object.put("pixelWidthHeightRatio", format.pixelWidthHeightRatio);
                        object.put("projectionData", format.projectionData);
                        object.put("stereoMode", format.stereoMode);
                        object.put("colorInfo", format.colorInfo);
                    }
                    // 音频轨道
                    else if (trackType == androidx.media3.common.C.TRACK_TYPE_AUDIO) {
                        object.put("channelCount", format.channelCount);
                        object.put("sampleRate", format.sampleRate);
                        object.put("pcmEncoding", format.pcmEncoding);
                        object.put("encoderDelay", format.encoderDelay);
                        object.put("encoderPadding", format.encoderPadding);
                    }
                    // 字幕轨道
                    else if (trackType == androidx.media3.common.C.TRACK_TYPE_TEXT) {
                        // Text specific.
                        object.put("accessibilityChannel", format.accessibilityChannel);
                    }
                    // 媒体信息
                    else if (trackType == androidx.media3.common.C.TRACK_TYPE_METADATA) {
                    }
                    LogUtil.log("VideoMediaxPlayer => getTrackInfo => i = " + i + ", j = " + j + ", trackType = " + trackType + ", trackInGroupIsSelected = " + trackInGroupIsSelected + ", trackInGroupIsSupported = " + trackInGroupIsSupported + ", isSupported = " + isSupported + ", isSelected = " + isSelected + ", object = " + object);

                    if (null == result) {
                        result = new JSONArray();
                    }
                    result.put(object);

                }
            }
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
