package lib.kalu.mpvplayer;

import android.view.Surface;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lib.kalu.mediaplayer.core.kernel.video.mpv.VideoMpvPlayer;

/** Host regression test using the real SDK state machine with an in-memory engine (no playback). */
public final class VideoMpvTrackTest {
    private static int assertions;
    private static void check(boolean value, String message) {
        assertions++;
        if (!value) throw new AssertionError(message);
    }
    private static Map<String, Object> map(Object... values) {
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < values.length; i += 2) result.put((String) values[i], values[i + 1]);
        return result;
    }
    private static final class Scheduler implements PlayerScheduler {
        final ArrayDeque<Runnable> queue = new ArrayDeque<>();
        public long now() { return 100; }
        public void post(Runnable task, long delay) { if (delay == 0) queue.add(task); }
        public void cancelAll() { queue.clear(); }
        void drain() { while (!queue.isEmpty()) queue.remove().run(); }
    }
    private static final class Engine implements PlayerEngine {
        Events events;
        final List<Map<String, Object>> tracks = new ArrayList<>();
        final Map<String, String> options = new HashMap<>();
        public void events(Events value) { events = value; }
        public void reset() {}
        public void source(MediaSource source) {}
        public void prepare() {}
        public void play() {}
        public void pause() {}
        public void stop() {}
        public void release() {}
        public void seek(long value) {}
        public void surface(Surface surface) {}
        public void surfaceSize(int width, int height) {}
        public void set(String name, String value) { options.put(name, value); }
        public Object get(String name) {
            if (name.equals("track-list")) return tracks;
            if (name.equals("decoder-list")) return Arrays.asList(map("codec", "h264"), map("codec", "aac"));
            return null;
        }
    }
    private static Map<String, Object> track(String type, long id, String codec, boolean selected) {
        return map("type", type, "id", id, "codec", codec, "selected", selected,
                "decoder", selected ? "decoder" : "", "title", "中文轨道", "lang", "zh");
    }
    public static void main(String[] args) throws Exception {
        Engine engine = new Engine();
        Scheduler scheduler = new Scheduler();
        VideoMpvPlayer ui = new VideoMpvPlayer();
        check(ui.getTrackInfoAll() == null && !ui.toggleTrack(null), "uninitialized player");
        try (MpvPlayer player = new MpvPlayer(engine, scheduler, new PlayerConfig.Builder().build())) {
            Field field = VideoMpvPlayer.class.getDeclaredField("mMpvPlayer");
            field.setAccessible(true);
            field.set(ui, player);
            Map<String, Object> low = track("video", 1, "h264", true);
            low.put("hls-bitrate", 1000000L); low.put("demux-w", 1280L); low.put("demux-h", 720L);
            Map<String, Object> high = track("video", 2, "h264", false);
            high.put("hls-bitrate", 3000000000L);
            Map<String, Object> subtitle = track("sub", 1, "ass", false);
            subtitle.put("default", true); subtitle.put("forced", true);
            engine.tracks.addAll(Arrays.asList(low, high, track("video", 3, "vp9", false),
                    track("audio", 1, "aac", true), subtitle));
            player.setMediaSource("https://example.test/master.m3u8"); player.prepareAsync();
            engine.events.prepared(); scheduler.drain();
            List<lib.kalu.mediaplayer.bean.info.TrackInfo> all = ui.getTrackInfoAll();
            List<lib.kalu.mediaplayer.bean.info.TrackInfo> video = ui.getTrackInfoVideo();
            List<lib.kalu.mediaplayer.bean.info.TrackInfo> audio = ui.getTrackInfoAudio();
            List<lib.kalu.mediaplayer.bean.info.TrackInfo> subs = ui.getTrackInfoSubtitle();
            check(all.size() == 4 && video.size() == 2 && audio.size() == 1 && subs.size() == 1, "type filtering and unsupported codec exclusion");
            check(ui.getTrackInfo(1).size() == 2 && ui.getTrackInfo(2).size() == 1 && ui.getTrackInfo(3).size() == 1, "query type contract");
            check(ui.getTrackInfo(4) == null && ui.getTrackInfo(0) == null, "unsupported query types");
            check(video.get(0).getTrackType() == 2 && audio.get(0).getTrackType() == 1 && subs.get(0).getTrackType() == 3, "Mediax-compatible result types");
            check(video.get(0).getGroupCount() == 3 && audio.get(0).getGroupIndex() == 1 && subs.get(0).getGroupIndex() == 2, "synthetic type groups");
            check(all.get(2).getGroupIndex() == audio.get(0).getGroupIndex(), "indices stable across filtered lists");
            check(video.get(1).getTrackIndex() == 1 && video.get(0).getTrackCount() == 3, "native list indices survive filtering");
            check(video.get(0).isTrackSelected() && !video.get(1).isTrackSelected() && video.get(1).isGroupSelected(), "actual selected flags");
            check(video.get(0).isTrackSupported() && !video.get(1).isTrackSupported(), "unknown decoder support is not reported as confirmed");
            check(video.get(0).isGroupAdaptiveSupported() && !audio.get(0).isGroupAdaptiveSupported(), "adaptive only for HLS video");
            check(video.get(0).getWidth() == 1280 && video.get(0).getHeight() == 720 && video.get(0).getBitrate() == 1000000, "video metadata");
            check(video.get(1).getBitrate() == Integer.MAX_VALUE, "bitrate cannot overflow int");
            check(subs.get(0).getSelectionFlags() == 3 && subs.get(0).getLanguage().equals("zh"), "default/forced flags and language");
            check(video.get(0).getSampleMimeType().equals("video/avc") && video.get(0).getLabel().equals("中文轨道"), "MIME and Unicode label");
            check(!video.get(0).getId().equals(audio.get(0).getId()), "overlapping native IDs stay type-qualified");
            check(ui.toggleTrack(video.get(1)) && engine.options.get("vid").equals("2"), "video selection uses SDK ID");
            check(player.getVideoTrackMode() == MpvPlayer.VideoTrackMode.FIXED, "manual video selection fixes quality");
            check(!video.get(1).isTrackSelected(), "accepted request must not fabricate completion");
            check(ui.toggleTrack(audio.get(0)) && engine.options.get("aid").equals("1"), "audio selection");
            check(ui.toggleTrack(subs.get(0)) && engine.options.get("sid").equals("1"), "subtitle selection");
            audio.get(0).setTrackType(2);
            check(!ui.toggleTrack(audio.get(0)), "type and ID mismatch rejected");
            check(!ui.toggleTrack(new lib.kalu.mediaplayer.bean.info.TrackInfo()), "missing ID rejected");
            player.setMediaSource("https://example.test/next.m3u8"); player.prepareAsync();
            engine.events.prepared(); scheduler.drain();
            check(!ui.toggleTrack(video.get(1)), "stale source ID rejected even when list indices match");
            player.stop();
            check(ui.getTrackInfoAll() == null && !ui.toggleTrack(subs.get(0)), "stopped player has no selectable tracks");
            player.release();
            check(ui.getTrackInfoAll() == null && !ui.toggleTrack(video.get(0)), "released player returns safe result");
        }
        System.out.println("VideoMpvPlayer tracks: " + assertions + " assertions passed; device playback not tested.");
    }
}
