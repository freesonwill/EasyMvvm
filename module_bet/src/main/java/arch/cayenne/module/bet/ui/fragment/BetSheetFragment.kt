package arch.cayenne.module.bet.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import arch.cayenne.lib.base.ui.BaseBottomSheetFragment
import arch.cayenne.lib.base.ui.viewBind
import arch.cayenne.module.bet.Config.KEY_RESULT
import arch.cayenne.module.bet.Config.VALUE_DISMISS
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentBetSheetBinding
import arch.cayenne.module.bet.repo.SingleBetRepository
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.java.KoinJavaComponent.inject

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

        // TODO 此為測試用！！之後會刪除  此為測試用！！之後會刪除  此為測試用！！之後會刪除
        fun addMockData() {
            val repo: SingleBetRepository by inject(SingleBetRepository::class.java)
            repo.addMockData()
        }
    }

    private val dismissObserver = Observer<String> { value ->
        if (value == VALUE_DISMISS) {
            dismiss()
        }
    }

    private var lastLiveData: LiveData<String>? = null

    override val mBinding: FragmentBetSheetBinding by viewBind()

    override fun initView(savedInstanceState: Bundle?) {
        isCancelable = false
    }

    override fun initListener() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStartDestination()
        setFitToContents()
    }

    private fun setFitToContents() {
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
        bottomSheet?.let { sheet ->
            val behavior = BottomSheetBehavior.from(sheet)

            behavior.saveFlags = BottomSheetBehavior.SAVE_ALL
            behavior.isFitToContents = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
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

        lastLiveData = backStackEntry.savedStateHandle.getLiveData<String>(KEY_RESULT).apply {
            observe(viewLifecycleOwner, dismissObserver)
        }
    }

    override fun superDismiss() {
        parentFragmentManager.setFragmentResult(KEY_RESULT, Bundle().apply {
            putString(VALUE_DISMISS, VALUE_DISMISS)
        })
        super.superDismiss()

    }
}

interface BetSheetListener {
    fun dismiss(key: String = KEY_RESULT, value: String = VALUE_DISMISS)
}