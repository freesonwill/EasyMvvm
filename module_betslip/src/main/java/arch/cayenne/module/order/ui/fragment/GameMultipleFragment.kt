package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.databinding.FragmentGameMultipleBinding
import arch.cayenne.module.order.ui.adapter.OrderGameAdapter
import arch.cayenne.module.order.ui.viewmodel.GameMultipleViewModel
import kotlin.reflect.KClass

/**
 * 投注记录-游戏-最大倍数列表页
 */

class GameMultipleFragment : BaseFragment<GameMultipleViewModel, FragmentGameMultipleBinding>() {

    override val vbClass: KClass<FragmentGameMultipleBinding> = FragmentGameMultipleBinding::class
    override val vmClass: KClass<GameMultipleViewModel> = GameMultipleViewModel::class
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