package lib.kalu.mediaplayer.core.kernel.video.exo2;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;

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
import com.google.android.exoplayer2.decoder.DecoderReuseEvaluation;
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
import com.google.android.exoplayer2.RenderersFactory;
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
            if (decoderType == PlayerType.DecoderType.EXO_ALL_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.VideoFFmpegAudioFFmpegRenderersFactory");
                LogUtil.log("VideoExo2Player => createDecoder => EXO_ALL_FFMPEG");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_ffmpeg_audio
            else if (decoderType == PlayerType.DecoderType.EXO_ONLY_AUDIO_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.OnlyAudioFFmpegRenderersFactory");
                LogUtil.log("VideoExo2Player => createDecoder => EXO_ONLY_AUDIO_FFMPEG");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_ffmpeg_video
            else if (decoderType == PlayerType.DecoderType.EXO_ONLY_VIDEO_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.OnlyVideoFFmpegRenderersFactory");
                LogUtil.log("VideoExo2Player => createDecoder => EXO_ONLY_VIDEO_FFMPEG");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // video_mediacodec_audio_ffmpeg
            else if (decoderType == PlayerType.DecoderType.EXO_VIDEO_CODEC_AUDIO_FFMPEG) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.VideoCodecAudioFFmpegRenderersFactory");
                LogUtil.log("VideoExo2Player => createDecoder => EXO_VIDEO_CODEC_AUDIO_FFMPEG");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // video_ffmpeg_audio_mediacodec
            else if (decoderType == PlayerType.DecoderType.EXO_VIDEO_FFMPEG_AUDIO_CODEC) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.VideoFFmpegAudioCodecRenderersFactory");
                LogUtil.log("VideoExo2Player => createDecoder => EXO_VIDEO_FFMPEG_AUDIO_CODEC");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_mediacodec_audio
            else if (decoderType == PlayerType.DecoderType.EXO_ONLY_AUDIO_CODEC) {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.OnlyAudioCodecRenderersFactory");
                LogUtil.log("VideoExo2Player => createDecoder => EXO_ONLY_AUDIO_CODEC");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_mediacodec_video
            else if (decoderType == PlayerType.DecoderType.EXO_ONLY_VIDEO_CODEC) {
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
            mExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
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
            LogUtil.log("VideoExo2Player => startDecoder => Exception4 " + e.getMessage());
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
            onEvent(PlayerType.KernelType.EXO_V2, seek<position?PlayerType.EventType.SEEK_START_REWIND:PlayerType.EventType.SEEK_START_FORWARD);
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
            int size = groups.size();
            if (size <= 0)
                throw new Exception("error: size<=0");
            JSONArray data = new JSONArray();
            for (int i = 0; i < size; i++) {
                Tracks.Group group = groups.get(0);
                if (null == group)
                    continue;
                Format trackFormat = group.getTrackFormat(0);
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
            try {
                Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.rtmp.RtmpDataSource");
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


            DataSource.Factory dataSourceFactory;
            try {
                int netType = getNetType();
                LogUtil.log("VideoExo2Player => createMediaSource => netType = " + netType);
                if (netType != PlayerType.NetType.EXO_OKHTTP)
                    throw new Exception("warning: netType != PlayerType.NetType.EXO_OKHTTP");
                Class<?> okhttpClass = Class.forName("okhttp3.OkHttpClient.Builder");
                Class<?> factoryClass = Class.forName("com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource.Factory");
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
                ((com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource.Factory) instance).setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY);
                dataSourceFactory = (com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource.Factory) instance;
            } catch (Exception e) {
                LogUtil.log("VideoExo2Player => createMediaSource => Exception  " + e.getMessage());
                DefaultHttpDataSource.Factory httpFactory = new DefaultHttpDataSource.Factory();
                httpFactory.setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY);
                long connectTimeout = args.getConnectTimout();
                httpFactory.setConnectTimeoutMs((int) connectTimeout);
                httpFactory.setReadTimeoutMs((int) connectTimeout);
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
                            long seek = getSeek();
                            if (seek <= 0L)
                                throw new Exception("warning: seek<=0");
                            boolean doSeeking = isDoSeeking();
                            if (!doSeeking)
                                throw new Exception("warning: doSeeking false");
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.START);
                            // 立即播放
                            boolean playWhenReady = isPlayWhenReady();
                            onEvent(PlayerType.KernelType.EXO_V2, playWhenReady ? PlayerType.EventType.PLAY_WHEN_READY_TRUE : PlayerType.EventType.PLAY_WHEN_READY_FALSE);
                        } catch (Exception e) {
                            LogUtil.log("VideoExo2Player => onPlaybackStateChanged => STATE_READY => Exception1 " + e.getMessage());
                        }

                        try {
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
        public void onVideoInputFormatChanged(EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
            LogUtil.log("VideoExo2Player => onVideoInputFormatChanged[出画面] =>");
            try {
                if (isPrepared())
                    throw new Exception("warning: isPrepared true");
                setPrepared(true);
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.LOADING_STOP);
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.RENDER_FIRST_FRAME);
                long seek = getSeek();
                if (seek <= 0L) {
                    onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.START);
                    // 立即播放
                    boolean playWhenReady = isPlayWhenReady();
                    onEvent(PlayerType.KernelType.EXO_V2, playWhenReady ? PlayerType.EventType.PLAY_WHEN_READY_TRUE : PlayerType.EventType.PLAY_WHEN_READY_FALSE);
                } else {
                    // 起播快进
                    onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.SEEK_START_FORWARD);
                    setDoSeeking(true);
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
