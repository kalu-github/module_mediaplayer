package lib.kalu.mpvplayer;

/** Snapshot. Times are ms, rates are bits/s. -1 means unavailable, not zero. */
public final class PlaybackStats {
    public final long positionMs, durationMs, bufferedPositionMs, bufferedDurationMs;
    public final long videoBitrate, audioBitrate, estimatedBandwidth, firstFrameMs, stalledMs, droppedFrames;
    public final int stallCount, trackSwitchCount, width, height;
    public final double frameRate;
    public final String videoCodec, audioCodec;
    public final long segmentLoadMs=-1, segmentFailureCount=-1;
    PlaybackStats(long position,long duration,long bufferedPosition,long bufferedDuration,long videoBitrate,
                  long audioBitrate,long bandwidth,long firstFrame,long stalled,int stalls,int switches,
                  long dropped,int width,int height,double fps,String videoCodec,String audioCodec) {
        positionMs=position; durationMs=duration; bufferedPositionMs=bufferedPosition; bufferedDurationMs=bufferedDuration;
        this.videoBitrate=videoBitrate; this.audioBitrate=audioBitrate; estimatedBandwidth=bandwidth;
        firstFrameMs=firstFrame; stalledMs=stalled; stallCount=stalls; trackSwitchCount=switches;
        droppedFrames=dropped; this.width=width; this.height=height; frameRate=fps;
        this.videoCodec=videoCodec; this.audioCodec=audioCodec;
    }
    PlaybackStats atEnd() {
        return new PlaybackStats(durationMs>=0 ? durationMs : positionMs,durationMs,bufferedPositionMs,
            bufferedDurationMs,videoBitrate,audioBitrate,estimatedBandwidth,firstFrameMs,stalledMs,
            stallCount,trackSwitchCount,droppedFrames,width,height,frameRate,videoCodec,audioCodec);
    }
}
