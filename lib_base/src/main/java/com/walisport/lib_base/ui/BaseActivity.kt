package com.walisport.lib_base.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.walisport.lib_base.data.viewmodel.BaseViewModel
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
abstract class BaseActivity<VM : BaseViewModel,VB : ViewBinding> : AppCompatActivity(), IView {
    protected open val TAG = this.javaClass.simpleName
    protected abstract val mBinding: VB
    protected abstract val mViewModel: VM
    //是否第一次加载
    private var isFirst: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersionPre()
        setContentView(mBinding.root)
        immersionPost()
        mViewModel.onInit()
        initView(savedInstanceState)
        initListener()
        initData()
        createObserver()
    }

    override fun onResume() {
        super.onResume()
        onVisible()
    }

    /**
     * 沉浸式（setContentView之前）
     */
    private fun immersionPre(){
        enableEdgeToEdge()
    }

    /**
     * 沉浸式（setContentView之后）
     */
    private fun immersionPost(){
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * 是否需要懒加载
     */
    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            // 延迟加载 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿
            lifecycleScope.launch {
                delay(lazyLoadTime())
                lazyLoadData()
                //在Fragment中，只有懒加载过了才能开启网络变化监听
                isFirst = false
            }
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
