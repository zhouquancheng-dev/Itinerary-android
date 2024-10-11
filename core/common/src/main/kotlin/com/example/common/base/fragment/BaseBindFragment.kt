package com.example.common.base.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.common.util.ReflectionUtil

open class BaseBindFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    private val binding get() = _binding!!

    private var currentToast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 使用反射创建 ViewBinding 实例
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
        // 清理 ViewBinding 防止内存泄漏
        _binding = null
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
            activity?.runOnUiThread {
                showToastInternal(message, duration)
            }
        }
    }

    private fun showToastInternal(message: String, duration: Int) {
        currentToast?.cancel()
        currentToast = Toast.makeText(requireActivity(), message, duration)
        currentToast?.show()
    }

    fun navigateTo(cls: Class<*>, bundle: Bundle? = null, flags: Int? = null) {
        val intent = Intent(requireActivity(), cls).apply {
            bundle?.let { putExtras(it) }
            flags?.let { this.flags = it }
        }
        startActivity(intent)
    }

    fun <T : View?> findViewById(@IdRes id: Int): T {
        return requireView().findViewById(id)
    }
}
