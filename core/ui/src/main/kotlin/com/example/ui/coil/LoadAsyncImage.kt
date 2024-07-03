package com.example.ui.coil

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.ui.R

@OptIn(ExperimentalCoilApi::class)
@Composable
fun LoadAsyncImage(
    model: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    placeholder: Painter = painterResource(R.drawable.module_ic_coil_placeholder),
    errorImage: Painter = painterResource(R.drawable.module_ic_coil_error)
) {
    val context = LocalContext.current

    val imageRequest = remember(model) {
        ImageRequest.Builder(context)
            .data(model)
            .crossfade(true)
            .build()
    }

    setSingletonImageLoaderFactory { platformContext ->
        getAsyncImageLoader(platformContext)
    }

    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        placeholder = placeholder,
        error = errorImage,
        modifier = modifier,
        contentScale = contentScale,
        alignment = alignment
    )
}
