package arch.cayenne.lib.common.utils.ext

import android.app.Application
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import arch.cayenne.lib.base.utils.log.Utils
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import org.koin.java.KoinJavaComponent.getKoin
import java.io.IOException

/**
 * @author: zhangsan
 * @date: 2025/3/31 16:44
 * @description: 资源扩展
 */
object ResourceExt {
    private val app: Application by lazy {
        // 占位对象，避免编辑器崩溃
        if(Utils.isInEditMode()) return@lazy Application()
        getKoin().get<Application>()
    }

    /***
     *  無Context狀態下取得String
     */
    fun @receiver:StringRes Int.getString(vararg formatArgs: Any,ctx: Context = app): String {
        return SkinnableResourceManager.getString(ctx,this,formatArgs = formatArgs)
    }

    /***
     *  無Context狀態下取得String
     */
    fun @receiver:StringRes Int.getStringArray(ctx: Context = app): List<String> {
        return ctx.resources.getStringArray(this).toList()
    }


    /***
     *  获取Color
     */
    fun @receiver:ColorRes Int.getColor(ctx: Context = app): Int {
        return ContextCompat.getColor(ctx, this)
    }

    /**
     * 获取ColorDrawable
     * @return
     */
    fun @receiver:ColorRes Int.getColorDrawable(ctx: Context = app): ColorDrawable {
        return ColorDrawable(ContextCompat.getColor(ctx, this))
    }

    /**
     * 获取Drawable
     * @return
     */
    fun @receiver:DrawableRes Int.getDrawable(ctx: Context = app): Drawable {
        return ContextCompat.getDrawable(ctx, this)!!
    }

    /**
     * 获取getDimension
     * @return
     */
    fun @receiver:DimenRes Int.getDimension(ctx: Context = app): Float {
        return ctx.resources.getDimension(this)
    }

    /**
     * getDimensionPixelSize
     * @return
     */
    fun @receiver:DimenRes Int.getDimensionPixelSize(ctx: Context = app): Int {
        return ctx.resources.getDimensionPixelSize(this)
    }

    /**
     * getDimensionPixelOffset
     * @return
     */
    fun @receiver:DimenRes Int.getDimensionPixelOffset(ctx: Context = app): Int {
        return ctx.resources.getDimensionPixelOffset(this)
    }

    fun getAssets(fileName: String, context: Context = app): String? {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            null
        }
    }

}