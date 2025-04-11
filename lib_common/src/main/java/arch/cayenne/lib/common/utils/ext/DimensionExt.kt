package arch.cayenne.lib.common.utils.ext

import android.app.Application
import android.util.TypedValue
import org.koin.java.KoinJavaComponent.getKoin

/**
 * 尺寸单位扩展
 */
object DimensionExt {

    inline val Int.dp2px
        get() = run {
            val context = getKoin().get<Application>()
            val scale = context.resources.displayMetrics.density
            val dp = this
            (dp * scale + 0.5).toInt()
        }

    inline val Int.px2dp
        get() = run {
            val context = getKoin().get<Application>()
            val scale = context.resources.displayMetrics.density
            val v = this
            (v / scale + 0.5f).toInt()
        }
    inline val Float.dp2px
        get() = run {
            val context = getKoin().get<Application>()
            val scale = context.resources.displayMetrics.density
            val dp = this
            (dp * scale + 0.5f).toInt()
        }

    inline val Float.px2dp
        get() = run {
            val context = getKoin().get<Application>()
            val scale = context.resources.displayMetrics.density
            val v = this
            (v / scale + 0.5f).toInt()
        }


    inline val Float.sp2px
        get() = run {
            val context = getKoin().get<Application>()
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                this, context.resources.displayMetrics
            )

        }

    inline val Float.px2sp
        get() = run {
            val context = getKoin().get<Application>()
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                this, context.resources.displayMetrics
            )
        }

    inline val Int.sp2px get() = this.toFloat().sp2px

    inline val Int.px2sp get() = this.toFloat().px2sp

}