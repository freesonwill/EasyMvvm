package com.walisport.module.home.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseFragment
import com.walisport.module.home.data.PlayType
import com.walisport.module.home.databinding.FragmentNewHomeBinding
import com.walisport.module.home.viewmodel.HomeViewModel
import kotlin.reflect.KClass

class NewHomeFragment : BaseFragment<HomeViewModel, FragmentNewHomeBinding>() {
    override val vbClass: KClass<FragmentNewHomeBinding> = FragmentNewHomeBinding::class
    override val vmClass: KClass<HomeViewModel> = HomeViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.setCurrentPlayType(PlayType.Today)
    }

    override fun initListener() {

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
}