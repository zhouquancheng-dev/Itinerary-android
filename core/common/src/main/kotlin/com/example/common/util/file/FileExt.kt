package com.example.common.util.file

import android.app.Activity
import android.app.Application
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.core.net.toFile
import com.example.common.util.mime.MimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.*
import java.nio.file.Files
import java.util.Calendar
import java.util.Locale
import java.util.zip.ZipException
import java.util.zip.ZipFile

private const val TAG = "FileExt"

private val ALBUM_DIR = Environment.DIRECTORY_PICTURES

private class OutputFileTaker(var file: File? = null)

/**
 * Storage and File System Operations
 */

/**
 * Gets the application's base folder.
 */
val Context.baseFolder: File
    get() = getExternalFilesDir(null) ?: filesDir

/**
 * Gets the temporary cache folder.
 */
val Context.tempCacheFolder: File
    get() = File(baseFolder, "tempCache").apply { createOrExistsDir() }

/**
 * Creates a directory if it doesn't exist.
 * @return true if the directory was created or already exists; false otherwise.
 */
fun File.createOrExistsDir(): Boolean = if (exists()) isDirectory else mkdirs()

/**
 * Creates a file if it doesn't exist.
 * @return true if the file was created or already exists; false otherwise.
 */
fun File.createOrExistsFile(): Boolean = if (exists()) {
    isFile
} else {
    parentFile?.createOrExistsDir() == true && createNewFile()
}

/**
 * Opens a file with the appropriate app based on MIME type.
 * @param path The file path to open
 */
@OptIn(ExperimentalStdlibApi::class)
fun Context.openFile(path: String) {
    try {
        val file = File(path)

        // Check if file exists and is readable
        if (!file.exists()) {
            Log.e(TAG, "File does not exist: $path")
            Toast.makeText(this, "File not found: ${file.name}", Toast.LENGTH_SHORT).show()
            return
        }

        if (!file.canRead()) {
            Log.e(TAG, "File is not readable: $path")
            Toast.makeText(this, "Cannot read file: ${file.name}", Toast.LENGTH_SHORT).show()
            return
        }

        // Get MIME type based on file extension
        val extension = file.extension.lowercase()
        val mimeType = MimeUtils.guessMimeTypeFromExtension(extension) ?: "*/*"

        Log.d(TAG, "Opening file: $path with MIME type: $mimeType")

        val fileUri = FileProvider.getUriForFile(this, "$packageName.fileProvider", file)
        Log.d(TAG, "File URI: $fileUri")

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Check if there's an app that can handle this file type
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Log.e(TAG, "No app found to handle MIME type: $mimeType")
            Toast.makeText(this, "No app found to open this file type", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error opening file: ${e.message}", e)
        Toast.makeText(this, "Error opening file: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

/**
 * URI Operations
 */

/**
 * Gets the filename from a URI.
 */
val Uri.fileName: String
    get() = Uri.decode(path).toString().substringAfterLast("/")

/**
 * Gets the filename from a URI using ContentResolver.
 * @param context Context for ContentResolver
 * @return Filename or empty string if not found
 */
fun Uri.findFileName(context: Context): String {
    return runCatching {
        context.contentResolver.query(this, arrayOf(MediaStore.Files.FileColumns.DISPLAY_NAME), null, null, null)?.use {
            val nameIndex = it.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                it.getStringOrNull(nameIndex) ?: ""
            } else ""
        } ?: ""
    }.getOrElse { "" }
}

/**
 * Finds the absolute path of a URI.
 * @param context Context for ContentResolver
 * @return Absolute path or empty string if not found
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
 * Finds the file size of a URI.
 * @param context Context for ContentResolver
 * @return File size in bytes or 0 if not found
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

/**
 * Loads a Bitmap from the given Uri, compatible with all API levels.
 * Returns a mutable Bitmap if possible.
 *
 * @receiver Uri The Uri of the image to load.
 * @param context Context used to access ContentResolver.
 * @return Bitmap? The loaded Bitmap if successful, otherwise null.
 */
suspend fun Uri.loadBitmap(context: Context): Bitmap? {
    return withContext(Dispatchers.Default) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, this@loadBitmap)
                return@withContext ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                }
            } else {
                context.contentResolver.openInputStream(this@loadBitmap)?.use {
                    return@withContext BitmapFactory.decodeStream(it)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap", e)
            null
        }
    }
}

/**
 * Sharing Operations
 */

/**
 * Shares a URI file.
 * @param context Context for sharing
 * @param cacheFile Cache file for sharing
 * @param packageName Target package name (optional)
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
 * Shares a file.
 * @param context Context for sharing
 * @param packageName Target package name (optional)
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

/**
 * Shares multiple image files.
 * @param context Context for sharing
 * @param packageName Target package name (optional)
 */
fun List<File>.sharePicFiles(context: Context, packageName: String? = null) {
    val uriList = map {
        FileProvider.getUriForFile(context, "${context.packageName}.fileProvider", it)
    }
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        packageName?.let { setPackage(it) }
        type = "image/*"
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uriList))
    }
    context.startActivity(Intent.createChooser(intent, "Share"))
}

/**
 * Shares multiple image URIs.
 * @param context Context for sharing
 * @param packageName Target package name (optional)
 */
fun List<Uri>.sharePicUris(context: Context, packageName: String? = null) {
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        packageName?.let { setPackage(it) }
        type = "image/*"
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(this@sharePicUris))
    }
    context.startActivity(Intent.createChooser(intent, "Share"))
}

/**
 * Media Storage Operations
 */

/**
 * Moves multiple files to a public directory.
 * @param context Context for ContentResolver
 * @param parentDir Parent directory in public storage
 * @param childDir Child directory in public storage
 * @return List of URIs for the moved files
 */
fun List<File>.moveToPublicDir(context: Context, parentDir: String, childDir: String): List<Uri?> {
    return map { it.moveToPublicDir(context, parentDir, childDir) }
}

/**
 * Moves a file to a public directory.
 * @param context Context for ContentResolver
 * @param parentDir Parent directory in public storage
 * @param childDir Child directory in public storage
 * @return URI of the moved file
 */
fun File.moveToPublicDir(context: Context, parentDir: String, childDir: String): Uri? {
    val mimeType = MimeUtils.guessMimeTypeFromExtension(extension)
    var resultUri: Uri? = null

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android Q and above
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
                        inputStream.copyTo(outputStream)
                        resultUri = uri
                    }
                }
            }
        } else {
            // Pre-Android Q
            val dstFilePath = "${Environment.getExternalStoragePublicDirectory(parentDir)}${File.separator}$name"
            if (renameTo(File(dstFilePath))) {
                MediaScannerConnection.scanFile(context, arrayOf(dstFilePath), arrayOf(mimeType), null)
                resultUri = FileProvider.getUriForFile(context, "${context.packageName}.fileProvider", File(dstFilePath))
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error moving file to public directory", e)
    }

    return resultUri
}

/**
 * Moves a file to a public directory, returns success status.
 * @param context Context for ContentResolver
 * @param parentDir Parent directory in public storage
 * @param childDir Child directory in public storage
 * @return true if successful, false otherwise
 */
fun File.moveToPublicDirWithStatus(context: Context, parentDir: String, childDir: String): Boolean {
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
                        inputStream.copyTo(outputStream)
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
        Log.e(TAG, "Error moving file to public directory", e)
        moveSuccess = false
    }

    return moveSuccess
}

/**
 * Image Operations
 */

// Temporarily save Bitmap to cache directory
private fun Context.saveBitmapToCache(bitmap: Bitmap): File {
    val tempFile = File(tempCacheFolder, "shared_image.png")
    FileOutputStream(tempFile).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    }
    return tempFile
}

// Register lifecycle callback to automatically delete file after sharing
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
 * Copies an image file to the album's Pictures folder.
 * @param context Context for ContentResolver
 * @param fileName Filename (with extension)
 * @param relativePath Path relative to Pictures
 * @param deleteSource Whether to delete the source file after copying (default: true)
 * @return URI of the saved image
 */
fun File.copyToAlbum(
    context: Context,
    fileName: String,
    relativePath: String?,
    deleteSource: Boolean = true
): Uri? {
    if (!this.canRead() || !this.exists()) {
        Log.w(TAG, "File check failed: cannot read or doesn't exist: $this")
        return null
    }
    val uri = inputStream().use { it.saveToAlbum(context, fileName, relativePath) }
    
    // Delete source file if requested and the copy was successful
    if (deleteSource && uri != null && this.exists()) {
        if (this.delete()) {
            Log.d(TAG, "Source file deleted after copying to album: $this")
        } else {
            Log.w(TAG, "Failed to delete source file after copying to album: $this")
        }
    }
    
    return uri
}

/**
 * Saves an image input stream to the album's Pictures folder.
 * @param context Context for ContentResolver
 * @param fileName Filename (with extension)
 * @param relativePath Path relative to Pictures
 * @return URI of the saved image
 */
fun InputStream.saveToAlbum(context: Context, fileName: String, relativePath: String?): Uri? {
    val resolver = context.contentResolver
    val outputFile = OutputFileTaker()
    val imageUri = resolver.insertMediaImage(fileName, relativePath, outputFile)
    if (imageUri == null) {
        Log.w(TAG, "Insert media image failed: uri is null")
        return null
    }

    // Save the image
    imageUri.outputStream(resolver)?.use { output ->
        this.use { input ->
            input.copyTo(output)
            imageUri.finishPending(context, resolver, outputFile.file)
        }
    }
    return imageUri
}

/**
 * Saves a Bitmap to the album's Pictures folder.
 * @param context Context for ContentResolver
 * @param fileName Filename (with extension)
 * @param relativePath Path relative to Pictures
 * @param quality Image quality (0-100)
 * @return URI of the saved image
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
        Log.w(TAG, "Insert media image failed: uri is null")
        return null
    }

    // Save the Bitmap
    imageUri.outputStream(resolver)?.use {
        val format = fileName.getBitmapFormat()
        this.compress(format, quality, it)
        imageUri.finishPending(context, resolver, outputFile.file)
    }
    return imageUri
}

/**
 * Gets the output stream for a URI.
 * @param resolver ContentResolver
 * @return OutputStream or null if not found
 */
private fun Uri.outputStream(resolver: ContentResolver): OutputStream? {
    return try {
        resolver.openOutputStream(this)
    } catch (e: FileNotFoundException) {
        Log.e(TAG, "Error opening output stream for URI: $e")
        null
    }
}

/**
 * Completes the pending operation and updates media library status.
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
        // Notify media scanner
        val intent = @Suppress("DEPRECATION") Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, this)
        context.sendBroadcast(intent)
    } else {
        // For Android Q and above, set IS_PENDING to 0 to make visible
        imageValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(this, imageValues, null, null)
    }
}

/**
 * Gets the bitmap compression format based on filename.
 * @return Compression format
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
 * Gets the MIME type based on filename.
 * @return MIME type or null if unknown
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
 * Inserts an image into the media library.
 * @param fileName Filename
 * @param relativePath Relative path
 * @param outputFileTaker Object to store output file
 * @return URI of the inserted image
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
        // For Android versions below Q
        val pictures = @Suppress("DEPRECATION") Environment.getExternalStoragePublicDirectory(ALBUM_DIR)
        val saveDir = relativePath?.let { File(pictures, it) } ?: pictures
        if (!saveDir.exists() && !saveDir.mkdirs()) {
            Log.e(TAG, "Error: Can't create Pictures directory")
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
 * Queries the media database for an image path (for Android pre-Q).
 * @return URI if found, null otherwise
 */
private fun ContentResolver.queryMediaImage28(imagePath: String): Uri? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return null

    val imageFile = File(imagePath)
    if (imageFile.canRead() && imageFile.exists()) {
        Log.v(TAG, "Path exists: $imagePath")
        // File exists, return a file:// URI
        return Uri.fromFile(imageFile)
    }

    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    query(
        collection,
        arrayOf(MediaStore.Images.Media._ID, @Suppress("DEPRECATION") MediaStore.Images.Media.DATA),
        "${@Suppress("DEPRECATION") MediaStore.Images.Media.DATA} == ?",
        arrayOf(imagePath), null
    )?.use {
        while (it.moveToNext()) {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val id = it.getLong(idColumn)
            val existsUri = ContentUris.withAppendedId(collection, id)
            Log.v(TAG, "Path exists in media database: $imagePath, URI: $existsUri")
            return existsUri
        }
    }
    return null
}

/**
 * Video Operations
 */

/**
 * Saves a video to the system gallery.
 * @param context Context for ContentResolver
 * @param videoFile Video file path
 * @param deleteSource Whether to delete the source file (default: true)
 * @return true if successful, false otherwise
 */
fun saveVideoToGallery(context: Context, videoFile: String, deleteSource: Boolean = true): Boolean {
    Log.d(TAG, "Saving video to gallery: $videoFile")
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        saveVideoToGalleryLegacy(context, videoFile, deleteSource)
    } else {
        saveVideoToGalleryModern(context, videoFile, deleteSource)
    }
}

/**
 * Saves a video to the system gallery for Android Q and above.
 * @param context Context for ContentResolver
 * @param videoFile Video file path
 * @param deleteSource Whether to delete the source file
 * @return true if successful, false otherwise
 */
@Suppress("DEPRECATION")
private fun saveVideoToGalleryModern(context: Context, videoFile: String, deleteSource: Boolean): Boolean {
    return try {
        val contentResolver = context.contentResolver
        val tempFile = File(videoFile)
        val contentValues = getVideoContentValues(context, tempFile, System.currentTimeMillis())
        val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)

        // Copy file to new URI
        copyFileModern(context, contentResolver, tempFile, uri, deleteSource)

        // Update URI status to visible
        contentValues.clear()
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        contentResolver.update(uri!!, contentValues, null, null)

        // Send media scan broadcast
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error saving video to gallery", e)
        false
    }
}

/**
 * Saves a video to the system gallery for Android pre-Q.
 * @param context Context for ContentResolver
 * @param videoFile Video file path
 * @param deleteSource Whether to delete the source file
 * @return true if successful, false otherwise
 */
private fun saveVideoToGalleryLegacy(context: Context, videoFile: String, deleteSource: Boolean): Boolean {
    val picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
    val tempFile = File(videoFile)
    val destFile = File(picDir, "${context.packageName}${File.separator}${tempFile.name}")

    return try {
        // Copy file to destination
        FileInputStream(tempFile).use { ins ->
            destFile.parentFile?.mkdirs()
            FileOutputStream(destFile).use { fos ->
                BufferedOutputStream(fos).use { ous ->
                    ins.copyTo(ous)
                }
            }
        }

        // Delete source file if requested
        if (deleteSource && tempFile.exists()) {
            tempFile.delete()
        }

        // Notify media scanner
        MediaScannerConnection.scanFile(context, arrayOf(destFile.absolutePath), arrayOf("video/*")) { path, uri ->
            Log.d(TAG, "Video scan complete: $path $uri")
        }
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error saving video to gallery", e)
        false
    }
}

/**
 * Copies a file to a URI location for Android Q and above.
 * @param context Context for ContentResolver
 * @param contentResolver ContentResolver
 * @param tempFile Source file
 * @param uri Destination URI
 * @param deleteSource Whether to delete the source file
 */
private fun copyFileModern(
    context: Context,
    contentResolver: ContentResolver,
    tempFile: File,
    uri: Uri?,
    deleteSource: Boolean
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
        context.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.Q
    ) {
        uri?.let {
            // Copy file to destination URI
            contentResolver.openOutputStream(it)?.use { outputStream ->
                Files.copy(tempFile.toPath(), outputStream)
            }
            if (deleteSource && tempFile.exists()) {
                tempFile.delete() // Delete temporary file
            }
        }
    }
}

/**
 * Gets ContentValues for a video file.
 * @param context Context for package name
 * @param file Video file
 * @param timestamp Time in milliseconds
 * @return ContentValues for media store
 */
private fun getVideoContentValues(context: Context, file: File, timestamp: Long): ContentValues {
    return ContentValues().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + context.packageName)
        }
        put(MediaStore.Video.Media.TITLE, file.name)
        put(MediaStore.Video.Media.DISPLAY_NAME, file.name)
        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        put(MediaStore.Video.Media.DATE_TAKEN, timestamp)
        put(MediaStore.Video.Media.DATE_MODIFIED, timestamp)
        put(MediaStore.Video.Media.DATE_ADDED, timestamp)
        put(MediaStore.Video.Media.SIZE, file.length())
    }
}

/**
 * File Type Checking and Utilities
 */

/**
 * Converts byte size to human-readable size format with specified precision.
 * @param precision Decimal precision (default: 2)
 * @param useBinaryPrefix Whether to use binary prefix (1024) and units (KiB, MiB, GiB) instead of
 *                        decimal prefix (1000) and units (KB, MB, GB)
 * @return Formatted size string with appropriate unit
 */
fun Long.byteToFitMemorySize(precision: Int = 2, useBinaryPrefix: Boolean = false): String {
    require(precision >= 0) { "Precision must be non-negative" }
    if (this < 0) throw IllegalArgumentException("Byte size cannot be negative")

    // Use the existing extension properties based on useBinaryPrefix
    val kb = 1.toKB(useBinaryPrefix)
    val mb = 1.toMB(useBinaryPrefix)
    val gb = 1.toGB(useBinaryPrefix)

    // Choose the appropriate unit symbols
    val kbUnit = if (useBinaryPrefix) "KiB" else "KB"
    val mbUnit = if (useBinaryPrefix) "MiB" else "MB"
    val gbUnit = if (useBinaryPrefix) "GiB" else "GB"

    return when {
        this < kb -> String.format(Locale.getDefault(), "%dB", this)
        this < mb -> String.format(Locale.getDefault(), "%.${precision}f%s", toDouble() / kb, kbUnit)
        this < gb -> String.format(Locale.getDefault(), "%.${precision}f%s", toDouble() / mb, mbUnit)
        else -> String.format(Locale.getDefault(), "%.${precision}f%s", toDouble() / gb, gbUnit)
    }
}

// Size constants with binary option
val Int.BYTE: Int
    get() = this

val Int.KB: Int
    get() = 1000 * BYTE

val Int.MB: Int
    get() = 1000 * KB

val Int.GB: Int
    get() = 1000 * MB

// Additional binary-based extension functions
fun Int.toKB(binary: Boolean): Int = this * if (binary) 1024 else 1000
fun Int.toMB(binary: Boolean): Int = this.toKB(binary) * if (binary) 1024 else 1000
fun Int.toGB(binary: Boolean): Int = this.toMB(binary) * if (binary) 1024 else 1000

/**
 * Checks if a file is a valid image by attempting to decode it.
 * @return true if the file is a valid image, false otherwise
 */
fun File.isValidImage(): Boolean {
    return try {
        if (!exists() || !canRead()) {
            return false
        }

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true // Only decode bounds, not the actual bitmap
        }

        BitmapFactory.decodeFile(absolutePath, options)

        // If the width and height are > 0, it's likely a valid image
        options.outWidth > 0 && options.outHeight > 0
    } catch (e: Exception) {
        Log.e(TAG, "Error checking if file is valid image: ${e.message}", e)
        false
    }
}

/**
 * Checks if a file is a valid ZIP file by attempting to open it.
 * @return true if the file is a valid ZIP file, false otherwise
 */
fun File.isValidZipFile(): Boolean {
    return try {
        if (!exists() || !canRead()) {
            return false
        }

        ZipFile(this).use { zipFile ->
            zipFile.entries().hasMoreElements() // Just check if we can read the ZIP structure
        }
        true
    } catch (e: ZipException) {
        Log.e(TAG, "Not a valid ZIP file: ${e.message}", e)
        false
    } catch (e: Exception) {
        Log.e(TAG, "Error checking if file is valid ZIP: ${e.message}", e)
        false
    }
}

/**
 * Checks if a file is an archive file based on its extension and MIME type.
 * @return true if the file is an archive file, false otherwise
 */
@OptIn(ExperimentalStdlibApi::class)
fun File.isArchiveFile(): Boolean {
    if (!exists() || !canRead()) {
        return false
    }

    val extension = extension.lowercase()
    val mimeType = MimeUtils.guessMimeTypeFromExtension(extension)

    // Common archive extensions
    val archiveExtensions = listOf("zip", "rar", "7z", "tar", "gz", "bz2", "xz", "tgz")

    // Common archive MIME types
    val archiveMimeTypes = listOf(
        "application/zip",
        "application/x-rar-compressed",
        "application/x-7z-compressed",
        "application/x-tar",
        "application/gzip",
        "application/x-bzip2",
        "application/x-xz"
    )

    return extension in archiveExtensions || mimeType in archiveMimeTypes
}

/**
 * Checks if a file can be extracted with Zip4j.
 * This includes both regular and password-protected ZIP files.
 *
 * @return true if the file is a valid ZIP file that can be extracted with Zip4j
 */
fun File.canExtractWithZip4j(): Boolean {
    if (!exists() || !canRead()) {
        return false
    }

    // First check the extension to avoid unnecessary operations
    @OptIn(ExperimentalStdlibApi::class)
    val extension = extension.lowercase()
    if (extension != "zip") {
        return false
    }

    // Then try to open it as a ZIP file
    return try {
        val zipFile = net.lingala.zip4j.ZipFile(this)
        // If we can get the file headers, it's a valid ZIP
        zipFile.fileHeaders
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error checking if file can be extracted with Zip4j: ${e.message}", e)
        false
    }
}