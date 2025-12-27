package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
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

    companion object {
        val TAG = ChatPrivateUserFragment::class.java.simpleName

        fun create(fragment: Fragment) {
            val manager = fragment.childFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                ChatPrivateUserFragment().customAttach(fragment, TAG)
            }
        }

        fun show(fragment: Fragment) {
            val f =
                fragment.childFragmentManager.findFragmentByTag(TAG) as? ChatPrivateUserFragment
            f?.customShow()
        }

    }


    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }
}