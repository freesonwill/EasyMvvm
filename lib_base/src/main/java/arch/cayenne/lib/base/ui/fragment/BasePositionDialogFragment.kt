package arch.cayenne.lib.base.ui.fragment

import android.view.Window
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel

abstract class BasePositionDialogFragment<VM : BaseViewModel, VB : ViewBinding>: BaseDialogFragment<VM, VB>() {

    private var isShowing = false

    override fun onStart() {
        super.onStart()
        if (!isShowing) {
            isShowing = true
            initDialog()
            showDialog()
        }
    }

    private fun showDialog() {
        val w = dialog?.window ?: return
        setDialogPosition(w)
    }

    protected open fun initDialog() {

    }

    override fun onDestroy() {
        isShowing = false
        super.onDestroy()
    }

    abstract fun setDialogPosition(w: Window)
}