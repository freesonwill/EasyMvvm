package arch.cayenne.module.home.ui.fragment

import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.utils.LogUtilsExt.logi
import arch.cayenne.lib.common.extension.sharedViewModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.module.home.data.Match
import arch.cayenne.module.home.databinding.FragmentHomeGameListBinding
import arch.cayenne.module.home.ui.adapter.MatchItemAdapter
import arch.cayenne.module.home.utils.MatchCardItemDecoration
import arch.cayenne.module.home.viewmodel.BasePlayTypeViewModel.Companion.TOURNAMENT_ALL_ID
import arch.cayenne.module.home.viewmodel.HomeViewModel
import arch.cayenne.module.home.viewmodel.TodayGameListViewModel
import kotlin.reflect.KClass

class TodayGameListFragment : BaseFragment<TodayGameListViewModel, FragmentHomeGameListBinding>() {
    override val vbClass: KClass<FragmentHomeGameListBinding> = FragmentHomeGameListBinding::class
    override val vmClass: KClass<TodayGameListViewModel> = TodayGameListViewModel::class
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()
    private lateinit var matchAdapter: MatchItemAdapter

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {
            matchAdapter = MatchItemAdapter(object : MatchItemAdapter.OnMatchItemClickListener {
                override fun onLiveEntryClick(item: Match) {
                    navigate(Uri.parse("walisport://module_live/liveFragment?matchId=${item.matchId}"))
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
        homeViewModel.currentSportChange.observe(this) {
            mViewModel.setCurrentSport(it)
            mViewModel.getCurrentMatch()
        }
        mViewModel.matchListChange.observe(this) { matchList ->
            //TODO 處理賽事卡片UI
            matchAdapter.submitList(matchList)
            "joseph 賽事size: ${
                matchList.map { "${it.basicInfo.homeTeam} vs ${it.basicInfo.awayTeam}" }.toList()
            }".logi(this::class.java.simpleName)
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