package com.example.common.util.permissionUtil.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

object TimeDataSource {
    private val coroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "PermissionSettings")

    fun updateTime(context: Context, tag: String) {
        CoroutineScope(coroutineDispatcher).launch {
            context.dataStore.edit { settings ->
                settings[longPreferencesKey(tag)] = System.currentTimeMillis()
            }
        }
    }

    suspend fun getTime(context: Context, tag: String): Long {
        return context.dataStore.data
            .map { preferences ->
                preferences[longPreferencesKey(tag)] ?: 0L
            }
            .flowOn(coroutineDispatcher)
            .first()
    }
}