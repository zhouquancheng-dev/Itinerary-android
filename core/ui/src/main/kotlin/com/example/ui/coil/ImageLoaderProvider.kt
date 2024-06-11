package com.example.ui.coil

import android.os.Build
import coil.ComponentRegistry
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.decode.VideoFrameDecoder
import com.example.common.BaseApplication

object ImageLoaderProvider {
    private val defaultImageLoader: ImageLoader by lazy {
        createImageLoader {}
    }
    private val gifImageLoader: ImageLoader by lazy {
        createImageLoader {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
    }
    private val videoImageLoader: ImageLoader by lazy {
        createImageLoader {
            add(VideoFrameDecoder.Factory())
        }
    }
    private val svgImageLoader: ImageLoader by lazy {
        createImageLoader {
            add(SvgDecoder.Factory())
        }
    }

    private fun createImageLoader(componentBuilder: ComponentRegistry.Builder.() -> Unit): ImageLoader {
        return ImageLoader
            .Builder(BaseApplication.getInstance())
            .components(componentBuilder)
            .build()
    }

    fun getImageLoader(type: LoadType): ImageLoader = when (type) {
        LoadType.DEFAULT -> defaultImageLoader
        LoadType.GIF -> gifImageLoader
        LoadType.VIDEO -> videoImageLoader
        LoadType.SVG -> svgImageLoader
    }
}
