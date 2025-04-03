package com.walisport.module.setting.dialog

import android.os.Bundle
import com.walisport.lib.base.ui.BaseBottomSheetFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.setting.databinding.DialogMatchNoticeBinding

class MatchNoticeDialog : BaseBottomSheetFragment<DialogMatchNoticeBinding>() {

    override val mBinding: DialogMatchNoticeBinding by viewBind()
    private var clicklistener: OnClickListener? = null

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.toggleBet.setOnCheckedChangeListener { _, isChecked ->
            clicklistener?.onClickBet(isChecked)
        }
        mBinding.toggleFav.setOnCheckedChangeListener { _, isChecked ->
            clicklistener?.onClickFav(isChecked)
        }
        mBinding.toggleAll.setOnCheckedChangeListener { _, isChecked ->
            clicklistener?.onClickAll(isChecked)
        }
        mBinding.tvClose.setOnClickListener {
            clicklistener?.onClickClose()
        }
    }

    fun setOnItemClickListener(listener: OnClickListener) {
        this.clicklistener = listener
    }

    interface OnClickListener {
        fun onClickBet(isChecked: Boolean)
        fun onClickFav(isChecked: Boolean)
        fun onClickAll(isChecked: Boolean)
        fun onClickClose()
    }


}