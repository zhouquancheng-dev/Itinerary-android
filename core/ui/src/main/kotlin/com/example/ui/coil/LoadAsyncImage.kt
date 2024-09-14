package com.example.ui.coil

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.size.Precision
import com.example.ui.R

@OptIn(ExperimentalCoilApi::class)
@Composable
fun LoadAsyncImage(
    model: Any?,
    modifier: Modifier = Modifier,
    memoryCachePolicy: CachePolicy = CachePolicy.ENABLED,
    diskCachePolicy: CachePolicy = CachePolicy.ENABLED,
    networkCachePolicy: CachePolicy = CachePolicy.ENABLED,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    @DrawableRes placeholderResId: Int = R.drawable.module_ic_coil_placeholder,
    @DrawableRes errorResId: Int = R.drawable.module_ic_coil_error
) {
    val context = LocalPlatformContext.current

    setSingletonImageLoaderFactory {
        getAsyncImageLoader(context)
    }

    val imageRequest = remember(model) {
        ImageRequest.Builder(context)
            .data(model)
            .memoryCachePolicy(memoryCachePolicy)
            .diskCachePolicy(diskCachePolicy)
            .networkCachePolicy(networkCachePolicy)
            .placeholder(placeholderResId)
            .error(errorResId)
            .crossfade(true)
            .precision(Precision.INEXACT)
            .build()
    }

    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale
    )
}