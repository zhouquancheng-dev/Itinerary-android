package com.example.login.use

import com.example.model.UserResponse
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(username: String, password: String): UserResponse<String?> {
        return repository.login(username, password)
    }
}
