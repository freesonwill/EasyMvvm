package arch.cayenne.lib.qyplayer.control

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import arch.cayenne.lib.qyplayer.R
import arch.cayenne.lib.qyplayer.util.ScreenUtils

/**
 * 更多设置弹窗
 */
class ShowSettingDialog(context: Context) : Dialog(context, R.style.ShowMoreDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.decorView?.setPadding(0, 0, 0, 0)
        setLayout()
    }

    private fun setLayout() {
        window?.attributes = window?.attributes?.apply {
            height = ViewGroup.LayoutParams.MATCH_PARENT
            gravity = Gravity.END

            val screenWidth = ScreenUtils.getWidth(context)
            val screenHeight = ScreenUtils.getHeight(context)
            width = if (screenWidth > screenHeight) screenHeight else screenWidth
        }
        setCanceledOnTouchOutside(true)
    }

    private fun fullScreenImmersive(view: View) {
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        view.systemUiVisibility = uiOptions
    }

    override fun show() {
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        super.show()
        window?.let {
            fullScreenImmersive(it.decorView)
            it.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }
    }
}