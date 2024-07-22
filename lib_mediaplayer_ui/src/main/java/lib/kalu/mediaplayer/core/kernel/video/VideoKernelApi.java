package lib.kalu.mediaplayer.core.kernel.video;

import android.content.Context;
import android.view.Surface;

import lib.kalu.mediaplayer.args.StartArgs;


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

        // 网络超时
        try {
            int type = args.getKernelType();
            long connectTimeout = args.getConnectTimout();
            startCheckConnectTimeout(type, connectTimeout);
        } catch (Exception e) {
        }

        initOptions(context, args);
        startDecoder(context, args);
    }

    /***********/

//    default void setDisplay(SurfaceHolder surfaceHolder){
//    }

    void setSurface(Surface surface, int w, int h);
}