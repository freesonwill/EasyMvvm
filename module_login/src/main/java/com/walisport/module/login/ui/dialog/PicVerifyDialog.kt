package com.walisport.module.login.ui.dialog

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.fragment.BaseDialogFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.login.databinding.DialogPicVerifyBinding
import kotlin.reflect.KClass

/**
 * @author: ricky.chang
 * @date: 2025/7/22 下午4:26
 * @description:
 */
class PicVerifyDialog: BaseDialogFragment<EmptyViewModel, DialogPicVerifyBinding>() {
    override val vbClass: KClass<DialogPicVerifyBinding>
        get() = DialogPicVerifyBinding::class
    override val vmClass: KClass<EmptyViewModel>
        get() = EmptyViewModel::class
    override val dialogBackground: Drawable?
        get() = SkinnableResourceManager.getDrawable(
            requireContext(),
            arch.cayenne.lib.base.R.drawable.bg_base_dialog
        )


    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
        with(mBinding) {
            btnClose.clickNoRepeat {
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(280.dp2px, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun dismiss() {
        super.dismiss()
        mBinding.root.isVisible = false
    }

    companion object {
        fun newInstance() = PicVerifyDialog()
    }
}