package com.example.common.util.ext

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.findViewTreeLifecycleOwner

/**
 * View显示
 */
fun View?.visible() {
    this?.visibility = View.VISIBLE
}

/**
 * View隐藏
 */
fun View?.gone() {
    this?.visibility = View.GONE
}

/**
 * View占位隐藏
 */
fun View?.inVisible() {
    this?.visibility = View.INVISIBLE
}

/**
 * View是否显示
 */
fun View?.isVisible(): Boolean {
    return this?.visibility == View.VISIBLE
}

/**
 * View是否隐藏
 */
fun View?.isGone(): Boolean {
    return this?.visibility == View.GONE
}

/**
 * View是否占位隐藏
 */
fun View?.isInVisible(): Boolean {
    return this?.visibility == View.INVISIBLE
}

/**
 * @param visible 如果为true 该View显示 否则隐藏
 */
fun View?.visibleOrGone(visible: Boolean) {
    if (visible) {
        this.visible()
    } else {
        this.gone()
    }
}

/**
 * @param visible 如果为true 该View显示 否则占位隐藏
 */
fun View?.visibleOrInvisible(visible: Boolean) {
    if (visible) {
        this.visible()
    } else {
        this.inVisible()
    }
}

/**
 * 显示传入的view集合
 */
fun visibleViews(vararg views: View?) {
    views.forEach {
        it?.visible()
    }
}

/**
 * 隐藏传入的view集合
 */
fun goneViews(vararg views: View?) {
    views.forEach {
        it?.gone()
    }
}

fun View.setOnDebouncedClickListener(interval: Long = 500L, onClick: (View) -> Unit) {
    var lastClickTime = 0L
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= interval) {
            lastClickTime = currentTime
            onClick(it)
        }
    }
}

/**
 * 获取当前是否为深色模式
 * 深色模式的值为: 0x21
 * 浅色模式的值为: 0x11
 * @return true 为深色模式   false 浅色模式
 */
fun Context.isDarkMode(): Boolean {
    return resources.configuration.uiMode == 0x21
}

fun Context.resolveColor(dayColorRes: Int, nightColorRes: Int): Int {
    return if (isDarkMode()) {
        getColorCompat(nightColorRes)
    } else {
        getColorCompat(dayColorRes)
    }
}

fun Context.dp2px(value: Int): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    value.toFloat(),
    resources.displayMetrics
).toInt()

fun Context.dp2px(value: Float) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)

val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)

val Context.inputMethodManager
    get() = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

fun Context.getDrawableCompat(@DrawableRes drawable: Int): Drawable =
    requireNotNull(ContextCompat.getDrawable(this, drawable))

fun Context.getColorCompat(@ColorRes color: Int) =
    ContextCompat.getColor(this, color)

fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))

fun Fragment.addStatusBarColorUpdate(@ColorRes colorRes: Int) {
    view?.findViewTreeLifecycleOwner()?.lifecycle?.addObserver(
        StatusBarColorLifecycleObserver(
            requireActivity(),
            requireContext().getColorCompat(colorRes),
        )
    )
}

fun AppCompatActivity.addStatusBarColorUpdate(@ColorRes colorRes: Int) {
    lifecycle.addObserver(
        StatusBarColorLifecycleObserver(
            this,
            getColor(colorRes),
        )
    )
}

fun ComponentActivity.setExitOnBackPressedCallback(action: (() -> Unit)? = null) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            action?.invoke()
            return
        }
    })
}

fun AppCompatActivity.setExitOnBackPressedCallback(action: (() -> Unit)? = null) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            action?.invoke()
            return
        }
    })
}