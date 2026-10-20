package lib.kalu.mpvplayer;

import android.os.ParcelFileDescriptor;
import android.view.Surface;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** Owns the native engine on one worker. No native calls or waits occur in public async methods. */
final class AsyncPlayerEngine implements PlayerEngine {
    interface Factory { PlayerEngine create(Executor callbacks); }
    private interface EventCall { void call(Events events); }
    private static final String[] PROPERTIES = {
        "track-list", "decoder-list", "time-pos", "duration", "seekable", "partially-seekable",
        "demuxer-cache-time", "demuxer-cache-duration", "demuxer-cache-state", "video-bitrate",
        "audio-bitrate", "frame-drop-count", "dwidth", "dheight", "estimated-vf-fps", "hwdec-current", "pause"
    };
    private final ScheduledThreadPoolExecutor worker;
    private final Factory factory;
    private volatile long generation;
    private volatile boolean closed;
    private volatile Events listener;
    private volatile Map<String,Object> snapshot = Collections.emptyMap();
    // Worker-owned state; never hold the public player's monitor while accessing native code.
    private PlayerEngine delegate;
    private long delegateGeneration;
    private final Map<String,String> options = new LinkedHashMap<>();
    private MediaSource source;
    private ParcelFileDescriptor descriptor;
    private long offset,length;
    private Surface surface;
    private int width,height;
    private boolean failed,prepared;

    AsyncPlayerEngine(Factory factory,long intervalMs) {
        this.factory = factory;
        worker = new ScheduledThreadPoolExecutor(1, task -> {
            Thread thread = new Thread(task,"mpv-control");
            thread.setDaemon(true);
            return thread;
        });
        worker.setRemoveOnCancelPolicy(true);
        worker.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        worker.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        worker.scheduleWithFixedDelay(() -> {
            long token = delegateGeneration;
            if (delegate != null && prepared && !failed && !closed) {
                try { refresh(token); } catch (RuntimeException | LinkageError error) { fail(token,error); }
            }
        },intervalMs,intervalMs,TimeUnit.MILLISECONDS);
    }
    public void events(Events events) { listener = events; }
    private boolean current(long token) { return !closed && token == generation; }
    private void checkOpen() { if (closed) throw new IllegalStateException("Player released"); }
    private synchronized void submit(Runnable action) {
        checkOpen(); long token = generation;
        worker.execute(() -> {
            if (!current(token) || failed) return;
            try { action.run(); } catch (RuntimeException | LinkageError error) { fail(token,error); }
        });
    }
    private void fail(long token,Throwable cause) {
        Events events;
        synchronized (this) {
            if (!current(token) || failed) return;
            failed = true; prepared = false; events = listener;
        }
        if (events != null) events.failure(cause);
    }
    // Native callback threads only enqueue work; they must never wait for the worker/SDK lock.
    private void callback(long token,Runnable action) {
        try {
            worker.execute(() -> {
                if (!current(token) || failed) return;
                try { action.run(); } catch (RuntimeException | LinkageError error) { fail(token,error); }
            });
        } catch (RejectedExecutionException ignored) { /* Released. */ }
    }
    private Events forwarding(long token,Events recipient) {
        return new Events() {
            private void send(EventCall call) {
                callback(token,() -> { Events events=recipient; if(events!=null) call.call(events); });
            }
            public void prepared() { callback(token,() -> {
                prepared=true; refresh(token); Events events=recipient; if(events!=null) events.prepared();
            }); }
            public void completed() { send(e -> { prepared=false; e.completed(); }); }
            public void error(int code) { send(e -> { prepared=false; failed=true; e.error(code); }); }
            public void failure(Throwable cause) { callback(token,() -> fail(token,cause)); }
            public void buffered(int value) { send(e -> e.buffered(value)); }
            public void info(int value) { send(e -> e.info(value)); }
            public void size(int w,int h) { callback(token,() -> {
                // Size getters must already see the new dimensions when the UI callback runs.
                refresh(token); Events events=recipient; if(events!=null) events.size(w,h);
            }); }
            public void seeked() { callback(token,() -> {
                refresh(token); Events events=recipient; if(events!=null) events.seeked();
            }); }
            public void subtitle(String value) { send(e -> e.subtitle(value)); }
        };
    }
    private void refresh(long token) {
        if (delegate == null || token != delegateGeneration || !current(token)) return;
        Map<String,Object> next = new LinkedHashMap<>();
        for (String name : PROPERTIES) {
            Object value = name.equals("decoder-list") && snapshot.containsKey(name) ? snapshot.get(name) : delegate.get(name);
            if (value != null) next.put(name,value);
        }
        // Serialize publication with reset so an old snapshot cannot reappear after a source change.
        synchronized (this) {
            if (current(token)) snapshot = Collections.unmodifiableMap(next);
        }
    }
    public Object get(String name) { checkOpen(); return snapshot.get(name); }
    /** Only the package-private diagnostic API uses a synchronous native property read. */
    public Object query(String name) {
        try { return waitFor(() -> delegate == null ? null : delegate.get(name)); }
        catch (IOException e) { throw new IllegalStateException("Query failed",e); }
    }
    public void set(String name,String value) {
        submit(() -> { options.put(name,value); if(delegate!=null) delegate.set(name,value); });
    }
    public void surface(Surface value) {
        submit(() -> {
            surface = value != null && value.isValid() ? value : null;
            if (delegate != null) delegate.surface(surface);
        });
    }
    public void surfaceSize(int w,int h) {
        submit(() -> { width=w; height=h; if(delegate!=null) delegate.surfaceSize(w,h); });
    }
    public void source(MediaSource value) { submit(() -> { closeDescriptor(); source=value; }); }
    public synchronized void source(FileDescriptor fd,long start,long count) throws IOException {
        checkOpen();
        // Duplicate before returning: the caller may immediately close/reset their descriptor.
        ParcelFileDescriptor owned = ParcelFileDescriptor.dup(fd);
        long token = generation;
        try {
            worker.execute(() -> {
                if (!current(token) || failed) { close(owned); return; }
                closeDescriptor(); descriptor=owned; source=null; offset=start; length=count;
            });
        } catch (RuntimeException e) { close(owned); throw e; }
    }
    private static void close(ParcelFileDescriptor value) {
        if (value != null) try { value.close(); } catch (IOException ignored) { }
    }
    private void closeDescriptor() { close(descriptor); descriptor=null; }
    private void load(boolean sync,long token) throws IOException {
        if (!current(token)) throw new IOException("Preparation cancelled");
        Events recipient = listener;
        delegateGeneration = token;
        delegate = factory.create(task -> callback(token,task));
        if (!current(token)) { destroy(); return; }
        delegate.events(forwarding(token,recipient));
        for (Map.Entry<String,String> option : options.entrySet()) delegate.set(option.getKey(),option.getValue());
        delegate.surface(surface != null && surface.isValid() ? surface : null);
        if (width>0 && height>0) delegate.surfaceSize(width,height);
        if (descriptor != null) {
            try { delegate.source(descriptor.getFileDescriptor(),offset,length); } finally { closeDescriptor(); }
        } else delegate.source(source);
        if (sync) { delegate.prepareSync(); prepared=true; refresh(token); }
        else delegate.prepare();
    }
    public synchronized void prepare() {
        long token = generation;
        submit(() -> {
            try { load(false,token); } catch (IOException e) { fail(token,e); }
        });
    }
    public void prepareSync() throws IOException {
        long token = generation;
        waitFor(() -> { load(true,token); return null; });
    }
    private <T> T waitFor(Callable<T> action) throws IOException {
        Future<T> future;
        synchronized (this) {
            checkOpen(); long token=generation;
            future=worker.submit(() -> {
                if (!current(token) || failed) throw new IOException("Player operation cancelled or failed");
                try { return action.call(); }
                catch (Exception | LinkageError error) { fail(token,error); throw error; }
            });
        }
        try { return future.get(); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new IOException("Interrupted waiting for player",e); }
        catch (ExecutionException e) { throw new IOException("Player operation failed",e.getCause()); }
    }
    public void play() { submit(() -> { if(delegate!=null) delegate.play(); }); }
    public void pause() { submit(() -> { if(delegate!=null) delegate.pause(); }); }
    public void stop() { reset(); }
    private void destroy() {
        PlayerEngine old=delegate; delegate=null; prepared=false;
        try { if(old!=null) old.release(); } finally { closeDescriptor(); }
    }
    public synchronized void reset() {
        checkOpen(); ++generation; snapshot=Collections.emptyMap();
        worker.execute(() -> {
            try { destroy(); }
            finally { failed=false; options.clear(); source=null; surface=null; width=height=0; }
        });
    }
    public void seek(long position) { submit(() -> { if(delegate!=null) delegate.seek(position); }); }
    public synchronized void release() {
        if (closed) return;
        closed=true; ++generation; listener=null; snapshot=Collections.emptyMap();
        worker.execute(() -> {
            try { destroy(); }
            finally { options.clear(); source=null; surface=null; }
        });
        worker.shutdown(); // Does not join; queued resource disposal completes on mpv-control.
    }
}
