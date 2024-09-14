package com.example.profile.activity

import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.profile.state.DialogType
import com.example.profile.ui.CropImage
import com.example.profile.vm.UploadImageViewModel
import com.example.ui.theme.JetItineraryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CropImageActivity : FragmentActivity() {

    private val imagePath by lazy { intent.getStringExtra("compressPath") ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 设置状态栏字体颜色为白色
            window.insetsController?.setSystemBarsAppearance(
                0, // 清除 LIGHT_STATUS_BARS 标志
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            // Android 11 以下的设备，使用 setSystemUiVisibility
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = 0
        }

        setContent {
            JetItineraryTheme {
                val uploadVm = hiltViewModel<UploadImageViewModel>()

                CropImage(
                    uploadVm = uploadVm,
                    imagePath = imagePath,
                    onBack = { finish() },
                    onCropStart = {
                        uploadVm.showDialog(DialogType.CROP)
                    }
                ) { imageBitmap ->
                    uploadVm.uploadImage(imageBitmap) {
                        finish()
                    }
                }
            }
        }
    }

}