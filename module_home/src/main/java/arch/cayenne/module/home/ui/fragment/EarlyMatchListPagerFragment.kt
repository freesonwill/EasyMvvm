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
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.BackToTopHelper
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.MatchListItem
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.bet.data.AddSelectionStatus
import arch.cayenne.module.bet.ui.fragment.BetSheetFragment
import arch.cayenne.module.bet.viewmodel.FloatingButtonControlViewModel
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.model.MatchDateItem
import arch.cayenne.module.home.data.model.MatchLoadMoreData
import arch.cayenne.module.home.data.model.MatchNoMoreData
import arch.cayenne.module.home.data.model.MatchQueryDateNoData
import arch.cayenne.module.home.databinding.FragmentEarlyMatchListPagerBinding
import arch.cayenne.module.home.ui.adapter.MatchItemAdapter
import arch.cayenne.module.home.ui.adapter.OnMatchItemClickListener
import arch.cayenne.module.home.ui.view.decoration.MatchCardItemDecoration
import arch.cayenne.module.home.ui.viewmodel.EarlyDate
import arch.cayenne.module.home.ui.viewmodel.EarlyDateType
import arch.cayenne.module.home.ui.viewmodel.EarlyMatchListViewModel
import arch.cayenne.module.home.ui.viewmodel.EarlyViewModel
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import arch.cayenne.module.home.utils.DateUtils
import arch.cayenne.module.home.utils.DateUtils.isSameDay
import arch.cayenne.module.home.utils.setFavoriteIcon
import com.walisport.module.message.ui.view.DeleteAnimator
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.lang.ref.WeakReference
import kotlin.reflect.KClass

/**
 * 早盘用的比赛列表， 具备日期切换及向前查询功能
 */
class EarlyMatchListPagerFragment :
    BaseFragment<EarlyMatchListViewModel, FragmentEarlyMatchListPagerBinding>() {
    override val vbClass: KClass<FragmentEarlyMatchListPagerBinding> =
        FragmentEarlyMatchListPagerBinding::class
    override val vmClass: KClass<EarlyMatchListViewModel> = EarlyMatchListViewModel::class
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()
    private val earlyViewModel: EarlyViewModel by viewModels({ requireParentFragment() })

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

                            else -> {}
                        }

                        if (status is AddSelectionStatus.Success.Combo || status is AddSelectionStatus.Success.Update) {
                            fabViewModel.setClickAnimation(x, y)
                        }
                    }
                }
            }, earlyViewModel.currentPlayTypeId)

            //賽事卡片之間的間閣
            val decoration = MatchCardItemDecoration(
                12.dp2px, if (arguments?.getInt(ARG_PLAY_TYPE_ID) == PlayType.EARLY.id) {
                    12.dp2px
                } else {
                    6.dp2px
                }
            )
            mBinding.rvHomeGameList.apply {
                this.layoutManager = gameLayoutManager
                this.adapter = matchAdapter
                addItemDecoration(decoration)
                itemAnimator = DeleteAnimator()
            }
            rvHomeGameList.itemAnimator = null
            rvHomeGameList.overScrollMode = View.OVER_SCROLL_NEVER
            rvHomeGameList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        subscribeVisibleMatch()
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    if (dy > 0) {
                        if (mViewModel.apiStateListener.value == HomeState.Match.LoadSuccess) {
                            val lastItemPos = layoutManager.findLastCompletelyVisibleItemPosition()
                            val itemCount = matchAdapter.itemCount - 8
                            if (lastItemPos > itemCount && lastItemPos > 1) {
                                mViewModel.loadNextPage()
                            }
                        }
                    } else if (dy < 0) {
                        if (mViewModel.prevApiStateListener.value == HomeState.Match.LoadSuccess) {
                            val firstItemPos =
                                layoutManager.findFirstCompletelyVisibleItemPosition()
                            if (firstItemPos <= 8) {
                                mViewModel.loadPrevPage()
                            }
                        }
                    }

                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    val itemList = matchAdapter.currentList
                    if (itemList.isEmpty()) {
                        return
                    }
                    when (val item = itemList[firstVisibleItemPosition]) {
                        is MatchWithMarkets -> {

                            val earlyDate = EarlyDate(
                                "",
                                "",
                                item.match.basicInfo.startTime,
                                EarlyDateType.Date
                            )

                            if (mBinding.rvHomeGameList.scrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
                                earlyViewModel.setDisplayDate(earlyDate)
                            }

                        }

                        is MatchNoMoreData -> {

                        }

                        is MatchLoadMoreData -> {

                        }

                        is MatchDateItem -> {
                            val earlyDate = EarlyDate(
                                "",
                                "",
                                item.timeStamp,
                                EarlyDateType.Date
                            )

                            if (mBinding.rvHomeGameList.scrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
                                earlyViewModel.setDisplayDate(earlyDate)
                            }

                        }

                        else -> {}
                    }


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

    override fun initListener() {
    }

    val matchListObserver = Observer<List<MatchWithMarkets>> { matchList ->
        "MatchListChange livedata Observed~ ${matchList.map { it.match.matchId }}".logi(this::class.java.simpleName)
        val preEmpty = matchAdapter.currentList.isEmpty()

        //早盘的比赛列表，需要添加日期条目
        val list = if (mViewModel.getPlayTypeId() == PlayType.EARLY.id) addDateItem(
            matchList
        ) else
            matchList


        matchAdapter.submitList(list) {
            if (mViewModel.requestScrollToTop) {
                mBinding.rvHomeGameList.scrollToPosition(0)
                mViewModel.resetRequestScrollToTop()
            }
            // 發送頁面載入完成通知
            if (preEmpty) {
                homeViewModel.changeState(
                    HomeState.FirstMatchListComplete(
                        mViewModel.getPlayTypeId(),
                        mViewModel.getTournamentIdList()
                    )
                )
            }
        }

        mBinding.tvHover.postDelayed({
            val firstVisibleItemPosition = gameLayoutManager.findFirstVisibleItemPosition()
            firstVisibleItemPosition.let {
                if (it < 0) return@let
                if (matchAdapter.currentList.isEmpty()) return@let
//                if (rvAdapter._data!!.size < dateIndex) return@let
                val item = matchAdapter.currentList[firstVisibleItemPosition]
                if (item is MatchDateItem) {
                    mBinding.tvHover.text = item.dateStr
                } else if (item is MatchWithMarkets) {
                    val (date, week) = DateUtils.getDisplay(item.match.basicInfo.startTime)
                    val display = "$date $week"
                    mBinding.tvHover.text = display
                }
            }
        }, 100)
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
            "MatchListPagerFragment playType: ${mViewModel.getPlayTypeId()} tournamentIdList: ${mViewModel.getTournamentIdList()} state change ${it::class.java.name}".logi(
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
                                mViewModel.getTournamentIdList()
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
                                mViewModel.getTournamentIdList()
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

        mViewModel.prevApiStateListener.observe(viewLifecycleOwner) {
            with(mBinding) {
                when (it) {
                    DataState.NetworkUnavailable, HomeState.Match.LoadNextFailure -> {}

                    HomeState.Match.PrevDataEmpty -> {

                    }

                    HomeState.Match.PrevNoMoreData -> {
                        mViewModel.changePrevPageEnd(true)
                    }

                    HomeState.Match.Loading -> {
                        mViewModel.changeState(HomeState.Match.Loading)
                    }

                    DataState.LoadSuccess, HomeState.Match.LoadSuccess -> {
                        mViewModel.changePrevPageEnd(false)
                    }
                }
            }
        }

        //只有早盘有日期变化的情况
        earlyViewModel.selectedDate.observeEvent(viewLifecycleOwner, this) { date ->
            if (date == HomeViewModel.DEFAULT_DATE
                || earlyViewModel.currentPlayTypeId != mViewModel.getPlayTypeId()
                || earlyViewModel.currentSportId != mViewModel.getSportId()
            ) {
                return@observeEvent
            }
            refreshListByDate(date)
        }

        earlyViewModel.displayDate.observe(viewLifecycleOwner) {
            it?.let {
                val (date, week) = DateUtils.getDisplay(it.timestamp)
                val display = "$date $week"
                mBinding.tvHover.text = display
            }
        }


        homeViewModel.notifySubHomeRefresh.observeEvent(viewLifecycleOwner, this) {
            reloadAllData()
        }

        //联赛列表可能发生变化
        if (arguments?.getBoolean(ARG_MUTABLE) == true) {
            earlyViewModel.savedTournamentSelections.observe(viewLifecycleOwner) {
                val sorted = it.sorted()
                if (mViewModel.getTournamentIdList() != sorted) {
                    mViewModel.setTournamentIdList(sorted)
                    mViewModel.startObserveMatch()
                    reloadAllData()
                }
            }

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
            mViewModel.setTournamentIdList(this.getIntArray(ARG_LEAGUE_ID)?.toList() ?: listOf(0))
            mViewModel.setSportId(this.getInt(ARG_SPORT_ID))
            mViewModel.setPlayTypeId(this.getInt(ARG_PLAY_TYPE_ID))
            mViewModel.setPosition(this.getInt(ARG_POSITION))
        }
        "EarlyMatchListPagerFragment playType: ${mViewModel.getPlayTypeId()} sportId: ${mViewModel.getSportId()} leagueIdList: ${mViewModel.getTournamentIdList()}".logi()
        startObserveMatch()
    }

    private fun startObserveMatch() {
        mViewModel.startObserveDate()
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

    private fun addDateItem(list: List<MatchListItem>?): List<MatchListItem>? {
        val isEmpty = (list?.size ?: 0) == 0
        if (isEmpty) {
            return list
        }

        val set: HashSet<String> = java.util.HashSet()

        val mutableList = mutableListOf<MatchListItem>()

        if (list != null) {
            for (i in list.indices) {
                val item = list[i]
                if (item is MatchWithMarkets) {

                    val prevItem: MatchWithMarkets? = list.subList(0, i)
                        .lastOrNull { it is MatchWithMarkets } as MatchWithMarkets?
                    val nextItem =
                        list.subList(i + 1, list.size).firstOrNull { it is MatchWithMarkets }
                    if (prevItem == null) {
                        //前面没有MatchWithMarkets
                        if (!isSameDay(
                                item.match.basicInfo.startTime,
                                mViewModel.queryDate.value
                            ) && item.match.basicInfo.startTime > mViewModel.queryDate.value
                        ) {
                            //第一条数据的日期就大于查询日期
                            //指定的查询日期无数据
                            val (date, week) = DateUtils.getDisplay(mViewModel.queryDate.value)
                            val display = "$date $week"
                            mutableList.add(MatchDateItem(display, mViewModel.queryDate.value))
                            mutableList.add(
                                MatchQueryDateNoData(
                                    earlyViewModel.getTournamentsName(
                                        mViewModel.getTournamentIdList()
                                    ), mViewModel.queryDate.value
                                ) {
                                    (requireParentFragment() as EarlyFragment).jumpToAllLeagueTab()
                                }
                            )
                        }
                    } else if (nextItem == null) {
                        //最后一个MatchWithMarkets
                        if (!isSameDay(
                                item.match.basicInfo.startTime, mViewModel.queryDate.value
                            ) && item.match.basicInfo.startTime < mViewModel.queryDate.value
                        ) {
                            //最后一条数据的日期小于查询日期
                            //指定的查询日期无数据
                            val (date, week) = DateUtils.getDisplay(mViewModel.queryDate.value)
                            val display = "$date $week"
                            mutableList.add(MatchDateItem(display, mViewModel.queryDate.value))
                            mutableList.add(
                                MatchQueryDateNoData(
                                    earlyViewModel.getTournamentsName(
                                        mViewModel.getTournamentIdList()
                                    ), mViewModel.queryDate.value
                                ) {
                                    (requireParentFragment() as EarlyFragment).jumpToAllLeagueTab()
                                }
                            )
                        }
                    } else {
                        //prevItem!=null
                        if (prevItem.match.basicInfo.startTime < mViewModel.queryDate.value
                            && !isSameDay(
                                item.match.basicInfo.startTime,
                                mViewModel.queryDate.value
                            ) && item.match.basicInfo.startTime >= mViewModel.queryDate.value
                        ) {
                            val (date, week) = DateUtils.getDisplay(mViewModel.queryDate.value)
                            val display = "$date $week"
                            mutableList.add(MatchDateItem(display, mViewModel.queryDate.value))
                            mutableList.add(
                                MatchQueryDateNoData(
                                    earlyViewModel.getTournamentsName(
                                        mViewModel.getTournamentIdList()
                                    ), mViewModel.queryDate.value
                                ) {
                                    (requireParentFragment() as EarlyFragment).jumpToAllLeagueTab()
                                }
                            )
                        }

                    }


                    val (date, week) = DateUtils.getDisplay(item.match.basicInfo.startTime)
                    val display = "$date $week"

                    if (!set.contains(display)) {
                        mutableList.add(MatchDateItem(display, item.match.basicInfo.startTime))
                        set.add(display)
                    }
                    mutableList.add(item)
                } else {
                    mutableList.add(item)
                }
            }
        }

        return mutableList
    }


    companion object {
        private const val ARG_SPORT_ID = "sport_id"
        private const val ARG_PLAY_TYPE_ID = "play_type_id"
        private const val ARG_LEAGUE_ID = "arg_league_id"
        private const val ARG_POSITION = "arg_position"
        private const val ARG_MUTABLE = "arg_mutable"
        fun newInstance(
            sportId: Int,
            playTypeId: Int,
            leagueIdList: List<Int>,
            position: Int,
            mutable: Boolean
        ): EarlyMatchListPagerFragment {
            return EarlyMatchListPagerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SPORT_ID, sportId)
                    putInt(ARG_PLAY_TYPE_ID, playTypeId)
                    putIntArray(ARG_LEAGUE_ID, leagueIdList.toIntArray())
                    putInt(ARG_POSITION, position)
                    putBoolean(ARG_MUTABLE, mutable)
                }
            }
        }
    }
}



