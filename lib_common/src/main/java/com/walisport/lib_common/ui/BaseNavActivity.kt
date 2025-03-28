package com.walisport.lib_common.ui

import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.annotation.NavigationRes
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.walisport.lib_base.data.viewmodel.EmptyViewModel
import com.walisport.lib_base.ui.BaseActivity
import com.walisport.lib_base.ui.viewBind
import com.walisport.lib_common.databinding.ActvityBaseNavBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author: zhangsan
 * @date: 2025/3/26 10:29
 * @description: 基础navigation的activity
 */
abstract class BaseNavActivity : BaseActivity<EmptyViewModel, ActvityBaseNavBinding>() {
    override val mViewModel: EmptyViewModel by viewModel()
    override val mBinding: ActvityBaseNavBinding by viewBind()
    protected fun findNavController(): NavController = mBinding.navHost.findNavController()

    @NavigationRes
    abstract fun navigationID(): Int

    @CallSuper
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.post {
            findNavController().setGraph(navigationID(),intent.extras)
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 返回到上一个 Fragment
                if (!findNavController().navigateUp()) {
                    finish()
                }
            }
        })
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

}