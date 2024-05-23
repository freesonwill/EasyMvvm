package com.xcjh.app.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.xcjh.app.utils.SoundManager
import com.xcjh.app.utils.dismissLoadingExt
import com.xcjh.app.utils.showLoadingExt
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.base.fragment.BaseVmDbFragment
import com.xcjh.base_lib.base.fragment.BaseVmVbFragment

/**
 *  @author zobo 2023.02.15
 */
abstract class BaseFragment<VM : BaseViewModel, VB : ViewDataBinding> : BaseVmDbFragment<VM, VB>() {


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
        SoundManager.playMedia()
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

}