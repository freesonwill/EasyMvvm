package arch.cayenne.module.account.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.data.constants.LanguageType
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.data.constants.OddsDisplayEnum

import arch.cayenne.module.account.databinding.DialogAvatarBinding
import kotlin.reflect.KClass

class AvatarDialogFragment : BaseBottomSheetFragment<EmptyViewModel, DialogAvatarBinding>() {

    override val vbClass: KClass<DialogAvatarBinding>
        get() = DialogAvatarBinding::class
    override val vmClass: KClass<EmptyViewModel>
        get() = EmptyViewModel::class
    private var clicklistener: OnClickListener? = null



    override fun initView(savedInstanceState: Bundle?) {

    }

    override suspend fun createObserver() {
        super.createObserver()
    }

    override fun initListener() {
        mBinding.tvConfirm.clickNoRepeat {
            clicklistener?.onClickConfirm()
            super.dismiss()
        }
        mBinding.tvAvtarDelete.clickNoRepeat {
            clicklistener?.onClickAvtarDelete()
            super.dismiss()
        }
        mBinding.tvClose.clickNoRepeat {
            super.dismiss()
        }
    }

    fun setOnItemClickListener(listener: OnClickListener) {
        this.clicklistener = listener
    }

    interface OnClickListener {
        fun onClickConfirm()
        fun onClickAvtarDelete()
    }
}