package lib.kalu.mpvplayer;

/** Exact HTTP/segment causes are not inferred from libmpv's aggregate error codes. */
public final class PlayerError extends RuntimeException {
    public enum Type {
        NETWORK_CONNECTION, NETWORK_TIMEOUT, HTTP_STATUS, PLAYLIST_PARSE, PLAYLIST_INVALID,
        SEGMENT_LOAD, DEMUX, AUDIO_DECODE, VIDEO_DECODE, AUDIO_OUTPUT, VIDEO_OUTPUT,
        UNSUPPORTED_FORMAT, UNSUPPORTED_CODEC, UNSUPPORTED_TRACK, TRACK_SWITCH,
        SUBTITLE_PARSE, SOURCE_UNAVAILABLE, STALLED, INTERNAL
    }
    public final int code;
    public final Type type;
    public final boolean retryable, recoverable;
    /** -1 means the backend has not exposed a response status. */
    public final int httpStatus = -1;
    public PlayerError(int code, Type type, String message, Throwable cause, boolean retryable, boolean recoverable) {
        super(message, cause); this.code=code; this.type=type; this.retryable=retryable; this.recoverable=recoverable;
    }
    static PlayerError nativeError(int code) {
        Type type = code == -13 || code == -16 ? Type.SOURCE_UNAVAILABLE : code == -14 ? Type.AUDIO_OUTPUT
            : code == -15 ? Type.VIDEO_OUTPUT : code == -17 ? Type.UNSUPPORTED_FORMAT : Type.INTERNAL;
        return new PlayerError(code, type, "libmpv playback error " + code, null, code == -13, code == -13);
    }
}
