package com.demo.photo.glide.model;

import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.demo.photo.Constants;
import com.demo.photo.util.CardUtil;

import java.io.File;

/**
 * description: Glide外部缓存路径
 * created by kalu on 17-10-15 上午5:00
 */
final class GlideDiskCache extends DiskLruCacheFactory {

    public GlideDiskCache() {
        this(DiskCache.Factory.DEFAULT_DISK_CACHE_DIR, DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE);
    }

    public GlideDiskCache(int diskCacheSize) {
        this(DiskCache.Factory.DEFAULT_DISK_CACHE_DIR, diskCacheSize);
    }

    public GlideDiskCache(final String diskCacheName, int diskCacheSize) {
        super(new CacheDirectoryGetter() {
            @Override
            public File getCacheDirectory() {
                File cacheDirectory = new File(CardUtil.getSDCardPath() + Constants.CACHE_IMAGE + diskCacheName);
                if (!cacheDirectory.exists()) {
                    cacheDirectory.mkdirs();
                }
                return cacheDirectory;
            }
        }, diskCacheSize);
    }
}
