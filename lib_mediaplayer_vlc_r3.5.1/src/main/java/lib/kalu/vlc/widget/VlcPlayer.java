package lib.kalu.vlc.widget;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IMedia;
import org.videolan.libvlc.interfaces.IVLCVout;

import java.io.File;
import java.util.ArrayList;

import lib.kalu.vlc.util.VlcLogUtil;

public final class VlcPlayer {

    private org.videolan.libvlc.LibVLC mLibVLC;
    private org.videolan.libvlc.MediaPlayer mMediaPlayer;
    private org.videolan.libvlc.MediaPlayer.EventListener mEventListener;
    private org.videolan.libvlc.interfaces.IVLCVout.OnNewVideoLayoutListener mOnNewVideoLayoutListener;

    public VlcPlayer(Context context) {

        // 1
        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        mLibVLC = new LibVLC(context, args);
        mMediaPlayer = new MediaPlayer(mLibVLC);

        // 2
        mEventListener = new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                VlcLogUtil.log("VlcPlayer => onEvent => code = " + event.type);
                // 解析开始
                if (event.type == MediaPlayer.Event.MediaChanged) {
                    if (null != mL) {
                        mL.onStart();
                    }
                }
                // 首帧画面
                else if (event.type == MediaPlayer.Event.Vout) {
                    if (null != mL) {
                        mL.onPlay();
                    }
                }
                // 播放完成
                else if (event.type == MediaPlayer.Event.EndReached) {
                    if (null != mL) {
                        mL.onEnd();
                    }
                }
                // 错误
                else if (event.type == MediaPlayer.Event.Stopped) {
                    if (null != mL) {
                        mL.onError();
                    }
                }
            }
        };
        mMediaPlayer.setEventListener(mEventListener);
        VlcLogUtil.log("VlcPlayer =>");
    }

    public void release() {

        if (null != mOnNewVideoLayoutListener) {
            if (null != mMediaPlayer) {
                mMediaPlayer.getVLCVout().attachViews(null);
            }
            mOnNewVideoLayoutListener = null;
        }

        if (null != mEventListener) {
            if (null != mMediaPlayer) {
                mMediaPlayer.setEventListener(null);
            }
            mEventListener = null;
        }

        if (null != mLibVLC) {
            mLibVLC.release();
        }

        if (null != mMediaPlayer) {

            mMediaPlayer.pause();
            mMediaPlayer.stop();

            IMedia media = mMediaPlayer.getMedia();
            if (null != media) {
                media.release();
            }

            mMediaPlayer.release();
        }
    }

    public void setLooping(boolean v) {
        try {
        } catch (Exception e) {
        }
    }

    public void setVolume(float v1, float v2) {
        try {
            mMediaPlayer.setVolume((int) ((v1 + v2) * 100 / 2));
        } catch (Exception e) {
        }
    }

    public int getVolume() {
        try {
            return mMediaPlayer.getVolume();
        } catch (Exception e) {
            return 1;
        }
    }

    public void setSurface(Surface surface, int w, int h) {
        try {
            mMediaPlayer.getVLCVout().setVideoSurface(surface, null);
            if (null != surface) {
                mMediaPlayer.setVideoTrackEnabled(true);
                mOnNewVideoLayoutListener = new IVLCVout.OnNewVideoLayoutListener() {
                    @Override
                    public void onNewVideoLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
                        mMediaPlayer.getVLCVout().setWindowSize(w, h);
                    }
                };
                mMediaPlayer.getVLCVout().attachViews(mOnNewVideoLayoutListener);
            }
            VlcLogUtil.log("VlcPlayer => setSurface => surface = " + surface + ", w = " + w + ", h = " + h);
        } catch (Exception e) {
            VlcLogUtil.log("VlcPlayer => setSurface => surface = " + surface);
        }
    }

    public void setDisplay(SurfaceHolder sh) {

        try {
            mMediaPlayer.getVLCVout().setVideoSurface(sh.getSurface(), sh);
            VlcLogUtil.log("VlcPlayer => setDisplay => sh = " + sh);
        } catch (Exception e) {
            VlcLogUtil.log("VlcPlayer => setDisplay => sh = " + sh);
        }
    }

    public void setDataSource(Uri uri) {
        setDataSource(uri, true);
    }

    public void setDataSource(String path) {
        setDataSource(Uri.parse(path), true);
    }

    public void setDataSource(Uri uri, boolean playWhenReady) {
        try {

            Media media;
            String path = uri.getPath();
            File file = new File(path);
            if (file.exists()) {
                media = new Media(mLibVLC, path);
            } else {
                media = new Media(mLibVLC, uri);
            }

            if (!playWhenReady) {
                media.addOption(":video-paused");
            }
            mMediaPlayer.setMedia(media);
            media.release();
            VlcLogUtil.log("VlcPlayer => setDataSource => uri = " + uri);
        } catch (Exception e) {
        }
    }

    public void play() {
        try {
            mMediaPlayer.play();
        } catch (Exception e) {
        }
    }

    public void pause() {
        try {
            mMediaPlayer.pause();
        } catch (Exception e) {
        }
    }

    public void stop() {
        try {
            mMediaPlayer.stop();
        } catch (Exception e) {
        }
    }

    public boolean isPlaying() {
        try {
            return mMediaPlayer.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    public long getPosition() {
        try {
            return mMediaPlayer.getLength();
        } catch (Exception e) {
            return 0L;
        }
    }

    public long getDuration() {
        try {
            return mMediaPlayer.getLength();
        } catch (Exception e) {
            return 0L;
        }
    }

    public float getSpeed() {
        try {
            return mMediaPlayer.getRate();
        } catch (Exception e) {
            return 1F;
        }
    }

    public void setSpeed(float v) {
        try {
            mMediaPlayer.setRate(v);
        } catch (Exception e) {
        }
    }

    private OnVlcInfoChangeListener mL;

    public void setOnVlcInfoChangeListener(OnVlcInfoChangeListener l) {
        mL = l;
    }
}
