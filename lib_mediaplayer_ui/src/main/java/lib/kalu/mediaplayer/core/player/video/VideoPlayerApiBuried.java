package lib.kalu.mediaplayer.core.player.video;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.proxy.Proxy;
import lib.kalu.mediaplayer.bean.proxy.ProxyBuried;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoPlayerApiBuried {

    default void onBuriedVideoRenderingStart() {
        callBuried("onVideoRenderingStart");
    }

    default void onBuriedStart() {
        callBuried("onStart");
    }

    default void onBuriedError(@PlayerType.EventType.Value int code) {
        callBuried("onError");
    }

    default void onBuriedPause() {
        callBuried("onPause");
    }

    default void onBuriedResume() {
        callBuried("onResume");
    }

    default void onBuriedComplete() {
        callBuried("onComplete");
    }

    default void onBuriedStop() {
        callBuried("onStop");
    }

    default void onBuriedBufferingStart() {
        callBuried("onBufferingStart");
    }

    default void onBuriedBufferingStop() {
        callBuried("onBufferingStop");
    }

    default void onBuriedSeekStartForward() {
        callBuried("onSeekStartForward");
    }

    default void onBuriedSeekStartRewind() {
        callBuried("onSeekStartRewind");
    }

    default void onBuriedSeekFinish() {
        callBuried("onSeekFinish");
    }

    default void onBuriedWindow(@PlayerType.WindowType.Value int type) {
        callBuried("onWindow");
    }

    default void callBuried(String name) {

        try {
            if (!(this instanceof VideoPlayerApi))
                throw new Exception("error: this not VideoPlayerApi");
            Proxy proxy = PlayerSDK.init().getPlayerBuilder().getProxy();
            if (null == proxy)
                throw new Exception("error: proxy null");
            ProxyBuried proxyBuried = proxy.getProxyBuried();
            if (null == proxyBuried)
                throw new Exception("error: proxyBuried null");
            StartArgs startArgs = ((VideoPlayerApi) this).getStartArgs();
            if (null == startArgs)
                throw new Exception("error: startArgs null");
            long position = ((VideoPlayerApi) this).getPosition();
            if (position < 0L) {
                position = -1L;
            }
            long duration = ((VideoPlayerApi) this).getDuration();
            if (duration < 0L) {
                duration = -1L;
            }
            proxyBuried.onCall(name, startArgs, position, duration);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuried => callBuried => Exception " + e.getMessage());
        }
    }
}
