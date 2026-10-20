package lib.kalu.mpvplayer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Immutable input. UNKNOWN is intentional: a URL or missing duration cannot prove a stream is live. */
public final class MediaSource {
    public enum StreamType { UNKNOWN, VOD, LIVE }
    public final String uri;
    public final Map<String, String> headers;
    public final StreamType streamType;

    public MediaSource(String uri) { this(uri, Collections.emptyMap(), StreamType.UNKNOWN); }
    public MediaSource(String uri, Map<String, String> headers, StreamType type) {
        if (uri == null || uri.trim().isEmpty() || uri.indexOf('\0') >= 0 || type == null)
            throw new IllegalArgumentException("A nonempty URI and stream type are required");
        Map<String, String> copy = new LinkedHashMap<>();
        if (headers != null) for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey(), value = entry.getValue();
            if (key == null || !key.matches("[!#$%&'*+.^_`|~0-9A-Za-z-]+") || value == null
                    || value.indexOf('\r') >= 0 || value.indexOf('\n') >= 0 || value.indexOf('\0') >= 0)
                throw new IllegalArgumentException("Invalid HTTP header");
            copy.put(key, value);
        }
        this.uri = uri; this.headers = Collections.unmodifiableMap(copy); this.streamType = type;
    }
}
