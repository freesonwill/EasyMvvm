package arch.cayenne.module.account.ui.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.databinding.TitleBarSimpleBinding
import arch.cayenne.lib.common.utils.EditTextUtils
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.startSafeObjectAnimator
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.account.R
import arch.cayenne.module.account.data.constants.SmsVerifyState
import arch.cayenne.module.account.databinding.FragmentSmsVerifyBinding
import arch.cayenne.module.account.ui.viewmodel.SmsVerifyViewModel
import kotlin.reflect.KClass

class SmsVerifyFragment :
    BaseFragment<SmsVerifyViewModel, FragmentSmsVerifyBinding>() {
    override val vbClass: KClass<FragmentSmsVerifyBinding> =
        FragmentSmsVerifyBinding::class
    override val vmClass: KClass<SmsVerifyViewModel> = SmsVerifyViewModel::class

    private var loadingAnim: ObjectAnimator? = null

    private val smsVerifyFragmentArgs by navArgs<SmsVerifyFragmentArgs>()

    private val titleBarBinding: TitleBarSimpleBinding by lazy {
        TitleBarSimpleBinding.inflate(
            LayoutInflater.from(context),
            mBinding.titleBar,
            false
        )
    }

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadDynamicsTitleBars(titleBarBinding.root)

        titleBarBinding.apply {
            tvTitleName.text = ""
            ivBack.addScaleOnTouchAnimation()
            ivBack.clickNoRepeat {
                findNavController().navigateUp()
            }
        }

        val hint = R.string.sms_verify_hint.getString()
        val spannable = android.text.SpannableString(hint)
        val index = hint.indexOf('6')
        if (index != -1) {
            spannable.setSpan(
                android.text.style.TypefaceSpan("sf_pro_display_bold"),
                index,
                index + 1,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        mBinding.tvSmsVerifyHint.text = spannable

        mBinding.tvPhoneNumber.text =
            "${smsVerifyFragmentArgs.countryCode} ${smsVerifyFragmentArgs.phoneNumber}"

        mBinding.etSmsCode.requestFocus()
        EditTextUtils.showKeyboard(requireContext(), mBinding.etSmsCode)

        mBinding.etSmsCode.setOnTextChangeListener { text, isComplete ->
            if (isComplete) {
                mViewModel.verifySmsCode(
                    smsVerifyFragmentArgs.countryCode,
                    smsVerifyFragmentArgs.phoneNumber,
                    text.toString()
                )
            }
        }

        //左侧错误提示图标
        val drawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.account_verify_failure_warning)
        drawable?.setBounds(0, 0, 15.dp2px, 15.dp2px)
        mBinding.tvVerifyFailure.setCompoundDrawables(drawable, null, null, null)

        mBinding.tvCountDown.isEnabled = false
        mViewModel.startCountDown()
    }

    override fun initData() {
        super.initData()
    }

    override fun initListener() {
        mBinding.tvCountDown.clickNoRepeat {
            //开始倒计时
            mViewModel.requestSMSCode(
                smsVerifyFragmentArgs.countryCode,
                smsVerifyFragmentArgs.phoneNumber
            )
            mViewModel.startCountDown()
        }
    }

    override suspend fun createObserver() {
        mViewModel.getCountDownLiveData().observe(this) { seconds ->
            if (seconds > 0) {
                mBinding.tvCountDown.isEnabled = false
                mBinding.tvCountDown.text =
                    getString(R.string.account_resend_sms_code_count_down, seconds)
            } else {
                mBinding.tvCountDown.isEnabled = true
                mBinding.tvCountDown.text = getString(R.string.account_resend_sms_code)
            }
        }

        mViewModel.apiStateListener.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.NetworkUnavailable -> {
                    showToast(getString(arch.cayenne.lib.common.R.string.error_net))

                    mBinding.ivLoading.visibility = View.GONE
                    loadingAnim?.cancel()
                }

                is DataState.Loading -> {
                    mBinding.tvVerifyFailure.visibility = android.view.View.GONE
                    //显示加载中
                    mBinding.ivLoading.visibility = View.VISIBLE
                    loadingAnim?.cancel()
                    loadingAnim = mBinding.ivLoading.startSafeObjectAnimator(
                        "rotation",  // 属性名称
                        0f, 360f // 从 0 度旋转到 360 度
                    ).run {
                        // 设置动画属性
                        setDuration(1500) // 持续时间 1.5 秒
                        repeatCount = ObjectAnimator.INFINITE // 无限循环
                        interpolator = LinearInterpolator() // 匀速旋转

                        // 启动动画
                        start()
                        this
                    }
                }

                is SmsVerifyState.Success -> {
                    //登录成功，关闭当前activity
                    showToast(getString(R.string.account_login_success))
                    mBinding.ivLoading.visibility = View.GONE
                    loadingAnim?.cancel()
                    requireActivity().finish()
                }

                is SmsVerifyState.ToNickName -> {
                    showToast(getString(R.string.account_register_success))
                    mBinding.ivLoading.visibility = View.GONE
                    loadingAnim?.cancel()
                    //转到修改昵称界面
                    navigate(arch.cayenne.lib.res.R.string.nav_module_nickname_initial_fragment.deeplink())
                }

                is SmsVerifyState.Failure -> {
                    //显示验证码错误
                    mBinding.ivLoading.visibility = View.GONE
                    loadingAnim?.cancel()
                    mBinding.tvVerifyFailure.visibility = android.view.View.VISIBLE
                    //清空验证码输入框
                    mBinding.etSmsCode.setText("")
                }

            }
        }

    }

}