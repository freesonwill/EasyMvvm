package com.walisport.module.login.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.login.R
import com.walisport.module.login.databinding.FragmentFastLoginBinding
import kotlin.reflect.KClass

/**
 * @author: ricky.chang
 * @date: 2025/7/21 下午3:10
 * @description:
 */
class FastLoginFragment : BaseFragment<EmptyViewModel, FragmentFastLoginBinding>() {
    override val vbClass: KClass<FragmentFastLoginBinding> = FragmentFastLoginBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
       with (mBinding) {
           titleBar.loadGeneralTitleBar("", {
               findNavController().navigateUp()
           })
           btnQuickLogin.clickNoRepeat {
               // 在你的 Fragment 內部
               val navController = findNavController()
               // 獲取當前導覽圖的 startDestination ID
               val startDestinationId = navController.graph.startDestinationId
               // 彈出後退堆疊，直到 startDestinationId。
               // inclusive = false 表示不包含 startDestinationId 本身，也就是停在它上面。
               navController.popBackStack(startDestinationId, false)
           }
       }
    }

    override suspend fun createObserver() {

    }
}