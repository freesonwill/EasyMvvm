package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.utils.LogUtilsExt.logi
import arch.cayenne.lib.common.extension.sharedViewModel
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentHomeGameListBinding
import arch.cayenne.module.home.viewmodel.BasePlayTypeViewModel
import arch.cayenne.module.home.viewmodel.BasePlayTypeViewModel.Companion.TOURNAMENT_ALL_ID
import arch.cayenne.module.home.viewmodel.HomeViewModel
import arch.cayenne.module.home.viewmodel.TodayGameListViewModel
import kotlin.reflect.KClass

class TodayGameListFragment : BaseFragment<TodayGameListViewModel, FragmentHomeGameListBinding>() {
    override val vbClass: KClass<FragmentHomeGameListBinding> = FragmentHomeGameListBinding::class
    override val vmClass: KClass<TodayGameListViewModel> = TodayGameListViewModel::class
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {

        mBinding.apply {

//            tvHomeGameTitle.text = getString(leagueId?.let { LeagueType.fromId(it)?.titleRes }
//                ?: R.string.league_all)

            // 初始化 RecyclerView
//            adapter = GameListAdapter()
            rvHomeGameList.layoutManager = LinearLayoutManager(context)
//            rvHomeGameList.adapter = adapter
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
        homeViewModel.currentSportChange.observe(this) {
            mViewModel.setCurrentSport(it)
            mViewModel.getCurrentMatch()
        }
        mViewModel.matchListChange.observe(this) {
            //TODO 處理賽事卡片UI
            "賽事size: ${it.map { "${it.basicInfo.homeTeam} vs ${it.basicInfo.awayTeam}" }.toList()}".logi(this::class.java.simpleName)
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