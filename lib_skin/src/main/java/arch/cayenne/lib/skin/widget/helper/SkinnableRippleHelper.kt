package arch.cayenne.lib.skin.widget.helper

import android.graphics.Color
import android.util.AttributeSet
import arch.cayenne.lib.skin.R
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.skin.widget.SkinnableRippleView

class SkinnableRippleHelper(mView: SkinnableRippleView) : SkinnableHelper(mView) {

    private var rippleColorID: Int = INVALID_ID
    var rippleColor: Int = Color.parseColor("#F3F3F4")
    var bgType: Int = 0
    var rippleCorner: Int = 0

    override fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = mView.context.obtainStyledAttributes(
            attrs,
            R.styleable.RippleView,
            defStyleAttr,
            0
        )
        try {
            if (a.hasValue(R.styleable.RippleView_rv_color)) {
                rippleColorID = a.getResourceId(R.styleable.RippleView_rv_color, 0)
                bgType = a.getInt(R.styleable.RippleView_rv_bgType, 0)
                rippleCorner = a.getDimensionPixelSize(R.styleable.RippleView_rv_rippleCorner, 12)
            }
        } finally {
            a.recycle()
        }
        updateSkin(SkinMsgType.SELF)
    }

    override fun updateSkin(msgType: SkinMsgType) {
        if (checkSkinName(msgType)) {
            return
        }
        if (checkResourceIdValid(rippleColor)) {
            rippleColor = SkinnableResourceManager.getColor(mView.context, rippleColorID)
            mView.invalidate()
        }
    }
}