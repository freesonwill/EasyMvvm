package arch.cayenne.lib.common.ui.view._interface

import android.content.Context
import android.util.AttributeSet
import arch.cayenne.lib.skin.widget.SkinnableView

abstract class BaseCustomTabIndicator(context: Context, attrs: AttributeSet) : SkinnableView(context, attrs) {
    abstract fun setIndicatorPosition(position: Int, offset: Float)
    abstract fun setTabWidth(width: Float,tabIndicatorWidth : Float = 0.45f)
}