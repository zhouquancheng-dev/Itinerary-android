package com.example.im.vm

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.example.common.data.Constants.TIM_TAG
import com.example.common.data.DatastoreKey.TIM_USER_ID
import com.example.common.data.DatastoreKey.TIM_USER_SIG
import com.example.common.data.LoginState
import com.example.common.di.AppDispatchers.IO
import com.example.common.di.Dispatcher
import com.example.common.flowbus.FlowBus
import com.example.common.util.DataStoreUtils.getStringSync
import com.example.common.base.vm.BaseViewModel
import com.example.common.listener.im.V2TIMListener
import com.example.common.listener.im.ListenerManager
import com.example.common.listener.im.conversation.TotalUnreadMessageCountChangedEvent
import com.example.common.listener.im.sdk.KickedOffline
import com.example.common.listener.im.sdk.UserSigExpired
import com.example.ui.utils.ToasterUtil.ToastStatus.WARN
import com.example.ui.utils.ToasterUtil.showCustomToaster
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMManager.V2TIM_STATUS_LOGINED
import com.tencent.imsdk.v2.V2TIMManager.V2TIM_STATUS_LOGOUT
import com.tencent.imsdk.v2.V2TIMValueCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class TIMBaseViewModel @Inject constructor(
    private val v2TimListener: V2TIMListener,
    @Dispatcher(IO) ioDispatcher: CoroutineDispatcher
) : BaseViewModel(ioDispatcher) {

    private fun initListener() {
        ListenerManager.registerListeners(v2TimListener)
    }

    fun unregisterListener() {
        ListenerManager.unregisterListeners(v2TimListener)
    }

    fun observeIMLoginState(owner: LifecycleOwner) {
        viewModelScope.launch {
            val loginStatus = V2TIMManager.getInstance().loginStatus
            when (loginStatus) {
                // 已登录
                V2TIM_STATUS_LOGINED -> {
                    initListener()
                    getTotalUnreadCount(owner)
                }
                // 未登录
                V2TIM_STATUS_LOGOUT -> {
                    val userId = getStringSync(TIM_USER_ID)
                    val userSig = getStringSync(TIM_USER_SIG)
                    V2TIMManager.getInstance().login(userId, userSig, object : V2TIMCallback {
                        override fun onSuccess() {
                            Log.i(TIM_TAG, "IM登录成功")
                            initListener()
                            getTotalUnreadCount(owner)
                        }

                        override fun onError(code: Int, desc: String?) {
                            LogUtils.iTag(TIM_TAG, "IM登录失败 code: $code, desc: $desc")
                        }
                    })
                }
            }
        }
    }

    fun multiTerminalLoginState(owner: LifecycleOwner, action: () -> Unit) {
        val handleLogout: (String) -> Unit = { message ->
            LoginState.isLoggedIn = false
            showCustomToaster(message, WARN)
            action()
        }

        FlowBus.subscribe<KickedOffline>(owner, dispatcher = ioDispatcher) {
            handleLogout("您的账号已在其他设备登录")
        }

        FlowBus.subscribe<UserSigExpired>(owner, dispatcher = ioDispatcher) {
            handleLogout("登录过期，请重新登录")
        }
    }

    private val _totalUnreadCount = MutableStateFlow(0L)
    val totalUnreadCount = _totalUnreadCount
        .asStateFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    private fun getTotalUnreadCount(owner: LifecycleOwner) {
        V2TIMManager.getConversationManager()
            .getTotalUnreadMessageCount(object : V2TIMValueCallback<Long?> {
                override fun onSuccess(aLong: Long?) {
                    if (aLong != null) {
                        _totalUnreadCount.value = aLong
                    }
                }

                override fun onError(code: Int, desc: String) {
                    Log.i(TIM_TAG, "Error, code:$code, desc:$desc")
                    _totalUnreadCount.value = 0
                }
            })

        FlowBus.subscribe<TotalUnreadMessageCountChangedEvent>(owner, dispatcher = ioDispatcher) { event ->
            _totalUnreadCount.value = event.totalUnreadCount
        }
    }

}