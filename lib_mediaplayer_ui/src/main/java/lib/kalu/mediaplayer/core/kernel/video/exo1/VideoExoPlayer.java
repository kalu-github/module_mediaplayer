
package lib.kalu.mediaplayer.core.kernel.video.exo1;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.android.exoplayer.ExoPlayer;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.base.BasePlayer;
import lib.kalu.mediaplayer.core.player.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;
import tv.kalu.android.exoplayer.player.DemoPlayer;

@Keep
public final class VideoExoPlayer extends BasePlayer {

    private boolean seekHelp = false;
    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;
    private boolean mPlayWhenReady = true;
    private DemoPlayer mExoPlayer;

    public VideoExoPlayer(@NonNull PlayerApi musicApi, @NonNull KernelApiEvent eventApi) {
        super(musicApi, eventApi);
    }

    @NonNull
    @Override
    public DemoPlayer getPlayer() {
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
            stopExternalMusic(true);
            mExoPlayer.setSurface(null);
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.release();
            mExoPlayer = null;
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(@NonNull Context context, @NonNull boolean logger, @NonNull int seekParameters) {
//        try {
//            Uri contentUri = Uri.parse(url);
//            String lastPathSegment = contentUri.getLastPathSegment();
//            int contentType = Util.inferContentType(lastPathSegment);
//            String userAgent = Util.getUserAgent(context, "ExoPlayerDemo");
//            switch (contentType) {
//                case Util.TYPE_SS:
//                    mExoPlayer = new DemoPlayer(new SmoothStreamingRendererBuilder(context, userAgent, contentUri.toString(), new SmoothStreamingTestMediaDrmCallback()));
//                case Util.TYPE_DASH:
//                    mExoPlayer = new DemoPlayer(new DashRendererBuilder(context, userAgent, contentUri.toString(), new WidevineTestMediaDrmCallback("content_id", "provider")));
//                case Util.TYPE_HLS:
//                    mExoPlayer = new DemoPlayer(new HlsRendererBuilder(context, userAgent, contentUri.toString()));
//                case Util.TYPE_OTHER:
//                    mExoPlayer = new DemoPlayer(new ExtractorRendererBuilder(context, userAgent, contentUri));
//                    break;
//                default:
//                    break;
//            }
//
//            mExoPlayer.addListener(new DemoPlayer.Listener() {
//
//                private boolean mIsPrepareing = false;
//                private boolean mDidPrepare = false;
//                private boolean mIsBuffering = false;
//
//                @Override
//                public void onStateChanged(boolean playWhenReady, int playbackState) {
//                    if (mIsBuffering) {
//                        switch (playbackState) {
//                            case ExoPlayer.STATE_ENDED:
//                            case ExoPlayer.STATE_READY:
//                                mIsBuffering = false;
//                                break;
//                        }
//                    }
//
//                    if (mIsPrepareing) {
//                        switch (playbackState) {
//                            case ExoPlayer.STATE_READY:
//                                mIsPrepareing = false;
//                                mDidPrepare = false;
//                                break;
//                        }
//                    }
//
//                    switch (playbackState) {
//                        case ExoPlayer.STATE_IDLE:
//                            break;
//                        case ExoPlayer.STATE_PREPARING:
//                            mIsPrepareing = true;
//                            break;
//                        case ExoPlayer.STATE_BUFFERING:
//                            mIsBuffering = true;
//                            break;
//                        case ExoPlayer.STATE_READY:
//                            onEvent(PlayerType.KernelType.EXO_V1, PlayerType.EventType.EVENT_LOADING_STOP);
//                            onEvent(PlayerType.KernelType.EXO_V1, PlayerType.EventType.EVENT_VIDEO_START);
//                            break;
//                        case ExoPlayer.STATE_ENDED:
//                            break;
//                        default:
//                            break;
//                    }
//                }
//
//                @Override
//                public void onError(Exception e) {
//
//                }
//
//                @Override
//                public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
//
//                }
//            });
//            setVolume(1F, 1F);
//        } catch (Exception e) {
//            MPLogUtil.log("VideoExoPlayer => createDecoder => " + e.getMessage());
//        }
    }

    @Override
    public void startDecoder(@NonNull Context context, @NonNull String url) {
        try {
            onEvent(PlayerType.KernelType.EXO_V1, PlayerType.EventType.EVENT_LOADING_START);
            mExoPlayer.setPlayWhenReady(mPlayWhenReady);
            mExoPlayer.prepare();
        } catch (IllegalArgumentException e) {
            MPLogUtil.log("VideoExoPlayer => startDecoder => " + e.getMessage());
            onEvent(PlayerType.KernelType.EXO_V1, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.EXO_V1, PlayerType.EventType.EVENT_ERROR_URL);
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer => startDecoder => " + e.getMessage());
            onEvent(PlayerType.KernelType.EXO_V1, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.EXO_V1, PlayerType.EventType.EVENT_ERROR_PARSE);
        }
    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {

    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        if (mExoPlayer == null) {
            return false;
        }
        int state = mExoPlayer.getPlaybackState();
        switch (state) {
            case ExoPlayer.STATE_BUFFERING:
            case ExoPlayer.STATE_READY:
                return mExoPlayer.getPlayWhenReady();
            case ExoPlayer.STATE_IDLE:
            case ExoPlayer.STATE_ENDED:
            default:
                return false;
        }
    }

    @Override
    public void seekTo(@NonNull long position, @NonNull boolean help) {
        try {
            seekHelp = help;
            mExoPlayer.seekTo(position);
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer => " + e.getMessage(), e);
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        if (mExoPlayer == null) {
            return 0L;
        }
        return mExoPlayer.getCurrentPosition();
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        if (mExoPlayer == null) {
            return 0L;
        }
        long duration = mExoPlayer.getDuration();
        if (duration < 0) {
            duration = 0L;
        }
        return duration;
    }

    @Override
    public void setSurface(@NonNull Surface surface, int w, int h) {
        if (null != surface && null != mExoPlayer) {
            try {
                mExoPlayer.setSurface(surface);
            } catch (Exception e) {
                MPLogUtil.log("VideoExoPlayer => " + e.getMessage(), e);
            }
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
    public void setSpeed(float speed) {
//        PlaybackParameters playbackParameters = new PlaybackParameters(speed);
//        mSpeedPlaybackParameters = playbackParameters;
//        if (mExoPlayer != null) {
//            mExoPlayer.setPlaybackParameters(playbackParameters);
//        }
    }

    /**
     * 获取播放速度
     */
    @Override
    public float getSpeed() {
//        try {
//            return mSpeedPlaybackParameters.speed;
//        } catch (Exception e) {
//            return 1f;
//        }
        return 1;
    }

    @Override
    public void setVolume(float v1, float v2) {
//        try {
//            float value;
//            boolean mute = isMute();
//            MPLogUtil.log("VideoExoPlayer => setVolume => mute = " + mute);
//            if (mute) {
//                value = 0F;
//            } else {
//                value = Math.max(v1, v2);
//                if (value > 1f) {
//                    value = 1f;
//                }
//            }
//            MPLogUtil.log("VideoExoPlayer => setVolume => value = " + value);
//        } catch (Exception e) {
//            MPLogUtil.log("VideoExoPlayer => setVolume => " + e.getMessage(), e);
//        }
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

    /**
     * 播放
     */
    @Override
    public void start() {
        try {
            boolean externalMusicPlaying = isExternalMusicPlaying();
            setVolume(externalMusicPlaying ? 0F : 1F, externalMusicPlaying ? 0F : 1F);
            mExoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer => start => " + e.getMessage());
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
            mExoPlayer.setPlayWhenReady(false);
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer => pause => " + e.getMessage());
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        try {
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.release();
        } catch (Exception e) {
            MPLogUtil.log("VideoExoPlayer => stop => " + e.getMessage());
        }
    }
}
