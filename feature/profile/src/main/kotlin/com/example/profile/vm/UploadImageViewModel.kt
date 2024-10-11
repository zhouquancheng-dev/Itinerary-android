package com.example.profile.vm

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.viewModelScope
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider
import com.alibaba.sdk.android.oss.common.utils.DateUtil
import com.example.common.data.Constants.TIM_TAG
import com.example.common.di.AppDispatchers.IO
import com.example.common.di.Dispatcher
import com.example.common.base.vm.BaseViewModel
import com.example.model.SUCCESS
import com.example.network.ItineraryNetwork
import com.example.profile.client.AliYunOssClient
import com.example.profile.client.OssBucketConfig.PROFILE_IMG_FOLDER
import com.example.profile.client.UploadCallback
import com.example.profile.state.DialogType
import com.hjq.toast.Toaster
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMUserFullInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class UploadImageViewModel @Inject constructor(
    @Dispatcher(IO) ioDispatcher: CoroutineDispatcher,
    private val networkApi: ItineraryNetwork,
    private val aliYunOssClient: AliYunOssClient
) : BaseViewModel(ioDispatcher) {

    private val tag = UploadImageViewModel::class.java.simpleName

    private val _dialogType = MutableStateFlow(DialogType.NONE)
    internal val dialogType = _dialogType.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress = _uploadProgress.asStateFlow()

    fun uploadImage(
        imageBitmap: ImageBitmap,
        callback: () -> Unit
    ) {
        fetchData { networkApi.getStsToken() }
            .onEach { response ->
                if (response.status == SUCCESS) {
                    val credentials = response.data?.credentials
                    credentials?.run {
                        val oss = aliYunOssClient.createClient(
                            accessKeyId,
                            accessKeySecret,
                            securityToken
                        )

                        // 如果StsToken即将过期，有效时间小于5分钟，则重新获取并更新StsToken
                        if (shouldUpdateStsToken(credentials.expiration)) {
                            updateStsToken(oss)
                        }

                        val byteArray = imageBitmapToByteArray(imageBitmap, Bitmap.CompressFormat.JPEG)
                        val loginUserId = V2TIMManager.getInstance().loginUser
                        val imageName = "im_${loginUserId}_profile_img_${System.currentTimeMillis()}"
                        val uploadObjectKey = "$PROFILE_IMG_FOLDER$imageName.jpeg"
                        val queryPrefix = "${PROFILE_IMG_FOLDER}im_${loginUserId}_profile_img"

                        oss?.let {
                            uploadAndUpdateFaceUrl(it, uploadObjectKey, byteArray, queryPrefix, callback)
                        }
                    }
                }
            }
            .catch { e ->
                Log.e(tag, "Error fetching STS token: $e")
            }
            .launchIn(viewModelScope)
    }

    private fun updateStsToken(oss: OSSClient?) {
        fetchData { networkApi.getStsToken() }
            .onEach { response ->
                if (response.status == SUCCESS) {
                    val credentials = response.data?.credentials
                    credentials?.run {
                        // 更新凭证
                        oss?.updateCredentialProvider(
                            OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken)
                        )
                    }
                }
            }
            .catch { e ->
                Log.e(tag, "Error updating STS token: $e")
            }
            .launchIn(viewModelScope)
    }

    private fun uploadAndUpdateFaceUrl(
        oss: OSSClient,
        uploadObjectKey: String,
        byteArray: ByteArray,
        queryPrefix: String,
        callback: () -> Unit
    ) {
        aliYunOssClient.queryObjects(oss, queryPrefix) { objectKeys ->
            if (objectKeys.isEmpty()) {
                uploadFileToOss(oss, uploadObjectKey, byteArray, callback)
            } else {
                aliYunOssClient.deleteObjects(oss, objectKeys) {
                    uploadFileToOss(oss, uploadObjectKey, byteArray, callback)
                }
            }
        }
    }

    private fun uploadFileToOss(
        oss: OSSClient,
        uploadObjectKey: String,
        byteArray: ByteArray,
        callback: () -> Unit
    ) {
        aliYunOssClient.uploadFile(oss, uploadObjectKey, byteArray, object : UploadCallback {
            override fun onStart() {
                _dialogType.value = DialogType.UPLOADING
            }

            override fun onSuccess(objectUrl: String) {
                Log.d("Upload", "File uploaded successfully: $objectUrl")
                _dialogType.value = DialogType.SETTING
                setFaceUrl(
                    objectUrl,
                    onSuccess = {
                        cancelDialog()
                        callback()
                    },
                    onError = {
                        callback()
                        Toaster.show("设置失败，请稍后重试")
                    }
                )
            }

            override fun onProgress(currentSize: Long, totalSize: Long) {
                _uploadProgress.value = currentSize.toFloat() / totalSize
            }

            override fun onFailure(exception: Exception) {
                callback()
                Toaster.show("上传失败，请稍后重试")
            }
        })
    }

    private fun setFaceUrl(
        faceUrl: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            delay(500)
            val info = V2TIMUserFullInfo()
            info.faceUrl = faceUrl

            V2TIMManager.getInstance().setSelfInfo(info, object : V2TIMCallback {
                override fun onSuccess() {
                    onSuccess()
                }

                override fun onError(code: Int, desc: String?) {
                    Log.i(TIM_TAG, "设置个人头像 error, code: $code, desc: $desc")
                    onError()
                }
            })
        }
    }

    internal fun showDialog(dialogType: DialogType) {
        _dialogType.value = dialogType
    }

    private fun cancelDialog() {
        _dialogType.value = DialogType.NONE
    }

    private fun shouldUpdateStsToken(expiration: String): Boolean {
        val expirationInstant = Instant.parse(expiration)
        val expirationTimeInSeconds = expirationInstant.epochSecond
        return DateUtil.getFixedSkewedTimeMillis() / 1000 > expirationTimeInSeconds - 5 * 60
    }

    // 将ImageBitmap转换为byte[]数组
    private fun imageBitmapToByteArray(
        imageBitmap: ImageBitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 100
    ): ByteArray {
        // 将ImageBitmap转为Bitmap
        val bitmap = imageBitmap.asAndroidBitmap()
        val stream = ByteArrayOutputStream()
        // 将Bitmap压缩为byte[]数组
        bitmap.compress(format, quality, stream)
        return stream.toByteArray()
    }

}