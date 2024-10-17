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

val Int.BYTE: Int
    get() = this

val Int.KB: Int
    get() = 1024 * BYTE

val Int.MB: Int
    get() = 1024 * KB

val Int.GB: Int
    get() = 1024 * MB

fun Long.byteToFitMemorySize(precision: Int = 1): String {
    require(precision >= 0) { "小数点精度必须大于0" }
    return if (this < 0) {
        throw IllegalArgumentException("byteSize shouldn't be less than zero!")
    } else if (this < 1.KB) {
        String.format("%." + precision + "fB", toDouble())
    } else if (this < 1.MB) {
        String.format("%." + precision + "fKB", toDouble() / 1.KB)
    } else if (this < 1.GB) {
        String.format("%." + precision + "fMB", toDouble() / 1.MB)
    } else {
        String.format("%." + precision + "fGB", toDouble() / 1.GB)
    }
}

val Context.BaseFolder: File
    get() = getExternalFilesDir(null) ?: filesDir

val Context.TempCacheFolder: File
    get() = File(BaseFolder, "tempCache").also { it.createOrExistsDir() }

fun Uri.share(context: Context, cacheFile: File) {
    val guessMimeTypeFromExtension = MimeUtils.guessMimeTypeFromExtension(cacheFile.extension)
    val intent = Intent(Intent.ACTION_SEND)
    intent.apply {
        type = guessMimeTypeFromExtension
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra(Intent.EXTRA_STREAM, this@share)
    }
    context.startActivity(Intent.createChooser(intent, cacheFile.nameWithoutExtension))
}

fun File.moveToPublicDir(context: Context, parentDir: String, childDir: String): Boolean {
    val mimeType = MimeUtils.guessMimeTypeFromExtension(extension)
    var moveSuccess = false

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues().apply {
                put(MediaStore.Images.ImageColumns.DISPLAY_NAME, name)
                put(MediaStore.Images.ImageColumns.MIME_TYPE, mimeType)
                put(MediaStore.Images.ImageColumns.TITLE, name)
                put(MediaStore.Images.ImageColumns.RELATIVE_PATH, "$parentDir${File.separator}$childDir")

                FileInputStream(this@moveToPublicDir).use { inputStream ->
                    val insertUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, this@apply)
                    if (null != insertUri) {
                        context.contentResolver.openOutputStream(insertUri)?.use { outputStream ->
                            val buffer = ByteArray(4096)
                            var byteCount: Int
                            while (inputStream.read(buffer).also { byteCount = it } != -1) {
                                outputStream.write(buffer, 0, byteCount)
                            }
                            moveSuccess = true
                        }
                    }
                }
            }
        } else {
            val dstFilePath = "${Environment.getExternalStoragePublicDirectory(parentDir)}${File.separator}${name}"
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

fun File.createOrExistsDir(): Boolean = if (exists()) isDirectory else mkdirs()

fun File.createOrExistsFile(): Boolean = if (exists()) {
    isFile
} else if (parentFile?.createOrExistsDir() == true) {
    createNewFile()
} else {
    false
}

fun Uri.findAbsolutePath(context: Context): String {
    return runCatching {
        var path = ""
        if (scheme == "file") {
            path = this.toFile().absolutePath
        } else {
            context.contentResolver.query(this, arrayOf(MediaStore.Files.FileColumns.DATA), null, null, null)?.use {
                val pathIndex = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                it.moveToFirst()
                path = it.getString(pathIndex)
            }
        }
        path
    }.getOrElse { "" }
}

val Uri.fileName: String
    get() {
        val uriStr = Uri.decode(path).toString()
        return uriStr.substring(uriStr.lastIndexOf("/") + 1)
    }

fun Uri.findFileSize(context: Context): Long {
    return runCatching {
        var size = 0L
        if (scheme == "file") {
            size = this.toFile().length()
        } else {
            context.contentResolver.query(this, arrayOf(MediaStore.Files.FileColumns.SIZE), null, null, null)?.use {
                val pathIndex = it.getColumnIndex(MediaStore.Files.FileColumns.SIZE)
                it.moveToFirst()
                size = it.getLongOrNull(pathIndex) ?: 0L
            }
        }
        size
    }.getOrElse { 0L }
}