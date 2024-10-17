package com.example.common.util.sp

import com.tencent.mmkv.MMKV

object MmKvUtils {
    private val mmkv: MMKV = MMKV.defaultMMKV()

    fun remove(key: String) {
        mmkv.removeValueForKey(key)
    }

    fun save(key: String, data: String) {
        mmkv.encode(key, data)
    }

    fun save(key: String, data: Int) {
        mmkv.encode(key, data)
    }

    fun save(key: String, data: Long) {
        mmkv.encode(key, data)
    }

    fun save(key: String, data: Boolean) {
        mmkv.encode(key, data)
    }

    fun save(key: String, data: Double) {
        mmkv.encode(key, data)
    }

    fun save(key: String, data: Float) {
        mmkv.encode(key, data)
    }

    fun get(key: String, defaultValue: String): String {
        return mmkv.decodeString(key, defaultValue) ?: defaultValue
    }

    fun get(key: String, defaultValue: Int): Int {
        return mmkv.decodeInt(key, defaultValue)
    }

    fun get(key: String, defaultValue: Long): Long {
        return mmkv.decodeLong(key, defaultValue)
    }

    fun get(key: String, defaultValue: Boolean): Boolean {
        return mmkv.decodeBool(key, defaultValue)
    }

    fun get(key: String, defaultValue: Double): Double {
        return mmkv.decodeDouble(key, defaultValue)
    }

    fun get(key: String, defaultValue: Float): Float {
        return mmkv.decodeFloat(key, defaultValue)
    }
}