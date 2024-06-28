package com.example.login.listener

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.util.regex.Pattern

class MyNotificationListenerService : NotificationListenerService() {

    private var listener: ((String) -> Unit)? = null

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let {
            val notification = it.notification
            val extras = notification.extras
            val text = extras.getCharSequence("android.text")?.toString()
            text?.let {
                val otp = extractOtp(text)
                if (otp != null) {
                    if (otp.isNotEmpty()) {
                        listener?.invoke(otp)
                    }
                }
            }
        }
    }

    private fun extractOtp(message: String): String? {
        val pattern = Pattern.compile("\\d{6}")
        val matcher = pattern.matcher(message)
        return if (matcher.find()) matcher.group(0) else ""
    }
}