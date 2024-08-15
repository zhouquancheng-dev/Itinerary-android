package com.example.network.service

import com.example.model.Response
import com.example.network.url.UPLOAD
import com.example.network.url.TIM_USER_SIG
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface IMService {

    /**
     * 获取TIM登录票据
     */
    @GET(TIM_USER_SIG)
    suspend fun getUserSig(@Query("userId") userId: String): Response<String?>

    /**
     * 上传文件
     */
    @Multipart
    @POST(UPLOAD)
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): Response<String?>

}