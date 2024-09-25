package com.example.im.ui.conversation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.util.startAcWithBundle
import com.example.common.util.startActivity
import com.example.im.activity.ContactsActivity
import com.example.im.components.FloatingButton
import com.example.im.ui.Color4285F4
import com.example.im.ui.Color60DDAD
import com.example.im.vm.IMViewModel
import com.example.ui.components.swipe.SwipeRowLayout
import com.tencent.imsdk.v2.V2TIMConversation
import com.tencent.qcloud.tuicore.TUIConstants
import com.tencent.qcloud.tuikit.tuichat.minimalistui.page.TUIC2CChatMinimalistActivity
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationHome(ivm: IMViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val conversations by ivm.conversations.collectAsStateWithLifecycle(emptyList())
    val isLoading by ivm.isLoading.collectAsStateWithLifecycle(false)
    val isPullRefreshing by ivm.isPullRefreshing.collectAsStateWithLifecycle(false)

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        ivm.getConversations(lifecycleOwner)
    }

    val listState = rememberLazyListState()
    val isAtEnd by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            // 列表中的总项目数
            val totalItemsCount = layoutInfo.totalItemsCount
            // 获取当前可见项目中的最后一个项目
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            // 确保列表中有项目
            // 确保存在可见项目
            // 检查最后一个可见项目的索引是否等于总项目数 -1，判断列表是否已经滚动到最后一个项目，即滚动到了列表底部
            totalItemsCount > 0 && lastVisibleItem != null && lastVisibleItem.index == totalItemsCount - 1
        }
    }
    LaunchedEffect(isAtEnd) {
        snapshotFlow { isAtEnd }
            .distinctUntilChanged()
            .collect { atEnd ->
                // 判断列表是否已经滚动到底部
                // 项目数大于可见项数（列表中的项目数量多于当前屏幕上可见的项目数量，这意味着列表已经足够长）
                // 并且当前没有正在加载的数据
                if (atEnd && conversations.size > listState.layoutInfo.visibleItemsInfo.size && !isLoading) {
                    ivm.loadMoreConversations()
                }
            }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingButton {
                startActivity<ContactsActivity>(context)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Row(modifier = Modifier.padding(start = 18.dp, top = 10.dp)) {
                BasicText(
                    text = if (isLoading) "收取中..." else "聊天",
                    style = MaterialTheme.typography.headlineLarge,
                    color = {
                        if (isLoading) Color60DDAD else Color4285F4
                    }
                )
            }
            PullToRefreshBox(
                isRefreshing = isPullRefreshing,
                onRefresh = ivm::refreshConversations
            ) {
                ConversationList(
                    listState = listState,
                    conversations =  conversations,
                    onChatUI = { conversationID, userId ->
                        ivm.cleanConversationUnreadMessageCount(conversationID)
                        val bundleExtra = bundleOf(
                            TUIConstants.TUIChat.CHAT_TYPE to V2TIMConversation.V2TIM_C2C,
                            TUIConstants.TUIChat.CHAT_ID to userId
                        )
                        startAcWithBundle<TUIC2CChatMinimalistActivity>(context, bundleExtra)
                    },
                    onPinned = { conversationID, isPinned ->
                        ivm.pinConversation(conversationID, isPinned)
                    },
                    onDeleteConversation = { conversationID ->
                        ivm.deleteConversation(listOf(conversationID))
                    }
                )
            }
        }
    }
}

@Composable
fun ConversationList(
    listState: LazyListState,
    conversations: List<V2TIMConversation>,
    onChatUI: (String, String) -> Unit,
    onPinned: (String, Boolean) -> Unit,
    onDeleteConversation: (String) -> Unit
) {
    val currentSwipedIndex = remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        itemsIndexed(
            items = conversations,
            key = { _, item ->
                item.conversationID
            },
            contentType = { _, item ->
                item.faceUrl + item.conversationID
            }
        ) { index, item ->
            ConversationItem(
                conversation = item,
                index = index,
                currentSwipedIndex = currentSwipedIndex,
                modifier = Modifier.animateItem(),
                onChatUI, onPinned, onDeleteConversation
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationItem(
    conversation: V2TIMConversation,
    index: Int,
    currentSwipedIndex: MutableState<Int?>,
    modifier: Modifier = Modifier,
    onChatUI: (String, String) -> Unit,
    onPinned: (String, Boolean) -> Unit,
    onDeleteConversation: (String) -> Unit
) {
    SwipeRowLayout(
        index = index,
        currentSwipedIndex = currentSwipedIndex,
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp),
        childContent = { draggableState ->
            ConversationChild(
                isPinned = conversation.isPinned,
                draggableState = draggableState,
                onPinned = { isPinned ->
                    onPinned(conversation.conversationID, isPinned)
                },
                onDeleteConversation = {
                    onDeleteConversation(conversation.conversationID)
                }
            )
        }
    ) { draggableState ->
        ConversationPrimary(
            conversation = conversation,
            draggableState = draggableState,
            currentSwipedIndex = currentSwipedIndex,
            onChatUI = { onChatUI(conversation.conversationID, conversation.userID) }
        )
    }
}
