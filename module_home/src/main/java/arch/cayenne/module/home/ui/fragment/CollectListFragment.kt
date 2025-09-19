package arch.cayenne.module.home.ui.fragment

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.scrollToBottomWithLoadMore
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.bet.data.AddSelectionStatus
import arch.cayenne.module.bet.ui.fragment.BetSheetFragment
import arch.cayenne.module.bet.viewmodel.FloatingButtonControlViewModel
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.databinding.FragmentCollectListBinding
import arch.cayenne.module.home.databinding.TitleBarFavoriteBinding
import arch.cayenne.module.home.ui.adapter.MatchItemAdapter
import arch.cayenne.module.home.ui.adapter.OnMatchItemClickListener
import arch.cayenne.module.home.ui.view.decoration.MatchCardItemDecoration
import arch.cayenne.module.home.ui.viewmodel.CollectListViewModel
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
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
    private val titleBarBinding: TitleBarFavoriteBinding by lazy {
        TitleBarFavoriteBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }
    private lateinit var matchAdapter: MatchItemAdapter
    private val gameLayoutManager by lazy { LinearLayoutManager(context) }
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()
    private val fabViewModel: FloatingButtonControlViewModel by activityViewModel()

    override fun initView(savedInstanceState: Bundle?) {
        with (mBinding) {
            titleBar.loadDynamicsTitleBar(titleBarBinding.root) {
                findNavController().navigateUp()
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

                override fun onOddsCellClick(cell: WeakReference<View>, selection: SelectionBeanLite, x: Float, y: Float) {
                    lifecycleScope.launch {
                        if (mViewModel.getCurrentSelectionCount() == 0) {
                            BetSheetFragment.show(requireActivity()) {
                                cell.get()?.isSelected = true
                            }
                        } else {
                            cell.get()?.isSelected = true
                        }
                        val status = mViewModel.setSelection(selection.selectionId)

                        if (status !is AddSelectionStatus.Success) {
                            cell.get()?.isSelected = false
                        }

                        if (status is AddSelectionStatus.Failure) {
                            status.msg?.let {
                                showToast(it)
                            }
                        } else if (status is AddSelectionStatus.Success.Combo || status is AddSelectionStatus.Success.Update) {
                            fabViewModel.setClickAnimation(x, y)
                        }
                    }
                }

            })
            val decoration = MatchCardItemDecoration(12.dp2px)
            rvCollectList.apply {
                this.layoutManager = gameLayoutManager
                this.adapter = matchAdapter
                addItemDecoration(decoration)
            }
            (rvCollectList.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            rvCollectList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    // 滑動停止時觸發
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        subscribeVisibleMatch()
                    }
                }
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    rvCollectList.scrollToBottomWithLoadMore(minScrollCount = 8, {
                        if (mViewModel.apiStateListener.value != HomeState.Match.LoadSuccess) return@scrollToBottomWithLoadMore
                        mViewModel.loadNextPage()
                    }, {
                        if (mViewModel.apiStateListener.value == HomeState.Match.LoadNextFailure) {
                            mViewModel.loadNextPage()
                        }
                    })
                }
            })
        }
        mBinding.rvCollectList.touchBackPressed()
        mBinding.root.touchBackPressed()
    }

    override fun initData() {
        super.initData()
        mViewModel.getCacheMatch()
    }

    override fun onFragmentAnimEnd(isEnter: Boolean) {
        if (isEnter) mViewModel.startObserveMatch()
    }

    override fun initListener() {
        titleBarBinding.llWalletEntry.clickNoRepeat {
            navigate(Uri.parse("walisport://module_topup/topUpFragment"))
        }
        titleBarBinding.llWalletEntry.addScaleOnTouchAnimation(titleBarBinding.ivWalletAdd)
    }

    @SuppressLint("SetTextI18n")
    override suspend fun createObserver() {
        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            titleBarBinding.tvMoney.text = "${CurrencySymbols.getSymbol(it?.currency?:"")} ${(it?.balance?:0L).getFormalMoney()}"
        }
        homeViewModel.timer.observeEvent(viewLifecycleOwner, this) {
            mViewModel.updateMatchLiveData()
        }
        mViewModel.matchListChange.observe(viewLifecycleOwner) { matchList ->
            val preEmpty = matchAdapter.currentList.isEmpty()
            matchAdapter.submitList(matchList)
            if (preEmpty && matchList.isNotEmpty()) {
                mBinding.rvCollectList.doOnPreDraw {
                    subscribeVisibleMatch()
                }
            }
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
                        matchAdapter.setLastItemType(MatchItemAdapter.LAST_ITEM_LOAD_MORE)
                    }
                    HomeState.Match.LoadingNext -> {
                        clDynamics.visibility = View.GONE
                    }
                    DataState.LoadSuccess -> {
                        if (refreshLayout.isRefreshing) refreshLayout.finishRefresh()
                        clDynamics.visibility = View.GONE
                    }
                }
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
}