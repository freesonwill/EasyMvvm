package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.MsgType
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.betslip.databinding.FragmentGameBonusBinding
import arch.cayenne.module.order.data.model.RecordsBean
import arch.cayenne.module.order.ui.adapter.OrderGameAdapter
import arch.cayenne.module.order.ui.viewmodel.ChatChooseViewModel
import arch.cayenne.module.order.ui.viewmodel.GameBonusViewModel
import kotlin.reflect.KClass

/**
 * 投注记录-游戏-奖金列表页
 */

class GameBonusFragment : BaseFragment<GameBonusViewModel, FragmentGameBonusBinding>() {

    override val vbClass: KClass<FragmentGameBonusBinding> = FragmentGameBonusBinding::class
    override val vmClass: KClass<GameBonusViewModel> = GameBonusViewModel::class
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
                 chooseViModel?.clickBtn(MsgType.BET_GAME)
             }

         })
    }

    override fun initData() {
        mViewModel.getGameList()
        //mViewModel.getMaxBonusList()
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