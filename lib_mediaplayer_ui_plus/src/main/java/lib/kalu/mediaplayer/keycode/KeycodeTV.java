package lib.kalu.mediaplayer.keycode;

import android.view.KeyEvent;

import androidx.annotation.Keep;

@Keep
public class KeycodeTV implements KeycodeApi {

    @Override
    public int pause() {
        return KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
    }

    @Override
    public int play() {
        return KeyEvent.KEYCODE_MEDIA_PLAY;
    }

    @Override
    public int fastForward() {
        return KeyEvent.KEYCODE_MEDIA_FAST_FORWARD;
    }

    @Override
    public int fastRewind() {
        return KeyEvent.KEYCODE_MEDIA_REWIND;
    }

    @Override
    public int volumeUp() {
        return KeyEvent.KEYCODE_VOLUME_UP;
    }

    @Override
    public int volumeDown() {
        return KeyEvent.KEYCODE_VOLUME_DOWN;
    }

    @Override
    public int search() {
        return KeyEvent.KEYCODE_SEARCH;
    }

    @Override
    public int back() {
        return KeyEvent.KEYCODE_BACK;
    }

    @Override
    public int home() {
        return KeyEvent.KEYCODE_HOME;
    }

    @Override
    public int menu() {
        return KeyEvent.KEYCODE_MENU;
    }

    @Override
    public int dpadLeft() {
        return KeyEvent.KEYCODE_DPAD_LEFT;
    }

    @Override
    public int dpadRight() {
        return KeyEvent.KEYCODE_DPAD_RIGHT;
    }

    @Override
    public int dpadUp() {
        return KeyEvent.KEYCODE_DPAD_UP;
    }

    @Override
    public int dpadDown() {
        return KeyEvent.KEYCODE_DPAD_DOWN;
    }

    @Override
    public int dpadCenter() {
        return KeyEvent.KEYCODE_DPAD_CENTER;
    }
}
