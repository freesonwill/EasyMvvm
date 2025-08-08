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
import com.walisport.module.topup.databinding.FragmentTopupRecordsBinding
import com.walisport.module.topup.ui.viewmodel.TopUpDetailViewModel
import com.walisport.module.topup.ui.viewmodel.TopUpRecordsViewModel
import kotlin.reflect.KClass


/**
 * 充值记录列表页
 */
class TopUpRecordsFragment : BaseFragment<TopUpRecordsViewModel, FragmentTopupRecordsBinding>() {

    override val vbClass: KClass<FragmentTopupRecordsBinding> = FragmentTopupRecordsBinding::class
    override val vmClass: KClass<TopUpRecordsViewModel> = TopUpRecordsViewModel::class

    private var defaultImmColor: Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        defaultImmColor = getStatusBarColor()
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.recharge_record.getString(), {
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