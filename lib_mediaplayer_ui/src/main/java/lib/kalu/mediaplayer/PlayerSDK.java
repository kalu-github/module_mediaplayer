package lib.kalu.mediaplayer;


import lib.kalu.mediaplayer.args.PlayerArgs;
import lib.kalu.mediaplayer.buried.BuriedEvent;
import lib.kalu.mediaplayer.type.PlayerType;


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

    public PlayerSDK setConnectTimeout(int v) {
        this.mPlayerBuilder.setConnectTimeout(v);
        return this;
    }

    public PlayerSDK setBufferingTimeoutRetry(boolean v) {
        this.mPlayerBuilder.setBufferingTimeoutRetry(v);
        return this;
    }

    public PlayerSDK setSeekType(@PlayerType.SeekType.Value int v) {
        mPlayerBuilder.setSeekType(v);
        return this;
    }

    public PlayerSDK setCacheType(@PlayerType.CacheType.Value int v) {
        mPlayerBuilder.setCacheType(v);
        return this;
    }

    public PlayerSDK setCacheLocal(@PlayerType.CacheLocalType.Value int v) {
        mPlayerBuilder.setCacheLocal(v);
        return this;
    }

    public PlayerSDK setCacheSize(int v) {
        mPlayerBuilder.setCacheSize(v);
        return this;
    }

    public PlayerSDK setCacheDirName(String v) {
        mPlayerBuilder.setCacheDirName(v);
        return this;
    }

    public PlayerSDK setLog(boolean v) {
        mPlayerBuilder.setLog(v);
        return this;
    }

    public PlayerSDK setInitRelease(boolean v) {
        mPlayerBuilder.setInitRelease(v);
        return this;
    }

    public PlayerSDK setSupportAutoRelease(boolean v) {
        mPlayerBuilder.setSupportAutoRelease(v);
        return this;
    }

    public PlayerSDK setExternalAudioKernel(@PlayerType.KernelType.Value int v) {
        mPlayerBuilder.setExternalAudioKernel(v);
        return this;
    }

    public PlayerSDK setKernelType(@PlayerType.KernelType.Value int v) {
        mPlayerBuilder.setKernelType(v);
        return this;
    }

    public PlayerSDK setRenderType(@PlayerType.RenderType.Value int v) {
        mPlayerBuilder.setRenderType(v);
        return this;
    }

    public PlayerSDK setDecoderType(@PlayerType.DecoderType.Value int v) {
        mPlayerBuilder.setDecoderType(v);
        return this;
    }

    public PlayerSDK setScaleType(@PlayerType.ScaleType.Value int v) {
        mPlayerBuilder.setScaleType(v);
        updatePlayerBuilder(false);
        return this;
    }

    public PlayerSDK setBuriedEvent(BuriedEvent v) {
        mPlayerBuilder.setBuriedEvent(v);
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
