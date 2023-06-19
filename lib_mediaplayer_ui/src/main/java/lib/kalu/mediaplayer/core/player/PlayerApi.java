package lib.kalu.mediaplayer.core.player;

import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.util.MPLogUtil;

public interface PlayerApi extends PlayerApiBuriedEvent, PlayerApiBase, PlayerApiKernel, PlayerApiDevice, PlayerApiComponent, PlayerApiCache, PlayerApiRender, PlayerApiExternalMusic {

    default boolean dispatchKeyEventPlayer(@NonNull KeyEvent event) {
        // stopFull
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK && isFull()) {
            stopFull();
            return true;
        }
        // stopFloat
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK && isFloat()) {
            stopFloat();
            return true;
        }

        dispatchKeyEventComponentAll(event);
        if (isFull()) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                return true;
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                return true;
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    default void checkOnWindowVisibilityChanged(int visibility) {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0)
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
            MPLogUtil.log("PlayerApi => checkOnWindowVisibilityChanged => " + e.getMessage());
        }
    }

    default void checkOnDetachedFromWindow(@NonNull boolean releaseTag) {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0)
                throw new Exception("url error: " + url);
            release(releaseTag, false);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApi => checkOnDetachedFromWindow => " + e.getMessage());
        }
    }

    default void checkOnAttachedToWindow() {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0)
                throw new Exception("url warning: "+url);
            boolean playing = isPlaying();
            MPLogUtil.log("PlayerApi => checkOnAttachedToWindow => url = " + url + ", playing = " + playing + ", this = " + this);
            if (playing)
                throw new Exception("playing warning: true");
            restart();
        }catch (Exception e){
            MPLogUtil.log("PlayerApi => checkOnAttachedToWindow => " + e.getMessage());
        }
    }

    default void onSaveBundle() {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0)
                throw new Exception("url warning: "+url);
            long position = getPosition();
            long duration = getDuration();
            saveBundle(getBaseContext(), url, position, duration);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApi => onSaveBundle => " + e.getMessage());
        }
    }
}
