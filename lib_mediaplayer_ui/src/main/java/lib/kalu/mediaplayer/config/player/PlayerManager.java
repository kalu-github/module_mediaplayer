package lib.kalu.mediaplayer.config.player;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.buried.BuriedEvent;
import lib.kalu.mediaplayer.keycode.KeycodeApi;

@Keep
public final class PlayerManager {

    private PlayerBuilder mConfig = null;
    private PlayerBuilder.Builder mPlayerBuilder;

    /***************/

    private static final class Holder {
        private static final PlayerManager mInstance = new PlayerManager();
    }

    public static PlayerManager getInstance() {
        return Holder.mInstance;
    }

    /***************/

    private PlayerManager() {
        mPlayerBuilder = new PlayerBuilder.Builder();
    }

    public PlayerManager setSeekHelp(boolean v) {
        mPlayerBuilder.setSeekHelp(v);
        return this;
    }

    public PlayerManager setExoUseOkhttp(@NonNull boolean v) {
        mPlayerBuilder.setExoUseOkhttp(v);
        return this;
    }

    public PlayerManager setExoUseOkhttpTimeoutSeconds(@NonNull int v) {
        this.mPlayerBuilder.setExoUseOkhttpTimeoutSeconds(v);
        return this;
    }

    public PlayerManager setBufferingTimeoutSeconds(@NonNull int v) {
        this.mPlayerBuilder.setBufferingTimeoutSeconds(v);
        return this;
    }

    public PlayerManager setBufferingTimeoutRetry(@NonNull boolean v) {
        this.mPlayerBuilder.setBufferingTimeoutRetry(v);
        return this;
    }

    public PlayerManager setExoFFmpeg(@PlayerType.FFmpegType int v) {
        mPlayerBuilder.setExoFFmpeg(v);
        return this;
    }

    public PlayerManager setExoSeekParameters(@PlayerType.SeekType int v) {
        mPlayerBuilder.setExoSeekParameters(v);
        return this;
    }

    public PlayerManager setCacheType(@PlayerType.CacheType int v) {
        mPlayerBuilder.setCacheType(v);
        return this;
    }

    public PlayerManager setCacheMax(int v) {
        mPlayerBuilder.setCacheMax(v);
        return this;
    }

    public PlayerManager setCacheDir(String v) {
        mPlayerBuilder.setCacheDir(v);
        return this;
    }

    public PlayerManager setLog(boolean v) {
        mPlayerBuilder.setLog(v);
        return this;
    }

    public PlayerManager setAudioKernel(@PlayerType.AudioKernelType.Value int v) {
        mPlayerBuilder.setVideoKernel(v);
        return this;
    }

    public PlayerManager setVideoKernel(@PlayerType.VideoKernelType.Value int v) {
        mPlayerBuilder.setVideoKernel(v);
        return this;
    }

    public PlayerManager setVideoRender(@PlayerType.VideoRenderType.Value int v) {
        mPlayerBuilder.setVideoRender(v);
        return this;
    }

    public PlayerManager setVideoScaleType(@PlayerType.ScaleType.Value int v) {
        mPlayerBuilder.setVideoScaleType(v);
        updateConfig(false);
        return this;
    }

    public PlayerManager setCheckMobileNetwork(boolean v) {
        mPlayerBuilder.setCheckMobileNetwork(v);
        return this;
    }

    public PlayerManager setFitMobileCutout(boolean v) {
        mPlayerBuilder.setFitMobileCutout(v);
        return this;
    }

    public PlayerManager setCheckOrientation(boolean v) {
        mPlayerBuilder.setCheckOrientation(v);
        return this;
    }

    public PlayerManager setBuriedEvent(BuriedEvent buriedEvent) {
        mPlayerBuilder.setBuriedEvent(buriedEvent);
        return this;
    }

    public PlayerManager setKeycodeApi(KeycodeApi keycodeApi) {
        mPlayerBuilder.setKeycodeApi(keycodeApi);
        return this;
    }

    public void build() {
        mConfig = mPlayerBuilder.build();
    }

    public PlayerBuilder getConfig() {
        updateConfig(true);
        return mConfig;
    }

    private void updateConfig(boolean check) {
        if (check) {
            if (null == mConfig) {
                mConfig = mPlayerBuilder.build();
            }
        } else {
            mConfig = mPlayerBuilder.build();
        }
    }

    /***************/
}
