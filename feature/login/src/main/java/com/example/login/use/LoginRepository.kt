package com.example.login.use

import com.example.model.UserResponse
import com.example.network.ItineraryNetwork
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val network: ItineraryNetwork
) {
    suspend fun login(username: String, password: String): UserResponse<String?> {
        // 实现具体的API请求逻辑
        return network.login("token", username, password)
    }
}
