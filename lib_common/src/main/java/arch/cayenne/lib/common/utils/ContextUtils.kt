package arch.cayenne.lib.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import dalvik.system.BaseDexClassLoader
import dalvik.system.DexFile

/**
 * @author: zhangsan
 * @date: 2025/3/30 15:31
 * @description:
 */
object ContextUtils {

    /**
     * 获取app所有包名
     *
     * @param context
     * @return
     */
    @SuppressLint("DiscouragedPrivateApi")
    fun getAllPackageName(context: Context): List<String> {
        val mainPackage = context.packageName
        val packages = mutableSetOf(mainPackage)

        try {
            val classLoader = context.classLoader as BaseDexClassLoader

            // Android 5.0+ 通用方案
            val pathListField = BaseDexClassLoader::class.java.getDeclaredField("pathList")
            pathListField.isAccessible = true
            val pathList = pathListField.get(classLoader)

            // 兼容不同Android版本
            val dexElementsField = try {
                pathList.javaClass.getDeclaredField("dexElements")
            } catch (e: NoSuchFieldException) {
                pathList.javaClass.getDeclaredField("pathElements") // 某些厂商修改
            }.apply { isAccessible = true }

            (dexElementsField.get(pathList) as Array<*>).forEach { element ->
                val dexFile = try {
                    // Android标准实现
                    element?.javaClass?.getDeclaredField("dexFile")?.apply { isAccessible = true }
                        ?.get(element) as? DexFile
                } catch (e: Exception) {
                    // 处理厂商定制（如华为）
                    element?.javaClass?.getMethod("getDexFile")?.invoke(element) as? DexFile
                }
                dexFile?.entries()?.iterator()?.forEach { className ->
                    when {
                        className.endsWith(".R") -> packages.add(className.substringBeforeLast(".R"))
                        className.contains(".R\$") -> packages.add(className.substringBeforeLast(".R"))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("PackageScan", "Deep scan failed", e)
        }
        return packages.filter { it.startsWith(mainPackage.substringBeforeLast('.')) }
            .distinct()
            .sorted()
    }



    /**
     * 系统主题
     * @param context
     * @return
     *  Configuration.UI_MODE_NIGHT_NO  // 浅色模式
     *  Configuration.UI_MODE_NIGHT_YES // 深色模式
     */
    val Context.systemTheme: Int get() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
}