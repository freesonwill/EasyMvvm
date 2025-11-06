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
import arch.cayenne.module.chat.databinding.FragmentChatPersonalLayoutBinding
import arch.cayenne.module.chat.ui.adapter.ChatPersonalAdapter
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
//
//            // 设置透明背景
//            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes.height = 320.dp2px

        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.minHeight = 320.dp2px
        ChatReportFragment.create(this)
        val nAdapter = ChatPersonalAdapter()
        nAdapter.setRecyclerItemListener(object : RecyclerItemListener<ChatPersonalData> {
            override fun onItemClick(item: ChatPersonalData?, position: Int) {
                if (position == 3) {
                    ChatReportFragment.show(this@ChatPersonalDialogFragment)
//                    lifecycleScope.launch {
//                        delay(200)
//                        dismissNow()
//                    }
                    dismiss()
                } else {
                    dismiss()
                }
            }
        })
        val list = arrayListOf(
            ChatPersonalData("范哥的忠实粉丝：大于五个字", 1),
            ChatPersonalData("@Ta"),
            ChatPersonalData("复制评论"),
            ChatPersonalData("举报评论"),
        )
        nAdapter.submitList(list)
        mBinding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = nAdapter
        }

    }

    override fun onStart() {
        super.onStart()

    }

    override fun initListener() {

        mBinding.apply {

            tvCancel.setOnClickListener { dismiss() }
        }
    }

}