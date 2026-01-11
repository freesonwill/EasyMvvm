package arch.cayenne.lib.base.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AnimRes
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui._interface.IFragment
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.ui._interface.IStatusBar
import arch.cayenne.lib.base.ui._interface.IView
import arch.cayenne.lib.base.ui.delegate.StatusBarDelegate
import arch.cayenne.lib.base.ui.delegate.UIBindDelegate
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModelForClass
import java.util.Stack
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
            vbProvider = ::createVB,
        )
    }

    protected open fun createVB(container: ViewGroup?): VB {
        return getViewBind(vbClass, container, false)
    }

    protected open fun createVM(): VM {
        return viewModelForClass(vmClass).value
    }

    //navigation跳转时是否保留view（true:保留；false：销毁）
    open val keepViewOnNavigation: Boolean = false

    //#endregion VB,VM
    // edittext软键盘弹出后，点击外部虚拟键盘消失 true启用，false不启用 子类可覆盖 默认启用
    open var enableHideKeyboardOnTouchOutside = true

    //设置颜色，默认根据主题颜色设定
    private val statusBar: IStatusBar by lazy { StatusBarDelegate(this) }

    private var enterAnim: Int? = null
    private var exitAnim: Int? = null
    private var onFinished = mutableListOf<(activity:BaseActivity<*,*>) -> Unit>()

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiBind.onCreateView(layoutInflater, null, savedInstanceState)
        setContentView(mBinding.root)
        uiBind.onViewCreated(mBinding.root, savedInstanceState)
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(configStatusBar(), mBinding.root)
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
    override fun onDestroy() {
        super.onDestroy()
        uiBind.onDestroyView()
        uiBind.onDestroy()
    }

    @CallSuper
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        uiBind.onNewIntent(intent)
    }

    override fun setStatusBar(config: StatusBarConfig, view: View) {
        statusBar.setStatusBar(config, view)
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

    override fun onBackPressed() {
        val roots = supportFragmentManager.fragments
        if (roots.isNotEmpty()) {
            val stack = ArrayDeque<Fragment>()
            val expanded = HashSet<Fragment>()

            // 根节点：按原顺序入栈，保证“右边的根”先被处理
            for (i in 0 until roots.size) {
                stack.addLast(roots[i])
            }

            while (stack.isNotEmpty()) {
                val cur = stack.last()

                if (!expanded.add(cur)) {
                    stack.removeLast()
                    val consumed = cur.isVisible && (cur as? IFragment)?.onBackPressed() == true
                    if (consumed) {
                        "onBackPressed consumed by fragment: $cur".logd(TAG)
                        return
                    }
                    continue
                }

                // children：按原顺序入栈，保证“右边的 child”先被处理
                val children = cur.childFragmentManager.fragments
                for (i in 0 until children.size) {
                    stack.addLast(children[i])
                }
            }
        }
        "onBackPressed consumed by activity: $this".logd(TAG)
        super.onBackPressed()
    }

    fun setEnterAnim(enterAnim: Int) {
        this.enterAnim = enterAnim
    }

    fun setExitAnim(exitAnim: Int) {
        this.exitAnim = exitAnim
    }

    @CallSuper
    override fun finish() {
        super.finish()
        enterAnim?.let { exitAnim?.let { it1 -> applyCloseTransition(it, it1) } }
        onFinished.forEach { it.invoke(this) }
        onFinished.clear()
    }

    fun doOnFinished(action: (activity:BaseActivity<*,*>) -> Unit) {
        if(isFinishing) {
            action.invoke(this)
        }else {
            this.onFinished.add(action)
        }
    }

    fun applyCloseTransition(@AnimRes enterAnim: Int, @AnimRes exitAnim: Int) {
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE,
                enterAnim,
                exitAnim
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(enterAnim, exitAnim)
        }
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
    @Suppress("DEPRECATION")
    return if (state == null) lifecycleScope.launch(block = block, context = context, start = start)
    else when(state) {
        Lifecycle.State.CREATED -> lifecycleScope.launchWhenCreated{ launch(context,start,block) }
        Lifecycle.State.STARTED -> lifecycleScope.launchWhenStarted{ launch(context,start,block) }
        Lifecycle.State.RESUMED -> lifecycleScope.launchWhenResumed{ launch(context,start,block) }
        else -> throw IllegalArgumentException("Unsupported lifecycle state: $state")
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
