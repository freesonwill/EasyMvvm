package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentChampionBinding
import arch.cayenne.module.home.databinding.TitleBarChampionBinding
import arch.cayenne.module.home.ui.adapter.ChampionItemAdapter
import arch.cayenne.module.home.ui.view.decoration.MatchCardItemDecoration
import arch.cayenne.module.home.ui.viewmodel.ChampionViewModel
import com.bumptech.glide.Glide
import kotlin.reflect.KClass

class ChampionFragment: BaseFragment<ChampionViewModel, FragmentChampionBinding>(){

    override val vbClass: KClass<FragmentChampionBinding> = FragmentChampionBinding::class
    override val vmClass: KClass<ChampionViewModel> = ChampionViewModel::class
    private val args: ChampionFragmentArgs by navArgs()
    private lateinit var championAdapter: ChampionItemAdapter

    private val tittleBarBinding: TitleBarChampionBinding by lazy {
        TitleBarChampionBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }
    override fun initData() {
        super.initData()
        mViewModel.setMatchId(args.matchId)
        mViewModel.getChampionDetail()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {
            titleBar.loadDynamicsTitleBar(tittleBarBinding.root)
            rvChampion.apply {
                championAdapter = ChampionItemAdapter()
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
            if (matchWithMarkets != null) {
                Glide.with(this).load(matchWithMarkets.match.basicInfo.tournamentIcon)
                    .error(R.drawable.title_league_icon).into(tittleBarBinding.ivLandscapeLeagueIcon)
                tittleBarBinding.tvCompetitionName.text = matchWithMarkets.match.basicInfo.matchName

                championAdapter.submitList(matchWithMarkets.markets)
            }

        }
    }

}