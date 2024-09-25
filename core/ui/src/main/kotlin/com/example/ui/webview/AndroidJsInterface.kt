package com.example.ui.webview

import android.util.Log
import android.webkit.JavascriptInterface

class AndroidJsInterface {
    @JavascriptInterface
    fun getVerifyResult(verifyResult: String?) {
        // 获取到验证结果后，可以进行不同的业务操作
        Log.d("AndroidJsInterface", "getVerifyResult: $verifyResult")
    }
}