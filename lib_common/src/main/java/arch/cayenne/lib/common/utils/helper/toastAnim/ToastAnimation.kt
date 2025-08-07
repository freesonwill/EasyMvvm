package arch.cayenne.lib.common.utils.helper.toastAnim

import android.view.View
import android.view.WindowManager

interface ToastAnimation {

    val animDuration: Long
    val showDuration: Long

    /**
     * 获取队列标识符, 如果列隊有相同標示符則會先dismiss前一個
     * 如果返回 null，则使用 hashCode() 作为标识符
     */
    fun getQueueTag(): String?

    fun onBeforeAddView(view: View)
    fun onAfterAddView(view: View)

    fun getLayoutParams(view: View): WindowManager.LayoutParams
    suspend fun playShowAnim(view: View)
    suspend fun playDismissAnim(view: View)

    suspend fun playQueueAnim(view: View) {

    }

    fun isPlayQueueAnim(): Boolean {
        return false
    }
}