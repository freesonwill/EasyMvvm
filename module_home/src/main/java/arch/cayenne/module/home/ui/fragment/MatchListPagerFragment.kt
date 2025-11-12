package arch.cayenne.module.home.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.scrollToBottomWithLoadMore
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.BackToTopHelper
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.bet.data.AddSelectionStatus
import arch.cayenne.module.bet.ui.fragment.BetSheetFragment
import arch.cayenne.module.bet.viewmodel.FloatingButtonControlViewModel
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.databinding.FragmentMatchListPagerBinding
import arch.cayenne.module.home.ui.adapter.MatchItemAdapter
import arch.cayenne.module.home.ui.adapter.OnMatchItemClickListener
import arch.cayenne.module.home.ui.view.decoration.MatchCardItemDecoration
import arch.cayenne.module.home.ui.viewmodel.EarlyViewModel
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import arch.cayenne.module.home.ui.viewmodel.MatchListViewModel
import arch.cayenne.module.home.ui.viewmodel.SubHomeViewModel
import arch.cayenne.module.home.utils.setFavoriteIcon
import com.walisport.module.message.ui.view.DeleteAnimator
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
    private val subHomeViewModel: SubHomeViewModel by lazy {
        if (arguments?.getInt(ARG_PLAY_TYPE_ID) == PlayType.EARLY.id) {
            viewModels<EarlyViewModel>({ requireParentFragment() }).value
        } else {
            viewModels<SubHomeViewModel>({ requireParentFragment() }).value
        }
    }

    private lateinit var matchAdapter: MatchItemAdapter
    private val gameLayoutManager by lazy { LinearLayoutManager(context) }
    private val fabViewModel: FloatingButtonControlViewModel by activityViewModel()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {
            refreshLayout.setEnableLoadMore(false)
            refreshLayout.setEnableScrollContentWhenLoaded(true)
            refreshLayout.setOnRefreshListener {
                reloadAllData()
            }

            matchAdapter = MatchItemAdapter(object : OnMatchItemClickListener {
                override fun onLiveEntryClick(item: MatchWithMarkets) {
                    val liveInfo = item.match.liveInfo
                    navigate(Uri.parse("walisport://module_live/liveFragment?matchId=${item.match.matchId}&sportId=${item.match.basicInfo.sportId}&showVideo=${liveInfo.liveVideo}&showAnim=${liveInfo.liveAnimation.isNotBlank()}"))
                }

                override fun onFavoriteClick(view: ImageView, item: MatchWithMarkets) {
                    lifecycleScope.launch {
                        view.setFavoriteIcon(!item.match.collect, false) //先點亮或點暗收藏按鈕
                        val success = mViewModel.addMatchCollect(item, !item.match.collect)
                        if (!success) view.setFavoriteIcon(!view.isSelected, true) //失敗了需要復原回來
                    }

                }

                override fun onOddsCellClick(
                    cell: WeakReference<View>,
                    selection: SelectionBeanLite,
                    x: Float,
                    y: Float
                ) {
                    lifecycleScope.launch {
                        val v = cell.get()
                        val status = mViewModel.setSelection(selection)
                        when (status) {
                            is AddSelectionStatus.Success.Single -> {
                                BetSheetFragment.show(
                                    requireActivity(),
                                    object : BetSheetFragment.ShowListener {
                                        override fun onShow() {
                                            v?.isSelected = true
                                        }

                                        override fun onCancel() {
                                            v?.isSelected = false
                                        }

                                        override fun onHide() {
                                            v?.isSelected = false
                                        }
                                    })
                            }

                            is AddSelectionStatus.Success.Combo, is AddSelectionStatus.Success.Update -> {
                                v?.isSelected = true
                            }

                            is AddSelectionStatus.Others.Remove -> {
                                v?.isSelected = false
                            }

                            is AddSelectionStatus.Failure -> {
                                v?.isSelected = false
                                status.msg?.let {
                                    showToast(it)
                                }
                            }
                        }

                        if (status is AddSelectionStatus.Success.Combo || status is AddSelectionStatus.Success.Update) {
                            fabViewModel.setClickAnimation(x, y)
                        }
                    }
                }
            })
            //賽事卡片之間的間閣
            val decoration = MatchCardItemDecoration(12.dp2px)
            mBinding.rvHomeGameList.apply {
                this.layoutManager = gameLayoutManager
                this.adapter = matchAdapter
                addItemDecoration(decoration)
                itemAnimator = DeleteAnimator()
            }
            rvHomeGameList.itemAnimator = null
            rvHomeGameList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        subscribeVisibleMatch()
                        updateMatchListPosition()
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    rvHomeGameList.scrollToBottomWithLoadMore(minScrollCount = 8, {
                        if (mViewModel.apiStateListener.value != HomeState.Match.LoadSuccess) return@scrollToBottomWithLoadMore
                        mViewModel.loadNextPage()
                    }, {
                        if (mViewModel.apiStateListener.value == HomeState.Match.LoadNextFailure) {
                            mViewModel.loadNextPage()
                        }
                    })
                }
            })

            // 初始化回到頂部按鈕
            BackToTopHelper(rvHomeGameList, ivBackToTop)
        }
    }

    private fun subscribeVisibleMatch() {
        val firstVisible = gameLayoutManager.findFirstVisibleItemPosition()
        val lastVisible = gameLayoutManager.findLastVisibleItemPosition()
        if (firstVisible >= 0 && lastVisible <= matchAdapter.itemCount) {
            mViewModel.compareSubscribeMatch(
                matchAdapter.currentList
                    .slice(firstVisible..lastVisible)
                    .filterIsInstance<MatchWithMarkets>()
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
        subHomeViewModel.updateCoordinate(
            playTypeId = mViewModel.getPlayTypeId(),
            sportId = mViewModel.getSportId(),
            tournamentId = mViewModel.getTournamentId(),
            coordinate = scrollY
        )
    }

    private fun setMatchListPosition() {
        lifecycleScope.launch {
            val position = subHomeViewModel.getCurrentPageCoordinate(
                playTypeId = mViewModel.getPlayTypeId(),
                sportId = mViewModel.getSportId(),
                tournamentId = mViewModel.getTournamentId()
            )
            mBinding.rvHomeGameList.scrollBy(0, position)
        }
    }

    override fun initListener() {
    }

    val matchListObserver = Observer<List<MatchWithMarkets>> { matchList ->
        "MatchListChange livedata Observed~ ${matchList.map { it.match.matchId }}".logi(this::class.java.simpleName)
        val preEmpty = matchAdapter.currentList.isEmpty()
        matchAdapter.submitList(matchList) {
            if (mViewModel.requestScrollToTop) {
                mBinding.rvHomeGameList.scrollToPosition(0)
                mViewModel.resetRequestScrollToTop()
            }
            // 發送頁面載入完成通知
            if (preEmpty) {
                homeViewModel.changeState(
                    HomeState.FirstMatchListComplete(
                        mViewModel.getPlayTypeId(),
                        mViewModel.getTournamentId()
                    )
                )
            }
        }
        mBinding.rvHomeGameList.doOnPreDraw {
            if (mBinding.rvHomeGameList.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                subscribeVisibleMatch()
            }
        }

        // 把 clDynamics 的顯示控制移到這裡，避免淡入淡出動畫時閃爍
        mBinding.clDynamics.visibility =
            if (matchList.isEmpty()) View.VISIBLE else View.GONE
    }

    override suspend fun createObserver() {

        homeViewModel.timer.observeEvent(viewLifecycleOwner, this) {
            mViewModel.updateMatchLiveData()
        }

        mViewModel.apiStateListener.observe(viewLifecycleOwner) {
            "MatchListPagerFragment playType: ${mViewModel.getPlayTypeId()} tournament: ${mViewModel.getTournamentId()} state change ${it::class.java.name}".logi(
                this::class.java.name
            )
            with(mBinding) {
                when (it) {
                    DataState.NetworkUnavailable, HomeState.Match.LoadNextFailure -> {
                        refreshLayout.finishRefresh()
                        matchAdapter.setLastItemType(MatchItemAdapter.LAST_ITEM_NONE)
                        if (it == DataState.NetworkUnavailable) {
                            mViewModel.changePageEnd(true)
                            clDynamics.setState(
                                DynamicStateLayout.States.NETWORK_ANOMALY(),
                                arch.cayenne.lib.common.R.string.error_net.getString()
                            )
                        }
                        showToast(arch.cayenne.lib.common.R.string.toast_server_disconnected.getString())
                        homeViewModel.changeState(
                            HomeState.FirstMatchListComplete(
                                mViewModel.getPlayTypeId(),
                                mViewModel.getTournamentId()
                            )
                        )
                    }

                    DataState.NoMoreData -> {     //這個DataEmpty表示api抓不到任何資料了，有可能是頁面到底，或是從第一頁就抓不到資料
                        refreshLayout.finishRefresh()
                        mViewModel.changePageEnd(true)
                        matchAdapter.setLastItemType(MatchItemAdapter.LAST_ITEM_NO_MORE)
                    }

                    HomeState.Match.DataEmpty -> {  //這個DataEmpty表示確定真的從第一頁就抓不到資料，表示當前的選擇沒有任何賽事
                        refreshLayout.finishRefresh()
                        matchAdapter.setLastItemType(MatchItemAdapter.LAST_ITEM_NONE)
                        clDynamics.setState(
                            DynamicStateLayout.States.DATA_EMPTY,
                            R.string.lineup_empty.getString()
                        )
                        homeViewModel.changeState(
                            HomeState.FirstMatchListComplete(
                                mViewModel.getPlayTypeId(),
                                mViewModel.getTournamentId()
                            )
                        )
                    }

                    HomeState.Match.Loading -> {
                        homeViewModel.changeState(HomeState.Match.Loading)
                    }

                    HomeState.Match.Refreshing -> {
                        mViewModel.changePageEnd(false)
                        matchAdapter.setLastItemType(MatchItemAdapter.LAST_ITEM_LOAD_MORE)
                    }

                    HomeState.Match.LoadingNext -> {
                    }

                    DataState.LoadSuccess, HomeState.Match.LoadSuccess -> {
                        if (refreshLayout.isRefreshing) refreshLayout.finishRefresh()
                        mViewModel.changePageEnd(false)
                        matchAdapter.setLastItemType(MatchItemAdapter.LAST_ITEM_LOAD_MORE)
                    }
                }
            }

        }

        //这个时候还没调用initData, 需要从arguments中获取playType
        if (arguments?.getInt(ARG_PLAY_TYPE_ID) == PlayType.EARLY.id) {
            //只有早盘有日期变化的情况
            val earlyViewModel: EarlyViewModel = subHomeViewModel as EarlyViewModel
            earlyViewModel.selectedDate.observeEvent(viewLifecycleOwner, this) { date ->
                if (date == HomeViewModel.DEFAULT_DATE
                    || earlyViewModel.currentPlayTypeId != mViewModel.getPlayTypeId()
                    || earlyViewModel.currentSportId != mViewModel.getSportId()
                ) {
                    return@observeEvent
                }
                refreshListByDate(date)
            }
        }

        homeViewModel.notifySubHomeRefresh.observeEvent(viewLifecycleOwner, this) {
            reloadAllData()
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
        "MatchListPagerFragment playType: ${mViewModel.getPlayTypeId()} sportId: ${mViewModel.getSportId()} leagueId: ${mViewModel.getTournamentId()}".logi()
        startObserveMatch()
    }

    fun startObserveMatch() {
        mViewModel.startObserveMatch()
    }

    fun startObserveMatchListChange() {
        if (!mViewModel.matchListChange.hasObservers()) {
            mViewModel.matchListChange.observe(viewLifecycleOwner, matchListObserver)
        }
    }

    fun reloadAllData() {
        mViewModel.reload()
    }

    override fun onResume() {
        super.onResume()
        //把暫時移除的訂閱加回來
        mViewModel.startMatchSubscribeNotify()
        mViewModel.subscribeMatch(mViewModel.getCurrentSubscribeMatchSet())
    }

    override fun onPause() {
        super.onPause()
        //如果切换时正在滚动则停止滚动
        mBinding.rvHomeGameList.stopScroll()
        //暫時移除訂閱
        mViewModel.cancelSubscribeMatch(mViewModel.getCurrentSubscribeMatchSet())
        mViewModel.stopMatchSubscribeNotify()
    }


    companion object {
        private const val ARG_SPORT_ID = "sport_id"
        private const val ARG_PLAY_TYPE_ID = "play_type_id"
        private const val ARG_LEAGUE_ID = "arg_league_id"
        private const val ARG_POSITION = "arg_position"
        fun newInstance(
            sportId: Int,
            playTypeId: Int,
            leagueId: Int,
            position: Int
        ): MatchListPagerFragment {
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