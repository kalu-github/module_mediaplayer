package lib.kalu.mediaplayer.args;


import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.proxy.Proxy;
import lib.kalu.mediaplayer.proxy.ProxyBuried;
import lib.kalu.mediaplayer.proxy.ProxyUrl;
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

    // 旋转角度
    @PlayerType.RotationType.Value
    private int rotation;

    public int getRotation() {
        return rotation;
    }

    @PlayerType.CacheType.Value
    private int cacheType;

    @PlayerType.CacheType.Value
    public int getCacheType() {
        return cacheType;
    }

    @PlayerType.CacheLocalType.Value
    private int cacheLocal;

    @PlayerType.CacheLocalType.Value
    public int getCacheLocal() {
        return cacheLocal;
    }

    private int cacheSize;

    public int getCacheSizeType() {
        return cacheSize;
    }

    private String cacheDirName;

    public String getCacheDirName() {
        return cacheDirName;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public boolean getBufferingTimeoutRetry() {
        return bufferingTimeoutRetry;
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

    @PlayerType.SeekType.Value
    private int seekType;

    public int getSeekType() {
        return seekType;
    }

    // 代理
    private Proxy proxy;

    public Proxy getProxy() {
        return proxy;
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
        cacheType = builder.cacheType;
        cacheLocal = builder.cacheLocal;
        cacheSize = builder.cacheSize;
        cacheDirName = builder.cacheDirName;
        seekType = builder.seekType;
        rotation = builder.rotation;
        proxy = builder.proxy;
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
        builder.setCacheType(this.cacheType);
        builder.setCacheLocal(this.cacheLocal);
        builder.setCacheSize(this.cacheSize);
        builder.setCacheDirName(this.cacheDirName);
        builder.setSeekType(this.seekType);
        builder.setRotation(this.rotation);
        builder.setProxy(this.proxy);
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

        // 旋转角度
        @PlayerType.RotationType.Value
        private int rotation = PlayerType.RotationType.DEFAULT;

        public Builder setRotation(int v) {
            this.rotation = v;
            return this;
        }

        // 视频缓存
        @PlayerType.CacheType.Value
        private int cacheType = PlayerType.CacheType.DEFAULT;

        public Builder setCacheType(@PlayerType.CacheType int v) {
            cacheType = v;
            return this;
        }

        // 缓存位置
        @PlayerType.CacheLocalType.Value
        private int cacheLocal = PlayerType.CacheLocalType.DEFAULT;

        public Builder setCacheLocal(@PlayerType.CacheLocalType.Value int v) {
            cacheLocal = v;
            return this;
        }

        // 缓存大小
        private int cacheSize = 500;

        public Builder setCacheSize(int v) {
            cacheSize = v;
            return this;
        }

        // 缓存文件夹名
        private String cacheDirName = "video_cache";

        public Builder setCacheDirName(@Nullable String v) {
            if (null != v && !v.isEmpty()) {
                cacheDirName = v;
            }
            return this;
        }

        // 快进参数
        @PlayerType.SeekType.Value
        private int seekType = PlayerType.SeekType.DEFAULT;

        public Builder setSeekType(@PlayerType.SeekType.Value int v) {
            seekType = v;
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

        public Builder setDecoderType(@PlayerType.DecoderType int v) {
            decoderType = v;
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

        // 代理
        private Proxy proxy = null;

        public Builder setProxy(Proxy v) {
            this.proxy = v;
            return this;
        }

        public PlayerArgs build() {
            return new PlayerArgs(this);
        }
    }
}
