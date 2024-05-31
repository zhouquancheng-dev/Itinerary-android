package com.example.login.use

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<User> {
        return repository.login(username, password)
    }
}
