package com.example.profile.state

import androidx.annotation.StringRes
import com.example.profile.R

internal enum class DialogType(@StringRes val dialogText: Int) {
    NONE(R.string.empty_text),
    CROP(R.string.crop_dialog_title),
    UPLOADING(R.string.upload_profile_image_dialog_title),
    SETTING(R.string.setting_dialog_title)
}