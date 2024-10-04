package com.example.common.util.permissionUtil.ext

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

object Constant {

    const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
    const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

    @RequiresApi(Build.VERSION_CODES.R)
    const val MANAGE_EXTERNAL_STORAGE = Manifest.permission.MANAGE_EXTERNAL_STORAGE

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    const val READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    const val READ_MEDIA_VIDEO = Manifest.permission.READ_MEDIA_VIDEO
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    const val READ_MEDIA_AUDIO = Manifest.permission.READ_MEDIA_AUDIO
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    const val READ_MEDIA_VISUAL_USER_SELECTED = Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED

    const val CAMERA = Manifest.permission.CAMERA
    const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
    const val READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE
    const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    const val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    const val READ_CALENDAR = Manifest.permission.READ_CALENDAR
    const val BODY_SENSORS = Manifest.permission.BODY_SENSORS

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    const val PERMISSION_BODY_SENSORS_BACKGROUND = Manifest.permission.BODY_SENSORS_BACKGROUND

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    const val POST_NOTIFICATIONS = Manifest.permission.POST_NOTIFICATIONS

}