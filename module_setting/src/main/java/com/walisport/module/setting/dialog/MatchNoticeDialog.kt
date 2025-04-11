package com.walisport.module.setting.dialog

import android.os.Bundle
import arch.cayenne.lib.base.ui.BaseBottomSheetFragment
import arch.cayenne.lib.base.ui.viewBind
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
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
        mBinding.tvClose.clickNoRepeat {
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