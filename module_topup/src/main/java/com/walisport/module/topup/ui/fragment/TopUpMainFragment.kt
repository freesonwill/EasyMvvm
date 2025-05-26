package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentTopupMainBinding
import com.walisport.module.topup.ui.viewmodel.TopUpMainViewModel
import kotlin.reflect.KClass


/**
 * 充值主页
 */
class TopUpMainFragment : BaseFragment<TopUpMainViewModel, FragmentTopupMainBinding>() {

    override val vbClass: KClass<FragmentTopupMainBinding> = FragmentTopupMainBinding::class
    override val vmClass: KClass<TopUpMainViewModel> = TopUpMainViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.fragment_title.getString(), {
                findNavController().navigateUp()
            })


        }
    }


    override fun initListener() {

    }

    override fun createObserver() {

    }


    override fun initData() {
        super.initData()
    }


}