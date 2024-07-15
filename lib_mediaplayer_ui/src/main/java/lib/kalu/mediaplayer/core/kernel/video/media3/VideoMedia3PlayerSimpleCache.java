package lib.kalu.mediaplayer.core.kernel.video.media3;

import android.content.Context;


import androidx.media3.common.util.UnstableApi;
import androidx.media3.database.StandaloneDatabaseProvider;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;

import java.io.File;

@UnstableApi
public final class VideoMedia3PlayerSimpleCache {

    private static SimpleCache mSimpleCache;

    public static SimpleCache getSimpleCache(
             Context context,
             int cacheMax,
             String cacheDir) {

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
            name = "mediaV2";
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
