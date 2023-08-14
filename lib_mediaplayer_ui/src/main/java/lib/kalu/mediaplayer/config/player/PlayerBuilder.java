package lib.kalu.mediaplayer.config.player;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

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
    private boolean exoUseOkhttp;
    private int exoUseOkhttpTimeoutSeconds;
    @PlayerType.KernelType.Value
    private int videoKernel; // 播放器内核
    @PlayerType.RenderType.Value
    private int videoRender; // 渲染类型
    @PlayerType.ScaleType
    private int videoScaleType; // 视频缩放比例
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

    public boolean isExoUseOkhttp() {
        return exoUseOkhttp;
    }

    public int getExoUseOkhttpTimeoutSeconds() {
        return exoUseOkhttpTimeoutSeconds;
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

    public int getVideoKernel() {
        return videoKernel;
    }

    public int getVideoRender() {
        return videoRender;
    }

    @PlayerType.ScaleType.Value
    public int getVideoScaleType() {
        return videoScaleType;
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
        exoUseOkhttp = builder.exoUseOkhttp;
        exoUseOkhttpTimeoutSeconds = builder.exoUseOkhttpTimeoutSeconds;
        videoKernel = builder.videoKernel;
        videoRender = builder.videoRender;
        videoScaleType = builder.videoScaleType;
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
        builder.setExoUseOkhttp(this.exoUseOkhttp);
        builder.setExoUseOkhttpTimeoutSeconds(this.exoUseOkhttpTimeoutSeconds);
        builder.setVideoKernel(this.videoKernel);
        builder.setVideoRender(this.videoRender);
        builder.setVideoScaleType(this.videoScaleType);
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
        private boolean exoUseOkhttp = true;
        private int exoUseOkhttpTimeoutSeconds = 4;
        @PlayerType.KernelType.Value
        private int videoKernel = PlayerType.KernelType.ANDROID; // 播放器内核
        @PlayerType.RenderType.Value
        private int videoRender = PlayerType.RenderType.TEXTURE_VIEW; // 渲染类型
        @PlayerType.ScaleType
        private int videoScaleType = PlayerType.ScaleType.SCREEN_SCALE_SCREEN_MATCH; // 视频缩放比例
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

        public Builder setExoUseOkhttpTimeoutSeconds(@NonNull int v) {
            exoUseOkhttpTimeoutSeconds = v;
            return this;
        }

        public Builder setExoUseOkhttp(@NonNull boolean v) {
            exoUseOkhttp = v;
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

        public Builder setVideoKernel(@PlayerType.KernelType.Value int v) {
            videoKernel = v;
            return this;
        }

        public Builder setVideoRender(@PlayerType.RenderType.Value int v) {
            videoRender = v;
            return this;
        }

        public Builder setVideoScaleType(@PlayerType.ScaleType.Value int v) {
            videoScaleType = v;
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
