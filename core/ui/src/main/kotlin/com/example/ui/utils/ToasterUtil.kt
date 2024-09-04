package com.example.ui.utils

import android.view.Gravity
import com.example.common.BaseApplication
import com.hjq.toast.ToastParams
import com.hjq.toast.Toaster
import com.hjq.toast.style.CustomToastStyle

object ToasterUtil {

    enum class ToastStatus {
        SUCCESS, ERROR, WARN
    }

    private fun getLayoutId(status: ToastStatus): Int {
        val isNightMode = BaseApplication.isModeNightYes()
        return when (status) {
            ToastStatus.SUCCESS -> if (isNightMode) com.example.ui.R.layout.toast_custom_view_success_white else com.example.ui.R.layout.toast_custom_view_success_black
            ToastStatus.ERROR -> if (isNightMode) com.example.ui.R.layout.toast_custom_view_error_white else com.example.ui.R.layout.toast_custom_view_error_black
            ToastStatus.WARN -> if (isNightMode) com.example.ui.R.layout.toast_custom_view_warn_white else com.example.ui.R.layout.toast_custom_view_warn_black
        }
    }

    fun showCustomToaster(message: String, status: ToastStatus) {
        val params = ToastParams().apply {
            text = message
            style = CustomToastStyle(getLayoutId(status), Gravity.CENTER)
        }
        Toaster.show(params)
    }
}
