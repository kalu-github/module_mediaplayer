package lib.kalu.mpvplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.Surface;
import android.view.SurfaceHolder;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import lib.kalu.mpvplayer.inter.*;


/**
 * Unified playback API over libmpv; prefer prepareAsync() on the UI thread. All time values are milliseconds.
 * Public methods are serialized; native work runs on a private worker and getters read snapshots.
 * Listener calls are queued on the main thread outside player locks. prepare() explicitly waits.
 * The caller owns the Surface and must detach it before destroying it. Close this player on teardown.
 */
public class MpvPlayer implements AutoCloseable {
    public static final int MEDIA_ERROR_UNKNOWN = 1;
    public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3;
    public static final int MEDIA_INFO_BUFFERING_START = 701;
    public static final int MEDIA_INFO_BUFFERING_END = 702;

    public enum PlaybackState { IDLE, PREPARING, READY, PLAYING, PAUSED, BUFFERING, ENDED, ERROR, RELEASED }
    public enum VideoTrackMode { AUTO, FIXED, AUTO_THEN_FIXED }
    public enum ResizeMode { ORIGINAL, FIT, FILL, ZOOM }
    public enum Capability {
        TRACK_SELECTION, ADAPTIVE_HLS_VARIANTS, CACHED_SEEK_RANGES, PLATFORM_DECODER_QUERY,
        HLS_RENDITION_GROUP_METADATA, LIVE_PLAYLIST_WINDOW, LIVE_EDGE, LIVE_TARGET_DELAY,
        INDEPENDENT_CONNECT_READ_TIMEOUTS, INDEPENDENT_START_REBUFFER_THRESHOLDS,
        HTTP_STATUS, PER_SEGMENT_STATS, NETWORK_CONNECTIVITY_EVENTS, SUBTITLE_CLIP_RECT, CLEAR_VIDEO_PIXELS
    }
    public static final class TimeRange {
        public final long startMs,endMs;
        TimeRange(long start,long end) { startMs=start; endMs=end; }
    }
    /** Origin playlist timing is unavailable from this backend; cached seek ranges are a separate API. */
    public static final class LiveWindow {
        public final boolean available=false;
        public final long startMs=-1,endMs=-1,latencyMs=-1;
        private LiveWindow() {}
    }
    private final PlayerEngine engine;
    private final PlayerScheduler scheduler;
    private final PlayerConfig config;
    private final CopyOnWriteArraySet<PlayerListener> players=new CopyOnWriteArraySet<>();
    private final CopyOnWriteArraySet<TrackListener> trackListeners=new CopyOnWriteArraySet<>();
    private final CopyOnWriteArraySet<AnalyticsListener> analytics=new CopyOnWriteArraySet<>();
    private final EnumMap<TrackInfo.Type,List<TrackInfo>> tracks=new EnumMap<>(TrackInfo.Type.class);
    private final EnumMap<TrackInfo.Type,PendingTrack> pending=new EnumMap<>(TrackInfo.Type.class);
    private PlaybackState state=PlaybackState.IDLE;
    private MediaSource source;
    private long epoch, requestSequence, preparedAt, firstFrame=-1, stallStarted=-1, stalledMs;
    private long lastAdaptiveSwitch, resumePosition=-1;
    private int stalls,switches,retries;
    private boolean ready, autoPlay, playWhenReady, buffering, polling, recovering, audioDisabled, subtitleDisabled, errorHandled;
    private boolean looping,muted,hardwareDecoding=true,renderingSuspended;
    private float volume=1,speed=1;
    private String audioLanguage="",subtitleLanguage="",lockedVideoId;
    private TrackInfo fixedVideoPreference;
    private VideoTrackMode videoMode=VideoTrackMode.AUTO_THEN_FIXED;
    private ResizeMode resizeMode=ResizeMode.FIT;
    private SubtitleStyle subtitleStyle;
    private Surface surface;
    private int surfaceWidth,surfaceHeight;
    private PlaybackStats stats;
    private Set<String> decoderCodecs;
    private ParcelFileDescriptor sourceDescriptor;
    private long sourceOffset,sourceLength;
    private SurfaceHolder displayHolder;
    private boolean screenOn;
    private final Map<String,String> extraProperties=new LinkedHashMap<>();
    private volatile OnPreparedListener onPreparedListener;
    private volatile OnCompletionListener onCompletionListener;
    private volatile OnBufferingUpdateListener onBufferingUpdateListener;
    private volatile OnSeekCompleteListener onSeekCompleteListener;
    private volatile OnVideoSizeChangedListener onVideoSizeChangedListener;
    private volatile OnErrorListener onErrorListener;
    private volatile OnInfoListener onInfoListener;
    private volatile OnTimedTextListener onTimedTextListener;
    private final PlayerListener legacyListener=new PlayerListener() {
        public void onPrepared(MpvPlayer p) { OnPreparedListener l=onPreparedListener; if(l!=null) l.onPrepared(p); }
        public void onCompletion(MpvPlayer p) { OnCompletionListener l=onCompletionListener; if(l!=null) l.onCompletion(p); }
        public void onBufferingUpdate(MpvPlayer p,int percent) { OnBufferingUpdateListener l=onBufferingUpdateListener; if(l!=null) l.onBufferingUpdate(p,percent); }
        public void onSeekComplete(MpvPlayer p) { OnSeekCompleteListener l=onSeekCompleteListener; if(l!=null) l.onSeekComplete(p); }
        public void onVideoSizeChanged(MpvPlayer p,int w,int h) { OnVideoSizeChangedListener l=onVideoSizeChangedListener; if(l!=null) l.onVideoSizeChanged(p,w,h); }
        public void onFirstFrame(MpvPlayer p,long elapsed) { info(MEDIA_INFO_VIDEO_RENDERING_START); }
        public void onBufferingChanged(MpvPlayer p,boolean value) { info(value ? MEDIA_INFO_BUFFERING_START : MEDIA_INFO_BUFFERING_END); }
        private void info(int what) { OnInfoListener l=onInfoListener; if(l!=null) l.onInfo(MpvPlayer.this,what,0); }
        public void onSubtitle(MpvPlayer p,String text) { OnTimedTextListener l=onTimedTextListener; if(l!=null) l.onTimedText(p,TimedText.fromText(text)); }
        public void onError(MpvPlayer p,PlayerError error) {
            OnErrorListener l=onErrorListener;
            boolean handled=l!=null && l.onError(p,MEDIA_ERROR_UNKNOWN,error.code);
            if(!handled && p.getPlaybackState()==PlaybackState.ERROR) onCompletion(p);
        }
    };

    public MpvPlayer() { this(new PlayerConfig.Builder().build()); }
    public MpvPlayer(PlayerConfig config) { this(createEngine(config),new MainPlayerScheduler(),config); }
    private static PlayerEngine createEngine(PlayerConfig config) { Objects.requireNonNull(config); return new AsyncPlayerEngine(NativePlayerEngine::new, config.progressIntervalMs); }
    MpvPlayer(PlayerEngine engine,PlayerScheduler scheduler,PlayerConfig config) {
        this.engine=Objects.requireNonNull(engine); this.scheduler=Objects.requireNonNull(scheduler);
        this.config=Objects.requireNonNull(config); autoPlay=config.autoPlay; playWhenReady=autoPlay;
        for (TrackInfo.Type type:TrackInfo.Type.values()) tracks.put(type,Collections.emptyList());
        players.add(legacyListener);
        bindEvents();
    }

    private interface PlayerCall { void call(PlayerListener listener); }
    private interface TrackCall { void call(TrackListener listener); }
    private void emit(PlayerCall call) {
        long token=epoch;
        List<PlayerListener> recipients=new ArrayList<>(players);
        scheduler.post(() -> {
            for (PlayerListener listener:recipients) {
                synchronized (MpvPlayer.this) { if (token!=epoch || !players.contains(listener)) continue; }
                call.call(listener);
            }
        },0);
    }
    private void emitTrack(TrackCall call) {
        long token=epoch;
        List<TrackListener> recipients=new ArrayList<>(trackListeners);
        scheduler.post(() -> {
            for (TrackListener listener:recipients) {
                synchronized (MpvPlayer.this) { if (token!=epoch || !trackListeners.contains(listener)) continue; }
                call.call(listener);
            }
        },0);
    }
    private void bindEvents() {
        final long token=epoch;
        engine.events(new PlayerEngine.Events() {
            private void dispatch(Runnable action) {
                // Never acquire the SDK lock while MpvPlayer holds its callback monitor.
                scheduler.post(() -> { synchronized(MpvPlayer.this) {
                    if (token==epoch && state!=PlaybackState.RELEASED) action.run();
                } },0);
            }
            public void buffered(int percent) { dispatch(() -> emit(l -> l.onBufferingUpdate(MpvPlayer.this,percent))); }
            public void prepared() { dispatch(MpvPlayer.this::onPrepared); }
            public void completed() { dispatch(() -> {
                if(stats!=null && !isLive()) stats=stats.atEnd();
                ready=false; playWhenReady=false; finishStall(); buffering=false;
                changeState(PlaybackState.ENDED); cancelPending("Playback ended");
                emit(l -> l.onCompletion(MpvPlayer.this));
            }); }
            public void error(int code) { dispatch(() -> onError(PlayerError.nativeError(code))); }
            public void failure(Throwable cause) { dispatch(() -> onError(new PlayerError(0,PlayerError.Type.INTERNAL,
                "Native player operation failed",cause,false,false))); }
            public void info(int what) { dispatch(() -> onInfo(what)); }
            public void size(int w,int h) { dispatch(() -> emit(l -> l.onVideoSizeChanged(MpvPlayer.this,w,h))); }
            public void seeked() { dispatch(() -> emit(l -> l.onSeekComplete(MpvPlayer.this))); }
            public void subtitle(String text) { dispatch(() -> emit(l -> l.onSubtitle(MpvPlayer.this,text))); }
        });
    }
    private void alive() { if (state==PlaybackState.RELEASED) throw new IllegalStateException("Player released"); }
    private void changeState(PlaybackState next) {
        if (state==next) return;
        state=next; updateScreen(); emit(l -> l.onPlaybackStateChanged(this,next));
    }
    public void addPlayerListener(PlayerListener listener) { synchronized(this) { alive(); players.add(Objects.requireNonNull(listener)); } }
    public void removePlayerListener(PlayerListener listener) { players.remove(listener); }
    public void addTrackListener(TrackListener listener) { synchronized(this) { alive(); trackListeners.add(Objects.requireNonNull(listener)); } }
    public void removeTrackListener(TrackListener listener) { trackListeners.remove(listener); }
    public void addAnalyticsListener(AnalyticsListener listener) { synchronized(this) { alive(); analytics.add(Objects.requireNonNull(listener)); } }
    public void removeAnalyticsListener(AnalyticsListener listener) { analytics.remove(listener); }

    public synchronized void setMediaSource(String uri) { setMediaSource(new MediaSource(uri)); }
    public synchronized void setMediaSource(MediaSource value) {
        alive(); Objects.requireNonNull(value);
        invalidate(); engine.reset(); closeSourceDescriptor(); source=value; ready=false; retries=0; recovering=false;
        resumePosition=-1; playWhenReady=autoPlay; audioDisabled=false; subtitleDisabled=false;
        fixedVideoPreference=null;
        if (videoMode==VideoTrackMode.FIXED) changeVideoMode(VideoTrackMode.AUTO_THEN_FIXED);
        clearMediaState(); bindEvents(); changeState(PlaybackState.IDLE);
    }
    /** Nonblocking: completion is reported by onPrepared. */
    public synchronized void prepareAsync() {
        alive();
        if (source==null) throw new IllegalStateException("Set a media source first");
        if (state==PlaybackState.PREPARING || ready) throw new IllegalStateException("Stop or reload before preparing again");
        load(false);
    }
    private void invalidate() { epoch++; scheduler.cancelAll(); polling=false; pending.clear(); }
    private void clearMediaState() {
        for (TrackInfo.Type type:TrackInfo.Type.values()) tracks.put(type,Collections.emptyList());
        lockedVideoId=null; buffering=false; firstFrame=-1; stallStarted=-1; stalledMs=0; stalls=0; switches=0; stats=null; decoderCodecs=null; errorHandled=false;
    }
    /** Synchronous compatibility API. Prefer prepareAsync() to avoid blocking the UI. */
    public synchronized void prepare() throws IOException {
        alive(); requireSource();
        if(state==PlaybackState.PREPARING || ready) throw new IllegalStateException("Stop or reload before preparing again");
        load(false,true);
    }
    private void load(boolean recovery) {
        try { load(recovery,false); } catch(IOException ignored) { /* Already reported through onError. */ }
    }
    private void load(boolean recovery,boolean wait) throws IOException {
        invalidate(); ready=false; clearMediaState(); recovering=recovery;
        try {
            engine.reset(); bindEvents(); applyOptions();
            if(sourceDescriptor!=null) engine.source(sourceDescriptor.getFileDescriptor(),sourceOffset,sourceLength);
            else engine.source(source);
            preparedAt=scheduler.now(); lastAdaptiveSwitch=preparedAt;
            changeState(PlaybackState.PREPARING);
            if(wait) {
                engine.prepareSync(); onPrepared();
                if(state==PlaybackState.ERROR) throw new IOException("Prepared media could not be initialized");
            } else engine.prepare();
        } catch(IOException e) {
            onError(new PlayerError(0,PlayerError.Type.SOURCE_UNAVAILABLE,"Preparing source failed",e,false,false));
            throw e;
        } catch(RuntimeException e) {
            onError(internal("Preparing source failed",e));
            if(wait) throw new IOException("Preparing source failed",e);
        }
    }
    private void applyOptions() {
        engine.set("network-timeout",seconds(config.networkTimeoutMs));
        engine.set("cache","yes"); engine.set("cache-secs",seconds(config.maxBufferMs));
        engine.set("demuxer-readahead-secs",seconds(config.maxBufferMs));
        engine.set("demuxer-max-bytes",Long.toString(config.maxBufferBytes));
        engine.set("cache-pause","yes"); engine.set("cache-pause-wait",seconds(config.rebufferMs));
        engine.set("cache-pause-initial",config.rebufferMs>0 ? "yes" : "no");
        engine.set("loop-file",looping ? "inf" : "no"); engine.set("mute",muted ? "yes" : "no");
        engine.set("volume",Float.toString(volume*100)); engine.set("speed",Float.toString(speed));
        engine.set("hwdec",hardwareDecoding ? "mediacodec-copy" : "no");
        engine.set("alang",audioLanguage); engine.set("slang",subtitleLanguage);
        engine.set("aid",audioDisabled ? "no" : "auto"); engine.set("sid",subtitleDisabled ? "no" : "auto");
        engine.set("vid","auto");
        // Begin conservatively. Adaptive changes are made by the SDK, never by hls-bitrate alone.
        engine.set("hls-bitrate","min");
        for(Map.Entry<String,String> entry:extraProperties.entrySet()) engine.set(entry.getKey(),entry.getValue());
        applyResize(); if (subtitleStyle!=null) applySubtitleStyle(subtitleStyle);
        engine.surface(renderingSuspended ? null : surface);
        if (surfaceWidth>0 && surfaceHeight>0) engine.surfaceSize(surfaceWidth,surfaceHeight);
    }
    private static String seconds(long ms) { return Double.toString(ms/1000.0); }
    private void onPrepared() {
        if (state!=PlaybackState.PREPARING) return;
        ready=true; changeState(PlaybackState.READY);
        try {
            refreshTracks();
            if(videoMode==VideoTrackMode.FIXED && fixedVideoPreference!=null) {
                TrackInfo match=null;
                for(TrackInfo candidate:getVideoTracks()) if(sameVariant(candidate,fixedVideoPreference)) {
                    if(match!=null) { match=null; break; } // Ambiguous identities must not silently select another stream.
                    match=candidate;
                }
                if(match==null) {
                    onError(new PlayerError(0,PlayerError.Type.UNSUPPORTED_TRACK,"Previously fixed video track is unavailable after reload",null,false,false));
                    return;
                }
                OperationResult selection=requestTrack(TrackInfo.Type.VIDEO,match.id,Long.toString(match.nativeId));
                if(!selection.isSuccess()) { onError(new PlayerError(0,PlayerError.Type.TRACK_SWITCH,selection.message,null,false,false)); return; }
            }
            stats=readStats();
            if (resumePosition>=0 && isSeekable()) engine.seek(resumePosition);
            resumePosition=-1;
            emit(l -> l.onPrepared(this));
            if (playWhenReady) startPlayback(false);
            if (recovering) { recovering=false; emit(l -> l.onRecoveryResult(this,true)); }
            schedulePoll();
        } catch (RuntimeException e) { onError(internal("Starting prepared media failed",e)); }
    }
    public synchronized void play() {
        alive(); playWhenReady=true;
        if (state==PlaybackState.PREPARING) return;
        if (state==PlaybackState.PLAYING || state==PlaybackState.BUFFERING) return;
        if (!ready) { prepareAsync(); return; }
        startPlayback(state==PlaybackState.PAUSED);
    }
    private void startPlayback(boolean resumed) {
        engine.play(); playWhenReady=true;
        if(buffering && stallStarted<0) { stalls++; stallStarted=scheduler.now(); }
        changeState(buffering ? PlaybackState.BUFFERING : PlaybackState.PLAYING);
        if (resumed) emit(l -> l.onResumed(this)); else emit(l -> l.onPlaybackStarted(this));
    }
    public synchronized void resume() { play(); }
    public synchronized void pause() {
        alive(); playWhenReady=false;
        if (state==PlaybackState.PREPARING) return;
        if (!ready) throw new IllegalStateException("Media is not ready");
        engine.pause(); finishStall(); changeState(PlaybackState.PAUSED); emit(l -> l.onPaused(this));
    }
    public synchronized void stop() {
        alive(); invalidate(); engine.reset(); bindEvents();
        ready=false; playWhenReady=false; recovering=false; retries=0; resumePosition=-1;
        finishStall(); clearMediaState(); changeState(PlaybackState.IDLE); emit(l -> l.onStopped(this));
    }
    public synchronized void replay() { alive(); requireSource(); retries=0; resumePosition=-1; playWhenReady=true; load(false); }
    public synchronized void reload() {
        alive(); requireSource(); resumePosition=ready && isSeekable() && !isLive() ? getCurrentPositionLong() : -1;
        retries=0; load(false);
    }
    private void requireSource() { if (source==null) throw new IllegalStateException("Set a media source first"); }
    public synchronized void setAutoPlay(boolean enabled) { alive(); autoPlay=enabled; if(!ready) playWhenReady=enabled; }
    public synchronized boolean getAutoPlay() { return autoPlay; }
    public synchronized PlaybackState getPlaybackState() { return state; }
    public synchronized boolean isPlaying() { return ready && playWhenReady && (state==PlaybackState.PLAYING || state==PlaybackState.BUFFERING); }
    public synchronized boolean isBuffering() { return buffering; }
    public synchronized boolean isLive() { return source!=null && source.streamType==MediaSource.StreamType.LIVE; }
    public synchronized MediaSource.StreamType getStreamType() { return source==null ? MediaSource.StreamType.UNKNOWN : source.streamType; }
    public synchronized boolean isSeekable() { return ready && (flag("seekable") || (flag("partially-seekable") && !getCachedSeekRanges().isEmpty())); }
    public synchronized long getCurrentPositionLong() { alive(); return ready ? millis("time-pos") : stats==null ? 0 : stats.positionMs; }
    public synchronized long getDurationLong() { alive(); return ready ? millis("duration") : stats==null ? -1 : stats.durationMs; }
    public synchronized long getBufferedPosition() { alive(); return ready ? millis("demuxer-cache-time") : stats==null ? -1 : stats.bufferedPositionMs; }
    public synchronized long getBufferedDuration() { alive(); return ready ? millis("demuxer-cache-duration") : stats==null ? -1 : stats.bufferedDurationMs; }
    public synchronized int getBufferedPercentage() {
        long duration=getDurationLong(), end=getBufferedPosition();
        return duration>0 && end>=0 ? (int)Math.max(0,Math.min(100,end*100.0/duration)) : -1;
    }
    public synchronized OperationResult trySeekTo(long positionMs) {
        alive();
        if (positionMs<0) return result(OperationResult.Status.INVALID_ARGUMENT,"Seek position must be nonnegative");
        if (!ready) return result(OperationResult.Status.INVALID_STATE,"Media is not ready");
        if (!isSeekable()) return unsupported("This media has no known seekable range");
        if (!flag("seekable")) {
            boolean inside=false;
            for (TimeRange range:getCachedSeekRanges()) if (positionMs>=range.startMs && positionMs<=range.endMs) inside=true;
            if (!inside) return unsupported("Position is outside the cached seek ranges");
        }
        try { engine.seek(positionMs); return result(OperationResult.Status.ACCEPTED,"Waiting for seek completion"); }
        catch (RuntimeException e) { return result(OperationResult.Status.FAILED,e.toString()); }
    }
    public synchronized OperationResult fastForward(long deltaMs) { return seekRelative(deltaMs,false); }
    public synchronized OperationResult rewind(long deltaMs) { return seekRelative(deltaMs,true); }
    private OperationResult seekRelative(long delta,boolean back) {
        if (delta<0) return result(OperationResult.Status.INVALID_ARGUMENT,"Negative delta");
        long position=getCurrentPositionLong();
        if (position<0) return unsupported("Position is unavailable");
        long target=back ? Math.max(0,position-delta) : delta>Long.MAX_VALUE-position ? Long.MAX_VALUE : position+delta;
        long duration=getDurationLong(); if (duration>=0) target=Math.min(target,duration);
        return trySeekTo(target);
    }
    @SuppressWarnings("unchecked")
    public synchronized List<TimeRange> getCachedSeekRanges() {
        alive(); if (!ready) return Collections.emptyList();
        Object cache=engine.get("demuxer-cache-state");
        Object ranges=cache instanceof Map ? ((Map<?,?>)cache).get("seekable-ranges") : null;
        List<TimeRange> result=new ArrayList<>();
        if (ranges instanceof List) for (Object value:(List<?>)ranges) if (value instanceof Map) {
            Map<String,Object> item=(Map<String,Object>)value;
            double start=TrackInfo.decimal(item,"start",-1),end=TrackInfo.decimal(item,"end",-1);
            if (start>=0 && end>=start) result.add(new TimeRange((long)(start*1000),(long)(end*1000)));
        }
        return Collections.unmodifiableList(result);
    }
    public LiveWindow getLiveWindow() { return new LiveWindow(); }
    public OperationResult seekToLiveEdge() { return unsupported("libmpv does not expose the origin HLS playlist edge"); }
    public OperationResult setLiveTargetDelay(long ms) { return unsupported("Origin playlist timing is unavailable"); }
    public OperationResult setConnectionAndReadTimeouts(long connectMs,long readMs) { return unsupported("Use PlayerConfig.networkTimeoutMs; separate timeout stages are unavailable"); }

    public synchronized OperationResult setPlaybackSpeed(float value) {
        alive();
        if (Float.isNaN(value) || Float.isInfinite(value) || value<0.5f || value>2)
            return result(OperationResult.Status.INVALID_ARGUMENT,"Supported speed range is 0.5..2.0");
        if (value!=1 && isLive() && !isSeekable()) return unsupported("Speed changes require a seekable live buffer");
        try { engine.set("speed",Float.toString(value)); speed=value; return applied(); }
        catch (RuntimeException e) { return result(OperationResult.Status.FAILED,e.toString()); }
    }
    public synchronized float getPlaybackSpeed() { return speed; }
    public synchronized void setVolume(float value) {
        alive(); if (Float.isNaN(value) || Float.isInfinite(value) || value<0 || value>1) throw new IllegalArgumentException("Volume must be 0..1");
        engine.set("volume",Float.toString(value*100)); volume=value;
    }
    public synchronized float getVolume() { return volume; }
    public synchronized void setMuted(boolean value) { alive(); engine.set("mute",value ? "yes" : "no"); muted=value; }
    public synchronized boolean isMuted() { return muted; }
    public synchronized void setLooping(boolean value) { alive(); engine.set("loop-file",value ? "inf" : "no"); looping=value; }
    public synchronized boolean isLooping() { return looping; }
    public synchronized void setHardwareDecoding(boolean value) { alive(); engine.set("hwdec",value ? "mediacodec-copy" : "no"); hardwareDecoding=value; }
    public synchronized String getActiveHardwareDecoder() { alive(); return string("hwdec-current"); }

    public synchronized void setSurface(Surface value) {
        alive(); if (value!=null && !value.isValid()) throw new IllegalArgumentException("Invalid Surface");
        engine.surface(value); surface=value; renderingSuspended=false;
        if(displayHolder!=null) displayHolder.setKeepScreenOn(false);
        displayHolder=null;
    }
    public synchronized void clearSurface() { setSurface(null); }
    public synchronized void setSurfaceSize(int width,int height) {
        alive(); if (width<=0 || height<=0) throw new IllegalArgumentException("Surface dimensions must be positive");
        engine.surfaceSize(width,height); surfaceWidth=width; surfaceHeight=height;
    }
    public synchronized void pauseVideoRendering() { alive(); engine.surface(null); renderingSuspended=true; }
    public synchronized void resumeVideoRendering() { alive(); engine.surface(surface); renderingSuspended=false; }
    public OperationResult clearVideoFrame() { return unsupported("Detach the Surface and clear or replace the caller-owned view"); }
    public synchronized void setResizeMode(ResizeMode value) { alive(); ResizeMode previous=resizeMode; resizeMode=Objects.requireNonNull(value); try { applyResize(); } catch(RuntimeException e) { resizeMode=previous; throw e; } }
    private void applyResize() {
        engine.set("keepaspect",resizeMode==ResizeMode.FILL ? "no" : "yes");
        engine.set("video-unscaled",resizeMode==ResizeMode.ORIGINAL ? "yes" : "no");
        engine.set("panscan",resizeMode==ResizeMode.ZOOM ? "1" : "0");
    }
    public synchronized ResizeMode getResizeMode() { return resizeMode; }
    public synchronized int getVideoWidth() { alive(); return (int)number("dwidth",-1); }
    public synchronized int getVideoHeight() { alive(); return (int)number("dheight",-1); }
    public synchronized int getVideoRotation() { TrackInfo t=getSelectedVideoTrack(); return t==null ? 0 : t.rotation; }
    public synchronized double getVideoPixelAspectRatio() { TrackInfo t=getSelectedVideoTrack(); return t==null ? -1 : t.pixelAspectRatio; }
    public synchronized void setSubtitleStyle(SubtitleStyle style) { alive(); applySubtitleStyle(Objects.requireNonNull(style)); subtitleStyle=style; }
    private void applySubtitleStyle(SubtitleStyle style) {
        engine.set("sub-ass-override",style.overrideAss ? "force" : "no");
        engine.set("sub-font-size",Float.toString(style.fontSize)); engine.set("sub-pos",Integer.toString(style.positionPercent));
        engine.set("sub-color",color(style.textColor,style.opacity));
        engine.set("sub-back-color",color(style.backgroundColor,style.opacity));
        engine.set("sub-outline-color",color(style.outlineColor,style.opacity));
        engine.set("sub-margin-x",Integer.toString(style.marginX)); engine.set("sub-margin-y",Integer.toString(style.marginY));
    }
    private static String color(int argb,float opacity) { return String.format(Locale.ROOT,"#%08X",(Math.round((argb>>>24)*opacity)<<24)|(argb&0xffffff)); }

    public synchronized List<TrackInfo> getVideoTracks() { return tracks.get(TrackInfo.Type.VIDEO); }
    public synchronized List<TrackInfo> getAudioTracks() { return tracks.get(TrackInfo.Type.AUDIO); }
    public synchronized List<TrackInfo> getSubtitleTracks() { return tracks.get(TrackInfo.Type.SUBTITLE); }
    public synchronized TrackInfo getSelectedVideoTrack() { return selected(TrackInfo.Type.VIDEO); }
    public synchronized TrackInfo getSelectedAudioTrack() { return selected(TrackInfo.Type.AUDIO); }
    public synchronized TrackInfo getSelectedSubtitleTrack() { return selected(TrackInfo.Type.SUBTITLE); }
    private TrackInfo selected(TrackInfo.Type type) { for (TrackInfo t:tracks.get(type)) if (t.selected) return t; return null; }
    public synchronized OperationResult selectVideoTrack(String id) {
        OperationResult result=select(TrackInfo.Type.VIDEO,id);
        if (result.isSuccess()) {
            for(TrackInfo track:getVideoTracks()) if(track.id.equals(id)) fixedVideoPreference=track;
            lockedVideoId=null; changeVideoMode(VideoTrackMode.FIXED);
        }
        return result;
    }
    public synchronized OperationResult selectAudioTrack(String id) { OperationResult r=select(TrackInfo.Type.AUDIO,id); if(r.isSuccess()) audioDisabled=false; return r; }
    public synchronized OperationResult selectSubtitleTrack(String id) { OperationResult r=select(TrackInfo.Type.SUBTITLE,id); if(r.isSuccess()) subtitleDisabled=false; return r; }
    private OperationResult select(TrackInfo.Type type,String id) {
        alive(); if (!ready) return result(OperationResult.Status.INVALID_STATE,"Tracks are not ready");
        for (TrackInfo track:tracks.get(type)) if (track.id.equals(id))
            return track.support==TrackInfo.Support.UNSUPPORTED ? unsupported("No decoder for this track codec") : requestTrack(type,id,Long.toString(track.nativeId));
        return result(OperationResult.Status.INVALID_ARGUMENT,"Unknown or stale session track ID");
    }
    public synchronized OperationResult disableAudioTrack() { OperationResult r=requestTrack(TrackInfo.Type.AUDIO,null,"no"); if(r.isSuccess()) audioDisabled=true; return r; }
    public synchronized OperationResult restoreAudioTrack() { OperationResult r=requestTrack(TrackInfo.Type.AUDIO,null,"auto"); if(r.isSuccess()) audioDisabled=false; return r; }
    public synchronized OperationResult disableSubtitleTrack() { OperationResult r=requestTrack(TrackInfo.Type.SUBTITLE,null,"no"); if(r.isSuccess()) subtitleDisabled=true; return r; }
    public synchronized OperationResult restoreSubtitleTrack() { OperationResult r=requestTrack(TrackInfo.Type.SUBTITLE,null,"auto"); if(r.isSuccess()) subtitleDisabled=false; return r; }
    public synchronized OperationResult setPreferredAudioLanguage(String language) {
        alive(); validateLanguage(language); engine.set("alang",language); audioLanguage=language;
        return ready && !audioDisabled ? restoreAudioTrack() : applied();
    }
    public synchronized OperationResult setPreferredSubtitleLanguage(String language) {
        alive(); validateLanguage(language); engine.set("slang",language); subtitleLanguage=language;
        return ready && !subtitleDisabled ? restoreSubtitleTrack() : applied();
    }
    private static void validateLanguage(String value) { if(value==null || (!value.isEmpty() && !value.matches("[A-Za-z0-9-]+(,[A-Za-z0-9-]+)*"))) throw new IllegalArgumentException("Expected comma-separated language tags"); }
    private static String option(TrackInfo.Type type) { return type==TrackInfo.Type.VIDEO ? "vid" : type==TrackInfo.Type.AUDIO ? "aid" : "sid"; }
    private OperationResult requestTrack(TrackInfo.Type type,String id,String value) {
        alive(); if(!ready) return result(OperationResult.Status.INVALID_STATE,"Tracks are not ready");
        long request=++requestSequence;
        PendingTrack old=pending.remove(type);
        if(old!=null) trackResult(type,old,OperationResult.Status.CANCELLED,"Superseded by a newer selection");
        PendingTrack task=new PendingTrack(id,value,request,scheduler.now()+config.trackSwitchTimeoutMs);
        try {
            engine.set(option(type),value); pending.put(type,task); schedulePoll();
            return OperationResult.of(OperationResult.Status.ACCEPTED,"Waiting for actual selected track",request);
        } catch(RuntimeException e) {
            trackResult(type,task,OperationResult.Status.FAILED,e.toString());
            return OperationResult.of(OperationResult.Status.FAILED,e.toString(),request);
        }
    }
    private static final class PendingTrack {
        final String id,value; final long request,deadline;
        PendingTrack(String id,String value,long request,long deadline) { this.id=id;this.value=value;this.request=request;this.deadline=deadline; }
    }
    private void trackResult(TrackInfo.Type type,PendingTrack task,OperationResult.Status status,String message) {
        OperationResult result=OperationResult.of(status,message,task.request);
        emitTrack(l -> l.onTrackSelectionResult(this,type,task.id,result));
    }
    private void cancelPending(String message) {
        for(Map.Entry<TrackInfo.Type,PendingTrack> entry:pending.entrySet()) trackResult(entry.getKey(),entry.getValue(),OperationResult.Status.CANCELLED,message);
        pending.clear();
    }
    public synchronized VideoTrackMode getVideoTrackMode() { return videoMode; }
    public synchronized boolean isVideoTrackLocked() { return lockedVideoId!=null && videoMode!=VideoTrackMode.AUTO; }
    public synchronized OperationResult setVideoTrackMode(VideoTrackMode mode) {
        alive(); if(mode==null) return result(OperationResult.Status.INVALID_ARGUMENT,"Missing mode");
        if(mode==VideoTrackMode.FIXED) {
            TrackInfo current=getSelectedVideoTrack();
            return current==null ? result(OperationResult.Status.INVALID_STATE,"Select a video track first") : selectVideoTrack(current.id);
        }
        if(mode==VideoTrackMode.AUTO && (!ready || !supports(Capability.ADAPTIVE_HLS_VARIANTS)))
            return unsupported("AUTO requires at least two identified HLS video variants after onPrepared");
        PendingTrack old=pending.remove(TrackInfo.Type.VIDEO);
        if(old!=null) trackResult(TrackInfo.Type.VIDEO,old,OperationResult.Status.CANCELLED,"Video mode changed");
        lockedVideoId=null; fixedVideoPreference=null; changeVideoMode(mode); return applied();
    }
    private static boolean sameVariant(TrackInfo a,TrackInfo b) {
        return a.kind==b.kind && a.hlsBitrate==b.hlsBitrate && a.programId==b.programId
            && a.width==b.width && a.height==b.height && a.codec.equals(b.codec)
            && a.name.equals(b.name) && a.language.equals(b.language);
    }
    private void changeVideoMode(VideoTrackMode mode) {
        if(videoMode==mode) return;
        videoMode=mode; emitTrack(l -> l.onVideoTrackModeChanged(this,mode));
    }
    @SuppressWarnings("unchecked")
    private void refreshTracks() {
        readDecoderCodecs();
        Object property=engine.get("track-list");
        if(!(property instanceof List)) return;
        EnumMap<TrackInfo.Type,List<TrackInfo>> updated=new EnumMap<>(TrackInfo.Type.class);
        for(TrackInfo.Type type:TrackInfo.Type.values()) updated.put(type,new ArrayList<>());
        for(Object item:(List<?>)property) if(item instanceof Map) {
            Map<String,Object> data=(Map<String,Object>)item;
            String name=TrackInfo.text(data,"type");
            TrackInfo.Type type="video".equals(name) ? TrackInfo.Type.VIDEO : "audio".equals(name) ? TrackInfo.Type.AUDIO : "sub".equals(name) ? TrackInfo.Type.SUBTITLE : null;
            if(type!=null && TrackInfo.integer(data,"id",-1)>=0) updated.get(type).add(new TrackInfo(epoch,type,data,decoderCodecs));
        }
        for(TrackInfo.Type type:TrackInfo.Type.values()) {
            List<TrackInfo> previous=tracks.get(type), next=Collections.unmodifiableList(updated.get(type));
            TrackInfo old=selected(type); tracks.put(type,next); TrackInfo current=selected(type);
            if(!previous.equals(next)) emitTrack(l -> l.onTracksChanged(this,type,next));
            if(!Objects.equals(old==null ? null : old.id,current==null ? null : current.id)) {
                if(!previous.isEmpty()) switches++;
                emitTrack(l -> l.onSelectedTrackChanged(this,type,current));
            }
            PendingTrack task=pending.get(type);
            if(task!=null) {
                boolean success="auto".equals(task.value) || ("no".equals(task.value) ? current==null : current!=null && current.id.equals(task.id));
                if(success || scheduler.now()>=task.deadline) {
                    pending.remove(type);
                    if(success && type==TrackInfo.Type.VIDEO && videoMode!=VideoTrackMode.AUTO) lockedVideoId=current==null ? null : current.id;
                    trackResult(type,task,success ? OperationResult.Status.APPLIED : OperationResult.Status.FAILED,
                        success ? ("auto".equals(task.value) ? "Automatic selection policy applied" : "Selection observed in track-list") : "Track selection timed out");
                }
            }
        }
        TrackInfo video=getSelectedVideoTrack();
        if(videoMode==VideoTrackMode.AUTO_THEN_FIXED && lockedVideoId==null && video!=null && video.support==TrackInfo.Support.SUPPORTED) {
            engine.set("vid",Long.toString(video.nativeId)); lockedVideoId=video.id;
        }
    }

    private void onInfo(int what) {
        if(state==PlaybackState.IDLE || state==PlaybackState.ERROR || state==PlaybackState.ENDED) return;
        if(what==MpvPlayer.MEDIA_INFO_VIDEO_RENDERING_START && firstFrame<0) {
            firstFrame=Math.max(0,scheduler.now()-preparedAt); long elapsed=firstFrame; emit(l -> l.onFirstFrame(this,elapsed));
        } else if(what==MpvPlayer.MEDIA_INFO_BUFFERING_START || what==MpvPlayer.MEDIA_INFO_BUFFERING_END) {
            boolean value=what==MpvPlayer.MEDIA_INFO_BUFFERING_START;
            if(buffering==value) return;
            buffering=value;
            if(value && playWhenReady) { stalls++; stallStarted=scheduler.now(); }
            else if(!value) finishStall();
            if(ready && playWhenReady) changeState(value ? PlaybackState.BUFFERING : PlaybackState.PLAYING);
            emit(l -> l.onBufferingChanged(this,value));
        }
    }
    private void finishStall() { if(stallStarted>=0) { stalledMs+=Math.max(0,scheduler.now()-stallStarted); stallStarted=-1; } }
    private void schedulePoll() {
        if(polling || !ready) return;
        polling=true; long token=epoch;
        scheduler.post(() -> { synchronized(MpvPlayer.this) {
            if(token!=epoch) return;
            polling=false;
            if(!ready || state==PlaybackState.RELEASED) return;
            try { poll(); } catch(RuntimeException e) { onError(internal("Polling playback information failed",e)); }
            schedulePoll();
        } },config.progressIntervalMs);
    }
    private void poll() {
        refreshTracks();
        stats=readStats(); PlaybackStats snapshot=stats;
        emit(l -> l.onProgress(this,snapshot.positionMs,snapshot.durationMs,snapshot.bufferedPositionMs));
        long token=epoch; List<AnalyticsListener> recipients=new ArrayList<>(analytics);
        scheduler.post(() -> { for(AnalyticsListener listener:recipients) {
            synchronized(MpvPlayer.this) { if(token!=epoch || !analytics.contains(listener)) continue; }
            listener.onPlaybackStats(this,snapshot);
        } },0);
        if(videoMode==VideoTrackMode.AUTO && playWhenReady && !pending.containsKey(TrackInfo.Type.VIDEO)
            && scheduler.now()-lastAdaptiveSwitch>=10000) {
            TrackInfo current=getSelectedVideoTrack();
            TrackInfo choice=AdaptiveTrackSelector.choose(getVideoTracks(),current,stats.estimatedBandwidth,stats.bufferedDurationMs,buffering);
            if(choice!=null && (current==null || !current.id.equals(choice.id))) {
                requestTrack(TrackInfo.Type.VIDEO,choice.id,Long.toString(choice.nativeId)); lastAdaptiveSwitch=scheduler.now();
            }
        }
        if(config.stallTimeoutMs>0 && stallStarted>=0 && scheduler.now()-stallStarted>=config.stallTimeoutMs)
            onError(new PlayerError(0,PlayerError.Type.STALLED,"Playback remained buffering beyond configured timeout",null,true,true));
    }
    private PlaybackStats readStats() {
        TrackInfo video=getSelectedVideoTrack(),audio=getSelectedAudioTrack();
        Object cache=engine.get("demuxer-cache-state"),rate=cache instanceof Map ? ((Map<?,?>)cache).get("raw-input-rate") : null;
        long bandwidth=rate instanceof Number && ((Number)rate).doubleValue()>=0 ? (long)(((Number)rate).doubleValue()*8) : -1;
        long position=getCurrentPositionLong(),duration=getDurationLong();
        if(stats!=null) { if(position<0) position=stats.positionMs; if(duration<0) duration=stats.durationMs; }
        return new PlaybackStats(position,duration,getBufferedPosition(),getBufferedDuration(),
            (long)number("video-bitrate",-1),(long)number("audio-bitrate",-1),bandwidth,firstFrame,
            stalledMs+(stallStarted>=0 ? scheduler.now()-stallStarted : 0),stalls,switches,
            (long)number("frame-drop-count",-1),getVideoWidth(),getVideoHeight(),number("estimated-vf-fps",-1),
            video==null ? "" : video.codec,audio==null ? "" : audio.codec);
    }
    public synchronized PlaybackStats getPlaybackStats() { alive(); if(ready) stats=readStats(); return stats; }
    private void readDecoderCodecs() {
        if(decoderCodecs!=null) return;
        Object value=engine.get("decoder-list");
        if(value instanceof List) {
            decoderCodecs=new LinkedHashSet<>();
            for(Object item:(List<?>)value) if(item instanceof Map) {
                Object codec=((Map<?,?>)item).get("codec");
                if(codec instanceof String) decoderCodecs.add((String)codec);
            }
        }
    }
    /** Decoder availability does not guarantee a particular profile/resolution will decode on this device. */
    public synchronized List<String> getAvailableDecoderCodecs() {
        alive(); readDecoderCodecs();
        return decoderCodecs==null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(decoderCodecs));
    }
    private void onError(PlayerError error) {
        if(state==PlaybackState.RELEASED || errorHandled) return;
        errorHandled=true;
        resumePosition=ready && !isLive() && isSeekable() ? getCurrentPositionLong() : -1;
        ready=false; buffering=false; finishStall(); cancelPending("Playback failed");
        changeState(PlaybackState.ERROR); emit(l -> l.onError(this,error));
        if(error.retryable && retries<config.maxRetries && source!=null) {
            recovering=true; int attempt=++retries; long token=epoch;
            emit(l -> l.onRetry(this,attempt,config.retryDelayMs));
            scheduler.post(() -> { synchronized(MpvPlayer.this) {
                if(token==epoch && state==PlaybackState.ERROR) load(true);
            } },config.retryDelayMs);
        } else if(recovering) { recovering=false; emit(l -> l.onRecoveryResult(this,false)); }
    }
    private static PlayerError internal(String message,RuntimeException cause) { return new PlayerError(0,PlayerError.Type.INTERNAL,message,cause,false,false); }
    public synchronized boolean supports(Capability capability) {
        Objects.requireNonNull(capability);
        switch(capability) {
            case TRACK_SELECTION: case CACHED_SEEK_RANGES: case PLATFORM_DECODER_QUERY: return true;
            case ADAPTIVE_HLS_VARIANTS:
                int count=0; for(TrackInfo track:getVideoTracks()) if(track.kind==TrackInfo.Kind.HLS_VARIANT && track.support!=TrackInfo.Support.UNSUPPORTED) count++;
                return count>1;
            default: return false;
        }
    }
    private boolean flag(String name) { return Boolean.TRUE.equals(engine.get(name)); }
    private String string(String name) { Object value=engine.get(name); return value instanceof String ? (String)value : ""; }
    private double number(String name,double fallback) { Object value=engine.get(name); double result=value instanceof Number ? ((Number)value).doubleValue() : fallback; return Double.isNaN(result) || Double.isInfinite(result) ? fallback : result; }
    private long millis(String name) { double value=number(name,-1); return value<0 ? -1 : (long)(value*1000); }
    private static OperationResult result(OperationResult.Status status,String text) { return OperationResult.of(status,text); }
    private static OperationResult applied() { return result(OperationResult.Status.APPLIED,"Applied"); }
    private static OperationResult unsupported(String message) { return result(OperationResult.Status.UNSUPPORTED,message); }
    public synchronized void release() {
        if(state==PlaybackState.RELEASED) return;
        invalidate(); ready=false; playWhenReady=false; finishStall(); surface=null;
        try { engine.release(); }
        finally {
            closeSourceDescriptor(); clearMediaState(); source=null; changeState(PlaybackState.RELEASED); displayHolder=null;
            emit(l -> l.onReleased(this));
            scheduler.post(() -> { players.clear(); trackListeners.clear(); analytics.clear(); clearLegacyListeners(); },0);
        }
    }
    @Override public void close() { release(); }

    // Existing MediaPlayer-style entry points share the same engine, state and callback queue.
    public synchronized void setDataSource(String uri) { setDataSource(uri,Collections.emptyMap()); }
    public synchronized void setDataSource(String uri,Map<String,String> headers) {
        requireEmptySource(); setMediaSource(new MediaSource(uri,headers,MediaSource.StreamType.UNKNOWN));
    }
    private void requireEmptySource() {
        alive(); if(state!=PlaybackState.IDLE || source!=null) throw new IllegalStateException("Reset before setting another data source");
    }
    public synchronized void setDataSource(Context context,Uri uri) throws IOException { setDataSource(context,uri,Collections.emptyMap()); }
    public synchronized void setDataSource(Context context,Uri uri,Map<String,String> headers) throws IOException {
        requireEmptySource(); Objects.requireNonNull(uri);
        String scheme=uri.getScheme();
        if("content".equals(scheme) || "android.resource".equals(scheme)) {
            try(AssetFileDescriptor fd=context.getContentResolver().openAssetFileDescriptor(uri,"r")) {
                if(fd==null) throw new IOException("Cannot open " + uri);
                setDataSource(fd.getFileDescriptor(),fd.getStartOffset(),fd.getDeclaredLength());
            }
        } else setDataSource("file".equals(scheme) ? uri.getPath() : uri.toString(),headers);
    }
    public synchronized void setDataSource(FileDescriptor fd) throws IOException { setDataSource(fd,0,-1); }
    /** Retains its own descriptor; the caller may close theirs immediately. */
    public synchronized void setDataSource(FileDescriptor fd,long offset,long length) throws IOException {
        requireEmptySource();
        if(offset<0 || length < -1) throw new IllegalArgumentException("Invalid file range");
        ParcelFileDescriptor duplicate=ParcelFileDescriptor.dup(fd);
        try {
            setMediaSource(new MediaSource("androidfd://media"));
            sourceDescriptor=duplicate; sourceOffset=offset; sourceLength=length;
        } catch(RuntimeException e) { duplicate.close(); throw e; }
    }
    private void closeSourceDescriptor() {
        ParcelFileDescriptor old=sourceDescriptor; sourceDescriptor=null;
        if(old!=null) try { old.close(); } catch(IOException ignored) { }
    }
    public synchronized void reset() {
        alive(); stop(); closeSourceDescriptor(); source=null; fixedVideoPreference=null;
        if(videoMode==VideoTrackMode.FIXED) changeVideoMode(VideoTrackMode.AUTO_THEN_FIXED);
        playWhenReady=autoPlay;
    }
    public synchronized void start() {
        alive();
        if(state==PlaybackState.ENDED) { replay(); return; }
        if(!ready) throw new IllegalStateException("Media is not ready");
        play();
    }
    public synchronized int getCurrentPosition() { return clampTime(getCurrentPositionLong()); }
    public synchronized int getDuration() { return clampTime(getDurationLong()); }
    private static int clampTime(long time) { return (int)Math.max(0,Math.min(Integer.MAX_VALUE,time)); }
    public synchronized void seekTo(int positionMs) { seekTo((long)positionMs); }
    public synchronized void seekTo(long positionMs) { requireApplied(trySeekTo(positionMs)); }
    public synchronized void setSpeed(float value) {
        alive();
        if(Float.isNaN(value) || Float.isInfinite(value) || value<=0) throw new IllegalArgumentException("Speed must be positive");
        engine.set("speed",Float.toString(value)); speed=value;
    }
    public synchronized float getSpeed() { alive(); return getPlaybackSpeed(); }
    public synchronized void setVolume(float left,float right) {
        if(Float.isNaN(left) || Float.isInfinite(left) || left<0 || left>1 || Float.isNaN(right) || Float.isInfinite(right) || right<0 || right>1)
            throw new IllegalArgumentException("Volume must be 0..1");
        setVolume((left+right)/2f);
    }
    private static void requireApplied(OperationResult result) {
        if(result.isSuccess()) return;
        if(result.status==OperationResult.Status.INVALID_ARGUMENT) throw new IllegalArgumentException(result.message);
        if(result.status==OperationResult.Status.UNSUPPORTED) throw new UnsupportedOperationException(result.message);
        throw new IllegalStateException(result.message);
    }
    public synchronized void setDisplay(SurfaceHolder holder) {
        setSurface(holder==null ? null : holder.getSurface()); displayHolder=holder; updateScreen();
    }
    public synchronized void setScreenOnWhilePlaying(boolean enabled) { alive(); screenOn=enabled; updateScreen(); }
    private void updateScreen() { if(displayHolder!=null) displayHolder.setKeepScreenOn(screenOn && isPlaying()); }
    synchronized Object queryProperty(String name) { alive(); return engine.query(name); }
    synchronized void setProperty(String name,String value) { alive(); engine.set(name,value); extraProperties.put(name,value); }
    public synchronized void setOnPreparedListener(OnPreparedListener listener) { alive(); onPreparedListener=listener; }
    public synchronized void setOnCompletionListener(OnCompletionListener listener) { alive(); onCompletionListener=listener; }
    public synchronized void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) { alive(); onBufferingUpdateListener=listener; }
    public synchronized void setOnSeekCompleteListener(OnSeekCompleteListener listener) { alive(); onSeekCompleteListener=listener; }
    public synchronized void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) { alive(); onVideoSizeChangedListener=listener; }
    public synchronized void setOnErrorListener(OnErrorListener listener) { alive(); onErrorListener=listener; }
    public synchronized void setOnInfoListener(OnInfoListener listener) { alive(); onInfoListener=listener; }
    public synchronized void setOnTimedTextListener(OnTimedTextListener listener) { alive(); onTimedTextListener=listener; }
    private void clearLegacyListeners() {
        onPreparedListener=null; onCompletionListener=null; onBufferingUpdateListener=null;
        onSeekCompleteListener=null; onVideoSizeChangedListener=null; onErrorListener=null;
        onInfoListener=null; onTimedTextListener=null;
    }

    private static void postEventFromNative(Object reference, int generation, int event, int arg1, int arg2, byte[] bytes) {
        Core.postEventFromNative(reference, generation, event, arg1, arg2, bytes);
    }

    private static native long nativeCreate(Object reference, int generation);
    private static native void nativeRelease(long handle);
    private static native void nativePrepare(long handle, String source, String[] headers, boolean wait, int generation);
    private static native void nativeCommand(long handle, String[] arguments);
    private static native void nativeSeek(long handle, double seconds);
    private static native void nativeSetProperty(long handle, String name, String value);
    private static native double nativeGetNumber(long handle, String name, double fallback);
    private static native Object nativeGetProperty(long handle, String name);
    private static native void nativeSetSurface(long handle, Surface surface);
    private static native void nativeSetFileDescriptor(long handle, int fd, long offset, long length) throws IOException;

    static final class Core implements AutoCloseable {
        public static final int MEDIA_ERROR_UNKNOWN = 1;
        public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3;
        public static final int MEDIA_INFO_BUFFERING_START = 701;
        public static final int MEDIA_INFO_BUFFERING_END = 702;
        private static final int PREPARED = 1, COMPLETE = 2, BUFFERING = 3,
                SEEK_COMPLETE = 4, VIDEO_SIZE = 5, TIMED_TEXT = 99, ERROR = 100, INFO = 200;
        private enum State { IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, PAUSED, STOPPED, COMPLETED, ERROR, RELEASED }

        static { System.loadLibrary("mpvplayer"); }

        private final java.util.concurrent.Executor callbacks;
        private long nativeHandle;
        private int generation;
        private State state = State.IDLE;
        private String source;
        private Map<String, String> headers = Collections.emptyMap();
        private Surface surface;
        private SurfaceHolder holder;
        private boolean screenOn, looping, hardwareDecoding = true, restartOnPrepared;
        private float volume = 1f, speed = 1f;

        private PlayerEngine.Events events;
        synchronized void setEvents(PlayerEngine.Events value) { events=value; }

        Core(java.util.concurrent.Executor callbacks) { this.callbacks=callbacks; nativeHandle = nativeCreate(new WeakReference<>(this), ++generation); }

        public synchronized void setDataSource(String path) { setDataSource(path, Collections.emptyMap()); }

        public synchronized void setDataSource(String path, Map<String, String> requestHeaders) {
            require(State.IDLE);
            if (path == null || path.isEmpty() || path.indexOf('\0') >= 0)
                throw new IllegalArgumentException("Empty or invalid media path");
            Map<String, String> copy = new LinkedHashMap<>();
            if (requestHeaders != null) {
                for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                    String key = entry.getKey(), value = entry.getValue();
                    if (key == null || !key.matches("[!#$%&'*+.^_`|~0-9A-Za-z-]+") ||
                            value == null || value.indexOf('\r') >= 0 || value.indexOf('\n') >= 0 || value.indexOf('\0') >= 0)
                        throw new IllegalArgumentException("Invalid HTTP header");
                    copy.put(key, value);
                }
            }
            source = path;
            headers = copy;
            state = State.INITIALIZED;
        }

        public synchronized void setDataSource(Context context, Uri uri) throws IOException {
            setDataSource(context, uri, Collections.emptyMap());
        }

        public synchronized void setDataSource(Context context, Uri uri, Map<String, String> requestHeaders) throws IOException {
            require(State.IDLE);
            String scheme = uri.getScheme();
            if ("content".equals(scheme) || "android.resource".equals(scheme)) {
                try (AssetFileDescriptor afd = context.getContentResolver().openAssetFileDescriptor(uri, "r")) {
                    if (afd == null) throw new IOException("Cannot open " + uri);
                    setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
                }
            } else {
                setDataSource("file".equals(scheme) ? uri.getPath() : uri.toString(), requestHeaders);
            }
        }

        public synchronized void setDataSource(FileDescriptor fd) throws IOException { setDataSource(fd, 0, -1); }

        /** Descriptor must refer to a seekable regular file. It may be closed after this call. */
        public synchronized void setDataSource(FileDescriptor fd, long offset, long length) throws IOException {
            require(State.IDLE);
            if (offset < 0 || length < -1) throw new IllegalArgumentException("Invalid file range");
            try (ParcelFileDescriptor duplicate = ParcelFileDescriptor.dup(fd)) {
                nativeSetFileDescriptor(nativeHandle, duplicate.getFd(), offset, length);
            }
            source = "androidfd://media";
            headers = Collections.emptyMap();
            state = State.INITIALIZED;
        }

        public synchronized void prepareAsync() { prepareInternal(false); }

        /** Waits for FILE_LOADED, with a 30 second timeout. Prefer prepareAsync on the UI thread. */
        public synchronized void prepare() throws IOException {
            try { prepareInternal(true); }
            catch (IllegalStateException e) { throw new IOException("Unable to prepare media", e); }
        }

        private void prepareInternal(boolean wait) {
            require(State.INITIALIZED, State.STOPPED);
            String[] fields = new String[headers.size()];
            int index = 0;
            for (Map.Entry<String, String> e : headers.entrySet()) fields[index++] = e.getKey() + ": " + e.getValue();
            state = State.PREPARING;
            try {
                nativePrepare(nativeHandle, source, fields, wait, ++generation);
                if (wait) state = State.PREPARED;
            } catch (RuntimeException e) { state = State.ERROR; throw e; }
        }

        public synchronized void start() {
            require(State.PREPARED, State.STARTED, State.PAUSED, State.COMPLETED);
            if (state == State.COMPLETED) {
                state = State.STOPPED;
                restartOnPrepared = true;
                prepareInternal(false);
                return;
            }
            set("pause", "no");
            state = State.STARTED;
            updateScreen();
        }

        public synchronized void pause() {
            require(State.PREPARED, State.STARTED, State.PAUSED);
            set("pause", "yes");
            state = State.PAUSED;
            updateScreen();
        }

        public synchronized void stop() {
            require(State.PREPARED, State.STARTED, State.PAUSED, State.COMPLETED, State.STOPPED);
            nativeCommand(nativeHandle, new String[]{"stop"});
            state = State.STOPPED;
            updateScreen();
        }

        public synchronized void seekTo(int milliseconds) { seekTo((long) milliseconds); }
        public synchronized void seekTo(long milliseconds) {
            require(State.PREPARED, State.STARTED, State.PAUSED);
            if (milliseconds < 0) throw new IllegalArgumentException("Negative seek position");
            nativeSeek(nativeHandle, milliseconds / 1000.0);
        }

        public synchronized int getCurrentPosition() { return clampTime(getCurrentPositionLong()); }
        public synchronized long getCurrentPositionLong() { return Math.max(0, (long) (number("time-pos", 0) * 1000)); }
        public synchronized int getDuration() { return clampTime(getDurationLong()); }
        public synchronized long getDurationLong() { return Math.max(0, (long) (number("duration", 0) * 1000)); }
        private static int clampTime(long time) { return (int) Math.min(Integer.MAX_VALUE, time); }
        public synchronized int getVideoWidth() { return (int) number("dwidth", 0); }
        public synchronized int getVideoHeight() { return (int) number("dheight", 0); }
        public synchronized boolean isPlaying() { checkAlive(); return state == State.STARTED; }
        public synchronized boolean isLooping() { checkAlive(); return looping; }

        public synchronized void setLooping(boolean enabled) {
            set("loop-file", enabled ? "inf" : "no"); looping = enabled;
        }
        /** mpv volume applies to all channels. Use the one-argument method for explicit master volume. */
        public synchronized void setVolume(float left, float right) {
            validateVolume(left); validateVolume(right);
            setVolume((left + right) / 2f);
        }
        private static void validateVolume(float value) {
            if (Float.isNaN(value) || Float.isInfinite(value) || value < 0 || value > 1)
                throw new IllegalArgumentException("Volume must be 0..1");
        }
        public synchronized void setVolume(float value) {
            validateVolume(value);
            set("volume", Float.toString(value * 100)); volume = value;
        }
        public synchronized void setSpeed(float value) {
            if ((Float.isNaN(value) || Float.isInfinite(value)) || value <= 0) throw new IllegalArgumentException("Speed must be positive");
            set("speed", Float.toString(value)); speed = value;
        }
        public synchronized float getSpeed() { checkAlive(); return speed; }
        public synchronized void setHardwareDecoding(boolean enabled) {
            set("hwdec", enabled ? "mediacodec-copy" : "no"); hardwareDecoding = enabled;
        }

        public synchronized void setDisplay(SurfaceHolder value) {
            checkAlive();
            Surface next = value == null ? null : value.getSurface();
            attach(next);
            if (holder != null) holder.setKeepScreenOn(false);
            holder = value;
            updateScreen();
        }
        public synchronized void setSurface(Surface value) {
            checkAlive();
            attach(value);
            if (holder != null) holder.setKeepScreenOn(false);
            holder = null;
        }
        private void attach(Surface value) {
            if (value != null && !value.isValid()) throw new IllegalArgumentException("Invalid Surface");
            nativeSetSurface(nativeHandle, value);
            surface = value;
        }
        public synchronized void setSurfaceSize(int width, int height) {
            if (width < 0 || height < 0) throw new IllegalArgumentException("Invalid surface size");
            set("android-surface-size", width + "x" + height);
        }
        public synchronized void setScreenOnWhilePlaying(boolean enabled) { checkAlive(); screenOn = enabled; updateScreen(); }
        private void updateScreen() { if (holder != null) holder.setKeepScreenOn(screenOn && state == State.STARTED); }

        public synchronized void reset() {
            checkAlive();
            long old = nativeHandle;
            nativeHandle = 0;
            ++generation; // Invalidate callbacks already posted by the old instance.
            state = State.IDLE;
            updateScreen();
            nativeRelease(old);
            source = null;
            headers = Collections.emptyMap();
            restartOnPrepared = false;
            try {
                nativeHandle = nativeCreate(new WeakReference<>(this), generation);
                setLooping(looping);
                setVolume(volume);
                setSpeed(speed);
                setHardwareDecoding(hardwareDecoding);
                if (surface != null && surface.isValid()) nativeSetSurface(nativeHandle, surface);
            } catch (RuntimeException e) { release(); throw e; }
        }

        public synchronized void release() {
            if (state == State.RELEASED) return;
            ++generation;
            state = State.RELEASED;
            updateScreen();
            long old = nativeHandle;
            nativeHandle = 0;
            nativeRelease(old);
            surface = null;
            holder = null;
            events = null;
        }
        @Override public void close() { release(); }

        private void checkAlive() { if (nativeHandle == 0) throw new IllegalStateException("Player released"); }
        private void require(State... allowed) {
            checkAlive();
            for (State candidate : allowed) if (state == candidate) return;
            throw new IllegalStateException("Invalid player state: " + state);
        }
        private void set(String name, String value) { checkAlive(); nativeSetProperty(nativeHandle, name, value); }
        private double number(String name, double fallback) {
            checkAlive(); return nativeGetNumber(nativeHandle, name, fallback);
        }

        // Typed SDK bridge. Keep raw mpv properties out of the public SDK contract.
        synchronized Object queryProperty(String name) {
            checkAlive(); return nativeGetProperty(nativeHandle, name);
        }
        synchronized void setProperty(String name, String value) { set(name, value); }

        // Native event thread only enqueues work. Never acquire the player monitor here:
        // release/reset may be holding it while joining that thread.
        private static void postEventFromNative(Object reference, int generation, int event, int arg1, int arg2, byte[] bytes) {
            String text = bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
            Core player = (Core) ((WeakReference<?>) reference).get();
            if (player != null) player.callbacks.execute(() -> player.dispatch(generation, event, arg1, arg2, text));
        }

        private synchronized void dispatch(int token, int event, int arg1, int arg2, String text) {
            if (token != generation || state == State.RELEASED || state == State.STOPPED) return;
            switch (event) {
                case PREPARED:
                    if (state != State.PREPARING && state != State.PREPARED) return;
                    state = State.PREPARED;
                    if (restartOnPrepared) { restartOnPrepared = false; start(); }
                    else if (events != null) events.prepared();
                    break;
                case COMPLETE:
                    state = State.COMPLETED;
                    updateScreen();
                    if (events != null) events.completed();
                    break;
                case ERROR:
                    state = State.ERROR;
                    updateScreen();
                    if (events != null) events.error(arg1);
                    break;
                case BUFFERING: if (events != null) events.buffered(arg1); break;
                case SEEK_COMPLETE: if (events != null) events.seeked(); break;
                case VIDEO_SIZE: if (events != null) events.size(arg1, arg2); break;
                case INFO: if (events != null) events.info(arg1); break;
                case TIMED_TEXT: if (events != null) events.subtitle(text); break;
                default: break;
            }
        }


    }
}
