package lib.kalu.mediaplayer.core.player.video;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;


import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.core.component.ComponentApiSeek;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoPlayerApi extends VideoPlayerApiBuriedEvent, VideoPlayerApiBase, VideoPlayerApiKernel, VideoPlayerApiDevice, VideoPlayerApiComponent, VideoPlayerApiCache, VideoPlayerApiRender {

    default void checkOnWindowVisibilityChanged(int visibility) {
        try {
            String url = getUrl();
            if (null == url || url.length() == 0)
                throw new Exception("url error: " + url);
            boolean playing = isPlaying();
            boolean windowVisibilityChangedRelease = isWindowVisibilityChangedRelease();
            // show
            if (visibility == View.VISIBLE) {
                if (playing)
                    return;
                if (windowVisibilityChangedRelease) {
                    restart();
                } else {
                    resume(false);
                }
            }
            // hide
            else {
                if (windowVisibilityChangedRelease) {
                    release();
                } else {
                    pause(true);
                }
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApi => checkOnWindowVisibilityChanged => " + e.getMessage());
        }
    }

    default void checkOnDetachedFromWindow(boolean releaseTag) {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0)
                throw new Exception("url error: " + url);
            release(releaseTag, false);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApi => checkOnDetachedFromWindow => " + e.getMessage());
        }
    }

    default void checkOnAttachedToWindow() {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0)
                throw new Exception("url warning: " + url);
            boolean playing = isPlaying();
            LogUtil.log("VideoPlayerApi => checkOnAttachedToWindow => url = " + url + ", playing = " + playing + ", this = " + this);
            if (playing)
                throw new Exception("playing warning: true");
            restart();
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApi => checkOnAttachedToWindow => " + e.getMessage());
        }
    }

    default void onSaveBundle() {
        try {
            String url = getUrl();
            if (null == url || url.length() == 0)
                throw new Exception("url warning: " + url);
            long position = getPosition();
            long duration = getDuration();
            saveBundle(getBaseContext(), url, position, duration);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApi => onSaveBundle => " + e.getMessage());
        }
    }
}
