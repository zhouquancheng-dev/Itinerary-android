package com.example.common.base.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.example.common.util.ReflectionUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class BaseBindFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding ?: throw IllegalStateException("ViewBinding is not initialized")

    private var currentToast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ReflectionUtil.newViewBinding(layoutInflater, this.javaClass)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        initData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        currentToast = null
    }

    // 初始化视图
    protected open fun initViews() {}

    // 初始化数据
    protected open fun initData() {}

    // 初始化监听器
    protected open fun initListeners() {}

    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showToastInternal(message, duration)
        } else {
            requireActivity().runOnUiThread {
                showToastInternal(message, duration)
            }
        }
    }

    private fun showToastInternal(message: String, duration: Int) {
        currentToast?.cancel()
        currentToast = Toast.makeText(requireActivity(), message, duration)
        currentToast?.show()
    }

    inline fun <reified T : Activity> navigateTo(bundle: Bundle? = null, flags: Int? = null) {
        val intent = Intent(requireActivity(), T::class.java).apply {
            bundle?.let { putExtras(it) }
            flags?.let { this.flags = it }
        }
        startActivity(intent)
    }

    fun <T : View?> findViewById(@IdRes id: Int): T {
        return requireView().findViewById(id)
    }

    protected fun <T> collectFlow(
        flow: Flow<T>,
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
        collector: suspend (T) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(minActiveState) {
                flow.collect(collector)
            }
        }
    }
}
