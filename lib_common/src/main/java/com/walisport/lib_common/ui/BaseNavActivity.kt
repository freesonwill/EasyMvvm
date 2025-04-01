package com.walisport.lib_common.ui

import android.os.Bundle
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
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp() || super.onSupportNavigateUp()
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

}