package com.walisport.module.bet.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import com.walisport.lib.base.ui.BaseBottomSheetFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.bet.R
import com.walisport.module.bet.databinding.FragmentBetSheetBinding

class BetSheetFragment private constructor(): BaseBottomSheetFragment<FragmentBetSheetBinding>() {

    companion object {
        private const val MATCH_ID = "matchId"
        fun newInstance(matchId: Int? = null): BetSheetFragment {
            val b = Bundle().apply {
                putInt(MATCH_ID, matchId ?: -1)
            }
            return BetSheetFragment().apply {
                arguments = b
            }
        }
    }

    override val mBinding: FragmentBetSheetBinding by viewBind()

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStartDestination()
    }

    private fun setStartDestination() {
        val navController = NavHostFragment.findNavController(mBinding.mainNav.getFragment())
        val navGraph = navController.navInflater.inflate(R.navigation.nav_bet)

        val bundle = requireArguments()

        val matchId = requireArguments().getInt(MATCH_ID)
        if (matchId == -1) {
            navGraph.setStartDestination(R.id.comboBetFragment)
        } else {
            navGraph.setStartDestination(R.id.singleBetFragment)
        }
        navController.setGraph(navGraph, bundle)
    }
}