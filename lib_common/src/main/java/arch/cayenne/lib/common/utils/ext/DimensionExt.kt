package arch.cayenne.lib.common.utils.ext

import android.app.Application
import android.util.TypedValue
import org.koin.core.context.GlobalContext
import org.koin.java.KoinJavaComponent.getKoin

/**
 * 尺寸单位扩展
 */
object DimensionExt {
    private val application: Application? get() {
        //inEditMode,koin not start
        if (GlobalContext.getOrNull() == null) return null
        return getKoin().get<Application>()
    }

    val Int.dp2px: Int
        get() = run {
            val context = application ?: return@run this
            val scale = context.resources.displayMetrics.density
            val dp = this
            (dp * scale + 0.5).toInt()
        }

    val Int.px2dp: Int
        get() = run {
            val context = application ?: return@run this
            val scale = context.resources.displayMetrics.density
            val v = this
            (v / scale + 0.5f).toInt()
        }

    val Float.dp2px: Int
        get() = run {
            val context = application ?: return@run this.toInt()
            val scale = context.resources.displayMetrics.density
            val dp = this
            (dp * scale + 0.5f).toInt()
        }

    val Float.px2dp: Int
        get() = run {
            val context = application ?: return@run this.toInt()
            val scale = context.resources.displayMetrics.density
            val v = this
            (v / scale + 0.5f).toInt()
        }


    val Float.sp2px: Float
        get() = run {
            val context = application ?: return@run this
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                this, context.resources.displayMetrics
            )

        }

    val Float.px2sp: Float
        get() = run {
            val context = application ?: return@run this
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                this, context.resources.displayMetrics
            )
        }

    inline val Int.sp2px: Float get() = this.toFloat().sp2px

    inline val Int.px2sp: Float get() = this.toFloat().px2sp

}