package com.example.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class PressedImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var scaleSize: Float = 0.90f

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        scaleX = if (pressed) scaleSize else 1.0f
        scaleY = if (pressed) scaleSize else 1.0f
    }
}