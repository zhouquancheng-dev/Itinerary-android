package com.example.common.util.sp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Jetpack DataStore 是一种数据存储解决方案，允许您使用协议缓冲区存储键值对或类型化对象。
 * DataStore 使用 Kotlin 协程和 Flow 以异步、一致的事务方式存储数据。
 *
 * 在 Kotlin 文件顶层创建 DataStore 实例一次，便可在应用的所有其余部分通过此属性访问该实例.这样可以更轻松地将 DataStore 保留为单例
 * 使用 preferencesDataStore 委托创建一个 DataStore 实例，并将 Context 作为接收者
 */
private const val MY_APP_DATASTORE = "AppDataStore"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = MY_APP_DATASTORE)

/**
 * DataStore 工具类
 */
object DataStoreUtils {

    private lateinit var dataStore: DataStore<Preferences>

    /**
     * 初始化 DataStore
     * @param context Context
     */
    @JvmStatic
    fun init(context: Context) {
        dataStore = context.dataStore
    }

    /**
     * 泛型方法：异步读取数据
     * @param key 数据键
     * @param default 默认值
     * @return Flow<T> 包含数据的 Flow
     */
    private inline fun <reified T> readDataFlow(key: Preferences.Key<T>, default: T): Flow<T> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[key] ?: default
            }

    /**
     * 泛型方法：同步读取数据
     * @param key 数据键
     * @param default 默认值
     * @return T 读取到的数据
     */
    private inline fun <reified T> readDataSync(key: Preferences.Key<T>, default: T): T =
        runBlocking {
            dataStore.data.first()[key] ?: default
        }

    /**
     * 泛型方法：异步存储数据
     * @param key 数据键
     * @param value 值
     */
    private suspend inline fun <reified T> writeData(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    /**
     * 泛型方法：同步存储数据
     * @param key 数据键
     * @param value 值
     */
    private inline fun <reified T> writeDataSync(key: Preferences.Key<T>, value: T) {
        runBlocking { writeData(key, value) }
    }

    // Boolean 类型数据处理

    /**
     * 异步读取 Boolean 数据
     * @param key 键名
     * @param default 默认值
     * @return Flow<Boolean>
     */
    fun getBooleanFlow(key: String, default: Boolean = false): Flow<Boolean> =
        readDataFlow(booleanPreferencesKey(key), default)

    /**
     * 同步读取 Boolean 数据
     * @param key 键名
     * @param default 默认值
     * @return Boolean
     */
    fun getBooleanSync(key: String, default: Boolean = false): Boolean =
        readDataSync(booleanPreferencesKey(key), default)

    /**
     * 异步存储 Boolean 数据
     * @param key 键名
     * @param value 值
     */
    suspend fun putBoolean(key: String, value: Boolean) {
        writeData(booleanPreferencesKey(key), value)
    }

    /**
     * 同步存储 Boolean 数据
     * @param key 键名
     * @param value 值
     */
    fun putBooleanSync(key: String, value: Boolean) {
        writeDataSync(booleanPreferencesKey(key), value)
    }

    // Int 类型数据处理

    /**
     * 异步读取 Int 数据
     * @param key 键名
     * @param default 默认值
     * @return Flow<Int>
     */
    fun getIntFlow(key: String, default: Int = 0): Flow<Int> =
        readDataFlow(intPreferencesKey(key), default)

    /**
     * 同步读取 Int 数据
     * @param key 键名
     * @param default 默认值
     * @return Int
     */
    fun getIntSync(key: String, default: Int = 0): Int =
        readDataSync(intPreferencesKey(key), default)

    /**
     * 异步存储 Int 数据
     * @param key 键名
     * @param value 值
     */
    suspend fun putInt(key: String, value: Int) {
        writeData(intPreferencesKey(key), value)
    }

    /**
     * 同步存储 Int 数据
     * @param key 键名
     * @param value 值
     */
    fun putIntSync(key: String, value: Int) {
        writeDataSync(intPreferencesKey(key), value)
    }

    // String 类型数据处理

    /**
     * 异步读取 String 数据
     * @param key 键名
     * @param default 默认值
     * @return Flow<String>
     */
    fun getStringFlow(key: String, default: String = ""): Flow<String> =
        readDataFlow(stringPreferencesKey(key), default)

    /**
     * 同步读取 String 数据
     * @param key 键名
     * @param default 默认值
     * @return String
     */
    fun getStringSync(key: String, default: String = ""): String =
        readDataSync(stringPreferencesKey(key), default)

    /**
     * 异步存储 String 数据
     * @param key 键名
     * @param value 值
     */
    suspend fun putString(key: String, value: String) {
        writeData(stringPreferencesKey(key), value)
    }

    /**
     * 同步存储 String 数据
     * @param key 键名
     * @param value 值
     */
    fun putStringSync(key: String, value: String) {
        writeDataSync(stringPreferencesKey(key), value)
    }

    // Float 类型数据处理

    /**
     * 异步读取 Float 数据
     * @param key 键名
     * @param default 默认值
     * @return Flow<Float>
     */
    fun getFloatFlow(key: String, default: Float = 0f): Flow<Float> =
        readDataFlow(floatPreferencesKey(key), default)

    /**
     * 同步读取 Float 数据
     * @param key 键名
     * @param default 默认值
     * @return Float
     */
    fun getFloatSync(key: String, default: Float = 0f): Float =
        readDataSync(floatPreferencesKey(key), default)

    /**
     * 异步存储 Float 数据
     * @param key 键名
     * @param value 值
     */
    suspend fun putFloat(key: String, value: Float) {
        writeData(floatPreferencesKey(key), value)
    }

    /**
     * 同步存储 Float 数据
     * @param key 键名
     * @param value 值
     */
    fun putFloatSync(key: String, value: Float) {
        writeDataSync(floatPreferencesKey(key), value)
    }

    // Long 类型数据处理

    /**
     * 异步读取 Long 数据
     * @param key 键名
     * @param default 默认值
     * @return Flow<Long>
     */
    fun getLongFlow(key: String, default: Long = 0L): Flow<Long> =
        readDataFlow(longPreferencesKey(key), default)

    /**
     * 同步读取 Long 数据
     * @param key 键名
     * @param default 默认值
     * @return Long
     */
    fun getLongSync(key: String, default: Long = 0L): Long =
        readDataSync(longPreferencesKey(key), default)

    /**
     * 异步存储 Long 数据
     * @param key 键名
     * @param value 值
     */
    suspend fun putLong(key: String, value: Long) {
        writeData(longPreferencesKey(key), value)
    }

    /**
     * 同步存储 Long 数据
     * @param key 键名
     * @param value 值
     */
    fun putLongSync(key: String, value: Long) {
        writeDataSync(longPreferencesKey(key), value)
    }

    /**
     * 异步清除所有数据
     */
    suspend fun clearAll() {
        dataStore.edit {
            it.clear()
        }
    }

    /**
     * 同步清除所有数据
     */
    fun clearAllSync() {
        runBlocking {
            dataStore.edit {
                it.clear()
            }
        }
    }

    /**
     * 泛型方法：异步删除指定键的数据
     * @param key 数据键
     */
    suspend fun <T> remove(key: Preferences.Key<T>) {
        dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    /**
     * 泛型方法：同步删除指定键的数据
     * @param key 数据键
     */
    fun <T> removeSync(key: Preferences.Key<T>) {
        runBlocking {
            dataStore.edit { preferences ->
                preferences.remove(key)
            }
        }
    }
}
