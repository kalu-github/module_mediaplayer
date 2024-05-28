package lib.kalu.mediaplayer.args;


public class StartArgs {
    private long max;
    private long seek;
    private boolean live;
    private boolean looping;
    private boolean loopingRelease;
    private boolean mute;
    private boolean playWhenReady;
    private boolean prepareAsync;
    private boolean windowVisibilityChangedRelease; // 不可见, release

    public long getMax() {
        return max;
    }

    public long getSeek() {
        return seek;
    }

    public boolean isLive() {
        return live;
    }

    public boolean isLooping() {
        return looping;
    }

    public boolean isLoopingRelease() {
        return loopingRelease;
    }

    public boolean isMute() {
        return mute;
    }

    public boolean isPlayWhenReady() {
        return playWhenReady;
    }

    public boolean isPrepareAsync() {
        return prepareAsync;
    }

    public boolean isWindowVisibilityChangedRelease() {
        return windowVisibilityChangedRelease;
    }

    public StartArgs(Builder builder) {
        this.max = builder.max;
        this.seek = builder.seek;
        this.mute = builder.mute;
        this.live = builder.live;
        this.looping = builder.looping;
        this.loopingRelease = builder.loopingRelease;
        this.prepareAsync = builder.prepareAsync;
        this.playWhenReady = builder.playWhenReady;
        this.windowVisibilityChangedRelease = builder.windowVisibilityChangedRelease;
    }

    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.max = max;
        builder.seek = seek;
        builder.mute = mute;
        builder.live = live;
        builder.looping = looping;
        builder.loopingRelease = loopingRelease;
        builder.prepareAsync = prepareAsync;
        builder.playWhenReady = playWhenReady;
        builder.windowVisibilityChangedRelease = windowVisibilityChangedRelease;
        return builder;
    }

    
    public static class Builder {

        private long max = 0;
        private long seek = 0;
        private boolean live = false;
        private boolean looping = false;
        private boolean loopingRelease = false;
        private boolean mute = false;
        private boolean playWhenReady = true; // 默认自动开播
        private boolean prepareAsync = true; // 默认异步初始化

        private boolean windowVisibilityChangedRelease = false; // 不可见, release

        public Builder() {
        }

        public Builder setPrepareAsync(boolean v) {
            this.prepareAsync = v;
            return this;
        }

        public Builder setPlayWhenReady(boolean v) {
            this.playWhenReady = v;
            return this;
        }

        public Builder setMute(boolean v) {
            this.mute = v;
            return this;
        }

        public Builder setMax(long v) {
            this.max = v;
            return this;
        }

        public Builder setSeek(long v) {
            this.seek = v;
            return this;
        }

        public Builder setLive(boolean v) {
            this.live = v;
            return this;
        }

        public Builder setLooping(boolean v) {
            this.looping = v;
            return this;
        }

        public Builder setLoopingRelease(boolean v) {
            this.loopingRelease = v;
            return this;
        }

        public Builder setWindowVisibilityChangedRelease(boolean v) {
            this.windowVisibilityChangedRelease = v;
            return this;
        }

        public StartArgs build() {
            return new StartArgs(this);
        }
    }
}
