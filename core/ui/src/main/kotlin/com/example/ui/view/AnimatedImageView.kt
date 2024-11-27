package com.example.ui.view

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class AnimatedImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("scaleX", 0.90f, 1.10f),
        PropertyValuesHolder.ofFloat("scaleY", 0.90f, 1.10f)
    ).apply {
        duration = 400
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
    }

    init {
        startAnimation()
    }

    private fun startAnimation() {
        scaleAnimator.start()
    }

    private fun stopAnimation() {
        scaleAnimator.cancel()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }
}
