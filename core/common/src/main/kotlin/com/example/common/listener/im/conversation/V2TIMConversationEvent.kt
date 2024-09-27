package com.example.common.listener.im.conversation

import com.tencent.imsdk.v2.V2TIMConversation
import com.tencent.imsdk.v2.V2TIMConversationListFilter

// 同步服务器会话开始
data class SyncServerStartEvent(val message: String)

// 同步服务器会话完成
data class SyncServerFinishEvent(val message: String)

// 同步服务器会话失败
data class SyncServerFailedEvent(val error: String)

// 有会话新增（这个新增指的是会话列表有新增，比如一个新好友发来消息，不是某个会话内容新增）
data class NewConversationEvent(val conversations: List<V2TIMConversation>)

// 有会话更新，例如未读计数发生变化、最后一条消息被更新等，此时可以重新对会话列表做排序
data class ConversationChangedEvent(val conversations: List<V2TIMConversation>)

// 有会话被删除
data class ConversationDeletedEvent(val conversationIDs: List<String?>)

// 会话未读总数变更通知
data class TotalUnreadMessageCountChangedEvent(val totalUnreadCount: Long)

// 过滤条件下未读总数变更通知
data class UnreadMessageCountChangedByFilterEvent(
    val filter: V2TIMConversationListFilter,
    val totalUnreadCount: Long
)