package com.example.network.service

import com.example.model.UserResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserService {

    /**
     * 用户登录请求
     * @param token 令牌
     * @param username 用户名
     * @param password 密码
     */
    @POST("/user/login")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    suspend fun login(
        @Header("Authorization") token: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): UserResponse<String?>

    /**
     * 用户注册请求
     * @param username 用户名
     * @param password 密码
     */
    @POST("/user/register")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String
    ): UserResponse<Any?>

    /**
     * 用户登出请求
     * @param token 令牌
     */
    @POST("/user/logout")
    suspend fun logout(
        @Header("Authorization") token: String,
    ): UserResponse<Any?>

    /**
     * 自动登录请求
     */
    @POST("/user/auto-login")
    suspend fun autoLogin(
        @Header("Authorization") token: String
    ): UserResponse<String?>

}