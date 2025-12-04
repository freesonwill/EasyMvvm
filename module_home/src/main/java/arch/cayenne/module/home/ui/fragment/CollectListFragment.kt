package arch.cayenne.module.home.ui.fragment

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.sharedViewModel
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
import arch.cayenne.module.home.databinding.FragmentCollectListBinding
import arch.cayenne.module.home.ui.adapter.MatchItemAdapter
import arch.cayenne.module.home.ui.adapter.OnMatchItemClickListener
import arch.cayenne.module.home.ui.view.decoration.MatchCardItemDecoration
import arch.cayenne.module.home.ui.viewmodel.CollectDate
import arch.cayenne.module.home.ui.viewmodel.CollectListViewModel
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import arch.cayenne.module.home.utils.DateUtils
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.lang.ref.WeakReference
import kotlin.reflect.KClass

/**
 * @author:
 * @date: 2025/5/23 上午11:30
 * @description:
 */
class CollectListFragment : BaseFragment<CollectListViewModel, FragmentCollectListBinding>() {
    override val vbClass: KClass<FragmentCollectListBinding> = FragmentCollectListBinding::class
    override val vmClass: KClass<CollectListViewModel> = CollectListViewModel::class
    private lateinit var matchAdapter: MatchItemAdapter
    private val gameLayoutManager by lazy { LinearLayoutManager(context) }
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()
    private val fabViewModel: FloatingButtonControlViewModel by activityViewModel()


    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            // 隱藏 titleBar（作為 ViewPager 頁面使用）
            titleBar.visibility = View.GONE

            refreshLayout.post {
                (refreshLayout.layoutParams as? androidx.constraintlayout.widget.ConstraintLayout.LayoutParams)?.apply {
                    topToBottom =
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
                    topToTop =
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
                    topMargin = 0
                    refreshLayout.layoutParams = this
                }
            }

            refreshLayout.setEnableLoadMore(false)
            refreshLayout.setEnableScrollContentWhenLoaded(true)
            refreshLayout.setOnRefreshListener {
                mViewModel.reload()
            }

            matchAdapter = MatchItemAdapter(object : OnMatchItemClickListener {
                override fun onLiveEntryClick(item: MatchWithMarkets) {
                    navigate(Uri.parse("walisport://module_live/liveFragment?matchId=${item.match.matchId}&sportId=${item.match.basicInfo.sportId}"))
                }

                override fun onFavoriteClick(view: ImageView, item: MatchWithMarkets) {
                    lifecycleScope.launch {
                        mViewModel.removeMatchCollect(item)
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

            }, PlayType.FAVORITE.id)
            val decoration = MatchCardItemDecoration(12.dp2px)
            rvCollectList.apply {
                this.layoutManager = gameLayoutManager
                this.adapter = matchAdapter
                addItemDecoration(decoration)
            }
            (rvCollectList.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            rvCollectList.addOnScrollListener(scrollListener)
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            // 滑動停止時觸發
            if (newState == RecyclerView.SCROLL_STATE_IDLE && view != null && isAdded) {
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

            updateDisplayDate(layoutManager)
        }
    }

    private fun updateDisplayDate(layoutManager: LinearLayoutManager) {
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        val itemList = matchAdapter.currentList
        if (itemList.isEmpty()) {
            return
        }
        when (val item = itemList[firstVisibleItemPosition]) {
            is MatchWithMarkets -> {

                val collectDate = CollectDate(
                    "",
                    "",
                    item.match.basicInfo.startTime,
                )

                if (mBinding.rvCollectList.scrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mViewModel.setDisplayDate(collectDate)
                }

            }

            is MatchNoMoreData -> {

            }

            is MatchLoadMoreData -> {

            }

            is MatchDateItem -> {
                val collectDate = CollectDate(
                    "",
                    "",
                    item.timeStamp,
                )

                if (mBinding.rvCollectList.scrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mViewModel.setDisplayDate(collectDate)
                }

            }

            else -> {}
        }
    }

    override fun onDestroyView() {
        mBinding.rvCollectList.removeOnScrollListener(scrollListener)
        super.onDestroyView()
    }

    override fun initData() {
        super.initData()
        mViewModel.getCacheMatch()
        mViewModel.startObserveMatch()
    }

    override fun onFragmentAnimEnd(isEnter: Boolean) {
    }

    override fun initListener() {
    }

    @SuppressLint("SetTextI18n")
    override suspend fun createObserver() {
        homeViewModel.timer.observeEvent(viewLifecycleOwner, this) {
            mViewModel.updateMatchLiveData()
        }
        mViewModel.matchListChange.observe(viewLifecycleOwner) { matchList ->
            val preEmpty = matchAdapter.currentList.isEmpty()
            val list = addDateItem(
                matchList
            )
            matchAdapter.submitList(list)
            if (preEmpty && matchList.isNotEmpty()) {
                mBinding.rvCollectList.doOnPreDraw {
                    subscribeVisibleMatch()
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
        }

        mViewModel.apiStateListener.observe(viewLifecycleOwner) { state ->
            with(mBinding) {
                when (state) {
                    DataState.NetworkUnavailable, HomeState.Match.LoadNextFailure -> {
                        mViewModel.changePageEnd(true)
                        refreshLayout.finishRefresh()
                        matchAdapter.setLastItemType(MatchItemAdapter.LAST_ITEM_NONE)
                        if (state == DataState.NetworkUnavailable) {
                            clDynamics.visibility = View.VISIBLE
                            clDynamics.setState(
                                DynamicStateLayout.States.NETWORK_ANOMALY(),
                                arch.cayenne.lib.common.R.string.error_net.getString()
                            )
                        }
                        showToast(arch.cayenne.lib.common.R.string.toast_server_disconnected.getString())
                    }

                    DataState.NoMoreData -> {     //這個DataEmpty表示api抓不到任何資料了，有可能是頁面到底，或是從第一頁就抓不到資料
                        clDynamics.visibility = View.GONE
                        mViewModel.changePageEnd(true)
                        matchAdapter.setLastItemType(MatchItemAdapter.LAST_ITEM_NO_MORE)
                    }

                    HomeState.Match.DataEmpty -> {  //這個DataEmpty表示確定真的從第一頁就抓不到資料，表示當前的選擇沒有任何賽事
                        refreshLayout.finishRefresh()
                        matchAdapter.setLastItemType(MatchItemAdapter.LAST_ITEM_LOAD_MORE)
                        clDynamics.visibility = View.VISIBLE
                        clDynamics.setState(
                            DynamicStateLayout.States.DATA_EMPTY,
                            R.string.collect_list_empty.getString()
                        )
                    }

                    HomeState.Match.Loading -> {
                        clDynamics.visibility = View.GONE
                    }

                    HomeState.Match.Refreshing -> {
                        clDynamics.visibility = View.GONE
                        mViewModel.changePageEnd(false)
                        matchAdapter.setLastItemType(MatchItemAdapter.LAST_ITEM_LOAD_MORE)
                    }

                    HomeState.Match.LoadingNext -> {
                        clDynamics.visibility = View.GONE
                    }

                    DataState.LoadSuccess, HomeState.Match.LoadSuccess -> {
                        if (refreshLayout.isRefreshing) refreshLayout.finishRefresh()
                        clDynamics.visibility = View.GONE
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

                    HomeState.Match.DataEmpty -> {

                    }

                    DataState.NoMoreData -> {
                        mViewModel.changePrevPageEnd(true)
                    }

                    HomeState.Match.Loading -> {
                    }

                    DataState.LoadSuccess, HomeState.Match.LoadSuccess -> {
                        mViewModel.changePrevPageEnd(false)
                    }
                }
            }
        }

        mViewModel.displayDate.observe(viewLifecycleOwner) {
            it?.let {
                val (date, week) = DateUtils.getDisplay(it.timestamp)
                val display = "$date $week"
                mBinding.tvHover.text = display
            }
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
}