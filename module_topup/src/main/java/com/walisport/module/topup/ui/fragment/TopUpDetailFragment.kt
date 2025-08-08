package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentTopupDetailBinding
import com.walisport.module.topup.databinding.FragmentTopupMainBinding
import com.walisport.module.topup.ui.viewmodel.TopUpDetailViewModel
import com.walisport.module.topup.ui.viewmodel.TopUpMainViewModel
import kotlin.reflect.KClass


/**
 * 充值记录详情页
 */
class TopUpDetailFragment : BaseFragment<TopUpDetailViewModel, FragmentTopupDetailBinding>() {

    override val vbClass: KClass<FragmentTopupDetailBinding> = FragmentTopupDetailBinding::class
    override val vmClass: KClass<TopUpDetailViewModel> = TopUpDetailViewModel::class

    private var defaultImmColor: Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        defaultImmColor = getStatusBarColor()
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.fragment_topup_detail_title.getString(), {
                findNavController().navigateUp()
            })
        }
    }

    override fun onStart() {
        super.onStart()
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
    }

    override fun initListener() {
        mBinding.root.touchBackPressed()
    }

    override suspend fun createObserver() {

    }


    override fun initData() {
        super.initData()
    }
}