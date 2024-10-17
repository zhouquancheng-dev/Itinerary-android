package com.example.utils

import android.content.*
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.database.getStringOrNull
import com.blankj.utilcode.util.FileUtils
import com.example.common.util.file.BaseFolder
import com.example.common.util.file.createOrExistsDir
import com.example.common.util.mime.MimeUtils
import kotlinx.coroutines.flow.flow
import java.io.*
import java.nio.file.Files
import java.util.Calendar

private const val TAG = "ImageExt"

private val ALBUM_DIR = Environment.DIRECTORY_PICTURES

private class OutputFileTaker(var file: File? = null)

val Context.CompressCacheFolder: File
    get() = File(BaseFolder, "CompressCache").also { it.createOrExistsDir() }

fun Uri.share(context: Context, cacheFile: File, packageName: String? = null) {
    val guessMimeTypeFromExtension = MimeUtils.guessMimeTypeFromExtension(cacheFile.extension)
    val intent = Intent(Intent.ACTION_SEND)
    intent.apply {
        packageName?.let { setPackage(packageName) }
        type = guessMimeTypeFromExtension
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra(Intent.EXTRA_STREAM, this@share)
    }
    context.startActivity(Intent.createChooser(intent, cacheFile.nameWithoutExtension))
}

fun File.share(context: Context, packageName: String? = null) {
    val uri =
        FileProvider.getUriForFile(context, context.packageName + ".fileProvider", this)
    val guessMimeTypeFromExtension = MimeUtils.guessMimeTypeFromExtension(extension)
    val intent = Intent(Intent.ACTION_SEND)
    intent.apply {
        packageName?.let { setPackage(packageName) }
        type = guessMimeTypeFromExtension
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra(Intent.EXTRA_STREAM, uri)
    }
    context.startActivity(Intent.createChooser(intent, nameWithoutExtension))
}

fun Context.openFile(path: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    val file = File(path)
    val mydir = FileProvider.getUriForFile(this, "$packageName.fileProvider", file)
    Log.e("TAG", mydir.toString())
    intent.setDataAndType(mydir, MimeUtils.guessMimeTypeFromExtension(file.extension))
    startActivity(intent)
}

fun List<File>.sharePicFile(context: Context, packageName: String? = null) {
    val uriList = map {
        FileProvider.getUriForFile(
            context,
            context.packageName + ".fileProvider",
            it
        )
    }
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
    intent.apply {
        packageName?.let { setPackage(packageName) }
        type = "image/*"
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList as ArrayList<out Parcelable>)
    }
    context.startActivity(Intent.createChooser(intent, "批量分享"))
}

fun List<Uri>.sharePicUri(context: Context, packageName: String? = null) {
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
    intent.apply {
        packageName?.let { setPackage(packageName) }
        type = "image/*"
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        putParcelableArrayListExtra(
            Intent.EXTRA_STREAM,
            this@sharePicUri as ArrayList<out Parcelable>
        )
    }
    context.startActivity(Intent.createChooser(intent, "批量分享"))
}

fun List<File>.moveToPublicDir(context: Context, parentDir: String, childDir: String): List<Uri?> {
    val saveSuccess = mutableListOf<Uri?>()
    forEach {
        saveSuccess.add(it.moveToPublicDir(context, parentDir, childDir))
    }
    return saveSuccess
}

fun File.moveToPublicDir(context: Context, parentDir: String, childDir: String): Uri? {
    val mimeType = MimeUtils.guessMimeTypeFromExtension(extension)
    var uri: Uri? = null

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues().apply {
                put(MediaStore.Images.ImageColumns.DISPLAY_NAME, name)
                put(MediaStore.Images.ImageColumns.MIME_TYPE, mimeType)
                put(MediaStore.Images.ImageColumns.TITLE, name)
                put(
                    MediaStore.Images.ImageColumns.RELATIVE_PATH,
                    "$parentDir${File.separator}$childDir"
                )

                FileInputStream(this@moveToPublicDir).use { inputStream ->
                    val insertUri = context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        this@apply
                    )
                    if (null != insertUri) {
                        context.contentResolver.openOutputStream(insertUri)?.use { outputStream ->
                            val buffer = ByteArray(4096)
                            var byteCount: Int
                            while (inputStream.read(buffer).also { byteCount = it } != -1) {
                                outputStream.write(buffer, 0, byteCount)
                            }
                            uri = insertUri
                        }
                    }
                }
            }
        } else {
            val dstFilePath =
                "${Environment.getExternalStoragePublicDirectory(parentDir)}${File.separator}${name}"
            if (renameTo(File(dstFilePath))) {
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(dstFilePath),
                    arrayOf(mimeType),
                    null
                )
                uri = FileProvider.getUriForFile(
                    context,
                    context.packageName + ".fileProvider",
                    File(dstFilePath)
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return uri
}

fun File.moveToPublicFileDir(context: Context, parentDir: String, childDir: String): Uri? {
    val mimeType = MimeUtils.guessMimeTypeFromExtension(extension)
    var uri: Uri? = null

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues().apply {
                put(MediaStore.DownloadColumns.DISPLAY_NAME, name)
                put(MediaStore.DownloadColumns.MIME_TYPE, mimeType)
                put(MediaStore.DownloadColumns.TITLE, name)
                put(MediaStore.Downloads.RELATIVE_PATH, "$parentDir${File.separator}$childDir")

                FileInputStream(this@moveToPublicFileDir).use { inputStream ->
                    val insertUri = context.contentResolver.insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                        this@apply
                    )
                    if (null != insertUri) {
                        context.contentResolver.openOutputStream(insertUri)?.use { outputStream ->
                            val buffer = ByteArray(4096)
                            var byteCount: Int
                            while (inputStream.read(buffer).also { byteCount = it } != -1) {
                                outputStream.write(buffer, 0, byteCount)
                            }
                            uri = insertUri
                        }
                    }
                }
            }
        } else {
            val dstFilePath =
                "${Environment.getExternalStoragePublicDirectory(parentDir)}${File.separator}${name}"
            if (FileUtils.copy(path, dstFilePath)) {
                uri = FileProvider.getUriForFile(
                    context,
                    context.packageName + ".fileProvider",
                    File(dstFilePath)
                )
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(dstFilePath),
                    arrayOf(mimeType),
                    null
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return uri
}


fun Uri.findFileName(context: Context): String {
    return runCatching {
        var size = ""
        context.contentResolver.query(
            this,
            arrayOf(MediaStore.Files.FileColumns.DISPLAY_NAME),
            null,
            null,
            null
        )?.use {
            val pathIndex = it.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
            it.moveToFirst()
            size = it.getStringOrNull(pathIndex) ?: ""
        }
        size
    }.getOrElse { "" }
}


/**
 * 复制图片文件到相册的Pictures文件夹
 *
 * @param context 上下文
 * @param fileName 文件名。 需要携带后缀
 * @param relativePath 相对于Pictures的路径
 */
fun File.copyToAlbum(context: Context, fileName: String, relativePath: String?): Uri? {
    if (!this.canRead() || !this.exists()) {
        Log.w(TAG, "check: read file error: $this")
        return null
    }
    return this.inputStream().use {
        it.saveToAlbum(context, fileName, relativePath)
    }
}

/**
 * 保存图片Stream到相册的Pictures文件夹
 *
 * @param context 上下文
 * @param fileName 文件名，需要携带后缀
 * @param relativePath 相对于Pictures的路径
 */
fun InputStream.saveToAlbum(context: Context, fileName: String, relativePath: String?): Uri? {
    val resolver = context.contentResolver
    val outputFile = OutputFileTaker()
    val imageUri = resolver.insertMediaImage(fileName, relativePath, outputFile)
    if (imageUri == null) {
        Log.w(TAG, "insert: error: uri == null")
        return null
    }

    (imageUri.outputStream(resolver) ?: return null).use { output ->
        this.use { input ->
            input.copyTo(output)
            imageUri.finishPending(context, resolver, outputFile.file)
        }
    }
    return imageUri
}

/**
 * 保存Bitmap到相册的Pictures文件夹
 *
 * https://developer.android.google.cn/training/data-storage/shared/media
 *
 * @param context 上下文
 * @param fileName 文件名，需要携带后缀
 * @param relativePath 相对于Pictures的路径
 * @param quality 质量
 */
fun Bitmap.saveToAlbum(
    context: Context,
    fileName: String,
    relativePath: String? = null,
    quality: Int = 100,
): Uri? {
    // 插入图片信息
    val resolver = context.contentResolver
    val outputFile = OutputFileTaker()
    val imageUri = resolver.insertMediaImage(fileName, relativePath, outputFile)
    if (imageUri == null) {
        Log.w(TAG, "insert: error: uri == null")
        return null
    }

    // 保存图片
    (imageUri.outputStream(resolver) ?: return null).use {
        val format = fileName.getBitmapFormat()
        this@saveToAlbum.compress(format, quality, it)
        imageUri.finishPending(context, resolver, outputFile.file)
    }
    return imageUri
}

private fun Uri.outputStream(resolver: ContentResolver): OutputStream? {
    return try {
        resolver.openOutputStream(this)
    } catch (e: FileNotFoundException) {
        Log.e(TAG, "save: open stream error: $e")
        null
    }
}

private fun Uri.finishPending(
    context: Context,
    resolver: ContentResolver,
    outputFile: File?,
) {
    val imageValues = ContentValues()
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        if (outputFile != null) {
            imageValues.put(MediaStore.Images.Media.SIZE, outputFile.length())
        }
        resolver.update(this, imageValues, null, null)
        // 通知媒体库更新
        val intent = Intent(@Suppress("DEPRECATION") Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, this)
        context.sendBroadcast(intent)
    } else {
        // Android Q添加了IS_PENDING状态，为0时其他应用才可见
        imageValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(this, imageValues, null, null)
    }
}

@Suppress("DEPRECATION")
private fun String.getBitmapFormat(): Bitmap.CompressFormat {
    val fileName = this.lowercase()
    return when {
        fileName.endsWith(".png") -> Bitmap.CompressFormat.PNG
        fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") -> Bitmap.CompressFormat.JPEG
        fileName.endsWith(".webp") -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            Bitmap.CompressFormat.WEBP_LOSSLESS else Bitmap.CompressFormat.WEBP
        else -> Bitmap.CompressFormat.PNG
    }
}

private fun String.getMimeType(): String? {
    val fileName = this.lowercase()
    return when {
        fileName.endsWith(".png") -> "image/png"
        fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") -> "image/jpeg"
        fileName.endsWith(".webp") -> "image/webp"
        fileName.endsWith(".gif") -> "image/gif"
        else -> null
    }
}

/**
 * 插入图片到媒体库
 */
private fun ContentResolver.insertMediaImage(
    fileName: String,
    relativePath: String?,
    outputFileTaker: OutputFileTaker? = null,
): Uri? {
    // 图片信息
    val imageValues = ContentValues().apply {
        val mimeType = fileName.getMimeType()
        if (mimeType != null) {
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        }
        val date = System.currentTimeMillis() / 1000
        put(MediaStore.Images.Media.DATE_ADDED, date)
        put(MediaStore.Images.Media.DATE_MODIFIED, date)
    }
    // 保存的位置
    val collection: Uri
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val path = if (relativePath != null) "$ALBUM_DIR/${relativePath}" else ALBUM_DIR
        imageValues.apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.RELATIVE_PATH, path)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        // 高版本不用查重直接插入，会自动重命名
    } else {
        // 老版本
        val pictures =
            @Suppress("DEPRECATION") Environment.getExternalStoragePublicDirectory(ALBUM_DIR)
        val saveDir = if (relativePath != null) File(pictures, relativePath) else pictures

        if (!saveDir.exists() && !saveDir.mkdirs()) {
            Log.e(TAG, "save: error: can't create Pictures directory")
            return null
        }

        // 文件路径查重，重复的话在文件名后拼接数字
        var imageFile = File(saveDir, fileName)
        val fileNameWithoutExtension = imageFile.nameWithoutExtension
        val fileExtension = imageFile.extension

        var queryUri = this.queryMediaImage28(imageFile.absolutePath)
        var suffix = 1
        while (queryUri != null) {
            val newName = fileNameWithoutExtension + "(${suffix++})." + fileExtension
            imageFile = File(saveDir, newName)
            queryUri = this.queryMediaImage28(imageFile.absolutePath)
        }

        imageValues.apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.name)
            // 保存路径
            val imagePath = imageFile.absolutePath
            Log.v(TAG, "save file: $imagePath")
            put(@Suppress("DEPRECATION") MediaStore.Images.Media.DATA, imagePath)
        }
        outputFileTaker?.file = imageFile // 回传文件路径，用于设置文件大小
        collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
    // 插入图片信息
    return this.insert(collection, imageValues)
}

/**
 * Android Q以下版本，查询媒体库中当前路径是否存在
 * @return Uri 返回null时说明不存在，可以进行图片插入逻辑
 */
private fun ContentResolver.queryMediaImage28(imagePath: String): Uri? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return null

    val imageFile = File(imagePath)
    if (imageFile.canRead() && imageFile.exists()) {
        Log.v(TAG, "query: path: $imagePath exists")
        // 文件已存在，返回一个file://xxx的uri
        return Uri.fromFile(imageFile)
    }
    // 保存的位置
    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    // 查询是否已经存在相同图片
    val query = this.query(
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
 */
fun saveVideoToGallery(context: Context, videoFile: String): Boolean {
    Log.d(TAG, "saveVideoToAlbum() videoFile = [$videoFile]")
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        saveVideoToAlbumBeforeQ(context, videoFile)
    } else {
        saveVideoToAlbumAfterQ(context, videoFile)
    }
}

@Suppress("DEPRECATION")
private fun saveVideoToAlbumAfterQ(context: Context, videoFile: String): Boolean {
    return try {
        val contentResolver = context.contentResolver
        val tempFile = File(videoFile)
        val contentValues = getVideoContentValues(context, tempFile, System.currentTimeMillis())
        val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
        copyFileAfterQ(context, contentResolver, tempFile, uri)
        contentValues.clear()
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        context.contentResolver.update(uri!!, contentValues, null, null)
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

private fun saveVideoToAlbumBeforeQ(context: Context, videoFile: String): Boolean {
    val picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
    val tempFile = File(videoFile)
    val destFile = File(picDir, context.packageName + File.separator + tempFile.name)
    var ins: FileInputStream? = null
    var ous: BufferedOutputStream? = null
    return try {
        ins = FileInputStream(tempFile)
        ous = BufferedOutputStream(FileOutputStream(destFile))
        var nread = 0L
        val buf = ByteArray(1024)
        var n: Int
        while (ins.read(buf).also { n = it } > 0) {
            ous.write(buf, 0, n)
            nread += n.toLong()
        }
        MediaScannerConnection.scanFile(
            context, arrayOf(destFile.absolutePath), arrayOf("video/*")
        ) { path: String, uri: Uri ->
            Log.d(TAG, "saveVideoToAlbum: $path $uri")
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    } finally {
        try {
            ins?.close()
            ous?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

private fun copyFileAfterQ(
    context: Context,
    localContentResolver: ContentResolver,
    tempFile: File,
    localUri: Uri?
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
        context.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.Q
    ) {
        // 拷贝文件到相册的uri,android10及以上得这么干，否则不会显示。可以参考ScreenMediaRecorder的save方法
        val os = localContentResolver.openOutputStream(localUri!!)
        Files.copy(tempFile.toPath(), os)
        os!!.close()
        tempFile.delete()
    }
}


/**
 * 获取视频的contentValue
 */
private fun getVideoContentValues(context: Context, paramFile: File, timestamp: Long): ContentValues {
    val localContentValues = ContentValues()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        localContentValues.put(
            MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM
                    + File.separator + context.packageName
        )
    }
    localContentValues.put(MediaStore.Video.Media.TITLE, paramFile.name)
    localContentValues.put(MediaStore.Video.Media.DISPLAY_NAME, paramFile.name)
    localContentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
    localContentValues.put(MediaStore.Video.Media.DATE_TAKEN, timestamp)
    localContentValues.put(MediaStore.Video.Media.DATE_MODIFIED, timestamp)
    localContentValues.put(MediaStore.Video.Media.DATE_ADDED, timestamp)
    localContentValues.put(MediaStore.Video.Media.SIZE, paramFile.length())
    return localContentValues
}