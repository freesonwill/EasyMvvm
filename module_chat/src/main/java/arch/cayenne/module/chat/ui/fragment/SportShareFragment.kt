package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.chat.databinding.FragmentSportShareLayoutBinding
import arch.cayenne.module.chat.ui.viewmodel.BetShareViewModel
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 8/12/25 19:50
 * @description: 体育注单分享
 */
class SportShareFragment:BaseFragment<BetShareViewModel,FragmentSportShareLayoutBinding>() {
    override val vbClass: KClass<FragmentSportShareLayoutBinding>
        get() = FragmentSportShareLayoutBinding::class
    override val vmClass: KClass<BetShareViewModel>
        get() = BetShareViewModel::class
    private val betShareModel:BetShareViewModel by sharedViewModel<BetShareViewModel,BetShareDialogFragment>()

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.apply {
            ivExpand.setOnClickListener {
                betShareModel.expandDialog()
            }
            ivClose.clickNoRepeat {
                betShareModel.closeDialog()
            }
        }
    }

    override suspend fun createObserver() {
    }

}