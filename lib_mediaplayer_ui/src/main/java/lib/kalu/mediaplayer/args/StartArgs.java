package lib.kalu.mediaplayer.args;


import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.type.PlayerType;

public class StartArgs {
    private int exoCacheType;

    public int getExoCacheType() {
        return exoCacheType;
    }

    private int exoCacheMax;

    public int getExoCacheMax() {
        return exoCacheMax;
    }

    private String exoCacheDir;

    public String getExoCacheDir() {
        return exoCacheDir;
    }

    @PlayerType.ExoFFmpegType.Value
    private int exoFFmpegType;

    public int getExoFFmpegType() {
        return exoFFmpegType;
    }

    @PlayerType.ExoSeekType
    private int exoSeekType;

    public int getExoSeekType() {
        return exoSeekType;
    }

    // 视频渲染类型
    @PlayerType.RenderType.Value
    private int renderType;

    public int getRenderType() {
        return renderType;
    }

    // 视频解码类型
    @PlayerType.KernelType.Value
    private int kernelType;

    public int getKernelType() {
        return kernelType;
    }

    // 超时时间
    private long connectTimout;

    public long getConnectTimout() {
        return connectTimout;
    }

    // 日志
    private boolean log;

    public boolean isLog() {
        return log;
    }

    // 缓冲超时, 是否重新播放
    private boolean bufferingTimeoutRetry;

    public boolean isBufferingTimeoutRetry() {
        return bufferingTimeoutRetry;
    }

    // 开始播放前，是否销毁已存在的播放器相关实例
    private boolean initRelease;

    public boolean isInitRelease() {
        return initRelease;
    }

    // View生命周期, 自动暂停&续播&销毁...
    private boolean supportViewLifecycle;

    public boolean isSupportViewLifecycle() {
        return supportViewLifecycle;
    }

    // 视频url
    private String mediaUrl;

    public String getMediaUrl() {
        return mediaUrl;
    }

    // 字幕url
    private String subtitleUrl;

    public String getSubtitleUrl() {
        return subtitleUrl;
    }

    // 限制最大时长
    private long maxDuration;

    public long getMaxDuration() {
        return maxDuration;
    }

    // 试看开关
    private boolean trySee;

    public boolean isTrySee() {
        return trySee;
    }
    // 起播快进指定位置
    private long seek;

    public long getSeek() {
        return seek;
    }

    // 是否直播源
    private boolean live;

    public boolean isLive() {
        return live;
    }

    // 循环播放
    private boolean looping;

    public boolean isLooping() {
        return looping;
    }

    // 静音
    private boolean mute;

    public boolean isMute() {
        return mute;
    }

    // 默认自动开播
    private boolean playWhenReady;

    public boolean isPlayWhenReady() {
        return playWhenReady;
    }

    // 默认异步初始化
    private boolean prepareAsync;

    public boolean isPrepareAsync() {
        return prepareAsync;
    }

    @Override
    public String toString() {
        return "StartArgs{" +
                "exoCacheType=" + exoCacheType +
                ", exoCacheMax=" + exoCacheMax +
                ", exoCacheDir='" + exoCacheDir + '\'' +
                ", exoFFmpegType=" + exoFFmpegType +
                ", exoSeekType=" + exoSeekType +
                ", renderType=" + renderType +
                ", kernelType=" + kernelType +
                ", connectTimout=" + connectTimout +
                ", log=" + log +
                ", bufferingTimeoutRetry=" + bufferingTimeoutRetry +
                ", initRelease=" + initRelease +
                ", supportViewLifecycle=" + supportViewLifecycle +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", subtitleUrl='" + subtitleUrl + '\'' +
                ", maxDuration=" + maxDuration +
                ", trySee=" + trySee +
                ", seek=" + seek +
                ", live=" + live +
                ", looping=" + looping +
                ", mute=" + mute +
                ", playWhenReady=" + playWhenReady +
                ", prepareAsync=" + prepareAsync +
                '}';
    }

    public StartArgs(Builder builder) {
        this.exoCacheType = builder.exoCacheType;
        this.exoCacheMax = builder.exoCacheMax;
        this.exoCacheDir = builder.exoCacheDir;
        this.exoFFmpegType = builder.exoFFmpegType;
        this.exoSeekType = builder.exoSeekType;
        this.renderType = builder.renderType;
        this.kernelType = builder.kernelType;
        this.connectTimout = builder.connectTimout;
        this.log = builder.log;
        this.bufferingTimeoutRetry = builder.bufferingTimeoutRetry;
        this.initRelease = builder.initRelease;
        this.supportViewLifecycle = builder.supportViewLifecycle;
        this.mediaUrl = builder.mediaUrl;
        this.subtitleUrl = builder.subtitleUrl;
        this.maxDuration = builder.maxDuration;
        this.trySee = builder.trySee;
        this.seek = builder.seek;
        this.live = builder.live;
        this.looping = builder.looping;
        this.mute = builder.mute;
        this.playWhenReady = builder.playWhenReady;
        this.prepareAsync = builder.prepareAsync;
    }

    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.exoCacheType = exoCacheType;
        builder.exoCacheMax = exoCacheMax;
        builder.exoCacheDir = exoCacheDir;
        builder.exoFFmpegType = exoFFmpegType;
        builder.exoSeekType = exoSeekType;
        builder.renderType = renderType;
        builder.kernelType = kernelType;
        builder.connectTimout = connectTimout;
        builder.log = log;
        builder.bufferingTimeoutRetry = bufferingTimeoutRetry;
        builder.initRelease = initRelease;
        builder.supportViewLifecycle = supportViewLifecycle;
        builder.mediaUrl = mediaUrl;
        builder.subtitleUrl = subtitleUrl;
        builder.maxDuration = maxDuration;
        builder.trySee = trySee;
        builder.seek = seek;
        builder.live = live;
        builder.looping = looping;
        builder.mute = mute;
        builder.playWhenReady = playWhenReady;
        builder.prepareAsync = prepareAsync;
        return builder;
    }

    public static class Builder {

        private final PlayerArgs playerArgs = PlayerSDK.init().getPlayerBuilder();
        private int exoCacheType = playerArgs.getExoCacheType();
        private int exoCacheMax = playerArgs.getExoCacheMax();
        private String exoCacheDir = playerArgs.getExoCacheDir();
        @PlayerType.ExoFFmpegType.Value
        private int exoFFmpegType = playerArgs.getExoFFmpeg();
        @PlayerType.ExoSeekType
        private int exoSeekType = playerArgs.getExoSeekType();
        // 视频渲染类型
        @PlayerType.RenderType.Value
        private int renderType = playerArgs.getRender();
        // 视频解码类型
        @PlayerType.KernelType.Value
        private int kernelType = playerArgs.getKernel();
        // 超时时间
        private long connectTimout = playerArgs.getConnectTimeout();
        // 日志
        private boolean log = playerArgs.isLog();
        // 缓冲超时, 是否重新播放
        private boolean bufferingTimeoutRetry = playerArgs.getBufferingTimeoutRetry();
        // 开始播放前，是否销毁已存在的播放器相关实例
        private boolean initRelease = playerArgs.isInitRelease();
        // View生命周期, 自动暂停&续播&销毁...
        private boolean supportViewLifecycle = playerArgs.isSupportViewLifecycle();
        // 视频url
        private String mediaUrl;

        public Builder setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
            return this;
        }

        // 字幕url
        private String subtitleUrl;

        public Builder setSubtitleUrl(String subtitleUrl) {
            this.subtitleUrl = subtitleUrl;
            return this;
        }

        // 显示最大时长
        private long maxDuration = 0;

        public Builder setMaxDuration(long max) {
            this.maxDuration = max;
            return this;
        }

        // 试看开关
        private boolean trySee;

        public Builder setTrySee(boolean v) {
            this.trySee = v;
            return this;
        }

        // 起播快进指定位置
        private long seek = 0;

        public Builder setSeek(long seek) {
            this.seek = seek;
            return this;
        }

        // 是否直播源
        private boolean live = false;

        public Builder setLive(boolean live) {
            this.live = live;
            return this;
        }

        // 循环播放
        private boolean looping = false;

        public Builder setLooping(boolean looping) {
            this.looping = looping;
            return this;
        }

        // 静音
        private boolean mute = false;

        public Builder setMute(boolean mute) {
            this.mute = mute;
            return this;
        }

        // 默认自动开播
        private boolean playWhenReady = true;

        public Builder setPlayWhenReady(boolean playWhenReady) {
            this.playWhenReady = playWhenReady;
            return this;
        }

        // 默认异步初始化
        private boolean prepareAsync = true;

        public Builder setPrepareAsync(boolean prepareAsync) {
            this.prepareAsync = prepareAsync;
            return this;
        }

        public Builder() {
        }

        public StartArgs build() {
            return new StartArgs(this);
        }
    }
}
