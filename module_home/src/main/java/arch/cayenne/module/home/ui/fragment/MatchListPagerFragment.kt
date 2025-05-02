package arch.cayenne.module.home.ui.fragment

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBean
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.home.databinding.FragmentMatchListPagerBinding
import arch.cayenne.module.home.ui.adapter.MatchItemAdapter
import arch.cayenne.module.home.utils.MatchCardItemDecoration
import arch.cayenne.module.home.viewmodel.HomeViewModel
import arch.cayenne.module.home.viewmodel.MatchListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class MatchListPagerFragment :
    BaseFragment<MatchListViewModel, FragmentMatchListPagerBinding>() {
    override val vbClass: KClass<FragmentMatchListPagerBinding> =
        FragmentMatchListPagerBinding::class
    override val vmClass: KClass<MatchListViewModel> = MatchListViewModel::class
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

                override fun onOddsCellClick(item: MatchWithMarkets, selection: SelectionBeanLite) {
                    homeViewModel.setSelection(item.match.matchId, selection.selectionId)
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
        homeViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            "selectedDate: $date".logd()
            refreshListByDate()
            if (date.isNullOrEmpty()) {
                //切換後選回全部
            } else {
                //TODO 早盤更新選中的日期列表
            }
        }

        mViewModel.matchListChange.observe(viewLifecycleOwner) { matchList ->
            matchAdapter.submitList(matchList)

        }

    }

    fun test() {

    }

    private fun refreshListByDate() {
        //TODO 早盤更新選中的日期列表
    }

    override fun initData() {
        arguments?.apply {
            mViewModel.setTournamentId(this.getInt(ARG_LEAGUE_ID))
            mViewModel.setSportId(this.getInt(ARG_SPORT_ID))
            mViewModel.setPlayTypeId(this.getInt(ARG_PLAY_TYPE_ID))
        }
        "joseph initData: playTypeId: ${mViewModel.getPlayTypeId()}, tournamentId: ${mViewModel.getTournamentId()}".logd()
        //TODO 早盤日期要資料
        mViewModel.getCurrentMatch()
    }

    companion object {
        private const val ARG_SPORT_ID = "sport_id"
        private const val ARG_PLAY_TYPE_ID = "play_type_id"
        private const val ARG_LEAGUE_ID = "arg_league_id"
        fun newInstance(sportId: Int, playTypeId: Int, leagueId: Int): MatchListPagerFragment {
            return MatchListPagerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SPORT_ID, sportId)
                    putInt(ARG_PLAY_TYPE_ID, playTypeId)
                    putInt(ARG_LEAGUE_ID, leagueId)
                }
            }
        }
    }
}