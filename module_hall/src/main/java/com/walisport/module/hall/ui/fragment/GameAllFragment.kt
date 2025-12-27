package com.walisport.module.hall.ui.fragment

import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.checkCurrentScrollState
import arch.cayenne.lib.common.utils.ext.onScrolledOver
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.NestedScrollViewBackToTopHelper
import com.walisport.module.hall.data.GameAllContentData
import com.walisport.module.hall.databinding.FragmentGameAllBinding
import com.walisport.module.hall.ui.adapter.GameAllHeaderAdapter
import com.walisport.module.hall.ui.adapter.GameAllHeaderViewHolder
import com.walisport.module.hall.ui.adapter.GameAllListAdapter
import com.walisport.module.hall.ui.adapter.GameAllListViewHolder
import com.walisport.module.hall.ui.adapter.GameAllRankingAdapter
import com.walisport.module.hall.ui.viewmodel.GameAllViewModel
import com.walisport.module.hall.ui.viewmodel.HallViewModel
import com.walisport.module.live.data.EventClick
import kotlin.reflect.KClass

class GameAllFragment : BaseFragment<GameAllViewModel, FragmentGameAllBinding>() {
    companion object {
        fun newInstance() = GameAllFragment()
    }

    var index: Int = 0

    private val headerAdapter by lazy {
        GameAllHeaderAdapter(object : GameAllHeaderViewHolder.OnHeaderItemClickListener {
            override fun onInviteFriendItemClick() {
                navigate(arch.cayenne.lib.res.R.string.nav_module_invite_friends_fragment.deeplink())
            }

            override fun onCompetitionItemClick() {
                navigate(arch.cayenne.lib.res.R.string.nav_module_competition_fragment.deeplink())
            }
        })
    }

    private val listAdapter by lazy {
        GameAllListAdapter(object : GameAllListViewHolder.OnAllItemClickListener {
            override fun onItemClick(data: GameAllContentData) {
                navigate(Uri.parse("walisport://module_hall/hallCategoryFragment?category=${data.category}&name=${data.name}"))
            }

            override fun onChildItemClick(gameId:Long) {
                mViewModel.setIsClickGame(EventClick.EVENT_CLICK_ACK_TRUE.type)
                navigate(arch.cayenne.lib.res.R.string.nav_module_gamedetail.deeplink("gameId" to gameId))
            }
        })
    }
    private val rankingAdapter by lazy {
        GameAllRankingAdapter(
            parentFragmentManager,
            childFragmentManager,
            lifecycle
        ){
            mBinding.nestedScrollView.requestLayout()
            //更新rvContent指定position
            // 更新 rvContent 指定 position
          //  mBinding.rvContent.adapter?.notifyItemChanged(headerAdapter.itemCount - 1)
            LogUtils.e("GameAllRankingViewHolder", "onPageSelected height-nestedScrollView=${mBinding.nestedScrollView.height}")
        }
    }


    override val vbClass: KClass<FragmentGameAllBinding> = FragmentGameAllBinding::class
    override val vmClass: KClass<GameAllViewModel> = GameAllViewModel::class
    private val hallViewModel: HallViewModel by sharedViewModel<HallViewModel, HallFragment>()
    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            val concatAdapter = ConcatAdapter(
                headerAdapter,
                listAdapter,
                rankingAdapter
            )
            rvContent.layoutManager = LinearLayoutManager(requireContext())
            rvContent.adapter = concatAdapter
            rvContent.setItemViewCacheSize(10)
            NestedScrollViewBackToTopHelper(nestedScrollView, ivBackToTop)
        }

    }

    override fun initData() {
        mViewModel.getAllList()
        super.initData()
    }

    override fun initListener() {
        mBinding.rvContent.onScrolledOver(100f, 80f, {
            hallViewModel.setScorll(true)
        }, {
            hallViewModel.setScorll(false)
        })

        // 监听 RecyclerView 滚动状态变更
        mBinding.rvContent.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // 这里可以根据 newState 处理滚动状态变更
                // 例如：RecyclerView.SCROLL_STATE_IDLE、SCROLL_STATE_DRAGGING、SCROLL_STATE_SETTLING
                hallViewModel.setScrollState(newState)
            }
        })
    }


    //循环请求数据
    override suspend fun createObserver() {
        //title列表
        mViewModel.gameRecentList.observe(viewLifecycleOwner) {
            it.let {
                mViewModel.queryGameList(it[index].category, it[index])
                index += 1
            }
        }

        //接收全部参数
        mViewModel.gameListLiveData.observe(viewLifecycleOwner) {
            listAdapter.submitList(it)
            //LogUtils.e("response------all--getGameTitleSize-${mViewModel.getGameTitleSize()},index-${index}")
            if (index < (mViewModel.getGameTitleSize())) {
                var gameData = mViewModel.getGame(index)
                gameData?.let {
                    mViewModel.queryGameList(
                        gameData.category,gameData)/**/
                }
            }
            index += 1
        }
    }

    override fun onStart() {
        super.onStart()
        // headerAdapter.restProBannerJob(mBinding.rvContent)
    }

    override fun onStop() {
        super.onStop()
        // headerAdapter.stopProBannerJob(mBinding.rvContent)
    }

    override fun onResume() {
        super.onResume()
        mBinding.rvContent.post {
            mBinding.rvContent.checkCurrentScrollState(100f, 80f, {
                hallViewModel.setScorll(true)
            }, {
                hallViewModel.setScorll(false)
            })
        }
    }
}