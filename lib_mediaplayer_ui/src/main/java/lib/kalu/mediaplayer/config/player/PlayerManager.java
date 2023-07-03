package lib.kalu.mediaplayer.config.player;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.buried.BuriedEvent;
import lib.kalu.mediaplayer.keycode.KeycodeApi;

@Keep
public final class PlayerManager {

    private PlayerBuilder mConfig;
    private PlayerBuilder.Builder mPlayerBuilder;

    private PlayerManager() {
        mPlayerBuilder = new PlayerBuilder.Builder();
    }

    private static final class Holder {
        private static final PlayerManager INSTANCE = new PlayerManager();
    }

    public static PlayerManager getInstance() {
        return PlayerManager.Holder.INSTANCE;
    }

    /***************/

    public PlayerBuilder.Builder setSeekHelp(boolean v) {
        mPlayerBuilder.setSeekHelp(v);
        return mPlayerBuilder;
    }

    public PlayerBuilder.Builder setExoFFmpeg(@PlayerType.FFmpegType int v) {
        mPlayerBuilder.setExoFFmpeg(v);
        return mPlayerBuilder;
    }

    public PlayerBuilder.Builder setExoSeekParameters(@PlayerType.SeekType int v) {
        mPlayerBuilder.setExoSeekParameters(v);
        return mPlayerBuilder;
    }

    public PlayerBuilder.Builder setCacheType(@PlayerType.CacheType int v) {
        mPlayerBuilder.setCacheType(v);
        return mPlayerBuilder;
    }

    public PlayerBuilder.Builder setCacheMax(int v) {
        mPlayerBuilder.setCacheMax(v);
        return mPlayerBuilder;
    }

    public PlayerBuilder.Builder setCacheDir(String v) {
        mPlayerBuilder.setCacheDir(v);
        return mPlayerBuilder;
    }

    public PlayerBuilder.Builder setLog(boolean v) {
        mPlayerBuilder.setLog(v);
        return mPlayerBuilder;
    }

    public PlayerBuilder.Builder setKernel(@PlayerType.KernelType.Value int v) {
        mPlayerBuilder.setKernel(v);
        return mPlayerBuilder;
    }

    public PlayerBuilder.Builder setRender(@PlayerType.RenderType.Value int v) {
        mPlayerBuilder.setRender(v);
        return mPlayerBuilder;
    }

    public PlayerBuilder.Builder setScaleType(@PlayerType.ScaleType.Value int v) {
        mPlayerBuilder.setScaleType(v);
        return mPlayerBuilder;
    }

    public PlayerBuilder.Builder setCheckMobileNetwork(boolean v) {
        mPlayerBuilder.setCheckMobileNetwork(v);
        return mPlayerBuilder;
    }

    public PlayerBuilder.Builder setFitMobileCutout(boolean v) {
        mPlayerBuilder.setFitMobileCutout(v);
        return mPlayerBuilder;
    }

    public PlayerBuilder.Builder setCheckOrientation(boolean v) {
        mPlayerBuilder.setCheckOrientation(v);
        return mPlayerBuilder;
    }

    public PlayerBuilder.Builder setBuriedEvent(BuriedEvent buriedEvent) {
        mPlayerBuilder.setBuriedEvent(buriedEvent);
        return mPlayerBuilder;
    }

    public PlayerBuilder.Builder setKeycodeApi(KeycodeApi keycodeApi) {
        mPlayerBuilder.setKeycodeApi(keycodeApi);
        return mPlayerBuilder;
    }

    public void build() {
        mConfig = mPlayerBuilder.build();
    }

    public PlayerBuilder getConfig() {
        if (null == mConfig) {
            mConfig = mPlayerBuilder.build();
        }
        return mConfig;
    }

    /***************/
}
