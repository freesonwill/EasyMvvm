package com.walisport.module.login.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.navigateUp
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.login.databinding.FragmentCompleteAccountBinding
import com.walisport.module.login.databinding.FragmentRegisterBinding
import kotlin.reflect.KClass

/**
 * @author: ricky.chang
 * @date: 2025/7/25 下午3:52
 * @description:
 */
class CompleteAccountFragment: BaseFragment<EmptyViewModel, FragmentCompleteAccountBinding>() {
    override val vbClass: KClass<FragmentCompleteAccountBinding> = FragmentCompleteAccountBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    private val args: LoginFragmentArgs by navArgs()

    override fun initView(savedInstanceState: Bundle?) {
        //val userId = arguments?.getString("userId")
        with (mBinding) {
            titleBar.loadGeneralTitleBar("", {
                findNavController().navigateUp()
            })
            btnSave.clickNoRepeat {
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

    override fun initListener() {

    }

    override fun createObserver() {
    }
}