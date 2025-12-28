package com.walisport.module.me.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import com.walisport.module.me.databinding.FragmentMatchCollectionsBinding
import com.walisport.module.me.ui.viewmodel.MeVIPInfoViewModel
import com.walisport.module.me.ui.viewmodel.MeViewModel
import kotlin.reflect.KClass
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
/**
 *
 * @date: 2025/10/17 16:52
 * @description:
 */
class MatchCollectionsFragment : BaseFragment<MeVIPInfoViewModel, FragmentMatchCollectionsBinding>() {

    override val vbClass: KClass<FragmentMatchCollectionsBinding> = FragmentMatchCollectionsBinding::class
    override val vmClass: KClass<MeVIPInfoViewModel> = MeVIPInfoViewModel::class
    private val parentViewModel: MeViewModel by viewModels({ requireParentFragment() })
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.clDynamics.visibility = View.VISIBLE
        mBinding.clDynamics.setState(
            DynamicStateLayout.States.DATA_EMPTY,
            com.walisport.module.business.common.R.string.game_data_empty.getString()
        )
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }

    override fun onResume() {
        if (mBinding.clDynamics.visibility == View.VISIBLE)
            parentViewModel.setOnHeight(mBinding.clDynamics.height)
        else
            if (mBinding.rvRecently.height == 0)
                parentViewModel.setOnHeight(mBinding.clDynamics.height)
            else
                parentViewModel.setOnHeight(mBinding.rvRecently.height)
        super.onResume()
    }
}