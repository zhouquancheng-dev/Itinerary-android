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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.example.common.util.ReflectionUtil
import java.lang.reflect.ParameterizedType

abstract class BaseVmBindActivity<VB : ViewBinding, VM : ViewModel> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = checkNotNull(_binding) {
            "ViewBinding is null. Ensure that you're accessing binding only between onCreate() and onDestroy(). " +
                    "If you're accessing it in onDestroy(), ensure it is before super.onDestroy() is called."
        }

    protected lateinit var viewModel: VM

    private var currentToast: Toast? = null
    protected open val needSystemBarsPadding: Boolean = true
    protected open val shouldClearBinding: Boolean = true
    protected lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        enableEdgeToEdge()
        setContentView(binding.root)
        setupSystemBarsPadding()
        initActivityResultLauncher()
        viewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[getViewModelClass()]
        initViews(savedInstanceState)
        initData()
        initListeners()
        setupObservers()
    }

    override fun onDestroy() {
        if (shouldClearBinding) {
            _binding = null
        }
        currentToast?.cancel()
        currentToast = null
        super.onDestroy()
    }

    private fun initBinding() {
        if (_binding == null) {
            _binding = ReflectionUtil.newViewBinding(layoutInflater, javaClass)
        }
    }

    private fun setupSystemBarsPadding() {
        if (needSystemBarsPadding) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(rootLayoutId)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }

    private fun initActivityResultLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleActivityResult(result)
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected open fun getViewModelClass(): Class<VM> {
        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]
        return type as Class<VM>
    }

    protected abstract val rootLayoutId: Int

    // 初始化视图
    protected abstract fun initViews(savedInstanceState: Bundle?)

    // 初始化数据
    protected open fun initData() {}

    // 初始化监听器
    protected open fun initListeners() {}

    // 观察 ViewModel 的变化
    protected open fun setupObservers() {}

    // 处理 ActivityResult 回调
    protected open fun handleActivityResult(result: ActivityResult) {}

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

    protected inline fun <reified T : Activity> startActivityForResult(extras: Bundle? = null) {
        val intent = Intent(this, T::class.java).apply {
            extras?.let { putExtras(it) }
        }
        activityResultLauncher.launch(intent)
    }

    /**
     * 手动清除 binding
     */
    protected open fun clearBinding() {
        _binding = null
    }

}
