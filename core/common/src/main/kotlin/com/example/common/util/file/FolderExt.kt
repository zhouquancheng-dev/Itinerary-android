package com.example.common.util.file

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.database.getLongOrNull
import androidx.core.net.toFile
import com.example.common.util.mime.MimeUtils
import java.io.File
import java.io.FileInputStream
import java.util.Locale

// 扩展属性：以字节为单位的大小
val Int.BYTE: Int
    get() = this

val Int.KB: Int
    get() = 1024 * BYTE

val Int.MB: Int
    get() = 1024 * KB

val Int.GB: Int
    get() = 1024 * MB

/**
 * 将字节大小转换为带有指定精度的易读内存大小格式。
 * @param precision 小数位数精度。
 * @return 格式化的大小字符串，包含适当的单位（B、KB、MB、GB）。
 */
fun Long.byteToFitMemorySize(precision: Int = 1): String {
    require(precision >= 0) { "精度必须大于等于零" }
    if (this < 0) throw IllegalArgumentException("字节大小不能小于零！")

    return when {
        this < 1.KB -> String.format(Locale.getDefault(), "%.${precision}fB", toDouble())
        this < 1.MB -> String.format(Locale.getDefault(), "%.${precision}fKB", toDouble() / 1.KB)
        this < 1.GB -> String.format(Locale.getDefault(), "%.${precision}fMB", toDouble() / 1.MB)
        else -> String.format(Locale.getDefault(), "%.${precision}fGB", toDouble() / 1.GB)
    }
}

/**
 * 获取应用程序的基本文件夹。
 */
val Context.BaseFolder: File
    get() = getExternalFilesDir(null) ?: filesDir

/**
 * 获取临时缓存文件夹。
 */
val Context.TempCacheFolder: File
    get() = File(BaseFolder, "tempCache").apply { createOrExistsDir() }

/**
 * 使用Intent共享文件表示的Uri。
 * @param context 用于启动共享Intent的上下文。
 * @param cacheFile 要共享的文件。
 */
fun Uri.share(context: Context, cacheFile: File) {
    val mimeType = MimeUtils.guessMimeTypeFromExtension(cacheFile.extension)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra(Intent.EXTRA_STREAM, this@share)
    }
    context.startActivity(Intent.createChooser(intent, cacheFile.nameWithoutExtension))
}

/**
 * 将文件移动到公共目录，处理Android版本的差异。
 * @param context 用于访问系统资源的上下文。
 * @param parentDir 公共存储中的父目录。
 * @param childDir 公共存储中的子目录。
 * @return 表示移动是否成功的布尔值。
 */
fun File.moveToPublicDir(context: Context, parentDir: String, childDir: String): Boolean {
    val mimeType = MimeUtils.guessMimeTypeFromExtension(extension)
    var moveSuccess = false

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.ImageColumns.DISPLAY_NAME, name)
                put(MediaStore.Images.ImageColumns.MIME_TYPE, mimeType)
                put(MediaStore.Images.ImageColumns.TITLE, name)
                put(MediaStore.Images.ImageColumns.RELATIVE_PATH, "$parentDir${File.separator}$childDir")
            }
            FileInputStream(this).use { inputStream ->
                val insertUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                insertUri?.let { uri ->
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        val buffer = ByteArray(4096)
                        var byteCount: Int
                        while (inputStream.read(buffer).also { byteCount = it } != -1) {
                            outputStream.write(buffer, 0, byteCount)
                        }
                        moveSuccess = true
                    }
                }
            }
        } else {
            val dstFilePath = "${Environment.getExternalStoragePublicDirectory(parentDir)}${File.separator}$name"
            if (renameTo(File(dstFilePath))) {
                moveSuccess = true
                MediaScannerConnection.scanFile(context, arrayOf(dstFilePath), arrayOf(mimeType), null)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        moveSuccess = false
    }

    return moveSuccess
}

/**
 * 创建或验证目录是否存在。
 * @return 如果目录已创建或已存在，则返回true；否则返回false。
 */
fun File.createOrExistsDir(): Boolean = if (exists()) isDirectory else mkdirs()

/**
 * 创建或验证文件是否存在。
 * @return 如果文件已创建或已存在，则返回true；否则返回false。
 */
fun File.createOrExistsFile(): Boolean = if (exists()) {
    isFile
} else {
    parentFile?.createOrExistsDir() == true && createNewFile()
}

/**
 * 查找Uri的绝对路径。
 * @param context 用于解析Uri的上下文。
 * @return Uri的绝对路径。
 */
fun Uri.findAbsolutePath(context: Context): String {
    return runCatching {
        when (scheme) {
            "file" -> toFile().absolutePath
            else -> {
                var path = ""
                context.contentResolver.query(this, arrayOf(MediaStore.Files.FileColumns.DATA), null, null, null)?.use {
                    val pathIndex = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                    if (it.moveToFirst()) path = it.getString(pathIndex)
                }
                path
            }
        }
    }.getOrElse { "" }
}

/**
 * 获取Uri中的文件名。
 */
val Uri.fileName: String
    get() = Uri.decode(path).toString().substringAfterLast("/")

/**
 * 查找由Uri表示的文件大小。
 * @param context 用于解析Uri的上下文。
 * @return 文件的字节大小。
 */
fun Uri.findFileSize(context: Context): Long {
    return runCatching {
        when (scheme) {
            "file" -> toFile().length()
            else -> {
                var size = 0L
                context.contentResolver.query(this, arrayOf(MediaStore.Files.FileColumns.SIZE), null, null, null)?.use {
                    val sizeIndex = it.getColumnIndex(MediaStore.Files.FileColumns.SIZE)
                    if (it.moveToFirst()) size = it.getLongOrNull(sizeIndex) ?: 0L
                }
                size
            }
        }
    }.getOrElse { 0L }
}
