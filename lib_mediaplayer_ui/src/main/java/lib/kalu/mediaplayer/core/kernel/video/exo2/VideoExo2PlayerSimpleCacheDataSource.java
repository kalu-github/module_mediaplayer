package lib.kalu.mediaplayer.core.kernel.video.exo2;

import static java.lang.Math.min;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.upstream.DataSource;
import static com.google.android.exoplayer2.util.Assertions.checkNotNull;
import static com.google.android.exoplayer2.util.Util.castNonNull;
import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSourceException;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.PlaceholderDataSource;
import com.google.android.exoplayer2.upstream.PriorityDataSource;
import com.google.android.exoplayer2.upstream.TeeDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.Cache.CacheException;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory;
import com.google.android.exoplayer2.upstream.cache.CacheSpan;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import com.google.android.exoplayer2.upstream.cache.ContentMetadataMutations;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.PriorityTaskManager;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A {@link DataSource} that reads and writes a {@link Cache}. Requests are fulfilled from the cache
 * when possible. When data is not cached it is requested from an upstream {@link DataSource} and
 * written into the cache.
 */
public final class VideoExo2PlayerSimpleCacheDataSource implements DataSource {

    /**
     * {@link DataSource.Factory} for {@link VideoExo2PlayerSimpleCacheDataSource} instances.
     */
    public static final class Factory implements DataSource.Factory {

        private Cache cache;
        private DataSource.Factory cacheReadDataSourceFactory;
        @Nullable
        private DataSink.Factory cacheWriteDataSinkFactory;
        private CacheKeyFactory cacheKeyFactory;
        private boolean cacheIsReadOnly;
        @Nullable
        private DataSource.Factory upstreamDataSourceFactory;
        @Nullable
        private PriorityTaskManager upstreamPriorityTaskManager;
        private int upstreamPriority;
        private @Flags int flags;
        @Nullable
        private EventListener eventListener;

        public Factory() {
            cacheReadDataSourceFactory = new FileDataSource.Factory();
            cacheKeyFactory = CacheKeyFactory.DEFAULT;
        }

        /**
         * Sets the cache that will be used.
         *
         * <p>Must be called before the factory is used.
         *
         * @param cache The cache that will be used.
         * @return This factory.
         */

        public Factory setCache(Cache cache) {
            this.cache = cache;
            return this;
        }

        /**
         * Returns the cache that will be used, or {@code null} if {@link #setCache} has yet to be
         * called.
         */
        @Nullable
        public Cache getCache() {
            return cache;
        }

        /**
         * Sets the {@link DataSource.Factory} for {@link DataSource DataSources} for reading from the
         * cache.
         *
         * <p>The default is a {@link FileDataSource.Factory} in its default configuration.
         *
         * @param cacheReadDataSourceFactory The {@link DataSource.Factory} for reading from the cache.
         * @return This factory.
         */

        public Factory setCacheReadDataSourceFactory(DataSource.Factory cacheReadDataSourceFactory) {
            this.cacheReadDataSourceFactory = cacheReadDataSourceFactory;
            return this;
        }

        /**
         * Sets the {@link DataSink.Factory} for generating {@link DataSink DataSinks} for writing data
         * to the cache. Passing {@code null} causes the cache to be read-only.
         *
         * <p>The default is a {@link CacheDataSink.Factory} in its default configuration.
         *
         * @param cacheWriteDataSinkFactory The {@link DataSink.Factory} for generating {@link DataSink
         *                                  DataSinks} for writing data to the cache, or {@code null} to disable writing.
         * @return This factory.
         */

        public Factory setCacheWriteDataSinkFactory(
                @Nullable DataSink.Factory cacheWriteDataSinkFactory) {
            this.cacheWriteDataSinkFactory = cacheWriteDataSinkFactory;
            this.cacheIsReadOnly = cacheWriteDataSinkFactory == null;
            return this;
        }

        /**
         * Sets the {@link CacheKeyFactory}.
         *
         * <p>The default is {@link CacheKeyFactory#DEFAULT}.
         *
         * @param cacheKeyFactory The {@link CacheKeyFactory}.
         * @return This factory.
         */

        public Factory setCacheKeyFactory(CacheKeyFactory cacheKeyFactory) {
            this.cacheKeyFactory = cacheKeyFactory;
            return this;
        }

        /**
         * Returns the {@link CacheKeyFactory} that will be used.
         */
        public CacheKeyFactory getCacheKeyFactory() {
            return cacheKeyFactory;
        }

        /**
         * Sets the {@link DataSource.Factory} for upstream {@link DataSource DataSources}, which are
         * used to read data in the case of a cache miss.
         *
         * <p>The default is {@code null}, and so this method must be called before the factory is used
         * in order for data to be read from upstream in the case of a cache miss.
         *
         * @param upstreamDataSourceFactory The upstream {@link DataSource} for reading data not in the
         *                                  cache, or {@code null} to cause failure in the case of a cache miss.
         * @return This factory.
         */

        public Factory setUpstreamDataSourceFactory(
                @Nullable DataSource.Factory upstreamDataSourceFactory) {
            this.upstreamDataSourceFactory = upstreamDataSourceFactory;
            return this;
        }

        /**
         * Sets an optional {@link PriorityTaskManager} to use when requesting data from upstream.
         *
         * <p>If set, reads from the upstream {@link DataSource} will only be allowed to proceed if
         * there are no higher priority tasks registered to the {@link PriorityTaskManager}. If there
         * exists a higher priority task then {@link PriorityTaskManager} will
         * be thrown instead.
         *
         * <p>Note that requests to {@link VideoExo2PlayerSimpleCacheDataSource} instances are intended to be used as parts
         * of (possibly larger) tasks that are registered with the {@link PriorityTaskManager}, and
         * hence {@link VideoExo2PlayerSimpleCacheDataSource} does <em>not</em> register a task by itself. This must be done
         * by the surrounding code that uses the {@link VideoExo2PlayerSimpleCacheDataSource} instances.
         *
         * <p>The default is {@code null}.
         *
         * @param upstreamPriorityTaskManager The upstream {@link PriorityTaskManager}.
         * @return This factory.
         */

        public Factory setUpstreamPriorityTaskManager(
                @Nullable PriorityTaskManager upstreamPriorityTaskManager) {
            this.upstreamPriorityTaskManager = upstreamPriorityTaskManager;
            return this;
        }

        /**
         * Returns the {@link PriorityTaskManager} that will bs used when requesting data from upstream,
         * or {@code null} if there is none.
         */
        @Nullable
        public PriorityTaskManager getUpstreamPriorityTaskManager() {
            return upstreamPriorityTaskManager;
        }

        /**
         * Sets the priority to use when requesting data from upstream. The priority is only used if a
         * {@link PriorityTaskManager} is set by calling {@link #setUpstreamPriorityTaskManager}.
         *
         * <p>The default is {@link C#PRIORITY_PLAYBACK}.
         *
         * @param upstreamPriority The priority to use when requesting data from upstream.
         * @return This factory.
         */

        public Factory setUpstreamPriority(int upstreamPriority) {
            this.upstreamPriority = upstreamPriority;
            return this;
        }

        /**
         * Sets the {@link Flags}.
         *
         * <p>The default is {@code 0}.
         *
         * @param flags The {@link Flags}.
         * @return This factory.
         */

        public Factory setFlags(@Flags int flags) {
            this.flags = flags;
            return this;
        }

        /**
         * Sets the {link EventListener} to which events are delivered.
         *
         * <p>The default is {@code null}.
         *
         * @param eventListener The {@link EventListener}.
         * @return This factory.
         */

        public Factory setEventListener(@Nullable EventListener eventListener) {
            this.eventListener = eventListener;
            return this;
        }

        @Override
        public VideoExo2PlayerSimpleCacheDataSource createDataSource() {
            return createDataSourceInternal(
                    upstreamDataSourceFactory != null ? upstreamDataSourceFactory.createDataSource() : null,
                    flags,
                    upstreamPriority);
        }

        /**
         * Returns an instance suitable for downloading content. The created instance is equivalent to
         * one that would be created by {@link #createDataSource()}, except:
         *
         * <ul>
         *   <li>The {@link #FLAG_BLOCK_ON_CACHE} is always set.
         *   <li>The task priority is overridden to be {@link C#PRIORITY_DOWNLOAD}.
         * </ul>
         *
         * @return An instance suitable for downloading content.
         */
        public VideoExo2PlayerSimpleCacheDataSource createDataSourceForDownloading() {
            return createDataSourceInternal(
                    upstreamDataSourceFactory != null ? upstreamDataSourceFactory.createDataSource() : null,
                    flags | FLAG_BLOCK_ON_CACHE,
                    C.PRIORITY_DOWNLOAD);
        }

        /**
         * Returns an instance suitable for reading cached content as part of removing a download. The
         * created instance is equivalent to one that would be created by {@link #createDataSource()},
         * except:
         *
         * <ul>
         *   <li>The upstream is overridden to be {@code null}, since when removing content we don't
         *       want to request anything that's not already cached.
         *   <li>The {@link #FLAG_BLOCK_ON_CACHE} is always set.
         *   <li>The task priority is overridden to be {@link C#PRIORITY_DOWNLOAD}.
         * </ul>
         *
         * @return An instance suitable for reading cached content as part of removing a download.
         */
        public VideoExo2PlayerSimpleCacheDataSource createDataSourceForRemovingDownload() {
            return createDataSourceInternal(
                    /* upstreamDataSource= */ null, flags | FLAG_BLOCK_ON_CACHE, C.PRIORITY_DOWNLOAD);
        }

        private VideoExo2PlayerSimpleCacheDataSource createDataSourceInternal(
                @Nullable DataSource upstreamDataSource, @Flags int flags, int upstreamPriority) {
            Cache cache = checkNotNull(this.cache);
            @Nullable DataSink cacheWriteDataSink;
            if (cacheIsReadOnly || upstreamDataSource == null) {
                cacheWriteDataSink = null;
            } else if (cacheWriteDataSinkFactory != null) {
                cacheWriteDataSink = cacheWriteDataSinkFactory.createDataSink();
            } else {
                cacheWriteDataSink = new CacheDataSink.Factory().setCache(cache).createDataSink();
            }
            return new VideoExo2PlayerSimpleCacheDataSource(
                    cache,
                    upstreamDataSource,
                    cacheReadDataSourceFactory.createDataSource(),
                    cacheWriteDataSink,
                    cacheKeyFactory,
                    flags,
                    upstreamPriorityTaskManager,
                    upstreamPriority,
                    eventListener);
        }
    }

    /**
     * Listener of {@link VideoExo2PlayerSimpleCacheDataSource} events.
     */
    public interface EventListener {

        /**
         * Called when bytes have been read from the cache.
         *
         * @param cacheSizeBytes  Current cache size in bytes.
         * @param cachedBytesRead Total bytes read from the cache since this method was last called.
         */
        void onCachedBytesRead(long cacheSizeBytes, long cachedBytesRead);

        void onCachedProgress(long contentLength, long bytesCached, float percent);

        /**
         * Called when the current request ignores cache.
         *
         * @param reason Reason cache is bypassed.
         */
        void onCacheIgnored(@CacheIgnoredReason int reason);
    }

    /**
     * Flags controlling the VideoExoplayer2CacheDataSource's behavior. Possible flag values are {@link
     * #FLAG_BLOCK_ON_CACHE}, {@link #FLAG_IGNORE_CACHE_ON_ERROR} and {@link
     * #FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS}.
     */
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target(TYPE_USE)
    @IntDef(
            flag = true,
            value = {
                    FLAG_BLOCK_ON_CACHE,
                    FLAG_IGNORE_CACHE_ON_ERROR,
                    FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS
            })
    public @interface Flags {
    }

    /**
     * A flag indicating whether we will block reads if the cache key is locked. If unset then data is
     * read from upstream if the cache key is locked, regardless of whether the data is cached.
     */
    public static final int FLAG_BLOCK_ON_CACHE = 1;

    /**
     * A flag indicating whether the cache is bypassed following any cache related error. If set then
     * cache related exceptions may be thrown for one cycle of open, read and close calls. Subsequent
     * cycles of these calls will then bypass the cache.
     */
    public static final int FLAG_IGNORE_CACHE_ON_ERROR = 1 << 1; // 2

    /**
     * A flag indicating that the cache should be bypassed for requests whose lengths are unset. This
     * flag is provided for legacy reasons only.
     */
    public static final int FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS = 1 << 2; // 4

    /**
     * Reasons the cache may be ignored. One of {@link #CACHE_IGNORED_REASON_ERROR} or {@link
     * #CACHE_IGNORED_REASON_UNSET_LENGTH}.
     */
    // @Target list includes both 'default' targets and TYPE_USE, to ensure backwards compatibility
    // with Kotlin usages from before TYPE_USE was added.
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target({FIELD, METHOD, PARAMETER, LOCAL_VARIABLE, TYPE_USE})
    @IntDef({CACHE_IGNORED_REASON_ERROR, CACHE_IGNORED_REASON_UNSET_LENGTH})
    public @interface CacheIgnoredReason {
    }

    /**
     * Cache not ignored.
     */
    private static final int CACHE_NOT_IGNORED = -1;

    /**
     * Cache ignored due to a cache related error.
     */
    public static final int CACHE_IGNORED_REASON_ERROR = 0;

    /**
     * Cache ignored due to a request with an unset length.
     */
    public static final int CACHE_IGNORED_REASON_UNSET_LENGTH = 1;

    /**
     * Minimum number of bytes to read before checking cache for availability.
     */
    private static final long MIN_READ_BEFORE_CHECKING_CACHE = 100 * 1024;

    private final Cache cache;
    private final DataSource cacheReadDataSource;
    @Nullable
    private final DataSource cacheWriteDataSource;
    private final DataSource upstreamDataSource;
    private final CacheKeyFactory cacheKeyFactory;
    @Nullable
    private final EventListener eventListener;

    private final boolean blockOnCache;
    private final boolean ignoreCacheOnError;
    private final boolean ignoreCacheForUnsetLengthRequests;

    @Nullable
    private Uri actualUri;
    @Nullable
    private DataSpec requestDataSpec;
    @Nullable
    private DataSpec currentDataSpec;
    @Nullable
    private DataSource currentDataSource;
    private long currentDataSourceBytesRead;
    private long readPosition;
    private long bytesRemaining;
    @Nullable
    private CacheSpan currentHoleSpan;
    private boolean seenCacheError;
    private boolean currentRequestIgnoresCache;
    private long totalCachedBytesRead;
    private long checkCachePosition;
    private long contentLength;
    private long mBytesCached;
    private long mLastBytesCached = 0L;
    private long mLastNotifyTime = SystemClock.elapsedRealtime();

    /**
     * Constructs an instance with default {@link DataSource} and {@link DataSink} instances for
     * reading and writing the cache.
     *
     * @param cache              The cache.
     * @param upstreamDataSource A {@link DataSource} for reading data not in the cache. If null,
     *                           reading will fail if a cache miss occurs.
     */
    public VideoExo2PlayerSimpleCacheDataSource(Cache cache, @Nullable DataSource upstreamDataSource) {
        this(cache, upstreamDataSource, /* flags= */ 0);
    }

    /**
     * Constructs an instance with default {@link DataSource} and {@link DataSink} instances for
     * reading and writing the cache.
     *
     * @param cache              The cache.
     * @param upstreamDataSource A {@link DataSource} for reading data not in the cache. If null,
     *                           reading will fail if a cache miss occurs.
     * @param flags              A combination of {@link #FLAG_BLOCK_ON_CACHE}, {@link #FLAG_IGNORE_CACHE_ON_ERROR}
     *                           and {@link #FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS}, or 0.
     */
    public VideoExo2PlayerSimpleCacheDataSource(Cache cache, @Nullable DataSource upstreamDataSource, @Flags int flags) {
        this(
                cache,
                upstreamDataSource,
                new FileDataSource(),
                new CacheDataSink(cache, CacheDataSink.DEFAULT_FRAGMENT_SIZE),
                flags,
                /* eventListener= */ null);
    }

    /**
     * Constructs an instance with arbitrary {@link DataSource} and {@link DataSink} instances for
     * reading and writing the cache. One use of this constructor is to allow data to be transformed
     * before it is written to disk.
     *
     * @param cache               The cache.
     * @param upstreamDataSource  A {@link DataSource} for reading data not in the cache. If null,
     *                            reading will fail if a cache miss occurs.
     * @param cacheReadDataSource A {@link DataSource} for reading data from the cache.
     * @param cacheWriteDataSink  A {@link DataSink} for writing data to the cache. If null, cache is
     *                            accessed read-only.
     * @param flags               A combination of {@link #FLAG_BLOCK_ON_CACHE}, {@link #FLAG_IGNORE_CACHE_ON_ERROR}
     *                            and {@link #FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS}, or 0.
     * @param eventListener       An optional {@link EventListener} to receive events.
     */
    public VideoExo2PlayerSimpleCacheDataSource(
            Cache cache,
            @Nullable DataSource upstreamDataSource,
            DataSource cacheReadDataSource,
            @Nullable DataSink cacheWriteDataSink,
            @Flags int flags,
            @Nullable EventListener eventListener) {
        this(
                cache,
                upstreamDataSource,
                cacheReadDataSource,
                cacheWriteDataSink,
                flags,
                eventListener,
                /* cacheKeyFactory= */ null);
    }

    /**
     * Constructs an instance with arbitrary {@link DataSource} and {@link DataSink} instances for
     * reading and writing the cache. One use of this constructor is to allow data to be transformed
     * before it is written to disk.
     *
     * @param cache               The cache.
     * @param upstreamDataSource  A {@link DataSource} for reading data not in the cache. If null,
     *                            reading will fail if a cache miss occurs.
     * @param cacheReadDataSource A {@link DataSource} for reading data from the cache.
     * @param cacheWriteDataSink  A {@link DataSink} for writing data to the cache. If null, cache is
     *                            accessed read-only.
     * @param flags               A combination of {@link #FLAG_BLOCK_ON_CACHE}, {@link #FLAG_IGNORE_CACHE_ON_ERROR}
     *                            and {@link #FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS}, or 0.
     * @param eventListener       An optional {@link EventListener} to receive events.
     * @param cacheKeyFactory     An optional factory for cache keys.
     */
    public VideoExo2PlayerSimpleCacheDataSource(
            Cache cache,
            @Nullable DataSource upstreamDataSource,
            DataSource cacheReadDataSource,
            @Nullable DataSink cacheWriteDataSink,
            @Flags int flags,
            @Nullable EventListener eventListener,
            @Nullable CacheKeyFactory cacheKeyFactory) {
        this(
                cache,
                upstreamDataSource,
                cacheReadDataSource,
                cacheWriteDataSink,
                cacheKeyFactory,
                flags,
                /* upstreamPriorityTaskManager= */ null,
                /* upstreamPriority= */ C.PRIORITY_PLAYBACK,
                eventListener);
    }

    private VideoExo2PlayerSimpleCacheDataSource(
            Cache cache,
            @Nullable DataSource upstreamDataSource,
            DataSource cacheReadDataSource,
            @Nullable DataSink cacheWriteDataSink,
            @Nullable CacheKeyFactory cacheKeyFactory,
            @Flags int flags,
            @Nullable PriorityTaskManager upstreamPriorityTaskManager,
            int upstreamPriority,
            @Nullable EventListener eventListener) {
        this.cache = cache;
        this.cacheReadDataSource = cacheReadDataSource;
        this.cacheKeyFactory = cacheKeyFactory != null ? cacheKeyFactory : CacheKeyFactory.DEFAULT;
        this.blockOnCache = (flags & FLAG_BLOCK_ON_CACHE) != 0;
        this.ignoreCacheOnError = (flags & FLAG_IGNORE_CACHE_ON_ERROR) != 0;
        this.ignoreCacheForUnsetLengthRequests =
                (flags & FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS) != 0;
        if (upstreamDataSource != null) {
            if (upstreamPriorityTaskManager != null) {
                upstreamDataSource =
                        new PriorityDataSource(
                                upstreamDataSource, upstreamPriorityTaskManager, upstreamPriority);
            }
            this.upstreamDataSource = upstreamDataSource;
            this.cacheWriteDataSource =
                    cacheWriteDataSink != null
                            ? new TeeDataSource(upstreamDataSource, cacheWriteDataSink)
                            : null;
        } else {
            this.upstreamDataSource = PlaceholderDataSource.INSTANCE;
            this.cacheWriteDataSource = null;
        }
        this.eventListener = eventListener;
    }

    /**
     * Returns the {@link Cache} used by this instance.
     */
    public Cache getCache() {
        return cache;
    }

    /**
     * Returns the {@link CacheKeyFactory} used by this instance.
     */
    public CacheKeyFactory getCacheKeyFactory() {
        return cacheKeyFactory;
    }

    @Override
    public void addTransferListener(TransferListener transferListener) {
        checkNotNull(transferListener);
        cacheReadDataSource.addTransferListener(transferListener);
        upstreamDataSource.addTransferListener(transferListener);
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        try {
            String key = cacheKeyFactory.buildCacheKey(dataSpec);
            DataSpec requestDataSpec = dataSpec.buildUpon().setKey(key).build();
            this.requestDataSpec = requestDataSpec;
            actualUri = getRedirectedUriOrDefault(cache, key, /* defaultUri= */ requestDataSpec.uri);
            readPosition = dataSpec.position;

            int reason = shouldIgnoreCacheForRequest(dataSpec);
            currentRequestIgnoresCache = reason != CACHE_NOT_IGNORED;
            if (currentRequestIgnoresCache) {
                notifyCacheIgnored(reason);
            }

            if (currentRequestIgnoresCache) {
                bytesRemaining = C.LENGTH_UNSET;
            } else {
                bytesRemaining = ContentMetadata.getContentLength(cache.getContentMetadata(key));
                if (bytesRemaining != C.LENGTH_UNSET) {
                    bytesRemaining -= dataSpec.position;
                    if (bytesRemaining < 0) {
                        throw new DataSourceException(
                                PlaybackException.ERROR_CODE_IO_READ_POSITION_OUT_OF_RANGE);
                    }
                }
            }
            if (dataSpec.length != C.LENGTH_UNSET) {
                bytesRemaining =
                        bytesRemaining == C.LENGTH_UNSET
                                ? dataSpec.length
                                : min(bytesRemaining, dataSpec.length);
            }
            if (bytesRemaining > 0 || bytesRemaining == C.LENGTH_UNSET) {
                openNextSource(requestDataSpec, false);
            }
            long length = dataSpec.length != C.LENGTH_UNSET ? dataSpec.length : bytesRemaining;
            return length;
        } catch (Throwable e) {
            handleBeforeThrow(e);
            throw e;
        }
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        if (length == 0) {
            return 0;
        }
        if (bytesRemaining == 0) {
            return C.RESULT_END_OF_INPUT;
        }
        DataSpec requestDataSpec = checkNotNull(this.requestDataSpec);
        DataSpec currentDataSpec = checkNotNull(this.currentDataSpec);
        try {
            if (readPosition >= checkCachePosition) {
                openNextSource(requestDataSpec, true);
            }
            int bytesRead = checkNotNull(currentDataSource).read(buffer, offset, length);

            if (isWritingToCache()) {
                mBytesCached += bytesRead;
                notifyProgressUpdate(0.001f);
            }
            if (bytesRead != C.RESULT_END_OF_INPUT) {
                if (isReadingFromCache()) {
                    totalCachedBytesRead += bytesRead;
                }
                readPosition += bytesRead;
                currentDataSourceBytesRead += bytesRead;
                if (bytesRemaining != C.LENGTH_UNSET) {
                    bytesRemaining -= bytesRead;
                }
            } else if (isReadingFromUpstream()
                    && (currentDataSpec.length == C.LENGTH_UNSET
                    || currentDataSourceBytesRead < currentDataSpec.length)) {
                // We've encountered RESULT_END_OF_INPUT from the upstream DataSource at a position not
                // imposed by the current DataSpec. This must mean that we've reached the end of the
                // resource.
                setNoBytesRemainingAndMaybeStoreLength(castNonNull(requestDataSpec.key));
            } else if (bytesRemaining > 0 || bytesRemaining == C.LENGTH_UNSET) {
                closeCurrentSource();
                openNextSource(requestDataSpec, false);
                return read(buffer, offset, length);
            }
            return bytesRead;
        } catch (Throwable e) {
            handleBeforeThrow(e);
            throw e;
        }
    }

    @Override
    @Nullable
    public Uri getUri() {
        return actualUri;
    }

    @Override
    public Map<String, List<String>> getResponseHeaders() {
        // TODO: Implement.
        return isReadingFromUpstream()
                ? upstreamDataSource.getResponseHeaders()
                : Collections.emptyMap();
    }

    @Override
    public void close() throws IOException {
        actualUri = null;
        readPosition = 0;
        notifyBytesRead();
        if (isWritingToCache()) {
            if (requestDataSpec != null) {
                String cacheKey = requestDataSpec.key;
                if (!TextUtils.isEmpty(cacheKey)) {
                    long cachedBytes = cache.getCachedBytes(cacheKey, 0L, C.LENGTH_UNSET);
                    if (cachedBytes > mBytesCached) {
                        mBytesCached = cachedBytes;
                    }
                }
            }
            notifyProgressUpdate(1f);
        }
        requestDataSpec = null;
        try {
            closeCurrentSource();
        } catch (Throwable e) {
            handleBeforeThrow(e);
            throw e;
        }
    }

    /**
     * Opens the next source. If the cache contains data spanning the current read position then
     * {@link #cacheReadDataSource} is opened to read from it. Else {@link #upstreamDataSource} is
     * opened to read from the upstream source and write into the cache.
     *
     * <p>There must not be a currently open source when this method is called, except in the case
     * that {@code checkCache} is true. If {@code checkCache} is true then there must be a currently
     * open source, and it must be {@link #upstreamDataSource}. It will be closed and a new source
     * opened if it's possible to switch to reading from or writing to the cache. If a switch isn't
     * possible then the current source is left unchanged.
     *
     * @param requestDataSpec The original {@link DataSpec} to build upon for the next source.
     * @param checkCache      If true tries to switch to reading from or writing to cache instead of
     *                        reading from {@link #upstreamDataSource}, which is the currently open source.
     */
    private void openNextSource(DataSpec requestDataSpec, boolean checkCache) throws IOException {
        @Nullable CacheSpan nextSpan;
        String key = castNonNull(requestDataSpec.key);
        if (currentRequestIgnoresCache) {
            nextSpan = null;
        } else if (blockOnCache) {
            try {
                nextSpan = cache.startReadWrite(key, readPosition, bytesRemaining);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InterruptedIOException();
            }
        } else {
            nextSpan = cache.startReadWriteNonBlocking(key, readPosition, bytesRemaining);
        }

        DataSpec nextDataSpec;
        DataSource nextDataSource;
        if (nextSpan == null) {
            // The data is locked in the cache, or we're ignoring the cache. Bypass the cache and read
            // from upstream.
            nextDataSource = upstreamDataSource;
            nextDataSpec =
                    requestDataSpec.buildUpon().setPosition(readPosition).setLength(bytesRemaining).build();
        } else if (nextSpan.isCached) {
            // Data is cached in a span file starting at nextSpan.position.
            Uri fileUri = Uri.fromFile(castNonNull(nextSpan.file));
            long filePositionOffset = nextSpan.position;
            long positionInFile = readPosition - filePositionOffset;
            long length = nextSpan.length - positionInFile;
            if (bytesRemaining != C.LENGTH_UNSET) {
                length = min(length, bytesRemaining);
            }
            nextDataSpec =
                    requestDataSpec
                            .buildUpon()
                            .setUri(fileUri)
                            .setUriPositionOffset(filePositionOffset)
                            .setPosition(positionInFile)
                            .setLength(length)
                            .build();
            nextDataSource = cacheReadDataSource;
        } else {
            // Data is not cached, and data is not locked, read from upstream with cache backing.
            long length;
            if (nextSpan.isOpenEnded()) {
                length = bytesRemaining;
            } else {
                length = nextSpan.length;
                if (bytesRemaining != C.LENGTH_UNSET) {
                    length = min(length, bytesRemaining);
                }
            }
            nextDataSpec =
                    requestDataSpec.buildUpon().setPosition(readPosition).setLength(length).build();
            if (cacheWriteDataSource != null) {
                nextDataSource = cacheWriteDataSource;
            } else {
                nextDataSource = upstreamDataSource;
                cache.releaseHoleSpan(nextSpan);
                nextSpan = null;
            }
        }
        checkCachePosition =
                !currentRequestIgnoresCache && nextDataSource == upstreamDataSource
                        ? readPosition + MIN_READ_BEFORE_CHECKING_CACHE
                        : Long.MAX_VALUE;
        if (checkCache) {
            Assertions.checkState(isBypassingCache());
            if (nextDataSource == upstreamDataSource) {
                // Continue reading from upstream.
                return;
            }
            // We're switching to reading from or writing to the cache.
            try {
                closeCurrentSource();
            } catch (Throwable e) {
                if (castNonNull(nextSpan).isHoleSpan()) {
                    // Release the hole span before throwing, else we'll hold it forever.
                    cache.releaseHoleSpan(nextSpan);
                }
                throw e;
            }
        }

        if (nextSpan != null && nextSpan.isHoleSpan()) {
            currentHoleSpan = nextSpan;
        }
        currentDataSource = nextDataSource;
        currentDataSpec = nextDataSpec;
        currentDataSourceBytesRead = 0;
        long resolvedLength = nextDataSource.open(nextDataSpec);

        // Update bytesRemaining, actualUri and (if writing to cache) the cache metadata.
        mLastBytesCached = 0L;
        contentLength = 0L;
        ContentMetadataMutations mutations = new ContentMetadataMutations();
        if (nextDataSpec.length == C.LENGTH_UNSET && resolvedLength != C.LENGTH_UNSET) {
            bytesRemaining = resolvedLength;
            contentLength = readPosition + bytesRemaining;
            ContentMetadataMutations.setContentLength(mutations, contentLength);
        }
        if (isReadingFromUpstream()) {
            actualUri = nextDataSource.getUri();
            boolean isRedirected = !requestDataSpec.uri.equals(actualUri);
            ContentMetadataMutations.setRedirectedUri(mutations, isRedirected ? actualUri : null);
        }
        if (isWritingToCache()) {
            cache.applyContentMetadataMutations(key, mutations);
        }
        CacheSpan finalNextSpan = nextSpan;
        String cacheKey = nextDataSpec.key;
        if (!TextUtils.isEmpty(cacheKey)) {
            mBytesCached = cache.getCachedBytes(cacheKey, 0L, C.LENGTH_UNSET);
            if (contentLength <= 0L) {
                if (bytesRemaining >= 0) {
                    contentLength = readPosition + bytesRemaining;
                } else {
                    contentLength = ContentMetadata.getContentLength(cache.getContentMetadata(cacheKey));
                }
            }
            notifyProgressUpdate(1f);
        } else {
            mBytesCached = 0L;
        }
    }

    private void notifyProgressUpdate(float sampleRate) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (mLastBytesCached != mBytesCached && contentLength > 0L && eventListener != null && (sampleRate == 1f || elapsedRealtime - mLastNotifyTime > 100L)) {
            mLastBytesCached = mBytesCached;
            mLastNotifyTime = elapsedRealtime;
            float result = mBytesCached / (float) contentLength;
            if (result < 0f) {
                result = 0f;
            }
            if (result > 1f) {
                result = 1f;
            }
            eventListener.onCachedProgress(contentLength, mBytesCached, result);
        }
    }

    private void setNoBytesRemainingAndMaybeStoreLength(String key) throws IOException {
        bytesRemaining = 0;
        if (isWritingToCache()) {
            notifyProgressUpdate(1f);

            ContentMetadataMutations mutations = new ContentMetadataMutations();
            ContentMetadataMutations.setContentLength(mutations, readPosition);
            cache.applyContentMetadataMutations(key, mutations);
        }
    }

    private static Uri getRedirectedUriOrDefault(Cache cache, String key, Uri defaultUri) {
        @Nullable Uri redirectedUri = ContentMetadata.getRedirectedUri(cache.getContentMetadata(key));
        return redirectedUri != null ? redirectedUri : defaultUri;
    }

    private boolean isReadingFromUpstream() {
        return !isReadingFromCache();
    }

    private boolean isBypassingCache() {
        return currentDataSource == upstreamDataSource;
    }

    private boolean isReadingFromCache() {
        return currentDataSource == cacheReadDataSource;
    }

    private boolean isWritingToCache() {
        return currentDataSource == cacheWriteDataSource;
    }

    private void closeCurrentSource() throws IOException {
        if (currentDataSource == null) {
            return;
        }
        try {
            currentDataSource.close();
        } finally {
            currentDataSpec = null;
            currentDataSource = null;
            if (currentHoleSpan != null) {
                cache.releaseHoleSpan(currentHoleSpan);
                currentHoleSpan = null;
            }
        }
    }

    private void handleBeforeThrow(Throwable exception) {
        if (isReadingFromCache() || exception instanceof CacheException) {
            seenCacheError = true;
        }
    }

    private int shouldIgnoreCacheForRequest(DataSpec dataSpec) {
        if (ignoreCacheOnError && seenCacheError) {
            return CACHE_IGNORED_REASON_ERROR;
        } else if (ignoreCacheForUnsetLengthRequests && dataSpec.length == C.LENGTH_UNSET) {
            return CACHE_IGNORED_REASON_UNSET_LENGTH;
        } else {
            return CACHE_NOT_IGNORED;
        }
    }

    private void notifyCacheIgnored(@CacheIgnoredReason int reason) {
        if (eventListener != null) {
            eventListener.onCacheIgnored(reason);
        }
    }

    private void notifyBytesRead() {
        if (eventListener != null && totalCachedBytesRead > 0) {
            eventListener.onCachedBytesRead(cache.getCacheSpace(), totalCachedBytesRead);
            totalCachedBytesRead = 0;
        }
    }
}