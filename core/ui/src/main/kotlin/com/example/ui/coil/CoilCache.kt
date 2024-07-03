package com.example.ui.coil

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.util.DebugLogger

fun getAsyncImageLoader(context: PlatformContext): ImageLoader {
    return ImageLoader.Builder(context)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .networkCachePolicy(CachePolicy.ENABLED)
        .memoryCache {
            newMemoryCache(context)
        }
        .diskCache {
            newDiskCache(context)
        }
        .logger(DebugLogger())
        .build()
}

fun newMemoryCache(context: PlatformContext): MemoryCache {
    return MemoryCache.Builder()
        .maxSizePercent(context, 0.3)
        .strongReferencesEnabled(true)
        .build()
}

fun newDiskCache(context: PlatformContext): DiskCache {
    return DiskCache.Builder()
        .directory(context.cacheDir.resolve("image_cache"))
        .maxSizeBytes(100L * 1024 * 1024)
        .build()
}
