package com.example.ui.webview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.ui.components.StandardCenterTopAppBar
import com.example.ui.theme.JetItineraryTheme

@OptIn(ExperimentalMaterial3Api::class)
class WebViewActivity : ComponentActivity() {

    private val url by lazy { intent.getStringExtra("url") ?: "" }
    private val title by lazy { intent.getStringExtra("title") ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetItineraryTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        StandardCenterTopAppBar(
                            title = title,
                            onPressClick = { finish() }
                        )
                    }
                ) { paddingValues ->
                    WebViewComponent(
                        url = url,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }

}