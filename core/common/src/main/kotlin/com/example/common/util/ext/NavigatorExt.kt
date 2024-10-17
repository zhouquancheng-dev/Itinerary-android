package com.example.common.util.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment

// Standard
inline fun <reified T : Activity> startActivity(context: Context) {
    (context as? Activity)?.let {
        val intent = Intent(it, T::class.java)
        it.startActivity(intent)
    }
}

inline fun startDeepLink(context: Context, deepLinkUri: String, configureIntent: (Intent) -> Unit = {}) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkUri)).apply {
        configureIntent(this)
    }
    context.startActivity(intent)
}

inline fun Activity.startDeepLink(deepLinkUri: String, configureIntent: (Intent) -> Unit = {}) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkUri)).apply {
        configureIntent(this)
    }
    startActivity(intent)
}

inline fun <reified T : Activity> startAcWithBundle(
    context: Context,
    extras: Bundle? = null
) {
    (context as? Activity)?.let {
        val intent = Intent(it, T::class.java).apply {
            if (extras != null) {
                putExtras(extras)
            }
        }
        it.startActivity(intent)
    }
}

inline fun <reified T : Activity> startAcWithIntent(
    context: Context,
    configureIntent: (Intent) -> Unit = {}
) {
    (context as? Activity)?.let {
        val intent = Intent(it, T::class.java).apply(configureIntent)
        it.startActivity(intent)
    }
}

// Activity
inline fun <reified T : Activity> Activity.startActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

inline fun <reified T : Activity> Activity.startAcWithBundle(
    extras: Bundle? = null
) {
    val intent = Intent(this, T::class.java).apply {
        extras?.let { putExtras(it) }
    }
    startActivity(intent)
}

inline fun <reified T : Activity> Activity.startAcWithIntent(
    configureIntent: (Intent) -> Unit = {}
) {
    val intent = Intent(this, T::class.java).apply {
        configureIntent(this)
    }
    startActivity(intent)
}

// Fragment
inline fun <reified T : Activity> Fragment.startActivity() {
    requireActivity().startActivity<T>()
}

inline fun <reified T : Activity> Fragment.startAcWithBundle(
    extras: Bundle? = null
) {
    requireActivity().startAcWithBundle<T>(extras)
}

inline fun <reified T : Activity> Fragment.startAcWithIntent(
    configureIntent: (Intent) -> Unit = {}
) {
    activity?.startAcWithIntent<T>(configureIntent)
}