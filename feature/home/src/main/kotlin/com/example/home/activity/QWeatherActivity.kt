package com.example.home.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.common.util.addStatusBarColorUpdate
import com.example.home.R

class QWeatherActivity : AppCompatActivity() {

    private val url by lazy { intent.getStringExtra("q_weather_url") ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        addStatusBarColorUpdate(R.color.ColorFF38383E)
        setContentView(R.layout.activity_qweather)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val webView = findViewById<WebView>(R.id.q_web_view)
        configureWebView(webView)
        webView.loadUrl(url)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView(webView: WebView) {
        val webSettings = webView.settings

        // 启用 JavaScript，确保网页可以正常运行 JavaScript 脚本。
        webSettings.javaScriptEnabled = true

        // 优化 WebView 渲染性能。
        webSettings.useWideViewPort = true // 允许使用宽视图端口
        webSettings.loadWithOverviewMode = true // 加载时调整到适应屏幕的大小
        webSettings.domStorageEnabled = true // 启用 DOM 存储，支持更多网页功能
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT // 使用默认的缓存模式

        // 禁用文件访问和内容访问，以减少内存占用。
        webSettings.allowFileAccess = false // 禁止文件访问
        webSettings.allowContentAccess = false // 禁止内容访问

        // 设置 WebViewClient 以自定义网页加载行为。
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webSettings.blockNetworkImage = false // 页面加载完成后允许图片加载
            }
        }

        // 设置 WebChromeClient 以处理 JavaScript 对话框、进度条等功能。
        webView.webChromeClient = WebChromeClient()
        // 使用硬件加速
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null) // 使用硬件加速
    }

}