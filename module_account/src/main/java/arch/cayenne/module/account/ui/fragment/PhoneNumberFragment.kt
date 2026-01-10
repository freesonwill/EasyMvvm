package arch.cayenne.module.account.ui.fragment

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.startSafeObjectAnimator
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.account.databinding.FragmentPhoneBinding
import arch.cayenne.module.account.ui.viewmodel.PhoneNumberViewModel
import arch.cayenne.module.account.ui.viewmodel.SmsState
import kotlin.reflect.KClass

class PhoneNumberFragment :
    BaseFragment<PhoneNumberViewModel, FragmentPhoneBinding>() {
    override val vbClass: KClass<FragmentPhoneBinding> =
        FragmentPhoneBinding::class
    override val vmClass: KClass<PhoneNumberViewModel> = PhoneNumberViewModel::class

    private var loadingAnim: ObjectAnimator? = null


    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            tvCountryCode.text = "+86" //先做*86
        }

    }

    override fun initData() {
        super.initData()
    }

    override fun initListener() {
        mBinding.llWrapper.clickNoRepeat {
            "TODO: 选择国家和区号".logi(TAG)
        }

        mBinding.llNextWrapper.clickNoRepeat {
            //发送验证码
            mViewModel.requestSMSCode(
                countryCode = mBinding.tvCountryCode.text.toString().replace("+", ""),  //区号， 要去掉加号
                phoneNumber = mBinding.editTextPhone.text.toString() //手机号
            )
        }

    }

    override suspend fun createObserver() {
        mViewModel.apiStateListener.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.NetworkUnavailable -> {
                    showToast(getString(arch.cayenne.lib.common.R.string.error_net))


                    mBinding.tvNext.visibility = View.VISIBLE
                    mBinding.ivLoading.visibility = View.GONE
                    loadingAnim?.cancel()
                }

                is DataState.LoadSuccess -> {
                    mBinding.tvNext.visibility = View.VISIBLE
                    mBinding.ivLoading.visibility = View.GONE
                    loadingAnim?.cancel()
                }

                is DataState.Loading -> {
                    mBinding.tvNext.visibility = View.GONE
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
            }

        }

        mViewModel.smsState.observe(viewLifecycleOwner) {
            when (it) {
                SmsState.Success -> {
                    showToast("验证码已发送，请注意查收")
                    navigate(
                        arch.cayenne.lib.res.R.string.nav_module_sms_verify_fragment.deeplink(
                            "phoneNumber" to mBinding.editTextPhone.text.toString(),
                            "countryCode" to mBinding.tvCountryCode.text.toString()
                        )
                    )
                }

                SmsState.Failure -> {
                    showToast("获取验证码失败，请重试")
                }
            }
        }

    }

}