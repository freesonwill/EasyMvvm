package com.walisport.module.login.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.login.databinding.FragmentRegisterNewAccountBinding
import kotlin.reflect.KClass

/**
 * @author: ricky.chang
 * @date: 2025/8/11 下午3:48
 * @description:
 */
class RegisterNewAccountFragment: BaseFragment<EmptyViewModel, FragmentRegisterNewAccountBinding>() {
    override val vbClass: KClass<FragmentRegisterNewAccountBinding> = FragmentRegisterNewAccountBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
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
            etSettingAccount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0.isNullOrEmpty()) {
                        btnNext.isEnabled = false
                    } else {
                        btnNext.isEnabled = isUsernameValid(p0.toString())
                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
            etPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0.isNullOrEmpty()) {
                        btnNext.isEnabled = false
                    } else {
                        btnNext.isEnabled = isUsernameValid(p0.toString())
                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
            etConfirmPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0.isNullOrEmpty()) {
                        btnNext.isEnabled = false
                    } else {
                        btnNext.isEnabled = isUsernameValid(p0.toString())
                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
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
    /**
     * 長度: 4 到 12 位。
     * 內容: 字母和數字的組合。
     * 首位: 必須是字母。
     * */
    fun isUsernameValid(username: String): Boolean {
        val pattern = "^(?=.*\\d)[a-zA-Z][a-zA-Z0-9]{4,12}$"
        return username.matches(Regex(pattern))
    }
    /**
     * 密碼長度: 7 到 12 位。
     * 必須包含至少一個字母和一個數字。
     * */
    fun isPasswordValid(password: String): Boolean {
        val pattern = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{7,12}$"
        return password.matches(Regex(pattern))
    }
}