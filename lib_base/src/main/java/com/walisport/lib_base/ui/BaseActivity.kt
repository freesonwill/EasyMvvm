package com.walisport.lib_base.ui

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.walisport.lib_base.data.viewmodel.BaseViewModel
import com.walisport.lib_base.ui.interface_.IStatusBar
import com.walisport.lib_base.ui.interface_.IView
import com.walisport.lib_base.utils.CommonUtils.inflateMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @author: zhangsan
 * @date: 2025/3/14 09:48
 * @description:
 */
abstract class BaseActivity<VM : BaseViewModel,VB : ViewBinding> : AppCompatActivity(), IView, IStatusBar {
    protected open val TAG = this.javaClass.simpleName
    protected abstract val mBinding: VB
    protected abstract val mViewModel: VM
    //是否第一次加载
    private var isFirst: Boolean = true

    //设置颜色，默认根据主题颜色设定
    private val statusBar:IStatusBar by lazy { StatusBarDelegate(this)  }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        initView(savedInstanceState)
        initListener()
        createObserver()
        setStatusBar(configStatusBar())
    }

    override fun setStatusBar(config: IStatusBar.Config) {
        statusBar.setStatusBar(config)
    }

    override fun configStatusBar():IStatusBar.Config = IStatusBar.Config()
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
 * ViewBinding.inflate(layoutInflater)
 * @param T
 * @return
 */
inline fun <reified T : ViewBinding> Activity.viewBind(): Lazy<T> =
    lazy {
        T::class.java.inflateMethod?.invoke(null, layoutInflater) as T
    }
