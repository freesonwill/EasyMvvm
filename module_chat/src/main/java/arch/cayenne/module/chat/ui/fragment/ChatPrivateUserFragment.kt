package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.base.utils.ext.FragmentExt.setFragmentResult
import arch.cayenne.lib.websocket.chat.data.ChatRefUser
import arch.cayenne.lib.websocket.chat.data.ChatType
import arch.cayenne.module.chat.databinding.FragmentPrivateUserLayoutBinding
import arch.cayenne.module.chat.ui.viewmodel.ChatUserInfoViewModel
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 27/12/25 00:15
 * @description:
 */
class ChatPrivateUserFragment :
    BasePreLoadBottomSheetFragment<ChatUserInfoViewModel, FragmentPrivateUserLayoutBinding>() {
    override val vbClass: KClass<FragmentPrivateUserLayoutBinding>
        get() = FragmentPrivateUserLayoutBinding::class
    override val vmClass: KClass<ChatUserInfoViewModel>
        get() = ChatUserInfoViewModel::class
    var user: ChatRefUser? = null
    var chatTYpe: ChatType = ChatType.LOBBY

    companion object {
        val TAG = ChatPrivateUserFragment::class.java.simpleName

        val CHAT_USER_RESULT = "chat_user_result"

        fun create(fragment: Fragment) {
            val manager = fragment.childFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                ChatPrivateUserFragment().customAttach(fragment, TAG)
            }
        }

        fun show(fragment: Fragment, user: ChatRefUser,chatType: ChatType) {
            val f =
                fragment.childFragmentManager.findFragmentByTag(TAG) as? ChatPrivateUserFragment
            f?.setUserArguments(user,chatType)
            f?.customShow()
        }

    }


    override fun initView(savedInstanceState: Bundle?) {
        ChatReportFragment.create(this)

    }

    fun setUserArguments(chatRefUser: ChatRefUser,chatType: ChatType) {
        this.user = chatRefUser
        this.chatTYpe = chatType
    }

    override fun initListener() {
        mBinding.tvReport.setOnClickListener {
            dismiss()
            ChatReportFragment.show(this@ChatPrivateUserFragment,user,chatTYpe)
        }
        mBinding.tvAt.setOnClickListener {
            setParamToParentFragment()
            dismiss()
        }
    }

    private fun setParamToParentFragment() {
        val bundle = Bundle().apply {
            putParcelable(CHAT_USER_RESULT, user)
        }
        parentFragmentManager.setFragmentResult(
            ChatPersonalDialogFragment.CHAT_PERSONAL_REQUEST,
            bundle
        )
    }

}