package lib.kalu.mediaplayer.core.component;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.listener.OnPlayerItemsLiatener;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;

public interface ComponentApiMenu extends ComponentApi {

    @Override
    default boolean enableDispatchKeyEvent() {
        return true;
    }

    default void setItemsData(int checkedPos, int count) {
    }

    default void setItemsChecked(int checkedPos) {
    }

    default void scrollNextItem(int action) {
    }

    default void showItems(){
    }

    default void showSpeeds(){

    }

    boolean[] mComponentPauseShowing = new boolean[]{false};

    default void checkComponentPause1() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: null == playerView");
            ComponentApi component = playerView.findComponent(ComponentApiPause.class);
            if (null == component)
                throw new Exception("error: null == component");
            boolean componentShowing = component.isComponentShowing();
            LogUtil.log("ComponentApiMenu => checkComponentPause1 => componentShowing = " + componentShowing);
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            mComponentPauseShowing[0] = true;
            component.hide();
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => checkComponentPause1 => " + e.getMessage());
        }
    }

    default void checkComponentPause2() {
        try {
            LogUtil.log("ComponentApiMenu => checkComponentPause1 => mComponentPauseShowing[0] = " + mComponentPauseShowing[0]);
            if (!mComponentPauseShowing[0])
                throw new Exception("warning: mComponentPauseShowing[0] false");
            mComponentPauseShowing[0] = false;
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: null == playerView");
            ComponentApi component = playerView.findComponent(ComponentApiPause.class);
            if (null == component)
                throw new Exception("error: null == component");
            component.show();
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => checkComponentPause2 => " + e.getMessage());
        }
    }

    default void callItemsClickListener(int pos) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: null == playerView");
            OnPlayerItemsLiatener listener = playerView.getOnPlayerItemsListener();
            if (null == listener)
                throw new Exception("error: null == listener");
            listener.onItem(pos);
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => callItemsClickListener => " + e.getMessage());
        }
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