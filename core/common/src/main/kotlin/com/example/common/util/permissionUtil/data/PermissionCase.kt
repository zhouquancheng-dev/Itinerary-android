package com.example.common.util.permissionUtil.data

import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import com.example.common.BaseApplication
import com.example.common.R
import com.example.common.util.permissionUtil.dialog.PermissionPreviewDialog
import com.example.common.util.permissionUtil.ext.Constant.ACCESS_COARSE_LOCATION
import com.example.common.util.permissionUtil.ext.Constant.ACCESS_FINE_LOCATION
import com.example.common.util.permissionUtil.ext.Constant.BODY_SENSORS
import com.example.common.util.permissionUtil.ext.Constant.CAMERA
import com.example.common.util.permissionUtil.ext.Constant.MANAGE_EXTERNAL_STORAGE
import com.example.common.util.permissionUtil.ext.Constant.PERMISSION_BODY_SENSORS_BACKGROUND
import com.example.common.util.permissionUtil.ext.Constant.POST_NOTIFICATIONS
import com.example.common.util.permissionUtil.ext.Constant.READ_CALENDAR
import com.example.common.util.permissionUtil.ext.Constant.READ_EXTERNAL_STORAGE
import com.example.common.util.permissionUtil.ext.Constant.READ_MEDIA_AUDIO
import com.example.common.util.permissionUtil.ext.Constant.READ_MEDIA_IMAGES
import com.example.common.util.permissionUtil.ext.Constant.READ_MEDIA_VIDEO
import com.example.common.util.permissionUtil.ext.Constant.READ_PHONE_STATE
import com.example.common.util.permissionUtil.ext.Constant.RECORD_AUDIO
import com.example.common.util.permissionUtil.ext.Constant.WRITE_EXTERNAL_STORAGE

object PermissionCase {

    private fun getString(@StringRes resId: Int): String {
        return BaseApplication.getApplication().getString(resId)
    }

    private val pDetails: Map<String, PermissionDetails> = createPermissionDetails()

    private fun createPermissionDetails(): Map<String, PermissionDetails> {
        val permissionsList = mutableListOf(
            PermissionDetails(
                getString(R.string.p_storage_title),
                getString(R.string.p_storage_desc),
                R.drawable.ic_store,
                listOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
            ),
            PermissionDetails(
                getString(R.string.p_camera_title),
                getString(R.string.p_camera_desc),
                R.drawable.ic_camera,
                listOf(CAMERA)
            ),
            PermissionDetails(
                getString(R.string.p_microphone_title),
                getString(R.string.p_microphone_desc),
                R.drawable.ic_microphone,
                listOf(RECORD_AUDIO)
            ),
            PermissionDetails(
                getString(R.string.p_location_title),
                getString(R.string.p_location_desc),
                R.drawable.ic_orientation,
                listOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
            ),
            PermissionDetails(
                getString(R.string.p_phone_title),
                getString(R.string.p_phone_desc),
                R.drawable.ic_telephone,
                listOf(READ_PHONE_STATE)
            ),
            PermissionDetails(
                getString(R.string.p_calendar_title),
                getString(R.string.p_calendar_desc),
                R.drawable.ic_calendar,
                listOf(READ_CALENDAR)
            ),
            PermissionDetails(
                getString(R.string.p_sensors_title),
                getString(R.string.p_sensors_desc),
                R.drawable.ic_sensor,
                listOf(BODY_SENSORS)
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissionsList.add(
                PermissionDetails(
                    getString(R.string.p_storage_title),
                    getString(R.string.p_storage_desc),
                    R.drawable.ic_store,
                    listOf(MANAGE_EXTERNAL_STORAGE)
                )
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsList.add(
                PermissionDetails(
                    getString(R.string.p_photoalbum_title),
                    getString(R.string.p_photoalbum_desc),
                    R.drawable.ic_photoalbum,
                    listOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_AUDIO)
                )
            )
            permissionsList.add(
                PermissionDetails(
                    getString(R.string.p_sensors_title),
                    getString(R.string.p_sensors_desc),
                    R.drawable.ic_sensor,
                    listOf(PERMISSION_BODY_SENSORS_BACKGROUND)
                )
            )
            permissionsList.add(
                PermissionDetails(
                    getString(R.string.p_notifications_title),
                    getString(R.string.p_notifications_desc),
                    R.drawable.ic_sensor,
                    listOf(POST_NOTIFICATIONS)
                )
            )
        }

        // 将列表中的每个权限映射到相应的 PermissionDetails，并放入 Map
        val permissionDetailsMap = mutableMapOf<String, PermissionDetails>()
        permissionsList.forEach { detail ->
            detail.permissions.firstOrNull()?.let { permission ->
                permissionDetailsMap[permission] = detail
            }
        }

        return permissionDetailsMap
    }

    @JvmStatic
    fun showPreviewDialog(context: Context, details: PermissionDetails, onConfirm: () -> Unit) {
        val dialog = PermissionPreviewDialog(context, details)
        dialog.show()
        dialog.setConfirmListener {
            onConfirm()
        }
    }

    @JvmStatic
    fun showPreviewDialog(context: Context, p: String, onResult: (Boolean) -> Unit) {
        val details = getPermissionDetails(p)
        if (details == null) {
            onResult(true)
        } else {
            val dialog = PermissionPreviewDialog(context, details)
            dialog.show()
            dialog.setConfirmListener {
                onResult(true)
            }
            dialog.setCancelListener {
                onResult(false)
            }
        }
    }

    @JvmStatic
    fun getPermissionDetails(permission: String): PermissionDetails? {
        return pDetails[permission]
    }
}
