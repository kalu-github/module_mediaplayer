package lib.kalu.mediaplayer.args;


import lib.kalu.mediaplayer.buried.BuriedEvent;
import lib.kalu.mediaplayer.type.PlayerType;

/**
 * @description: 播放器全局配置
 * @date: 2021-05-12 14:43
 */

public final class PlayerArgs {

    private boolean log;// 日志log
    private boolean initRelease;
    private boolean supportAutoRelease; // 自动销毁
    private int connectTimeout;
    private boolean bufferingTimeoutRetry;
    @PlayerType.KernelType.Value
    private int externalAudioKernel; // 音频播放器内核
    @PlayerType.KernelType.Value
    private int kernelType; // 视频播放器内核
    @PlayerType.RenderType.Value
    private int renderType; // 视频渲染类型
    @PlayerType.DecoderType.Value
    private int decoderType; // 解码器类型
    @PlayerType.ScaleType
    private int scaleType; // 视频缩放比例
    private boolean checkMobileNetwork; // 监测手机网络环境
    private boolean fitMobileCutout; // 是否适配手机刘海屏，默认适配
    private boolean checkOrientation;  // 是否监听设备方向来切换全屏/半屏， 默认不开启
    private BuriedEvent buriedEvent;  // 埋点事件log

    @PlayerType.ExoSeekType.Value
    private int exoSeekType;
    private boolean exoUseOkhttp;
    @PlayerType.CacheType
    private int exoCacheType = PlayerType.CacheType.NONE;
    private int exoCacheMax = 0;
    private String exoCacheDir = null;

    // exo ffmpeg
    private boolean exoUseFFmpeg = false;

    public boolean isExoUseFFmpeg() {
        return exoUseFFmpeg;
    }

    // ijk mediacodec
    private boolean ijkUseMediaCodec;

    public boolean isIjkUseMediaCodec() {
        return ijkUseMediaCodec;
    }

    // 旋转角度
    @PlayerType.RotationType.Value
    private int rotation;

    public int getRotation() {
        return rotation;
    }

    // 试看时长
    private long trySeeDuration;

    public long getTrySeeDuration() {
        return trySeeDuration;
    }

    public boolean isExoUseOkhttp() {
        return exoUseOkhttp;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public boolean getBufferingTimeoutRetry() {
        return bufferingTimeoutRetry;
    }

    public int getExoSeekType() {
        return exoSeekType;
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

    public boolean isInitRelease() {
        return initRelease;
    }

    public boolean isSupportAutoRelease() {
        return supportAutoRelease;
    }

    public int getExtrAudioKernel() {
        return externalAudioKernel;
    }

    @PlayerType.KernelType.Value
    public int getKernelType() {
        return kernelType;
    }

    @PlayerType.RenderType.Value
    public int getRenderType() {
        return renderType;
    }

    @PlayerType.DecoderType.Value
    public int getDecoderType() {
        return decoderType;
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

    /****************/

    private PlayerArgs(Builder builder) {
        log = builder.log;
        initRelease = builder.initRelease;
        supportAutoRelease = builder.supportAutoRelease;
        connectTimeout = builder.connectTimeout;
        bufferingTimeoutRetry = builder.bufferingTimeoutRetry;
        externalAudioKernel = builder.externalAudioKernel;
        kernelType = builder.kernelType;
        renderType = builder.renderType;
        decoderType = builder.decoderType;
        scaleType = builder.scaleType;
        checkMobileNetwork = builder.checkMobileNetwork;
        fitMobileCutout = builder.fitMobileCutout;
        checkOrientation = builder.checkOrientation;
        buriedEvent = builder.buriedEvent;
        exoSeekType = builder.exoSeekType;
        exoUseOkhttp = builder.exoUseOkhttp;
        exoCacheType = builder.exoCacheType;
        exoCacheDir = builder.exoCacheDir;
        exoCacheMax = builder.exoCacheMax;
        exoUseFFmpeg = builder.exoUseFFmpeg;
        trySeeDuration = builder.trySeeDuration;
        rotation = builder.rotation;
        ijkUseMediaCodec = builder.ijkUseMediaCodec;
    }

    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.setLog(this.log);
        builder.setInitRelease(this.initRelease);
        builder.setSupportAutoRelease(this.supportAutoRelease);
        builder.setConnectTimeout(this.connectTimeout);
        builder.setBufferingTimeoutRetry(this.bufferingTimeoutRetry);
        builder.setExternalAudioKernel(this.externalAudioKernel);
        builder.setKernelType(this.kernelType);
        builder.setRenderType(this.renderType);
        builder.setDecoderType(this.decoderType);
        builder.setScaleType(this.scaleType);
        builder.setCheckMobileNetwork(this.checkMobileNetwork);
        builder.setFitMobileCutout(this.fitMobileCutout);
        builder.setCheckOrientation(this.checkOrientation);
        builder.setBuriedEvent(this.buriedEvent);
        builder.setExoSeekType(this.exoSeekType);
        builder.setExoUseOkhttp(this.exoUseOkhttp);
        builder.setExoCacheType(this.exoCacheType);
        builder.setExoCacheDir(this.exoCacheDir);
        builder.setExoCacheMax(this.exoCacheMax);
        builder.setExoUseFFmpeg(this.exoUseFFmpeg);
        builder.setTrySeeDuration(this.trySeeDuration);
        builder.setRotation(this.rotation);
        builder.setIjkUseMediaCodec(this.ijkUseMediaCodec);
        return builder;
    }


    public final static class Builder {

        private boolean log = false;// 日志log
        private boolean initRelease = false;
        private boolean supportAutoRelease = true;
        private int connectTimeout = 20 * 1000;  // 连接超时
        private boolean bufferingTimeoutRetry = false; // 缓冲失败重试
        @PlayerType.KernelType.Value
        private int externalAudioKernel = PlayerType.KernelType.DEFAULT; // 音频播放器内核
        @PlayerType.KernelType.Value
        private int kernelType = PlayerType.KernelType.DEFAULT; // 视频播放器内核
        @PlayerType.RenderType.Value
        private int renderType = PlayerType.RenderType.DEFAULT; // 视频渲染类型
        @PlayerType.DecoderType.Value
        private int decoderType = PlayerType.DecoderType.DEFAULT; // 解码器类型
        @PlayerType.ScaleType
        private int scaleType = PlayerType.ScaleType.DEFAULT; // 视频缩放比例
        private boolean checkMobileNetwork = false; // 监测手机网络环境
        private boolean fitMobileCutout = true; // 是否适配手机刘海屏，默认适配
        private boolean checkOrientation = false;  // 是否监听设备方向来切换全屏/半屏， 默认不开启
        private BuriedEvent buriedEvent = null;  // 埋点事件log


        @PlayerType.ExoSeekType.Value
        private int exoSeekType = PlayerType.ExoSeekType.DEFAULT;
        /**
         * 本地视频缓存
         */
        @PlayerType.CacheType.Value
        private int exoCacheType = PlayerType.CacheType.NONE;
        private int exoCacheMax = 0;
        private String exoCacheDir = null;
        private boolean exoUseOkhttp = true;


        // exo ffmpeg
        private boolean exoUseFFmpeg = false;

        public Builder setExoUseFFmpeg(boolean v) {
            this.exoUseFFmpeg = v;
            return this;
        }

        // ijk mediacodec
        private boolean ijkUseMediaCodec = false;

        public Builder setIjkUseMediaCodec(boolean v) {
            this.ijkUseMediaCodec = v;
            return this;
        }

        // 旋转角度
        @PlayerType.RotationType.Value
        private int rotation = PlayerType.RotationType.DEFAULT;

        public Builder setRotation(int v) {
            this.rotation = v;
            return this;
        }

        // 试看时长
        private long trySeeDuration = 0L;

        public Builder setTrySeeDuration(long v) {
            this.trySeeDuration = v;
            return this;
        }

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

        public Builder setDecoderType(@PlayerType.DecoderType int v) {
            decoderType = v;
            return this;
        }

        public Builder setExoSeekType(@PlayerType.ExoSeekType int v) {
            exoSeekType = v;
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

        public Builder setInitRelease(boolean v) {
            initRelease = v;
            return this;
        }

        public Builder setSupportAutoRelease(boolean v) {
            supportAutoRelease = v;
            return this;
        }

        public Builder setExternalAudioKernel(@PlayerType.KernelType.Value int v) {
            externalAudioKernel = v;
            return this;
        }

        public Builder setKernelType(@PlayerType.KernelType.Value int v) {
            kernelType = v;
            return this;
        }

        public Builder setRenderType(@PlayerType.RenderType.Value int v) {
            renderType = v;
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

        public PlayerArgs build() {
            return new PlayerArgs(this);
        }
    }
}
