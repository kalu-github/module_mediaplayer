package lib.kalu.mpvplayer;

import java.util.List;

/** Conservative HLS variant policy; independent of mpv's initial hls-bitrate selection. */
final class AdaptiveTrackSelector {
    static TrackInfo choose(List<TrackInfo> tracks,TrackInfo current,long inputBitsPerSecond,
                            long bufferedMs,boolean buffering) {
        TrackInfo lowest=null, target=null;
        long budget=inputBitsPerSecond > 0 ? (long)(inputBitsPerSecond*0.75) : -1;
        for (TrackInfo track:tracks) {
            if (track.kind!=TrackInfo.Kind.HLS_VARIANT || track.support==TrackInfo.Support.UNSUPPORTED) continue;
            if (lowest==null || track.hlsBitrate<lowest.hlsBitrate) lowest=track;
            if (budget>0 && track.hlsBitrate<=budget && (target==null || track.hlsBitrate>target.hlsBitrate)) target=track;
        }
        if (current==null) return target==null ? lowest : target;
        if (buffering || (bufferedMs>=0 && bufferedMs<1500)) {
            TrackInfo lower=null;
            for (TrackInfo track:tracks)
                if (track.kind==TrackInfo.Kind.HLS_VARIANT && track.support!=TrackInfo.Support.UNSUPPORTED
                    && track.hlsBitrate<current.hlsBitrate && (lower==null || track.hlsBitrate>lower.hlsBitrate)) lower=track;
            return lower==null ? current : lower;
        }
        if (budget<0) return current; // An unavailable throughput sample is not zero bandwidth.
        if (target==null) target=lowest;
        if (target==null || (target.hlsBitrate>current.hlsBitrate && bufferedMs<5000)) return current;
        return target;
    }
}
