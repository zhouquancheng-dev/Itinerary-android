package com.example.common.base.activity

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.common.BaseApplication
import com.example.common.util.ReflectionUtil

open class BaseBindActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    private val binding get() = _binding!!

    private var currentToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 调用 inflate 方法，创建 ViewBinding
        _binding = ReflectionUtil.newViewBinding(layoutInflater, javaClass)
        setContentView(binding.root)
        initViews()
        initListeners()
        initData()
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
            runOnUiThread {
                showToastInternal(message, duration)
            }
        }
    }

    private fun showToastInternal(message: String, duration: Int) {
        currentToast?.cancel()
        currentToast = Toast.makeText(BaseApplication.getApplication(), message, duration)
        currentToast?.show()
    }

    fun navigateTo(cls: Class<*>, bundle: Bundle? = null, flags: Int? = null) {
        val intent = Intent(this, cls).apply {
            bundle?.let { putExtras(it) }
            flags?.let { this.flags = it }
        }
        startActivity(intent)
    }

}
