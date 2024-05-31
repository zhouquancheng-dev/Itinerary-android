package com.example.ui.coil

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.ui.R
import okhttp3.HttpUrl
import java.io.File
import java.nio.ByteBuffer

/**
 * Set the data to load.
 *
 * The default supported data types are:
 * - [String] (mapped to a [Uri])
 * - [Uri] ("android.resource", "content", "file", "http", and "https" schemes only)
 * - [HttpUrl]
 * - [File]
 * - [DrawableRes]
 * - [Drawable]
 * - [Bitmap]
 * - [ByteArray]
 * - [ByteBuffer]
 */
@Composable
fun LoadAsyncImage(
    model: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    loadType: LoadType = LoadType.DEFAULT,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    @DrawableRes placeholder: Int = R.drawable.module_ic_coil_placeholder,
    @DrawableRes errorImage: Int = R.drawable.module_ic_coil_error
) {
    val context = LocalContext.current
    val imageLoader = ImageLoaderProvider.getImageLoader(loadType)

    val imageRequest = remember(model, loadType) {
        ImageRequest.Builder(context)
            .data(model)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .placeholder(placeholder)
            .error(errorImage)
            .crossfade(true)
            .build()
    }

    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        imageLoader = imageLoader,
        alignment = alignment
    )
}
