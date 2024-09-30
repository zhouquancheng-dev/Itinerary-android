package com.example.ui.coil

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.util.DebugLogger
import com.example.common.config.AppConfig

fun getAsyncImageLoader(context: PlatformContext) =
    ImageLoader.Builder(context)
        .memoryCache {
            newMemoryCache(context)
        }
        .diskCache {
            newDiskCache(context)
        }
        .logger(
            if (AppConfig.IS_DEBUG) {
                DebugLogger()
            } else {
                null
            }
        )
        .build()

fun newMemoryCache(context: PlatformContext) =
    MemoryCache.Builder()
        .maxSizePercent(context)
        .strongReferencesEnabled(true)
        .weakReferencesEnabled(true)
        .build()

fun newDiskCache(context: PlatformContext) =
    DiskCache.Builder()
        .directory(context.cacheDir.resolve("image_cache"))
        .maxSizeBytes(100L * 1024 * 1024)
        .build()