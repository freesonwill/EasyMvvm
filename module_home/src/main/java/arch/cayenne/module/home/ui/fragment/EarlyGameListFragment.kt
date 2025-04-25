package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.home.databinding.FragmentHomeGameListBinding
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
        mViewModel.matchListChange.observe(viewLifecycleOwner) {
            //TODO 處理賽事卡片UI

        }
    }

    override fun initData() {
        arguments?.apply {
            mViewModel.setTournamentId(this.getInt(ARG_LEAGUE_ID, HomeViewModel.TOURNAMENT_ALL_ID))
            mViewModel.setSportId(this.getInt(ARG_SPORT_ID))
        }
        mViewModel.getCurrentMatch()
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
        private const val ARG_SPORT_ID = "sport_id"

        fun newInstance(sportId: Int, leagueId: Int, date: String): TodayGameListFragment {
            val fragment = TodayGameListFragment()
            val args = Bundle()
            args.putInt(ARG_SPORT_ID, sportId)
            args.putString(ARG_DATE, date)

            args.putInt(ARG_LEAGUE_ID, leagueId)
            fragment.arguments = args
            return fragment
        }
    }
}
