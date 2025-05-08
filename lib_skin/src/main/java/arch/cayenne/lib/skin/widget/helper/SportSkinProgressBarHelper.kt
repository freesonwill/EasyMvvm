package arch.cayenne.lib.skin.widget.helper

import android.util.AttributeSet
import android.widget.ProgressBar
import arch.cayenne.lib.skin.R

class SportSkinProgressBarHelper(private val view: ProgressBar) : SportSkinHelper(view) {

    override fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = mView.context.obtainStyledAttributes(
            attrs,
            R.styleable.SportBackgroundHelper,
            defStyleAttr,
            0
        )
        try {
            if (a.hasValue(R.styleable.SportBackgroundHelper_android_progressDrawable)) {
                mSrcId = a.getResourceId(
                    R.styleable.SportBackgroundHelper_android_progressDrawable,
                    INVALID_ID
                )
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
        val drawable = resourcesManager.getDrawable(context = mView.context, mSrcId)
        if (drawable != null) {
            view.progressDrawable = drawable
        }
    }

    override fun updateLanguage(languageCode: String) {
        TODO("Not yet implemented")
    }
}