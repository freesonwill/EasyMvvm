package arch.cayenne.lib.common.utils.helper.toastAnim

import android.view.View
import android.view.WindowManager

interface ToastAnimation {

    val animDuration: Long
    val showDuration: Long

    fun onBeforeAddView(view: View)
    fun onAfterAddView(view: View)

    fun getLayoutParams(): WindowManager.LayoutParams
    suspend fun playShowAnim(view: View)
    suspend fun playDismissAnim(view: View)
}