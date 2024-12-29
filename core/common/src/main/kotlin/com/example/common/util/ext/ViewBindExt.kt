package com.example.common.util.ext

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

// 缓存 ViewBinding 方法的池
private val viewBindingMethodPool = ConcurrentHashMap<Class<*>, Method>()

/**
 * 创建 ViewBinding 实例
 *
 * @param layoutInflater 用于膨胀布局的 LayoutInflater
 * @param clazz 目标类
 * @param genericIndex 泛型索引，默认为 0
 * @return ViewBinding 实例
 * @throws RuntimeException 如果反射操作失败
 */
@Suppress("UNCHECKED_CAST")
fun <VB : ViewBinding> newViewBinding(layoutInflater: LayoutInflater, clazz: Class<*>, genericIndex: Int = 0): VB {
    try {
        // 获取 ViewBinding 的类型
        val vbClass = clazz.getGenericType().actualTypeArguments[genericIndex] as Class<VB>
        // 从缓存池中获取或反射获取 inflate 方法
        val inflateMethod = viewBindingMethodPool.getOrPut(vbClass) {
            vbClass.getMethod("inflate", LayoutInflater::class.java)
        }
        return inflateMethod.invoke(null, layoutInflater) as VB
    } catch (e: Exception) {
        e.printStackTrace()
        throw RuntimeException("Failed to create ViewBinding instance for ${clazz.simpleName}", e)
    }
}

/**
 * 获取泛型类型
 *
 * @return ParameterizedType 泛型类型
 * @throws ClassCastException 如果类型转换失败
 */
private fun Class<*>.getGenericType(): ParameterizedType {
    return try {
        this.genericSuperclass as ParameterizedType
    } catch (_: ClassCastException) {
        this.superclass.genericSuperclass as ParameterizedType
    }
}

inline fun <reified VB : ViewBinding> Activity.binding() = lazy {
    inflateBinding<VB>(layoutInflater).apply { setContentView(root) }
}

inline fun <reified VB : ViewBinding> Dialog.binding() = lazy {
    inflateBinding<VB>(layoutInflater).apply { setContentView(root) }
}

inline fun <reified VB : ViewBinding> PopupWindow.binding(context: Context) = lazy {
    inflateBinding<VB>(LayoutInflater.from(context))
}

inline fun <reified VB : ViewBinding> ViewGroup.binding(attachToRoot: Boolean = false) = lazy {
    inflateBinding<VB>(LayoutInflater.from(context), this, attachToRoot)
}

inline fun <reified VB : ViewBinding> inflateBinding(
    layoutInflater: LayoutInflater,
    root: ViewGroup?,
    attachToRoot: Boolean
) = try {
    VB::class.java.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java
    ).invoke(
        null,
        layoutInflater,
        root
    ) as VB
} catch (e: Exception) {
    VB::class.java.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    ).invoke(
        null,
        layoutInflater,
        root,
        attachToRoot
    ) as VB
}

inline fun <reified VB : ViewBinding> inflateBinding(
    layoutInflater: LayoutInflater
) =
    VB::class.java.getMethod(
        "inflate",
        LayoutInflater::class.java,
    ).invoke(
        null,
        layoutInflater,
    ) as VB


inline fun <reified VB : ViewBinding> binding() = FragmentBindingDelegate(VB::class.java)

@Suppress("UNCHECKED_CAST")
class FragmentBindingDelegate<VB : ViewBinding>(
    private val clazz: Class<VB>
) : ReadOnlyProperty<Fragment, VB> {

    private var isInitialized = false
    private var _binding: VB? = null
    private val binding: VB
        get() = _binding ?: throw IllegalStateException("Binding has not been initialized or is empty.")

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        if (!isInitialized) {
            thisRef.viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    _binding = null
                }
            })

            _binding = runCatching {
                clazz.getMethod("bind", View::class.java)
                    .invoke(null, thisRef.requireView()) as? VB
            }.getOrElse {
                throw IllegalStateException("Unable to create binding object: ${it.message}", it)
            }

            isInitialized = true
        }
        return binding
    }
}
