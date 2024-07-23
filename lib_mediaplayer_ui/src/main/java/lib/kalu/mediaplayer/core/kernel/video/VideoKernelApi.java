package lib.kalu.mediaplayer.core.kernel.video;

import android.content.Context;
import android.view.Surface;

import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */

public interface VideoKernelApi extends VideoKernelApiHandler, VideoKernelApiBase, VideoKernelApiEvent, VideoKernelApiStartArgs {

    void onUpdateProgress();

    <T extends Object> T getPlayer();

    void releaseDecoder(boolean isFromUser);

    void startDecoder(Context context, StartArgs args);

    void initOptions(Context context, StartArgs args);

    void registListener();

    void unRegistListener();

    default void clear() {
        clearArgs();
        clearHandler();
    }

    default void createDecoder(Context context, StartArgs args) {
    }

    default void initDecoder(Context context, StartArgs args) {
        clear();
        try {
            setStartArgs(args);
        } catch (Exception e) {
        }
        try {
            boolean mute = args.isMute();
            setMute(mute);
            boolean looping = args.isLooping();
            setLooping(looping);
        } catch (Exception e) {
        }

        long playWhenReadyDelayedTime = args.getPlayWhenReadyDelayedTime();
        // 延迟播放
        if (playWhenReadyDelayedTime > 0L) {
            @PlayerType.KernelType.Value
            int kernelType = args.getKernelType();
            startPlayWhenReadyDelayedTime(context, kernelType, playWhenReadyDelayedTime);
        }
        // 立即播放
        else {
            initDecoder2(context);
        }
    }

    default void initDecoder2(Context context) {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            // 1
            long connectTimeout = args.getConnectTimout();
            @PlayerType.KernelType.Value
            int kernelType = args.getKernelType();
            startCheckConnectTimeout(kernelType, connectTimeout);
            // 2
            initOptions(context, args);
            startDecoder(context, args);
        } catch (Exception e) {
            LogUtil.log("VideoKernelApi => initDecoder2 => Exception " + e.getMessage());
        }
    }

    @Override
    default void callPlayWhenReadyDelayedTimeComplete(Context context) {
        initDecoder2(context);
    }

    /***********/

    void setSurface(Surface surface, int w, int h);
}