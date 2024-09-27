package com.example.im.vm

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.example.common.data.Constants.TIM_TAG
import com.example.common.di.AppDispatchers.IO
import com.example.common.di.Dispatcher
import com.example.common.flowbus.FlowBus
import com.example.common.listener.im.V2TIMListener
import com.example.common.listener.im.conversation.ConversationChangedEvent
import com.hjq.toast.Toaster
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMConversation
import com.tencent.imsdk.v2.V2TIMConversationOperationResult
import com.tencent.imsdk.v2.V2TIMConversationResult
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMValueCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class IMViewModel @Inject constructor(
    v2TimListener: V2TIMListener,
    @Dispatcher(IO) ioDispatcher: CoroutineDispatcher
) : TIMBaseViewModel(v2TimListener, ioDispatcher) {

    private val _conversations = MutableStateFlow<List<V2TIMConversation>>(emptyList())
    val conversations = _conversations
        .asStateFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var nextSeq: Long = 0
    private var isFinished: Boolean = false

    fun getConversations(owner: LifecycleOwner) {
        loadConversations()
        FlowBus.subscribe<ConversationChangedEvent>(owner, dispatcher = ioDispatcher) { event ->
            updateConversations(event.conversations)
        }
    }

    private fun loadConversations(nextSeq: Long = 0, count: Int = 20) {
        if (isFinished) return
        _isLoading.value = true

        V2TIMManager.getConversationManager()
            .getConversationList(nextSeq, count, object : V2TIMValueCallback<V2TIMConversationResult> {
                override fun onSuccess(v2TResult: V2TIMConversationResult?) {
                    val v2TIMConversationList = v2TResult?.conversationList.orEmpty()
                    _conversations.value = v2TIMConversationList

                    this@IMViewModel.nextSeq = v2TResult?.nextSeq ?: 0
                    this@IMViewModel.isFinished = v2TResult?.isFinished ?: true

                    _isLoading.value = false
                }

                override fun onError(code: Int, desc: String?) {
                    Log.i(TIM_TAG, "failure, code: $code, desc: $desc")
                    _isLoading.value = false
                }
            })
    }

    fun loadMoreConversations() {
        LogUtils.d("会话列表调用加载更多")
        loadConversations(nextSeq, 20)
    }

    private fun updateConversations(newConversations: List<V2TIMConversation>) {
        launch {
            val currentConversations = _conversations.replayCache.firstOrNull().orEmpty()
            val conversationMap = currentConversations.associateBy { it.conversationID }.toMutableMap()
            newConversations.forEach { newConversation ->
                conversationMap[newConversation.conversationID] = newConversation
            }
            val sortedConversations = conversationMap.values.sortedByDescending { it.orderKey }
            _conversations.value = sortedConversations
        }
    }

    private val _isPullRefreshing = MutableStateFlow(false)
    val isPullRefreshing = _isPullRefreshing.asStateFlow()

    fun refreshConversations() {
        launch {
            _isPullRefreshing.value = true
            delay(500)
            V2TIMManager.getConversationManager()
                .getConversationList(0, 100, object : V2TIMValueCallback<V2TIMConversationResult> {
                    override fun onSuccess(v2TResult: V2TIMConversationResult?) {
                        val v2TIMConversationList = v2TResult?.conversationList.orEmpty()
                        _conversations.value = v2TIMConversationList
                        _isPullRefreshing.value = false
                        Toaster.showShort("更新成功")
                    }

                    override fun onError(code: Int, desc: String?) {
                        Log.i(TIM_TAG, "failure, code: $code, desc: $desc")
                        _isPullRefreshing.value = false
                        Toaster.showShort("更新失败")
                    }
                })
        }
    }

    fun pinConversation(conversationID: String, isPinned: Boolean) {
        V2TIMManager.getConversationManager().pinConversation(conversationID, isPinned, object : V2TIMCallback {
            override fun onSuccess() {

            }

            override fun onError(code: Int, desc: String?) {
                Log.i(TIM_TAG, "pinConversation failure, code:$code, desc:$desc")
            }
        })
    }

    fun deleteConversation(conversationIds: List<String>, clearMessage: Boolean = false) {
        V2TIMManager.getConversationManager().deleteConversationList(conversationIds, clearMessage,
            object : V2TIMValueCallback<MutableList<V2TIMConversationOperationResult>> {
                override fun onSuccess(results: MutableList<V2TIMConversationOperationResult>?) {
                    val currentConversations = _conversations.replayCache.firstOrNull().orEmpty()
                    val updatedConversations = currentConversations.filterNot { conversation ->
                        results?.any { it.conversationID == conversation.conversationID } == true
                    }
                    _conversations.value = updatedConversations
                }

                override fun onError(code: Int, desc: String?) {
                    Log.i(TIM_TAG, "deleteConversation failure, code: $code, desc: $desc")
                }

            })
    }

    fun cleanConversationUnreadMessageCount(conversationID: String) {
        V2TIMManager.getConversationManager()
            .cleanConversationUnreadMessageCount(conversationID, 0, 0, object : V2TIMCallback {
                override fun onSuccess() {
                    Log.i(TIM_TAG, "cleanConversationUnreadMessageCount success")
                }

                override fun onError(code: Int, desc: String?) {
                    Log.i(TIM_TAG, "cleanConversationUnreadMessageCount failure, code: $code, desc: $desc")
                }
            })
    }

}