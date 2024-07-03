package com.example.splash.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.example.common.BaseApplication
import com.example.common.data.Constants.TIM_TAG
import com.example.common.data.DatastoreKey.IS_FIRST_TIME_LAUNCH
import com.example.common.data.DatastoreKey.IS_LOGIN_STATUS
import com.example.common.data.DatastoreKey.IS_PRIVACY_AGREE
import com.example.common.data.DatastoreKey.TIM_USER_ID
import com.example.common.data.DatastoreKey.TIM_USER_SIG
import com.example.common.util.DataStoreUtils.getBooleanFlow
import com.example.common.util.DataStoreUtils.getStringSync
import com.example.common.util.DataStoreUtils.putBoolean
import com.hjq.toast.Toaster
import com.tencent.imsdk.BaseConstants.ERR_SVR_ACCOUNT_USERSIG_EXPIRED
import com.tencent.imsdk.BaseConstants.ERR_USER_SIG_EXPIRED
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val bApplication by lazy { BaseApplication.getInstance() }

    private val _eventChannel = Channel<Event>()
    val eventFlow = _eventChannel.receiveAsFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    fun initPrivacyState() {
        viewModelScope.launch {
            if (!isPrivacyAgree()) {
                _showDialog.value = true
            } else {
                navigateToActivity()
            }
        }
    }

    private suspend fun isFirstTimeLaunch() = getBooleanFlow(IS_FIRST_TIME_LAUNCH, true).first()

    private suspend fun isPrivacyAgree() = getBooleanFlow(IS_PRIVACY_AGREE).first()

    private suspend fun isLoginStatus() = getBooleanFlow(IS_LOGIN_STATUS).first()

    fun acceptPrivacy() {
        viewModelScope.launch {
            putBoolean(IS_PRIVACY_AGREE, true)
            _showDialog.value = false
            navigateToActivity()
        }
    }

    fun rejectPrivacy() {
        viewModelScope.launch {
            _showDialog.value = false
            _eventChannel.send(Event.FinishAc)
        }
    }

    private fun navigateToActivity() {
        viewModelScope.launch(Dispatchers.Main) {
            when {
                isFirstTimeLaunch() -> {
                    bApplication.initPrivacyRequiredSDKs()
                    _eventChannel.send(Event.StartWelcome)
                }
                isLoginStatus() -> {
                    val userId = getStringSync(TIM_USER_ID)
                    val userSig = getStringSync(TIM_USER_SIG)
                    V2TIMManager.getInstance().login(userId, userSig, object : V2TIMCallback {
                        override fun onSuccess() {
                            LogUtils.iTag(TIM_TAG, "IM登录成功")

                            viewModelScope.launch {
                                _eventChannel.send(Event.StartMain)
                            }
                        }

                        override fun onError(code: Int, desc: String?) {
                            LogUtils.iTag(TIM_TAG, "IM登录失败 code: $code, desc: $desc")
                            viewModelScope.launch {
                                if (code == ERR_USER_SIG_EXPIRED || code == ERR_SVR_ACCOUNT_USERSIG_EXPIRED) {
                                    Toaster.show("登录已过期请重新登录")
                                }
                                _eventChannel.send(Event.StartLogin)
                            }
                        }
                    })
                }
                else -> _eventChannel.send(Event.StartLogin)
            }
        }
    }

}
