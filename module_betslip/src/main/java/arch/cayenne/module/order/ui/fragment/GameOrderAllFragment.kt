package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.databinding.FragmentGameOrderAllBinding
import arch.cayenne.module.order.ui.adapter.AllGameAdapter
import arch.cayenne.module.order.ui.viewmodel.GameAllViewModel
import kotlin.reflect.KClass

/**
 * 投注记录-游戏-全部游戏列表页
 */

class GameOrderAllFragment : BaseFragment<GameAllViewModel, FragmentGameOrderAllBinding>() {

    override val vbClass: KClass<FragmentGameOrderAllBinding> = FragmentGameOrderAllBinding::class
    override val vmClass: KClass<GameAllViewModel> = GameAllViewModel::class
    private val gameAdapter by lazy { AllGameAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.rvContent.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = gameAdapter
        }
        gameAdapter.setOnItemClickListener { _ ->

        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getAllGameList()
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
        mViewModel.recordData.observe(viewLifecycleOwner) {
            if (it != null) {
                gameAdapter.submitList(it)
            }
        }
    }
}