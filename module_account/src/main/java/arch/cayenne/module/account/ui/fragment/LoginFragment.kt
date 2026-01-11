package arch.cayenne.module.account.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.account.R
import arch.cayenne.module.account.databinding.FragmentLoginBinding
import kotlin.reflect.KClass

class LoginFragment : BaseFragment<EmptyViewModel, FragmentLoginBinding>() {

    override val vbClass: KClass<FragmentLoginBinding> = FragmentLoginBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        val spanStr = SpannableString(R.string.tip_agree.getString())
        spanStr.setSpan(
            ForegroundColorSpan(Color.parseColor("#98bde5")),
            11,
            15,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spanStr.setSpan(
            ForegroundColorSpan(Color.parseColor("#98bde5")),
            16,
            20,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mBinding.tvTipAgree.text = spanStr
    }

    override fun initListener() {
        mBinding.apply {
            btnClose.addScaleOnTouchAnimation()
            btnClose.clickNoRepeat {
                requireActivity().finish()
            }
            btnPhone.clickNoRepeat {
                navigate(R.id.action_to_loginOrRegisterFragment)
            }
            btnGoogle.clickNoRepeat {

            }
            btnMeta.clickNoRepeat {

            }
            btnLine.clickNoRepeat {

            }
            btnTelegram.clickNoRepeat {

            }
            btnKey.clickNoRepeat {

            }
            btnX.clickNoRepeat {

            }
        }
    }

    override suspend fun createObserver() {

    }
}