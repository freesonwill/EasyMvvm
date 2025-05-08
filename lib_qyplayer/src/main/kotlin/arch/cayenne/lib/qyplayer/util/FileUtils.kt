package arch.cayenne.lib.qyplayer.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.os.StatFs
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.supucloud.qyplayer.log.L
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {

    val sdcardAvailableSize: Long
        /**
         * 获取sdcard剩余内存
         * @return 单位b
         */
        get() {
            val directory = Environment.getExternalStorageDirectory()

            val statFs = StatFs(directory.path)
            //获取可供程序使用的Block数量
            val blockAvailable = statFs.availableBlocks.toLong()
            //获得Sdcard上每个block的size
            val blockSize = statFs.blockSize.toLong()

            return blockAvailable * blockSize
        }

    val sdcardTotalSize: Long
        /**
         * 获取sdcard总内存大小
         * @return 单位b
         */
        get() {
            val directory = Environment.getExternalStorageDirectory()

            val statFs = StatFs(directory.path)
            //获得sdcard上 block的总数
            val blockCount = statFs.blockCount.toLong()
            //获得sdcard上每个block 的大小
            val blockSize = statFs.blockSize.toLong()

            return blockCount * blockSize
        }

    fun getApplicationSdcardPath(context: Context): File? {
        var var1 = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        if (var1 == null) {
            var1 = context.filesDir
        }

        return var1
    }

    fun deleteFD(path: String?): Boolean {
        if (TextUtils.isEmpty(path)) {
            return false
        } else {
            val var1 = File(path)
            val var2 = File(var1.absolutePath + System.currentTimeMillis())
            var1.renameTo(var2)
            return deleteFD(var2)
        }
    }

    fun deleteFD(fd: File): Boolean {
        return if (!fd.exists()) {
            false
        } else {
            if (fd.isDirectory) deleteDirectory(fd) else fd.delete()
        }
    }

    fun deleteDirectory(dir: File): Boolean {
        clearDirectory(dir)
        return dir.delete()
    }

    fun clearDirectory(dir: File) {
        val var1 = dir.listFiles()
        if (var1 != null) {
            val var2 = var1
            val var3 = var1.size

            for (var4 in 0 until var3) {
                val var5 = var2[var4]
                if (var5.isDirectory) {
                    deleteDirectory(var5)
                } else {
                    var5.delete()
                }
            }
        }
    }

    /**
     * 保存图片到本地
     */
    fun saveBitmap(bitmap: Bitmap, path: String?): String {
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val f = File(path, "snapShot_" + System.currentTimeMillis() + ".png")
        var out: FileOutputStream? = null
        if (f.exists()) {
            f.delete()
        }
        try {
            out = FileOutputStream(f)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (out != null) {
                try {
                    out.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return f.absolutePath
    }

    /**
     * android Q 版本默认路径
     * /storage/emulated/0/Android/data/包名/files/Media/
     * android Q 以下版本默认"/sdcard/DCIM/Camera/"
     */
    fun getDir(context: Context): String {
        val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getExternalFilesDir("")
                .toString() + File.separator + "Media" + File.separator
        } else {
            (Environment.getExternalStorageDirectory()
                .toString() + File.separator + "DCIM"
                    + File.separator + "Camera" + File.separator)
        }
        val file = File(dir)
        if (!file.exists()) {
            file.mkdirs()
        }
        return dir
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    fun saveImgToMediaStore(context: Context, fileName: String?, mimeType: String?) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        values.put(MediaStore.Images.Media.IS_PENDING, 1)

        val resolver = context.contentResolver
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val item = resolver.insert(collection, values)

        try {
            resolver.openFileDescriptor(item!!, "w", null).use { pfd ->
                // Write data into the pending image.
                val bin = BufferedInputStream(FileInputStream(fileName))
                val outputStream = ParcelFileDescriptor.AutoCloseOutputStream(pfd)
                val bot = BufferedOutputStream(outputStream)
                val bt = ByteArray(2048)
                var len: Int
                while ((bin.read(bt).also { len = it }) >= 0) {
                    bot.write(bt, 0, len)
                    bot.flush()
                }
                bin.close()
                bot.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Now that we're finished, release the "pending" status, and allow other apps
        // to view the image.
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(item!!, values, null, null)
        //打印写入时间
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    fun saveVideoToMediaStore(context: Context, fileName: String?) {
        val startTime = System.currentTimeMillis()
        val values = ContentValues()
        val name = "$startTime-video.mp4"
        values.put(MediaStore.Video.Media.DISPLAY_NAME, name)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(MediaStore.Video.Media.IS_PENDING, 1)

        val resolver = context.contentResolver
        val collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val item = resolver.insert(collection, values)

        try {
            resolver.openFileDescriptor(item!!, "w", null).use { pfd ->
                // Write data into the pending video.
                val bin = BufferedInputStream(FileInputStream(fileName))
                val outputStream = ParcelFileDescriptor.AutoCloseOutputStream(pfd)
                val bot = BufferedOutputStream(outputStream)
                val bt = ByteArray(2048)
                var len: Int
                while ((bin.read(bt).also { len = it }) >= 0) {
                    bot.write(bt, 0, len)
                    bot.flush()
                }
                bin.close()
                bot.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Now that we're finished, release the "pending" status, and allow other apps
        // to view the video.
        values.clear()
        values.put(MediaStore.Video.Media.IS_PENDING, 0)
        resolver.update(item!!, values, null, null)
        //打印写入时间
    }

    fun copyVideoFromAssetsToCache(context: Context, videoFileName: String) {
        val assetManager = context.assets
        val filesDir = context.filesDir ?: return
        val destFile = File(filesDir, videoFileName)

        // 确保缓存目录存在
        if (!filesDir.exists()) {
            filesDir.mkdirs()
        }

        // 如果文件已经存在，则不执行复制
        if (!destFile.exists()) {
            try {
                assetManager.open(videoFileName).use { inputStream ->
                    FileOutputStream(destFile).use { fileOutputStream ->
                        val buffer = ByteArray(4096)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead)
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                L.e(e.message)
            }
        }
    }
}
