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
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.bet.ui.fragment.BetSheetFragment
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.MatchListState
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

                override fun onOddsCellClick(selection: SelectionBeanLite) {
                    lifecycleScope.launch {
                        val status = mViewModel.setSelection(selection.selectionId)
                        if (status == AddSelectionStatus.SINGLE) {
                            BetSheetFragment.newInstance().show(parentFragmentManager)
                        } else if (status == AddSelectionStatus.DISABLE_COMBO) {
                            showToast(getString(R.string.disabled_to_combo))
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
        val animation = AnimationUtils.loadAnimation(requireContext(), nextAnim)
        if (enter) {
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    mViewModel.startObserveMatch()
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }

        return animation
    }

    override fun initData() {
        super.initData()

    }

    override fun initListener() {
        titleBarBinding.llWalletEntry.clickNoRepeat {
            navigate(Uri.parse("walisport://module_topup/topUpFragment"))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun createObserver() {
        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            titleBarBinding.tvMoney.text = "${CurrencySymbols.CNY} ${it.getFormalMoney()}"
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
        mViewModel.state.observeEvent(viewLifecycleOwner, this) { state ->
            with(mBinding) {
                when (state) {
                    MatchListState.FIRST_LOADING -> {
                        clDynamics.visibility = View.GONE
//                        homeViewModel.changeState(HomeState.LOADING_MATCH)
                    }

                    MatchListState.REFRESHING -> {
                        clDynamics.visibility = View.GONE
                    }

                    MatchListState.IDLE -> {
                        if (refreshLayout.isRefreshing) refreshLayout.finishRefresh()
                        refreshLayout.finishLoadMore()
                        clDynamics.visibility = View.GONE
//                        homeViewModel.changeState(HomeState.LOADING_MATCH_SUCCESS)
                    }

                    MatchListState.FAILED -> {
                        mBinding.refreshLayout.finishRefresh()
                        refreshLayout.finishLoadMore()
                        clDynamics.visibility = View.VISIBLE
                        clDynamics.setState(
                            DynamicStateLayout.States.DATA_EMPTY,
                            R.string.lineup_empty.getString()
                        )
//                        homeViewModel.changeState(HomeState.LOADING_MATCH_SUCCESS)
                    }

                    MatchListState.LOADING_NEXT -> {
                        clDynamics.visibility = View.GONE
                    }
                    MatchListState.NO_MORE_DATA -> {
                        refreshLayout.finishLoadMore()
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