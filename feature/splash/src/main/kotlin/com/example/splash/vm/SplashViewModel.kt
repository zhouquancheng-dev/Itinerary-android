package com.example.splash.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.example.common.BaseApplication
import com.example.common.data.Constants.TIM_TAG
import com.example.common.data.DatastoreKey.IS_FIRST_TIME_LAUNCH
import com.example.common.data.DatastoreKey.IS_PRIVACY_AGREE
import com.example.common.data.DatastoreKey.TIM_USER_ID
import com.example.common.data.DatastoreKey.TIM_USER_SIG
import com.example.common.data.LoginState
import com.example.common.di.network.NetworkMonitor
import com.example.common.util.DataStoreUtils.getBooleanFlow
import com.example.common.util.DataStoreUtils.getStringSync
import com.example.common.util.DataStoreUtils.putBoolean
import com.hjq.toast.Toaster
import com.tencent.imsdk.BaseConstants.ERR_SVR_ACCOUNT_USERSIG_EXPIRED
import com.tencent.imsdk.BaseConstants.ERR_USER_SIG_EXPIRED
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val bApplication by lazy { BaseApplication.getInstance() }

    private val _eventChannel = Channel<Event>()
    val eventFlow = _eventChannel.receiveAsFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    private val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

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
                // 为首次安装打开应用的情况，欢迎页
                isFirstTimeLaunch() -> {
                    bApplication.initPrivacyRequiredSDKs()
                    _eventChannel.send(Event.StartWelcome)
                }
                // LoginState.isLoggedIn 为 true 这个分支是已经登录了应用账号的状态下，但是每次启动应用还需要登录IM SDK
                LoginState.isLoggedIn -> {
                    if (!isOffline.value) {
                        // 有网络连接再IM登录
                        val userId = getStringSync(TIM_USER_ID)
                        val userSig = getStringSync(TIM_USER_SIG)
                        V2TIMManager.getInstance().login(userId, userSig, object : V2TIMCallback {
                            override fun onSuccess() {
                                Log.i(TIM_TAG, "IM登录成功")

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
                    } else {
                        // 如果启动时没有网络，可以进入应用
                        // 此时有两种情况：
                        // 1.有网络且已经登录成功过IM的，在主页里网络断开再重连时，不需要调用 IM 登录，重新连接后 IM SDK 会自动上线
                        // 2.如果启动应用前就是没有网络的情况，由于此时是直接进入主页的，在进入主页之后需要调用IM登录
                        _eventChannel.send(Event.StartMain)
                    }
                }
                // LoginState.isLoggedIn 为 false 的情况，也就是还没有登录过应用，跳转登录
                else -> _eventChannel.send(Event.StartLogin)
            }
        }
    }

}
