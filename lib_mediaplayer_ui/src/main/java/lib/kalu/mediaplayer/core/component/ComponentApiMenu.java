package lib.kalu.mediaplayer.core.component;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public interface ComponentApiMenu extends ComponentApi {

    /***************/

    @PlayerType.ScaleType.Value
    ArrayList<Integer> mScaleTypes = new ArrayList<Integer>();

    default void setScaleTypes(@PlayerType.ScaleType.Value int... ints) {
        try {
            if (null == ints)
                throw new Exception("warning: ints null");
            mScaleTypes.clear();
            for (int i : ints) {
                mScaleTypes.add(i);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => setScaleTypes => Exception " + e.getMessage());
        }
    }

    @PlayerType.ScaleType.Value
    default int[] getScaleTypes() {
        if (null == mScaleTypes || mScaleTypes.size() == 0) {
            return ((View) this).getResources().getIntArray(R.array.module_mediaplayer_array_scales);
        } else {
            int size = mScaleTypes.size();
            @PlayerType.ScaleType.Value
            int[] values = new int[size];
            for (int i = 0; i < size; i++) {
                values[i] = mScaleTypes.get(i);
            }
            return values;
        }
    }

    /***************/

    @PlayerType.SpeedType.Value
    ArrayList<Integer> mSpeedTypes = new ArrayList<Integer>();

    default void setSpeedTypes(@PlayerType.SpeedType.Value int... ints) {
        try {
            if (null == ints)
                throw new Exception("warning: ints null");
            mSpeedTypes.clear();
            for (int i : ints) {
                mSpeedTypes.add(i);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => setSpeedTypes => Exception " + e.getMessage());
        }
    }

    @PlayerType.SpeedType.Value
    default int[] getSpeedTypes() {
        if (null == mSpeedTypes || mSpeedTypes.size() == 0) {
            return ((View) this).getResources().getIntArray(R.array.module_mediaplayer_array_speeds);
        } else {
            int size = mSpeedTypes.size();
            @PlayerType.SpeedType.Value
            int[] values = new int[size];
            for (int i = 0; i < size; i++) {
                values[i] = mSpeedTypes.get(i);
            }
            return values;
        }
    }

    /***************/

    @Override
    default boolean enableDispatchKeyEvent() {
        return true;
    }

    default int getEpisodeItemCount() {
        try {
            StartArgs tags = getStartArgs();
            if (null == tags)
                throw new Exception("error: tags null");
            int itemCount = tags.getEpisodeItemCount();
            if (itemCount <= 0)
                throw new Exception("warning: itemCount " + itemCount);
            return itemCount;
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => getEpisodeItemCount => " + e.getMessage());
            return -1;
        }
    }

    default int getEpisodePlayingIndex() {
        try {
            StartArgs tags = getStartArgs();
            if (null == tags)
                throw new Exception("error: tags null");
            int playingIndex = tags.getEpisodePlayingIndex();
            if (playingIndex < 0)
                throw new Exception("warning: playingIndex " + playingIndex);
            return playingIndex;
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => getEpisodePlayingIndex => " + e.getMessage());
            return -1;
        }
    }

    default void clickEpisode(int pos) {
        try {
            OnPlayerEpisodeListener listener = getOnPlayerEpisodeListener();
            if (null == listener)
                throw new Exception("error: null == listener");
            listener.onEpisode(pos);
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => clickEpisode => " + e.getMessage());
        }
    }

    default void scrollEpisode(int action) {
    }

    default void updateItemSelected(int viewId) {
    }

    default void updateTabSelected(int viewId) {
    }

    default void updateTabCheckedChange(boolean requestFocus) {
    }

    default void toggleScale(int focusId) {
    }

    default void toggleSpeed(int focusId) {

    }

    default void toggleEpisode(int focusId) {

    }

    Handler[] mHandlerDelayedMsg = new Handler[]{null};

    default void stopDelayedMsg() {
        if (null != mHandlerDelayedMsg[0]) {
            mHandlerDelayedMsg[0].removeMessages(1000);
            mHandlerDelayedMsg[0] = null;
        }
    }

    default void startDelayedMsg() {
        if (null == mHandlerDelayedMsg[0]) {
            mHandlerDelayedMsg[0] = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if (msg.what == 1000) {
                        hide();
                    }
                }
            };
        }
        mHandlerDelayedMsg[0].removeMessages(1000);
        mHandlerDelayedMsg[0].sendEmptyMessageDelayed(1000, 4000);
    }
}