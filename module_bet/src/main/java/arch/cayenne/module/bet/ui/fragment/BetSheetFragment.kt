package arch.cayenne.module.bet.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_DISMISS
import arch.cayenne.module.bet.databinding.FragmentBetSheetBinding
import arch.cayenne.module.bet.viewmodel.BetSheetViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class BetSheetFragment private constructor(): BaseBottomSheetFragment<BetSheetViewModel, FragmentBetSheetBinding>() {

    companion object {

        /***
         * 調起投注彈窗
         * 調起前需先將注單加入到資料庫 (BetBean)
         */
        fun newInstance(): BetSheetFragment {
            return BetSheetFragment()
        }
    }

    override val vbClass: KClass<FragmentBetSheetBinding>
        get() = FragmentBetSheetBinding::class
    override val vmClass: KClass<BetSheetViewModel>
        get() = BetSheetViewModel::class

    private val dismissObserver = Observer<String> { value ->
        if (value == VALUE_DISMISS) {
            dismiss()
        }
    }

    private var lastLiveData: LiveData<String>? = null


    override fun initView(savedInstanceState: Bundle?) {
        isCancelable = false
    }

    override fun initListener() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            mViewModel.getBetType()?.let { type ->
                setStartDestination(type)
                setFitToContents()
            }
        }

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

    private fun setStartDestination(type: BetTypeEnum) {
        val navController = NavHostFragment.findNavController(mBinding.mainNav.getFragment())
        val navGraph = navController.navInflater.inflate(R.navigation.nav_bet)

        if (type == BetTypeEnum.COMBO) {
            navGraph.setStartDestination(R.id.comboBetFragment)
        } else {
            navGraph.setStartDestination(R.id.singleBetFragment)
        }
        navController.setGraph(navGraph, Bundle())
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