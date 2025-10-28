package arch.cayenne.lib.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {

    /**
     * 将 Bitmap 保存到本地（固定文件名，覆盖旧文件）
     * @param bitmap 要保存的 Bitmap
     * @return 保存的文件路径，若失败则返回 null
     */
    fun saveBitmapToFile(context:Context,bitmap: Bitmap): String? {
        try {
            // 使用固定的文件名
            val fileName = "circular_image.png"
            val directory = File(context.getExternalFilesDir("Pictures"), "CircularImages")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val file = File(directory, fileName)

            // 覆盖旧文件（若存在）
            if (file.exists()) {
                file.delete()
            }
            // 使用 PNG 格式保存（支持透明）
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            return file.absolutePath
        } catch (e: Exception) {
            return null
        }
    }
    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            // 使用 ContentResolver 打开输入流
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                // 从输入流解码为 Bitmap
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    /**
     * 加载本地保存的 Bitmap 并显示
     * @return 是否成功加载并显示
     */
    fun loadLocalBitmap(context:Context): Bitmap? {
        try {
            val fileName = "circular_image.png"
            val file = File(context.getExternalFilesDir("Pictures")?.absolutePath + "/CircularImages", fileName)
            if (!file.exists()) {
                return null
            }
            // 从文件加载 Bitmap
            val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: run {
                return null
            }
            return bitmap
        } catch (e: Exception) {
            return null
        }
    }
}