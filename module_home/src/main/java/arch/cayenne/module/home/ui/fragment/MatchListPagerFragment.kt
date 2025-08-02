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
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
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
import arch.cayenne.module.home.databinding.FragmentMatchListPagerBinding
import arch.cayenne.module.home.ui.adapter.MatchItemAdapter
import arch.cayenne.module.home.ui.adapter.OnMatchItemClickListener
import arch.cayenne.module.home.ui.view.decoration.MatchCardItemDecoration
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import arch.cayenne.module.home.ui.viewmodel.MatchListViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.lang.ref.WeakReference
import kotlin.reflect.KClass

class MatchListPagerFragment :
    BaseFragment<MatchListViewModel, FragmentMatchListPagerBinding>() {
    override val vbClass: KClass<FragmentMatchListPagerBinding> =
        FragmentMatchListPagerBinding::class
    override val vmClass: KClass<MatchListViewModel> = MatchListViewModel::class
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()
    private lateinit var matchAdapter: MatchItemAdapter
    private val gameLayoutManager by lazy { LinearLayoutManager(context) }
    private val fabViewModel: FloatingButtonControlViewModel by activityViewModel()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {
            refreshLayout.setEnableLoadMore(true)
            refreshLayout.setEnableScrollContentWhenLoaded(true)
            refreshLayout.setOnRefreshListener {
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

                override fun onOddsCellClick(cell: WeakReference<View>, selection: SelectionBeanLite, x: Float, y: Float) {
                    lifecycleScope.launch {
                        cell.get()?.isSelected = true
                        val status = mViewModel.setSelection(selection.selectionId)

                        if (status !is AddSelectionStatus.Success) {
                            cell.get()?.isSelected = false
                        }

                        if (status is AddSelectionStatus.Success.Single) {
                            BetSheetFragment.show(requireActivity())
                        } else if (status is AddSelectionStatus.Failure.DisableComboForParlay) {
                            showToast(getString(R.string.disabled_to_combo))
                        } else if (status is AddSelectionStatus.Failure.DisableComboForProvider) {
                            showToast(getString(R.string.disabled_to_combo_for_provider))
                        } else if (status is AddSelectionStatus.Failure.NetworkDisconnected) {
                            showToast(getString(arch.cayenne.lib.common.R.string.toast_server_disconnected))
                        } else if (status is AddSelectionStatus.Success.Combo || status is AddSelectionStatus.Success.Update) {
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
            rvHomeGameList.itemAnimator  = null
            rvHomeGameList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    // 滑動停止時觸發
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        subscribeVisibleMatch()
                        updateMatchListPosition()
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

    private fun updateMatchListPosition() {
        val firstView = gameLayoutManager.getChildAt(0)
        val firstPos = gameLayoutManager.findFirstVisibleItemPosition()
        val firstViewTop = firstView?.top ?: 0
        val itemHeight = firstView?.height ?: 0
        val scrollY = firstPos * itemHeight - firstViewTop
        homeViewModel.updateCoordinate(
            playTypeId = mViewModel.getPlayTypeId(),
            sportId = mViewModel.getSportId(),
            tournamentId = mViewModel.getTournamentId(),
            coordinate = scrollY
        )
    }

    private fun setMatchListPosition() {
        lifecycleScope.launch {
            val position = homeViewModel.getCurrentPageCoordinate(
                playTypeId = mViewModel.getPlayTypeId(),
                sportId = mViewModel.getSportId(),
                tournamentId = mViewModel.getTournamentId()
            )
            mBinding.rvHomeGameList.scrollBy(0, position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            //暫時移除訂閱
            mViewModel.cancelSubscribeMatch(mViewModel.getCurrentSubscribeMatchSet())
            mViewModel.stopMatchSubscribeNotify()
            if (mViewModel.matchListChange.hasObservers()) {
                mViewModel.matchListChange.removeObserver(matchListObserver)
            }
        } else {
            //把暫時移除的訂閱加回來
            mViewModel.startMatchSubscribeNotify()
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
        "KC_ 更新賽事列表 ${matchList.map { it.match.matchId }}".logi()
        matchAdapter.submitList(matchList)
        mBinding.rvHomeGameList.doOnPreDraw {
            subscribeVisibleMatch()
            if (preEmpty && matchList.isNotEmpty()) {
                homeViewModel.changeState(HomeState.Match.LoadSuccess)
                setMatchListPosition()
            }
        }

    }
    override suspend fun createObserver() {

        homeViewModel.timer.observeEvent(viewLifecycleOwner, this) {
            mViewModel.updateMatchLiveData()
        }
        mViewModel.matchListChange.observe(viewLifecycleOwner, matchListObserver)

        mViewModel.apiStateListener.observe(viewLifecycleOwner) {
            with(mBinding) {
                when(it) {
                    DataState.NetworkUnavailable -> {
                        mViewModel.changePageEnd(true)
                        lvMatchLoading.visibility = View.GONE
                        refreshLayout.finishRefresh()
                        refreshLayout.finishLoadMore()
                        refreshLayout.setEnableLoadMore(false)
                        clDynamics.visibility = View.VISIBLE
                        clDynamics.setState(
                            DynamicStateLayout.States.NETWORK_ANOMALY,
                            arch.cayenne.lib.common.R.string.error_net.getString()
                        )
                        homeViewModel.changeState(DataState.NetworkUnavailable)
                    }
                    DataState.DataEmpty -> {     //這個DataEmpty表示api抓不到任何資料了，有可能是頁面到底，或是從第一頁就抓不到資料
                        mViewModel.changePageEnd(true)
                        refreshLayout.finishLoadMore()
                        refreshLayout.setEnableLoadMore(false)
                        matchAdapter.showNoMoreData(true)
                    }

                    DataState.NoMoreData -> {
                        mViewModel.changePageEnd(true)
                        refreshLayout.setEnableLoadMore(false)
                        refreshLayout.finishLoadMoreWithNoMoreData()
                        matchAdapter.showNoMoreData(true)
                    }

                    HomeState.Match.DataEmpty -> {  //這個DataEmpty表示真的從第一頁就抓不到資料，表示當前的選擇沒有任何賽事
                        lvMatchLoading.visibility = View.GONE
                        refreshLayout.finishRefresh()
                        clDynamics.visibility = View.VISIBLE
                        clDynamics.setState(
                            DynamicStateLayout.States.DATA_EMPTY,
                            R.string.lineup_empty.getString()
                        )
                        homeViewModel.changeState(HomeState.Match.LoadSuccess)
                    }
                    HomeState.Match.Loading -> {
                        lvMatchLoading.visibility = View.VISIBLE
                        clDynamics.visibility = View.GONE
                        refreshLayout.setEnableLoadMore(true)
                        homeViewModel.changeState(HomeState.Match.Loading)
                    }
                    HomeState.Match.Refreshing -> {
                        clDynamics.visibility = View.GONE
                        refreshLayout.setEnableLoadMore(true)
                    }
                    HomeState.Match.LoadingNext -> {
                        clDynamics.visibility = View.GONE
                    }
                    DataState.LoadSuccess, HomeState.Match.LoadSuccess -> {
                        lvMatchLoading.visibility = View.GONE
                        if (refreshLayout.isRefreshing) refreshLayout.finishRefresh()
                        refreshLayout.finishLoadMore()
                        clDynamics.visibility = View.GONE
                        homeViewModel.changeState(HomeState.Match.LoadSuccess)
                    }
                }
            }

        }

        homeViewModel.selectedDate.observeEvent(viewLifecycleOwner, this) { date ->
            if (date  == HomeViewModel.DEFAULT_DATE
                || homeViewModel.currentPlayTypeId != mViewModel.getPlayTypeId()
                || homeViewModel.currentSportId != mViewModel.getSportId())
                return@observeEvent
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
//        mViewModel.startObserveMatch()
    }

    fun startObserveMatch() {
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