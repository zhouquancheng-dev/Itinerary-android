package com.example.profile.vm

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.example.common.data.Constants.TIM_TAG
import com.example.common.data.LoginState
import com.example.common.di.AppDispatchers.IO
import com.example.common.di.Dispatcher
import com.example.common.flowbus.FlowBus
import com.example.common.listener.sdk.SelfInfoUpdated
import com.example.common.vm.BaseViewModel
import com.hjq.toast.Toaster
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMUserFullInfo
import com.tencent.imsdk.v2.V2TIMValueCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @Dispatcher(IO) ioDispatcher: CoroutineDispatcher
) : BaseViewModel(ioDispatcher) {

    private val _profile = MutableStateFlow<List<V2TIMUserFullInfo>>(emptyList())
    val profile = _profile.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun getUserInfo(owner: LifecycleOwner) {
        val loginUserId = V2TIMManager.getInstance().loginUser
        val userIds: MutableList<String> = mutableListOf()
        userIds.add(loginUserId)

        V2TIMManager.getInstance().getUsersInfo(
            userIds, object : V2TIMValueCallback<List<V2TIMUserFullInfo>> {
                override fun onSuccess(profiles: List<V2TIMUserFullInfo>?) {
                    _profile.value = profiles ?: emptyList()
                }

                override fun onError(code: Int, desc: String?) {
                    Log.i(TIM_TAG, "获取个人资料 error, code: $code, desc: $desc")
                }
            }
        )
        FlowBus.subscribe<SelfInfoUpdated>(owner = owner, dispatcher = ioDispatcher) {
            _profile.value = listOf(it.info)
        }
    }

    fun setSelfInfo(
        faceUrl: String? = null,
        nickname: String? = null,
        selfSignature: String? = null,
        gender: Int? = null,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            delay(500)

            val info = V2TIMUserFullInfo()
            faceUrl?.let {
                info.faceUrl = it
            }
            nickname?.let {
                info.setNickname(it)
            }
            selfSignature?.let {
                info.selfSignature = it
            }
            gender?.let {
                info.gender = it
            }

            V2TIMManager.getInstance().setSelfInfo(info, object : V2TIMCallback {
                override fun onSuccess() {
                    _loading.value = false
                    callback()
                }

                override fun onError(code: Int, desc: String?) {
                    Log.i(TIM_TAG, "设置个人资料 error, code: $code, desc: $desc")
                    _loading.value = false
                    Toaster.show("设置失败，请稍后重试")
                }
            })
        }
    }

    fun logout(action: () -> Unit) {
        V2TIMManager.getInstance().logout(object : V2TIMCallback {
            override fun onSuccess() {
                LoginState.isLoggedIn = false
                Log.i(TIM_TAG, "IM登出成功")
                V2TIMManager.getInstance().unInitSDK()
                Toaster.show("退出登录成功")
                action()
            }

            override fun onError(code: Int, desc: String?) {
                LogUtils.iTag(TIM_TAG, "IM登出失败 code: $code, desc: $desc")
            }
        })
    }


}