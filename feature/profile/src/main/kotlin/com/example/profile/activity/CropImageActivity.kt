package com.example.profile.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.util.ext.addSystemBarsColorUpdate
import com.example.profile.state.DialogType
import com.example.profile.ui.CropImage
import com.example.profile.vm.UploadImageViewModel
import com.example.ui.R
import com.example.ui.theme.JetItineraryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CropImageActivity : FragmentActivity() {

    private val imagePath by lazy { intent.getStringExtra("compressPath") ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        addSystemBarsColorUpdate(R.color.black, false)
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