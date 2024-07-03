package com.example.network.service

import com.example.model.Response
import com.example.network.url.TIM_USER_SIG
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IMService {

    /**
     * 获取TIM登录票据
     */
    @GET(TIM_USER_SIG)
    suspend fun getUserSig(@Query("userId") userId: String): Response<String?>

}