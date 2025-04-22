package arch.cayenne.module.home.ui.fragment

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.base.utils.LogUtilsExt.logi
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBean
import arch.cayenne.module.bet.ui.fragment.BetSheetFragment
import arch.cayenne.module.home.databinding.FragmentHomeGameListBinding
import arch.cayenne.module.home.ui.adapter.MatchItemAdapter
import arch.cayenne.module.home.utils.MatchCardItemDecoration
import arch.cayenne.module.home.viewmodel.BasePlayTypeViewModel.Companion.TOURNAMENT_ALL_ID
import arch.cayenne.module.home.viewmodel.HomeViewModel
import arch.cayenne.module.home.viewmodel.TodayGameListViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class TodayGameListFragment : BaseFragment<TodayGameListViewModel, FragmentHomeGameListBinding>() {
    override val vbClass: KClass<FragmentHomeGameListBinding> = FragmentHomeGameListBinding::class
    override val vmClass: KClass<TodayGameListViewModel> = TodayGameListViewModel::class
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()
    private lateinit var matchAdapter: MatchItemAdapter

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {
            matchAdapter = MatchItemAdapter(object : MatchItemAdapter.OnMatchItemClickListener {
                override fun onLiveEntryClick(item: MatchWithMarkets) {
                    navigate(Uri.parse("walisport://module_live/liveFragment?matchId=${item.match.matchId}&sportId=${item.match.basicInfo.sportId}"))
                }

                override fun onFavoriteClick(item: MatchWithMarkets) {
                }

                override fun onOddsCellClick(item: MatchWithMarkets, selection: SelectionBean) {
                    //TODO 投注點擊狀態顯示規則待處理
                    lifecycleScope.launch {
                        val id =
                            homeViewModel.setSelection(item.match.matchId, selection.selectionId)
                        if (id == BetTypeEnum.SINGLE) {
                            BetSheetFragment.newInstance(item.match.matchId)
                                .show(childFragmentManager)
                        }
                    }
                }
            })
            val decoration = MatchCardItemDecoration(12.dp2px)
            mBinding.rvHomeGameList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = matchAdapter
                addItemDecoration(decoration)
            }
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
        homeViewModel.currentSportChange.observe(viewLifecycleOwner) {
            mViewModel.setCurrentSport(it)
            mViewModel.getCurrentMatch()
        }
        mViewModel.matchListChange.observe(viewLifecycleOwner) { matchList ->
            //TODO 處理賽事卡片UI
            matchAdapter.submitList(matchList)
        }
    }

    override fun initData() {
        arguments?.apply {
            mViewModel.setCurrentTournamentId(this.getInt(ARG_LEAGUE_ID, TOURNAMENT_ALL_ID))
        }
    }

    companion object {
        private const val ARG_LEAGUE_ID = "league_id"

        fun newInstance(leagueId: Int): TodayGameListFragment {
            val fragment = TodayGameListFragment()
            val args = Bundle()
            args.putInt(ARG_LEAGUE_ID, leagueId)
            fragment.arguments = args
            return fragment
        }
    }
}