package lib.kalu.mediaplayer.core.player.video;

import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.buried.BuriedEvent;
import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.util.LogUtil;

interface VideoPlayerApiBuriedEvent {

    default void onBuriedEventPlaying() {
        try {
            if (!(this instanceof VideoPlayerApi))
                throw new Exception("this not this instanceof PlayerApi");
            BuriedEvent buriedEvent = PlayerSDK.init().getPlayerBuilder().getBuriedEvent();
            if (null == buriedEvent)
                throw new Exception("buriedEvent warning: null");

            String url = null;
            StartArgs tags = ((VideoPlayerApi) this).getTags();
            if (null != tags) {
                url = tags.getUrl();
            }

            long position = ((VideoPlayerApi) this).getPosition();
            long duration = ((VideoPlayerApi) this).getDuration();
            buriedEvent.onPlaying(url, position, duration);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedEventPlaying => " + e.getMessage());
        }
    }

    default void onBuriedEventPause() {
        try {
            if (!(this instanceof VideoPlayerApi))
                throw new Exception("this not this instanceof PlayerApi");
            BuriedEvent buriedEvent = PlayerSDK.init().getPlayerBuilder().getBuriedEvent();
            if (null == buriedEvent)
                throw new Exception("buriedEvent warning: null");

            String url = null;
            StartArgs tags = ((VideoPlayerApi) this).getTags();
            if (null != tags) {
                url = tags.getUrl();
            }

            long position = ((VideoPlayerApi) this).getPosition();
            long duration = ((VideoPlayerApi) this).getDuration();
            buriedEvent.onPause(url, position, duration);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedEventPause => " + e.getMessage());
        }
    }

    default void onBuriedEventResume() {
        try {
            if (!(this instanceof VideoPlayerApi))
                throw new Exception("this not this instanceof PlayerApi");
            BuriedEvent buriedEvent = PlayerSDK.init().getPlayerBuilder().getBuriedEvent();
            if (null == buriedEvent)
                throw new Exception("buriedEvent warning: null");

            String url = null;
            StartArgs tags = ((VideoPlayerApi) this).getTags();
            if (null != tags) {
                url = tags.getUrl();
            }

            long position = ((VideoPlayerApi) this).getPosition();
            long duration = ((VideoPlayerApi) this).getDuration();
            buriedEvent.onResume(url, position, duration);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedEventResume => " + e.getMessage());
        }
    }

    default void onBuriedEventError() {
        try {
            if (!(this instanceof VideoPlayerApi))
                throw new Exception("this not this instanceof PlayerApi");
            BuriedEvent buriedEvent = PlayerSDK.init().getPlayerBuilder().getBuriedEvent();
            if (null == buriedEvent)
                throw new Exception("buriedEvent warning: null");

            String url = null;
            StartArgs tags = ((VideoPlayerApi) this).getTags();
            if (null != tags) {
                url = tags.getUrl();
            }

            long position = ((VideoPlayerApi) this).getPosition();
            long duration = ((VideoPlayerApi) this).getDuration();
            buriedEvent.onError(url, position, duration);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedEventError => " + e.getMessage());
        }
    }

    default void onBuriedEventCompletion() {
        try {
            if (!(this instanceof VideoPlayerApi))
                throw new Exception("this not this instanceof PlayerApi");
            BuriedEvent buriedEvent = PlayerSDK.init().getPlayerBuilder().getBuriedEvent();
            if (null == buriedEvent)
                throw new Exception("buriedEvent warning: null");

            String url = null;
            StartArgs tags = ((VideoPlayerApi) this).getTags();
            if (null != tags) {
                url = tags.getUrl();
            }

            long position = ((VideoPlayerApi) this).getPosition();
            long duration = ((VideoPlayerApi) this).getDuration();
            buriedEvent.onCompletion(url, position, duration);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedEventCompletion => " + e.getMessage());
        }
    }
}
