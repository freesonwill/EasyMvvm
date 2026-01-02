package com.walisport.module.misc.util

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * @author: wenxi
 * @date: 18/12/25 16:20
 * @description:
 */
object GallerySaveManager {
    /**
     * 保存Base64图片到相册
     * @param base64Data Base64编码的图片数据（可包含前缀）
     * @param context Context
     * @param fileName 文件名（可选，不包含扩展名）
     * @return 保存结果：成功返回文件路径，失败返回null
     */
    fun saveBase64ToGallery(
        context: Context,
        base64Data: String,
    ): String? {
        return try {
            // 解码Base64
            val bitmap = decodeBase64ToBitmap(base64Data) ?: return null
            val fileName = "invite-share-${System.currentTimeMillis()}.png"

            // 根据Android版本选择保存方式
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveToGalleryQ(context, bitmap, fileName)
            } else {
                saveToGalleryLegacy(context, bitmap, fileName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 解码Base64字符串为Bitmap
     */
    private fun decodeBase64ToBitmap(base64Data: String): Bitmap? {
        return try {
            // 移除可能的前缀（如："data:image/png;base64,"）
            val pureBase64 = if (base64Data.contains(",")) {
                base64Data.substring(base64Data.indexOf(",") + 1)
            } else {
                base64Data
            }

            val decodedBytes = Base64.decode(pureBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Android 10及以上保存到相册（使用MediaStore）
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToGalleryQ(
        context: Context,
        bitmap: Bitmap,
        fileName: String
    ): String? {
        return try {
            // 获取图片MIME类型和扩展名
            val mimeType = getMimeTypeFromBitmap()
            // 创建ContentValues
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, mimeType)

                val path = Environment.DIRECTORY_PICTURES
                // 对于Android Q及以上，需要设置相对路径
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "$path/Screenshots" // 自定义文件夹
                )

                // 设置图片属性
                put(MediaStore.Images.Media.IS_PENDING, 1)

                // 可选：添加图片描述
                put(MediaStore.Images.Media.DESCRIPTION, "invite friends image")

                // 可选：设置图片宽度和高度
                put(MediaStore.Images.Media.WIDTH, bitmap.width)
                put(MediaStore.Images.Media.HEIGHT, bitmap.height)
            }
            // 获取ContentResolver并插入到MediaStore
            val contentResolver = context.contentResolver
            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: return null

            // 保存图片数据
            var outputStream: OutputStream? = null
            try {
                outputStream = contentResolver.openOutputStream(uri)
                outputStream?.let { os ->
                    val compressFormat = Bitmap.CompressFormat.PNG
                    val quality = 100
                    bitmap.compress(compressFormat, quality, os)
                }
            } finally {
                outputStream?.close()

                // 更新IS_PENDING状态为0，使其他应用可以访问
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, contentValues, null, null)
            }
            // 返回URI路径
            val result = uri.toString()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取Bitmap的MIME类型
     */
    private fun getMimeTypeFromBitmap(): String {
        // 如果有需要，可以通过其他方式确定原始格式
        // 这里默认为JPEG，因为大部分情况下是JPEG
        return "image/png"
    }

    /**
     * Android 9及以下保存到相册
     */
    @Suppress("DEPRECATION")
    private fun saveToGalleryLegacy(
        context: Context,
        bitmap: Bitmap,
        fileName: String
    ): String? {
        return try {
            // 检查存储权限
            if (!isExternalStorageWritable()) {
                // 可能需要请求权限
                return null
            }
            // 获取MIME类型和扩展名
            val mimeType = "image/png"
            // 创建图片目录
            val picturesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            )
            val appDir = File(picturesDir, "Screenshots") // 自定义文件夹

            if (!appDir.exists() && !appDir.mkdirs()) {
                return null
            }
            // 创建文件
            val imageFile = File(appDir, fileName)

            // 保存Bitmap到文件
            FileOutputStream(imageFile).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos)
                fos.flush()
            }

            // 通知媒体扫描器扫描新文件（使相册可见）
            MediaScannerConnection.scanFile(
                context,
                arrayOf(imageFile.absolutePath),
                arrayOf(mimeType)
            ) { path, uri -> }

            // 返回文件路径
            imageFile.absolutePath

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 检查外部存储是否可写
     */
    @Suppress("DEPRECATION")
    private fun isExternalStorageWritable(): Boolean {
        val isPermission = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        return isPermission
    }

    @Suppress("DEPRECATION")
    fun hasStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}