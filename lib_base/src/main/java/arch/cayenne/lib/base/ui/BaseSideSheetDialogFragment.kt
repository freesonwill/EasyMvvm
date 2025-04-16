package arch.cayenne.lib.base.ui

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.R
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel

abstract class BaseSideSheetDialogFragment<VM : BaseViewModel, VB : ViewBinding> : BaseDialogFragment<VM,VB>(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BaseSideSheetDialogTheme)
    }
}