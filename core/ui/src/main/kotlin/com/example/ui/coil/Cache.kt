package com.example.ui.coil

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.util.DebugLogger

fun getAsyncImageLoader(context: Context) =
    ImageLoader.Builder(context)
        .memoryCache {
            newMemoryCache(context)
        }
        .diskCache {
            newDiskCache(context)
        }
        .logger(DebugLogger())
        .build()

fun newMemoryCache(context: Context) =
    MemoryCache.Builder()
        .maxSizePercent(context)
        .strongReferencesEnabled(true)
        .weakReferencesEnabled(true)
        .build()

fun newDiskCache(context: Context) =
    DiskCache.Builder()
        .directory(context.cacheDir.resolve("image_cache"))
        .maxSizeBytes(100L * 1024 * 1024)
        .build()