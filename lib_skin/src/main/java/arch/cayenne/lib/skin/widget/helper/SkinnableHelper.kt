package arch.cayenne.lib.skin.widget.helper

import android.util.AttributeSet
import android.view.View
import arch.cayenne.lib.skin.res.SkinnableResourceManager


abstract class SkinnableHelper (protected open val mView:View) {
    val resourcesManager = SkinnableResourceManager

    protected var mSrcId: Int = INVALID_ID

    /**
     * 加载资源布局
     */
    abstract fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int)

    /**
     * 刷新UI
     */
    abstract fun updateSkin()

    /**
     * 刷新language
     * */
    abstract fun updateLanguage(languageCode:String)

    open fun setSrcId(srcId: Int) {
        mSrcId = srcId
        updateSkin()
    }

    companion object {
        const val INVALID_ID = 0

        fun checkResourceIdValid(resId: Int): Boolean {
            return resId != INVALID_ID
        }
    }
}