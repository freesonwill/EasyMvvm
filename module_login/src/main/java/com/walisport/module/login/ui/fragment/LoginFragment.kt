package com.walisport.module.login.ui.fragment

import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.login.R
import com.walisport.module.login.databinding.FragmentLoginBinding
import com.walisport.module.login.ui.dialog.PicVerifyDialog
import kotlin.reflect.KClass

/**
 * @author: zhangsan
 * @date: 2025/3/26 12:02
 * @description:
 */
class LoginFragment : BaseFragment<EmptyViewModel, FragmentLoginBinding>() {
    override val vbClass: KClass<FragmentLoginBinding> = FragmentLoginBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    private val args: LoginFragmentArgs by navArgs()

    override fun initView(savedInstanceState: Bundle?) {
        //val userId = arguments?.getString("userId")
        val userId = args.userId
        "LoginFragment--->$userId,arguments:$arguments,args:$args".logd(TAG)
        mBinding.etUsername.setText(userId)
        //val userId = arguments?.getString("userId")
        val statusBarHeight =
            ViewCompat.getRootWindowInsets(requireView())
                ?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
        val params = mBinding.ivLoginTitle.layoutParams as ViewGroup.MarginLayoutParams
        // 設定 topMargin
        params.topMargin = statusBarHeight + 77.dp2px
        // 將修改後的 LayoutParams 重新應用到 maskView
        mBinding.ivLoginTitle.layoutParams = params
    }

    override fun initListener() {
        with (mBinding) {
            btnLogin.clickNoRepeat {
                findNavController().navigateUp()
            }
            tvRegister.clickNoRepeat {
                navigate(R.id.registerNewAccountFragment)
//                navigate(R.id.registerFragment)
            }
            tvGuest.clickNoRepeat {
//                findNavController().navigateUp()
                navigate(R.id.fastLoginFragment)
            }
            tvForgotPassword.clickNoRepeat {
//                navigate(R.id.identifyVerifyFragment)
                showPicVerifyDialog()
            }
            tvVerify.clickNoRepeat {
//               navigate(R.id.identifyVerifyFragment)
                navigate(R.id.newMobileVerifyFragment)
            }
            tvSetNewPassword.clickNoRepeat {
                navigate(R.id.setNewPasswordFragment)
            }
            tvRegisterFormalAccount.clickNoRepeat {
                navigate(R.id.registerFormalAccountFragment)
            }
        }
    }

    override suspend fun createObserver() {
    }
    override fun onStart() {
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig,mBinding.ivLogo)
        super.onStart()
    }
    private fun showConfirmDialog(id: Long) {
        CommonDialog.newInstance(
            "",
            getString(R.string.confirm_logout_title),
            getString(R.string.confirm_logout),
            getString(R.string.cancel_logout)
        ).also {
            it.setOnOkClickListener {

            }
            it.show(childFragmentManager)
        }
    }
    private fun showPicVerifyDialog(){
        PicVerifyDialog.newInstance().show(childFragmentManager)
    }
}