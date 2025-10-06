package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.hall.R
import com.walisport.module.hall.databinding.FragmentHallBinding
import com.walisport.module.hall.ui.viewmodel.HallViewModel
import kotlin.reflect.KClass

/**
 * 游戏大厅界面
 */

class HallFragment : BaseFragment<HallViewModel, FragmentHallBinding>() {

    override val vbClass: KClass<FragmentHallBinding> = FragmentHallBinding::class
    override val vmClass: KClass<HallViewModel> = HallViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.hall, {
            findNavController().navigateUp()
        })
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }

}