package com.example.common.util.rv

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlin.math.roundToInt
import android.util.SparseArray
import android.animation.ValueAnimator
import android.graphics.*

/**
 * Versatile RecyclerView Divider Decoration
 *
 * Features:
 * 1. Image-based dividers
 * 2. Color-based dividers
 * 3. Adjustable spacing and margins
 * 4. Conditional divider visibility via callbacks
 * 5. Optional display of dividers at edges
 * 6. Type-specific divider visibility
 * 7. Support for all LayoutManagers (Linear, Grid, StaggeredGrid)
 * 8. Enhanced support for evenly spaced grid dividers
 * 9. Group-specific divider customization
 * 10. Animated, clickable, and dynamic dividers
 * 11. Auto Dark Mode support
 * 12. Customizable dashed or solid line styles
 */
class RecyclerViewDecoration(private val context: Context) : RecyclerView.ItemDecoration() {

    /** Show dividers at the start of items */
    var isStartVisible = false

    /** Show dividers at the end of items */
    var isEndVisible = false

    /**
     * Property to control the visibility of both start and end dividers simultaneously.
     * When set to true, both `isStartVisible` and `isEndVisible` are enabled.
     * When set to false, both are disabled.
     */
    var areEdgesVisible: Boolean
        get() = isStartVisible && isEndVisible
        set(value) {
            isStartVisible = value
            isEndVisible = value
        }

    /** Show dividers for expanded items */
    var isExpandVisible = false

    /** Orientation of dividers */
    var orientation = DividerOrientation.VERTICAL

    private var dividerSize = 1
    private var marginStart = 0
    private var marginEnd = 0
    private var dividerDrawable: Drawable? = null

    /** Allowed types for divider visibility */
    private var typePool: MutableList<Int>? = null

    /** Dynamic visibility toggle */
    var isEnabled: Boolean = true

    /** Divider click listener */
    var onDividerClickListener: ((Int) -> Unit)? = null

    /** Custom draw strategy */
    var customDrawStrategy: ((Canvas, RecyclerView, Int) -> Unit)? = null

    /** Animated alpha for dividers */
    var alpha: Int = 255

    /** Dynamic divider size */
    var dividerSizeCallback: ((Int) -> Int)? = null

    /** ViewType-specific divider styles */
    private val viewTypeDividerMap = mutableMapOf<Int, Drawable>()

    /** Group divider logic */
    var isGroupDividerEnabled: Boolean = false
    var groupCallback: ((Int) -> Boolean)? = null

    /** Cached offsets for performance */
    private val offsetCache = SparseArray<Rect>()

    /** Reusable objects for performance */
    private val reusableRect = Rect()
    private val reusablePaint = Paint()

    /** Add types for which dividers should be visible */
    fun addTypes(@LayoutRes vararg types: Int) {
        if (typePool == null) typePool = mutableListOf()
        typePool?.addAll(types.toList())
    }

    /** Set a drawable as the divider */
    fun setDividerDrawable(drawable: Drawable) {
        dividerDrawable = drawable
    }

    /** Set a drawable resource as the divider */
    fun setDividerDrawable(@DrawableRes drawableRes: Int) {
        dividerDrawable = ContextCompat.getDrawable(context, drawableRes)
            ?: throw IllegalArgumentException("Drawable resource not found")
    }

    /** Set a solid color as the divider */
    fun setDividerColor(@ColorInt color: Int) {
        dividerDrawable = ColorDrawable(color)
    }

    /** Set a color resource as the divider */
    fun setDividerColorRes(@ColorRes colorRes: Int) {
        dividerDrawable = ColorDrawable(ContextCompat.getColor(context, colorRes))
    }

    /** Set the background color for dividers */
    fun setDividerBackground(@ColorInt color: Int) {
        reusablePaint.color = color
    }

    /** Set divider size in pixels or dp */
    fun setDividerSize(size: Int, isDp: Boolean = false) {
        dividerSize = if (isDp) {
            (size * context.resources.displayMetrics.density).roundToInt()
        } else {
            size
        }
    }

    /** Set margins for the divider */
    fun setDividerMargin(start: Int, end: Int, isDp: Boolean = true) {
        val density = context.resources.displayMetrics.density
        marginStart = if (isDp) (start * density).roundToInt() else start
        marginEnd = if (isDp) (end * density).roundToInt() else end
    }

    /** Add ViewType-specific dividers */
    fun addViewTypeDivider(viewType: Int, drawable: Drawable) {
        viewTypeDividerMap[viewType] = drawable
    }

    fun addViewTypeDivider(viewType: Int, @DrawableRes drawableRes: Int) {
        val drawable = ContextCompat.getDrawable(context, drawableRes)
            ?: throw IllegalArgumentException("Drawable resource not found")
        viewTypeDividerMap[viewType] = drawable
    }

    /** Dynamic divider style */
    fun setDividerStyle(color: Int, strokeWidth: Float, dashEffect: FloatArray? = null) {
        reusablePaint.apply {
            this.color = color
            this.style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            this.pathEffect = dashEffect?.let { DashPathEffect(it, 0f) }
        }
    }

    /** Dynamic divider animation */
    fun setDividerAnimation(parent: RecyclerView, startColor: Int, endColor: Int, duration: Long) {
        val animator = ValueAnimator.ofArgb(startColor, endColor).apply {
            this.duration = duration
            addUpdateListener { animation ->
                reusablePaint.color = animation.animatedValue as Int
                parent.invalidateItemDecorations() // 刷新分割线
            }
        }
        animator.start()
    }

    /** Auto adjust divider color for Dark Mode */
    fun setAutoDarkModeSupport(lightColor: Int, darkColor: Int) {
        val uiMode = context.resources.configuration.uiMode
        val isDarkMode = (uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
        setDividerColor(if (isDarkMode) darkColor else lightColor)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (!isEnabled) return

        val position = parent.getChildAdapterPosition(view)
        if (offsetCache[position] == null) {
            val rect = Rect()
            calculateOffsets(position, rect, parent)
            offsetCache.put(position, rect)
        }
        outRect.set(offsetCache[position])
    }

    private fun calculateOffsets(position: Int, rect: Rect, parent: RecyclerView) {
        val layoutManager = parent.layoutManager ?: return
        val edge = Edge.calculate(position, layoutManager)

        val height = dividerSizeCallback?.invoke(position)
            ?: dividerDrawable?.intrinsicHeight?.takeIf { it > 0 } ?: dividerSize
        val width = dividerSizeCallback?.invoke(position)
            ?: dividerDrawable?.intrinsicWidth?.takeIf { it > 0 } ?: dividerSize

        adjustOrientation(layoutManager)

        when (orientation) {
            DividerOrientation.HORIZONTAL -> {
                val top = if (isStartVisible && edge.isTop) height else 0
                val bottom = if (isEndVisible || !edge.isBottom) height else 0
                rect.set(0, top, 0, bottom)
            }
            DividerOrientation.VERTICAL -> {
                val left = if (isStartVisible && edge.isLeft) width else 0
                val right = if (isEndVisible || !edge.isRight) width else 0
                rect.set(left, 0, right, 0)
            }
            DividerOrientation.GRID -> applyGridOffsets(rect, position, layoutManager, parent, width, height)
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (!isEnabled) return

        val layoutManager = parent.layoutManager ?: return
        adjustOrientation(layoutManager)

        when (orientation) {
            DividerOrientation.HORIZONTAL -> drawHorizontalDividers(canvas, parent)
            DividerOrientation.VERTICAL -> drawVerticalDividers(canvas, parent)
            DividerOrientation.GRID -> drawGridDividers(canvas, parent)
        }
    }

    private fun adjustOrientation(layoutManager: RecyclerView.LayoutManager) {
        orientation = when (layoutManager) {
            is GridLayoutManager, is StaggeredGridLayoutManager -> DividerOrientation.GRID
            is LinearLayoutManager -> if (layoutManager.orientation == RecyclerView.VERTICAL) {
                DividerOrientation.HORIZONTAL
            } else {
                DividerOrientation.VERTICAL
            }
            else -> orientation
        }
    }

    private fun applyGridOffsets(
        outRect: Rect,
        position: Int,
        layoutManager: RecyclerView.LayoutManager,
        parent: RecyclerView,
        width: Int,
        height: Int
    ) {
        val spanCount = when (layoutManager) {
            is GridLayoutManager -> layoutManager.spanCount
            is StaggeredGridLayoutManager -> layoutManager.spanCount
            else -> 1
        }

        val isFirstRow = position < spanCount
        val isLastRow = position >= parent.adapter!!.itemCount - spanCount

        outRect.set(
            if (isStartVisible) width else 0,
            if (isFirstRow && isStartVisible) height else 0,
            if (isEndVisible) width else 0,
            if (isLastRow && isEndVisible) height else 0
        )
    }

    private fun drawHorizontalDividers(canvas: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft + marginStart
        val right = parent.width - parent.paddingRight - marginEnd
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, reusableRect)
            val viewType = parent.adapter?.getItemViewType(i) ?: -1
            val drawable = viewTypeDividerMap[viewType] ?: dividerDrawable

            drawable?.apply {
                alpha = this@RecyclerViewDecoration.alpha
                setBounds(left, reusableRect.bottom - dividerSize, right, reusableRect.bottom)
                draw(canvas)
            } ?: reusablePaint.apply {
                canvas.drawLine(
                    left.toFloat(),
                    reusableRect.bottom.toFloat(),
                    right.toFloat(),
                    reusableRect.bottom.toFloat(),
                    this
                )
            }
        }
    }

    private fun drawVerticalDividers(canvas: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop + marginStart
        val bottom = parent.height - parent.paddingBottom - marginEnd
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, reusableRect)
            val viewType = parent.adapter?.getItemViewType(i) ?: -1
            val drawable = viewTypeDividerMap[viewType] ?: dividerDrawable

            drawable?.apply {
                alpha = this@RecyclerViewDecoration.alpha
                setBounds(reusableRect.right - dividerSize, top, reusableRect.right, bottom)
                draw(canvas)
            } ?: reusablePaint.apply {
                canvas.drawLine(
                    reusableRect.right.toFloat(),
                    top.toFloat(),
                    reusableRect.right.toFloat(),
                    bottom.toFloat(),
                    this
                )
            }
        }
    }

    private fun drawGridDividers(canvas: Canvas, parent: RecyclerView) {
        customDrawStrategy?.let { strategy ->
            for (i in 0 until parent.childCount) {
                strategy.invoke(canvas, parent, i)
            }
            return
        }

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, reusableRect)
            val viewType = parent.adapter?.getItemViewType(i) ?: -1
            val drawable = viewTypeDividerMap[viewType] ?: dividerDrawable

            drawable?.apply {
                alpha = this@RecyclerViewDecoration.alpha
                setBounds(reusableRect.left, reusableRect.top, reusableRect.right, reusableRect.bottom)
                draw(canvas)
            } ?: reusablePaint.apply {
                canvas.drawRect(reusableRect, this)
            }
        }
    }

    data class Edge(val isLeft: Boolean, val isTop: Boolean, val isRight: Boolean, val isBottom: Boolean) {
        companion object {
            fun calculate(position: Int, layoutManager: RecyclerView.LayoutManager): Edge {
                val isLeft = true
                val isTop = position == 0
                val isRight = true
                val isBottom = position == layoutManager.itemCount - 1
                return Edge(isLeft, isTop, isRight, isBottom)
            }
        }
    }

    enum class DividerOrientation {
        HORIZONTAL, VERTICAL, GRID
    }
}
