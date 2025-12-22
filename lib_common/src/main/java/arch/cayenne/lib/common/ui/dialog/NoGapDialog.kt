package arch.cayenne.lib.common.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.MotionEvent

/**
 * 原生Dialog点击边缘16像素会被拦截，不向下穿透
 */

class NoGapDialog : Dialog {

    constructor(context: Context) : super(context)

    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isShowing && (event.action == MotionEvent.ACTION_UP
                    || event.action == MotionEvent.ACTION_OUTSIDE)
        ) {
            cancel()
            return true
        }
        return super.onTouchEvent(event)
    }
}