package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.chat.databinding.FragmentChatReportLayoutBinding
import arch.cayenne.module.chat.ui.viewmodel.ChatReportViewModel
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 4/11/25 14:37
 * @description:
 */
class ChatReportFragment :
    BasePreLoadBottomSheetFragment<ChatReportViewModel, FragmentChatReportLayoutBinding>() {

    companion object {
        val TAG = ChatReportFragment::class.java.simpleName

        fun create(fragment: Fragment) {
            val manager = fragment.childFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                ChatReportFragment().customAttach(fragment, TAG)
            }
        }

        fun show(fragment: Fragment) {
            val f = fragment.childFragmentManager.findFragmentByTag(TAG) as? ChatReportFragment
            f?.customShow()
        }


    }

    override val vbClass: KClass<FragmentChatReportLayoutBinding>
        get() = FragmentChatReportLayoutBinding::class
    override val vmClass: KClass<ChatReportViewModel>
        get() = ChatReportViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
          mBinding.root.minimumHeight = 432.dp2px
    }

    override fun initListener() {
        val clickListener = View.OnClickListener {
            dismissNow()
        }
        mBinding.apply {
            tvAd.setOnClickListener(clickListener)
            tvFraud.setOnClickListener(clickListener)
            tvAbuse.setOnClickListener(clickListener)
            tvOther.setOnClickListener(clickListener)
            tvIllegal.setOnClickListener(clickListener)
            tvPorn.setOnClickListener(clickListener)
            tvCancel.setOnClickListener {
                dismissNow()
            }
        }

    }
}