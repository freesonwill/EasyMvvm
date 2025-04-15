package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.bet.ui.fragment.FloatingButtonFragment
import arch.cayenne.module.home.data.PlayType
import arch.cayenne.module.home.databinding.FragmentNewHomeBinding
import arch.cayenne.module.home.enums.HomeTab
import arch.cayenne.module.home.enums.SportType
import arch.cayenne.module.home.ui.adapter.HomePagerAdapter
import arch.cayenne.module.home.ui.adapter.SportsListAdapter
import com.google.android.material.tabs.TabLayoutMediator
import arch.cayenne.module.home.viewmodel.HomeViewModel
import kotlin.reflect.KClass

class NewHomeFragment : BaseFragment<HomeViewModel, FragmentNewHomeBinding>() {
    override val vbClass: KClass<FragmentNewHomeBinding> = FragmentNewHomeBinding::class
    override val vmClass: KClass<HomeViewModel> = HomeViewModel::class
    // TODO viewmodel待實作, 串接資料後再依據mvvm架構重構
    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.setCurrentPlayType(PlayType.Today)
        childFragmentManager.beginTransaction()
            .replace(mBinding.floatingContainer.id, FloatingButtonFragment())
            .commit()
        with(mBinding) {
            vpHome.apply {
                adapter = HomePagerAdapter(childFragmentManager, lifecycle, HomeTab.entries)
                isUserInputEnabled = false
                currentItem = 0
            }

            TabLayoutMediator(tlHome, vpHome) { tab, position ->
                val tabView = tab.view
                tab.text = HomeTab.entries[position].getTitle(this@NewHomeFragment.requireContext())
                tabView.setPadding(11.dp2px, 0, 11.dp2px, 0)
            }.attach()


            val apiSportIds = listOf(1, 2, 3, 4, 5) // TODO mock API 回傳的運動 ID
            val sportsList = apiSportIds.mapNotNull { SportType.fromId(it) }

            rvSportsList.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = SportsListAdapter(sportsList) { sport ->
                    Toast.makeText(
                        requireContext(),
                        "選擇：${getString(sport.titleResId)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun initListener() {
        with(mBinding) {
            llWalletEntry.setOnClickListener {

            }

            llFavoriteEntry.setOnClickListener {

            }

            llSearchEntry.setOnClickListener {

            }

            llBetEntry.setOnClickListener {

            }
        }
    }

    override fun createObserver() {
        mViewModel.sportsStatistical.observe(this) {
            //TODO sport那一塊的UI
            if (mViewModel.currentSport == null) {
                mViewModel.setCurrentSport(it[0].sportId)
            }
            mViewModel.getCurrentTournament()
        }
        mViewModel.tournaments.observe(this) {
            //TODO 聯賽那一塊的UI
            if (!mViewModel.currentTournament.containsKey(mViewModel.currentSport)) {
                mViewModel.setCurrentTournament(mViewModel.currentSport!!, it[0].tournamentId)
            }
            mViewModel.getCurrentMatch()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.vpHome.adapter = null
    }
}