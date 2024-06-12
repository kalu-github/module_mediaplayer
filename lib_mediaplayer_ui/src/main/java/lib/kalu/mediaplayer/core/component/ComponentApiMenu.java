package lib.kalu.mediaplayer.core.component;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.listener.OnPlayerItemsLiatener;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;

public interface ComponentApiMenu extends ComponentApi {

    default void setItemsData(int pos, int count) {
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