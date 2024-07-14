package lib.kalu.mediaplayer.core.kernel.video.exo2;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.FloatRange;
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
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.analytics.DefaultAnalyticsCollector;
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
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lib.kalu.mediaplayer.args.PlayerArgs;
import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.util.LogUtil;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;


public final class VideoExo2Player extends VideoBasePlayer {

    private ExoPlayer mExoPlayer;
    private ExoPlayer.Builder mExoPlayerBuilder;

    @Override
    public ExoPlayer getPlayer() {
        return mExoPlayer;
    }

    @Override
    public void releaseDecoder(boolean isFromUser) {
        try {
            if (null == mExoPlayerBuilder)
                throw new Exception("mExoPlayerBuilder error: null");
            if (isFromUser) {
                setEvent(null);
            }
            release();
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => releaseDecoder => " + e.getMessage());
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
            LogUtil.log("VideoExo2Player => createDecoder => " + e.getMessage());
        }
    }

    @Override
    public void startDecoder(Context context, StartArgs args) {

        try {
            if (null == mExoPlayerBuilder)
                throw new Exception("error: mExoPlayerBuilder null");
            String url = args.getUrl();
            if (null == url)
                throw new Exception("error: url null");
            int ffmpegType = args.getExoFFmpegType();
            mExoPlayerBuilder.setAnalyticsCollector(new DefaultAnalyticsCollector(Clock.DEFAULT));
            mExoPlayerBuilder.setBandwidthMeter(DefaultBandwidthMeter.getSingletonInstance(context));
            mExoPlayerBuilder.setLoadControl(new DefaultLoadControl());
            mExoPlayerBuilder.setMediaSourceFactory(new DefaultMediaSourceFactory(context));
            mExoPlayerBuilder.setTrackSelector(new DefaultTrackSelector(context));
            // only_mediacodec
            if (ffmpegType == PlayerType.ExoFFmpegType.ONLY_MEDIACODEC) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseVideoMediaCodecAudioMediaCodecRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseVideoMediaCodecAudioMediaCodecRenderersFactory");
                mExoPlayerBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseVideoMediaCodecAudioMediaCodecRenderersFactory(context));
            }
            // only_mediacodec_audio
            else if (ffmpegType == PlayerType.ExoFFmpegType.ONLY_MEDIACODEC_AUDIO) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseOnlyMediaCodecAudioRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseOnlyMediaCodecAudioRenderersFactory");
                mExoPlayerBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseOnlyMediaCodecAudioRenderersFactory(context));
            }
            // only_mediacodec_video
            else if (ffmpegType == PlayerType.ExoFFmpegType.ONLY_MEDIACODEC_VIDEO) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseOnlyMediaCodecVideoRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseOnlyMediaCodecVideoRenderersFactory");
                mExoPlayerBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseOnlyMediaCodecVideoRenderersFactory(context));
            }
            // only_ffmpeg
            else if (ffmpegType == PlayerType.ExoFFmpegType.ONLY_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseVideoFFmpegAudioFFmpegRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseVideoFFmpegAudioFFmpegRenderersFactory");
                mExoPlayerBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseVideoFFmpegAudioFFmpegRenderersFactory(context));
            }
            // only_ffmpeg_audio
            else if (ffmpegType == PlayerType.ExoFFmpegType.ONLY_FFMPEG_AUDIO) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseOnlyFFmpegAudioRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseOnlyFFmpegAudioRenderersFactory");
                mExoPlayerBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseOnlyFFmpegAudioRenderersFactory(context));
            }
            // only_ffmpeg_video
            else if (ffmpegType == PlayerType.ExoFFmpegType.ONLY_FFMPEG_VIDEO) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseOnlyFFmpegVideoRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseOnlyFFmpegVideoRenderersFactory");
                mExoPlayerBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseOnlyFFmpegVideoRenderersFactory(context));
            }
            // video_mediacodec_audio_ffmpeg
            else if (ffmpegType == PlayerType.ExoFFmpegType.VIDEO_MEDIACODEC_AUDIO_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseVideoMediaCodecAudioFFmpegRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseVideoMediaCodecAudioFFmpegRenderersFactory");
                mExoPlayerBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseVideoMediaCodecAudioFFmpegRenderersFactory(context));
            }
            // video_ffmpeg_audio_mediacodec
            else if (ffmpegType == PlayerType.ExoFFmpegType.VIDEO_FFMPEG_AUDIO_MEDIACODEC) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.ffmpeg.BaseVideoFFmpegAudioMediaCodecRenderersFactory");
                if (null == clazz)
                    throw new Exception("not find: lib.kalu.exoplayer2.ffmpeg.BaseVideoFFmpegAudioMediaCodecRenderersFactory");
                mExoPlayerBuilder.setRenderersFactory(new lib.kalu.exoplayer2.ffmpeg.BaseVideoFFmpegAudioMediaCodecRenderersFactory(context));
            }
        } catch (Exception e) {
        }


        try {
            if (null != mExoPlayer)
                throw new Exception("warning: mExoPlayer not null");
            String url = args.getUrl();
            if (null == url)
                throw new Exception("error: url null");
            if (null != mExoPlayer)
                throw new Exception("warning: mExoPlayer not null");
            mExoPlayer = mExoPlayerBuilder.build();
            mExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        } catch (Exception e) {
        }

        try {
            String url = args.getUrl();
            if (null == url)
                throw new Exception("error: url null");
            mExoPlayer.addAnalyticsListener(mAnalyticsListener);
            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.LOADING_START);

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
            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.ERROR_URL);
            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.LOADING_STOP);
            LogUtil.log("VideoExo2Player => startDecoder => " + e.getMessage());
        }
    }

    @Override
    public void initOptions(Context context, StartArgs args) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            int seekType = args.getExoSeekType();
            if (seekType == PlayerType.ExoSeekType.CLOSEST_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.CLOSEST_SYNC);
            } else if (seekType == PlayerType.ExoSeekType.PREVIOUS_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.PREVIOUS_SYNC);
            } else if (seekType == PlayerType.ExoSeekType.NEXT_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.NEXT_SYNC);
            } else {
                mExoPlayer.setSeekParameters(SeekParameters.DEFAULT);
            }
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => initOptions => " + e.getMessage());
        }

        // log
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
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
                throw new Exception("mExoPlayer error: null");
            Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary");
            if (null == clazz)
                throw new Exception("warning: com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary not find");
            boolean log = args.isLog();
            com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary.ffmpegLogger(log);
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
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.seekTo(seek);
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
    public void release() {
        clear();

        try {
            if (null != mExoPlayerBuilder) {
                mExoPlayerBuilder = null;
            }
        } catch (Exception e) {
        }

        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.removeAnalyticsListener(mAnalyticsListener);
            mExoPlayer.setPlaybackParameters(null);
            mExoPlayer.setPlayWhenReady(false);
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

    @Override
    public JSONArray getTrackInfo() {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            Tracks currentTracks = mExoPlayer.getCurrentTracks();
            if (null == currentTracks)
                throw new Exception("currentTracks error: null");
            ImmutableList<Tracks.Group> groups = currentTracks.getGroups();
            if (null == groups)
                throw new Exception("groups error: null");
            JSONArray data = new JSONArray();
            for (Tracks.Group g : groups) {
                if (null == g)
                    continue;
                Format trackFormat = g.getTrackFormat(0);
                if (null == trackFormat)
                    continue;
                JSONObject o = new JSONObject();
                o.put("type", trackFormat.sampleMimeType);
                o.put("launcher", trackFormat.language);
                o.put("id", trackFormat.id);
                o.put("info", trackFormat.toString());
                data.put(o);
            }
            return data;
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => getTrackInfo => " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean switchTrack(int trackIndex) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            TrackSelector trackSelector = mExoPlayer.getTrackSelector();
            if (null == trackSelector)
                throw new Exception("trackSelector error: null");
//            Tracks currentTracks = mExoPlayer.getCurrentTracks();
//            if (null == currentTracks)
//                throw new Exception("currentTracks error: null");
//            ImmutableList<Tracks.Group> groups = currentTracks.getGroups();
//            if (null == groups || groups.size() >= trackIndex)
//                throw new Exception("groups error: null");
            TrackGroup trackGroup = mExoPlayer.getCurrentTrackGroups().get(trackIndex);
            TrackSelectionOverride trackSelectionOverride = new TrackSelectionOverride(trackGroup, trackIndex);
            TrackSelectionParameters selectionParameters = mExoPlayer.getTrackSelectionParameters().buildUpon().setOverrideForType(trackSelectionOverride).build();
            mExoPlayer.setTrackSelectionParameters(selectionParameters);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => switchTrack => " + e.getMessage());
            return false;
        }
    }

    /************************/

    public MediaSource buildMediaSource(Context context, StartArgs args) {

        String url = args.getUrl();
        String scheme;
        Uri uri = Uri.parse(url);
        try {
            scheme = uri.getScheme();
        } catch (Exception e) {
            scheme = null;
        }
        LogUtil.log("VideoExo2Player => createMediaSource => scheme = " + scheme);

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
                String s = url.toLowerCase();
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
            LogUtil.log("VideoExo2Player => createMediaSource => contentType = " + contentType);

            // 2
            MediaItem.Builder builder = new MediaItem.Builder();
            builder.setUri(Uri.parse(url));

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

            boolean useOkhttp = PlayerSDK.init().getPlayerBuilder().isExoUseOkhttp();
            LogUtil.log("VideoExo2Player => createMediaSource => useOkhttp = " + useOkhttp);
            DataSource.Factory dataSourceFactory;
            try {
                if (!useOkhttp)
                    throw new Exception();
                Class<?> clazz = Class.forName("okhttp3.OkHttpClient");
                if (null == clazz)
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
                OkHttpDataSource.Factory okHttpFactory = new OkHttpDataSource.Factory(okHttpClient);
                okHttpFactory.setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY);
                dataSourceFactory = okHttpFactory;
            } catch (Exception e) {
                DefaultHttpDataSource.Factory httpFactory = new DefaultHttpDataSource.Factory();
                httpFactory.setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY);
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
        public void onPlaybackStateChanged(AnalyticsListener.EventTime eventTime, int state) {
            LogUtil.log("VideoExo2Player => onPlaybackStateChanged => state = " + state + ", mute = " + isMute());

            // 播放错误
            if (state == Player.STATE_IDLE) {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged[播放错误] =>");
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.ERROR_PARSE);
            }
            // 播放结束
            else if (state == Player.STATE_ENDED) {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged[播放结束] =>");
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.VIDEO_END);
            }
            // 缓冲开始
            else if (state == Player.STATE_BUFFERING) {
                if (isPrepared()) {
                    onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.BUFFERING_START);
                } else {
                    LogUtil.log("VideoExo2Player => onPlaybackStateChanged => mPrepared = false");
                }
            }
            // 播放开始
            else if (state == Player.STATE_READY) {
                if (isPrepared()) {
                    onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.BUFFERING_STOP);
                } else {
                    setPrepared(true);
                    onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.VIDEO_START);
                }
            }
        }

        // 首帧视频
        @Override
        public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime, Object output, long renderTimeMs) {
            LogUtil.log("VideoExo2Player => onRenderedFirstFrame =>");
            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.LOADING_STOP);
            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.VIDEO_RENDERING_START);
//                    try {
//                        long seek = getSeek();
//                        if (seek <= 0)
//                            throw new Exception("seek warning: " + seek);
//                        // 起播快进
//                        setSeek(0);
//                        seekTo(seek);
//                    } catch (Exception e) {
//                    }
        }

        @Override
        public void onPlayerError(AnalyticsListener.EventTime eventTime, PlaybackException error) {
            LogUtil.log("VideoExo2Player => onPlayerError => error = " + error.getMessage());
            try {
                if (null == error)
                    throw new Exception("warning: null == error");
                if (!(error instanceof ExoPlaybackException))
                    throw new Exception("warning: error not instanceof ExoPlaybackException");
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.ERROR_SOURCE);
            } catch (Exception e) {
                LogUtil.log("VideoExo2Player => onPlayerError => " + e.getMessage());
            }
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
                setVideoSizeChanged(true);
                StartArgs args = getStartArgs();
                @PlayerType.ScaleType.Value
                int scaleType = (null == args ? PlayerType.ScaleType.DEFAULT : args.getRenderScaleType());
                int rotation = (videoSize.unappliedRotationDegrees > 0 ? videoSize.unappliedRotationDegrees : PlayerType.RotationType.DEFAULT);
                onUpdateSizeChanged(PlayerType.KernelType.EXO_V2, videoWidth, videoHeight, rotation, scaleType);
            } catch (Exception e) {
                LogUtil.log("VideoExo2Player => onVideoSizeChanged => " + e.getMessage());
            }
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
}
