package com.example.ui.view

import android.view.Gravity
import com.hjq.toast.ToastParams
import com.hjq.toast.Toaster
import com.hjq.toast.style.CustomToastStyle

object ToasterUtil {

    enum class ToastStatus(val layoutId: Int) {
        SUCCESS(com.example.ui.R.layout.toast_custom_view_success),
        ERROR(com.example.ui.R.layout.toast_custom_view_error),
        WARN(com.example.ui.R.layout.toast_custom_view_warn)
    }

    fun showCustomToaster(message: String, status: ToastStatus) {
        val params = ToastParams().apply {
            text = message
            style = CustomToastStyle(status.layoutId, Gravity.CENTER)
        }
        Toaster.show(params)
    }

}
