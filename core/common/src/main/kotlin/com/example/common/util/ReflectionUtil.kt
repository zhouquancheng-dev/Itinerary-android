package com.example.common.util

import android.content.Context
import android.view.LayoutInflater
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

object ReflectionUtil {

    /**
     * 使用反射获取应用程序的 ApplicationContext
     *
     * @param packageName 应用程序的包名
     * @param className   应用程序的类名
     * @return 应用程序的 ApplicationContext，如果获取失败则返回 null
     */
    fun getApplicationContextUsingReflection(packageName: String, className: String): Context? {
        return try {
            // 根据包名和类名获取 Class 对象
            val appClass = Class.forName("$packageName.$className")
            // 获取 getContext() 方法
            val getContextMethod: Method = appClass.getMethod("getContext")
            // 调用 getContext() 方法获取 ApplicationContext
            getContextMethod.invoke(null) as Context
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            null
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
            null
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            null
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 创建 View Binding 实例
     *
     * @param layoutInflater 用于膨胀布局的 LayoutInflater
     * @param clazz 目标类
     * @return View Binding 实例
     * @throws RuntimeException 如果反射操作失败
     */
    @Suppress("UNCHECKED_CAST")
    fun <VB> newViewBinding(layoutInflater: LayoutInflater, clazz: Class<*>): VB {
        return try {
            // 获取泛型参数对象
            val type = getGenericType(clazz)

            // 获取 ViewBinding 类
            val clazzVB = type.actualTypeArguments[0] as Class<*>

            // 获取 inflate 方法
            val inflateMethod = clazzVB.getMethod("inflate", LayoutInflater::class.java)

            // 调用 inflate 方法创建 ViewBinding 实例
            inflateMethod.invoke(null, layoutInflater) as VB
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
        } catch (e: ClassCastException) {
            clazz.superclass.genericSuperclass as ParameterizedType
        }
    }
}
