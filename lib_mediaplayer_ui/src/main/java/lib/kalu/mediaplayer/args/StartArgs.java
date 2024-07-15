package lib.kalu.mediaplayer.args;


import org.json.JSONObject;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.buried.BuriedEvent;
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

    @PlayerType.ExoRenderersType.Value
    private int exoRenderersType;

    public int getExoRenderersType() {
        return exoRenderersType;
    }

    @PlayerType.ExoSeekType
    private int exoSeekType;

    public int getExoSeekType() {
        return exoSeekType;
    }

    private boolean exoUseOkhttp;

    public boolean isExoUseOkhttp() {
        return exoUseOkhttp;
    }

    // 视频渲染类型
    @PlayerType.RenderType.Value
    private int renderType;

    public int getRenderType() {
        return renderType;
    }

    // 画面缩放类型
    @PlayerType.ScaleType.Value
    private int renderScaleType;

    public int getRenderScaleType() {
        return renderScaleType;
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
    private boolean supportAutoRelease;

    public boolean isSupportAutoRelease() {
        return supportAutoRelease;
    }

    // 视频url
    private String url;

    public String getUrl() {
        return url;
    }

    // 视频title
    private String title;

    public String getTitle() {
        return title;
    }

    // 字幕url
    private String subtitleUrl;

    public String getSubtitleUrl() {
        return subtitleUrl;
    }

    // 试看时长
    private long trySeeDuration = 0L;

    public long getTrySeeDuration() {
        return trySeeDuration;
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

    // 透传数据
    private JSONObject extra;

    public JSONObject getExtra() {
        return extra;
    }

    // 多剧集 选中
    private int episodePlayingIndex = -1;

    public int getEpisodePlayingIndex() {
        return episodePlayingIndex;
    }

    // 多剧集 总数
    private int episodeItemCount = -1;

    public int getEpisodeItemCount() {
        return episodeItemCount;
    }

    // 数据埋点
    private BuriedEvent buriedEvent;

    public BuriedEvent getBuriedEvent() {
        return buriedEvent;
    }

    @Override
    public String toString() {
        return "StartArgs{" +
                "exoCacheType=" + exoCacheType +
                ", exoCacheMax=" + exoCacheMax +
                ", exoCacheDir='" + exoCacheDir + '\'' +
                ", exoRenderersType=" + exoRenderersType +
                ", exoSeekType=" + exoSeekType +
                ", exoUseOkhttp=" + exoUseOkhttp +
                ", renderType=" + renderType +
                ", renderScaleType=" + renderScaleType +
                ", kernelType=" + kernelType +
                ", connectTimout=" + connectTimout +
                ", log=" + log +
                ", bufferingTimeoutRetry=" + bufferingTimeoutRetry +
                ", initRelease=" + initRelease +
                ", supportAutoRelease=" + supportAutoRelease +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", subtitleUrl='" + subtitleUrl + '\'' +
                ", trySeeDuration=" + trySeeDuration +
                ", seek=" + seek +
                ", live=" + live +
                ", looping=" + looping +
                ", mute=" + mute +
                ", playWhenReady=" + playWhenReady +
                ", prepareAsync=" + prepareAsync +
                ", extra=" + extra +
                ", episodePlayingIndex=" + episodePlayingIndex +
                ", episodeItemCount=" + episodeItemCount +
                ", buriedEvent=" + buriedEvent +
                '}';
    }

    public StartArgs(Builder builder) {
        this.exoCacheType = builder.exoCacheType;
        this.exoCacheMax = builder.exoCacheMax;
        this.exoCacheDir = builder.exoCacheDir;
        this.exoRenderersType = builder.exoRenderersType;
        this.exoSeekType = builder.exoSeekType;
        this.exoUseOkhttp = builder.exoUseOkhttp;
        this.renderType = builder.renderType;
        this.renderScaleType = builder.renderScaleType;
        this.kernelType = builder.kernelType;
        this.connectTimout = builder.connectTimout;
        this.log = builder.log;
        this.bufferingTimeoutRetry = builder.bufferingTimeoutRetry;
        this.initRelease = builder.initRelease;
        this.supportAutoRelease = builder.supportAutoRelease;
        this.url = builder.url;
        this.title = builder.title;
        this.subtitleUrl = builder.subtitleUrl;
        this.trySeeDuration = builder.trySeeDuration;
        this.seek = builder.seek;
        this.live = builder.live;
        this.looping = builder.looping;
        this.mute = builder.mute;
        this.playWhenReady = builder.playWhenReady;
        this.prepareAsync = builder.prepareAsync;
        this.extra = builder.extra;
        this.episodePlayingIndex = builder.episodePlayingIndex;
        this.episodeItemCount = builder.episodeItemCount;
        this.buriedEvent = builder.buriedEvent;
    }

    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.exoCacheType = exoCacheType;
        builder.exoCacheMax = exoCacheMax;
        builder.exoCacheDir = exoCacheDir;
        builder.exoRenderersType = exoRenderersType;
        builder.exoSeekType = exoSeekType;
        builder.exoUseOkhttp = exoUseOkhttp;
        builder.renderType = renderType;
        builder.renderScaleType = renderScaleType;
        builder.kernelType = kernelType;
        builder.connectTimout = connectTimout;
        builder.log = log;
        builder.bufferingTimeoutRetry = bufferingTimeoutRetry;
        builder.initRelease = initRelease;
        builder.supportAutoRelease = supportAutoRelease;
        builder.url = url;
        builder.title = title;
        builder.subtitleUrl = subtitleUrl;
        builder.trySeeDuration = trySeeDuration;
        builder.seek = seek;
        builder.live = live;
        builder.looping = looping;
        builder.mute = mute;
        builder.playWhenReady = playWhenReady;
        builder.prepareAsync = prepareAsync;
        builder.extra = extra;
        builder.episodePlayingIndex = episodePlayingIndex;
        builder.episodeItemCount = episodeItemCount;
        builder.buriedEvent = buriedEvent;
        return builder;
    }

    public static class Builder {

        private final PlayerArgs playerArgs = PlayerSDK.init().getPlayerBuilder();
        private int exoCacheType = playerArgs.getExoCacheType();
        private int exoCacheMax = playerArgs.getExoCacheMax();
        private String exoCacheDir = playerArgs.getExoCacheDir();
        private boolean exoUseOkhttp = playerArgs.isExoUseOkhttp();
        @PlayerType.ExoRenderersType.Value
        private int exoRenderersType = playerArgs.getExoRenderersType();
        @PlayerType.ExoSeekType
        private int exoSeekType = playerArgs.getExoSeekType();
        // 视频渲染类型
        @PlayerType.RenderType.Value
        private int renderType = playerArgs.getRender();
        // 画面缩放类型
        @PlayerType.ScaleType.Value
        private int renderScaleType = playerArgs.getScaleType();
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
        // 数据埋点
        private BuriedEvent buriedEvent = playerArgs.getBuriedEvent();
        // 自动暂停&续播&销毁...
        private boolean supportAutoRelease = playerArgs.isSupportAutoRelease();

        public Builder setSupportAutoRelease(boolean v) {
            this.supportAutoRelease = v;
            return this;
        }

        // 视频url
        private String url;

        public Builder setUrl(String v) {
            this.url = v;
            return this;
        }

        // 视频title
        private String title;

        public Builder setTitle(String v) {
            this.title = v;
            return this;
        }

        // 字幕url
        private String subtitleUrl;

        public Builder setSubtitleUrl(String subtitleUrl) {
            this.subtitleUrl = subtitleUrl;
            return this;
        }

        // 试看时长
        private long trySeeDuration = playerArgs.getTrySeeDuration();

        public Builder setTrySeeDuration(long v) {
            this.trySeeDuration = v;
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

        // 透传数据
        private JSONObject extra;

        public Builder setExtra(JSONObject v) {
            this.extra = v;
            return this;
        }

        // 多剧集 选中
        private int episodePlayingIndex = -1;

        public Builder setEpisodePlayingIndex(int v) {
            this.episodePlayingIndex = v;
            return this;
        }

        // 多剧集 总数
        private int episodeItemCount = -1;

        public Builder setEpisodeItemCount(int v) {
            this.episodeItemCount = v;
            return this;
        }

        public Builder() {
        }

        public StartArgs build() {
            return new StartArgs(this);
        }
    }
}
