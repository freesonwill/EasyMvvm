package com.walisport.module.setting.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.setting.R
import com.walisport.module.setting.data.BackgroundViewModel
import com.walisport.module.setting.databinding.FragmentBackgroundBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 背景设置
 */

class BackgroundFragment: BaseFragment<BackgroundViewModel, FragmentBackgroundBinding>() {

    override val mBinding: FragmentBackgroundBinding by viewBind()
    override val mViewModel: BackgroundViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.titleBar.loadBackgroundTitleBar(R.string.menu_background_set.getString(),R.string.cancel.getString(),R.string.confirm.getString(),{
            findNavController().navigateUp()
        },{//确定

        })

    }

    override fun createObserver() {
    }
}