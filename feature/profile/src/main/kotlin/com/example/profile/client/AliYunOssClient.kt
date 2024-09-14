package com.example.profile.client

import android.content.Context
import android.util.Log
import com.aleyn.annotation.Singleton
import com.alibaba.sdk.android.oss.ClientConfiguration
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider
import com.alibaba.sdk.android.oss.model.CannedAccessControlList
import com.alibaba.sdk.android.oss.model.CreateBucketRequest
import com.alibaba.sdk.android.oss.model.CreateBucketResult
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.alibaba.sdk.android.oss.model.StorageClass
import com.example.common.config.AppConfig
import com.example.network.okhttp.OkHttpDns
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Singleton
class AliYunOssClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpDns: OkHttpDns
) {
    private var ossClient: OSSClient? = null

    companion object {
        private val TAG = AliYunOssClient::class.java.simpleName

        private const val OSS_ENDPOINT = "https://oss-cn-shenzhen.aliyuncs.com"
        private const val MY_OSS_DOMAIN = "https://oss.zyuxr.top"
        private const val BUCKET_NAME = "web-userzhou"
    }

    /**
     * 创建 OSSClient
     */
    fun createClient(
        accessKeyId: String,
        accessKeySecret: String,
        securityToken: String
    ): OSSClient? {
        // 开启日志记录, 将一些日志信息记录在本地, 手机内置sd卡路径\OSSLog\logs.csv下写入日志文件
        if (AppConfig.IS_DEBUG) {
            OSSLog.enableLog()
        }

        return ossClient ?: synchronized(this) {
            // 双重检查锁定，确保只有一个线程能初始化 ossClient
            ossClient ?: run {
                val config = ClientConfiguration().apply {
                    connectionTimeout = 15 * 1000
                    socketTimeout = 15 * 1000
                    maxConcurrentRequest = 5
                    maxErrorRetry = 2
                    maxLogSize = 3 * 1024 * 1024
                }

                val dispatcher = Dispatcher().apply { maxRequests = config.maxConcurrentRequest }

                val builder = OkHttpClient.Builder().apply {
                    dns(okHttpDns)
                    connectTimeout(config.connectionTimeout.toLong(), TimeUnit.MILLISECONDS)
                    readTimeout(config.socketTimeout.toLong(), TimeUnit.MILLISECONDS)
                    writeTimeout(config.socketTimeout.toLong(), TimeUnit.MILLISECONDS)
                    followRedirects(config.isFollowRedirectsEnable)
                    followSslRedirects(config.isFollowRedirectsEnable)
                    dispatcher(dispatcher)

                    if (config.proxyHost != null && config.proxyPort != 0) {
                        proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(config.proxyHost, config.proxyPort)))
                    }
                }

                config.okHttpClient = builder.build()

                try {
                    val credentialProvider = OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken)
                    ossClient = OSSClient(context, MY_OSS_DOMAIN, credentialProvider, config)
                } catch (e: Exception) {
                    Log.e(TAG, "CreateOssClient Exception: ", e)
                }

                ossClient
            }
        }
    }

    /**
     * 创建存储空间 Bucket
     */
    fun createBucket(
        ossClient: OSSClient,
        bucketName: String
    ) {
        val createBucketRequest = CreateBucketRequest(bucketName).apply {
            bucketACL = CannedAccessControlList.Private
            bucketStorageClass = StorageClass.Standard
        }
        ossClient.asyncCreateBucket(createBucketRequest, object : OSSCompletedCallback<CreateBucketRequest, CreateBucketResult> {
            override fun onSuccess(request: CreateBucketRequest?, result: CreateBucketResult?) {
                Log.d(TAG, "AsyncCreateBucket Success")
            }

            override fun onFailure(
                request: CreateBucketRequest?,
                clientException: ClientException?,
                serviceException: ServiceException?
            ) {
                clientException?.run {
                    Log.e(TAG, "AsyncCreateBucket ClientException: ", this)
                }
                serviceException?.run {
                    Log.e(TAG, "AsyncCreateBucket ErrorCode: $errorCode")
                    Log.e(TAG, "AsyncCreateBucket RequestId: $requestId")
                    Log.e(TAG, "AsyncCreateBucket HostId: $hostId")
                    Log.e(TAG, "AsyncCreateBucket RawMessage: $rawMessage")
                }
            }
        })
    }

    /**
     * 异步上传二进制byte数组
     */
    fun uploadFile(
        ossClient: OSSClient,
        objectKey: String,
        byteArray: ByteArray,
        callback: UploadCallback
    ) {
        // 检查 objectKey 是否包含后缀名
        require(objectKey.contains(".")) {
            "objectKey 需要包含文件后缀名，例如 example.txt"
        }

        // 确保 objectKey 不包含 BUCKET_NAME
        require(!objectKey.contains(BUCKET_NAME)) {
            "objectKey 不应包含 Bucket 名称，只需提供文件的完整路径，例如 xxx/example.txt 或 example.txt"
        }

        callback.onStart()

        val put = PutObjectRequest(BUCKET_NAME, objectKey, byteArray)
        put.setProgressCallback { _, currentSize, totalSize ->
            // currentSize表示已上传文件的大小，单位字节
            // totalSize表示上传文件的总大小，单位字节
            callback.onProgress(currentSize, totalSize)
        }

        ossClient.asyncPutObject(put, object : OSSCompletedCallback<PutObjectRequest, PutObjectResult> {
            override fun onSuccess(request: PutObjectRequest?, result: PutObjectResult?) {
                // Bucket私有权限使用如下方法获取上传成功后的URL
//                val expiration = System.currentTimeMillis() + 3600 * 1000
//                val signatureRequest = GeneratePresignedUrlRequest(BUCKET_NAME, objectKey, expiration, HttpMethod.GET)
//                val objectURL = ossClient.presignConstrainedObjectURL(signatureRequest)

                val objectURL = "$MY_OSS_DOMAIN/$objectKey"

                Log.d(TAG, "uploadFile UploadSuccess")
                Log.d(TAG, "uploadFile ETag: ${result?.eTag}")
                Log.d(TAG, "uploadFile RequestId: ${result?.requestId}")

                callback.onSuccess(objectURL)
            }

            override fun onFailure(
                request: PutObjectRequest?,
                clientException: ClientException?,
                serviceException: ServiceException?
            ) {
                clientException?.run {
                    Log.e(TAG, "UploadFile ClientException: ", this)
                    callback.onFailure(this)
                }
                serviceException?.run {
                    Log.e(TAG, "UploadFile ErrorCode: $errorCode")
                    Log.e(TAG, "UploadFile RequestId: $requestId")
                    Log.e(TAG, "UploadFile HostId: $hostId")
                    Log.e(TAG, "UploadFile RawMessage: $rawMessage")
                    callback.onFailure(this)
                }
            }
        })
    }

}