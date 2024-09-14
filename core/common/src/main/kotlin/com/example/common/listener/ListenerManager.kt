package com.example.common.listener

object ListenerManager {

    private val listeners = mutableListOf<Any>()

    fun registerListeners(instance: Any) {
        val methods = instance::class.java.declaredMethods
        for (method in methods) {
            if (method.isAnnotationPresent(ListenerMethod::class.java)) {
                method.invoke(instance)
            }
        }
        listeners.add(instance)
    }

    fun unregisterListeners(instance: Any) {
        val methods = instance::class.java.declaredMethods
        for (method in methods) {
            if (method.isAnnotationPresent(UnregisterMethod::class.java)) {
                method.invoke(instance)
            }
        }
        listeners.remove(instance)
    }

}
