package lib.kalu.mediaplayer.args;


import androidx.annotation.DrawableRes;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.buried.BuriedEvent;
import lib.kalu.mediaplayer.type.PlayerType;

public class StartArgs implements Serializable {

    @PlayerType.CacheType.Value
    private int cacheType;

    @PlayerType.CacheType.Value
    public int getCacheType() {
        return cacheType;
    }

    @PlayerType.CacheLocalType.Value
    protected int cacheLocalType;

    @PlayerType.CacheLocalType.Value
    public int getCacheLocalType() {
        return cacheLocalType;
    }

    @PlayerType.CacheSizeType.Value
    private int cacheSizeType;

    @PlayerType.CacheSizeType.Value
    public int getCacheSizeType() {
        return cacheSizeType;
    }

    private String cacheDirName;

    public String getCacheDirName() {
        return cacheDirName;
    }

    @PlayerType.DecoderType.Value
    private int decoderType;

    public int getDecoderType() {
        return decoderType;
    }

    @PlayerType.SeekType.Value
    private int seekType;

    public int getSeekType() {
        return seekType;
    }

    // 视频渲染类型
    @PlayerType.RenderType.Value
    private int renderType;

    public int getRenderType() {
        return renderType;
    }

    // 画面缩放类型
    @PlayerType.ScaleType.Value
    private int scaleType;

    public int getscaleType() {
        return scaleType;
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


    // 试看时长
    private long trySeeDuration = 0L;

    public long getTrySeeDuration() {
        return trySeeDuration;
    }


    // 起播快进指定位置
    private long playWhenReadySeekToPosition;

    public long getPlayWhenReadySeekToPosition() {
        return playWhenReadySeekToPosition;
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

    // 延迟播放
    private long playWhenReadyDelayedTime;

    public long getPlayWhenReadyDelayedTime() {
        return playWhenReadyDelayedTime;
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

    // 多剧集 收费角标
    private String episodeFlagVipImgUrl;

    public String getEpisodeFlagVipImgUrl() {
        return episodeFlagVipImgUrl;
    }

    // 多剧集 收费角标
    private String episodeFlagVipFilePath;

    public String getEpisodeFlagVipFilePath() {
        return episodeFlagVipFilePath;
    }

    // 多剧集 收费角标
    @DrawableRes
    private int episodeFlagVipResourceId;

    public int getEpisodeFlagVipResourceId() {
        return episodeFlagVipResourceId;
    }

    // 多剧集 免费角标
    private String episodeFlagFreeImgUrl;

    public String getEpisodeFlagFreeImgUrl() {
        return episodeFlagFreeImgUrl;
    }

    // 多剧集 免费角标
    private String episodeFlagFreeFilePath;

    public String getEpisodeFlagFreeFilePath() {
        return episodeFlagFreeFilePath;
    }

    // 多剧集 免费角标
    @DrawableRes
    private int episodeFlagFreeResourceId;

    public int getEpisodeFlagFreeResourceId() {
        return episodeFlagFreeResourceId;
    }

    // 多剧集 角标位置
    @PlayerType.EpisodeFlagLoactionType.Value
    private int episodeFlagLoaction;

    @PlayerType.EpisodeFlagLoactionType.Value
    public int getEpisodeFlagLoaction() {
        return episodeFlagLoaction;
    }

    // 多剧集 免费剧集
    private int episodeFreeItemCount = -1;

    public int getEpisodeFreeItemCount() {
        return episodeFreeItemCount;
    }

    // 数据埋点
    private BuriedEvent buriedEvent;

    public BuriedEvent getBuriedEvent() {
        return buriedEvent;
    }

    // 旋转角度
    @PlayerType.RotationType.Value
    private int rotation;

    public int getRotation() {
        return rotation;
    }

    // 外挂字幕
    private List<SubtitleTrack> extraTrackSubtitle;

    public List<SubtitleTrack> getExtraTrackSubtitle() {
        return extraTrackSubtitle;
    }

    // 外挂音轨
    private List<AudioTrack> extraTrackAudio;

    public List<AudioTrack> getExtraTrackAudio() {
        return extraTrackAudio;
    }

    @Override
    public String toString() {
        return "StartArgs{" +
                ", seekType=" + seekType +
                ", cacheType=" + cacheType +
                ", cacheLocalType=" + cacheLocalType +
                ", cacheSizeType=" + cacheSizeType +
                ", cacheDirName=" + cacheDirName +
                ", renderType=" + renderType +
                ", scaleType=" + scaleType +
                ", decoderType=" + decoderType +
                ", kernelType=" + kernelType +
                ", connectTimout=" + connectTimout +
                ", log=" + log +
                ", bufferingTimeoutRetry=" + bufferingTimeoutRetry +
                ", initRelease=" + initRelease +
                ", supportAutoRelease=" + supportAutoRelease +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", trySeeDuration=" + trySeeDuration +
                ", live=" + live +
                ", looping=" + looping +
                ", mute=" + mute +
                ", playWhenReady=" + playWhenReady +
                ", playWhenReadyDelayedTime=" + playWhenReadyDelayedTime +
                ", playWhenReadySeekToPosition=" + playWhenReadySeekToPosition +
                ", prepareAsync=" + prepareAsync +
                ", extra=" + extra +
                ", episodePlayingIndex=" + episodePlayingIndex +
                ", episodeItemCount=" + episodeItemCount +
                ", episodeFreeItemCount=" + episodeFreeItemCount +
                ", episodeFlagFreeResourceId=" + episodeFlagFreeResourceId +
                ", episodeFlagFreeFilePath=" + episodeFlagFreeFilePath +
                ", episodeFlagFreeImgUrl=" + episodeFlagFreeImgUrl +
                ", episodeFlagVipResourceId=" + episodeFlagVipResourceId +
                ", episodeFlagVipFilePath=" + episodeFlagVipFilePath +
                ", episodeFlagVipImgUrl=" + episodeFlagVipImgUrl +
                ", episodeFlagLoaction=" + episodeFlagLoaction +
                ", buriedEvent=" + buriedEvent +
                ", rotation=" + rotation +
                ", extraTrackSubtitle=" + extraTrackSubtitle +
                ", extraTrackAudio=" + extraTrackAudio +
                '}';
    }

    public StartArgs(Builder builder) {
        this.decoderType = builder.decoderType;
        this.seekType = builder.seekType;
        this.cacheType = builder.cacheType;
        this.cacheLocalType = builder.cacheLocalType;
        this.cacheSizeType = builder.cacheSizeType;
        this.cacheDirName = builder.cacheDirName;
        this.renderType = builder.renderType;
        this.scaleType = builder.scaleType;
        this.kernelType = builder.kernelType;
        this.connectTimout = builder.connectTimout;
        this.log = builder.log;
        this.bufferingTimeoutRetry = builder.bufferingTimeoutRetry;
        this.initRelease = builder.initRelease;
        this.supportAutoRelease = builder.supportAutoRelease;
        this.url = builder.url;
        this.title = builder.title;
        this.extraTrackSubtitle = builder.extraTrackSubtitle;
        this.extraTrackAudio = builder.extraTrackAudio;
        this.trySeeDuration = builder.trySeeDuration;
        this.live = builder.live;
        this.looping = builder.looping;
        this.mute = builder.mute;
        this.playWhenReady = builder.playWhenReady;
        this.playWhenReadyDelayedTime = builder.playWhenReadyDelayedTime;
        this.playWhenReadySeekToPosition = builder.playWhenReadySeekToPosition;
        this.prepareAsync = builder.prepareAsync;
        this.extra = builder.extra;
        this.episodePlayingIndex = builder.episodePlayingIndex;
        this.episodeItemCount = builder.episodeItemCount;
        this.episodeFreeItemCount = builder.episodeFreeItemCount;
        this.episodeFlagFreeResourceId = builder.episodeFlagFreeResourceId;
        this.episodeFlagFreeFilePath = builder.episodeFlagFreeFilePath;
        this.episodeFlagFreeImgUrl = builder.episodeFlagFreeImgUrl;
        this.episodeFlagVipResourceId = builder.episodeFlagVipResourceId;
        this.episodeFlagVipFilePath = builder.episodeFlagVipFilePath;
        this.episodeFlagVipImgUrl = builder.episodeFlagVipImgUrl;
        this.episodeFlagLoaction = builder.episodeFlagLoaction;
        this.buriedEvent = builder.buriedEvent;
        this.rotation = builder.rotation;
    }

    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.seekType = seekType;
        builder.cacheType = cacheType;
        builder.cacheLocalType = cacheLocalType;
        builder.cacheSizeType = cacheSizeType;
        builder.cacheDirName = cacheDirName;
        builder.decoderType = decoderType;
        builder.renderType = renderType;
        builder.scaleType = scaleType;
        builder.kernelType = kernelType;
        builder.connectTimout = connectTimout;
        builder.log = log;
        builder.bufferingTimeoutRetry = bufferingTimeoutRetry;
        builder.initRelease = initRelease;
        builder.supportAutoRelease = supportAutoRelease;
        builder.url = url;
        builder.title = title;
        builder.extraTrackSubtitle = extraTrackSubtitle;
        builder.extraTrackAudio = extraTrackAudio;
        builder.trySeeDuration = trySeeDuration;
        builder.live = live;
        builder.looping = looping;
        builder.mute = mute;
        builder.playWhenReady = playWhenReady;
        builder.playWhenReadyDelayedTime = playWhenReadyDelayedTime;
        builder.playWhenReadySeekToPosition = playWhenReadySeekToPosition;
        builder.prepareAsync = prepareAsync;
        builder.extra = extra;
        builder.episodePlayingIndex = episodePlayingIndex;
        builder.episodeItemCount = episodeItemCount;
        builder.episodeFreeItemCount = episodeFreeItemCount;
        builder.episodeFlagFreeResourceId = episodeFlagFreeResourceId;
        builder.episodeFlagFreeFilePath = episodeFlagFreeFilePath;
        builder.episodeFlagFreeImgUrl = episodeFlagFreeImgUrl;
        builder.episodeFlagVipResourceId = episodeFlagVipResourceId;
        builder.episodeFlagVipFilePath = episodeFlagVipFilePath;
        builder.episodeFlagVipImgUrl = episodeFlagVipImgUrl;
        builder.episodeFlagLoaction = episodeFlagLoaction;
        builder.buriedEvent = buriedEvent;
        builder.rotation = rotation;
        return builder;
    }

    public static class Builder implements Serializable {

        private final PlayerArgs playerArgs = PlayerSDK.init().getPlayerBuilder();

        // 解码器类型
        private int decoderType = playerArgs.getDecoderType();
        // 播放器类型
        @PlayerType.KernelType.Value
        private int kernelType = playerArgs.getKernelType();
        // 画面缩放类型
        @PlayerType.ScaleType.Value
        private int scaleType = playerArgs.getScaleType();
        // 旋转角度
        @PlayerType.RotationType.Value
        private int rotation = playerArgs.getRotation();
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

        // 缓存类型
        @PlayerType.CacheType.Value
        private int cacheType = playerArgs.getCacheType();

        @PlayerType.CacheLocalType.Value
        private int cacheLocalType = playerArgs.getCacheLocalType();

        @PlayerType.CacheSizeType.Value
        private int cacheSizeType = playerArgs.getCacheSizeType();

        private String cacheDirName = playerArgs.getCacheDirName();

        @PlayerType.SeekType.Value
        private int seekType = playerArgs.getSeekType();

        @PlayerType.SeekType.Value
        public int getSeekType() {
            return seekType;
        }

        // 视频渲染类型
        @PlayerType.RenderType.Value
        private int renderType = playerArgs.getRenderType();

        public Builder setRenderType(@PlayerType.RenderType.Value int v) {
            this.renderType = v;
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
        private long trySeeDuration = 0L;

        public Builder setTrySeeDuration(long v) {
            this.trySeeDuration = v;
            return this;
        }

        // 起播快进
        private long playWhenReadySeekToPosition = 0;

        public Builder setPlayWhenReadySeekToPosition(long v) {
            this.playWhenReadySeekToPosition = v;
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

        // 延迟播放
        private long playWhenReadyDelayedTime = 0L;

        public Builder setPlayWhenReadyDelayedTime(long v) {
            this.playWhenReadyDelayedTime = v;
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

        // 多剧集 收费角标
        private String episodeFlagVipImgUrl = null;

        public Builder setEpisodeFlagVipImgUrl(String v) {
            this.episodeFlagVipImgUrl = v;
            return this;
        }

        // 多剧集 收费角标
        private String episodeFlagVipFilePath = null;

        public Builder setEpisodeFlagVipFilePath(String v) {
            this.episodeFlagVipFilePath = v;
            return this;
        }

        // 多剧集 收费角标
        @DrawableRes
        private int episodeFlagVipResourceId = 0;

        public Builder setEpisodeFlagVipResourceId(@DrawableRes int v) {
            this.episodeFlagVipResourceId = v;
            return this;
        }

        // 多剧集 免费角标
        private String episodeFlagFreeImgUrl = null;

        public Builder setEpisodeFlagFreeImgUrl(String v) {
            this.episodeFlagFreeImgUrl = v;
            return this;
        }

        // 多剧集 免费角标
        @DrawableRes
        private int episodeFlagFreeResourceId = 0;

        public Builder setEpisodeFlagFreeResourceId(@DrawableRes int v) {
            this.episodeFlagFreeResourceId = v;
            return this;
        }

        // 多剧集 免费角标
        private String episodeFlagFreeFilePath = null;

        public Builder setEpisodeFlagFreeFilePath(String v) {
            this.episodeFlagFreeFilePath = v;
            return this;
        }

        // 多剧集 角标位置
        @PlayerType.EpisodeFlagLoactionType.Value
        private int episodeFlagLoaction = PlayerType.EpisodeFlagLoactionType.DEFAULT;

        public Builder setEpisodeFlagLoaction(@PlayerType.EpisodeFlagLoactionType.Value int v) {
            this.episodeFlagLoaction = v;
            return this;
        }

        // 多剧集 免费剧集
        private int episodeFreeItemCount = -1;

        public Builder setEpisodeFreeItemCount(int v) {
            this.episodeFreeItemCount = v;
            return this;
        }

        // 多剧集 总数
        private int episodeItemCount = -1;

        public Builder setEpisodeItemCount(int v) {
            this.episodeItemCount = v;
            return this;
        }

        // 多剧集 选中
        private int episodePlayingIndex = -1;

        public Builder setEpisodePlayingIndex(int v) {
            this.episodePlayingIndex = v;
            return this;
        }

        // 外挂字幕
        private List<SubtitleTrack> extraTrackSubtitle;

        public Builder setExtraTrackSubtitle(List<SubtitleTrack> v) {
            this.extraTrackSubtitle = v;
            return this;
        }

        // 外挂音轨
        private List<AudioTrack> extraTrackAudio;

        public Builder setExtraTrackAudio(List<AudioTrack> v) {
            this.extraTrackAudio = v;
            return this;
        }

        public Builder() {
        }

        public StartArgs build() {
            return new StartArgs(this);
        }
    }
}
