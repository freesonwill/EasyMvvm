package com.xcjh.app.ui.details.fragment

import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.drake.brv.BindingAdapter
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.divider
import com.drake.brv.utils.grid
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.xcjh.app.R
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.databinding.FragmentDetailTabLiveBinding
import com.xcjh.app.databinding.ItemMainLiveListBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.ui.home.home.tab.setLiveMatchItem
import com.xcjh.app.utils.smartListData
import com.xcjh.app.utils.smartPageListData
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.dp2px

/**
 * 其他直播间列表
 * matchType 1足球，2篮球
 */
class DetailLiveFragment(var matchId: String, var matchType: String) :
    BaseVpFragment<DetailVm, FragmentDetailTabLiveBinding>() {
    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }
    override val typeId: Long
        get() = 6

    override fun initView(savedInstanceState: Bundle?) {
        matchId = if (matchId.contains("_")) {
            ""
        } else {
            matchId
        }
        //初始化直播列表
        mDatabind.rcvRecommend.grid(2)
           /* .divider {
                // setDrawable(R.drawable.divider_horizontal)
                setDivider(16, true)
                setColor("#666777")
                orientation = DividerOrientation.GRID
            }*/
            .setup {
                addType<BeingLiveBean>(R.layout.item_main_live_list)
                onBind {
                    when (itemViewType) {
                        R.layout.item_main_live_list -> {
                            setLiveMatchItem(1)
                        }
                    }
                }
                R.id.llLiveSpacing.onClick {
                    val bean = _data as BeingLiveBean
                    // 先退出当前房间，移除监听器， 然后才能进入新的房间
                    MatchDetailActivity.open(
                        matchType,
                        bean.matchId,
                        bean.competitionName,
                        bean.userId,
                        bean.playUrl
                    )
                }
            }
        // 下拉刷新
        mDatabind.page.onRefresh {
            mViewModel.getNowLive(true, matchType, matchId)
        }
        // 上拉加载
        mDatabind.page.onLoadMore {
            mViewModel.getNowLive(false, matchType, matchId)
        }
        loadData()
    }


    override fun lazyLoadData() {
        // mDatabind.page.showLoading()
    }

    private fun loadData() {
        mDatabind.page.showLoading()
        // mViewModel.getNowLive(true,matchType,matchId)
    }

    override fun createObserver() {
        //直播列表接口返回监听处理
        mViewModel.liveList.observe(this) {
            if (it != null) {
                smartPageListData(it, mDatabind.rcvRecommend, mDatabind.page)
            }
        }

        vm.anchorInfo.observe(this) {
            //切换直播间
            matchId = if (it.liveId.contains("_")) {
                ""
            } else {
                it.liveId
            }
            mDatabind.page.refresh()
        }
    }

}

