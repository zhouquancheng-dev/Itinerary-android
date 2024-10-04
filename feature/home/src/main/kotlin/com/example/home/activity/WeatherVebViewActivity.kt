package com.example.home.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import com.example.common.util.addStatusBarColorUpdate
import com.example.home.R
import com.example.ui.webview.WebViewComponent

class WeatherVebViewActivity : AppCompatActivity() {

    private val url by lazy { intent.getStringExtra("url") ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        addStatusBarColorUpdate(R.color.ColorFF38383E)
        setContent {
            WebViewComponent(
                url = url,
                modifier = Modifier.systemBarsPadding()
            )
        }
    }

}