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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.example.common.util.ReflectionUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class BaseBindActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding ?: throw IllegalStateException("ViewBinding is not initialized")

    private var currentToast: Toast? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 调用 inflate 方法，创建 ViewBinding
        _binding = ReflectionUtil.newViewBinding(layoutInflater, javaClass)
        enableEdgeToEdge()
        setContentView(binding.root)
        initActivityResultLauncher()
        initViews(savedInstanceState)
        initListeners()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        currentToast = null
    }

    // 初始化视图
    protected open fun initViews(savedInstanceState: Bundle?) {}

    // 初始化数据
    protected open fun initData() {}

    // 初始化监听器
    protected open fun initListeners() {}

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

    private inline fun <reified T : Activity> startActivityForResult(
        extras: Bundle? = null,
        crossinline onResult: (ActivityResult) -> Unit
    ) {
        val intent = Intent(this, T::class.java).apply {
            extras?.let { putExtras(it) }
        }
        activityResultLauncher.launch(intent)
    }

    protected fun <T> collectFlow(
        flow: Flow<T>,
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
        collector: suspend (T) -> Unit
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(minActiveState) {
                flow.collect(collector)
            }
        }
    }

}
