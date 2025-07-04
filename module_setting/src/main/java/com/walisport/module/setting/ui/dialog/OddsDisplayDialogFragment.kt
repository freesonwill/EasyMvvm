package com.walisport.module.setting.ui.dialog

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.setting.R
import com.walisport.module.setting.data.OddsDisplayEnum
import com.walisport.module.setting.databinding.DialogOddsDisplayBinding
import com.walisport.module.setting.ui.viewmodel.OddsDisplayViewModel
import kotlin.reflect.KClass

class OddsDisplayDialogFragment : BaseBottomSheetFragment<OddsDisplayViewModel, DialogOddsDisplayBinding>() {
    override val vbClass: KClass<DialogOddsDisplayBinding>
        get() = DialogOddsDisplayBinding::class
    override val vmClass: KClass<OddsDisplayViewModel>
        get() = OddsDisplayViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        val spannableString = SpannableString(getString(R.string.display_odds_ben))
        spannableString.setSpan(
            ForegroundColorSpan(Color.RED),
            5,
            7,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mBinding.tvDisplayOddsEp.text = spannableString
        val spannableStringHK = SpannableString(getString(R.string.display_odds_not))
        spannableStringHK.setSpan(
            ForegroundColorSpan(Color.RED),
            5,
            8,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mBinding.tvDisplayOddsHk.text = spannableStringHK
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.displayType.observe(viewLifecycleOwner) { displayType ->
            mBinding.radioEp.isSelected = displayType == OddsDisplayEnum.EU
            mBinding.radioHk.isSelected = displayType == OddsDisplayEnum.HK
        }
    }

    override fun initListener() {
        mBinding.itemHk.clickNoRepeat {
            mViewModel.setOddsType(OddsDisplayEnum.HK)
            dialogDismiss()
        }
        mBinding.itemEp.clickNoRepeat {
            mViewModel.setOddsType(OddsDisplayEnum.EU)
            dialogDismiss()
        }
        mBinding.tvClose.clickNoRepeat {
            super.dismiss()
        }
    }

    private fun dialogDismiss() {
        mBinding.root.postDelayed({
            super.dismiss()
        }, 300)
    }
}