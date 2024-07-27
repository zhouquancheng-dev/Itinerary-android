package com.example.network.provider

import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.common.util.DataStoreUtils.getStringFlow
import com.example.common.util.DataStoreUtils.putString
import com.example.common.util.DataStoreUtils.remove
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenProvider @Inject constructor() {

    private var cachedToken: String? = null

    // 获取当前的身份验证令牌
    suspend fun getAuthToken(): String {
        return cachedToken ?: withContext(Dispatchers.IO) {
            val token = getStringFlow("auth_token").first()
            cachedToken = token
            token
        }
    }

    // 保存新的身份验证令牌
    private suspend fun setAuthToken(token: String) {
        withContext(Dispatchers.IO) {
            putString("auth_token", token)
            cachedToken = token
        }
    }

    // 清除当前的身份验证令牌
    suspend fun clearAuthToken() {
        withContext(Dispatchers.IO) {
            remove(stringPreferencesKey("auth_token"))
            cachedToken = null
        }
    }

    // 模拟令牌刷新
    suspend fun refreshToken(): String {
        // 假设通过网络请求获取新令牌
        val newToken = "new_token_from_network"
        setAuthToken(newToken)
        return newToken
    }
}