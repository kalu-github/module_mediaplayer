package lib.kalu.mpvplayer;

/** Milliseconds and bytes. mpv exposes one network timeout and a shared start/rebuffer threshold. */
public final class PlayerConfig {
    public final long networkTimeoutMs, maxBufferMs, rebufferMs, maxBufferBytes;
    public final long progressIntervalMs, retryDelayMs, stallTimeoutMs, trackSwitchTimeoutMs;
    public final int maxRetries;
    public final boolean autoPlay;
    private PlayerConfig(Builder b) {
        if (b.networkTimeoutMs <= 0 || b.networkTimeoutMs > 3600000 || b.maxBufferMs <= 0
                || b.rebufferMs < 0 || b.rebufferMs > b.maxBufferMs || b.maxBufferBytes <= 0
                || b.progressIntervalMs < 100 || b.progressIntervalMs > 60000 || b.maxRetries < 0
                || b.retryDelayMs < 0 || b.retryDelayMs > 3600000 || b.stallTimeoutMs < 0
                || b.trackSwitchTimeoutMs < 100)
            throw new IllegalArgumentException("Invalid timeout, buffer, polling or retry configuration");
        networkTimeoutMs=b.networkTimeoutMs; maxBufferMs=b.maxBufferMs; rebufferMs=b.rebufferMs;
        maxBufferBytes=b.maxBufferBytes; progressIntervalMs=b.progressIntervalMs; maxRetries=b.maxRetries;
        retryDelayMs=b.retryDelayMs; stallTimeoutMs=b.stallTimeoutMs;
        trackSwitchTimeoutMs=b.trackSwitchTimeoutMs; autoPlay=b.autoPlay;
    }
    public static final class Builder {
        private long networkTimeoutMs=30000, maxBufferMs=60000, rebufferMs=2000, maxBufferBytes=64L*1024*1024;
        private long progressIntervalMs=500, retryDelayMs=1000, stallTimeoutMs=0, trackSwitchTimeoutMs=10000;
        private int maxRetries=0;
        private boolean autoPlay=false;
        public Builder networkTimeoutMs(long v) { networkTimeoutMs=v; return this; }
        public Builder buffer(long maximumMs, long startAndResumeMs, long bytes) {
            maxBufferMs=maximumMs; rebufferMs=startAndResumeMs; maxBufferBytes=bytes; return this;
        }
        public Builder progressIntervalMs(long v) { progressIntervalMs=v; return this; }
        public Builder retry(int count, long delayMs) { maxRetries=count; retryDelayMs=delayMs; return this; }
        public Builder stallTimeoutMs(long v) { stallTimeoutMs=v; return this; }
        public Builder trackSwitchTimeoutMs(long v) { trackSwitchTimeoutMs=v; return this; }
        public Builder autoPlay(boolean v) { autoPlay=v; return this; }
        public PlayerConfig build() { return new PlayerConfig(this); }
    }
}
