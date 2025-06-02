package arch.cayenne.module.home.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
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
    private val titleBarBinding: TitleBarFavoriteBinding by lazy {
        TitleBarFavoriteBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }
    private lateinit var matchAdapter: MatchItemAdapter
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
//                    mViewModel.addMatchCollect(item, !item.match.collect)
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
            val layoutManager = LinearLayoutManager(context)
            rvCollectList.apply {
                this.layoutManager = layoutManager
                this.adapter = matchAdapter
                addItemDecoration(decoration)
            }
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

        }
    }

    override fun createObserver() {
        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            titleBarBinding.tvMoney.text = it.getFormalMoney()
        }
        mViewModel.matchListChange.observe(viewLifecycleOwner) { matchList ->
            matchAdapter.submitList(matchList)
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
}