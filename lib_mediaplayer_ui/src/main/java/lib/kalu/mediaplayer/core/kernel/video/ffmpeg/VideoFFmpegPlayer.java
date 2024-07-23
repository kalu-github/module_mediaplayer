package lib.kalu.mediaplayer.core.kernel.video.ffmpeg;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;

import androidx.annotation.NonNull;

import lib.kalu.ffplayer.FFmpegPlayer;
import lib.kalu.ffplayer.inter.OnBufferingUpdateListener;
import lib.kalu.ffplayer.inter.OnCompletionListener;
import lib.kalu.ffplayer.inter.OnErrorListener;
import lib.kalu.ffplayer.inter.OnInfoListener;
import lib.kalu.ffplayer.inter.OnPreparedListener;
import lib.kalu.ffplayer.inter.OnSeekCompleteListener;
import lib.kalu.ffplayer.inter.OnVideoSizeChangedListener;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;


public final class VideoFFmpegPlayer extends VideoBasePlayer {

    private FFmpegPlayer mFFmpegPlayer = null;

    @Override
    public VideoFFmpegPlayer getPlayer() {
        return this;
    }

    @Override
    public void releaseDecoder(boolean isFromUser) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayerCollects error: null");
            if (isFromUser) {
                setEvent(null);
            }
            clear();
            unRegistListener();
            release();
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(Context context, StartArgs args) {
        try {
            if (null != mFFmpegPlayer)
                throw new Exception("warning: null != mFFmpegPlayer");
            mFFmpegPlayer = new FFmpegPlayer();
            registListener();
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => createDecoder => " + e.getMessage());
        }
    }


    @Override
    public void startDecoder(Context context, StartArgs args) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("error: mFFmpegPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            String url = args.getUrl();
            if (url == null)
                throw new Exception("url error: " + url);
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.INIT_READY);
            mFFmpegPlayer.setDataSource(context, Uri.parse(url), null);
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.PREPARE_START);
            mFFmpegPlayer.prepare();
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => startDecoder => " + e.getMessage());
            stop();
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.ERROR);
        }
    }

    @Override
    public void initOptions(Context context, StartArgs args) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("error: mFFmpegPlayer null");
            boolean mute = isMute();
            setVolume(mute ? 0L : 1L, mute ? 0L : 1L);
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => initOptions => Exception " + e.getMessage());
        }
    }

    @Override
    public void registListener() {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("error: mFFmpegPlayer null");
            mFFmpegPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mFFmpegPlayer.setOnErrorListener(onErrorListener);
            mFFmpegPlayer.setOnCompletionListener(onCompletionListener);
            mFFmpegPlayer.setOnInfoListener(onInfoListener);
            mFFmpegPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
            mFFmpegPlayer.setOnPreparedListener(onPreparedListener);
            mFFmpegPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
            mFFmpegPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => registListener => Exception " + e.getMessage());
        }
    }

    @Override
    public void unRegistListener() {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            mFFmpegPlayer.setOnErrorListener(null);
            mFFmpegPlayer.setOnCompletionListener(null);
            mFFmpegPlayer.setOnInfoListener(null);
            mFFmpegPlayer.setOnBufferingUpdateListener(null);
            mFFmpegPlayer.setOnPreparedListener(null);
            mFFmpegPlayer.setOnVideoSizeChangedListener(null);
            mFFmpegPlayer.setOnSeekCompleteListener(null);
            mFFmpegPlayer.setOnBufferingUpdateListener(null);
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => unRegistListener => Exception " + e.getMessage());
        }
    }

    //    /**
//     * 用于播放raw和asset里面的视频文件
//     */
//    @Override
//    public void setDataSource(AssetFileDescriptor fd) {
//        try {
//            mFFmpegPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
//        } catch (Exception e) {
//            MPLogUtil.log("VideoFFmpegPlayer => " + e.getMessage());
//        }
//    }

    @Override
    public void release() {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            mFFmpegPlayer.setSurface(null);
            mFFmpegPlayer.release();
            mFFmpegPlayer = null;
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => start => " + e.getMessage());
        }
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            mFFmpegPlayer.start();
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => start => " + e.getMessage());
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
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            mFFmpegPlayer.pause();
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => pause => " + e.getMessage());
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        clear();
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            mFFmpegPlayer.stop();
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => stop => " + e.getMessage());
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
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            return mFFmpegPlayer.isPlaying();
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => stop => " + e.getMessage());
            return false;
        }
    }

    /**
     * 调整进度
     */
    @Override
    public void seekTo(long seek) {
        try {
            if (seek < 0L)
                throw new Exception("error: seek<0");
            if (null == mFFmpegPlayer)
                throw new Exception("error: mFFmpegPlayer null");
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");

            long duration = getDuration();
            if (duration > 0 && seek > duration) {
                seek = duration;
            }

            long position = getPosition();
            onEvent(PlayerType.KernelType.FFPLAYER, seek < position ? PlayerType.EventType.SEEK_START_REWIND : PlayerType.EventType.SEEK_START_FORWARD);
            mFFmpegPlayer.seekTo((int) seek);
            LogUtil.log("VideoFFmpegPlayer => seekTo =>");
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => seekTo => " + e.getMessage());
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
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            long currentPosition = mFFmpegPlayer.getCurrentPosition();
            if (currentPosition < 0)
                throw new Exception("currentPosition warning: " + currentPosition);
            return currentPosition;
        } catch (Exception e) {
//            MPLogUtil.log("VideoFFmpegPlayer => getPosition => " + e.getMessage());
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
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            int duration = mFFmpegPlayer.getDuration();
            if (duration <= 0)
                throw new Exception("duration warning: " + duration);
            return duration;
        } catch (Exception e) {
//            MPLogUtil.log("VideoFFmpegPlayer => getDuration => " + e.getMessage());
            return 0L;
        }
    }

    @Override
    public void setSurface(Surface surface, int w, int h) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            if (null == surface)
                throw new Exception("surface error: null");
            mFFmpegPlayer.setSurface(surface);
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => setSurface => " + e.getMessage());
        }
    }

    @Override
    public boolean setSpeed(float speed) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
//                throw new Exception("only support above Android M");
            return false;
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => setSpeed => " + e.getMessage());
            return false;
        }
    }

    private OnInfoListener onInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(FFmpegPlayer mp, int what, int extra) {
            LogUtil.log("VideoFFmpegPlayer => onInfo => what = " + what);
            // 缓冲开始
            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                if (isPrepared()) {
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.BUFFERING_START);
                } else {
                    LogUtil.log("VideoFFmpegPlayer => onInfo => what = " + what + ", mPrepared = false");
                }
            }
            // 缓冲结束
            else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                if (isPrepared()) {
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.BUFFERING_STOP);
                } else {
                    LogUtil.log("VideoFFmpegPlayer => onInfo => what = " + what + ", mPrepared = false");
                }
            }
            // 开始播放
            else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                try {
                    if (isPrepared())
                        throw new Exception("warning: mPrepared true");
                    setPrepared(true);
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.PREPARE_COMPLETE);
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.VIDEO_RENDERING_START);
                    long seek = getPlayWhenReadySeekToPosition();
                    if (seek <= 0) {
                        onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.START);
                        // 立即播放
                        boolean playWhenReady = isPlayWhenReady();
                        onEvent(PlayerType.KernelType.FFPLAYER, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                        if (!playWhenReady) {
                            pause();
                            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.PAUSE);
                        }
                    } else {
                        onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.SEEK_START_FORWARD);
                        // 起播快进
                        setPlayWhenReadySeekFinish(true);
                        seekTo(seek);
                    }
                } catch (Exception e) {
                    LogUtil.log("VideoFFmpegPlayer => onInfo => what = " + what + ", msg = " + e.getMessage());
                }
            }
            return true;
        }
    };

    private OnSeekCompleteListener mOnSeekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(FFmpegPlayer mediaPlayer) {
            LogUtil.log("VideoFFmpegPlayer => onSeekComplete =>");
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.SEEK_FINISH);

            try {
                long seek = getPlayWhenReadySeekToPosition();
                if (seek <= 0L)
                    throw new Exception("warning: seek<=0");
                boolean playWhenReadySeekFinish = isPlayWhenReadySeekFinish();
                if (playWhenReadySeekFinish)
                    throw new Exception("warning: playWhenReadySeekFinish true");
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.START);
                // 立即播放
                boolean playWhenReady = isPlayWhenReady();
                onEvent(PlayerType.KernelType.FFPLAYER, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                if (!playWhenReady) {
                    pause();
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.PAUSE);
                }
            } catch (Exception e) {
                LogUtil.log("VideoFFmpegPlayer => onSeekComplete => Exception1 " + e.getMessage());
            }

            try {
                boolean playing = isPlaying();
                if (playing)
                    throw new Exception("warning: playing true");
                start();
            } catch (Exception e) {
                LogUtil.log("VideoFFmpegPlayer => onSeekComplete => Exception2 " + e.getMessage());
            }
        }
    };

    private OnPreparedListener onPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(FFmpegPlayer mp) {
            LogUtil.log("VideoFFmpegPlayer => onPrepared =>");
            start();
        }
    };

    private OnBufferingUpdateListener onBufferingUpdateListener = new OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(FFmpegPlayer mp, int percent) {
            LogUtil.log("VideoFFmpegPlayer => onBufferingUpdate => percent = " + percent);
        }
    };

    private OnErrorListener onErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(FFmpegPlayer mp, int what, int extra) {
            LogUtil.log("VideoFFmpegPlayer => onError => what = " + what);
            // ignore -38
            if (what == -38) {

            }
            // error
            else {
                stop();
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.STOP);
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.ERROR);
            }
            return true;
        }
    };

    private OnCompletionListener onCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(FFmpegPlayer mp) {
            LogUtil.log("VideoFFmpegPlayer => onCompletion =>");
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.COMPLETE);
        }
    };

    private OnVideoSizeChangedListener onVideoSizeChangedListener = new OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(FFmpegPlayer mp, int width, int height) {
            try {
                if (null == mp)
                    throw new Exception("error: MediaPlayer null");
                int videoWidth = mp.getVideoWidth();
                if (videoWidth <= 0)
                    throw new Exception("error: videoWidth <=0");
                int videoHeight = mp.getVideoHeight();
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
                onUpdateSizeChanged(PlayerType.KernelType.FFPLAYER, videoWidth, videoHeight, rotation, scaleType);
            } catch (Exception e) {
                LogUtil.log("VideoFFmpegPlayer => onVideoSizeChanged => " + e.getMessage());
            }
        }
    };

    /****************/

    @Override
    public void setVolume(float v1, float v2) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            float value;
            if (isMute()) {
                value = 0F;
            } else {
                value = Math.max(v1, v2);
            }
            if (value > 1f) {
                value = 1f;
            }
            mFFmpegPlayer.setVolume(value, value);
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => setVolume => " + e.getMessage());
        }
    }
}
