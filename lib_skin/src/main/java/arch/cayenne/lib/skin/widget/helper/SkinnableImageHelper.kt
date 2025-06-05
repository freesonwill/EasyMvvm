package arch.cayenne.lib.skin.widget.helper

import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.ImageView
import arch.cayenne.lib.skin.R
import arch.cayenne.lib.skin.data.SkinMsgType

class SkinnableImageHelper(view: ImageView) : SkinnableHelper(view) {
    private var mSrcCompatResId = INVALID_ID
    private var _radius: Float = 0f
    override val mView: ImageView
        get() = super.mView as ImageView

    override fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        var a: TypedArray? = null
        try {
            a = mView.context.obtainStyledAttributes(
                attrs,
                R.styleable.SportImageView,
                defStyleAttr,
                0
            )
            mSrcId = a!!.getResourceId(R.styleable.SportImageView_srcCompat, INVALID_ID)
            mSrcCompatResId = a.getResourceId(R.styleable.SportImageView_android_src, INVALID_ID)
            _radius = a.getFloat(R.styleable.SportImageView_android_radius, 0f)
        } finally {
            a?.recycle()
        }
        updateSkin(SkinMsgType.SELF)
    }

    override fun updateSkin(msgType: SkinMsgType) {
        if(checkSkinName(msgType)){
            return
        }
        if (checkResourceIdValid(mSrcCompatResId)) {
            val drawable = resourcesManager.getDrawable(mView.context, mSrcCompatResId)
            mView.setImageDrawable(drawable)
        } else {
            if (checkResourceIdValid(mSrcId)) {
                val drawable = resourcesManager.getDrawable(mView.context, mSrcId)
                mView.setImageDrawable(drawable)
            }
        }
    }

    fun getRadius(): Float {
        return _radius
    }

    override fun updateLanguage(languageCode: String) {
    }

}
