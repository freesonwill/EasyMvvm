package com.walisport.module.live.ui

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.viewBind
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.extension.sharedViewModel
import arch.cayenne.lib.common.ui.widget.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveLineupBinding
import com.walisport.module.live.databinding.FragmentLiveMainBinding
import com.walisport.module.live.ui.viewmodel.LiveLineupViewModel
import com.walisport.module.live.viewmodel.LiveMainViewModel
import galaxy.client.proto.Sloth
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

/**
 * 阵容
 * 描述:live_lineup_item_layout and live_lineup_item_bottom_layout 列表控件根据数据动态添加lineup_head 数据
 */
class LiveLineupFragment : BaseFragment<LiveLineupViewModel, FragmentLiveLineupBinding>() {
    override val vbClass: KClass<FragmentLiveLineupBinding> = FragmentLiveLineupBinding::class
    override val vmClass: KClass<LiveLineupViewModel> = LiveLineupViewModel::class
    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.geMatchLineupDetail(mainViewModel.matchId)
    }

    override fun initListener() {

    }

    override fun createObserver() {
        mViewModel.matchLineupDetail.observe(viewLifecycleOwner) {
            it?.let {
                mBinding.main.setVisibilityGone()
                upData(it)
            } ?: run {
                mBinding.main.setState(DynamicStateLayout.States.DATA_EMPTY, R.string.lineup_empty.getString())
            }
        }
    }

    private fun upData(data: Sloth.MatchLineupDetail) {
        LogUtils.dTag(TAG, "MatchLineupDetail----->${data}")
    }
}