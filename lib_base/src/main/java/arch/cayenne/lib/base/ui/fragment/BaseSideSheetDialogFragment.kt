package arch.cayenne.lib.base.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.R
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel

abstract class BaseSideSheetDialogFragment<VM : BaseViewModel, VB : ViewBinding> : BaseDialogFragment<VM, VB>(){
    override val dialogBackground: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BaseSideSheetDialogTheme)
    }
}