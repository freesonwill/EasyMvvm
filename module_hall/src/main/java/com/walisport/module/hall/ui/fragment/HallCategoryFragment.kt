package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.hall.databinding.FragmentHallCategoryBinding
import kotlin.reflect.KClass

class HallCategoryFragment: BaseFragment<EmptyViewModel, FragmentHallCategoryBinding>() {
    override val vbClass: KClass<FragmentHallCategoryBinding> = FragmentHallCategoryBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class


    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            titleBar.loadGeneralTitleBar("老虎機")
        }
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }
}