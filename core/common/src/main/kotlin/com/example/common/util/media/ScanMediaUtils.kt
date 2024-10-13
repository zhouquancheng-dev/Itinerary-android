package com.example.common.util.media

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retry
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ScanMediaUtils {

    /**
     * 动态申请权限后进行媒体查询
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun queryLocalMediaFlow(context: Context, vararg mediaTypes: MediaType): Flow<MediaInfo> =
        flowOf(*mediaTypes).flatMapMerge(mediaTypes.size) { mediaType ->
            val projection = buildProjection()
            val selection = buildSelection()
            val selectionArgs = arrayOf("1")
            val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

            context.queryMediaFlow(mediaType, projection, selection, selectionArgs, sortOrder)
        }
            .flowOn(Dispatchers.IO)
            .distinctUntilChanged()

    /**
     * Context 扩展函数进行媒体查询
     */
    private fun Context.queryMediaFlow(mediaType: MediaType, projection: Array<String>, selection: String, selectionArgs: Array<String>, sortOrder: String): Flow<MediaInfo> = flow {
        val uri = mediaType.getMediaUri()
        contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            while (cursor.moveToNext()) {
                emit(cursor.toMediaInfo(mediaType))
            }
        }
    }.retry(3) { cause ->
        cause is IOException || cause is SecurityException
    }

    /**
     * 构建查询的 projection
     */
    private fun buildProjection(): Array<String> = mutableListOf(
        MediaStore.MediaColumns._ID,
        MediaStore.MediaColumns.DISPLAY_NAME,
        MediaStore.MediaColumns.DATE_ADDED,
        MediaStore.MediaColumns.SIZE
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(MediaStore.MediaColumns.RELATIVE_PATH)
            add(MediaStore.MediaColumns.IS_PENDING)
        } else {
            add(MediaStore.MediaColumns.DATA)
        }
    }.toTypedArray()

    /**
     * 构建查询条件 selection
     */
    private fun buildSelection(): String = StringBuilder("${MediaStore.MediaColumns.SIZE} >= ?").apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            append(" AND ${MediaStore.MediaColumns.IS_PENDING} = 0")
        }
    }.toString()

    /**
     * MediaType 扩展函数获取对应的 Uri
     */
    private fun MediaType.getMediaUri(): Uri = when (this) {
        MediaType.IMAGE -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        MediaType.VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        MediaType.AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    /**
     * Cursor 扩展函数转换为 MediaInfo
     */
    private fun Cursor.toMediaInfo(mediaType: MediaType): MediaInfo {
        val idColumn = getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
        val nameColumn = getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
        val timeColumn = getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
        val sizeColumn = getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
        val relativePathColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)
        } else {
            getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        }

        val id = getLong(idColumn)
        val name = getString(nameColumn)
        val timeInSeconds = getLong(timeColumn)
        val size = getLong(sizeColumn)

        val mediaPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val relativePath = getString(relativePathColumn)
            val displayName = getString(nameColumn)
            "${Environment.getExternalStorageDirectory()}/$relativePath$displayName"
        } else {
            getString(relativePathColumn)
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedTime = dateFormat.format(Date(timeInSeconds * 1000))
        val formattedSize = size.formatFileSize()

        return MediaInfo(id, mediaPath, name, formattedTime, formattedSize, mediaType)
    }

    /**
     * Long 扩展函数格式化文件大小
     */
    private fun Long.formatFileSize(): String {
        val kiloByte: Long = 1024
        val megaByte = kiloByte * 1024
        val gigaByte = megaByte * 1024
        val teraBytes = gigaByte * 1024

        return when {
            this < kiloByte -> "${this}B"
            this < megaByte -> "${this / kiloByte}KB"
            this < gigaByte -> "${String.format(Locale.getDefault(), "%.2f", this / megaByte.toDouble())}MB"
            this < teraBytes -> "${String.format(Locale.getDefault(), "%.2f", this / gigaByte.toDouble())}GB"
            else -> "${String.format(Locale.getDefault(), "%.2f", this / teraBytes.toDouble())}TB"
        }
    }

    /**
     * 动态申请权限的封装函数
     */
    fun checkAndRequestPermissions(activity: Activity, permissionRequestCode: Int) {
        val permissionsToRequest = mutableListOf<String>()

        // 检查 READ_EXTERNAL_STORAGE 权限
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // 检查 Android 10 以下的 WRITE_EXTERNAL_STORAGE 权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        // 针对 Android 10 及以上的 ACCESS_MEDIA_LOCATION 权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_MEDIA_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_MEDIA_LOCATION)
        }

        // 针对 Android 11 及以上的 MANAGE_EXTERNAL_STORAGE 权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            !Environment.isExternalStorageManager()) {
            // MANAGE_EXTERNAL_STORAGE 需要特别处理
            // 引导用户到设置中开启权限
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            activity.startActivity(intent)
            return
        }

        // 如果有需要申请的权限，动态申请
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                permissionsToRequest.toTypedArray(),
                permissionRequestCode
            )
        }
    }

}
