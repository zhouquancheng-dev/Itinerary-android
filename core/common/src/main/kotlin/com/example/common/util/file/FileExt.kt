package com.example.common.util.file

import android.app.Activity
import android.app.Application
import android.content.*
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.core.net.toFile
import com.avatar.lite.scas.R
import kotlinx.coroutines.flow.flow
import java.io.*
import java.nio.file.Files
import java.util.Calendar
import java.util.Locale

private const val TAG = "FileExt"

private val ALBUM_DIR = Environment.DIRECTORY_PICTURES

private class OutputFileTaker(var file: File? = null)

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
 * 获取Uri中的文件名。
 */
val Uri.fileName: String
    get() = Uri.decode(path).toString().substringAfterLast("/")

/**
 * 分享Uri文件
 * @param context 上下文
 * @param cacheFile 缓存文件
 * @param packageName 目标应用的包名（可选）
 */
fun Uri.share(context: Context, cacheFile: File, packageName: String? = null) {
    val mimeType = MimeUtils.guessMimeTypeFromExtension(cacheFile.extension)
    val intent = Intent(Intent.ACTION_SEND).apply {
        packageName?.let { setPackage(it) }
        type = mimeType
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra(Intent.EXTRA_STREAM, this@share)
    }
    context.startActivity(Intent.createChooser(intent, cacheFile.nameWithoutExtension))
}

/**
 * 分享文件
 * @param context 上下文
 * @param packageName 目标应用的包名（可选）
 */
fun File.share(context: Context, packageName: String? = null) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileProvider", this)
    val mimeType = MimeUtils.guessMimeTypeFromExtension(extension)
    val intent = Intent(Intent.ACTION_SEND).apply {
        packageName?.let { setPackage(it) }
        type = mimeType
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra(Intent.EXTRA_STREAM, uri)
    }
    context.startActivity(Intent.createChooser(intent, nameWithoutExtension))
}

/**
 * Shares a Bitmap image with other applications on the device.
 *
 * This function takes a Bitmap and shares it as a PNG image using the device's sharing mechanism.
 * It saves the Bitmap to a temporary file in the app's cache directory, creates a content URI for the file,
 * and then launches the share intent chooser, allowing the user to select the app they want to share the image with.
 *
 * The temporary file is automatically registered for deletion after the share operation.
 *
 * @receiver The Bitmap to be shared.
 * @param context The Context used to access application resources and start the sharing activity.
 *
 * @throws IllegalArgumentException if the bitmap is null.
 */
fun Bitmap.share(context: Context) {
    val tempFile = context.saveBitmapToCache(this)
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val appName = context.applicationInfo.loadLabel(context.packageManager).toString()

    context.startActivity(
        Intent.createChooser(intent, appName).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )

    context.registerTempFileDeletion(tempFile)
}

// 临时保存 Bitmap 到缓存目录
private fun Context.saveBitmapToCache(bitmap: Bitmap): File {
    val tempFile = File(cacheDir, "shared_image.png")
    FileOutputStream(tempFile).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    }
    return tempFile
}

// 注册生命周期回调，系统分享ui关闭后自动删除文件
private fun Context.registerTempFileDeletion(tempFile: File) {
    if (this is Application) {
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityDestroyed(activity: Activity) {
                if (activity.intent?.action == Intent.ACTION_CHOOSER || activity.intent?.action == Intent.ACTION_SEND) {
                    if (tempFile.exists()) {
                        tempFile.delete()
                    }
                    unregisterActivityLifecycleCallbacks(this)
                }
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        })
    }
}

/**
 * 打开文件
 * @param path 文件路径
 */
fun Context.openFile(path: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    val file = File(path)
    val fileUri = FileProvider.getUriForFile(this, "$packageName.fileProvider", file)
    Log.e(TAG, fileUri.toString())
    intent.setDataAndType(fileUri, MimeUtils.guessMimeTypeFromExtension(file.extension))
    startActivity(intent)
}

/**
 * 批量分享图片文件
 */
fun List<File>.sharePicFile(context: Context, packageName: String? = null) {
    val uriList = map {
        FileProvider.getUriForFile(context, "${context.packageName}.fileProvider", it)
    }
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        packageName?.let { setPackage(it) }
        type = "image/*"
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uriList))
    }
    context.startActivity(Intent.createChooser(intent, "批量分享"))
}

/**
 * 批量分享图片Uri
 */
fun List<Uri>.sharePicUri(context: Context, packageName: String? = null) {
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        packageName?.let { setPackage(it) }
        type = "image/*"
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(this@sharePicUri))
    }
    context.startActivity(Intent.createChooser(intent, "批量分享"))
}

/**
 * 将多个文件移动到公共目录
 * @return 返回Uri列表
 */
fun List<File>.moveToPublicDir(context: Context, parentDir: String, childDir: String): List<Uri?> {
    return map { it.moveToPublicDir(context, parentDir, childDir) }
}

/**
 * 将单个文件移动到公共目录
 * @return 返回Uri
 */
fun File.moveToPublicDir(context: Context, parentDir: String, childDir: String): Uri? {
    val mimeType = MimeUtils.guessMimeTypeFromExtension(extension)
    var resultUri: Uri? = null

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 适配Android Q及以上版本
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.ImageColumns.DISPLAY_NAME, name)
                put(MediaStore.Images.ImageColumns.MIME_TYPE, mimeType)
                put(MediaStore.Images.ImageColumns.TITLE, name)
                put(MediaStore.Images.ImageColumns.RELATIVE_PATH, "$parentDir${File.separator}$childDir")
            }

            FileInputStream(this).use { inputStream ->
                val insertUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                insertUri?.let { uri ->
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        val buffer = ByteArray(4096)
                        var byteCount: Int
                        while (inputStream.read(buffer).also { byteCount = it } != -1) {
                            outputStream.write(buffer, 0, byteCount)
                        }
                        resultUri = uri
                    }
                }
            }
        } else {
            // 适配Android Q以下版本
            val dstFilePath = "${Environment.getExternalStoragePublicDirectory(parentDir)}${File.separator}$name"
            if (renameTo(File(dstFilePath))) {
                MediaScannerConnection.scanFile(context, arrayOf(dstFilePath), arrayOf(mimeType), null)
                resultUri = FileProvider.getUriForFile(context, "${context.packageName}.fileProvider", File(dstFilePath))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return resultUri
}

/**
 * 将文件移动到公共目录，处理Android版本的差异。
 * @param context 用于访问系统资源的上下文。
 * @param parentDir 公共存储中的父目录。
 * @param childDir 公共存储中的子目录。
 * @return 表示移动是否成功的布尔值。
 */
fun File.moveToPublicDirReturnBoolean(context: Context, parentDir: String, childDir: String): Boolean {
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
 * 获取Uri的文件名
 */
fun Uri.findFileName(context: Context): String {
    return runCatching {
        var name = ""
        context.contentResolver.query(this, arrayOf(MediaStore.Files.FileColumns.DISPLAY_NAME), null, null, null)?.use {
            val nameIndex = it.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
            it.moveToFirst()
            name = it.getStringOrNull(nameIndex) ?: ""
        }
        name
    }.getOrElse { "" }
}

/**
 * 复制图片文件到相册的Pictures文件夹
 * @param context 上下文
 * @param fileName 文件名（包含扩展名）
 * @param relativePath 相对于Pictures的路径
 * @return 返回保存的Uri
 */
fun File.copyToAlbum(context: Context, fileName: String, relativePath: String?): Uri? {
    if (!this.canRead() || !this.exists()) {
        Log.w(TAG, "check: read file error: $this")
        return null
    }
    return this.inputStream().use { it.saveToAlbum(context, fileName, relativePath) }
}

/**
 * 保存图片输入流到相册的Pictures文件夹
 * @param context 上下文
 * @param fileName 文件名（包含扩展名）
 * @param relativePath 相对于Pictures的路径
 * @return 返回保存的Uri
 */
fun InputStream.saveToAlbum(context: Context, fileName: String, relativePath: String?): Uri? {
    val resolver = context.contentResolver
    val outputFile = OutputFileTaker()
    val imageUri = resolver.insertMediaImage(fileName, relativePath, outputFile)
    if (imageUri == null) {
        Log.w(TAG, "insert: error: uri == null")
        return null
    }

    // 保存图片
    imageUri.outputStream(resolver)?.use { output ->
        this.use { input ->
            input.copyTo(output)
            imageUri.finishPending(context, resolver, outputFile.file)
        }
    }
    return imageUri
}

/**
 * 保存Bitmap到相册的Pictures文件夹
 * @param context 上下文
 * @param fileName 文件名（包含扩展名）
 * @param relativePath 相对于Pictures的路径
 * @param quality 图片质量
 * @return 返回保存的Uri
 */
fun Bitmap.saveToAlbum(
    context: Context,
    fileName: String,
    relativePath: String? = null,
    quality: Int = 100,
): Uri? {
    val resolver = context.contentResolver
    val outputFile = OutputFileTaker()
    val imageUri = resolver.insertMediaImage(fileName, relativePath, outputFile)
    if (imageUri == null) {
        Log.w(TAG, "insert: error: uri == null")
        return null
    }

    // 保存Bitmap
    imageUri.outputStream(resolver)?.use {
        val format = fileName.getBitmapFormat()
        this.compress(format, quality, it)
        imageUri.finishPending(context, resolver, outputFile.file)
    }
    return imageUri
}

/**
 * 获取指定Uri的输出流
 * @param resolver 内容解析器
 * @return 输出流对象
 */
private fun Uri.outputStream(resolver: ContentResolver): OutputStream? {
    return try {
        resolver.openOutputStream(this)
    } catch (e: FileNotFoundException) {
        Log.e(TAG, "save: open stream error: $e")
        null
    }
}

/**
 * 完成保存任务并更新媒体库状态
 */
private fun Uri.finishPending(
    context: Context,
    resolver: ContentResolver,
    outputFile: File?,
) {
    val imageValues = ContentValues()
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        outputFile?.let {
            imageValues.put(MediaStore.Images.Media.SIZE, it.length())
        }
        resolver.update(this, imageValues, null, null)
        // 通知媒体库更新
        val intent = Intent(@Suppress("DEPRECATION") Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, this)
        context.sendBroadcast(intent)
    } else {
        // Android Q及以上版本，需要设置IS_PENDING为0，表示可见
        imageValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(this, imageValues, null, null)
    }
}

/**
 * 获取文件的压缩格式
 * @return 图片的压缩格式
 */
@Suppress("DEPRECATION")
private fun String.getBitmapFormat(): Bitmap.CompressFormat {
    return when {
        endsWith(".png", true) -> Bitmap.CompressFormat.PNG
        endsWith(".jpg", true) || endsWith(".jpeg", true) -> Bitmap.CompressFormat.JPEG
        endsWith(".webp", true) -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSLESS
        } else {
            Bitmap.CompressFormat.WEBP
        }
        else -> Bitmap.CompressFormat.PNG
    }
}

/**
 * 获取文件的MIME类型
 * @return MIME类型字符串
 */
private fun String.getMimeType(): String? {
    return when {
        endsWith(".png", true) -> "image/png"
        endsWith(".jpg", true) || endsWith(".jpeg", true) -> "image/jpeg"
        endsWith(".webp", true) -> "image/webp"
        endsWith(".gif", true) -> "image/gif"
        else -> null
    }
}

/**
 * 插入图片到媒体库
 * @param fileName 文件名
 * @param relativePath 相对路径
 * @param outputFileTaker 用于存储输出文件的对象
 * @return 插入图片的Uri
 */
private fun ContentResolver.insertMediaImage(
    fileName: String,
    relativePath: String?,
    outputFileTaker: OutputFileTaker? = null,
): Uri? {
    val imageValues = ContentValues().apply {
        val mimeType = fileName.getMimeType()
        mimeType?.let { put(MediaStore.Images.Media.MIME_TYPE, it) }
        val date = System.currentTimeMillis() / 1000
        put(MediaStore.Images.Media.DATE_ADDED, date)
        put(MediaStore.Images.Media.DATE_MODIFIED, date)
    }
    val collection: Uri
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val path = relativePath?.let { "$ALBUM_DIR/$it" } ?: ALBUM_DIR
        imageValues.apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.RELATIVE_PATH, path)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        // Android Q以下版本处理逻辑
        val pictures = @Suppress("DEPRECATION") Environment.getExternalStoragePublicDirectory(ALBUM_DIR)
        val saveDir = relativePath?.let { File(pictures, it) } ?: pictures
        if (!saveDir.exists() && !saveDir.mkdirs()) {
            Log.e(TAG, "save: error: can't create Pictures directory")
            return null
        }

        var imageFile = File(saveDir, fileName)
        val fileNameWithoutExtension = imageFile.nameWithoutExtension
        val fileExtension = imageFile.extension
        var queryUri = queryMediaImage28(imageFile.absolutePath)
        var suffix = 1
        while (queryUri != null) {
            val newName = "$fileNameWithoutExtension($suffix).$fileExtension"
            imageFile = File(saveDir, newName)
            queryUri = queryMediaImage28(imageFile.absolutePath)
        }

        imageValues.apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.name)
            put(@Suppress("DEPRECATION") MediaStore.Images.Media.DATA, imageFile.absolutePath)
        }
        outputFileTaker?.file = imageFile
        collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
    return insert(collection, imageValues)
}

/**
 * Android Q以下版本，查询媒体库中当前路径是否存在
 * @return 返回Uri，null表示不存在
 */
private fun ContentResolver.queryMediaImage28(imagePath: String): Uri? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return null

    val imageFile = File(imagePath)
    if (imageFile.canRead() && imageFile.exists()) {
        Log.v(TAG, "query: path: $imagePath exists")
        // 文件已存在，返回一个file://xxx的uri
        return Uri.fromFile(imageFile)
    }

    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val query = query(
        collection,
        arrayOf(MediaStore.Images.Media._ID, @Suppress("DEPRECATION") MediaStore.Images.Media.DATA),
        "${@Suppress("DEPRECATION") MediaStore.Images.Media.DATA} == ?",
        arrayOf(imagePath), null
    )
    query?.use {
        while (it.moveToNext()) {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val id = it.getLong(idColumn)
            val existsUri = ContentUris.withAppendedId(collection, id)
            Log.v(TAG, "query: path: $imagePath exists uri: $existsUri")
            return existsUri
        }
    }
    return null
}

/**
 * 将视频保存到系统相册
 * @param context 上下文
 * @param videoFile 视频文件路径
 * @param deleteSource 是否删除源文件，默认为true删除
 * @return 成功返回true，失败返回false
 */
fun saveVideoToGallery(context: Context, videoFile: String, deleteSource: Boolean = true): Boolean {
    Log.d(TAG, "saveVideoToAlbum() videoFile = [$videoFile]")
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        saveVideoToAlbumBeforeQ(context, videoFile, deleteSource)
    } else {
        saveVideoToAlbumAfterQ(context, videoFile, deleteSource)
    }
}

/**
 * 保存视频到系统相册（适配Android Q及以上版本）
 * @param context 上下文
 * @param videoFile 视频文件路径
 * @param deleteSource 是否删除源文件
 * @return 成功返回true，失败返回false
 */
@Suppress("DEPRECATION")
private fun saveVideoToAlbumAfterQ(context: Context, videoFile: String, deleteSource: Boolean): Boolean {
    return try {
        val contentResolver = context.contentResolver
        val tempFile = File(videoFile)
        val contentValues = getVideoContentValues(context, tempFile, System.currentTimeMillis())
        val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)

        // 拷贝文件到新Uri
        copyFileAfterQ(context, contentResolver, tempFile, uri, deleteSource)

        // 更新Uri状态为可见
        contentValues.clear()
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        contentResolver.update(uri!!, contentValues, null, null)

        // 发送媒体扫描广播
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 保存视频到系统相册（适配Android Q以下版本）
 * @param context 上下文
 * @param videoFile 视频文件路径
 * @param deleteSource 是否删除源文件
 * @return 成功返回true，失败返回false
 */
private fun saveVideoToAlbumBeforeQ(context: Context, videoFile: String, deleteSource: Boolean): Boolean {
    val picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
    val tempFile = File(videoFile)
    val destFile = File(picDir, "${context.packageName}${File.separator}${tempFile.name}")

    return try {
        // 拷贝文件到目标路径
        FileInputStream(tempFile).use { ins ->
            BufferedOutputStream(FileOutputStream(destFile)).use { ous ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (ins.read(buffer).also { bytesRead = it } > 0) {
                    ous.write(buffer, 0, bytesRead)
                }
            }
        }

        // 删除源文件
        if (deleteSource) {
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }

        // 通知媒体库更新
        MediaScannerConnection.scanFile(context, arrayOf(destFile.absolutePath), arrayOf("video/*")) { path, uri ->
            Log.d(TAG, "saveVideoToAlbum: $path $uri")
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 在Android Q及以上版本中将视频文件拷贝到相册的Uri位置
 * @param context 上下文
 * @param localContentResolver 内容解析器
 * @param tempFile 临时文件
 * @param localUri 保存位置的Uri
 * @param deleteSource 是否删除源文件
 */
private fun copyFileAfterQ(
    context: Context,
    localContentResolver: ContentResolver,
    tempFile: File,
    localUri: Uri?,
    deleteSource: Boolean
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
        context.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.Q
    ) {
        localUri?.let {
            // 拷贝文件到目标Uri
            localContentResolver.openOutputStream(it)?.use { outputStream ->
                Files.copy(tempFile.toPath(), outputStream)
            }
            if (deleteSource) {
                if (tempFile.exists()) {
                    tempFile.delete() // 删除临时文件
                }
            }
        }
    }
}

/**
 * 获取视频文件的ContentValues
 * @param context 上下文
 * @param paramFile 视频文件
 * @param timestamp 时间戳
 * @return ContentValues对象
 */
private fun getVideoContentValues(context: Context, paramFile: File, timestamp: Long): ContentValues {
    return ContentValues().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + context.packageName)
        }
        put(MediaStore.Video.Media.TITLE, paramFile.name)
        put(MediaStore.Video.Media.DISPLAY_NAME, paramFile.name)
        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        put(MediaStore.Video.Media.DATE_TAKEN, timestamp)
        put(MediaStore.Video.Media.DATE_MODIFIED, timestamp)
        put(MediaStore.Video.Media.DATE_ADDED, timestamp)
        put(MediaStore.Video.Media.SIZE, paramFile.length())
    }
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
