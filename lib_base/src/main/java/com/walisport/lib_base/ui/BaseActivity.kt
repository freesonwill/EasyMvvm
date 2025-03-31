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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersionPre()
        setContentView(mBinding.root)
        immersionPost()
        initView(savedInstanceState)
        initListener()
        initData()
        createObserver()
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
