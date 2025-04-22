package com.walisport.module.live.ui

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.viewBind
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.databinding.TittleBarDefaultBinding
import arch.cayenne.lib.common.extension.sharedViewModel
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveLineupBinding
import com.walisport.module.live.databinding.FragmentLiveMainBinding
import com.walisport.module.live.databinding.LineupHeadBinding
import com.walisport.module.live.databinding.LineupHeadBlueBinding
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
//        mViewModel.geMatchLineupDetail(mainViewModel.matchId)
        mViewModel.geMatchLineupDetail(458436)
    }

    override fun initListener() {

    }

    override fun createObserver() {
        mViewModel.matchLineupDetail.observe(viewLifecycleOwner) {
            it?.let {
                upData(it)
            } ?: run {

            }
        }
    }

    // repeated Player home = 6;        // 主队阵型球员列表
    //  repeated Player away = 7;        // 客队阵型球员列表
    @SuppressLint("MissingInflatedId")
    private fun upData(data: Sloth.MatchLineupDetail) {
        LogUtils.dTag(TAG, "MatchLineupDetail----->${data}")
        data.homeOrBuilderList.forEach { i ->
            //是否是首发
            if(i.first==1) {
                val newView = LayoutInflater.from(context).inflate(R.layout.lineup_head, mBinding.sclLineupItemTop, false)
                val sclLineupItem = newView.findViewById<LinearLayoutCompat>(R.id.clLineupHead)
                val x = mViewModel.lineupArrangementX(
                    sclLineupItem.width,
                    mBinding.sclLineupItemTop.width,
                    i.x
                )
                val y = mViewModel.lineupArrangementY(
                    sclLineupItem.height,
                    mBinding.sclLineupItemTop.height,
                    i.y
                )
                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    leftMargin = x // X 坐标
                    topMargin = y // Y 坐标
                }
                newView.layoutParams = params
                mBinding.sclLineupItemTop.addView(sclLineupItem)
            }
        }
        data.awayOrBuilderList.forEach { i ->
            //是否是首发
            if(i.first==1) {
                val newView = LayoutInflater.from(context).inflate(R.layout.lineup_head_blue, mBinding.sclLineupItemBottom, false)
                val sclLineupItem = newView.findViewById<ConstraintLayout>(R.id.clLineupHead)
                val x = mViewModel.lineupArrangementX(
                    sclLineupItem.width,
                    mBinding.sclLineupItemBottom.width,
                    i.x
                )
                val y = mViewModel.lineupArrangementY(
                    sclLineupItem.height,
                    mBinding.sclLineupItemBottom.height,
                    i.y
                )
                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    leftMargin = x // X 坐标
                    topMargin = y // Y 坐标
                }
                newView.layoutParams = params
                mBinding.sclLineupItemBottom.addView(newView)
            }

    }}
}