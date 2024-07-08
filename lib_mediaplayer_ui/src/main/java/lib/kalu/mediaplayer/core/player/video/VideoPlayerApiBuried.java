package lib.kalu.mediaplayer.core.player.video;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.buried.BuriedEvent;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

interface VideoPlayerApiBuried {

    default void onBuriedRendering() {
        try {
            Object[] objects = checkValue();
            if (null == objects)
                throw new Exception("warning: objects null");
            ((BuriedEvent) objects[0]).onRendering((StartArgs) objects[1], (long) objects[2], (long) objects[3]);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedRendering => Exception " + e.getMessage());
        }
    }

    default void onBuriedStart() {
        try {
            Object[] objects = checkValue();
            if (null == objects)
                throw new Exception("warning: objects null");
            ((BuriedEvent) objects[0]).onStart((StartArgs) objects[1], (long) objects[2], (long) objects[3]);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedStart => Exception " + e.getMessage());
        }
    }

    default void onBuriedError(@PlayerType.EventType.Value int code) {
        try {
            Object[] objects = checkValue();
            if (null == objects)
                throw new Exception("warning: objects null");
            ((BuriedEvent) objects[0]).onError((StartArgs) objects[1], (long) objects[2], (long) objects[3], code);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedError => Exception " + e.getMessage());
        }
    }

    default void onBuriedPause() {
        try {
            Object[] objects = checkValue();
            if (null == objects)
                throw new Exception("warning: objects null");
            ((BuriedEvent) objects[0]).onPause((StartArgs) objects[1], (long) objects[2], (long) objects[3]);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedPause => Exception " + e.getMessage());
        }
    }

    default void onBuriedResume() {
        try {
            Object[] objects = checkValue();
            if (null == objects)
                throw new Exception("warning: objects null");
            ((BuriedEvent) objects[0]).onResume((StartArgs) objects[1], (long) objects[2], (long) objects[3]);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedResume => Exception " + e.getMessage());
        }
    }

    default void onBuriedComplete() {
        try {
            Object[] objects = checkValue();
            if (null == objects)
                throw new Exception("warning: objects null");
            ((BuriedEvent) objects[0]).onComplete((StartArgs) objects[1], (long) objects[2], (long) objects[3]);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedComplete => Exception " + e.getMessage());
        }
    }

    default void onBuriedStop() {
        try {
            Object[] objects = checkValue();
            if (null == objects)
                throw new Exception("warning: objects null");
            ((BuriedEvent) objects[0]).onStop((StartArgs) objects[1], (long) objects[2], (long) objects[3]);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedStop => Exception " + e.getMessage());
        }
    }

    default void onBuriedBufferingStart() {
        try {
            Object[] objects = checkValue();
            if (null == objects)
                throw new Exception("warning: objects null");
            ((BuriedEvent) objects[0]).onBufferingStart((StartArgs) objects[1], (long) objects[2], (long) objects[3]);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedBufferingStart => Exception " + e.getMessage());
        }
    }

    default void onBuriedBufferingStop() {
        try {
            Object[] objects = checkValue();
            if (null == objects)
                throw new Exception("warning: objects null");
            ((BuriedEvent) objects[0]).onBufferingStop((StartArgs) objects[1], (long) objects[2], (long) objects[3]);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedBufferingStop => Exception " + e.getMessage());
        }
    }

    default void onBuriedSeekStart() {
        try {
            Object[] objects = checkValue();
            if (null == objects)
                throw new Exception("warning: objects null");
            ((BuriedEvent) objects[0]).onSeekStart((StartArgs) objects[1], (long) objects[2], (long) objects[3]);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedSeekStart => Exception " + e.getMessage());
        }
    }

    default void onBuriedSeekFinish() {
        try {
            Object[] objects = checkValue();
            if (null == objects)
                throw new Exception("warning: objects null");
            ((BuriedEvent) objects[0]).onSeekFinish((StartArgs) objects[1], (long) objects[2], (long) objects[3]);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedSeekFinish => Exception " + e.getMessage());
        }
    }

    default void onBuriedWindow(@PlayerType.WindowType.Value int type) {
        try {
            Object[] objects = checkValue();
            if (null == objects)
                throw new Exception("warning: objects null");
            ((BuriedEvent) objects[0]).onWindow((StartArgs) objects[1], (long) objects[2], (long) objects[3], type);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuriedEvent => onBuriedWindow => Exception " + e.getMessage());
        }
    }

    default Object[] checkValue() {
        try {
            BuriedEvent buriedEvent = PlayerSDK.init().getPlayerBuilder().getBuriedEvent();
            if (null == buriedEvent)
                throw new Exception("warning: buriedEvent null");
            if (!(this instanceof VideoPlayerApi))
                throw new Exception("error: this not VideoPlayerApi");
            StartArgs startArgs = ((VideoPlayerApi) this).getTags();
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
            return new Object[]{buriedEvent, startArgs, position, duration};
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBuried => checkValue => Exception " + e.getMessage());
            return null;
        }
    }
}
