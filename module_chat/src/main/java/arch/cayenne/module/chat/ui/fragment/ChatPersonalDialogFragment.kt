package arch.cayenne.module.chat.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.websocket.chat.data.ChatRefUser
import arch.cayenne.lib.websocket.chat.data.ChatType
import arch.cayenne.module.chat.data.model.ChatMsgPageBean
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
        val CHAT_PERSONAL_REQUEST = "chat_personal_request"
        val CHAT_PERSONAL_RESULT = "chat_personal_result"

        fun create(fragment: Fragment) {
            val manager = fragment.childFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                ChatPersonalDialogFragment().customAttach(fragment, TAG)
            }
        }

        fun show(fragment: Fragment, bean: ChatMsgPageBean,chatType: ChatType) {
            val f =
                fragment.childFragmentManager.findFragmentByTag(TAG) as? ChatPersonalDialogFragment
            f?.setChatArguments(bean,chatType)
            f?.customShow()
        }

    }

    override val vbClass: KClass<FragmentChatPersonalLayoutBinding>
        get() = FragmentChatPersonalLayoutBinding::class
    override val vmClass: KClass<ChatPersonalDialogViewModel>
        get() = ChatPersonalDialogViewModel::class
    private var msgBean: ChatMsgPageBean? = null
    private var chatTYpe: ChatType = ChatType.LOBBY

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            setupFullScreen(dialog)
        }
        return dialog
    }

    fun setChatArguments(bean: ChatMsgPageBean, chatType: ChatType) {
        this.msgBean = bean
        this.chatTYpe = chatType
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
                setFragmentResult(position)
                if (position == 3) {
                    ChatReportFragment.show(
                        this@ChatPersonalDialogFragment,
                        msgBean?.toChatRefUsers(),
                        chatTYpe
                    )
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

    private fun setFragmentResult(data: Int) {
        val bundle = Bundle().apply {
            putInt(CHAT_PERSONAL_RESULT, data)
        }
        parentFragmentManager.setFragmentResult(CHAT_PERSONAL_REQUEST, bundle)
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