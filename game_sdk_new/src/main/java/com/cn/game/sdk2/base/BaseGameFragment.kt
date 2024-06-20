package com.cn.game.sdk2.base

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.base.fragment.BaseVmDbFragment
import com.xcjh.base_lib.utils.dismissLoadingExt
import com.xcjh.base_lib.utils.showLoadingExt
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class BaseGameFragment <VM : BaseViewModel, VB : ViewDataBinding> : BaseVmDbFragment<VM, VB>() {
    private val TAG = this::class.java.simpleName

    abstract override fun initView(savedInstanceState: Bundle?)

    /**
     * 懒加载 只有当前fragment视图显示时才会触发该方法
     */
    override fun lazyLoadData() {}

    /**
     * 创建LiveData观察者 Fragment执行onViewCreated后触发
     */
    override fun createObserver() {}

    /**
     * Fragment执行onViewCreated后触发
     */
    override fun initData() {

    }

    /**
     * 打开等待框
     */
    override fun showLoading(message: String) {
        showLoadingExt(message)
    }

    /**
     * 关闭等待框
     */
    override fun dismissLoading() {
        dismissLoadingExt()
    }

    fun finishFragClick() {
        parentFragmentManager.popBackStack()
    }

    open fun finishTopClick(view: View?) {
        activity?.finish()
    }

    /**
     * 延迟加载 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿  bug
     * 这里传入你想要延迟的时间，延迟时间可以设置比转场动画时间长一点 单位： 毫秒
     * 不传默认 300毫秒
     * @return Long
     */
    override fun lazyLoadTime(): Long {
        return 300
    }

    /**
     * 泛型的高级特性 泛型实例化
     * 跳转
     */
    inline fun <reified T> startNewActivity(block: Intent.() -> Unit = {}) {
        val intent = Intent(this.activity, T::class.java)
        //把intent实例 传入block 函数类型参数
        intent.block()
        startActivity(intent)
    }

    /**
     * 播放透明度动画
     */
    protected suspend fun playAlphaAnimTogether(dic:List<View>, duration:Long, count:Int){
        suspendCoroutine { continuation ->
            val animatorSet = AnimatorSet()
            val animators = dic.map { maskView->
                val animator = ObjectAnimator.ofFloat(maskView, "alpha", 1f, 0f, 1f).apply {
                    this.duration = duration // 设置动画持续时间
                    this.repeatCount = count // 设置无限循环
                    this.repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    this.addListener( onStart = {
                        maskView.isVisible = true
                        Log.d(TAG,"maskView-->${maskView} visible true")
                    }, onEnd = {
                        it.cancel()
                        maskView.isVisible = false
                        Log.d(TAG,"maskView-->${maskView} visible false")
                    })
                }
                animator
            }
            animatorSet.playTogether(animators)
            animatorSet.addListener(onEnd = {
                dic.forEach {
                    it.isVisible = false
                    Log.d(TAG,"maskView-->${it} visible false")
                }
                continuation.resume("")
            })
            animatorSet.start()
        }
    }

}