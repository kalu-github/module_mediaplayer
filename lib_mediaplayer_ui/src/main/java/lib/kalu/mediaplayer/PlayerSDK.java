package lib.kalu.mediaplayer;


import lib.kalu.mediaplayer.args.PlayerArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.buried.BuriedEvent;
import lib.kalu.mediaplayer.keycode.KeycodeApi;


public final class PlayerSDK {

    private PlayerArgs mConfig = null;
    private PlayerArgs.Builder mPlayerBuilder;

    /***************/

    private static final class Holder {
        private static final PlayerSDK mInstance = new PlayerSDK();
    }

    public static PlayerSDK init() {
        return Holder.mInstance;
    }

    /***************/

    private PlayerSDK() {
        mPlayerBuilder = new PlayerArgs.Builder();
    }

    public PlayerSDK setExoUseOkhttp(boolean v) {
        mPlayerBuilder.setExoUseOkhttp(v);
        return this;
    }

    public PlayerSDK setConnectTimeout(int v) {
        this.mPlayerBuilder.setConnectTimeout(v);
        return this;
    }

    public PlayerSDK setBufferingTimeoutRetry(boolean v) {
        this.mPlayerBuilder.setBufferingTimeoutRetry(v);
        return this;
    }

    public PlayerSDK setExoFFmpeg(@PlayerType.FFmpegType int v) {
        mPlayerBuilder.setExoFFmpeg(v);
        return this;
    }

    public PlayerSDK setExoSeekParameters(@PlayerType.SeekType int v) {
        mPlayerBuilder.setExoSeekParameters(v);
        return this;
    }

    public PlayerSDK setExoCacheType(@PlayerType.CacheType int v) {
        mPlayerBuilder.setExoCacheType(v);
        return this;
    }

    public PlayerSDK setExoCacheMax(int v) {
        mPlayerBuilder.setExoCacheMax(v);
        return this;
    }

    public PlayerSDK setExoCacheDir(String v) {
        mPlayerBuilder.setExoCacheDir(v);
        return this;
    }

    public PlayerSDK setLog(boolean v) {
        mPlayerBuilder.setLog(v);
        return this;
    }

    public PlayerSDK setKernelAlwaysRelease(boolean v) {
        mPlayerBuilder.setKernelAlwaysRelease(v);
        return this;
    }

    public PlayerSDK setExternalAudioKernel(@PlayerType.KernelType.Value int v) {
        mPlayerBuilder.setExternalAudioKernel(v);
        return this;
    }

    public PlayerSDK setKernel(@PlayerType.KernelType.Value int v) {
        mPlayerBuilder.setKernel(v);
        return this;
    }

    public PlayerSDK setRender(@PlayerType.RenderType.Value int v) {
        mPlayerBuilder.setRender(v);
        return this;
    }

    public PlayerSDK setScaleType(@PlayerType.ScaleType.Value int v) {
        mPlayerBuilder.setScaleType(v);
        updatePlayerBuilder(false);
        return this;
    }

    public PlayerSDK setCheckMobileNetwork(boolean v) {
        mPlayerBuilder.setCheckMobileNetwork(v);
        return this;
    }

    public PlayerSDK setFitMobileCutout(boolean v) {
        mPlayerBuilder.setFitMobileCutout(v);
        return this;
    }

    public PlayerSDK setCheckOrientation(boolean v) {
        mPlayerBuilder.setCheckOrientation(v);
        return this;
    }

    public PlayerSDK setBuriedEvent(BuriedEvent buriedEvent) {
        mPlayerBuilder.setBuriedEvent(buriedEvent);
        return this;
    }

    public PlayerSDK setKeycodeApi(KeycodeApi keycodeApi) {
        mPlayerBuilder.setKeycodeApi(keycodeApi);
        return this;
    }

    public void build() {
        mConfig = mPlayerBuilder.build();
    }

    public PlayerArgs getPlayerBuilder() {
        updatePlayerBuilder(true);
        return mConfig;
    }

    private void updatePlayerBuilder(boolean check) {
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
