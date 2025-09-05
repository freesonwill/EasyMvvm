package arch.cayenne.lib.skin.widget.helper

import android.util.AttributeSet
import android.view.View
import androidx.annotation.AnyRes
import arch.cayenne.lib.skin.R
import arch.cayenne.lib.skin.data.SkinMsgType
import java.util.Locale

class SkinnableBackGroundHelper(mView: View) : SkinnableHelper(mView) {
    private var backgroundTintId:Int = INVALID_ID
    private var foregroundId:Int = INVALID_ID

    override fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = mView.context.obtainStyledAttributes(
            attrs,
            R.styleable.SportBackgroundHelper,
            defStyleAttr,
            0
        )
        try {
            if (a.hasValue(R.styleable.SportBackgroundHelper_android_background)) {
                mSrcId = a.getResourceId(R.styleable.SportBackgroundHelper_android_background, INVALID_ID)
            }
            if(a.hasValue(R.styleable.SportBackgroundHelper_android_backgroundTint)){
                backgroundTintId = a.getResourceId(R.styleable.SportBackgroundHelper_android_backgroundTint, INVALID_ID)
            }
            if(a.hasValue(R.styleable.SportBackgroundHelper_android_foreground)){
                foregroundId = a.getResourceId(R.styleable.SportBackgroundHelper_android_foreground, INVALID_ID)
            }
        } finally {
            a.recycle()
        }
        updateSkin(SkinMsgType.SELF)
    }

    override fun updateSkin(msgType: SkinMsgType) {
        if(checkSkinName(msgType)){
            return
        }
        updateBackground()
        updateBackgroundTintId()
        updateForegroundId()
    }

    fun updateBackground(@AnyRes resId:Int = INVALID_ID) {
        if(resId != INVALID_ID){
            mSrcId = resId
        }
        if (checkResourceIdValid(mSrcId)) {
            val drawable = resourcesManager.getDrawable(context = mView.context,mSrcId)
            if (drawable != null) {
                val paddingLeft = mView.paddingLeft
                val paddingTop = mView.paddingTop
                val paddingRight = mView.paddingRight
                val paddingBottom = mView.paddingBottom
                mView.background = drawable
                mView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
            }
        }
    }

    fun updateBackgroundTintId(@AnyRes resId: Int = INVALID_ID) {
        if(resId != INVALID_ID){
            backgroundTintId = resId
        }
        if(checkResourceIdValid(backgroundTintId)){
            val backgroundTint = resourcesManager.getColorStateList(mView.context,backgroundTintId)
            mView.backgroundTintList = backgroundTint
        }
    }

    fun updateForegroundId(@AnyRes resId:Int = INVALID_ID) {
        if(resId != INVALID_ID){
            foregroundId = resId
        }
        if(checkResourceIdValid(foregroundId)){
            val drawable = resourcesManager.getDrawable(context = mView.context,foregroundId)
            mView.foreground = drawable
        }
    }


}