package arch.cayenne.lib.base.utils.log

/**
 * @date: 2025/12/22 15:25
 * @description:
 */
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Objects
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object Utils {
    @Volatile
    private var sApp: Application? = null

    @JvmStatic
    fun getApp(): Application {
        val cached = sApp
        if (cached != null) return cached
        init(UtilsBridge.getApplicationByReflect())
        val app = sApp ?: throw NullPointerException("reflect failed.")
        Log.i("Utils", UtilsBridge.getCurrentProcessName() + " reflect app success.")
        return app
    }

    @JvmStatic
    fun init(app: Application?) {
        if (app == null) {
            Log.e("Utils", "app is null.")
            return
        }
        val current = sApp
        if (current == null) {
            sApp = app
            UtilsBridge.init(sApp)
            UtilsBridge.preLoad()
            return
        }
        if (current == app) return
        UtilsBridge.unInit(current)
        sApp = app
        UtilsBridge.init(sApp)
    }

    /**
     * 是否编辑模式（预览）
     */
    @JvmStatic
    fun isInEditMode(): Boolean {
        val brand = android.os.Build.BRAND
        val model = android.os.Build.MODEL
        val idea = System.getProperty("idea.active")
        // throw IllegalArgumentException("brand:$brand,model:$model,idea:$idea")
        return Objects.equals(brand, "studio")
    }

    // 压缩指定目录下的日志文件为 zip
    @JvmStatic
    suspend fun compressFiles(directory: File, zipFile: File): File = withContext(Dispatchers.IO) {
        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
            if (directory.exists() && directory.isDirectory) {
                directory.listFiles()
                    ?.asSequence()
                    ?.filter { it.isFile }
                    ?.forEach { file ->
                        FileInputStream(file).use { fis ->
                            BufferedInputStream(fis).use { bis ->
                                zos.putNextEntry(ZipEntry(file.name))
                                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                                while (true) {
                                    val len = bis.read(buffer)
                                    if (len <= 0) break
                                    zos.write(buffer, 0, len)
                                }
                                zos.closeEntry()
                            }
                        }
                    }
            }
        }
        return@withContext zipFile
    }

    // 启动分享意图
    @JvmStatic
    suspend fun shareLogFile(context: Context, logDir: File=LogUtils.getConfig().getDir()) {
        val pd = ProgressDialog(context).apply {
            setMessage("正在压缩日志…")
            setCancelable(false)
            setProgressStyle(ProgressDialog.STYLE_SPINNER)

        }
        pd.show()

        val cache = context.externalCacheDir ?: context.cacheDir
        // 清理旧的日志压缩文件
        cache.listFiles { f -> f.isFile && f.name.startsWith("logs_") && f.name.endsWith(".zip") }
            ?.forEach { runCatching { it.delete() } }

        var zipFile = File(cache, "logs_${SimpleDateFormat("yyyyMMdd-HHmmss", Locale.ROOT).format(System.currentTimeMillis())}.zip")

        compressFiles(logDir, zipFile)



        val sizeBytes = runCatching { zipFile.length() }.getOrDefault(0L)

        val sizeStr = when {
            sizeBytes < 1024L -> "${sizeBytes}B"
            sizeBytes < 1024L * 1024L -> String.format(Locale.ROOT, "%.1fKB", sizeBytes / 1024.0)
            sizeBytes < 1024L * 1024L * 1024L -> String.format(Locale.ROOT, "%.1fMB", sizeBytes / (1024.0 * 1024.0))
            else -> String.format(Locale.ROOT, "%.1fGB", sizeBytes / (1024.0 * 1024.0 * 1024.0))
        }
        // 重命名压缩文件，加入大小信息
        zipFile.name.split(".").let { it[0] + "_${sizeStr}." + it[1] }.let {newName->
            val newZipName = File(zipFile.parent, newName)
            zipFile.renameTo(newZipName)
            zipFile = newZipName
        }
        val name = zipFile.name

        "shareLogFile $name $sizeStr".logd()
        val fileUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            zipFile
        )
        // 创建分享意图（把文件名、大小、创建时间写进 EXTRA\_TEXT 里方便对方看到）
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_TEXT, sizeStr)
            putExtra(Intent.EXTRA_STREAM, fileUri)
            putExtra(Intent.EXTRA_TITLE, name+"\t"+sizeStr)
        }

        val chooser = Intent.createChooser(intent, "Share Logs").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        // 启动选择分享应用的界面
        ContextCompat.startActivity(context, chooser, null)
        pd.dismiss()
    }
}
