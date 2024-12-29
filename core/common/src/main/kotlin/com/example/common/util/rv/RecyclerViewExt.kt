package com.example.common.util.rv

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 纵向recyclerview
 * @receiver RecyclerView
 * @return RecyclerView
 */
fun RecyclerView.vertical(): RecyclerView {
    layoutManager = LinearLayoutManager(this.context)
    return this
}

/**
 * 横向 recyclerview
 * @receiver RecyclerView
 * @return RecyclerView
 */
fun RecyclerView.horizontal(): RecyclerView {
    layoutManager = LinearLayoutManager(this.context).apply {
        orientation = RecyclerView.HORIZONTAL
    }
    return this
}

/**
 * grid recyclerview
 * @receiver RecyclerView
 * @return RecyclerView
 */
fun RecyclerView.grid(
    count: Int,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL
): RecyclerView {
    layoutManager = GridLayoutManager(this.context, count, orientation, false)
    return this
}

/**
 * 配置万能分割线
 * @receiver RecyclerView
 * @param block [@kotlin.ExtensionFunctionType] Function1<DefaultDecoration, Unit>
 * @return RecyclerView
 */
fun RecyclerView.divider(block: RecyclerViewDecoration.() -> Unit): RecyclerView {
    val itemDecoration = RecyclerViewDecoration(context).apply(block)
    addItemDecoration(itemDecoration)
    return this
}
