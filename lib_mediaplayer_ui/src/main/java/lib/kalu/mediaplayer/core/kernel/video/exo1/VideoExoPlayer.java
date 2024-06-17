package lib.kalu.mediaplayer.core.kernel.video.exo1;

import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.FloatRange;

import com.google.android.exoplayer.ExoPlayer;

import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.util.LogUtil;
import tv.kalu.android.exoplayer.player.DemoPlayer;

public final class VideoExoPlayer extends VideoBasePlayer {

    private DemoPlayer mExoPlayer;

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
            release();
        } catch (Exception e) {
            LogUtil.log("VideoExoPlayer => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(Context context) {
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
    public void startDecoder(Context context, StartArgs args) {
        try {
            onEvent(PlayerType.KernelType.EXO_V1, PlayerType.EventType.EVENT_LOADING_START);
            mExoPlayer.setPlayWhenReady(isPlayWhenReady());
            mExoPlayer.prepare();
        } catch (IllegalArgumentException e) {
            LogUtil.log("VideoExoPlayer => startDecoder => " + e.getMessage());
            onEvent(PlayerType.KernelType.EXO_V1, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.EXO_V1, PlayerType.EventType.EVENT_ERROR_URL);
        } catch (Exception e) {
            LogUtil.log("VideoExoPlayer => startDecoder => " + e.getMessage());
            onEvent(PlayerType.KernelType.EXO_V1, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.EXO_V1, PlayerType.EventType.EVENT_ERROR_PARSE);
        }
    }

    @Override
    public void initOptions(Context context, StartArgs args) {
        try {
            Class<?> clazz = Class.forName("lib.kalu.exoplayer.util.ExoLogUtil");
            if (null == clazz)
                throw new Exception("warning: lib.kalu.exoplayer.util.ExoLogUtil not find");
            boolean log = args.isLog();
            lib.kalu.exoplayer.util.ExoLogUtil.setLogger(log);
        } catch (Exception e) {
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
                throw new Exception("mExoPlayer warning: null");
            return mExoPlayer.getPlaybackState() == ExoPlayer.STATE_READY && mExoPlayer.getPlayWhenReady();
        } catch (Exception e) {
            LogUtil.log("VideoExoPlayer => isPlaying => " + e.getMessage());
            return false;
        }
    }

    @Override
    public void seekTo(long position) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer warning: null");
            mExoPlayer.seekTo(position);
        } catch (Exception e) {
            LogUtil.log("VideoExoPlayer => seekTo => " + e.getMessage());
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
                throw new Exception("mExoPlayer warning: null");
            long currentPosition = mExoPlayer.getCurrentPosition();
            if (currentPosition < 0)
                throw new Exception("currentPosition warning: " + currentPosition);
            return currentPosition;
        } catch (Exception e) {
//            MPLogUtil.log("VideoExoPlayer => getPosition => " + e.getMessage());
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
                throw new Exception("mExoPlayer warning: null");
            long duration = mExoPlayer.getDuration();
            if (duration <= 0)
                throw new Exception("duration warning: " + duration);
            return duration;
        } catch (Exception e) {
//            MPLogUtil.log("VideoExoPlayer => getDuration => " + e.getMessage());
            return 0L;
        }
    }

    @Override
    public void setSurface(Surface surface, int w, int h) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.setSurface(surface);
        } catch (Exception e) {
            LogUtil.log("VideoExoPlayer => setSurface => " + e.getMessage());
        }
    }

    /**
     * 设置播放速度
     */
    @Override
    public boolean setSpeed(@FloatRange(from = 1F, to = 4F) float speed) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
//            if (speed < 1f)
//                throw new Exception("speed error: " + speed);
//            return speed;
            return false;
        } catch (Exception e) {
            LogUtil.log("VideoExoPlayer => setSpeed => " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取播放速度
     */
    @Override
    @FloatRange(from = 1F, to = 4F)
    public float getSpeed() {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
//            if (speed < 1f)
//                throw new Exception("speed error: " + speed);
//            return speed;
            return 1F;
        } catch (Exception e) {
            LogUtil.log("VideoExoPlayer => getSpeed => " + e.getMessage());
            return 1F;
        }
    }

    @Override
    public void setVolume(float v1, float v2) {
    }

    @Override
    public void release() {
        clear();
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            setPrepared(false);
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.setSurface(null);
            mExoPlayer.release();
            mExoPlayer = null;
        } catch (Exception e) {
            LogUtil.log("VideoExoPlayer => release => " + e.getMessage());
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
            mExoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            LogUtil.log("VideoExoPlayer => start => " + e.getMessage());
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
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.setPlayWhenReady(false);
        } catch (Exception e) {
            LogUtil.log("VideoExoPlayer => pause => " + e.getMessage());
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
            mExoPlayer.setPlayWhenReady(false);
        } catch (Exception e) {
            LogUtil.log("VideoExoPlayer => stop => " + e.getMessage());
        }
    }
}
