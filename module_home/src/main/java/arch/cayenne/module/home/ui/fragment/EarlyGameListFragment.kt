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
import arch.cayenne.module.home.viewmodel.EarlyGameListViewModel
import arch.cayenne.module.home.viewmodel.HomeViewModel
import kotlin.reflect.KClass

class EarlyGameListFragment : BaseFragment<EarlyGameListViewModel, FragmentHomeGameListBinding>() {
    override val vbClass: KClass<FragmentHomeGameListBinding> = FragmentHomeGameListBinding::class
    override val vmClass: KClass<EarlyGameListViewModel> = EarlyGameListViewModel::class
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()

    private var selectedDate: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedDate = it.getString(ARG_DATE).orEmpty()
        }
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
            mViewModel.setCurrentTournamentId(this.getInt(
                ARG_LEAGUE_ID,
                BasePlayTypeViewModel.TOURNAMENT_ALL_ID
            ))
        }
    }

    fun onDateChanged(newDate: String) {
        selectedDate = newDate
        refreshData()
    }

    private fun refreshData() {
        // 根據 leagueId 與 date 更新列表
    }

    companion object {
        private const val ARG_DATE = "arg_date"
        private const val ARG_LEAGUE_ID = "league_id"
        fun newInstance(leagueId: Int, date: String): EarlyGameListFragment {
            return EarlyGameListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_LEAGUE_ID, leagueId)
                    putString(ARG_DATE, date)
                }
            }
        }
    }
}
