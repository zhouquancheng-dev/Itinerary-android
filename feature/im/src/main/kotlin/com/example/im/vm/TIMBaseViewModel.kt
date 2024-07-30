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
import com.example.common.vm.BaseViewModel
import com.example.im.listener.V2TIMListener
import com.example.im.listener.ListenerManager
import com.example.im.listener.sdk.KickedOffline
import com.example.im.listener.sdk.UserSigExpired
import com.example.ui.utils.ToasterUtil.ToastStatus.WARN
import com.example.ui.utils.ToasterUtil.showCustomToaster
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMManager.V2TIM_STATUS_LOGINED
import com.tencent.imsdk.v2.V2TIMManager.V2TIM_STATUS_LOGOUT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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

    fun observeIMLoginState(owner: LifecycleOwner, action: () -> Unit) {
        viewModelScope.launch {
            val loginStatus = V2TIMManager.getInstance().loginStatus
            when (loginStatus) {
                // 已登录
                V2TIM_STATUS_LOGINED -> {
                    initListener()
                }
                // 未登录
                V2TIM_STATUS_LOGOUT -> {
                    val userId = getStringSync(TIM_USER_ID)
                    val userSig = getStringSync(TIM_USER_SIG)
                    V2TIMManager.getInstance().login(userId, userSig, object : V2TIMCallback {
                        override fun onSuccess() {
                            Log.i(TIM_TAG, "IM登录成功")
                            initListener()
                        }

                        override fun onError(code: Int, desc: String?) {
                            LogUtils.iTag(TIM_TAG, "IM登录失败 code: $code, desc: $desc")
                        }
                    })
                }
            }

            // 在线时被踢下线
            FlowBus.subscribe<KickedOffline>(owner, dispatcher = ioDispatcher) {
                LoginState.isLoggedIn = false
                showCustomToaster("您的账号已在其他设备登录", WARN)
                action()
            }

            // 在线时票据过期
            FlowBus.subscribe<UserSigExpired>(owner, dispatcher = ioDispatcher) {
                LoginState.isLoggedIn = false
                showCustomToaster("登录过期，请重新登录", WARN)
                action()
            }
        }
    }

}