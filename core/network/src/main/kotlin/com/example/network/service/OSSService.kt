package com.example.network.service

import com.example.model.Response
import com.example.model.oss.StsResponse
import com.example.network.url.OSS_STS_TOKEN
import com.example.network.url.UPLOAD
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OSSService {

    /**
     * 上传文件
     */
    @Multipart
    @POST(UPLOAD)
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("bucketDirName") bucketDirName: RequestBody,
        @Part("fileName") fileName: RequestBody
    ): Response<String?>

    /**
     * 获取OSS STS临时凭证
     */
    @GET(OSS_STS_TOKEN)
    suspend fun getStsToken(): Response<StsResponse>

}