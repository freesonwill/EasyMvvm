package com.walisport.module.setting.ui.dialog

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.common.data.constants.LanguageType
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.data.constants.OddsDisplayEnum
import com.walisport.module.setting.databinding.DialogOddsDisplayBinding
import com.walisport.module.setting.ui.viewmodel.OddsDisplayViewModel
import kotlin.reflect.KClass

class OddsDisplayDialogFragment :
    BaseBottomSheetFragment<OddsDisplayViewModel, DialogOddsDisplayBinding>() {
    override val vbClass: KClass<DialogOddsDisplayBinding>
        get() = DialogOddsDisplayBinding::class
    override val vmClass: KClass<OddsDisplayViewModel>
        get() = OddsDisplayViewModel::class

    private var clicklistener: OnClickListener? = null

    val lang: String = "Language"
    val epTip: String = "EPTip"
    val hkTip: String = "HKTip"

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            val language = it.getString(lang)
            val ep = it.getString(epTip)
            val hk = it.getString(hkTip)
            var start = 5
            var epEnd = 7
            var hkEnd = 7
            if (language == LanguageType.LANGUAGE_SIMPLE.value) {
                start = 5
                epEnd = 7
                hkEnd = 8
            } else if (language == LanguageType.LANGUAGE_ENGLISH.value) {
                start = 14
                epEnd = 23
                hkEnd = 27
            } else if (language == LanguageType.LANGUAGE_ID.value) {
                start = 16
                epEnd = 24
                hkEnd = 29
            } else if (language == LanguageType.LANGUAGE_PT.value) {
                start = 6
                epEnd = 13
                hkEnd = 13
            }
            val spannableStringEP = SpannableString(ep)
            spannableStringEP.setSpan(
                ForegroundColorSpan(Color.RED),
                start,
                epEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val spannableStringHK = SpannableString(hk)
            spannableStringHK.setSpan(
                ForegroundColorSpan(Color.RED),
                start,
                hkEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            mBinding.tvDisplayOddsEp.text = spannableStringEP
            mBinding.tvDisplayOddsHk.text = spannableStringHK
        }
    }

    override suspend fun createObserver() {
        super.createObserver()
        mViewModel.displayType.observe(viewLifecycleOwner) { displayType ->
            mBinding.radioEp.isSelected = displayType == OddsDisplayEnum.EU
            mBinding.radioHk.isSelected = displayType == OddsDisplayEnum.HK
        }
    }

    override fun initListener() {
        mBinding.itemHk.clickNoRepeat {
            mViewModel.setOddsType(OddsDisplayEnum.HK)
            clicklistener?.onClickHK()
            dialogDismiss()
        }
        mBinding.itemEp.clickNoRepeat {
            mViewModel.setOddsType(OddsDisplayEnum.EU)
            clicklistener?.onClickEP()
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

    fun setOnItemClickListener(listener: OnClickListener) {
        this.clicklistener = listener
    }

    interface OnClickListener {
        fun onClickHK()
        fun onClickEP()
    }
}