package com.example.common.util

import android.content.Context
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

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
}
