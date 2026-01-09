package arch.cayenne.module.account.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.databinding.TitleBarSimpleBinding
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.account.R
import arch.cayenne.module.account.databinding.FragmentSmsVerifyBinding
import arch.cayenne.module.account.ui.viewmodel.SmsVerifyViewModel
import kotlin.reflect.KClass

class SmsVerifyFragment :
    BaseFragment<SmsVerifyViewModel, FragmentSmsVerifyBinding>() {
    override val vbClass: KClass<FragmentSmsVerifyBinding> =
        FragmentSmsVerifyBinding::class
    override val vmClass: KClass<SmsVerifyViewModel> = SmsVerifyViewModel::class

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

        mBinding.otpView.setOtpCompletionListener {

        }
    }

    override fun initData() {
        super.initData()
    }

    override fun initListener() {


    }

    override suspend fun createObserver() {

    }

}