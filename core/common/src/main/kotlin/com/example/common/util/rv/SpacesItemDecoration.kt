package com.example.common.util.rv

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Orientation

class SpacesItemDecoration(
    private val space: Int,
    @Orientation private val orientation: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (orientation == LinearLayoutManager.VERTICAL) {
            outRect.run {
                left = space
                right = space
                bottom = space / 2
            }
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space
            }
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            outRect.run {
                top = space
                right = space
                bottom = space
            }
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = space
            }
        }
    }
}