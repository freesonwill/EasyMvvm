package arch.cayenne.lib.base.utils.log

/**
 * @date: 2025/12/22 15:25
 * @description:
 */
import android.app.Application
import android.util.Log
import java.util.Objects

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
}
