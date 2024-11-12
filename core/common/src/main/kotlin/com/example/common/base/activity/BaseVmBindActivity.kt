package com.example.common.base.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.example.common.util.ReflectionUtil
import java.lang.reflect.ParameterizedType

abstract class BaseVmBindActivity<VB : ViewBinding, VM : ViewModel> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding ?: throw IllegalStateException("ViewBinding is not initialized")

    protected lateinit var viewModel: VM

    private var currentToast: Toast? = null
    protected lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ReflectionUtil.newViewBinding(layoutInflater, javaClass)
        enableEdgeToEdge()
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[getViewModelClass()]
        initActivityResultLauncher()
        initViews(savedInstanceState)
        initData()
        initListeners()
        setupObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        currentToast = null
    }

    @Suppress("UNCHECKED_CAST")
    protected open fun getViewModelClass(): Class<VM> {
        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]
        return type as Class<VM>
    }

    // 初始化视图
    protected open fun initViews(savedInstanceState: Bundle?) {}

    // 初始化数据
    protected open fun initData() {}

    // 初始化监听器
    protected open fun initListeners() {}

    // 观察 ViewModel 的变化
    protected open fun setupObservers() {}

    // 处理 ActivityResult 回调
    protected open fun handleActivityResult(result: ActivityResult) {}

    private fun initActivityResultLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleActivityResult(result)
        }
    }

    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showToastInternal(message, duration)
        } else {
            runOnUiThread {
                showToastInternal(message, duration)
            }
        }
    }

    private fun showToastInternal(message: String, duration: Int) {
        currentToast?.cancel()
        currentToast = Toast.makeText(this, message, duration)
        currentToast?.show()
    }

    inline fun <reified T : Activity> navigateTo(bundle: Bundle? = null, flags: Int? = null) {
        val intent = Intent(this, T::class.java).apply {
            bundle?.let { putExtras(it) }
            flags?.let { this.flags = it }
        }
        startActivity(intent)
    }

    protected inline fun <reified T : Activity> startActivityForResult(
        extras: Bundle? = null,
        crossinline onResult: (ActivityResult) -> Unit = {}
    ) {
        val intent = Intent(this, T::class.java).apply {
            extras?.let { putExtras(it) }
        }
        activityResultLauncher.launch(intent)
    }

}
