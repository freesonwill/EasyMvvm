package arch.cayenne.module.chat.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.chat.data.model.ChatPersonalData
import arch.cayenne.module.chat.databinding.FragmentChatReportLayoutBinding
import arch.cayenne.module.chat.ui.adapter.ChatPersonalAdapter
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            setupFullScreen(dialog)
        }

        return dialog
    }

    private fun setupFullScreen(dialog: Dialog) {
        dialog.window?.apply {
            // 设置窗口标志
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            dialog.window?.attributes?.height = 432.dp2px
//
//            // 设置透明背景
//            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.minHeight = 432.dp2px
        mBinding.recycler.apply {
            val nAdapter = ChatPersonalAdapter()
            nAdapter.setRecyclerItemListener(object : RecyclerItemListener<ChatPersonalData> {
                override fun onItemClick(item: ChatPersonalData?, position: Int) {
                    dismiss()
                }
            })
            val list = arrayListOf(
                ChatPersonalData("色情低俗"),
                ChatPersonalData("非法广告"),
                ChatPersonalData("辱骂我或他人"),
                ChatPersonalData("违法违规"),
                ChatPersonalData("涉嫌诈骗"),
                ChatPersonalData("其他"),
            )
            nAdapter.submitList(list)
            mBinding.recycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = nAdapter
            }

        }
    }

    override fun initListener() {

        mBinding.apply {

            tvCancel.setOnClickListener {
                dismiss()
            }
        }

    }
}