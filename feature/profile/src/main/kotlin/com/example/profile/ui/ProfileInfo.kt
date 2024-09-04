package com.example.profile.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.profile.R
import com.example.ui.components.StandardCenterTopAppBar
import com.example.ui.theme.JetItineraryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfo(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            StandardCenterTopAppBar(
                title = stringResource(R.string.profile_info_title),
                onPressClick = onBackClick
            )
        },
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ListItem(
                headlineContent = {
                    Text(text = "头像", style = MaterialTheme.typography.titleLarge)
                },
                trailingContent = {
                    Icon(
                        painter = painterResource(R.drawable.chevron_right_24dp),
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ProfileInfoPreview() {
    JetItineraryTheme {
        ProfileInfo {}
    }
}