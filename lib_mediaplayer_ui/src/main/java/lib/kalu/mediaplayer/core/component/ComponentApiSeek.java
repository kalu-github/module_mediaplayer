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

    void actionDown(int repeatCount, int keyCode);

//    default void updateTimeMillis() {
//        try {
//            long millis = System.currentTimeMillis();
//            ((View) this).setTag(millis);
//        } catch (Exception e) {
//        }
//    }
//
//    default void clearTimeMillis() {
//        try {
//            ((View) this).setTag(null);
//        } catch (Exception e) {
//        }
//    }
//
//    default long getTimeMillis() {
//        try {
//            Object tag = ((View) this).getTag();
//            if (null == tag)
//                throw new Exception("warning: tag null");
//            return (long) tag;
//        } catch (Exception e) {
//            return 0L;
//        }
//    }
}