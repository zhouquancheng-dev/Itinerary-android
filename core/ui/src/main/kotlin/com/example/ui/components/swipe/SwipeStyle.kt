package com.example.ui.components.swipe


/**
 * 滑动时的手势行为
 * [StartToEnd] 从 左至右 滑动手势 -->
 * [EndToStart] 从 右至左 滑动手势 <--
 */
enum class SwipeStyle {
    StartToEnd,
    EndToStart
}

/**
 * 滑动组件的状态
 * [Start]: 滑动的起始状态
 * [End]: 滑动的结束状态
 */
enum class DragValue {
    Start,
    End
}
