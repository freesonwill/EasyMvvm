package arch.cayenne.module.home.ui.fragment

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
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
import arch.cayenne.module.bet.viewmodel.FloatingButtonControlViewModel
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.MatchListState
import arch.cayenne.module.home.databinding.FragmentMatchListPagerBinding
import arch.cayenne.module.home.ui.adapter.MatchItemAdapter
import arch.cayenne.module.home.ui.adapter.OnMatchItemClickListener
import arch.cayenne.module.home.ui.view.decoration.MatchCardItemDecoration
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import arch.cayenne.module.home.ui.viewmodel.MatchListViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.reflect.KClass

class MatchListPagerFragment :
    BaseFragment<MatchListViewModel, FragmentMatchListPagerBinding>() {
    override val vbClass: KClass<FragmentMatchListPagerBinding> =
        FragmentMatchListPagerBinding::class
    override val vmClass: KClass<MatchListViewModel> = MatchListViewModel::class
    override val keepViewOnNavigation: Boolean = false
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()
    private lateinit var matchAdapter: MatchItemAdapter
    private val gameLayoutManager by lazy { LinearLayoutManager(context) }
    private val fabViewModel: FloatingButtonControlViewModel by activityViewModel()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {
            refreshLayout.setEnableLoadMore(true)
            refreshLayout.setEnableScrollContentWhenLoaded(true)
            refreshLayout.setOnRefreshListener {
                mViewModel.setHomeOrPullLoadingState(true)
                mViewModel.reload()
            }
            refreshLayout.setOnLoadMoreListener {
                mViewModel.loadNextPage()
            }

            matchAdapter = MatchItemAdapter(object : OnMatchItemClickListener {
                override fun onLiveEntryClick(item: MatchWithMarkets) {
                    navigate(Uri.parse("walisport://module_live/liveFragment?matchId=${item.match.matchId}&sportId=${item.match.basicInfo.sportId}"))
                }

                override fun onFavoriteClick(item: MatchWithMarkets) {
                    mViewModel.addMatchCollect(item, !item.match.collect)
                }

                override fun onOddsCellClick(selection: SelectionBeanLite, x: Float, y: Float) {
                    lifecycleScope.launch {
                        val status = mViewModel.setSelection(selection.selectionId)
                        if (status == AddSelectionStatus.SINGLE) {
                            BetSheetFragment.newInstance().show(parentFragmentManager)
                        } else if (status == AddSelectionStatus.DISABLE_COMBO_FOR_PARLAY) {
                            showToast(getString(R.string.disabled_to_combo))
                        } else if (status == AddSelectionStatus.DISABLE_COMBO_FOR_PROVIDER) {
                            showToast(getString(R.string.disabled_to_combo_for_provider))
                        } else if (status == AddSelectionStatus.COMBO || status == AddSelectionStatus.UPDATE) {
                            fabViewModel.setClickAnimation(x, y)
                        }
                    }
                }
            })
            val decoration = MatchCardItemDecoration(12.dp2px)
            mBinding.rvHomeGameList.apply {
                this.layoutManager = gameLayoutManager
                this.adapter = matchAdapter
                addItemDecoration(decoration)
            }
            (rvHomeGameList.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            rvHomeGameList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    // 滑動停止時觸發
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        subscribeVisibleMatch()
                    }
                }
            })

        }
    }

    private fun subscribeVisibleMatch() {
        val firstVisible = gameLayoutManager.findFirstVisibleItemPosition()
        val lastVisible = gameLayoutManager.findLastVisibleItemPosition()
        if (firstVisible >= 0 && lastVisible <= matchAdapter.itemCount) {
            mViewModel.compareSubscribeMatch(
                matchAdapter.currentList
                    .slice(firstVisible..lastVisible)
                    .map { it.match.matchId }
                    .toSet()
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            //暫時移除訂閱
            mViewModel.cancelSubscribeMatch(mViewModel.getCurrentSubscribeMatchSet())
            if (mViewModel.matchListChange.hasObservers()) {
                mViewModel.matchListChange.removeObserver(matchListObserver)
            }
        } else {
            //把暫時移除的訂閱加回來
            mViewModel.subscribeMatch(mViewModel.getCurrentSubscribeMatchSet())
            if (!mViewModel.matchListChange.hasObservers()) {
                mViewModel.matchListChange.observe(viewLifecycleOwner, matchListObserver)
            }
        }
    }
    override fun initListener() {
    }

    private val matchListObserver = Observer <List<MatchWithMarkets>> { matchList ->
        val preEmpty = matchAdapter.currentList.isEmpty()

        matchAdapter.submitList(matchList)
        if (preEmpty && matchList.isNotEmpty()) {
            mBinding.rvHomeGameList.doOnPreDraw {
                homeViewModel.changeState(HomeState.Match.LoadSuccess)
                subscribeVisibleMatch()
            }
        }
    }
    override fun createObserver() {

        homeViewModel.timer.observeEvent(viewLifecycleOwner, this) {
            mViewModel.updateMatchLiveData()
        }
        mViewModel.matchListChange.observe(viewLifecycleOwner, matchListObserver)

        homeViewModel.isHomeLoading.observe(viewLifecycleOwner) {
            mViewModel.setHomeOrPullLoadingState(it)
        }

        mViewModel.state.observeEvent(viewLifecycleOwner, this) {state ->
            with(mBinding) {
                when(state) {
                    MatchListState.FIRST_LOADING -> {
                        clDynamics.visibility = View.GONE
                        homeViewModel.changeState(HomeState.Match.Loading)
                    }
                    MatchListState.REFRESHING -> {
                        mViewModel.showLoading()
                        clDynamics.visibility = View.GONE
                        mViewModel.setHomeOrPullLoadingState(false)
                    }
                    MatchListState.IDLE -> {
                        lvMatchLoading.visibility = View.GONE
                        if (refreshLayout.isRefreshing) refreshLayout.finishRefresh()
                        refreshLayout.finishLoadMore()
                        clDynamics.visibility = View.GONE
                        homeViewModel.setIsHomeLoading(false)
                    }
                    MatchListState.FAILED -> {
                        lvMatchLoading.visibility = View.GONE
                        refreshLayout.finishRefresh()
                        refreshLayout.finishLoadMore()
                        clDynamics.visibility = View.VISIBLE
                        clDynamics.setState(
                            DynamicStateLayout.States.DATA_EMPTY,
                            R.string.lineup_empty.getString()
                        )
                        homeViewModel.changeState(HomeState.Match.LoadSuccess)
                        homeViewModel.setIsHomeLoading(false)
                    }
                    MatchListState.LOADING_NEXT -> {
                        clDynamics.visibility = View.GONE
                    }
                    MatchListState.NO_MORE_DATA -> {
                        refreshLayout.finishLoadMore()
                    }

                    MatchListState.SHOW_LOADING -> {
                        lvMatchLoading.visibility = View.VISIBLE
                    }
                }
            }
        }

        homeViewModel.selectedDate.observeEvent(viewLifecycleOwner, this) { date ->
            refreshListByDate(date)
        }
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