package com.example.common.util.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment

inline fun Context.startDeepLink(deepLinkUri: String, configureIntent: (Intent) -> Unit = {}) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkUri)).apply {
        configureIntent(this)
    }
    startActivity(intent)
}

inline fun Activity.startDeepLink(deepLinkUri: String, configureIntent: (Intent) -> Unit = {}) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkUri)).apply {
        configureIntent(this)
    }
    startActivity(intent)
}

inline fun <reified T : Activity> Context.startActivity(
    extras: Bundle? = null,
    configureIntent: (Intent) -> Unit = {}
) {
    val intent = Intent(this, T::class.java).apply {
        extras?.let { putExtras(it) }
        configureIntent(this)
    }
    startActivity(intent)
}

inline fun <reified T : Activity> Activity.startActivity(
    extras: Bundle? = null,
    configureIntent: (Intent) -> Unit = {}
) {
    val intent = Intent(this, T::class.java).apply {
        extras?.let { putExtras(it) }
        configureIntent(this)
    }
    startActivity(intent)
}

inline fun <reified T : Activity> Fragment.startActivity(
    extras: Bundle? = null,
    configureIntent: (Intent) -> Unit = {}
) {
    requireContext().startActivity<T>(extras, configureIntent)
}