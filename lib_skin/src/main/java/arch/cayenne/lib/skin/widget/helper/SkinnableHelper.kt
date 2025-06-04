package arch.cayenne.lib.skin.widget.helper

import android.util.AttributeSet
import android.view.View
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import java.util.Locale


abstract class SkinnableHelper (protected open val mView:View) {
    val resourcesManager = SkinnableResourceManager

    protected var mSrcId: Int = INVALID_ID
    protected var lastSkin:String = ""
    protected var lastLanguage:Locale? = null

    /**
     * 加载资源布局
     */
    abstract fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int)

    /**
     * 刷新UI
     */
    abstract fun updateSkin(msgType: SkinMsgType = SkinMsgType.FLOW)

    /**
     * 刷新language
     * */
    abstract fun updateLanguage(languageCode:String)

    open fun setSrcId(srcId: Int) {
        mSrcId = srcId
        updateSkin(SkinMsgType.SELF)
    }

    /**
     * 避免重复的皮肤更换
     * */
    open fun checkSkinName(msgType: SkinMsgType):Boolean{
        val flag = msgType == SkinMsgType.FLOW && resourcesManager.getSkinName() == lastSkin
        lastSkin = resourcesManager.getSkinName()
        return flag
    }

    open fun checkLanguage(value:Locale?,msgType: SkinMsgType):Boolean{
        return msgType == SkinMsgType.FLOW && value == lastLanguage
    }

    companion object {
        const val INVALID_ID = 0

        fun checkResourceIdValid(resId: Int): Boolean {
            return resId != INVALID_ID
        }
    }
}