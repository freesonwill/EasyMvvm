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
import arch.cayenne.lib.common.utils.helper.BackToTopHelper
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

class GameAllFragment: BaseFragment<GameAllViewModel, FragmentGameAllBinding>() {
    companion object {
        fun newInstance() = GameAllFragment()
    }

    private val headerAdapter by lazy {
        GameAllHeaderAdapter(object : GameAllHeaderViewHolder.OnHeaderItemClickListener {
            override fun onInviteFriendItemClick() {
                navigate(arch.cayenne.lib.res.R.string.nav_module_invite_friends_fragment.deeplink())
            }

            override fun onCompetitionItemClick() {
                navigate(arch.cayenne.lib.res.R.string.nav_module_competition_fragment.deeplink() )
            }
        })
    }

    private val listAdapter by lazy {
        GameAllListAdapter(object :GameAllListViewHolder.OnAllItemClickListener {
            override fun onItemClick(data: GameAllContentData) {
                navigate(Uri.parse("walisport://module_hall/hallCategoryFragment?category=${data.category}&name=${data.name}"))
            }

            override fun onChildItemClick() {
                mViewModel.setIsClickGame(EventClick.EVENT_CLICK_ACK_TRUE.type)
                navigate(arch.cayenne.lib.res.R.string.nav_module_gamedetail.deeplink() )
            }
        })
    }
    private val rankingAdapter by lazy {
        GameAllRankingAdapter(
            parentFragmentManager,
            childFragmentManager,
            lifecycle
        )
    }



    override val vbClass: KClass<FragmentGameAllBinding> = FragmentGameAllBinding::class
    override val vmClass: KClass<GameAllViewModel> = GameAllViewModel::class
    private val hallViewModel: HallViewModel by sharedViewModel<HallViewModel, HallFragment>()
    private var list : MutableList<GameAllContentData> = mutableListOf()
    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            val concatAdapter = ConcatAdapter(
                headerAdapter,
                listAdapter,
                rankingAdapter
            )
            rvContent.layoutManager = LinearLayoutManager(requireContext())
            rvContent.adapter = concatAdapter
            BackToTopHelper(rvContent, ivBackToTop, false)
        }
        mViewModel.mockAllList(1)
    }

    override fun initListener() {
        mBinding.rvContent.onScrolledOver(100f, 80f, {
            hallViewModel.setScorll(true)
        }, {
            hallViewModel.setScorll(false)
        })
    }

    override suspend fun createObserver() {
        mViewModel.gameRecentList.observe(viewLifecycleOwner) {
            it.let {
                list.addAll(it)
                listAdapter.submitList(list)
            }
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