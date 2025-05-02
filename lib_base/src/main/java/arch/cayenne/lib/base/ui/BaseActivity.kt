package arch.cayenne.lib.base.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.ui._interface.IStatusBar
import arch.cayenne.lib.base.ui._interface.IView
import arch.cayenne.lib.base.ui.delegate.StatusBarDelegate
import arch.cayenne.lib.base.ui.delegate.UIBindDelegate
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
 * @date: 2025/3/14 09:48
 * @description:
 */
abstract class BaseActivity<VM : BaseViewModel,VB : ViewBinding> : AppCompatActivity(), IView,
    IStatusBar {
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
            vbProvider = ::createVB)
    }

    protected open fun createVB(container: ViewGroup?): VB {
        return getViewBind(vbClass, container, false)
    }

    protected open fun createVM(): VM {
        return viewModelForClass(vmClass).value
    }
    //#endregion VB,VM

    // 默认不启用键盘隐藏功能，子类可覆盖 edittext软键盘弹出后，点击外部虚拟键盘消失
    open val enableHideKeyboardOnTouchOutside = false
    //设置颜色，默认根据主题颜色设定
    private val statusBar: IStatusBar by lazy { StatusBarDelegate(this)  }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiBind.onCreateView(layoutInflater,null,savedInstanceState)
        setContentView(mBinding.root)
        uiBind.onViewCreated(mBinding.root,savedInstanceState)
        setStatusBar(configStatusBar())
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        uiBind.onDestroyView()
    }

    override fun setStatusBar(config: StatusBarConfig) {
        statusBar.setStatusBar(config)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (enableHideKeyboardOnTouchOutside && ev?.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view != null) {
                val imm = getSystemService(InputMethodManager::class.java)
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
                view.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
    fun reset() {
        mViewModel.reset()
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
fun AppCompatActivity.launch(
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
 * ViewBinding.inflate(layoutInflater,parent,attachedToParent)
 * @param T
 * @return
 */
inline fun <reified T : ViewBinding> Activity.viewBind(parent:ViewGroup? = null, attachedToParent:Boolean=false): Lazy<T> =
    lazy {
        getViewBind(parent,attachedToParent)
    }

inline fun <reified T : ViewBinding> Activity.getViewBind(parent:ViewGroup? = null, attachedToParent:Boolean=false): T  {
    val inflaterMethod = T::class.java.getMethod("inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java)
    return inflaterMethod.invoke(null,layoutInflater,parent,attachedToParent) as T
}

fun <T : ViewBinding> Activity.getViewBind(
    cls: KClass<T>,
    parent: ViewGroup?,
    attachedToParent: Boolean = false
): T {
    val inflaterMethod = cls.java.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )
    return inflaterMethod.invoke(null, layoutInflater, parent, attachedToParent) as T
}
