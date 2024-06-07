package com.example.ui.webview

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.zqc.itinerary.webview.AndroidJsInterface

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CaptchaWebView(url: String) {
    val context = LocalContext.current

    val webViewState = remember {
        WebView(context).apply {
            // 页面布局
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // 设置成透明
            setBackgroundColor(0)

            // WebView设置
            settings.apply {
                // 可选，设置开启Chrome调试
                WebView.setWebContentsDebuggingEnabled(true)
                // 设置屏幕自适应
                useWideViewPort = true
                loadWithOverviewMode = true
                // 建议禁止缓存加载，以确保在攻击发生时可快速获取最新的阿里云验证码2.0进行对抗
                cacheMode = WebSettings.LOAD_NO_CACHE
                // 设置WebView组件支持加载JavaScript
                javaScriptEnabled = true
                domStorageEnabled = true
            }

            webViewClient = object : WebViewClient() {
                // 设置不使用默认浏览器，而直接使用WebView组件加载页面
                override fun shouldOverrideUrlLoading(
                    view: WebView?, request: WebResourceRequest?
                ): Boolean {
                    view?.loadUrl(url)
                    return true
                }
            }

            // 建立JavaScript调用Java接口的桥梁
            addJavascriptInterface(AndroidJsInterface(), "AndroidJsInterface")
        }
    }

    AndroidView(
        factory = { webViewState },
        modifier = Modifier.fillMaxSize()
    ) { view ->
        view.loadUrl(url)
    }
}