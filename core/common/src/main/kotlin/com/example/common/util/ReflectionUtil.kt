package com.example.common.util

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.concurrent.ConcurrentHashMap

object ReflectionUtil {
    private val viewBindingMethodPool = ConcurrentHashMap<Class<*>, Method>()

    /**
     * 创建 View Binding 实例
     *
     * @param layoutInflater 用于膨胀布局的 LayoutInflater
     * @param clazz 目标类
     * @return View Binding 实例
     * @throws RuntimeException 如果反射操作失败
     */
    @Suppress("UNCHECKED_CAST")
    fun <VB : ViewBinding> newViewBinding(layoutInflater: LayoutInflater, clazz: Class<*>): VB {
        try {
            val type = getGenericType(clazz)
            val vbClass = type.actualTypeArguments[0] as Class<*>
            val inflateMethod = viewBindingMethodPool.getOrPut(vbClass) {
                vbClass.getMethod("inflate", LayoutInflater::class.java)
            }
            return inflateMethod.invoke(null, layoutInflater) as VB
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Failed to create ViewBinding instance", e)
        }
    }

    /**
     * 获取泛型类型
     *
     * @param clazz 目标类
     * @return ParameterizedType 泛型类型
     * @throws ClassCastException 如果类型转换失败
     */
    private fun getGenericType(clazz: Class<*>): ParameterizedType {
        return try {
            clazz.genericSuperclass as ParameterizedType
        } catch (_: ClassCastException) {
            clazz.superclass.genericSuperclass as ParameterizedType
        }
    }

}
