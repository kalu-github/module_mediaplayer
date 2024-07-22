package lib.kalu.mediaplayer.core.component;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public interface ComponentApiSeek extends ComponentApi {

    @Override
    default boolean enableDispatchKeyEvent() {
        return true;
    }

    void initSeekBarChangeListener();

    void seekToStopTrackingTouch();

    void actionUp();

    void actionDown(int repeatCount, int keyCode);
}