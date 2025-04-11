package arch.cayenne.lib.common.utils.ext

import android.app.Application
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import org.koin.java.KoinJavaComponent.getKoin

/**
 * @author: zhangsan
 * @date: 2025/3/31 16:44
 * @description: 资源扩展
 */
object ResourceExt {
    private val application: Application by lazy { getKoin().get<Application>() }

    /***
     *  無Context狀態下取得String
     */
    fun @receiver:StringRes Int.getString(vararg formatArgs: Any): String {
        return application.getString(this, *formatArgs)
    }


    /***
     *  获取Color
     */
    fun @receiver:ColorRes Int.getColor(): Int {
        return ContextCompat.getColor(application, this)
    }

    /**
     * 获取ColorDrawable
     * @return
     */
    fun @receiver:ColorRes Int.getColorDrawable(): ColorDrawable {
        return ColorDrawable(ContextCompat.getColor(application, this))
    }

    /**
     * 获取Drawable
     * @return
     */
    fun @receiver:DrawableRes Int.getDrawable(): Drawable {
        return ContextCompat.getDrawable(application, this)!!
    }

}