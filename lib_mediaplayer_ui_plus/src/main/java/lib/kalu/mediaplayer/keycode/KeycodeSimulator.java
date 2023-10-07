package lib.kalu.mediaplayer.keycode;

import android.view.KeyEvent;

import androidx.annotation.Keep;

/**
 * description: test
 * created by kalu on 2021/9/27
 */
@Keep
public class KeycodeSimulator extends KeycodeTV {

    @Override
    public int dpadLeft() {
        return KeyEvent.KEYCODE_A;
    }

    @Override
    public int dpadCenter() {
        return KeyEvent.KEYCODE_B;
    }

    @Override
    public int home() {
        return KeyEvent.KEYCODE_C;
    }

    @Override
    public int back() {
        return KeyEvent.KEYCODE_D;
    }
}
