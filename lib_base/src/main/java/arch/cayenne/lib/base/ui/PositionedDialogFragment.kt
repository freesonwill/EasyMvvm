package arch.cayenne.lib.base.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewTreeObserver
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel

/**
 * 指定位置的DialogFragment
 */
abstract class PositionedDialogFragment<VM : BaseViewModel, VB : ViewBinding> :
    BaseVMDialogFragment<VM, VB>() {

    companion object {
        const val POSITION_X = "positionX"
        const val POSITION_Y = "positionY"
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val positionX = requireArguments().getInt(POSITION_X, -1)
            val positionY = requireArguments().getInt(POSITION_Y, -1)

            if (positionX != -1 && positionY != -1) {
                mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        val layoutParams = it.attributes
                        layoutParams.gravity = Gravity.TOP or Gravity.START

                        layoutParams.x = positionX
                        layoutParams.y = positionY

                        it.attributes = layoutParams
                    }
                })
            }
        }
    }
}