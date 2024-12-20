package com.example.ui.coil

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.example.ui.R

@Composable
fun LoadAsyncImage(
    model: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    @DrawableRes placeholderResId: Int = R.drawable.module_ic_coil_placeholder,
    @DrawableRes errorResId: Int = R.drawable.module_ic_coil_error
) {
    val context = LocalContext.current

    val imageRequest = remember(model) {
        ImageRequest.Builder(context)
            .data(model)
            .placeholder(placeholderResId)
            .error(errorResId)
            .crossfade(true)
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