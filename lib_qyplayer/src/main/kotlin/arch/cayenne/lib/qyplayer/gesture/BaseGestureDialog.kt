package arch.cayenne.lib.qyplayer.gesture

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import arch.cayenne.lib.qyplayer.R
import arch.cayenne.lib.qyplayer.ScreenMode

/**
 * 手势滑动的手势提示框。
 */
open class BaseGestureDialog(context: Context) : PopupWindow() {
    //手势文字
    @JvmField
    var mTextView: TextView

    //手势图片
    @JvmField
    var mImageView: ImageView

    //对话框的宽高
    private val mDialogWidthAndHeight: Int

    //当前屏幕模式
    private var mCurrentScreenMode = ScreenMode.SMALL

    init {
        //使用同一个布局
        val mInflater =
            context.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = mInflater.inflate(R.layout.dialog_gesture, null)
        view.measure(0, 0)
        contentView = view

        //找出view
        mTextView = view.findViewById<View>(R.id.gesture_text) as TextView
        mImageView = view.findViewById<View>(R.id.gesture_image) as ImageView

        //设置对话框宽高
        mDialogWidthAndHeight =
            context.resources.getDimensionPixelSize(R.dimen.player_gesture_dialog_size)
        width = mDialogWidthAndHeight
        height = mDialogWidthAndHeight
    }

    /**
     * 居中显示对话框
     * @param parent 所属的父界面
     */
    fun show(parent: View) {
        val location = IntArray(2)
        parent.getLocationOnScreen(location)
        //保证显示居中
        val x = location[0] + (parent.right - parent.left - mDialogWidthAndHeight) / 2
        val y = location[1] + (parent.bottom - parent.top - mDialogWidthAndHeight) / 2

        if (mCurrentScreenMode == ScreenMode.SMALL) {
            showAtLocation(parent, Gravity.TOP or Gravity.START, x, y)
        } else {
            //当全屏模式下,用CENTER,解决在分屏模式下,部分手机展示不居中问题
            showAtLocation(parent, Gravity.CENTER, 0, 0)
        }
    }

    fun setScreenMode(currentScreenMode: ScreenMode) {
        this.mCurrentScreenMode = currentScreenMode
    }
}
