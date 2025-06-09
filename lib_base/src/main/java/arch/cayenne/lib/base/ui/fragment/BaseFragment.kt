package arch.cayenne.lib.base.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.R
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.ui.delegate.StatusBarDelegate
import arch.cayenne.lib.base.ui.delegate.UIBindDelegate
import arch.cayenne.lib.base.ui._interface.IStatusBar
import arch.cayenne.lib.base.ui._interface.IView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModelForClass
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass

/**
 * @author: zhangsan
 * @date: 2025/3/13 18:38
 * @description: ViewModelFragment基类，自动把ViewModel注入Fragment
 */
abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding> : Fragment(), IView, IStatusBar {
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
    private val statusBar: IStatusBar by lazy { StatusBarDelegate(this) }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        uiBind.onCreateView(inflater,container,savedInstanceState)
        return mBinding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiBind.onViewCreated(view,savedInstanceState)
    }
    @CallSuper
    override fun onStart() {
        super.onStart()
        uiBind.onStart()
    }
    @CallSuper
    override fun onResume() {
        super.onResume()
        uiBind.onResume()
    }
    @CallSuper
    override fun onPause() {
        super.onPause()
        uiBind.onPause()
    }
    @CallSuper
    override fun onStop() {
        super.onStop()
        uiBind.onStop()
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

    override fun setStatusBar(config: StatusBarConfig,view: View) {
        statusBar.setStatusBar(config,view)
    }

    fun getStatusBarColor() : Int{
        return statusBar.configStatusBar().statusBarColor
    }

    /**
     * fragment是否在前台
     * @return
     */
    protected fun isAtFront(): Boolean {
        val v = view ?: return false
        return v.isLaidOut
    }

    /**
     * 获取childNavController： fragment嵌套了fragment
     *
     * @param id
     * @return
     */
    fun findChildNavController(@IdRes id: Int): NavController {
        return (childFragmentManager.findFragmentById(id) as NavHostFragment).findNavController()
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

/**
 * 启动一个协程
 *
 * @param state null时为普通协程；非null为生命周期协程
 * @param context
 * @param start
 * @param block 协程任务
 */
fun Fragment.launch(
    state: Lifecycle.State? = null,
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return if (state == null) lifecycleScope.launch(block = block, context = context, start = start)
    else lifecycleScope.launch(context = context, start = start) {
        repeatOnLifecycle(state, block)
    }
}

/**
 * ViewBinding.inflate(layoutInflater)
 *
 * @param T
 * @return
 */
inline fun <reified T : ViewBinding> Fragment.viewBind(
    root: ViewGroup? = null,
    attachedToParent: Boolean = false
): Lazy<T> =
    lazy {
        getViewBind(root, attachedToParent)
    }

inline fun <reified T : ViewBinding> Fragment.getViewBind(
    root: ViewGroup?,
    attachedToParent: Boolean = false
): T {
    val inflaterMethod = T::class.java.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )
    return inflaterMethod.invoke(null, layoutInflater, root, attachedToParent) as T
}

fun <T : ViewBinding> Fragment.getViewBind(
    cls: KClass<T>,
    root: ViewGroup?,
    attachedToParent: Boolean = false
): T {
    val inflaterMethod = cls.java.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )
    return inflaterMethod.invoke(null, layoutInflater, root, attachedToParent) as T
}