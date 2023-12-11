package lib.kalu.mediaplayer.core.kernel.video.exo2;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.database.StandaloneDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

public final class VideoExoPlayer2Cache {

    private static SimpleCache mSimpleCache;

    public static SimpleCache getSimpleCache(
            @NonNull Context context,
            @NonNull int cacheMax,
            @NonNull String cacheDir) {

        if (null != mSimpleCache) {
            mSimpleCache.release();
        }

        // cache
        int size;
        if (cacheMax <= 0) {
            size = 1024;
        } else {
            size = cacheMax;
        }

        String name;
        if (null == cacheDir || cacheDir.length() <= 0) {
            name = "exoV2";
        } else {
            name = cacheDir;
        }

        File dir = new File(context.getCacheDir(), name);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(size * 1024 * 1024);
        StandaloneDatabaseProvider provider = new StandaloneDatabaseProvider(context);
        mSimpleCache = new SimpleCache(dir, evictor, provider);
        return mSimpleCache;
    }
}
