package lib.kalu.mpvplayer;

import java.util.List;

public interface TrackListener {
    default void onTracksChanged(MpvPlayer player, TrackInfo.Type type, List<TrackInfo> tracks) {}
    default void onSelectedTrackChanged(MpvPlayer player, TrackInfo.Type type, TrackInfo selected) {}
    default void onTrackSelectionResult(MpvPlayer player, TrackInfo.Type type, String requestedId, OperationResult result) {}
    default void onVideoTrackModeChanged(MpvPlayer player, MpvPlayer.VideoTrackMode mode) {}
}
