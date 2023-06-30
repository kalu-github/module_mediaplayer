package lib.kalu.mediaplayer.config.player;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.buried.BuriedEvent;
import lib.kalu.mediaplayer.keycode.KeycodeApi;

/**
 * @description: 播放器全局配置
 * @date: 2021-05-12 14:43
 */
@Keep
public final class PlayerBuilder {

    private boolean log;// 日志log
    private boolean seekHelp = false; // 解决seek不准确
    @PlayerType.SeekType.Value
    private int exoSeekParameters = PlayerType.SeekType.EXO_SEEK_DEFAULT;
    @PlayerType.FFmpegType.Value
    private int exoFFmpeg = PlayerType.FFmpegType.EXO_RENDERER_ONLY_MEDIACODEC;
    @PlayerType.KernelType.Value
    private int kernel; // 播放器内核
    @PlayerType.RenderType.Value
    private int render; // 渲染类型
    @PlayerType.ScaleType
    private int scaleType; // 视频缩放比例
    private boolean checkMobileNetwork; // 监测手机网络环境
    private boolean fitMobileCutout; // 是否适配手机刘海屏，默认适配
    private boolean checkOrientation;  // 是否监听设备方向来切换全屏/半屏， 默认不开启
    private BuriedEvent buriedEvent;  // 埋点事件log
    private KeycodeApi keycodeApi; // 遥控code

    /**
     * 本地视频缓存
     */
    @PlayerType.CacheType
    private int cacheType = PlayerType.CacheType.NONE;
    private int cacheMax = 0;
    private String cacheDir = null;

    public boolean isSeekHelp() {
        return seekHelp;
    }

    public int getExoFFmpeg() {
        return exoFFmpeg;
    }

    public int getExoSeekParameters() {
        return exoSeekParameters;
    }

    public int getCacheType() {
        return cacheType;
    }

    public int getCacheMax() {
        return cacheMax;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public boolean isLog() {
        return log;
    }

    public int getKernel() {
        return kernel;
    }

    public int getRender() {
        return render;
    }

    @PlayerType.ScaleType.Value
    public int getScaleType() {
        return scaleType;
    }

    public boolean isCheckMobileNetwork() {
        return checkMobileNetwork;
    }

    public boolean isFitMobileCutout() {
        return fitMobileCutout;
    }

    public boolean isCheckOrientation() {
        return checkOrientation;
    }

    public BuriedEvent getBuriedEvent() {
        return buriedEvent;
    }

    public KeycodeApi getKeycodeApi() {
        return keycodeApi;
    }

    /****************/

    private PlayerBuilder(Builder builder) {
        log = builder.log;
        seekHelp = builder.seekHelp;
        exoSeekParameters = builder.exoSeekParameters;
        exoFFmpeg = builder.exoFFmpeg;
        kernel = builder.kernel;
        render = builder.render;
        scaleType = builder.scaleType;
        checkMobileNetwork = builder.checkMobileNetwork;
        fitMobileCutout = builder.fitMobileCutout;
        checkOrientation = builder.checkOrientation;
        buriedEvent = builder.buriedEvent;
        keycodeApi = builder.keycodeApi;
        cacheType = builder.cacheType;
        cacheDir = builder.cacheDir;
        cacheMax = builder.cacheMax;
    }

    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.setLog(this.log);
        builder.setSeekHelp(this.seekHelp);
        builder.setExoSeekParameters(this.exoSeekParameters);
        builder.setExoFFmpeg(this.exoFFmpeg);
        builder.setKernel(this.kernel);
        builder.setRender(this.render);
        builder.setScaleType(this.scaleType);
        builder.setCheckMobileNetwork(this.checkMobileNetwork);
        builder.setFitMobileCutout(this.fitMobileCutout);
        builder.setCheckOrientation(this.checkOrientation);
        builder.setBuriedEvent(this.buriedEvent);
        builder.setKeycodeApi(this.keycodeApi);
        builder.setCacheType(this.cacheType);
        builder.setCacheDir(this.cacheDir);
        builder.setCacheMax(this.cacheMax);
        return builder;
    }

    @Keep
    public final static class Builder {

        private boolean log = false;// 日志log
        private boolean seekHelp = false; // 解决seek不准确
        @PlayerType.SeekType.Value
        private int exoSeekParameters = PlayerType.SeekType.EXO_SEEK_DEFAULT;
        @PlayerType.FFmpegType.Value
        private int exoFFmpeg = PlayerType.FFmpegType.EXO_RENDERER_ONLY_MEDIACODEC;
        @PlayerType.KernelType.Value
        private int kernel = PlayerType.KernelType.ANDROID; // 播放器内核
        @PlayerType.RenderType.Value
        private int render = PlayerType.RenderType.TEXTURE_VIEW; // 渲染类型
        @PlayerType.ScaleType
        private int scaleType = PlayerType.ScaleType.SCREEN_SCALE_DEFAULT; // 视频缩放比例
        private boolean checkMobileNetwork = false; // 监测手机网络环境
        private boolean fitMobileCutout = true; // 是否适配手机刘海屏，默认适配
        private boolean checkOrientation = false;  // 是否监听设备方向来切换全屏/半屏， 默认不开启
        private BuriedEvent buriedEvent = null;  // 埋点事件log
        private KeycodeApi keycodeApi = null; // 遥控code

        /**
         * 本地视频缓存
         */
        @PlayerType.CacheType
        private int cacheType = PlayerType.CacheType.NONE;
        private int cacheMax = 0;
        private String cacheDir = null;

        public Builder setSeekHelp(boolean v) {
            seekHelp = v;
            return this;
        }

        public Builder setExoFFmpeg(@PlayerType.FFmpegType int v) {
            exoFFmpeg = v;
            return this;
        }

        public Builder setExoSeekParameters(@PlayerType.SeekType int v) {
            exoSeekParameters = v;
            return this;
        }

        public Builder setCacheType(@PlayerType.CacheType int v) {
            cacheType = v;
            return this;
        }

        public Builder setCacheMax(int v) {
            cacheMax = v;
            return this;
        }

        public Builder setCacheDir(String v) {
            cacheDir = v;
            return this;
        }

        public Builder setLog(boolean v) {
            log = v;
            return this;
        }

        public Builder setKernel(@PlayerType.KernelType.Value int v) {
            kernel = v;
            return this;
        }

        public Builder setRender(@PlayerType.RenderType.Value int v) {
            render = v;
            return this;
        }

        public Builder setScaleType(@PlayerType.ScaleType.Value int v) {
            scaleType = v;
            return this;
        }

        public Builder setCheckMobileNetwork(boolean v) {
            checkMobileNetwork = v;
            return this;
        }

        public Builder setFitMobileCutout(boolean v) {
            fitMobileCutout = v;
            return this;
        }

        public Builder setCheckOrientation(boolean v) {
            checkOrientation = v;
            return this;
        }

        public Builder setBuriedEvent(BuriedEvent buriedEvent) {
            this.buriedEvent = buriedEvent;
            return this;
        }

        public Builder setKeycodeApi(KeycodeApi keycodeApi) {
            this.keycodeApi = keycodeApi;
            return this;
        }

        public PlayerBuilder build() {
            return new PlayerBuilder(this);
        }
    }
}
