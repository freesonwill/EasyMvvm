package arch.cayenne.module.home.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.home.viewmodel.HomeViewModel
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentChampionBinding
import com.bumptech.glide.Glide
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.home.databinding.FragmentChampionBinding
import arch.cayenne.module.home.ui.view.TournamentSectionView
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import kotlin.reflect.KClass
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.bet.ui.fragment.BetSheetFragment
import arch.cayenne.module.home.databinding.TitleBarChampionBinding
import arch.cayenne.module.home.ui.adapter.ChampionItemAdapter
import arch.cayenne.module.home.ui.adapter.OnChampionItemClickListener
import arch.cayenne.module.home.ui.view.decoration.MatchCardItemDecoration
import arch.cayenne.module.home.ui.viewmodel.ChampionViewModel
import kotlinx.coroutines.launch

class ChampionFragment: BaseFragment<ChampionViewModel, FragmentChampionBinding>(){

    override val vbClass: KClass<FragmentChampionBinding> = FragmentChampionBinding::class
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()
    private var dropdownListener: TournamentSectionView.OnChampionDropdownListener? = null
    override val vmClass: KClass<ChampionViewModel> = ChampionViewModel::class
    private val args: ChampionFragmentArgs by navArgs()
    private lateinit var championAdapter: ChampionItemAdapter

    private val tittleBarBinding: TitleBarChampionBinding by lazy {
        TitleBarChampionBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }
    override fun initData() {
        super.initData()
        mViewModel.setMatchId(args.matchId)
        mViewModel.subscribeMatch()
        mViewModel.getChampionDetail()
        arguments?.apply {
            mViewModel.setCurrentSport(this.getInt(ARG_SPORT_ID))
            homeViewModel.setCurrentSport(this.getInt(ARG_SPORT_ID))
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dropdownListener = parentFragment as? TournamentSectionView.OnChampionDropdownListener
    }
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {
            titleBar.loadDynamicsTitleBar(tittleBarBinding.root)
            rvChampion.apply {
                championAdapter = ChampionItemAdapter(object : OnChampionItemClickListener {
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

                this.adapter = championAdapter
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(MatchCardItemDecoration(12.dp2px))
            }
        }

    }

    override fun initListener() {
    }

    override fun createObserver() {
        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            tittleBarBinding.tvMoney.text = it.getFormalMoney()
        }
        mViewModel.matchWithMarketsChange.observe(viewLifecycleOwner) { matchWithMarkets ->
            with(mBinding) {
                if (matchWithMarkets != null && matchWithMarkets.markets.isNotEmpty()) {
                    clDynamics.visibility = View.GONE
                    Glide.with(this@ChampionFragment).load(matchWithMarkets.match.basicInfo.tournamentIcon)
                        .error(R.drawable.title_league_icon).into(tittleBarBinding.ivLandscapeLeagueIcon)
                    tittleBarBinding.tvCompetitionName.text = matchWithMarkets.match.basicInfo.matchName

                    championAdapter.submitList(matchWithMarkets.markets)
                } else {
                    clDynamics.visibility = View.VISIBLE
                    clDynamics.setState(
                        DynamicStateLayout.States.DATA_EMPTY,
                        R.string.lineup_empty.getString()
                    )
                }
            }


        }
        homeViewModel.allTournaments.observe(viewLifecycleOwner) { list ->
            "joseph observe tournaments:$list".logd()
            if (!list.isNullOrEmpty()) {
                mBinding.tsvContainer.postSetTournamentList(list)
// 使用者點擊某聯賽
                mBinding.tsvContainer.onTournamentClick = { id ->
                    mViewModel.selectTournament(id)
                    homeViewModel.setShowAllTournaments(false)
                    mBinding.tsvContainer.collapseWithAnimation()
                }

                // 使用者點擊 collapse icon
                mBinding.tsvContainer.onCollapse = {
                    // 呼叫 parent fragment（NewHomeFragment）的 toggle 方法
                    dropdownListener?.onRequestCollapseChampion()
                }
            }
        }
    }

    private fun initSectionLayout(it: List<TournamentDataModel>) {
        with(mBinding) {
            tsvContainer.setTournamentList(it)
        }
    }
    override fun onDestroyView() {
        mViewModel.cancelSubscribeMatch()
        super.onDestroyView()
    }
    override fun onDetach() {
        dropdownListener = null
        super.onDetach()
    }
    companion object {
        private const val ARG_SPORT_ID = "sport_id"
        fun newInstance(sportId: Int): ChampionFragment {
            return ChampionFragment().apply {
                "joseph new ChampionFragment:$sportId".logd()
                arguments = Bundle().apply {
                    putInt(ARG_SPORT_ID, sportId)
                }
            }
        }
    }
}