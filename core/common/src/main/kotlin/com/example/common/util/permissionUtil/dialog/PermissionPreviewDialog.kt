package com.example.common.util.permissionUtil.dialog

import android.app.Dialog
import android.app.UiModeManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.common.R
import com.example.common.util.ext.getColorCompat
import com.example.common.util.permissionUtil.data.PermissionDetails

class PermissionPreviewDialog(
    context: Context,
    private val paris: PermissionDetails
) : Dialog(context, R.style.StyleBaseDialog) {

    private var cancelListener: View.OnClickListener? = null
    private var confirmListener: View.OnClickListener? = null
    private val isAutoDismiss = true

    fun setCancelListener(cancelListener: View.OnClickListener) {
        this.cancelListener = cancelListener
    }

    fun setConfirmListener(confirmListener: View.OnClickListener) {
        this.confirmListener = confirmListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_permission)

        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if (uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES) {
            setDarkTheme()
        }
        initView()
    }

    private fun setDarkTheme() {
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvTop = findViewById<TextView>(R.id.tvTop)
        findViewById<View>(R.id.dialogRoot).setBackgroundResource(R.drawable.dialog_bg_dark)
        tvTitle.setTextColor(context.getColorCompat(R.color.dialog_bg_color))
        tvTop.setTextColor(context.getColorCompat(R.color.dialog_bg_color))
    }

    private fun initView() {
        val tvContent = findViewById<TextView>(R.id.tvContent)
        val tvCancel = findViewById<TextView>(R.id.tvCancel)
        val tvConfirm = findViewById<TextView>(R.id.tvConfirm)
        val ivIcon = findViewById<ImageView>(R.id.ivIcon)
        findViewById<View>(R.id.ivClose).setOnClickListener { dismiss() }

        tvContent.text = paris.description
        ivIcon.setImageResource(paris.icon)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = paris.name

        tvCancel.setOnClickListener { v ->
            if (isAutoDismiss) {
                dismiss()
            }
            cancelListener?.onClick(v)
        }

        tvConfirm.setOnClickListener { v ->
            if (isAutoDismiss) {
                dismiss()
            }
            confirmListener?.onClick(v)
        }
    }
}
