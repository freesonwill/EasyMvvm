package arch.cayenne.lib.base.ui

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.R
import arch.cayenne.lib.base.data.StatusBarConfig
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.base.ui.interface_.IStatusBar
import arch.cayenne.lib.base.ui.interface_.IView
import arch.cayenne.lib.base.utils.LogUtilsExt.logd
import org.koin.androidx.viewmodel.ext.android.viewModelForClass
import kotlin.reflect.KClass

abstract class BaseSideSheetDialogFragment<VM : BaseViewModel, VB : ViewBinding> : BaseDialogFragment<VM,VB>(),
    IView, IStatusBar {


    //设置颜色，默认根据主题颜色设定
    private val statusBar: IStatusBar by lazy { StatusBarDelegate(requireActivity()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BaseSideSheetDialogTheme)
    }
}