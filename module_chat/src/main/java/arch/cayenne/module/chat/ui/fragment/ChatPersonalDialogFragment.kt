package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.chat.databinding.FragmentChatPersonalLayoutBinding
import arch.cayenne.module.chat.ui.viewmodel.ChatPersonalDialogViewModel
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 4/11/25 14:36
 * @description:
 */
class ChatPersonalDialogFragment :
    BasePreLoadBottomSheetFragment<ChatPersonalDialogViewModel, FragmentChatPersonalLayoutBinding>() {

    companion object {
        val TAG = ChatPersonalDialogFragment::class.java.simpleName

        fun create(fragment: Fragment) {
            val manager = fragment.childFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                ChatPersonalDialogFragment().customAttach(fragment, TAG)
            }
        }

        fun show(fragment: Fragment) {
            val f =
                fragment.childFragmentManager.findFragmentByTag(TAG) as? ChatPersonalDialogFragment
            f?.customShow()
        }

    }

    override val vbClass: KClass<FragmentChatPersonalLayoutBinding>
        get() = FragmentChatPersonalLayoutBinding::class
    override val vmClass: KClass<ChatPersonalDialogViewModel>
        get() = ChatPersonalDialogViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        ChatReportFragment.create(this)
        mBinding.root.minimumHeight = 320.dp2px
    }

    override fun initListener() {
        val clickListener = View.OnClickListener {
            dismissNow()
        }
        mBinding.apply {
            tvReport.setOnClickListener {
                ChatReportFragment.show(this@ChatPersonalDialogFragment)
            }
            tvAt.setOnClickListener(clickListener)
            tvCopy.setOnClickListener(clickListener)
            tvCancel.setOnClickListener { dismissNow() }
        }
    }

}