package com.example.common.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.findViewTreeLifecycleOwner

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
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