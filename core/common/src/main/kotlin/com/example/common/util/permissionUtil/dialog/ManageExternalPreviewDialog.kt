package com.example.common.util.permissionUtil.dialog

import android.app.UiModeManager
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.common.R
import com.example.common.databinding.DialogPermissionPreviewBinding
import com.example.common.util.getColorCompat
import com.example.common.util.permissionUtil.data.PermissionDetails
import com.permissionx.guolindev.dialog.RationaleDialog

class ManageExternalPreviewDialog(
    context: Context,
    private val paris: PermissionDetails
) : RationaleDialog(context, R.style.StyleBaseDialog) {
    private lateinit var binding: DialogPermissionPreviewBinding

    // 是否点击按钮时自动关闭对话框
    private val isAutoDismiss = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogPermissionPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.let {
            val param = it.attributes
            val width = (context.resources.displayMetrics.widthPixels * 0.8).toInt()
            val height = param.height
            it.setLayout(width, height)
        }
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            setDarkTheme()
        }
        initView()
    }

    override fun getPositiveButton(): View {
        return binding.tvConfirm
    }

    override fun getNegativeButton(): View {
        return binding.tvCancel
    }

    override fun getPermissionsToRequest(): MutableList<String> {
        return paris.permissions.toMutableList()
    }

    private fun setDarkTheme() {
        binding.dialogRoot.setBackgroundResource(R.drawable.dialog_bg_dark)
        binding.tvTitle.setTextColor(context.getColorCompat(R.color.dialog_bg_color))
        binding.tvTop.setTextColor(context.getColorCompat(R.color.dialog_bg_color))
    }

    private fun initView() {
        val ivIcon = binding.ivIcon
        binding.ivClose.setOnClickListener { dismiss() }
        binding.tvContent.text = paris.description
        ivIcon.setImageResource(paris.icon)
        val tvTitle = binding.tvTitle
        tvTitle.text = paris.name
        binding.tvCancel.setOnClickListener {
            if (isAutoDismiss) {
                dismiss()
            }
        }
        binding.tvConfirm.setOnClickListener {
            if (isAutoDismiss) {
                dismiss()
            }
        }
    }
}