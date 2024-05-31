package com.example.login.use

class LoginRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun login(username: String, password: String): Result<User> {
        // 实现具体的API请求逻辑
        return apiService.login(username, password)
    }
}
