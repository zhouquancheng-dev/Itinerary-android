package com.example.common.util.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment

inline fun <reified T : Activity> Context.startActivity(
    deepLinkUri: String? = null,
    extras: Bundle? = null,
    configureIntent: (Intent) -> Unit = {}
) {
    val intent = if (deepLinkUri != null) {
        Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkUri)).apply {
            extras?.let { putExtras(it) }
            configureIntent(this)
        }
    } else {
        Intent(this, T::class.java).apply {
            extras?.let { putExtras(it) }
            configureIntent(this)
        }
    }

    if (this is Activity) {
        startActivity(intent)
    } else {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}

inline fun <reified T : Activity> Activity.startActivity(
    deepLinkUri: String? = null,
    extras: Bundle? = null,
    configureIntent: (Intent) -> Unit = {}
) {
    val intent = if (deepLinkUri != null) {
        Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkUri)).apply {
            configureIntent(this)
            extras?.let { putExtras(it) }
        }
    } else {
        Intent(this, T::class.java).apply {
            extras?.let { putExtras(it) }
            configureIntent(this)
        }
    }
    startActivity(intent)
}

inline fun <reified T : Activity> Fragment.startActivity(
    deepLinkUri: String? = null,
    extras: Bundle? = null,
    configureIntent: (Intent) -> Unit = {}
) {
    requireContext().startActivity<T>(deepLinkUri, extras, configureIntent)
}