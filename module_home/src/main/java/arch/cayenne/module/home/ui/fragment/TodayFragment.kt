package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.skin.res.SportSkinResourceManager.getColorStateList
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentTodayBinding
import arch.cayenne.module.home.databinding.ItemLeagueTabBinding
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.ui.adapter.LeaguePagerAdapter
import arch.cayenne.module.home.viewmodel.BasePlayTypeViewModel.Companion.TOURNAMENT_ALL_ID
import arch.cayenne.module.home.viewmodel.HomeViewModel
import arch.cayenne.module.home.viewmodel.TodayViewModel
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.KClass

class TodayFragment : BaseFragment<TodayViewModel, FragmentTodayBinding>() {
    override val vbClass: KClass<FragmentTodayBinding> = FragmentTodayBinding::class
    override val vmClass: KClass<TodayViewModel> = TodayViewModel::class
    private lateinit var leagueAdapter: LeaguePagerAdapter
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()
    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            vpGameList.isSaveEnabled = false
            vpGameList.adapter = null
            leagueAdapter = LeaguePagerAdapter(
                childFragmentManager,
                viewLifecycleOwner.lifecycle,
                PlayType.TODAY
            )
            vpGameList.adapter = leagueAdapter
            tlLeagueList.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.customView?.isSelected = true
//                    tab?.view?.isSelected = true
                    vpGameList.currentItem = tab?.position ?: 0
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            vpGameList.post {
                vpGameList.currentItem = 0
//                tlLeagueList.getTabAt(0)?.select()
            }
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
        homeViewModel.currentSportChange.observe(this) {
            mViewModel.setCurrentSport(it)
            mViewModel.getCurrentTournament(it)
        }
        mViewModel.tournaments.observe(this) {
            initLeaguesLayout(it)
        }
    }

    private fun initLeaguesLayout(tournaments: List<TournamentDataModel>) {
        mBinding.apply {
            leagueAdapter.setData(tournaments)

            TabLayoutMediator(tlLeagueList, vpGameList) { tab, position ->
                val tournament = tournaments[position]

                val tabBinding =
                    ItemLeagueTabBinding.inflate(LayoutInflater.from(context), null, false)
                tabBinding.apply {

                    if (tournament.id == TOURNAMENT_ALL_ID) {   //ALL 標籤
                        ivLeagueIcon.visibility = View.GONE
                        tvLeagueName.text = getString(R.string.league_all)
                    } else {
                        Glide.with(this@TodayFragment).load(tournament.icon).into(ivLeagueIcon)
                        tvLeagueName.text = tournament.simpleName
                        ivLeagueIcon.imageTintList = context?.let {
                            getColorStateList(it, R.color.selector_league_tab_tint)
                        }
                    }

                    root.setBackgroundResource(R.drawable.selector_league_tab_bg)
                }

                tab.customView = tabBinding.root
                tab.view.setPadding(
                    0,
                    0,
                    10f.dp2px,
                    0
                )
//                tab.view.setOnClickListener {
//                    //傳聯賽id索取賽事列表資料更新列表
//                }
            }.attach()
        }

    }

}