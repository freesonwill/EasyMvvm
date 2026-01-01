package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.ChatMsgType
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.betslip.databinding.FragmentGameOrderAllBinding
import arch.cayenne.module.order.data.model.ChooseBetData
import arch.cayenne.module.order.data.model.OrderAllBean
import arch.cayenne.module.order.ui.adapter.AllGameAdapter
import arch.cayenne.module.order.ui.viewmodel.ChatChooseViewModel
import arch.cayenne.module.order.ui.viewmodel.GameAllViewModel
import kotlin.reflect.KClass

/**
 * 投注记录-游戏-全部游戏列表页
 */

class GameOrderAllFragment : BaseFragment<GameAllViewModel, FragmentGameOrderAllBinding>() {

    override val vbClass: KClass<FragmentGameOrderAllBinding> = FragmentGameOrderAllBinding::class
    override val vmClass: KClass<GameAllViewModel> = GameAllViewModel::class
    private val gameAdapter by lazy { AllGameAdapter() }
    private var chooseViModel: ChatChooseViewModel? = null

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.rvContent.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = gameAdapter
        }
        gameAdapter.setOnItemClickListener(object :RecyclerItemListener<OrderAllBean>{
            override fun onItemClick(item: OrderAllBean?, position: Int) {
                chooseViModel?.clickBtn(ChooseBetData(ChatMsgType.BET_GAME,item?.bet?:""))
            }
        })
        checkChooseFragment()
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

    private fun checkChooseFragment(){
        if(parentFragment?.parentFragment is ChatChooseBetFragment){
            chooseViModel = sharedViewModel<ChatChooseViewModel, ChatChooseBetFragment>().value
        }
    }
}