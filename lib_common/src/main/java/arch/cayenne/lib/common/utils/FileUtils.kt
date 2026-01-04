package arch.cayenne.lib.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {


    /**
     * 保存 Bitmap 到本地，自动修正方向，支持压缩质量设置
     * @param bitmap 要保存的 Bitmap
     * @param quality 压缩质量（0-100）
     * @return 保存的文件路径，若失败则返回 null
     */
    fun saveBitmapToFile(context: Context, bitmap: Bitmap, quality: Int = 50): String? {
        return try {
            val fileName = "circular_image.png"
            val directory = File(context.getExternalFilesDir("Pictures"), "CircularImages")
            if (!directory.exists()) directory.mkdirs()
            val file = File(directory, fileName)
            if (file.exists()) file.delete()
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 从文件路径加载 Bitmap，并自动修正方向
     */
    fun getCorrectedBitmap(filePath: String): Bitmap? {
        val bitmap = BitmapFactory.decodeFile(filePath) ?: return null
        val exif = try {
            ExifInterface(filePath)
        } catch (e: Exception) {
            null
        }
        val orientation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
        ) ?: ExifInterface.ORIENTATION_NORMAL
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).also {
            if (it != bitmap) bitmap.recycle()
        }
    }

    fun getBitmapFromUriAsync(context: Context, uri: Uri, callback: (Bitmap?) -> Unit) {
        Thread {
            val bitmap = try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val originalBitmap = BitmapFactory.decodeStream(inputStream)
                    // 读取 EXIF 信息修正方向
                    val exif =
                        context.contentResolver.openInputStream(uri)?.use { ExifInterface(it) }
                    val orientation = exif?.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
                    ) ?: ExifInterface.ORIENTATION_NORMAL
                    val matrix = Matrix()
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    }
                    if (matrix.isIdentity || originalBitmap == null) {
                        originalBitmap
                    } else {
                        Bitmap.createBitmap(
                            originalBitmap, 0, 0,
                            originalBitmap.width, originalBitmap.height, matrix, true
                        ).also { if (it != originalBitmap) originalBitmap.recycle() }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                callback(bitmap)
            }
        }.start()
    }


    /**
     * 加载本地保存的 Bitmap 并显示
     */
    fun loadLocalBitmap(context: Context): Bitmap? {
        return try {
            val fileName = "circular_image.png"
            val file = File(
                context.getExternalFilesDir("Pictures")?.absolutePath + "/CircularImages",
                fileName
            )
            if (!file.exists()) return null
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            null
        }
    }
}