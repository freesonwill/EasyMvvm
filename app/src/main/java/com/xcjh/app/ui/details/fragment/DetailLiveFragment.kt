package com.xcjh.app.ui.details.fragment

import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.xcjh.app.R
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.databinding.FragmentDetailTabLiveBinding
import com.xcjh.app.databinding.ItemMainLiveListBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.utils.smartListData
import com.xcjh.app.utils.smartPageListData
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.dp2px

/**
 * 其他直播间列表
 * matchType 1足球，2篮球
 */
class DetailLiveFragment(var matchId: String,var matchType: String) : BaseVpFragment<DetailVm, FragmentDetailTabLiveBinding>() {
    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }
    override val typeId: Long
        get() = 6
    override fun initView(savedInstanceState: Bundle?) {
        matchId = if (matchId.contains("_")){
            ""
        }else{
            matchId
        }
        //初始化直播列表
        mDatabind.rcvRecommend.grid(2).setup {
            addType<BeingLiveBean>(R.layout.item_main_live_list)
            onBind {
                when (itemViewType) {
                    R.layout.item_main_live_list -> {
                        val bindingItem = getBinding<ItemMainLiveListBinding>()
                        val bean = _data as BeingLiveBean
                        Glide.with(context)
                            .load(bean.titlePage)
                            .error(R.drawable.main_top_load)
                            .placeholder(R.drawable.main_top_load)
                            .into(bindingItem.ivLiveBe)
                        Glide.with(context)
                            .load(bean.userLogo) // 替换为您要加载的图片 URL
                            .error(R.drawable.load_round)
                            .placeholder(R.drawable.load_round)
                            .into(bindingItem.ivLiveHead)
                        bindingItem.txtLiveName.text = bean.nickName
                        if(matchType=="1"){
                            bindingItem.txtLiveTeam.text="${bean.homeTeamName} VS ${bean.awayTeamName}"
                        }else{
                            bindingItem.txtLiveTeam.text="${bean.awayTeamName } VS ${bean.homeTeamName}"
                        }
                        bindingItem.txtLiveCompetition.text = bean.competitionName
                        if (bean.hotValue <= 9999) {
                            bindingItem.txtLiveHeat.text = "${bean.hotValue}"
                        } else {
                            bindingItem.txtLiveHeat.text = "9999+"
                        }
                        if (layoutPosition % 2 == 0) {
                            val layoutParams =
                                bindingItem.llLiveSpacing.layoutParams as ViewGroup.MarginLayoutParams
                            layoutParams.setMargins(0, 0, context.dp2px(13), context.dp2px(20))
                            bindingItem.llLiveSpacing.layoutParams = layoutParams
                        } else {
                            val layoutParams =
                                bindingItem.llLiveSpacing.layoutParams as ViewGroup.MarginLayoutParams
                            layoutParams.setMargins(0, 0, 0, context.dp2px(20))
                            bindingItem.llLiveSpacing.layoutParams = layoutParams
                        }
                    }
                }
            }
            R.id.llLiveSpacing.onClick {
                val bean = _data as BeingLiveBean
                //todo 先退出当前房间，移除监听器， 然后才能进入新的房间
                MatchDetailActivity.open(matchType,bean.matchId,bean.competitionName,bean.userId,bean.playUrl)
            }
        }
        // 下拉刷新
        mDatabind.page.onRefresh {
            mViewModel.getNowLive(true, matchType,matchId)
        }
        // 上拉加载
        mDatabind.page.onLoadMore {
            mViewModel.getNowLive(false, matchType,matchId)
        }

    }

    override fun lazyLoadData() {
        mDatabind.page.showLoading()
      //  mViewModel.getNowLive(true,matchType,matchId)
    }

    override fun createObserver() {
        //直播列表接口返回监听处理
        mViewModel.liveList.observe(this) {
            if (it != null) {
                smartPageListData(it,  mDatabind.rcvRecommend, mDatabind.page)
            }
        }

        vm.anchorInfo.observe(this) {
            //切换直播间
            matchId = if (it.liveId.contains("_")){
                ""
            }else{
                it.liveId
            }
            mDatabind.page.refresh()
        }
    }
}