package com.example.common.util

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import java.lang.reflect.InvocationTargetException

class ViewBindViewHolder<B : ViewBinding>(val bindView: B) : RecyclerView.ViewHolder(bindView.root)

class HeaderViewHolder(headerView: ViewBinding) : RecyclerView.ViewHolder(headerView.root)

class FooterViewHolder(footerView: ViewBinding) : RecyclerView.ViewHolder(footerView.root)

/**
 * 一个通用的RecyclerView适配器，支持头部和尾部视图。
 *
 * @param T 数据项的类型
 * @param B ViewBinding的类型
 * @param diffCallback 用于计算列表差异的DiffUtil.ItemCallback
 */
abstract class ViewBindAdapter<T : Any, B : ViewBinding>(diffCallback: DiffUtil.ItemCallback<T>) :
    ListAdapter<T, RecyclerView.ViewHolder>(diffCallback) {

    // 定义Item类型：头部、普通、尾部
    val typeHeader = 0
    val typeNormal = 1
    val typeFooter = 2

    // 头部视图的ViewBinding对象
    var headerViewBinding: ViewBinding? = null

    // 尾部视图的ViewBinding对象
    var footerViewBinding: ViewBinding? = null

    /**
     * 返回Item的数量，根据是否有头部和尾部视图进行调整
     */
    override fun getItemCount(): Int {
        return when {
            headerViewBinding != null && footerViewBinding != null -> currentList.size + 2
            headerViewBinding != null && footerViewBinding == null -> currentList.size + 1
            headerViewBinding == null && footerViewBinding != null -> currentList.size + 1
            else -> currentList.size
        }
    }

    /**
     * 返回指定位置的Item类型
     *
     * @param position Item的位置
     * @return Item类型
     */
    override fun getItemViewType(position: Int): Int {
        return when {
            headerViewBinding != null && position == 0 -> typeHeader
            footerViewBinding != null && position == itemCount - 1 -> typeFooter
            else -> typeNormal
        }
    }

    /**
     * 添加头部视图
     *
     * @param headerViewBinding 头部视图的ViewBinding对象
     */
    fun addHeader(headerViewBinding: ViewBinding) {
        this.headerViewBinding = headerViewBinding
        notifyItemInserted(0)
    }

    /**
     * 移除头部视图
     */
    fun removeHeader() {
        if (headerViewBinding != null) {
            notifyItemRemoved(0)
            headerViewBinding = null
        }
    }

    /**
     * 添加尾部视图
     *
     * @param footerViewBinding 尾部视图的ViewBinding对象
     */
    fun addFooter(footerViewBinding: ViewBinding) {
        this.footerViewBinding = footerViewBinding
        notifyItemInserted(itemCount - 1)
    }

    /**
     * 移除尾部视图
     */
    fun removeFooter() {
        if (footerViewBinding != null) {
            notifyItemRemoved(itemCount - 1)
            footerViewBinding = null
        }
    }
}

class BindViewAdapterConfig<T : Any, B : ViewBinding> {
    // RecyclerView的布局管理器，可以设置为LinearLayoutManager、GridLayoutManager等
    var layoutManger: RecyclerView.LayoutManager? = null

    // Item点击事件，接收参数：ViewHolder、Item数据和位置
    var mOnItemClick: ((itemViewHolder: B, itemData: T, position: Int) -> Unit)? = null

    // Item长按事件，接收参数：ViewHolder、Item数据和位置
    var mOnItemLongClick: ((itemViewHolder: B, itemData: T, position: Int) -> Unit)? = null

    // Item装饰器，用于自定义Item间距、分割线等，接收参数：Rect、View、RecyclerView和State
    // outRect: 用于指定Item的边距
    // view: 当前Item的视图
    // parent: 当前的RecyclerView
    // state: 当前的RecyclerView状态
    var itemDecoration: ((outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) -> Unit)? = null

    // 必须实现的Item布局数据绑定方法，接收参数：ViewHolder、Item数据和位置
    lateinit var bindView: ((itemViewHolder: B, itemData: T, position: Int) -> Unit)

    // RecyclerView的头部视图绑定对象
    var headerViewBinding: ViewBinding? = null

    // RecyclerView的尾部视图绑定对象
    var footerViewBinding: ViewBinding? = null

    // 必须实现设置Item布局数据绑定方法，用于将数据绑定到视图上
    // itemViewHolder: 代表单个Item视图的ViewHolder
    // itemData: 代表单个Item的数据
    // position: 代表Item的位置
    fun onBindView(bindView: (itemViewHolder: B, itemData: T, position: Int) -> Unit) {
        this.bindView = bindView
    }

    // 添加Item装饰器
    fun addItemDecoration(decoration: (outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) -> Unit) {
        itemDecoration = decoration
    }

    // 设置Item点击事件
    fun onItemClick(onItemClick: (itemViewHolder: B, itemData: T, position: Int) -> Unit) {
        mOnItemClick = onItemClick
    }

    // 设置Item长按事件
    fun onItemLongClick(onItemLongClick: (itemViewHolder: B, itemData: T, position: Int) -> Unit) {
        mOnItemLongClick = onItemLongClick
    }
}

@Throws(
    NoSuchMethodException::class,
    InvocationTargetException::class,
    IllegalAccessException::class
)
inline fun <reified B : ViewBinding> Class<B>.inflateViewBind(
    context: Context,
    parent: ViewGroup
): B {
    val method = getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.javaPrimitiveType
    )
    return method.invoke(null, LayoutInflater.from(context), parent, false) as B
}

inline fun <T : Any, reified B : ViewBinding> RecyclerView.bindAdapter(
    dataList: MutableList<T>? = null,
    noinline itemComparator: (T, T) -> Boolean = { oldItem, newItem -> oldItem == newItem },
    noinline contentComparator: (T, T) -> Boolean = { oldItem, newItem -> oldItem == newItem },
    config: BindViewAdapterConfig<T, B>.() -> Unit
): ViewBindAdapter<T, B> {

    val adapterConfig = BindViewAdapterConfig<T, B>().apply(config)

    val bindAdapter = object : ViewBindAdapter<T, B>(DefaultDiffCallback(itemComparator, contentComparator)) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                typeHeader -> headerViewBinding?.let { HeaderViewHolder(it) }
                    ?: throw IllegalStateException("HeaderViewBinding is null")
                typeFooter -> footerViewBinding?.let { FooterViewHolder(it) }
                    ?: throw IllegalStateException("FooterViewBinding is null")
                else -> ViewBindViewHolder(B::class.java.inflateViewBind(context, parent))
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (getItemViewType(position) == typeNormal) {
                val i = if (null != headerViewBinding) position - 1 else position

                val viewHolder = holder as ViewBindViewHolder<*>
                val binding = viewHolder.bindView as B

                adapterConfig.bindView.invoke(binding, getItem(i), i)

                holder.itemView.setOnClickListener {
                    adapterConfig.mOnItemClick?.invoke(binding, getItem(i), i)
                }

                holder.itemView.setOnLongClickListener {
                    adapterConfig.mOnItemLongClick?.invoke(binding, getItem(i), i)
                    return@setOnLongClickListener true
                }
            }
        }
    }

    dataList?.let { bindAdapter.submitList(it) }

    adapterConfig.itemDecoration?.also {
        if (itemDecorationCount > 0) removeItemDecoration(getItemDecorationAt(0))
        addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                it.invoke(outRect, view, parent, state)
            }
        })
    }

    layoutManager = adapterConfig.layoutManger ?: LinearLayoutManager(context)

    adapter = bindAdapter

    adapterConfig.headerViewBinding?.let {
        bindAdapter.addHeader(it)
    }

    adapterConfig.footerViewBinding?.let {
        bindAdapter.addFooter(it)
    }

    return bindAdapter
}

class DefaultDiffCallback<T : Any>(
    private val itemComparator: (T, T) -> Boolean,
    private val contentComparator: (T, T) -> Boolean
) : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return itemComparator(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return contentComparator(oldItem, newItem)
    }
}

inline fun <T : Any, reified B : ViewBinding> ViewPager2.bindAdapter(
    dataList: MutableList<T>? = null,
    noinline itemComparator: (T, T) -> Boolean = { oldItem, newItem -> oldItem == newItem },
    noinline contentComparator: (T, T) -> Boolean = { oldItem, newItem -> oldItem == newItem },
    config: BindViewAdapterConfig<T, B>.() -> Unit
): ViewBindAdapter<T, B> {

    val adapterConfig = BindViewAdapterConfig<T, B>().apply(config)

    val bindAdapter = object : ViewBindAdapter<T, B>(DefaultDiffCallback(itemComparator, contentComparator)) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindViewHolder<B> {
            return ViewBindViewHolder(B::class.java.inflateViewBind(context, parent))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewHolder = holder as ViewBindViewHolder<*>
            val binding = viewHolder.bindView as B

            adapterConfig.bindView.invoke(binding, getItem(position), position)

            holder.itemView.setOnClickListener {
                adapterConfig.mOnItemClick?.invoke(binding, getItem(position), position)
            }

            holder.itemView.setOnLongClickListener {
                adapterConfig.mOnItemLongClick?.invoke(binding, getItem(position), position)
                return@setOnLongClickListener true
            }
        }
    }

    dataList?.let { bindAdapter.submitList(it) }

    adapter = bindAdapter
    return bindAdapter
}
