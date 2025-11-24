package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.databinding.FragmentGameBonusBinding
import arch.cayenne.module.order.ui.adapter.OrderGameAdapter
import arch.cayenne.module.order.ui.viewmodel.GameBonusViewModel
import kotlin.reflect.KClass

/**
 * 投注记录-游戏-奖金列表页
 */

class GameBonusFragment: BaseFragment<GameBonusViewModel, FragmentGameBonusBinding>() {

    override val vbClass: KClass<FragmentGameBonusBinding> = FragmentGameBonusBinding::class
    override val vmClass: KClass<GameBonusViewModel> = GameBonusViewModel::class
    private val gameAdapter by lazy { OrderGameAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.rvContent.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = gameAdapter
        }
    }

    override fun initListener() {

    }

    override fun initData() {
        mViewModel.getGameList()
    }

    override suspend fun createObserver() {
        mViewModel.recordData.observe(viewLifecycleOwner) {
            if (it != null) {
                gameAdapter.submitList(it)
            }
        }
    }
}