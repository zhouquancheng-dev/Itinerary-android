package com.example.ui.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment

abstract class AbstractComposeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        registerBackPressHandler()
        return createComposeView()
    }

    private fun createComposeView(): ComposeView {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ProvideDefaultContent()
            }
        }
    }

    private fun registerBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!handleComposeBackPress()) {
                    // 如果Compose中没有处理返回事件，执行默认行为
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    /**
     * 重写方法在Compose中实现返回键处理
     */
    protected open fun handleComposeBackPress(): Boolean {
        return false
    }

    @Composable
    private fun ProvideDefaultContent() {
        ComposeContent()
    }

    @Composable
    protected abstract fun ComposeContent()
}
