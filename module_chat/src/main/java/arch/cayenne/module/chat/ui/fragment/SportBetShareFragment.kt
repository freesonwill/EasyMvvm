package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.databinding.FragmentSportShareLayoutBinding
import arch.cayenne.module.chat.ui.adapter.SportBetShareAdapter
import arch.cayenne.module.chat.ui.viewmodel.BetShareViewModel
import arch.cayenne.module.chat.ui.viewmodel.SportBetShareViewModel
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 8/12/25 19:50
 * @description: 体育注单分享
 */
class SportBetShareFragment:BaseFragment<SportBetShareViewModel,FragmentSportShareLayoutBinding>() {
    override val vbClass: KClass<FragmentSportShareLayoutBinding>
        get() = FragmentSportShareLayoutBinding::class
    override val vmClass: KClass<SportBetShareViewModel>
        get() = SportBetShareViewModel::class
    private val betShareModel:BetShareViewModel by sharedViewModel<BetShareViewModel,BetShareDialogFragment>()

    override fun initView(savedInstanceState: Bundle?) {
        val betAdapter = SportBetShareAdapter()
        mBinding.recycler.apply {
            adapter = betAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.setOrderData(OrderSportPageEnum.UNSETTLED)
    }

    override fun initListener() {
        mBinding.apply {
            iconExpand.setOnClickListener {
                ivExpand.setImageResource(if (mViewModel.isExpand) R.drawable.icon_chat_expand else R.drawable.icon_chat_fold)
                betShareModel.expandDialog()
                mViewModel.isExpand = !mViewModel.isExpand
            }
            iconClose.clickNoRepeat {
                betShareModel.closeDialog()
            }

        }
    }

    override suspend fun createObserver() {
        mViewModel.orderDataListener.observe(viewLifecycleOwner){
            (mBinding.recycler.adapter as? SportBetShareAdapter)?.submitList(it)
        }
    }

    private fun checkButton(){
        //比赛进行中 显示为跟单 比赛结束 显示前往体育
    }

}