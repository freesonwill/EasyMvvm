package arch.cayenne.module.home.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.bet.ui.fragment.BetSheetFragment
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentMatchListPagerBinding
import arch.cayenne.module.home.ui.adapter.MatchItemAdapter
import arch.cayenne.module.home.ui.adapter.OnMatchItemClickListener
import arch.cayenne.module.home.ui.view.decoration.MatchCardItemDecoration
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import arch.cayenne.module.home.ui.viewmodel.MatchListViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class MatchListPagerFragment :
    BaseFragment<MatchListViewModel, FragmentMatchListPagerBinding>() {
    override val vbClass: KClass<FragmentMatchListPagerBinding> =
        FragmentMatchListPagerBinding::class
    override val vmClass: KClass<MatchListViewModel> = MatchListViewModel::class
    override val keepViewOnNavigation: Boolean = true
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()
    private lateinit var matchAdapter: MatchItemAdapter

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {
            refreshLayout.setOnRefreshListener {
                mViewModel.reload()
            }
            matchAdapter = MatchItemAdapter(object : OnMatchItemClickListener {
                override fun onLiveEntryClick(item: MatchWithMarkets) {
                    navigate(Uri.parse("walisport://module_live/liveFragment?matchId=${item.match.matchId}&sportId=${item.match.basicInfo.sportId}"))
                }

                override fun onFavoriteClick(item: MatchWithMarkets) {
                    mViewModel.addMatchCollect(item, !item.match.collect)
                }

                override fun onOddsCellClick(item: MatchWithMarkets, selection: SelectionBeanLite) {
                    lifecycleScope.launch {
                        val status = mViewModel.setSelection(item.match.matchId, selection.selectionId)
                        if (status == AddSelectionStatus.SINGLE) {
                            BetSheetFragment.newInstance().show(parentFragmentManager)
                        } else if (status == AddSelectionStatus.DISABLE_COMBO) {
                            showToast(getString(R.string.disabled_to_combo))
                        }
                    }
                }
            })
            val decoration = MatchCardItemDecoration(12.dp2px)
            val layoutManager = LinearLayoutManager(context)
            mBinding.rvHomeGameList.apply {
                this.layoutManager = layoutManager
                this.adapter = matchAdapter
                addItemDecoration(decoration)
            }

            rvHomeGameList.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (matchAdapter.itemCount == 0) return
                    rvHomeGameList.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val firstVisible = layoutManager.findFirstVisibleItemPosition()
                    val lastVisible = layoutManager.findLastVisibleItemPosition()

                    if (firstVisible >= 0 && lastVisible <= matchAdapter.itemCount) {
                        mViewModel.compareSubscribeMatch(
                            matchAdapter.currentList
                                .slice(firstVisible..lastVisible)
                                .map { it.match.matchId }
                                .toSet()
                        )
                    }
                }

            })

            rvHomeGameList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    // 滑動停止時觸發
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val firstVisible = layoutManager.findFirstVisibleItemPosition()
                        val lastVisible = layoutManager.findLastVisibleItemPosition()
                        val totalItemCount = layoutManager.itemCount
                        //讀取下一頁
                        if (lastVisible >= totalItemCount - 1) {
                            mViewModel.loadNextPage()
                        }
                        if (firstVisible >= 0 && lastVisible <= matchAdapter.itemCount) {
                            mViewModel.compareSubscribeMatch(
                                matchAdapter.currentList
                                    .slice(firstVisible..lastVisible)
                                    .map { it.match.matchId }
                                    .toSet()
                            )
                        }
                    }
                }
            })
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
        homeViewModel.selectedDate.observeEvent(viewLifecycleOwner, this) { date ->
            refreshListByDate(date)
        }

        mViewModel.matchListChange.observe(viewLifecycleOwner) { matchList ->
            mBinding.apply {
                if (matchList.isEmpty()) {
                    clDynamics.visibility = View.VISIBLE
                    clDynamics.setState(
                        DynamicStateLayout.States.DATA_EMPTY,
                        R.string.lineup_empty.getString()
                    )
                } else {
                    clDynamics.visibility = View.GONE
                }
            }
            matchAdapter.submitList(matchList)
        }

        mViewModel.isLoadingData.observe(viewLifecycleOwner) { isLoading ->
            if (!isLoading) {
                mBinding.refreshLayout.finishRefresh()
            }
        }
    }

    fun test() {

    }

    private fun refreshListByDate(date: Long) {
        if (date.toInt() == 0) {
            //切換後選回全部
            mViewModel.setSelectedDate(0)
        } else {
            mViewModel.setSelectedDate(date)
        }
    }

    override fun initData() {
        arguments?.apply {
            mViewModel.setTournamentId(this.getInt(ARG_LEAGUE_ID))
            mViewModel.setSportId(this.getInt(ARG_SPORT_ID))
            mViewModel.setPlayTypeId(this.getInt(ARG_PLAY_TYPE_ID))
            mViewModel.setPosition(this.getInt(ARG_POSITION))
        }
        mViewModel.startObserveMatch()
    }

    override fun onPause() {
        super.onPause()
        //暫時移除訂閱
        mViewModel.cancelSubscribeMatch(mViewModel.getCurrentSubscribeMatchSet())
    }

    override fun onResume() {
        super.onResume()
        //把暫時移除的訂閱加回來
        mViewModel.subscribeMatch(mViewModel.getCurrentSubscribeMatchSet())
    }


    companion object {
        private const val ARG_SPORT_ID = "sport_id"
        private const val ARG_PLAY_TYPE_ID = "play_type_id"
        private const val ARG_LEAGUE_ID = "arg_league_id"
        private const val ARG_POSITION = "arg_position"
        fun newInstance(sportId: Int, playTypeId: Int, leagueId: Int, position: Int): MatchListPagerFragment {
            return MatchListPagerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SPORT_ID, sportId)
                    putInt(ARG_PLAY_TYPE_ID, playTypeId)
                    putInt(ARG_LEAGUE_ID, leagueId)
                    putInt(ARG_POSITION, position)
                }
            }
        }
    }
}