package lib.kalu.mediaplayer.config.start;

import androidx.annotation.Keep;

@Keep
public final class StartBuilder {

    private long max;
    private long seek;
    private boolean live;
    private boolean loop;
    private boolean mute;
    private boolean playWhenReady;

    private boolean windowVisibilityChangedRelease; // 不可见, release

    // 外部背景音
    private boolean externalEnable;
    private String externalMusicUrl;
    private boolean externalMusicLoop;
    private boolean externalMusicPlayWhenReady;
    private boolean externalMusicSeek;

    public boolean isMute() {
        return mute;
    }

    public long getMax() {
        return max;
    }

    public long getSeek() {
        return seek;
    }

    public boolean isLive() {
        return live;
    }

    public boolean isLoop() {
        return loop;
    }

    public boolean isPlayWhenReady() {
        return playWhenReady;
    }

    public boolean isWindowVisibilityChangedRelease() {
        return windowVisibilityChangedRelease;
    }

    public boolean isExternalEnable() {
        return externalEnable;
    }

    public String getExternalMusicUrl() {
        return externalMusicUrl;
    }

    public boolean isExternalMusicLoop() {
        return externalMusicLoop;
    }

    public boolean isExternalMusicPlayWhenReady() {
        return externalMusicPlayWhenReady;
    }

    public boolean isExternalMusicSeek() {
        return externalMusicSeek;
    }

    public StartBuilder(StartBuilder.Builder builder) {
        this.max = builder.max;
        this.seek = builder.seek;
        this.mute = builder.mute;
        this.live = builder.live;
        this.loop = builder.loop;
        this.playWhenReady = builder.playWhenReady;
        this.windowVisibilityChangedRelease = builder.windowVisibilityChangedRelease;
        this.externalEnable = builder.externalEnable;
        this.externalMusicUrl = builder.externalMusicUrl;
        this.externalMusicLoop = builder.externalMusicLoop;
        this.externalMusicPlayWhenReady = builder.externalMusicPlayWhenReady;
        this.externalMusicSeek = builder.externalMusicSeek;
    }

    @Override
    public String toString() {
        return "StartBuilder{" +
                "playWhenReady=" + playWhenReady +
                ", max=" + max +
                ", seek=" + seek +
                ", live=" + live +
                ", loop=" + loop +
                ", mute=" + mute +
                ", windowVisibilityChangedRelease=" + windowVisibilityChangedRelease +
                ", externalEnable='" + externalEnable + '\'' +
                ", externalMusicUrl='" + externalMusicUrl + '\'' +
                ", externalMusicLoop=" + externalMusicLoop +
                ", externalMusicPlayWhenReady=" + externalMusicPlayWhenReady +
                ", externalMusicSeek=" + externalMusicSeek +
                '}';
    }

    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.max = max;
        builder.seek = seek;
        builder.mute = mute;
        builder.live = live;
        builder.loop = loop;
        builder.playWhenReady = playWhenReady;
        builder.windowVisibilityChangedRelease = windowVisibilityChangedRelease;
        builder.externalEnable = externalEnable;
        builder.externalMusicUrl = externalMusicUrl;
        builder.externalMusicLoop = externalMusicLoop;
        builder.externalMusicPlayWhenReady = externalMusicPlayWhenReady;
        builder.externalMusicSeek = externalMusicSeek;
        return builder;
    }

    @Keep
    public final static class Builder {

        private long max = 0;
        private long seek = 0;
        private boolean live = false;
        private boolean loop = false;
        private boolean mute = false;
        private boolean playWhenReady = true;

        private boolean windowVisibilityChangedRelease = false; // 不可见, release

        private boolean externalEnable = false;
        private String externalMusicUrl = null;
        private boolean externalMusicLoop = false;
        private boolean externalMusicPlayWhenReady = false;
        private boolean externalMusicSeek = true;

        public Builder() {
        }

        public Builder setExternalEnable(boolean v) {
            externalEnable = v;
            return this;
        }

        public Builder setPlayWhenReady(boolean v) {
            playWhenReady = v;
            return this;
        }

        public Builder setExternalMusicPlayWhenReady(boolean v) {
            externalMusicPlayWhenReady = v;
            return this;
        }

        public Builder setExternalMusicLooping(boolean v) {
            externalMusicLoop = v;
            return this;
        }

        public Builder setExternalMusicSeek(boolean v) {
            externalMusicSeek = v;
            return this;
        }

        public Builder setExternalMusicUrl(String v) {
            externalMusicUrl = v;
            return this;
        }

        public Builder setMute(boolean v) {
            mute = v;
            return this;
        }

        public Builder setMax(long v) {
            max = v;
            return this;
        }

        public Builder setSeek(long v) {
            seek = v;
            return this;
        }

        public Builder setLive(boolean v) {
            live = v;
            return this;
        }

        public Builder setLoop(boolean v) {
            loop = v;
            return this;
        }

        public Builder setWindowVisibilityChangedRelease(boolean v) {
            this.windowVisibilityChangedRelease = v;
            return this;
        }

        public StartBuilder build() {
            return new StartBuilder(this);
        }
    }
}
