package arch.cayenne.module.home.ui.fragment

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
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
import arch.cayenne.module.home.viewmodel.HomeViewModel.Companion.TOURNAMENT_ALL_ID
import arch.cayenne.module.home.viewmodel.HomeViewModel
import arch.cayenne.module.home.viewmodel.TodayGameListViewModel
import kotlinx.coroutines.delay
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
        mViewModel.matchListChange.observe(viewLifecycleOwner) { matchList ->
            val oldList = matchAdapter.currentList
            matchAdapter.submitList(oldList+matchList)
            //測試
            lifecycleScope.launch {
                if (mViewModel.page <= 3) {
                    delay(3000)
                    mViewModel.page++
                    mViewModel.getCurrentMatch()
                }
            }

        }
    }

    override fun initData() {
        arguments?.apply {
            mViewModel.setTournamentId(this.getInt(ARG_LEAGUE_ID, HomeViewModel.TOURNAMENT_ALL_ID))
            mViewModel.setSportId(this.getInt(ARG_SPORT_ID))
        }
        mViewModel.getCurrentMatch()
    }

    companion object {
        private const val ARG_LEAGUE_ID = "league_id"
        private const val ARG_SPORT_ID = "sport_id"

        fun newInstance(sportId: Int, leagueId: Int): TodayGameListFragment {
            val fragment = TodayGameListFragment()
            val args = Bundle()
            args.putInt(ARG_SPORT_ID, sportId)
            args.putInt(ARG_LEAGUE_ID, leagueId)
            fragment.arguments = args
            return fragment
        }
    }
}