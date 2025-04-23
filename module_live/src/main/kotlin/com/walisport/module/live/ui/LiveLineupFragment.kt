package com.walisport.module.live.ui

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.ui.widget.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.bumptech.glide.Glide
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveLineupBinding
import com.walisport.module.live.ui.viewmodel.LiveLineupViewModel
import com.walisport.module.live.viewmodel.LiveMainViewModel
import galaxy.client.proto.Sloth
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
                mBinding.main.setVisibilityGone()
                upData(it)
            } ?: run {
                mBinding.main.setState(DynamicStateLayout.States.DATA_EMPTY, R.string.lineup_empty.getString())
            }
        }
    }
    // repeated Player home = 6;        // 主队阵型球员列表
    //  repeated Player away = 7;        // 客队阵型球员列表
    @SuppressLint("MissingInflatedId", "CutPasteId")
    private fun upData(data: Sloth.MatchLineupDetail) {
        Glide.with(this).load(data.homeLogo).error(R.drawable.icon_lineuup_head).into( mBinding.ivNationalFlagTop)
        Glide.with(this).load(data.awayLogo).error(R.drawable.icon_lineuup_head).into( mBinding.ivNationalFlagBottom)
        mBinding.tvNationalNameTop.text = data.homeFormation
        mBinding.tvNationalNameBottom.text = data.awayFormation
        val headHeightViewNumber = 44.dp2px
        val headWidthViewNumber = 100.dp2px
        LogUtils.dTag(TAG, "MatchLineupDetail----->${data}")
        data.homeOrBuilderList.forEach { i ->
            //是否是首发
            if(i.first==1) {
                val newView = LayoutInflater.from(context).inflate(R.layout.lineup_head, mBinding.sclLineupItemTop, false)
                val x = mViewModel.lineupArrangementXY(
                    headWidthViewNumber,
                    mBinding.sclLineupItemTop.width,
                    i.x
                )
                val y = mViewModel.lineupArrangementXY(
                    headHeightViewNumber,
                    mBinding.sclLineupItemTop.height,
                    i.y
                )
                val params = RelativeLayout.LayoutParams(
                    headWidthViewNumber,
                    headHeightViewNumber
                ).apply {
                    leftMargin = x
                    topMargin = y
                }
                newView.apply {
                    findViewById<TextView>(R.id.shirtNumber).text = i.shirtNumber.toString()
                    findViewById<TextView>(R.id.tvName).text = i.name
                    Glide.with(this).load(i.logo)
                        .error(R.drawable.lineup_head)
                        .into( findViewById(R.id.imageLogo))
                }
                newView.layoutParams = params
                mBinding.sclLineupItemTop.addView(newView)
            }
        }
        data.awayOrBuilderList.forEach { i ->
            //是否是首发
            if(i.first==1) {
                val newView = LayoutInflater.from(context).inflate(R.layout.lineup_head_blue, mBinding.sclLineupItemBottom, false)
                val x = mViewModel.lineupArrangementXY(
                    headWidthViewNumber,
                    mBinding.sclLineupItemBottom.width,
                    i.x,true
                )
                val y = mViewModel.lineupArrangementXY(
                    headHeightViewNumber,
                    mBinding.sclLineupItemBottom.height,
                    i.y,true
                )
                val params = RelativeLayout.LayoutParams(
                    headWidthViewNumber,
                    headHeightViewNumber
                ).apply {
                    leftMargin = x
                    topMargin = y
                }
                newView.apply {
                    findViewById<TextView>(R.id.shirtNumber).text = i.shirtNumber.toString()
                    findViewById<TextView>(R.id.tvName).text = i.name
                    Glide.with(this).load(i.logo)
                        .error(R.drawable.lineup_head)
                        .into( findViewById(R.id.imageLogo))
                }
                newView.layoutParams = params
                mBinding.sclLineupItemBottom.addView(newView)
            }

        }}
}