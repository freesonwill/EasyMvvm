package arch.cayenne.lib.skin.res

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Environment
import androidx.core.content.res.ResourcesCompat
import arch.cayenne.lib.skin.widget.helper.SkinnableHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class SkinnableAssetsResourceLoader(context: Context, private val _skinName: String) : SkinnableResourceLoader {
    val SKIN_DEPLOY_PATH = "skins"
    private var _resources: Resources? = null

    init {
        val nSkinPath = getSkinPath(context, _skinName)
        if (File(nSkinPath).exists()) {
            val pkgName = getSkinPackageName(context, nSkinPath)
            val resources = getSkinResources(context, nSkinPath)
            if (resources != null) {
                _resources = resources
            }
        }

    }


    override fun getColor(context: Context, resId: Int): Int {
        if (resId != SkinnableHelper.INVALID_ID) {
            _resources?.let {
                try {
                    return ResourcesCompat.getColor(it, resId, context.theme)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return SkinnableHelper.INVALID_ID
    }

    override fun getColorStateList(context: Context, resId: Int): ColorStateList? {
        if (resId != SkinnableHelper.INVALID_ID) {
            _resources?.let {
                try {
                    return ResourcesCompat.getColorStateList(it, resId, context.theme)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
        return null
    }

    override fun getDrawable(context: Context, resId: Int): Drawable? {
        if (resId != SkinnableHelper.INVALID_ID) {
            _resources?.let {
                try {
                    return ResourcesCompat.getDrawable(it, resId, context.theme)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    override fun getTargetResourceId(context: Context, resId: Int): Int {
        return SkinnableHelper.INVALID_ID
    }

    override fun getSkinName(): String {
        return _skinName
    }

    override fun setSecondarySkin(skinName: String) {

    }

    private fun getSkinResources(context: Context, skinPkgPath: String): Resources? {
        try {
            val packageInfo =
                context.packageManager.getPackageArchiveInfo(skinPkgPath, 0) ?: return null
            packageInfo.applicationInfo!!.sourceDir = skinPkgPath
            packageInfo.applicationInfo!!.publicSourceDir = skinPkgPath
            val res =
                context.packageManager.getResourcesForApplication(packageInfo.applicationInfo!!)
            val superRes = context.resources
            return Resources(res.assets, superRes.displayMetrics, superRes.configuration)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getSkinPackageName(context: Context, skinPkgPath: String): String {
        return context.packageManager
            .getPackageArchiveInfo(skinPkgPath, PackageManager.GET_ACTIVITIES)
            ?.packageName ?: ""
    }

    fun getSkinPath(context: Context, skinName: String): String {
        return copySkinFromAssets(context, skinName)
    }

    private fun copySkinFromAssets(context: Context, name: String): String {
        val skinPath = File(getSkinDir(context), name).absolutePath
        try {
            val inputStream = context.assets.open(SKIN_DEPLOY_PATH + File.separator + name)
            val os = FileOutputStream(skinPath)
            var byteCount: Int
            val bytes = ByteArray(1024)
            byteCount = inputStream.read(bytes)
            while (byteCount != -1) {
                os.write(bytes, 0, byteCount)
                byteCount = inputStream.read(bytes)
            }
            os.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return skinPath
    }


    private fun getSkinDir(context: Context): String {
        val skinDir = File(getCacheDir(context), SKIN_DEPLOY_PATH)
        if (!skinDir.exists()) {
            skinDir.mkdirs()
        }
        return skinDir.absolutePath
    }

    private fun getCacheDir(context: Context): String {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val cacheDir = context.externalCacheDir
            if (cacheDir != null && (cacheDir.exists() || cacheDir.mkdirs())) {
                return cacheDir.absolutePath
            }
        }

        return context.cacheDir.absolutePath
    }
}