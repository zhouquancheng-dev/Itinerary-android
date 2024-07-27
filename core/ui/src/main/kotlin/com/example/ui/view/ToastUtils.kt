package com.example.ui.view

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.ui.R

object ToastUtils {

    private var currentToast: Toast? = null

    /**
     * 确保在调用 showToast 方法时传递的是 Activity 的 Context
     */
    fun showCustomToast(context: Context, message: String) {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val parent: ViewGroup = (context as Activity).findViewById(android.R.id.content)
        val layout: View = inflater.inflate(R.layout.toast_custom_layout, parent, false)
        val text: TextView = layout.findViewById(R.id.custom_toast_message)
        text.text = message

        // 取消上一个Toast
        currentToast?.cancel()

        // 创建新的Toast
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        @Suppress("DEPRECATION")
        toast.view = layout
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()

        // 保存当前显示的Toast
        currentToast = toast
    }
}