package com.example.ui.lce

import androidx.compose.runtime.Composable

/**
 * 根据 State 的页面封装
 * @param uiState 数据状态
 * @param onRetryClick 重试请求
 * @param loadingContent 正在加载
 * @param errorContent 失败
 * @param noContent 空页
 * @param content 显示数据加载成功的 @Composable
 */
@Composable
fun <T> LcePage(
    uiState: UiState<T>,
    onRetryClick: () -> Unit = {},
    loadingContent: @Composable (() -> Unit) = { LoadingContent() },
    errorContent: @Composable ((Throwable?) -> Unit) = { ErrorContent(onErrorClick = onRetryClick) },
    noContent: @Composable ((String) -> Unit) = { EmptyContent() },
    content: @Composable (result: T) -> Unit
) {
    when (uiState) {
        is Loading -> loadingContent()
        is Error -> errorContent(uiState.error)
        is NoContent -> noContent(uiState.reason)
        is Success -> content(uiState.data)
    }
}
