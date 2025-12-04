package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.ui.adapter.GridSpacingItemDecoration
import arch.cayenne.lib.common.ui.view.SimpleTabDataModel
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.onScrolledOver
import arch.cayenne.lib.common.utils.ext.checkCurrentScrollState
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.scrollToBottomWithLoadMore
import arch.cayenne.lib.common.utils.helper.BackToTopHelper
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.data.HotColdType
import com.walisport.module.hall.data.UniversalLoadMoreScrollListener
import com.walisport.module.hall.databinding.FragmentGameContentBinding
import com.walisport.module.hall.ui.adapter.GameContentAdapter
import com.walisport.module.hall.ui.viewmodel.GameRecentViewModel
import com.walisport.module.hall.ui.viewmodel.HallViewModel
import kotlin.random.Random
import kotlin.reflect.KClass

class GameContentFragment : BaseFragment<GameRecentViewModel, FragmentGameContentBinding>() {
    companion object {
        fun newInstance() = GameContentFragment()
    }
    override val vbClass: KClass<FragmentGameContentBinding> = FragmentGameContentBinding::class
    override val vmClass: KClass<GameRecentViewModel> = GameRecentViewModel::class
    private val hallViewModel: HallViewModel by sharedViewModel<HallViewModel, HallFragment>()
    private lateinit var adapter: GameContentAdapter
    private var list : MutableList<GameContentData> = mutableListOf()
    private var page : Long = 0
    private val mockVendorList by lazy {
        val l = ArrayList<SimpleTabDataModel>()
        for (i in 0..5) {
            l.add(
                SimpleTabDataModel(
                    id = i,
                    simpleName = getString(R.string.wali),
                    icon = "",
                )
            )
        }
        l
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            rvGame.layoutManager = GridLayoutManager(requireContext(),  3)
            val itemDecoration = GridSpacingItemDecoration(
                spanCount = 3,
                horizontalSpacing = 9.dp2px,
                verticalSpacing = 12.dp2px,
                includeEdge = false // 確保邊緣沒有空隙
            )
            rvGame.addItemDecoration(itemDecoration)
            adapter = GameContentAdapter(onItemClick = {
                navigate(arch.cayenne.lib.res.R.string.nav_module_gamedetail.deeplink())
            })
            rvGame.adapter = adapter
            customTabGroup.submitTabList(mockVendorList)
            BackToTopHelper(rvGame, ivBackToTop)
        }
        mViewModel.mockList(page)
    }

    override fun initListener() {
        mBinding.rvGame.onScrolledOver(100f, 80f, {
            hallViewModel.setScorll(true)
        }, {
            hallViewModel.setScorll(false)
        })
        mBinding.rvGame.addOnScrollListener(UniversalLoadMoreScrollListener(6) {
            page++
            mViewModel.mockList(page)
        })
    }

    override suspend fun createObserver() {
        mViewModel.gameRecentList.observe(viewLifecycleOwner) {
            it.let {
                list.addAll(it)
                adapter.submitList(list)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.rvGame.post {
            mBinding.rvGame.checkCurrentScrollState(100f, 80f, {
                hallViewModel.setScorll(true)
            }, {
                hallViewModel.setScorll(false)
            })
        }
    }
}