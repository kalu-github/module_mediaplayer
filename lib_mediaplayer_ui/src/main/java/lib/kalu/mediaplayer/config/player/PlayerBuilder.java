package lib.kalu.mediaplayer.config.player;


import lib.kalu.mediaplayer.buried.BuriedEvent;
import lib.kalu.mediaplayer.keycode.KeycodeApi;

/**
 * @description: 播放器全局配置
 * @date: 2021-05-12 14:43
 */

public final class PlayerBuilder {

    private boolean log;// 日志log
    private boolean kernelAlwaysRelease;
    private int connectTimeout;
    private boolean bufferingTimeoutRetry;
    @PlayerType.KernelType.Value
    private int externalAudioKernel; // 音频播放器内核
    @PlayerType.KernelType.Value
    private int kernel; // 视频播放器内核
    @PlayerType.RenderType.Value
    private int render; // 视频渲染类型
    @PlayerType.ScaleType
    private int scaleType; // 视频缩放比例
    private boolean checkMobileNetwork; // 监测手机网络环境
    private boolean fitMobileCutout; // 是否适配手机刘海屏，默认适配
    private boolean checkOrientation;  // 是否监听设备方向来切换全屏/半屏， 默认不开启
    private BuriedEvent buriedEvent;  // 埋点事件log
    private KeycodeApi keycodeApi; // 遥控code

    @PlayerType.SeekType.Value
    private int exoSeekParameters = PlayerType.SeekType.EXO_SEEK_DEFAULT;
    @PlayerType.FFmpegType.Value
    private int exoFFmpeg = PlayerType.FFmpegType.EXO_RENDERER_ONLY_MEDIACODEC;
    private boolean exoUseOkhttp;
    @PlayerType.CacheType
    private int exoCacheType = PlayerType.CacheType.NONE;
    private int exoCacheMax = 0;
    private String exoCacheDir = null;

    public boolean isExoUseOkhttp() {
        return exoUseOkhttp;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public boolean getBufferingTimeoutRetry() {
        return bufferingTimeoutRetry;
    }

    public int getExoFFmpeg() {
        return exoFFmpeg;
    }

    public int getExoSeekParameters() {
        return exoSeekParameters;
    }

    public int getExoCacheType() {
        return exoCacheType;
    }

    public int getExoCacheMax() {
        return exoCacheMax;
    }

    public String getExoCacheDir() {
        return exoCacheDir;
    }

    public boolean isLog() {
        return log;
    }

    public boolean isKernelAlwaysRelease() {
        return kernelAlwaysRelease;
    }

    public int getExtrAudioKernel() {
        return externalAudioKernel;
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
        kernelAlwaysRelease = builder.kernelAlwaysRelease;
        connectTimeout = builder.connectTimeout;
        bufferingTimeoutRetry = builder.bufferingTimeoutRetry;
        externalAudioKernel = builder.externalAudioKernel;
        kernel = builder.kernel;
        render = builder.render;
        scaleType = builder.scaleType;
        checkMobileNetwork = builder.checkMobileNetwork;
        fitMobileCutout = builder.fitMobileCutout;
        checkOrientation = builder.checkOrientation;
        buriedEvent = builder.buriedEvent;
        keycodeApi = builder.keycodeApi;
        exoSeekParameters = builder.exoSeekParameters;
        exoFFmpeg = builder.exoFFmpeg;
        exoUseOkhttp = builder.exoUseOkhttp;
        exoCacheType = builder.exoCacheType;
        exoCacheDir = builder.exoCacheDir;
        exoCacheMax = builder.exoCacheMax;
    }

    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.setLog(this.log);
        builder.setKernelAlwaysRelease(this.kernelAlwaysRelease);
        builder.setConnectTimeout(this.connectTimeout);
        builder.setBufferingTimeoutRetry(this.bufferingTimeoutRetry);
        builder.setExternalAudioKernel(this.externalAudioKernel);
        builder.setKernel(this.kernel);
        builder.setRender(this.render);
        builder.setScaleType(this.scaleType);
        builder.setCheckMobileNetwork(this.checkMobileNetwork);
        builder.setFitMobileCutout(this.fitMobileCutout);
        builder.setCheckOrientation(this.checkOrientation);
        builder.setBuriedEvent(this.buriedEvent);
        builder.setKeycodeApi(this.keycodeApi);
        builder.setExoSeekParameters(this.exoSeekParameters);
        builder.setExoFFmpeg(this.exoFFmpeg);
        builder.setExoUseOkhttp(this.exoUseOkhttp);
        builder.setExoCacheType(this.exoCacheType);
        builder.setExoCacheDir(this.exoCacheDir);
        builder.setExoCacheMax(this.exoCacheMax);
        return builder;
    }


    public final static class Builder {

        private boolean log = false;// 日志log
        private int connectTimeout = 10 * 1000;  // 连接超时
        private boolean bufferingTimeoutRetry = false; // 缓冲失败重试
        @PlayerType.KernelType.Value
        private int externalAudioKernel = PlayerType.KernelType.ANDROID; // 音频播放器内核
        @PlayerType.KernelType.Value
        private int kernel = PlayerType.KernelType.ANDROID; // 视频播放器内核
        private boolean kernelAlwaysRelease = false;
        @PlayerType.RenderType.Value
        private int render = PlayerType.RenderType.SURFACE_VIEW; // 视频渲染类型
        @PlayerType.ScaleType
        private int scaleType = PlayerType.ScaleType.SCREEN_SCALE_SCREEN_MATCH; // 视频缩放比例
        private boolean checkMobileNetwork = false; // 监测手机网络环境
        private boolean fitMobileCutout = true; // 是否适配手机刘海屏，默认适配
        private boolean checkOrientation = false;  // 是否监听设备方向来切换全屏/半屏， 默认不开启
        private BuriedEvent buriedEvent = null;  // 埋点事件log
        private KeycodeApi keycodeApi = null; // 遥控code


        @PlayerType.SeekType.Value
        private int exoSeekParameters = PlayerType.SeekType.EXO_SEEK_DEFAULT;
        @PlayerType.FFmpegType.Value
        private int exoFFmpeg = PlayerType.FFmpegType.EXO_RENDERER_ONLY_MEDIACODEC;
        /**
         * 本地视频缓存
         */
        @PlayerType.CacheType
        private int exoCacheType = PlayerType.CacheType.NONE;
        private int exoCacheMax = 0;
        private String exoCacheDir = null;
        private boolean exoUseOkhttp = true;


        public Builder setConnectTimeout(int v) {
            connectTimeout = v;
            return this;
        }

        public Builder setBufferingTimeoutRetry(boolean v) {
            bufferingTimeoutRetry = v;
            return this;
        }

        public Builder setExoUseOkhttp(boolean v) {
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

        public Builder setExoCacheType(@PlayerType.CacheType int v) {
            exoCacheType = v;
            return this;
        }

        public Builder setExoCacheMax(int v) {
            exoCacheMax = v;
            return this;
        }

        public Builder setExoCacheDir(String v) {
            exoCacheDir = v;
            return this;
        }

        public Builder setLog(boolean v) {
            log = v;
            return this;
        }

        public Builder setKernelAlwaysRelease(boolean v) {
            kernelAlwaysRelease = v;
            return this;
        }

        public Builder setExternalAudioKernel(@PlayerType.KernelType.Value int v) {
            externalAudioKernel = v;
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
