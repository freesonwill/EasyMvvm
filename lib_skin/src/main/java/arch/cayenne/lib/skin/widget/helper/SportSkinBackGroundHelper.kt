package arch.cayenne.lib.skin.widget.helper

import android.util.AttributeSet
import android.view.View
import arch.cayenne.lib.skin.R

class SportSkinBackGroundHelper(mView: View) : SportSkinHelper(mView) {

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
        } finally {
            a.recycle()
        }
        updateSkin()
    }


    override fun updateSkin() {

        if (!checkResourceIdValid(mSrcId)) {
            return
        }
        val drawable = resourcesManager.getDrawable(context = mView.context,mSrcId)
        if (drawable != null) {
            val paddingLeft = mView.paddingLeft
            val paddingTop = mView.paddingTop
            val paddingRight = mView.paddingRight
            val paddingBottom = mView.paddingBottom
            mView.background =drawable
            mView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        }

    }

    override fun updateLanguage(languageCode:String) {

    }
}