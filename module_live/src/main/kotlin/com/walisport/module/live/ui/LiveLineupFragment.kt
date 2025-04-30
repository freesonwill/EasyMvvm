package com.walisport.module.live.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.bumptech.glide.Glide
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.live.R
import com.walisport.module.live.data.PlayerPosition
import com.walisport.module.live.databinding.FragmentLiveLineupBinding
import com.walisport.module.live.databinding.LineupHeadBinding
import com.walisport.module.live.databinding.LineupRepairItemAwayBinding
import com.walisport.module.live.databinding.LineupRepairItemHomeBinding
import com.walisport.module.live.databinding.LineupSubstitutionItemBinding
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
    private val allPlayerInfo = mutableListOf<LineupPlayerInfo>()
    private val headHeightViewNumber = 44.dp2px
    private val headWidthViewNumber = 100.dp2px
    //用于隐藏布局
    private var isIncidents :Boolean= false
    private var isSubstitutes :Boolean= false

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.geMatchLineupDetail(mainViewModel.matchId)
      //  mViewModel.geMatchLineupDetail(458436)
    }
    override fun initListener() {
    }
    override fun createObserver() {
        mViewModel.matchLineupDetail.observe(viewLifecycleOwner) {it->
            it?.let {
                mBinding.main.setVisibilityGone()
                upData(it)
                if(it.awayOrBuilderList.isEmpty()){
                    mBinding.main.setState(DynamicStateLayout.States.DATA_EMPTY, R.string.lineup_empty.getString())
                }
            } ?: run {
                    mBinding.main.setState(DynamicStateLayout.States.DATA_EMPTY, R.string.lineup_empty.getString())
            }
        }
    }
    // repeated Player home = 6;        // 主队阵型球员列表
    //  repeated Player away = 7;        // 客队阵型球员列表
    @SuppressLint("MissingInflatedId", "CutPasteId")
    private fun upData(data: Sloth.MatchLineupDetail) {
        initPlayer(data)
        Glide.with(this).load(data.homeLogo).into( mBinding.ivNationalFlagTop)
        Glide.with(this).load(data.awayLogo).into( mBinding.ivNationalFlagBottom)
        mBinding.tvNationalNameTop.text = data.homeFormation
        mBinding.tvNationalNameBottom.text = data.awayFormation
        Glide.with(this).load(data.homeLogo).into( mBinding.homeIncidentsLogo)
        Glide.with(this).load(data.awayLogo).into( mBinding.awayIncidentsLogo)
        Glide.with(this).load(data.homeLogo).into( mBinding.homeSubstituteLogo)
        Glide.with(this).load(data.awayLogo).into( mBinding.awaySubstituteLogo)
        mainViewModel.matchMainMatch.value?.basicInfo.let {
            mBinding.homeSubstituteName.text = it?.homeTeam
            mBinding.awaySubstituteName.text = it?.awayTeam
            mBinding.homeIncidentsName.text = it?.homeTeam
            mBinding.awayIncidentsName.text = it?.awayTeam
        }

        LogUtils.dTag(TAG, "MatchLineupDetail----->${data}")
        data.homeOrBuilderList.forEach { i ->
            //是否是首发
            if(i.first==1) {
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
                val view = orBuilderList(true,i)
                view.layoutParams = params
                mBinding.sclLineupItemTop.addView(view)
                //主队换人
                incidents(i.incidentsList,i.position,true)
            }else{
                isSubstitutes = true
                substituteHome(i)
            }
        }
        data.awayOrBuilderList.forEach { i ->
            //是否是首发
            if(i.first==1) {
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
                val view = orBuilderList(false,i)
                view.layoutParams = params
                mBinding.sclLineupItemBottom.addView(view)
                //客队换人
                incidents(i.incidentsList,i.position,false)
            }else{
                isSubstitutes = true
                substituteAway(i)
            }
        }
        mBinding.sllIncidentsMain.visibility = if (isIncidents) View.VISIBLE else View.GONE
        mBinding.sllSubstituteMain.visibility = if (isSubstitutes) View.VISIBLE else View.GONE
    }
    //通过first判断球员是不是替补
    private fun substituteHome(data: Sloth.PlayerOrBuilder){
                val binding = LineupRepairItemHomeBinding.inflate(LayoutInflater.from(context),mBinding.llcHomeSubstitute, false)
                binding.apply {
                    Glide.with(this@LiveLineupFragment).load(data.logo) .error(R.drawable.icon_lineuup_head) .into(ivLogo )
                    stvName.text = data.name
                    tvNumber.text = data.shirtNumber.toString()
                    tvPosition.text = getPositionFromString(data.position)?.description
                }
                mBinding.llcHomeSubstitute.addView(binding.root)
    }
    private fun substituteAway(data: Sloth.PlayerOrBuilder){
        val binding = LineupRepairItemAwayBinding.inflate(LayoutInflater.from(context),mBinding.llcAwaySubstitute, false)
        binding.apply {
            Glide.with(this@LiveLineupFragment).load(data.logo) .error(R.drawable.icon_lineuup_head) .into(ivLogo )
            stvName.text = data.name
            tvNumber.text = data.shirtNumber.toString()
            tvPosition.text = getPositionFromString(data.position)?.description
        }
        mBinding.llcAwaySubstitute.addView(binding.root)
    }


        // string position = 9;       // 球员位置，F-前锋、M-中场、D-后卫、G-守门员
        @SuppressLint("SetTextI18n")
        private fun incidents(list:List<Sloth.PlayerIncident>, positionName:String,isHome:Boolean){
            list.forEach{itData->
                isIncidents = true
                LogUtils.d("homeIncidents,itData.inPlayer-name${itData.inPlayer.name}---itData.outPlayer-name${itData.outPlayer.name}")
                if (itData.inPlayer.name.isNotEmpty()){
                val binding = LineupSubstitutionItemBinding.inflate(LayoutInflater.from(context), if (isHome)mBinding.llcHome else mBinding.llcAway, false)
                binding.apply {
                    homeTopName.text = itData.inPlayer.name
                    homePositionName.text =getPositionFromString(positionName)?.description
                    homeTopNumber.text = allPlayerInfo.find { it.id==itData.inPlayer.id}?.shirtNumber.toString()
                    Glide.with(this@LiveLineupFragment).load(allPlayerInfo.find { it.id==itData.inPlayer.id}?.logUrl) .error(R.drawable.icon_lineuup_head) .into( homeTopLogo)
                    homeTopBottom.text ="${itData.time}'"
                    homeBottomName.text = itData.outPlayer.name
                    homeBottomPosition.text = getPositionFromString(positionName)?.description
                    homeBottomNumber.text =  allPlayerInfo.find { it.id==itData.outPlayer.id}?.shirtNumber.toString()
                    Glide.with(this@LiveLineupFragment).load(allPlayerInfo.find { it.id==itData.outPlayer.id}?.logUrl) .error(R.drawable.icon_lineuup_head) .into( homeBottomLogo)
                    homeTopNumber.setBackgroundResource( if (isHome)R.drawable.circle_badge else R.drawable.circle_badge_blue)
                    homeBottomNumber.setBackgroundResource( if (isHome)R.drawable.circle_badge else R.drawable.circle_badge_blue)
                }
                    if(isHome){
                        mBinding.llcHome.addView(binding.root)
                    }else{
                        mBinding.llcAway.addView(binding.root)
                    }

                }
            }
        }

    private fun orBuilderList(isTopView:Boolean, data: Sloth.PlayerOrBuilder): View {
        val binding = LineupHeadBinding.inflate(LayoutInflater.from(context), if (isTopView)mBinding.sclLineupItemTop else mBinding.sclLineupItemBottom, false)
        binding.apply {
          shirtNumber.text = data.shirtNumber.toString()
            tvName.text = data.name
            Glide.with(this@LiveLineupFragment).load(data.logo)
                .error(R.drawable.icon_lineuup_head)
                .into(imageLogo)
            shirtNumber.setBackgroundResource( if (isTopView)R.drawable.circle_badge else R.drawable.circle_badge_blue)
        }
       return binding.root
    }

    private fun initPlayer(data: Sloth.MatchLineupDetail){
        data.homeOrBuilderList.forEach { i ->
            allPlayerInfo.add(LineupPlayerInfo(id = i.id, logUrl = i.logo,shirtNumber = i.shirtNumber))
        }
        data.awayOrBuilderList.forEach { i ->
            allPlayerInfo.add(LineupPlayerInfo(id = i.id, logUrl = i.logo,shirtNumber = i.shirtNumber))
    }
    }
    private fun getPositionFromString(position: String): PlayerPosition? {
        return try {
            enumValueOf<PlayerPosition>(position)
        } catch (e: IllegalArgumentException) {
            null // 如果字符串不匹配任何枚举值，返回 null
        }
    }
    data class LineupPlayerInfo(val id:Int,val logUrl:String,val shirtNumber:Int)
}