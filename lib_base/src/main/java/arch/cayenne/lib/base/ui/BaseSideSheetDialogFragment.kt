package arch.cayenne.lib.base.ui

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.R
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.base.ui.interface_.IStatusBar
import arch.cayenne.lib.base.ui.interface_.IView

abstract class BaseSideSheetDialogFragment<VM : BaseViewModel, VB : ViewBinding> : BaseDialogFragment<VM,VB>(),
    IView, IStatusBar {


    //设置颜色，默认根据主题颜色设定
    private val statusBar: IStatusBar by lazy { StatusBarDelegate(requireActivity()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BaseSideSheetDialogTheme)
    }
}