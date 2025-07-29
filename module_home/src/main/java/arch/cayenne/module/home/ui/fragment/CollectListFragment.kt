package arch.cayenne.module.home.ui.fragment

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.bet.ui.fragment.BetSheetFragment
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.databinding.FragmentCollectListBinding
import arch.cayenne.module.home.databinding.TitleBarFavoriteBinding
import arch.cayenne.module.home.ui.adapter.MatchItemAdapter
import arch.cayenne.module.home.ui.adapter.OnMatchItemClickListener
import arch.cayenne.module.home.ui.view.decoration.MatchCardItemDecoration
import arch.cayenne.module.home.ui.viewmodel.CollectListViewModel
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 * @author:
 * @date: 2025/5/23 上午11:30
 * @description:
 */
class CollectListFragment : BaseFragment<CollectListViewModel, FragmentCollectListBinding>() {
    override val vbClass: KClass<FragmentCollectListBinding> = FragmentCollectListBinding::class
    override val vmClass: KClass<CollectListViewModel> = CollectListViewModel::class
    override val keepViewOnNavigation: Boolean = true
    private val titleBarBinding: TitleBarFavoriteBinding by lazy {
        TitleBarFavoriteBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }
    private lateinit var matchAdapter: MatchItemAdapter
    private val gameLayoutManager by lazy { LinearLayoutManager(context) }
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()

    override fun initView(savedInstanceState: Bundle?) {
        with (mBinding) {
            titleBar.loadDynamicsTitleBar(titleBarBinding.root) {
                findNavController().navigateUp()
            }

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
                    mViewModel.removeMatchCollect(item)
                }

                override fun onOddsCellClick(selection: SelectionBeanLite, x: Float, y: Float) {
                    lifecycleScope.launch {
                        val status = mViewModel.setSelection(selection.selectionId)
                        if (status is AddSelectionStatus.Success.Single) {
                            BetSheetFragment.newInstance().show(requireActivity().supportFragmentManager)
                        } else if (status is AddSelectionStatus.Failure.DisableComboForParlay) {
                            showToast(getString(R.string.disabled_to_combo))
                        } else if (status is AddSelectionStatus.Failure.DisableComboForProvider) {
                            showToast(getString(R.string.disabled_to_combo_for_provider))
                        } else if (status is AddSelectionStatus.Failure.NetworkDisconnected) {
                            showToast(getString(arch.cayenne.lib.common.R.string.toast_server_disconnected))
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
            })
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (enter && nextAnim != 0) {
            val animation = AnimationUtils.loadAnimation(requireContext(), nextAnim)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    mViewModel.startObserveMatch()
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
            animation
        } else {
            super.onCreateAnimation(transit, enter, nextAnim)
        }
    }

    override fun initListener() {
        titleBarBinding.llWalletEntry.clickNoRepeat {
            navigate(Uri.parse("walisport://module_topup/topUpFragment"))
        }
        titleBarBinding.llWalletEntry.addScaleOnTouchAnimation(titleBarBinding.ivWalletAdd)
    }

    @SuppressLint("SetTextI18n")
    override fun createObserver() {
        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            titleBarBinding.tvMoney.text = "${CurrencySymbols.getSymbol(it.currency)} ${it.balance.getFormalMoney()}"
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
                    DataState.NetworkUnavailable -> {
                        mViewModel.changePageEnd(true)
                        loadingView.visibility = View.GONE
                        refreshLayout.finishRefresh()
                        refreshLayout.finishLoadMore()
                        refreshLayout.setEnableLoadMore(false)
                        clDynamics.visibility = View.VISIBLE
                        clDynamics.setState(
                            DynamicStateLayout.States.NETWORK_ANOMALY,
                            arch.cayenne.lib.common.R.string.error_net.getString()
                        )
                    }
                    DataState.DataEmpty -> {     //這個DataEmpty表示api抓不到任何資料了，有可能是頁面到底，或是從第一頁就抓不到資料
                        mViewModel.changePageEnd(true)
                        refreshLayout.finishLoadMore()
                        refreshLayout.setEnableLoadMore(false)
                    }
                    HomeState.Match.DataEmpty -> {  //這個DataEmpty表示真的從第一頁就抓不到資料，表示當前的選擇沒有任何賽事
                        loadingView.visibility = View.GONE
                        refreshLayout.finishRefresh()
                        clDynamics.visibility = View.VISIBLE
                        clDynamics.setState(
                            DynamicStateLayout.States.DATA_EMPTY,
                            R.string.collect_list_empty.getString()
                        )
                    }
                    HomeState.Match.Loading -> {
                        loadingView.visibility = View.VISIBLE
                        clDynamics.visibility = View.GONE
                        refreshLayout.setEnableLoadMore(true)
                    }
                    HomeState.Match.Refreshing -> {
                        clDynamics.visibility = View.GONE
                        refreshLayout.setEnableLoadMore(true)
                    }
                    HomeState.Match.LoadingNext -> {
                        clDynamics.visibility = View.GONE
                    }
                    DataState.LoadSuccess -> {
                        loadingView.visibility = View.GONE
                        if (refreshLayout.isRefreshing) refreshLayout.finishRefresh()
                        refreshLayout.finishLoadMore()
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