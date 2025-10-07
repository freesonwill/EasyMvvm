package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.chat.databinding.FragmentMainChatLayoutBinding
import arch.cayenne.module.chat.databinding.ItemChatTablayoutLayoutBinding
import arch.cayenne.module.chat.ui.viewmodel.MainChatViewModel
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 6/10/25 17:46
 * @description: 首页聊天室
 */
class MainChatFragment:BaseFragment<MainChatViewModel,FragmentMainChatLayoutBinding>() {
    override val vbClass: KClass<FragmentMainChatLayoutBinding>
        get() = FragmentMainChatLayoutBinding::class
    override val vmClass: KClass<MainChatViewModel>
        get() = MainChatViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }

    private fun initTabLayout(){

        val view = ItemChatTablayoutLayoutBinding.inflate(LayoutInflater.from(requireContext()),mBinding.root,false)
      mBinding.tablayout.apply {

      }
    }
}