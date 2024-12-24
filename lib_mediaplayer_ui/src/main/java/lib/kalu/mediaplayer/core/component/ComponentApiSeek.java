package lib.kalu.mediaplayer.core.component;

import android.view.View;

public interface ComponentApiSeek extends ComponentApi {

    @Override
    default boolean enableDispatchKeyEvent() {
        return true;
    }

    void initSeekBarChangeListener();

    void actionDown(int repeatCount, int keyCode);

    default void updateTimeMillis() {
        try {
            long millis = System.currentTimeMillis();
            ((View) this).setTag(millis);
        } catch (Exception e) {
        }
    }

    default void clearTimeMillis() {
        try {
            ((View) this).setTag(null);
        } catch (Exception e) {
        }
    }

    default long getCastTimeMillis() {
        try {
            Object tag = ((View) this).getTag();
            if (null == tag)
                throw new Exception("warning: tag null");
            long start = (long) tag;
            long millis = System.currentTimeMillis();
            return Math.abs(millis - start);
        } catch (Exception e) {
            return 0L;
        }
    }
}