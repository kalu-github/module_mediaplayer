package lib.kalu.mpvplayer;

import android.view.Surface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** A blocked backend must not block SDK calls or the caller's event queue. No Android playback. */
public final class AsyncEngineTest {
    private static int assertions;
    private static final Thread CALLER = Thread.currentThread();
    private static void check(boolean value,String message) {
        assertions++; if(!value) throw new AssertionError(message);
    }
    private static void await(CountDownLatch latch,String message) throws Exception {
        check(latch.await(3,TimeUnit.SECONDS),message);
    }
    private static void block(CountDownLatch latch) {
        try { if(!latch.await(10,TimeUnit.SECONDS)) throw new AssertionError("Blocked test did not get released"); }
        catch(InterruptedException e) { throw new AssertionError(e); }
    }
    private static final class Queue implements PlayerScheduler {
        final ConcurrentLinkedQueue<Runnable> tasks=new ConcurrentLinkedQueue<>();
        public long now() { return 100; }
        public void post(Runnable action,long delay) { if(delay==0) tasks.add(action); }
        public void cancelAll() { tasks.clear(); }
        void drain() { Runnable task; while((task=tasks.poll())!=null) task.run(); }
    }
    private static final class Backend implements PlayerEngine {
        final List<String> calls=Collections.synchronizedList(new ArrayList<>());
        final Map<String,String> options=new HashMap<>();
        final CountDownLatch enteredPrepare=new CountDownLatch(1), preparedGate=new CountDownLatch(1);
        final CountDownLatch enteredRelease=new CountDownLatch(1), releaseGate=new CountDownLatch(1), released=new CountDownLatch(1);
        final CountDownLatch enteredRead=new CountDownLatch(1), readGate=new CountDownLatch(1);
        volatile Events events;
        volatile boolean blockPrepare,blockRelease,blockRead;
        private void call(String name) {
            if(Thread.currentThread()==CALLER) throw new AssertionError("Native call on caller: "+name);
            calls.add(name);
        }
        public void events(Events value) { call("events");events=value; }
        public void reset() { call("reset"); }
        public void source(MediaSource value) { call("source:"+value.uri); }
        public void prepare() { call("prepare");enteredPrepare.countDown();if(blockPrepare) block(preparedGate);events.prepared(); }
        public void prepareSync() { prepare(); }
        public void play() { call("play"); }
        public void pause() { call("pause"); }
        public void stop() { call("stop"); }
        public void seek(long value) { call("seek"); }
        public void surface(Surface value) { call("surface"); }
        public void surfaceSize(int w,int h) { call("size"); }
        public void set(String name,String value) { call("set:"+name);options.put(name,value); }
        public Object get(String name) {
            call("get:"+name);
            if(blockRead) { enteredRead.countDown();block(readGate); }
            if(name.equals("duration")) return 12.0;
            if(name.equals("time-pos")) return 1.0;
            if(name.equals("seekable")) return true;
            if(name.equals("track-list") || name.equals("decoder-list")) return Collections.emptyList();
            return null;
        }
        public void release() {
            call("release");enteredRelease.countDown();if(blockRelease) block(releaseGate);released.countDown();
        }
    }
    private static void drainUntilReady(MpvPlayer player,Queue queue) throws Exception {
        long end=System.nanoTime()+TimeUnit.SECONDS.toNanos(3);
        while(player.getPlaybackState()==MpvPlayer.PlaybackState.PREPARING && System.nanoTime()<end) {
            queue.drain();Thread.yield();
        }
        check(player.getPlaybackState()==MpvPlayer.PlaybackState.READY,"prepared callback delivered");queue.drain();
    }
    private static void blockedCreationAndCancellation() throws Exception {
        CountDownLatch creating=new CountDownLatch(1), createGate=new CountDownLatch(1);
        AtomicInteger creates=new AtomicInteger(),callbacks=new AtomicInteger();
        Backend backend=new Backend();Queue queue=new Queue();
        AsyncPlayerEngine engine=new AsyncPlayerEngine(executor -> {
            creates.incrementAndGet();creating.countDown();block(createGate);return backend;
        },100);
        MpvPlayer player=new MpvPlayer(engine,queue,new PlayerConfig.Builder().build());
        ExecutorService caller=Executors.newSingleThreadExecutor();
        try {
            player.setOnPreparedListener(p -> callbacks.incrementAndGet());
            player.setSurface(null);player.setSpeed(1.5f);
            player.setMediaSource("first");player.prepareAsync();
            await(creating,"worker entered slow creation");
            Future<?> responsive=caller.submit(() -> {
                player.getDuration();player.getVideoWidth();player.getPlaybackStats();
                player.setSurface(null);player.pause();player.stop();player.release();
            });
            responsive.get(1,TimeUnit.SECONDS);check(true,"getters/reset/release return while native creation is still blocked");
            check(createGate.getCount()==1,"caller did not wait for factory");
        } finally { createGate.countDown();player.release();caller.shutdownNow(); }
        await(backend.released,"cancelled creation disposed");queue.drain();
        check(creates.get()==1,"cold setSource/prepare creates only once");
        check(!backend.calls.contains("prepare") && callbacks.get()==0,"cancelled creation cannot prepare or notify");
    }
    private static void blockedPrepareAndOldCallbacks() throws Exception {
        Backend first=new Backend(),second=new Backend();first.blockPrepare=true;
        AtomicInteger creates=new AtomicInteger(),callbacks=new AtomicInteger();Queue queue=new Queue();
        AsyncPlayerEngine engine=new AsyncPlayerEngine(executor -> creates.getAndIncrement()==0 ? first : second,100);
        MpvPlayer player=new MpvPlayer(engine,queue,new PlayerConfig.Builder().build());
        ExecutorService caller=Executors.newSingleThreadExecutor();
        try {
            player.setOnPreparedListener(p -> {
                check(Thread.currentThread()==CALLER && !Thread.holdsLock(p),"listener stays on caller queue outside lock");
                callbacks.incrementAndGet();
            });
            player.setMediaSource("first");player.prepareAsync();await(first.enteredPrepare,"worker entered slow prepare");
            Future<?> switchSource=caller.submit(() -> {
                player.getCurrentPositionLong();player.setMediaSource("second");player.prepareAsync();
            });
            switchSource.get(1,TimeUnit.SECONDS);check(first.preparedGate.getCount()==1,"source replacement did not join slow prepare");
            first.preparedGate.countDown();await(second.enteredPrepare,"new source eventually prepared in order");
            drainUntilReady(player,queue);
            check(callbacks.get()==1 && first.released.getCount()==0,"old prepare suppressed and old backend released");
            check(player.getDurationLong()==12000 && player.isSeekable(),"prepared callback has initialized snapshot");
            check(second.calls.indexOf("set:volume")<second.calls.indexOf("prepare") && second.calls.contains("source:second"),"configuration and source precede load");
            first.events.error(-13);first.events.completed();engine.query("duration");queue.drain();
            check(player.getPlaybackState()==MpvPlayer.PlaybackState.READY,"old native errors/completion cannot affect new source");
            second.blockRead=true;await(second.enteredRead,"worker entered slow property read");
            Future<?> getters=caller.submit(() -> {
                if(player.getDurationLong()!=12000) throw new AssertionError("snapshot missing");
                player.getVideoTracks();player.setMuted(true);player.pause();
            });
            getters.get(1,TimeUnit.SECONDS);check(second.readGate.getCount()==1,"public queries and controls do not wait for native reads");
            second.blockRelease=true;player.release();second.readGate.countDown();
            await(second.enteredRelease,"worker entered slow shutdown");
            check(player.getPlaybackState()==MpvPlayer.PlaybackState.RELEASED && engine.getClass()!=null,"release state immediate while teardown blocked");
        } finally {
            first.preparedGate.countDown();second.readGate.countDown();second.releaseGate.countDown();player.release();caller.shutdownNow();
        }
        await(second.released,"release finishes off caller thread");
    }
    private static void syncAndFailure() throws Exception {
        Backend backend=new Backend();Queue queue=new Queue();AtomicInteger callbacks=new AtomicInteger();
        MpvPlayer player=new MpvPlayer(new AsyncPlayerEngine(executor -> backend,100),queue,new PlayerConfig.Builder().build());
        try {
            player.setOnPreparedListener(p -> callbacks.incrementAndGet());player.setMediaSource("sync");player.prepare();
            check(player.getPlaybackState()==MpvPlayer.PlaybackState.READY && player.getDurationLong()==12000,"explicit prepare remains synchronous");
            queue.drain();check(callbacks.get()==1,"sync prepare does not duplicate callback");
        } finally { player.release(); }
        await(backend.released,"synchronous player release");
        Queue failures=new Queue();CountDownLatch attempted=new CountDownLatch(1);AtomicReference<PlayerError> received=new AtomicReference<>();
        UnsatisfiedLinkError cause=new UnsatisfiedLinkError("test missing JNI");
        AsyncPlayerEngine failed=new AsyncPlayerEngine(executor -> { attempted.countDown();throw cause; },100);
        MpvPlayer broken=new MpvPlayer(failed,failures,new PlayerConfig.Builder().build());
        try {
            broken.addPlayerListener(new PlayerListener() { public void onError(MpvPlayer p,PlayerError error) {received.set(error);} });
            broken.setMediaSource("failure");broken.prepareAsync();await(attempted,"factory failure attempted asynchronously");
            try { failed.query("duration"); } catch(IllegalStateException expected) { }
            failures.drain();
            check(broken.getPlaybackState()==MpvPlayer.PlaybackState.ERROR && received.get().getCause()==cause,"native load failures retain cause and report on listener queue");
        } finally { broken.release(); }
    }
    public static void main(String[] args) throws Exception {
        blockedCreationAndCancellation();blockedPrepareAndOldCallbacks();syncAndFailure();
        System.out.println("Async native dispatch tests passed: "+assertions+" assertions. Device startup latency not measured.");
    }
}
