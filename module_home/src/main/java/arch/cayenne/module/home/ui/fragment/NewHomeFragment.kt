package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.module.bet.ui.fragment.FloatingButtonFragment
import arch.cayenne.module.home.databinding.FragmentNewHomeBinding
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.enums.SportType
import arch.cayenne.module.home.ui.adapter.HomePagerAdapter
import arch.cayenne.module.home.ui.adapter.SportsListAdapter
import arch.cayenne.module.home.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.KClass

class NewHomeFragment : BaseFragment<HomeViewModel, FragmentNewHomeBinding>() {
    override val vbClass: KClass<FragmentNewHomeBinding> = FragmentNewHomeBinding::class
    override val vmClass: KClass<HomeViewModel> = HomeViewModel::class

    private val sportsListAdapter by lazy {
        SportsListAdapter { sport ->
            Toast.makeText(
                requireContext(),
                "選擇：${getString(SportType.fromId(sport.id)!!.titleResId)}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {

        childFragmentManager.beginTransaction()
            .replace(mBinding.floatingContainer.id, FloatingButtonFragment())
            .commit()
        with(mBinding) {
            vpHome.apply {
                adapter = HomePagerAdapter(childFragmentManager, lifecycle, PlayType.entries)
                isUserInputEnabled = false
                currentItem = 0
            }

            TabLayoutMediator(tlHome, vpHome) { tab, position ->
                val tabView = tab.view
                tab.text = PlayType.entries[position].getTitle(this@NewHomeFragment.requireContext())
                tabView.setPadding(11.dp2px, 0, 11.dp2px, 0)
            }.attach()
            vpHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mViewModel.setCurrentPlayType(PlayType.entries[position])
                }
            })

            rvSportsList.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = sportsListAdapter
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
        mViewModel.sportsStatistical.observe(viewLifecycleOwner) {
            sportsListAdapter.setData(it)
            sportsListAdapter.notifyItemRangeChanged(0,it.size-1)
            if (mViewModel.currentSportChange.value == null) {
                mViewModel.setCurrentSport(it[0].id)
            }
//            mViewModel.getCurrentTournament()
        }

        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            mBinding.tvWalletBalance.text = it.getFormalMoney()
        }

//        mViewModel.tournaments.observe(this) {
//            //TODO 聯賽那一塊的UI
//            if (!mViewModel.currentTournament.containsKey(mViewModel.currentSport)) {
//                mViewModel.setCurrentTournament(mViewModel.currentSport!!, it[0].tournamentId)
//            }
//            mViewModel.getCurrentMatch()
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.vpHome.adapter = null
    }
}