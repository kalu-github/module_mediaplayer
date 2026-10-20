package lib.kalu.mpvplayer;

import android.view.Surface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/** Deterministic host tests of SDK policy/lifecycle. These do NOT assert device media playback. */
public final class SdkStateTest {
    static int assertions;
    static void check(boolean value,String message) { assertions++; if(!value) throw new AssertionError(message); }
    static Map<String,Object> map(Object... pairs) {
        Map<String,Object> result=new HashMap<>();
        for(int i=0;i<pairs.length;i+=2) result.put((String)pairs[i],pairs[i+1]);
        return result;
    }
    static Map<String,Object> track(String type,long id,boolean selected,long bitrate) {
        return map("id",id,"type",type,"selected",selected,"codec","video".equals(type) ? "h264" : "aac",
            "decoder",selected ? "decoder" : "", "hls-bitrate",bitrate,"program-id",id,
            "demux-w",1280L,"demux-h",720L,"lang","zh","title","中文 🎞️");
    }
    static final class Queue implements PlayerScheduler {
        long time=100,sequence;
        static final class Task { long when,order; Runnable run; Task(long w,long o,Runnable r) {when=w;order=o;run=r;} }
        final PriorityQueue<Task> tasks=new PriorityQueue<>(Comparator.comparingLong((Task t)->t.when).thenComparingLong(t->t.order));
        public long now() { return time; }
        public void post(Runnable action,long delay) { tasks.add(new Task(time+delay,sequence++,action)); }
        public void cancelAll() { tasks.clear(); }
        void advance(long duration) {
            long end=time+duration; int safety=10000;
            while(!tasks.isEmpty() && tasks.peek().when<=end) {
                if(--safety==0) throw new AssertionError("Scheduler loop");
                Task task=tasks.remove(); time=task.when; task.run.run();
            }
            time=end;
        }
    }
    static final class Engine implements PlayerEngine {
        Events callback;
        Map<String,Object> properties=new HashMap<>();
        Map<String,String> options=new HashMap<>();
        List<Map<String,Object>> mediaTracks=new ArrayList<>();
        boolean confirm=true,released;
        int loads,plays,resets,seeks;
        long seekPosition;
        Engine() {
            mediaTracks.add(track("video",1,true,1000000)); mediaTracks.add(track("video",2,false,3000000));
            mediaTracks.add(track("audio",1,true,-1)); mediaTracks.add(track("audio",2,false,-1));
            mediaTracks.add(track("sub",1,false,-1));
            properties.put("seekable",true); properties.put("time-pos",3.0); properties.put("duration",100.0);
            properties.put("demuxer-cache-time",9.0); properties.put("demuxer-cache-duration",6.0);
            properties.put("demuxer-cache-state",map("raw-input-rate",1000000L,"seekable-ranges",Arrays.asList(map("start",1.0,"end",9.0))));
        }
        public void events(Events e) { callback=e; }
        public void reset() { resets++; }
        public void source(MediaSource s) {}
        public void prepare() { loads++; }
        public void prepareSync() { loads++; }
        public void play() { plays++; }
        public void pause() {}
        public void stop() {}
        public void release() { released=true; }
        public void seek(long p) { seeks++;seekPosition=p; }
        public void surface(Surface s) {}
        public void surfaceSize(int w,int h) {}
        public void set(String key,String value) {
            options.put(key,value);
            if(confirm && Arrays.asList("vid","aid","sid").contains(key)) {
                String type=key.equals("vid") ? "video" : key.equals("aid") ? "audio" : "sub";
                if(!value.equals("auto")) choose(type,value.equals("no") ? -1 : Long.parseLong(value));
            }
        }
        void choose(String type,long id) {
            for(Map<String,Object> track:mediaTracks) if(type.equals(track.get("type"))) {
                boolean selected=((Number)track.get("id")).longValue()==id;
                track.put("selected",selected);track.put("decoder",selected ? "decoder" : "");
            }
        }
        public Object get(String key) { return key.equals("track-list") ? mediaTracks : properties.get(key); }
    }
    static final class Fixture {
        final Engine engine=new Engine(); final Queue queue=new Queue(); final MpvPlayer player;
        final List<String> events=new ArrayList<>(); final List<OperationResult> selections=new ArrayList<>();
        Fixture() { this(new PlayerConfig.Builder().build()); }
        Fixture(PlayerConfig config) {
            player=new MpvPlayer(engine,queue,config);
            player.addPlayerListener(new PlayerListener() {
                public void onPrepared(MpvPlayer p) { events.add("prepared"); }
                public void onPlaybackStateChanged(MpvPlayer p,MpvPlayer.PlaybackState s) { events.add(s.name()); }
                public void onError(MpvPlayer p,PlayerError e) { events.add("error"); }
                public void onRecoveryResult(MpvPlayer p,boolean value) { events.add("recovery:"+value); }
                public void onReleased(MpvPlayer p) { events.add("released"); }
            });
            player.addTrackListener(new TrackListener() {
                public void onTrackSelectionResult(MpvPlayer p,TrackInfo.Type t,String id,OperationResult r) { selections.add(r); }
            });
        }
        void load() {
            player.setMediaSource("https://example.test/master.m3u8"); player.prepareAsync();
            check(player.getPlaybackState()==MpvPlayer.PlaybackState.PREPARING,"prepare must be asynchronous");
            engine.callback.prepared(); queue.advance(0);
        }
        void close() { player.release();queue.advance(0); }
    }
    static void metadataAndSelection() {
        Fixture f=new Fixture(); f.load();
        check(f.player.getVideoTracks().size()==2,"video tracks");
        TrackInfo video=f.player.getVideoTracks().get(0),audio=f.player.getAudioTracks().get(0);
        check(!video.id.equals(audio.id),"ids must include track type");
        check(video.kind==TrackInfo.Kind.HLS_VARIANT && audio.kind==TrackInfo.Kind.DEMUXED_TRACK,"variant vs track");
        check(video.name.equals("中文 🎞️"),"Unicode metadata");
        check(video.mimeType.equals("video/avc") && video.bitrate==-1,"MIME and unavailable bitrate");
        check(video.programId==1,"HLS program id");
        check(f.player.isVideoTrackLocked(),"AUTO_THEN_FIXED must lock actual initial selection");
        String second=f.player.getVideoTracks().get(1).id;
        OperationResult result=f.player.selectVideoTrack(second);
        check(result.status==OperationResult.Status.ACCEPTED,"selection request is not completion");
        check(f.selections.isEmpty(),"must await observed selection");
        f.queue.advance(500);
        check(f.selections.get(0).status==OperationResult.Status.APPLIED,"selection confirmation");
        check(f.selections.get(0).requestId==result.requestId,"selection correlation");
        check(f.player.getSelectedVideoTrack().id.equals(second),"selected video");
        check(f.player.getVideoTrackMode()==MpvPlayer.VideoTrackMode.FIXED && f.player.isVideoTrackLocked(),"manual locks");
        f.player.disableAudioTrack();f.queue.advance(500);
        check(f.player.getSelectedAudioTrack()==null,"audio disabled");
        f.player.setMediaSource("https://example.test/next.m3u8"); f.player.prepareAsync();f.engine.callback.prepared();f.queue.advance(0);
        check(f.player.selectVideoTrack(second).status==OperationResult.Status.INVALID_ARGUMENT,"old ID rejected");
        check(f.player.getVideoTrackMode()==MpvPlayer.VideoTrackMode.AUTO_THEN_FIXED,"new source resets manual pin");
        f.close();
    }
    static void pendingAndTimeout() {
        Fixture f=new Fixture(new PlayerConfig.Builder().trackSwitchTimeoutMs(1000).build());f.load();f.engine.confirm=false;
        String id=f.player.getVideoTracks().get(1).id;
        f.player.selectVideoTrack(id);f.player.selectVideoTrack(id);f.queue.advance(0);
        check(f.selections.size()==1 && f.selections.get(0).status==OperationResult.Status.CANCELLED,"superseded selection cancelled");
        f.queue.advance(1000);
        check(f.selections.get(1).status==OperationResult.Status.FAILED,"unobserved switch times out");
        check(!f.player.isVideoTrackLocked(),"failed selection cannot claim lock");
        f.close();
    }
    static void adaptiveModes() {
        Fixture f=new Fixture();f.load();f.player.play();
        check(f.player.setVideoTrackMode(MpvPlayer.VideoTrackMode.AUTO).isSuccess(),"recognized HLS variants allow AUTO");
        f.queue.advance(11000);
        check(f.player.getSelectedVideoTrack().hlsBitrate==3000000,"bandwidth plus buffer allows upgrade");
        f.engine.properties.put("demuxer-cache-duration",0.4);
        f.queue.advance(10000);
        check(f.player.getSelectedVideoTrack().hlsBitrate==1000000,"low buffer downgrades");
        String high=f.player.getVideoTracks().get(1).id;f.player.selectVideoTrack(high);f.queue.advance(500);
        f.queue.advance(20000);
        check(f.player.getSelectedVideoTrack().id.equals(high),"FIXED must not adapt on low buffer");
        f.close();
        Fixture locked=new Fixture();locked.load();locked.player.play();locked.queue.advance(30000);
        check(locked.player.getSelectedVideoTrack().hlsBitrate==1000000,"AUTO_THEN_FIXED must stay pinned");locked.close();
    }
    static void retriesAndCancellation() {
        Fixture f=new Fixture(new PlayerConfig.Builder().retry(2,1000).autoPlay(true).build());f.load();
        check(f.engine.plays==1,"autoplay");
        f.engine.callback.error(-13);f.queue.advance(0);
        check(f.player.getPlaybackState()==MpvPlayer.PlaybackState.ERROR,"error state");
        f.queue.advance(1000);check(f.engine.loads==2,"retry reloads source");
        f.engine.callback.prepared();f.queue.advance(0);
        check(f.engine.seekPosition==3000 && f.events.contains("recovery:true"),"retry resumes seekable VOD and reports result");
        f.engine.callback.error(-13);f.queue.advance(0);
        f.player.stop();int loads=f.engine.loads;f.queue.advance(2000);
        check(f.engine.loads==loads,"stop cancels retry");
        f.close();
        Fixture exhausted=new Fixture(new PlayerConfig.Builder().retry(1,10).build());exhausted.load();
        exhausted.engine.callback.error(-13);exhausted.queue.advance(10);exhausted.engine.callback.error(-13);exhausted.queue.advance(100);
        check(exhausted.engine.loads==2 && exhausted.events.contains("recovery:false"),"retry budget is finite");exhausted.close();
    }
    static void staleEventsAndRelease() {
        Fixture f=new Fixture();f.load();PlayerEngine.Events old=f.engine.callback;
        f.player.setMediaSource("https://example.test/new.m3u8");f.events.clear();
        old.prepared();old.error(-13);old.completed();f.queue.advance(0);
        check(!f.events.contains("prepared") && !f.events.contains("error") && f.player.getPlaybackState()==MpvPlayer.PlaybackState.IDLE,"stale callbacks ignored");
        f.player.prepareAsync();old=f.engine.callback;f.player.release();old.prepared();f.queue.advance(0);
        check(f.engine.released && f.player.getPlaybackState()==MpvPlayer.PlaybackState.RELEASED,"release state");
        check(f.events.contains("released") && !f.events.contains("prepared"),"only final release callback survives");
        check(f.queue.tasks.isEmpty(),"no polling/retry tasks after release");
        f.player.release();
    }
    static void honestCapabilitiesAndValues() {
        Fixture f=new Fixture();f.player.setAutoPlay(true);f.load();check(f.engine.plays==1,"autoplay persists across setMediaSource");
        check(f.player.getBufferedPosition()==9000 && f.player.getBufferedDuration()==6000,"buffer units");
        check(f.player.getBufferedPercentage()==9,"buffer percentage");
        check(f.player.getPlaybackStats().estimatedBandwidth==8000000,"bytes per second converted to bits");
        check(f.player.getPlaybackStats().segmentFailureCount==-1,"unavailable segment failures not zero");
        check(!f.player.getLiveWindow().available && !f.player.supports(MpvPlayer.Capability.LIVE_EDGE),"cache window is not playlist window");
        check(f.player.getCachedSeekRanges().get(0).endMs==9000,"cached range");
        check(f.player.seekToLiveEdge().status==OperationResult.Status.UNSUPPORTED,"live-edge seek explicitly unsupported");
        f.engine.properties.put("seekable",false);f.engine.properties.put("partially-seekable",true);
        check(f.player.trySeekTo(20000).status==OperationResult.Status.UNSUPPORTED,"partial cache bounds");
        check(f.player.trySeekTo(2000).isSuccess(),"cached seek permitted");
        f.engine.properties.remove("duration");check(f.player.getDurationLong()==-1 && !f.player.isLive(),"missing duration does not imply live");
        f.close();
    }
    static void validation() {
        boolean rejected=false;
        try { new MediaSource("https://example.test",mapStrings("Cookie","ok\r\nInjected: true"),MediaSource.StreamType.UNKNOWN); }
        catch(IllegalArgumentException expected) { rejected=true; }
        check(rejected,"header injection rejected");
        rejected=false;try { new PlayerConfig.Builder().buffer(100,200,10).build(); } catch(IllegalArgumentException expected) {rejected=true;}
        check(rejected,"invalid buffer thresholds rejected");
        Map<String,String> headers=mapStrings("User-Agent","test");MediaSource source=new MediaSource("https://example.test",headers,MediaSource.StreamType.VOD);
        headers.clear();check(source.headers.size()==1,"source headers copied");
    }
    static Map<String,String> mapStrings(String key,String value) { Map<String,String> result=new HashMap<>();result.put(key,value);return result; }
    static void legacyCompatibility() throws Exception {
        Fixture f=new Fixture(); final List<String> oldEvents=new ArrayList<>();
        f.player.setOnPreparedListener(p -> {
            check(p==f.player,"legacy callback uses the public instance");
            check(!Thread.holdsLock(p),"legacy callback is outside player lock");
            oldEvents.add("prepared");
        });
        f.player.setOnInfoListener((p,what,extra) -> { oldEvents.add("info:"+what);return true; });
        f.player.setOnBufferingUpdateListener((p,percent) -> oldEvents.add("buffer:"+percent));
        f.player.setOnSeekCompleteListener(p -> oldEvents.add("seek"));
        f.player.setOnCompletionListener(p -> oldEvents.add("complete"));
        f.player.setOnErrorListener((p,what,extra) -> { oldEvents.add("error:"+extra);return true; });
        f.player.setDataSource("https://example.test/legacy.mp4",mapStrings("User-Agent","test"));
        boolean rejected=false;
        try { f.player.setDataSource("second"); } catch(IllegalStateException expected) { rejected=true; }
        check(rejected,"legacy setDataSource requires reset before replacement");
        f.player.prepare();
        check(f.player.getPlaybackState()==MpvPlayer.PlaybackState.READY,"legacy prepare completes synchronously");
        check(oldEvents.isEmpty(),"synchronous preparation still queues listeners");
        f.engine.callback.prepared(); f.queue.advance(0);
        check(oldEvents.size()==1 && f.events.contains("prepared"),"native prepared echo does not duplicate either listener");
        f.player.start();check(f.player.isPlaying(),"legacy start");
        f.player.setVolume(0.2f,0.8f);check(f.player.getVolume()==0.5f,"legacy stereo volume maps to master volume");
        f.player.setSpeed(3);check(f.player.getSpeed()==3,"legacy positive speed preserved");
        check(f.player.setPlaybackSpeed(3).status==OperationResult.Status.INVALID_ARGUMENT,"new speed contract remains bounded");
        f.player.seekTo(500);f.engine.callback.seeked();f.engine.callback.buffered(42);f.engine.callback.info(3);f.queue.advance(0);
        check(oldEvents.contains("seek") && oldEvents.contains("buffer:42") && oldEvents.contains("info:3"),"legacy seek/buffer/first frame forwarded");
        f.engine.properties.put("duration",3000000.0);
        check(f.player.getDuration()==Integer.MAX_VALUE && f.player.getDurationLong()==3000000000L,"legacy int time saturates and long time stays intact");
        f.engine.callback.error(-13);f.queue.advance(0);
        check(oldEvents.contains("error:-13") && !oldEvents.contains("complete"),"handled legacy error suppresses completion");
        f.player.reset();check(f.player.getDuration()==0 && f.player.getDurationLong()==-1,"unknown time keeps old/new contracts");
        f.player.setDataSource("next");f.player.prepareAsync();PlayerEngine.Events stale=f.engine.callback;
        f.player.reset();oldEvents.clear();stale.prepared();f.queue.advance(0);
        check(oldEvents.isEmpty(),"reset discards old legacy callbacks");
        f.player.setDataSource("next");f.player.prepareAsync();f.engine.callback.prepared();f.queue.advance(0);
        f.player.setOnErrorListener((p,what,extra) -> false);
        f.engine.callback.error(-13);f.queue.advance(0);
        check(oldEvents.contains("complete") && f.player.getPlaybackState()==MpvPlayer.PlaybackState.ERROR,"unhandled legacy error calls completion without changing SDK error state");
        f.close();
    }
    public static void main(String[] args) throws Exception {
        metadataAndSelection();pendingAndTimeout();adaptiveModes();retriesAndCancellation();staleEventsAndRelease();honestCapabilitiesAndValues();validation();legacyCompatibility();
        System.out.println("SDK state/policy tests passed: "+assertions+" assertions. Device playback is not verified by these tests.");
    }
}
