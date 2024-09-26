package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.ui.components.symbols.AppIcons
import com.example.ui.theme.JetItineraryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardTopAppBar(
    title: String,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    iconSize: DpSize = DpSize(24.dp, 24.dp),
    showBackButton: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    colors: @Composable () -> TopAppBarColors = { TopAppBarDefaults.topAppBarColors() },
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    onPressClick: () -> Unit = {}
) {
    val titleComposable = @Composable {
        Text(
            text = title,
            maxLines = 1,
            style = textStyle,
            overflow = TextOverflow.Ellipsis
        )
    }

    TopAppBar(
        title = titleComposable,
        modifier = Modifier.fillMaxWidth(),
        navigationIcon = if (showBackButton) { { BackButton(iconSize, onPressClick) } } else { {} },
        actions = actions,
        colors = colors(),
        windowInsets = windowInsets
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardCenterTopAppBar(
    title: String,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    iconSize: DpSize = DpSize(24.dp, 24.dp),
    showBackButton: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    colors: @Composable () -> TopAppBarColors = { TopAppBarDefaults.topAppBarColors() },
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    onPressClick: () -> Unit = {}
) {
    val titleComposable = @Composable {
        Text(
            text = title,
            maxLines = 1,
            style = textStyle,
            overflow = TextOverflow.Ellipsis
        )
    }

    CenterAlignedTopAppBar(
        title = titleComposable,
        modifier = Modifier.fillMaxWidth(),
        navigationIcon = if (showBackButton) { { BackButton(iconSize, onPressClick) } } else { {} },
        actions = actions,
        colors = colors(),
        windowInsets = windowInsets
    )
}

@Composable
private fun BackButton(
    iconSize: DpSize = DpSize(24.dp, 24.dp),
    onPressClick: () -> Unit
) {
    IconButton(onClick = onPressClick) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = AppIcons.ArrowBackIosNew,
            contentDescription = "back"
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun MovieTopBarPreview() {
    JetItineraryTheme {
        Column {
            StandardTopAppBar(title = "TopAppBar")
            StandardCenterTopAppBar(title = "CenterTopAppBar", showBackButton = false)
        }
    }
}
