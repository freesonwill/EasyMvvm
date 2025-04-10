package com.walisport.module.bet.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.walisport.lib.base.ui.BaseBottomSheetFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.bet.R
import com.walisport.module.bet.databinding.FragmentBetSheetBinding
import com.walisport.module.bet.repo.BetSheetRepository
import org.koin.java.KoinJavaComponent.inject

class BetSheetFragment private constructor(): BaseBottomSheetFragment<FragmentBetSheetBinding>() {

    companion object {
        private const val MATCH_ID = "matchId"
        const val RESULT_KEY = "result_key"
        const val DISMISS_KEY = "dismiss_key"

        fun newInstance(matchId: Int? = null): BetSheetFragment {
            val b = Bundle().apply {
                putInt(MATCH_ID, matchId ?: -1)
            }
            return BetSheetFragment().apply {
                arguments = b
            }
        }

        // TODO 此為測試用！！之後會刪除  此為測試用！！之後會刪除  此為測試用！！之後會刪除
        fun addMockData() {
            val repo: BetSheetRepository by inject(BetSheetRepository::class.java)
            repo.addMockData()
        }
    }

    private val dismissObserver = Observer<String> { value ->
        if (value == DISMISS_KEY) {
            dismiss()
        }
    }
    private var lastLiveData: LiveData<String>? = null

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

    override fun createObserver() {
        // navigation的fragment沒有收起彈窗方法，必須靠回調頂層bottom sheet收起彈窗
        val navController = NavHostFragment.findNavController(mBinding.mainNav.getFragment())
        navController.addOnDestinationChangedListener { _, destination, _ ->
            removeLastObserver()
            handleDismissObserve(navController, destination.id)
        }
    }

    private fun removeLastObserver() {
        lastLiveData?.removeObserver(dismissObserver)
        lastLiveData = null
    }

    private fun handleDismissObserve(navController: NavController, destinationId: Int) {
        val backStackEntry = navController.getBackStackEntry(destinationId)

        lastLiveData = backStackEntry.savedStateHandle.getLiveData<String>(RESULT_KEY).apply {
            observe(viewLifecycleOwner, dismissObserver)
        }
    }
}

interface BetSheetListener {
    fun dismiss(key: String = BetSheetFragment.RESULT_KEY, value: String = BetSheetFragment.DISMISS_KEY)
}