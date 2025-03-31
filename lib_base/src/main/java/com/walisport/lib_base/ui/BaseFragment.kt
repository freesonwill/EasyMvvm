package com.walisport.lib_base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.walisport.lib_base.data.viewmodel.BaseViewModel
import com.walisport.lib_base.ui.interface_.IStatusBar
import com.walisport.lib_base.ui.interface_.IView
import com.walisport.lib_base.utils.CommonUtils.inflateMethod
import com.walisport.lib_base.utils.LogUtilsExt.logd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @author: zhangsan
 * @date: 2025/3/13 18:38
 * @description: ViewModelFragment基类，自动把ViewModel注入Fragment
 */
abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding> : Fragment(), IView,
    KoinComponent,IStatusBar {
    protected abstract val mBinding: VB
    protected abstract val mViewModel: VM
    //设置颜色，默认根据主题颜色设定
    private val statusBar:IStatusBar by lazy { StatusBarDelegate(requireActivity())  }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        if (mBinding is ViewDataBinding) {
            (mBinding as ViewDataBinding).lifecycleOwner = viewLifecycleOwner
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        initListener()
        initData()
        createObserver()
        trackLoadingTime()
    }

    override fun setStatusBar(config: IStatusBar.Config) {
        statusBar.setStatusBar(config)
    }

    override fun configStatusBar():IStatusBar.Config = IStatusBar.Config()
    /**
     * 是否开启统计加载时间
     */
    open fun enableTrackLoadTime() = false

    /**
     * 统计加载时间
     */
    private fun trackLoadingTime() {
        if (!enableTrackLoadTime()) return
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            val TAG = this@BaseFragment.javaClass.simpleName
            val FRAGMENT_INFO =
                "${this@BaseFragment::class.java.simpleName}{${Integer.toHexString(this.hashCode())}}"
            var t1 = longArrayOf(0, 0, 0)

            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                t1[0] = System.currentTimeMillis()
            }

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                t1[1] = System.currentTimeMillis()
                //"$FRAGMENT_INFO costMills onCreate->onStart: ${t1[1]-t1[0]}".logd(TAG)
            }
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                t1[2] = System.currentTimeMillis()
                "$FRAGMENT_INFO costMills onCreate->onStart: ${t1[1] - t1[0]}, onStart->onResume:${t1[2] - t1[1]}, onCreate->onResume: ${t1[2] - t1[0]}".logd(
                    TAG
                )
                lifecycle.removeObserver(this)
            }
        })
    }

    /**
     * fragment是否在前台
     * @return
     */
    protected fun isAtFront(): Boolean {
        val v = view ?: return false
        return v.isLaidOut
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
inline fun <reified T : ViewBinding> Fragment.viewBind(): Lazy<T> =
    lazy {
        T::class.java.inflateMethod?.invoke(null, layoutInflater) as T
    }
