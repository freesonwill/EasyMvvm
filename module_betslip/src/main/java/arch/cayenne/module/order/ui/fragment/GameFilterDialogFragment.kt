package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.databinding.FragmentGameFilterBinding
import arch.cayenne.module.betslip.ui.adapter.SportPickerAdapter
import arch.cayenne.module.order.ui.viewmodel.GameFilterViewModel
import kotlin.reflect.KClass

/**
 * 游戏选择筛选项弹窗
 */

class GameFilterDialogFragment : BaseBottomSheetFragment<GameFilterViewModel, FragmentGameFilterBinding>() {

    override val vbClass: KClass<FragmentGameFilterBinding> = FragmentGameFilterBinding::class
    override val vmClass: KClass<GameFilterViewModel> = GameFilterViewModel::class

    companion object {
        private const val KEY_SELECTED_GAME_ID = "key_selected_game_id"
        fun newInstance(gameIds: List<Int> = emptyList()): GameFilterDialogFragment {
            return GameFilterDialogFragment().apply {
                if (gameIds.isNotEmpty()) {
                    val bundle = Bundle()
                    bundle.putIntArray(KEY_SELECTED_GAME_ID, gameIds.toIntArray())
                    arguments = bundle
                }
            }
        }
    }

    private val gameAdapter: SportPickerAdapter by lazy {
        SportPickerAdapter(object : SportPickerAdapter.SportPickerListener {
            override fun onSportSelected(id: Int) {
                mViewModel.setSelectedById(id)
            }
        })
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.rvContent.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = gameAdapter
        }
    }

    override fun initListener() {
        mBinding.btnReset.setOnClickListener {
            mViewModel.reset()
            sendResult()
        }
        mBinding.btnConfirm.setOnClickListener {
            sendResult()
        }
    }

    override suspend fun createObserver() {
        mViewModel.onSportListener.observe(viewLifecycleOwner) {
            gameAdapter.submitList(it)
        }
    }

    private fun sendResult() {
        //val bean = mViewModel.getSelectedSportBean()
        parentFragmentManager.setFragmentResult(Config.KEY_RESULT, Bundle().apply {
            //putIntArray(Config.VALUE_SELECTED_SPORT_ID, bean.map { it.sportId }.toIntArray())
        })
        dismiss()
    }
}