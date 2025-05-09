package arch.cayenne.lib.base.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.R
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.ui.delegate.StatusBarDelegate
import arch.cayenne.lib.base.ui.delegate.UIBindDelegate
import arch.cayenne.lib.base.ui._interface.IStatusBar
import arch.cayenne.lib.base.ui._interface.IView
import org.koin.androidx.viewmodel.ext.android.viewModelForClass
import kotlin.reflect.KClass

abstract class BaseDialogFragment<VM : BaseViewModel, VB : ViewBinding> : DialogFragment(), IView,IStatusBar {
    protected open val TAG = this.javaClass.simpleName

    //#region VB,VM
    protected val mBinding: VB get() = uiBind.binding
    protected val mViewModel: VM get() = uiBind.viewModel
    abstract val vbClass: KClass<VB>
    abstract val vmClass: KClass<VM>
    private val uiBind by lazy {
        UIBindDelegate(
            uiOwner = this,
            vmProvider = ::createVM,
            vbProvider = ::createVB,
            keepViewOnNavigation = keepViewOnNavigation
        )
    }
    protected open fun createVB(container: ViewGroup?): VB {
        return getViewBind(vbClass, container, false)
    }

    protected open fun createVM(): VM {
        return viewModelForClass(vmClass).value
    }
    //navigation跳转时是否保留view（true:保留；false：销毁）
    open val keepViewOnNavigation:Boolean = false
    //#endregion VB,VM

    //设置颜色，默认根据主题颜色设定
    private val statusBar: IStatusBar by lazy { StatusBarDelegate(requireActivity()) }

    /**
     * 对话框默认背景，默认白色，子类根据需求复写
     */
    protected open val dialogBackground: Drawable? by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.bg_base_dialog
        )
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        uiBind.onCreateView(inflater,container,savedInstanceState)
        return mBinding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply {
            setBackgroundDrawable(dialogBackground)
        }
        uiBind.onViewCreated(view,savedInstanceState)
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        uiBind.onDestroyView()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        uiBind.onDestroy()
    }


    override fun createObserver() {
    }

    fun show(manager: FragmentManager) {
        val f = manager.findFragmentByTag(this::class.java.simpleName)
        if (f == null || !f.isAdded) {
            super.show(manager, this::class.java.simpleName)
        }
    }

    override fun setStatusBar(config: StatusBarConfig) {
        statusBar.setStatusBar(config)
    }

    /**
     * 获取activity的NavController
     *
     * @param id
     * @return
     */
    private fun findActivityNavController(@IdRes id: Int = R.id.nav_host): NavController {
        return requireActivity().findNavController(id)
    }
}