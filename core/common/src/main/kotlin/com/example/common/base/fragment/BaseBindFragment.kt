package com.example.common.base.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.common.util.ReflectionUtil

abstract class BaseBindFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = checkNotNull(_binding) {
            "ViewBinding is null. Ensure that you're accessing binding only between onViewCreated() and onDestroyView(). " +
                    "If you're accessing it in onDestroyView(), ensure it is before super.onDestroyView() is called."
        }

    private var currentToast: Toast? = null
    protected lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivityResultLauncher()
    }

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
        initViews(savedInstanceState)
        initData()
        initListeners()
    }

    override fun onDestroyView() {
        _binding = null
        currentToast?.cancel()
        currentToast = null
        super.onDestroyView()
    }

    private fun initActivityResultLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleActivityResult(result)
        }
    }

    // 初始化视图
    protected abstract fun initViews(savedInstanceState: Bundle?)

    // 初始化数据
    protected open fun initData() {}

    // 初始化监听器
    protected open fun initListeners() {}

    // 处理 ActivityResult 回调
    protected open fun handleActivityResult(result: ActivityResult) {}

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

    protected inline fun <reified T : Activity> startActivityForResult(extras: Bundle? = null) {
        val intent = Intent(requireActivity(), T::class.java).apply {
            extras?.let { putExtras(it) }
        }
        activityResultLauncher.launch(intent)
    }

    fun <T : View?> findViewById(@IdRes id: Int): T {
        return requireView().findViewById(id)
    }

}
