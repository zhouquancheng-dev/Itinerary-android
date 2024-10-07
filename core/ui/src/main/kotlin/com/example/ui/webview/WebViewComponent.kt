package com.example.ui.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View.LAYER_TYPE_HARDWARE
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebViewComponent(
    url: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var canGoBack by remember { mutableStateOf(false) }

    val webView = remember { WebView(context) }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = {
            webView.apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                setupWebViewSettings(this)

                webViewClient = createWebViewClient(
                    onPageStarted = {},
                    onPageFinished = {
                        canGoBack = webView.canGoBack()
                    }
                )

                loadUrl(url)
            }
        },
        update = { view ->
            view.loadUrl(url)
        }
    )

    // 使用 BackHandler 处理返回事件
    BackHandler(enabled = canGoBack) {
        // 如果 WebView 可以后退，则调用 goBack()
        webView.goBack()
    }
}

/**
 * 设置 WebView 的基本配置
 */
@SuppressLint("SetJavaScriptEnabled")
private fun setupWebViewSettings(webView: WebView) {
    webView.settings.apply {
        javaScriptEnabled = true // 启用 JavaScript
        loadWithOverviewMode = true
        domStorageEnabled = true  // 启用 DOM 存储
        useWideViewPort = true  // 支持缩放
        loadWithOverviewMode = true  // 缩放适应屏幕
        cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK  // 优先使用缓存
        setSupportZoom(true)  // 启用页面缩放
        builtInZoomControls = true  // 启用缩放控制
        displayZoomControls = false  // 隐藏缩放控制
        loadsImagesAutomatically = true  // 自动加载图片
        blockNetworkImage = false  // 不阻塞图片加载
        javaScriptCanOpenWindowsAutomatically = true  // 允许 JS 打开窗口
        allowFileAccess = false  // 禁止访问文件
        allowContentAccess = false  // 禁止访问内容
    }
    // 设置 WebChromeClient 以处理 JavaScript 对话框、进度条等功能
    webView.webChromeClient = WebChromeClient()

    webView.setLayerType(LAYER_TYPE_HARDWARE, null)
}

/**
 * 创建 WebViewClient
 */
private fun createWebViewClient(
    onPageStarted: () -> Unit,
    onPageFinished: () -> Unit
): WebViewClient {
    return object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            onPageStarted()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            onPageFinished()
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return true
        }
    }
}