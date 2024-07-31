package com.example.common.util.permissionUtil

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import com.example.common.R
import com.example.common.config.AppConfig
import com.example.common.util.permissionUtil.ext.Constant.MANAGE_EXTERNAL_STORAGE
import com.example.common.util.permissionUtil.ext.Constant.READ_EXTERNAL_STORAGE
import com.example.common.util.permissionUtil.ext.Constant.READ_MEDIA_AUDIO
import com.example.common.util.permissionUtil.ext.Constant.READ_MEDIA_IMAGES
import com.example.common.util.permissionUtil.ext.Constant.READ_MEDIA_VIDEO
import com.example.common.util.permissionUtil.ext.Constant.WRITE_EXTERNAL_STORAGE
import com.example.common.util.permissionUtil.data.PermissionCase
import com.example.common.util.permissionUtil.data.TimeDataSource
import com.example.common.util.permissionUtil.dialog.ManageExternalPreviewDialog
import com.example.common.util.permissionUtil.ext.requestPermission
import com.example.common.util.permissionUtil.manage.PermissionPageManagement
import com.hjq.toast.Toaster
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.launch

object AllowPermissionUseCase {

    private const val PERMISSION_REQUEST_TAG = "PERMISSION_REQUEST_TAG"
    private const val DEFAULT_MESSAGE = "请同意申请的必要权限"

    /**
     * 使用 [PermissionX] 权限请求库以及库提供的 Dialog UI 申请权限
     *
     * @param fragment The [Fragment] requesting the permissions
     * @param permissions List of permissions to request
     * @param tag The tag used for permission request (default: [PERMISSION_REQUEST_TAG])
     * @param message The message to display when requesting permissions (default: [DEFAULT_MESSAGE])
     * @param onPermissionGranted Callback invoked when permissions are granted
     */
    @JvmStatic
    operator fun invoke(
        fragment: Fragment,
        permissions: List<String>,
        tag: String = PERMISSION_REQUEST_TAG,
        message: String = DEFAULT_MESSAGE,
        onPermissionGranted: () -> Unit
    ) {
        if (arePermissionsGranted(fragment.requireContext(), permissions)) {
            onPermissionGranted.invoke()
            return
        }
        fragment.viewLifecycleOwner.lifecycleScope.launch {
            if (shouldShowPermissionRequest(fragment.requireContext(), tag)) {
                fragment.requestPermission(
                    permissions,
                    deniedBlock = { showPermissionDeniedMessage(fragment.requireContext(), tag, message) },
                    grantedBlock = onPermissionGranted
                )
            } else {
                navigateToPermissionSettings(fragment.requireContext(), message)
            }
        }
    }

    /**
     * 使用 [PermissionX] 权限请求库以及库提供的 Dialog UI 申请权限
     *
     * @param activity The [FragmentActivity] requesting the permissions
     * @param permissions List of permissions to request
     * @param tag The tag used for permission request (default: [PERMISSION_REQUEST_TAG])
     * @param message The message to display when requesting permissions (default: [DEFAULT_MESSAGE])
     * @param onPermissionGranted Callback invoked when permissions are granted
     */
    @JvmStatic
    operator fun invoke(
        activity: FragmentActivity,
        permissions: List<String>,
        tag: String = PERMISSION_REQUEST_TAG,
        message: String = DEFAULT_MESSAGE,
        onPermissionGranted: () -> Unit
    ) {
        if (arePermissionsGranted(activity, permissions)) {
            onPermissionGranted.invoke()
            return
        }
        activity.lifecycleScope.launch {
            if (shouldShowPermissionRequest(activity, tag)) {
                activity.requestPermission(
                    permissions,
                    deniedBlock = { showPermissionDeniedMessage(activity, tag, message) },
                    grantedBlock = onPermissionGranted
                )
            } else {
                navigateToPermissionSettings(activity, message)
            }
        }
    }

    /**
     * 使用 [PermissionX] 权限请求库和自定义的 Dialog UI 请求媒体库权限
     *
     * 对于 Android 13 (TIRAMISU) 及以上版本，方法中使用以下权限：
     * - [Manifest.permission.READ_MEDIA_IMAGES]
     * - [Manifest.permission.READ_MEDIA_VIDEO]
     * - [Manifest.permission.READ_MEDIA_AUDIO]
     *
     * 对于 Android 13 以下版本，则使用以下权限：
     * - [Manifest.permission.WRITE_EXTERNAL_STORAGE]
     * - [Manifest.permission.READ_EXTERNAL_STORAGE]
     *
     * @param fragment The [Fragment] requesting the permissions
     * @param tag The tag used for permission request (default: [PERMISSION_REQUEST_TAG])
     * @param message The message to display when requesting permissions (default: [DEFAULT_MESSAGE])
     * @param onPermissionGranted Callback invoked when permissions are granted
     */
    @JvmStatic
    fun requestGalleryPermission(
        fragment: Fragment,
        tag: String = PERMISSION_REQUEST_TAG,
        message: String = DEFAULT_MESSAGE,
        onPermissionGranted: () -> Unit
    ) {
        handleGalleryPermission(fragment, tag, message, onPermissionGranted)
    }

    /**
     * 使用 [PermissionX] 权限请求库和自定义的 Dialog UI 请求媒体库权限
     *
     * 对于 Android 13 (TIRAMISU) 及以上版本，方法中使用以下权限：
     * - [Manifest.permission.READ_MEDIA_IMAGES]
     * - [Manifest.permission.READ_MEDIA_VIDEO]
     * - [Manifest.permission.READ_MEDIA_AUDIO]
     *
     * 对于 Android 13 以下版本，则使用以下权限：
     * - [Manifest.permission.WRITE_EXTERNAL_STORAGE]
     * - [Manifest.permission.READ_EXTERNAL_STORAGE]
     *
     * @param activity The [FragmentActivity] requesting the permissions
     * @param tag The tag used for permission request (default: [PERMISSION_REQUEST_TAG])
     * @param message The message to display when requesting permissions (default: [DEFAULT_MESSAGE])
     * @param onPermissionGranted Callback invoked when permissions are granted
     */
    @JvmStatic
    fun requestGalleryPermission(
        activity: FragmentActivity,
        tag: String = PERMISSION_REQUEST_TAG,
        message: String = DEFAULT_MESSAGE,
        onPermissionGranted: () -> Unit
    ) {
        handleGalleryPermission(activity, tag, message, onPermissionGranted)
    }

    private fun handleGalleryPermission(
        fragment: Fragment,
        tag: String,
        message: String,
        onPermissionGranted: () -> Unit
    ) {
        val p: String
        val permissionsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = READ_MEDIA_IMAGES
            listOf(
                READ_MEDIA_IMAGES,
                READ_MEDIA_VIDEO,
                READ_MEDIA_AUDIO
            )
        } else {
            p = WRITE_EXTERNAL_STORAGE
            listOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
        }
        if (arePermissionsGranted(fragment.requireContext(), permissionsList)) {
            onPermissionGranted.invoke()
            return
        }
        fragment.viewLifecycleOwner.lifecycleScope.launch {
            if (shouldShowPermissionRequest(fragment.requireContext(), tag)) {
                PermissionCase.showPreviewDialog(fragment.requireContext(), p) {
                    if (it) {
                        PermissionX.init(fragment)
                            .permissions(permissionsList)
                            .request { allGranted, _, _ ->
                                if (allGranted) {
                                    onPermissionGranted.invoke()
                                } else {
                                    showPermissionDeniedMessage(fragment.requireContext(), tag, message)
                                }
                            }
                    } else {
                        showPermissionDeniedMessage(fragment.requireContext(), tag, message)
                    }
                }
            } else {
                navigateToPermissionSettings(fragment.requireContext(), message)
            }
        }
    }

    private fun handleGalleryPermission(
        activity: FragmentActivity,
        tag: String,
        message: String,
        onPermissionGranted: () -> Unit
    ) {
        val p: String
        val permissionsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = READ_MEDIA_IMAGES
            listOf(
                READ_MEDIA_IMAGES,
                READ_MEDIA_VIDEO,
                READ_MEDIA_AUDIO
            )
        } else {
            p = WRITE_EXTERNAL_STORAGE
            listOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
        }
        if (arePermissionsGranted(activity, permissionsList)) {
            onPermissionGranted.invoke()
            return
        }
        activity.lifecycleScope.launch {
            if (shouldShowPermissionRequest(activity, tag)) {
                PermissionCase.showPreviewDialog(activity, p) {
                    if (it) {
                        PermissionX.init(activity)
                            .permissions(permissionsList)
                            .request { allGranted, _, _ ->
                                if (allGranted) {
                                    onPermissionGranted.invoke()
                                } else {
                                    showPermissionDeniedMessage(activity, tag, message)
                                }
                            }
                    } else {
                        showPermissionDeniedMessage(activity, tag, message)
                    }
                }
            } else {
                navigateToPermissionSettings(activity, message)
            }
        }
    }

    /**
     * 使用 [PermissionX] 权限请求库和自定义的 Dialog UI 请求应用存储权限
     *
     * 对于 Android 11 (R) 及以上版本，方法中使用以下权限：
     * - [Manifest.permission.MANAGE_EXTERNAL_STORAGE]
     *
     * 对于 Android 11 以下版本，则使用以下权限：
     * - [Manifest.permission.WRITE_EXTERNAL_STORAGE]
     *
     * @param activity The [FragmentActivity] requesting the permissions
     * @param tag The tag used for permission request (default: [PERMISSION_REQUEST_TAG])
     * @param message The message to display when requesting permissions (default: [DEFAULT_MESSAGE])
     * @param onPermissionGranted Callback invoked when permissions are granted
     */
    @JvmStatic
    fun requestManageExternalStoragePermission(
        activity: FragmentActivity,
        tag: String = PERMISSION_REQUEST_TAG,
        message: String = DEFAULT_MESSAGE,
        onPermissionGranted: () -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                onPermissionGranted.invoke()
                return
            }
        } else {
            if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                onPermissionGranted.invoke()
                return
            }
        }

        activity.lifecycleScope.launch {
            if (shouldShowPermissionRequest(activity, tag)) {
                handleManageExternalStorage(activity, tag, message, onPermissionGranted)
            } else {
                navigateToPermissionSettings(activity, message) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        handleManageExternalStorage(activity, tag, message, onPermissionGranted)
                    }
                }
            }
        }
    }

    private fun handleManageExternalStorage(
        activity: FragmentActivity,
        tag: String,
        message: String,
        onPermissionGranted: () -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val manageExternalStorageDetails = PermissionCase.getPermissionDetails(MANAGE_EXTERNAL_STORAGE) ?: return
            PermissionX.init(activity)
                .permissions(listOf(MANAGE_EXTERNAL_STORAGE))
                .onExplainRequestReason { scope, _ ->
                    scope.showRequestReasonDialog(ManageExternalPreviewDialog(activity, manageExternalStorageDetails))
                }
                .request { allGranted, _, _ ->
                    if (allGranted) {
                        onPermissionGranted.invoke()
                    } else {
                        showPermissionDeniedMessage(activity, tag, message)
                    }
                }
        } else {
            PermissionCase.showPreviewDialog(activity, WRITE_EXTERNAL_STORAGE) {
                if (it) {
                    PermissionX.init(activity)
                        .permissions(listOf(WRITE_EXTERNAL_STORAGE))
                        .request { allGranted, _, _ ->
                            if (allGranted) {
                                onPermissionGranted.invoke()
                            } else {
                                showPermissionDeniedMessage(activity, tag, message)
                            }
                        }
                } else {
                    showPermissionDeniedMessage(activity, tag, message)
                }
            }
        }
    }

    /**
     * 使用 [PermissionX] 权限请求库和自定义的 Dialog UI 请求单个权限
     *
     * @param fragment The [Fragment] requesting the permissions
     * @param permissions The name of the permission to request
     * @param tag The tag used for permission request (default: [PERMISSION_REQUEST_TAG])
     * @param message The message to display when requesting permissions (default: [DEFAULT_MESSAGE])
     * @param onPermissionGranted Callback invoked when permissions are granted
     */
    @JvmStatic
    fun requestPermission(
        fragment: Fragment,
        permissions: String,
        tag: String = PERMISSION_REQUEST_TAG,
        message: String = DEFAULT_MESSAGE,
        onPermissionGranted: () -> Unit
    ) {
        requestWithPreviewDialog(fragment, permissions, tag, message, onPermissionGranted)
    }

    /**
     * 使用 [PermissionX] 权限请求库和自定义的 Dialog UI 请求单个权限
     *
     * @param activity The [FragmentActivity] requesting the permissions
     * @param permissions The name of the permission to request
     * @param tag The tag used for permission request (default: [PERMISSION_REQUEST_TAG])
     * @param message The message to display when requesting permissions (default: [DEFAULT_MESSAGE])
     * @param onPermissionGranted Callback invoked when permissions are granted
     */
    @JvmStatic
    fun requestPermission(
        activity: FragmentActivity,
        permissions: String,
        tag: String = PERMISSION_REQUEST_TAG,
        message: String = DEFAULT_MESSAGE,
        onPermissionGranted: () -> Unit
    ) {
        requestWithPreviewDialog(activity, permissions, tag, message, onPermissionGranted)
    }

    /**
     * 使用 [PermissionX] 权限请求库和自定义的 Dialog UI 请求多个权限
     *
     * @param fragment The [Fragment] requesting the permissions
     * @param permissions List of permissions to request
     * @param tag The tag used for permission request (default: [PERMISSION_REQUEST_TAG])
     * @param message The message to display when requesting permissions (default: [DEFAULT_MESSAGE])
     * @param onPermissionGranted Callback invoked when permissions are granted
     */
    @JvmStatic
    fun requestMultiPermission(
        fragment: Fragment,
        permissions: List<String>,
        tag: String = PERMISSION_REQUEST_TAG,
        message: String = DEFAULT_MESSAGE,
        onPermissionGranted: () -> Unit
    ) {
        if (arePermissionsGranted(fragment.requireContext(), permissions)) {
            onPermissionGranted.invoke()
            return
        }
        var index = 0
        var grantedCallback: (() -> Unit)? = null
        grantedCallback = {
            index++
            if (index < permissions.size) {
                requestWithPreviewDialog(fragment, permissions[index], tag, message, grantedCallback!!)
            } else {
                onPermissionGranted.invoke()
            }
        }
        requestWithPreviewDialog(fragment, permissions[index], tag, message, grantedCallback)
    }

    /**
     * 使用 [PermissionX] 权限请求库和自定义的 Dialog UI 请求多个权限
     *
     * @param activity The [FragmentActivity] requesting the permissions
     * @param permissions List of permissions to request
     * @param tag The tag used for permission request (default: [PERMISSION_REQUEST_TAG])
     * @param message The message to display when requesting permissions (default: [DEFAULT_MESSAGE])
     * @param onPermissionGranted Callback invoked when permissions are granted
     */
    @JvmStatic
    fun requestMultiPermission(
        activity: FragmentActivity,
        permissions: List<String>,
        tag: String = PERMISSION_REQUEST_TAG,
        message: String = DEFAULT_MESSAGE,
        onPermissionGranted: () -> Unit
    ) {
        if (arePermissionsGranted(activity, permissions)) {
            onPermissionGranted.invoke()
            return
        }
        var index = 0
        var grantedCallback: (() -> Unit)? = null
        grantedCallback = {
            index++
            if (index < permissions.size) {
                requestWithPreviewDialog(activity, permissions[index], tag, message, grantedCallback!!)
            } else {
                onPermissionGranted.invoke()
            }
        }
        requestWithPreviewDialog(activity, permissions[index], tag, message, grantedCallback)
    }

    private fun requestWithPreviewDialog(
        fragment: Fragment,
        permission: String,
        tag: String,
        message: String,
        onPermissionGranted: () -> Unit
    ) {
        if (arePermissionsGranted(fragment.requireContext(), listOf(permission))) {
            onPermissionGranted.invoke()
            return
        }
        fragment.viewLifecycleOwner.lifecycleScope.launch {
            if (shouldShowPermissionRequest(fragment.requireContext(), tag)) {
                PermissionCase.showPreviewDialog(fragment.requireContext(), permission) {
                    if (it) {
                        PermissionX.init(fragment)
                            .permissions(listOf(permission))
                            .request { allGranted, _, _ ->
                                if (allGranted) {
                                    onPermissionGranted.invoke()
                                } else {
                                    showPermissionDeniedMessage(fragment.requireContext(), tag, message)
                                }
                            }
                    } else {
                        showPermissionDeniedMessage(fragment.requireContext(), tag, message)
                    }
                }
            } else {
                navigateToPermissionSettings(fragment.requireContext(), message)
            }
        }
    }

    private fun requestWithPreviewDialog(
        activity: FragmentActivity,
        permission: String,
        tag: String,
        message: String,
        onPermissionGranted: () -> Unit
    ) {
        if (arePermissionsGranted(activity, listOf(permission))) {
            onPermissionGranted.invoke()
            return
        }
        activity.lifecycleScope.launch {
            if (shouldShowPermissionRequest(activity, tag)) {
                PermissionCase.showPreviewDialog(activity, permission) {
                    if (it) {
                        PermissionX.init(activity)
                            .permissions(listOf(permission))
                            .request { allGranted, _, _ ->
                                if (allGranted) {
                                    onPermissionGranted.invoke()
                                } else {
                                    showPermissionDeniedMessage(activity, tag, message)
                                }
                            }
                    } else {
                        showPermissionDeniedMessage(activity, tag, message)
                    }
                }
            } else {
                navigateToPermissionSettings(activity, message)
            }
        }
    }

    private fun showPermissionDeniedMessage(context: Context, tag: String, message: String) {
        savePermissionRequestTime(context, tag)
        Toaster.show(if (message == DEFAULT_MESSAGE) message else "请同意我们申请${message}")
    }

    private fun navigateToPermissionSettings(
        context: Context,
        message: String?,
        toSetting: (() -> Unit)? = null
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_permission, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialog_title)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        val positiveButton = dialogView.findViewById<Button>(R.id.dialog_positive_button)
        val negativeButton = dialogView.findViewById<Button>(R.id.dialog_negative_button)

        dialogTitle.setText(R.string.hint)
        dialogMessage.text = context.getString(
            R.string.permission_settings_message, context.getString(R.string.app_name),
            if (message == DEFAULT_MESSAGE) "" else message
        )

        val alertDialog = AlertDialog.Builder(context, R.style.BaseAlertDialogStyle)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        positiveButton.setText(R.string.go_to_settings)
        positiveButton.setOnClickListener {
            val pm = PermissionPageManagement()
            toSetting?.invoke() ?: pm.startSettingAppPermission(context)
            alertDialog.dismiss()
        }

        negativeButton.setText(R.string.cancel)
        negativeButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun arePermissionsGranted(context: Context, permissions: List<String>): Boolean {
        return permissions.all {
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, it)
        }
    }

    private suspend fun shouldShowPermissionRequest(context: Context, tag: String): Boolean {
        val lastRequestTime = TimeDataSource.getTime(context, tag)
        val currentTime = System.currentTimeMillis()
        val minIntervalTime = if (AppConfig.IS_DEBUG) 60 * 1000 else 24 * 60 * 60 * 1000
        val shouldShow = currentTime - lastRequestTime > minIntervalTime
        LogUtils.d(
            "最后拒绝时间: $lastRequestTime",
            "当前时间: $currentTime",
            "时间间隔: $minIntervalTime",
            "是否需要申请权限: $shouldShow"
        )
        return shouldShow
    }

    private fun savePermissionRequestTime(context: Context, tag: String) {
        TimeDataSource.updateTime(context, tag)
    }
}
