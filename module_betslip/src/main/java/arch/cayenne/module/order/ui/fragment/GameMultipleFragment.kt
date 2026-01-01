package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.ChatMsgType
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.betslip.databinding.FragmentGameMultipleBinding
import arch.cayenne.module.order.data.model.ChooseBetData
import arch.cayenne.module.order.data.model.RecordsBean
import arch.cayenne.module.order.ui.adapter.OrderGameAdapter
import arch.cayenne.module.order.ui.viewmodel.ChatChooseViewModel
import arch.cayenne.module.order.ui.viewmodel.GameMultipleViewModel
import kotlin.reflect.KClass

/**
 * 投注记录-游戏-最大倍数列表页
 */

class GameMultipleFragment : BaseFragment<GameMultipleViewModel, FragmentGameMultipleBinding>() {

    override val vbClass: KClass<FragmentGameMultipleBinding> = FragmentGameMultipleBinding::class
    override val vmClass: KClass<GameMultipleViewModel> = GameMultipleViewModel::class
    private val gameAdapter by lazy { OrderGameAdapter() }
    private var chooseViModel: ChatChooseViewModel? = null

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.rvContent.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = gameAdapter
        }
        checkChooseFragment()
    }

    override fun initListener() {
        gameAdapter.setItemClickListener(object :RecyclerItemListener<RecordsBean>{
            override fun onItemClick(item: RecordsBean?, position: Int) {
                chooseViModel?.clickBtn(ChooseBetData(ChatMsgType.BET_GAME,item?.bet?:""))
            }
        })
    }

    override fun initData() {
        mViewModel.getGameList()
        //mViewModel.getMaxMultipleList()
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