package com.walisport.module.setting.ui.dialog

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.setting.R
import com.walisport.module.setting.databinding.DialogOddsDisplayBinding
import kotlin.reflect.KClass

class OddsDisplayDialog : BaseBottomSheetFragment<EmptyViewModel, DialogOddsDisplayBinding>() {
    override val vbClass: KClass<DialogOddsDisplayBinding>
        get() = DialogOddsDisplayBinding::class
    override val vmClass: KClass<EmptyViewModel>
        get() = EmptyViewModel::class
    private var clicklistener: OnClickListener? = null
    val bundle = "display_type"
    private var displayType = 0  //赔率类型, 0-欧盘 1-香港盘

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            displayType = it.getInt(bundle)
        }
        if (0 == displayType) {
            mBinding.radioEp.isSelected = true
            mBinding.radioHk.isSelected = false
        } else {
            mBinding.radioEp.isSelected = false
            mBinding.radioHk.isSelected = true
        }
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

    override fun initListener() {
        mBinding.itemHk.clickNoRepeat {
            mBinding.radioEp.isSelected = false
            mBinding.radioHk.isSelected = true
            clicklistener?.onClickHK()
        }
        mBinding.itemEp.clickNoRepeat {
            mBinding.radioEp.isSelected = true
            mBinding.radioHk.isSelected = false
            clicklistener?.onClickEP()
        }
        mBinding.tvClose.clickNoRepeat {
            clicklistener?.onClickClose()
        }
    }

    fun setOnItemClickListener(listener: OnClickListener) {
        this.clicklistener = listener
    }

    interface OnClickListener {
        fun onClickHK()
        fun onClickEP()
        fun onClickClose()
    }
}