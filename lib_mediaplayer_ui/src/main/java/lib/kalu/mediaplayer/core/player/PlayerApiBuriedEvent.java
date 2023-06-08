package lib.kalu.mediaplayer.core.player;

import lib.kalu.mediaplayer.buried.BuriedEvent;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface PlayerApiBuriedEvent {

    default void onBuriedEventPlaying() {
        try {
            if (!(this instanceof PlayerApi))
                throw new Exception("this not this instanceof PlayerApi");
            BuriedEvent buriedEvent = PlayerManager.getInstance().getConfig().getBuriedEvent();
            if (null == buriedEvent)
                throw new Exception("buriedEvent warning: null");
            String url = ((PlayerApi) this).getUrl();
            long position = ((PlayerApi) this).getPosition();
            long duration = ((PlayerApi) this).getDuration();
            buriedEvent.onPlaying(url, position, duration);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBuriedEvent => onBuriedEventPlaying => " + e.getMessage());
        }
    }

    default void onBuriedEventPause() {
        try {
            if (!(this instanceof PlayerApi))
                throw new Exception("this not this instanceof PlayerApi");
            BuriedEvent buriedEvent = PlayerManager.getInstance().getConfig().getBuriedEvent();
            if (null == buriedEvent)
                throw new Exception("buriedEvent warning: null");
            String url = ((PlayerApi) this).getUrl();
            long position = ((PlayerApi) this).getPosition();
            long duration = ((PlayerApi) this).getDuration();
            buriedEvent.onPause(url, position, duration);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBuriedEvent => onBuriedEventPause => " + e.getMessage());
        }
    }

    default void onBuriedEventResume() {
        try {
            if (!(this instanceof PlayerApi))
                throw new Exception("this not this instanceof PlayerApi");
            BuriedEvent buriedEvent = PlayerManager.getInstance().getConfig().getBuriedEvent();
            if (null == buriedEvent)
                throw new Exception("buriedEvent warning: null");
            String url = ((PlayerApi) this).getUrl();
            long position = ((PlayerApi) this).getPosition();
            long duration = ((PlayerApi) this).getDuration();
            buriedEvent.onResume(url, position, duration);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBuriedEvent => onBuriedEventResume => " + e.getMessage());
        }
    }

    default void onBuriedEventError() {
        try {
            if (!(this instanceof PlayerApi))
                throw new Exception("this not this instanceof PlayerApi");
            BuriedEvent buriedEvent = PlayerManager.getInstance().getConfig().getBuriedEvent();
            if (null == buriedEvent)
                throw new Exception("buriedEvent warning: null");
            String url = ((PlayerApi) this).getUrl();
            long position = ((PlayerApi) this).getPosition();
            long duration = ((PlayerApi) this).getDuration();
            buriedEvent.onError(url, position, duration);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBuriedEvent => onBuriedEventError => " + e.getMessage());
        }
    }

    default void onBuriedEventCompletion() {
        try {
            if (!(this instanceof PlayerApi))
                throw new Exception("this not this instanceof PlayerApi");
            BuriedEvent buriedEvent = PlayerManager.getInstance().getConfig().getBuriedEvent();
            if (null == buriedEvent)
                throw new Exception("buriedEvent warning: null");
            String url = ((PlayerApi) this).getUrl();
            long position = ((PlayerApi) this).getPosition();
            long duration = ((PlayerApi) this).getDuration();
            buriedEvent.onCompletion(url, position, duration);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiBuriedEvent => onBuriedEventCompletion => " + e.getMessage());
        }
    }
}
