package lib.kalu.mediaplayer.core.kernel.video.media3;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.database.StandaloneDatabaseProvider;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;

import java.io.File;

import lib.kalu.mediaplayer.type.PlayerType;

@UnstableApi
public final class VideoMedia3PlayerSimpleCache {

    private static SimpleCache mSimpleCache;

    public static SimpleCache getSimpleCache(Context context,
                                             @PlayerType.CacheLocalType.Value
                                             int cacheLocalType,
                                             @PlayerType.CacheSizeType.Value
                                             int cacheSizeType,
                                             @NonNull
                                             String cacheDirName) {

        if (null != mSimpleCache) {
            mSimpleCache.release();
        }

        // 缓存大小
        int size;
        switch (cacheSizeType) {
            case PlayerType.CacheSizeType._100:
                size = 100;
                break;
            case PlayerType.CacheSizeType._200:
                size = 200;
                break;
            case PlayerType.CacheSizeType._400:
                size = 400;
                break;
            case PlayerType.CacheSizeType._800:
                size = 800;
                break;
            default:
                size = 1024;
                break;
        }

        // 缓存位置
        File dir;
        if (cacheLocalType == PlayerType.CacheLocalType.EXTERNAL) {
            File externalCacheDir = context.getExternalCacheDir();
            dir = new File(externalCacheDir, cacheDirName);
        } else {
            File cacheDir = context.getCacheDir();
            dir = new File(cacheDir, cacheDirName);
        }

        if (!dir.exists()) {
            dir.mkdirs();
        }
        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(size * 1024 * 1024);
        StandaloneDatabaseProvider provider = new StandaloneDatabaseProvider(context);
        mSimpleCache = new SimpleCache(dir, evictor, provider);
        return mSimpleCache;
    }
}
