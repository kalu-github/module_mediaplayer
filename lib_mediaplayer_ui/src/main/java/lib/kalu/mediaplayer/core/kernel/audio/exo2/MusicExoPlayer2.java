package lib.kalu.mediaplayer.core.kernel.audio.exo2;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;

import lib.kalu.exoplayer2.ffmpeg.BaseRenderersFactory;
import lib.kalu.exoplayer2.ffmpeg.FFmpegHighAudioRenderersFactory;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.audio.MusicKernelApi;
import lib.kalu.mediaplayer.core.kernel.audio.OnMusicPlayerChangeListener;
import lib.kalu.mediaplayer.util.MPLogUtil;

@Keep
public final class MusicExoPlayer2 implements MusicKernelApi {

    private boolean mMusicEnable = true;
    private ExoPlayer mExoPlayer;
    private AnalyticsListener mAnalyticsListener;
    //
    private OnMusicPlayerChangeListener mOnMusicPlayerChangeListener;

    public MusicExoPlayer2() {
    }

    @Override
    public void setMusicListener(@NonNull OnMusicPlayerChangeListener listener) {
        MPLogUtil.log("MusicExoPlayer => setMusicListener => mExoPlayer = " + mExoPlayer + ", listener = " + listener);
        mOnMusicPlayerChangeListener = listener;
    }

    @Override
    public void createDecoder(@NonNull Context context) {
        if (null != mExoPlayer) {
            release();
        }
        if (null == mExoPlayer) {
            MPLogUtil.log("MusicExoPlayer => createDecoder => mExoPlayer = " + mExoPlayer);
            ExoPlayer.Builder builder = new ExoPlayer.Builder(context);
//            builder.setAnalyticsCollector(new DefaultAnalyticsCollector(Clock.DEFAULT));
//        builder.setBandwidthMeter(DefaultBandwidthMeter.getSingletonInstance(context));
//        builder.setTrackSelector(new DefaultTrackSelector(context));
            builder.setLoadControl(new DefaultLoadControl());
            builder.setMediaSourceFactory(new DefaultMediaSourceFactory(context));
            PlayerBuilder config = PlayerManager.getInstance().getConfig();
            int exoFFmpeg = config.getExoFFmpeg();
            if (exoFFmpeg != PlayerType.FFmpegType.EXO_EXTENSION_RENDERER_OFF) {
                builder.setRenderersFactory(new FFmpegHighAudioRenderersFactory(context));
            } else {
                builder.setRenderersFactory(new BaseRenderersFactory(context));
            }
            mExoPlayer = builder.build();
            setVolume(1F);
            setLooping(false);
            setSeekParameters();
        }
    }

    @Override
    public void setDataSource(@NonNull Context context, @NonNull String musicUrl) {
        // 2
        createDecoder(context);
        // 3
        if (null != mExoPlayer) {
            MPLogUtil.log("MusicExoPlayer => setDataSource => musicUrl = " + musicUrl + ", mExoPlayer = " + mExoPlayer);

            // 2
            MediaItem.Builder builder = new MediaItem.Builder();
            builder.setUri(Uri.parse(musicUrl));

            // 3
            DefaultHttpDataSource.Factory httpFactory = new DefaultHttpDataSource.Factory();
            httpFactory.setUserAgent("(Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY);
            httpFactory.setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS);
            httpFactory.setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS);
            httpFactory.setAllowCrossProtocolRedirects(true);
            httpFactory.setKeepPostFor302Redirects(true);

            // 4
            DefaultDataSource.Factory dataSource = new DefaultDataSource.Factory(context, httpFactory);

            // 5
            MediaItem mediaItem = builder.build();
            DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            extractorsFactory.setConstantBitrateSeekingEnabled(true);
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSource, extractorsFactory).createMediaSource(mediaItem);


            mExoPlayer.setMediaSource(mediaSource);
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.prepare();
        }
    }

    @Override
    public void start(long position, OnMusicPlayerChangeListener l) {
        // 1
        if (null == l) {
            removeListener(true);
        } else {
            removeListener(false);
        }
        // 2
        if (null != l) {
            setMusicListener(l);
        }
        // 3
        addListener(position);
        // 3
        setVolume(1F);
        // 4
        if (null != mExoPlayer) {
            MPLogUtil.log("MusicExoPlayer => start => mExoPlayer = " + mExoPlayer);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void stop() {
        if (null != mExoPlayer) {
            MPLogUtil.log("MusicExoPlayer => stop => mExoPlayer = " + mExoPlayer);
            mExoPlayer.stop();
        }
    }

    @Override
    public void pause() {
        if (null != mExoPlayer) {
            MPLogUtil.log("MusicExoPlayer => pause => mExoPlayer = " + mExoPlayer);
            mExoPlayer.pause();
        }
    }

    @Override
    public void release() {
        removeListener(true);
        if (null != mExoPlayer) {
            MPLogUtil.log("MusicExoPlayer => release => mExoPlayer = " + mExoPlayer);
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void addListener(long position) {

        if (null != mExoPlayer) {
            MPLogUtil.log("MusicExoPlayer => addListener => mExoPlayer = " + mExoPlayer);
            final boolean[] status = {false};
            mAnalyticsListener = new AnalyticsListener() {

                @Override
                public void onPlayerError(EventTime eventTime, PlaybackException error) {
                    MPLogUtil.log("MusicExoPlayer => onPlayerError => " + error.getMessage(), error);
                    if (null != mOnMusicPlayerChangeListener) {
                        mOnMusicPlayerChangeListener.onError();
                    }
                }

                @Override
                public void onPlaybackStateChanged(EventTime eventTime, int state) {
                    MPLogUtil.log("MusicExoPlayer => onPlaybackStateChanged => state = " + state + ", mOnMusicPlayerChangeListener = " + mOnMusicPlayerChangeListener);
                    // 播放结束
                    if (state == Player.STATE_ENDED) {
                        if (null != mOnMusicPlayerChangeListener) {
                            mOnMusicPlayerChangeListener.onEnd();
                        }
                    }
                    // 播放开始
                    else if (state == Player.STATE_READY) {
                        if (!status[0] && position > 0) {
                            long duration = getDuration();
                            if (duration > 0 && position <= duration) {
                                status[0] = true;
                                seekTo(position);
                                MPLogUtil.log("MusicExoPlayer => onPlaybackStateChanged => seekTo => state = " + state);
                            } else {
                                if (null != mOnMusicPlayerChangeListener) {
                                    MPLogUtil.log("MusicExoPlayer => onPlaybackStateChanged => onStart => state = " + state);
                                    mOnMusicPlayerChangeListener.onStart();
                                }
                            }
                        } else {
                            if (null != mOnMusicPlayerChangeListener) {
                                MPLogUtil.log("MusicExoPlayer => onPlaybackStateChanged => onStart => state = " + state);
                                mOnMusicPlayerChangeListener.onStart();
                            }
                        }
                    }
                }
            };
            mExoPlayer.addAnalyticsListener(mAnalyticsListener);
        }
    }

    @Override
    public void removeListener(boolean clear) {
        MPLogUtil.log("MusicExoPlayer => removeListener => mExoPlayer = " + mExoPlayer);
        if (clear && null != mOnMusicPlayerChangeListener) {
            setMusicListener(null);
            mOnMusicPlayerChangeListener = null;
        }
        if (null != mAnalyticsListener) {
            if (null != mExoPlayer) {
                mExoPlayer.removeAnalyticsListener(mAnalyticsListener);
            }
            mAnalyticsListener = null;
        }
    }

    @Override
    public void setLooping(boolean v) {
        if (null != mExoPlayer) {
            MPLogUtil.log("MusicExoPlayer => setLooping => v = " + v + ", mExoPlayer = " + mExoPlayer);
            mExoPlayer.setRepeatMode(v ? Player.REPEAT_MODE_ALL : Player.REPEAT_MODE_OFF);
        }
    }

    @Override
    public void setVolume(float v) {
        if (null != mExoPlayer) {
            MPLogUtil.log("MusicExoPlayer => setVolume => v = " + v + ", mExoPlayer = " + mExoPlayer);
            mExoPlayer.setVolume(v);
        }
    }

    @Override
    public boolean isPlaying() {
        MPLogUtil.log("MusicExoPlayer => isPlaying => mExoPlayer = " + mExoPlayer);
        if (mExoPlayer == null)
            return false;
        return mExoPlayer.isPlaying();
    }

//    @Override
//    public boolean isEnable() {
//        return mMusicEnable;
//    }
//
//    @Override
//    public void setEnable(boolean v) {
//        mMusicEnable = v;
//    }

    @Override
    public void seekTo(long v) {
        if (null != mExoPlayer) {
            MPLogUtil.log("MusicExoPlayer => seekTo => v = " + v + ", mExoPlayer = " + mExoPlayer);
            long duration = getDuration();
            MPLogUtil.log("MusicExoPlayer => seekTo =>  duration = " + duration);
            if (v < duration) {
                mExoPlayer.seekTo(v);
            }
        }
    }

    @Override
    public long getDuration() {
        MPLogUtil.log("MusicExoPlayer => getDuration =>  mExoPlayer = " + mExoPlayer);
        if (mExoPlayer == null)
            return 0L;
        return mExoPlayer.getDuration();
    }

    @Override
    public long getPosition() {
        MPLogUtil.log("MusicExoPlayer => getPosition =>  mExoPlayer = " + mExoPlayer);
        if (mExoPlayer == null)
            return 0L;
        return mExoPlayer.getCurrentPosition();
    }

    @Override
    public void setSeekParameters() {
        if (null != mExoPlayer) {
            MPLogUtil.log("MusicExoPlayer => setSeekParameters => mExoPlayer = " + mExoPlayer);
            mExoPlayer.setSeekParameters(SeekParameters.DEFAULT);
        }
    }
}
