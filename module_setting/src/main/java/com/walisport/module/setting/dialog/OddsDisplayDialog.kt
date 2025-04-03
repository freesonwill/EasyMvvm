package com.walisport.module.setting.dialog

import android.os.Bundle
import com.walisport.lib.base.ui.BaseBottomSheetFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.setting.databinding.DialogOddsDisplayBinding

class OddsDisplayDialog : BaseBottomSheetFragment<DialogOddsDisplayBinding>() {

    override val mBinding: DialogOddsDisplayBinding by viewBind()
    private var clicklistener: OnClickListener? = null
    val bundle = "display_type"
    private var displayType = "EP"

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            displayType = it.getString(bundle) ?: ""
        }
        if ("EP" == displayType) {
            mBinding.radioEp.isChecked = true
            mBinding.radioHk.isChecked = false
        } else {
            mBinding.radioEp.isChecked = false
            mBinding.radioHk.isChecked = true
        }
    }

    override fun initListener() {
        mBinding.radioEp.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mBinding.radioEp.isChecked = true
                mBinding.radioHk.isChecked = false
                clicklistener?.onClickEP()
            }
        }
        mBinding.radioHk.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mBinding.radioEp.isChecked = false
                mBinding.radioHk.isChecked = true
                clicklistener?.onClickHK()
            }
        }
        mBinding.itemHk.setOnClickListener {
            mBinding.radioEp.isChecked = false
            mBinding.radioHk.isChecked = true
            clicklistener?.onClickHK()
        }
        mBinding.itemEp.setOnClickListener {
            mBinding.radioEp.isChecked = true
            mBinding.radioHk.isChecked = false
            clicklistener?.onClickEP()
        }
        mBinding.tvClose.setOnClickListener {
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