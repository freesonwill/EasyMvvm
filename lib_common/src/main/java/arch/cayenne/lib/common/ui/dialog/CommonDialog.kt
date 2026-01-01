package arch.cayenne.lib.common.ui.dialog

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.fragment.BaseDialogFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.databinding.DialogCommonBinding
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import kotlin.reflect.KClass

class CommonDialog : BaseDialogFragment<EmptyViewModel, DialogCommonBinding>() {
    override val vbClass: KClass<DialogCommonBinding> get() = DialogCommonBinding::class
    override val vmClass: KClass<EmptyViewModel> get() = EmptyViewModel::class
    override val dialogBackground: Drawable?
        get() = SkinnableResourceManager.getDrawable(
            requireContext(),
            arch.cayenne.lib.base.R.drawable.bg_base_dialog
        )

    private var title: String? = null
    private var message: String? = null
    private var okText: String? = null
    private var cancelText: String? = null
    private var onOkClick: (() -> Unit)? = null
    private var onCancelClick: (() -> Unit)? = null
    private var blueTheme: Boolean = true
    private var cancelable: Boolean = true

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            title = it.getString(ARG_TITLE)
            message = it.getString(ARG_MESSAGE)
            okText = it.getString(ARG_OK_TEXT)
            blueTheme = it.getBoolean(ARG_THEME)
            cancelText = it.getString(ARG_CANCEL_TEXT)
            cancelable = it.getBoolean(ARG_CANCELABLE)
        }
        with(mBinding) {
            isCancelable = cancelable
            tvCommonDialogTitle.apply {
                text = title ?: ""
                visibility = if (title.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            tvCommonDialogMessage.text = message ?: ""
            btnCommonDialogOk.text = okText ?: ""
            btnCommonDialogCancel.text = cancelText ?: ""
            if (cancelText.isNullOrEmpty()) {
                llDoubleButton.visibility = View.GONE
                vDividerContent.visibility = View.GONE
                btnSingleConfirm.visibility = View.VISIBLE
                btnSingleConfirm.text = okText ?: ""
            } else {
                llDoubleButton.visibility = View.VISIBLE
                vDividerContent.visibility = View.VISIBLE
                btnSingleConfirm.visibility = View.GONE
            }
            if (!blueTheme) {
                tvCommonDialogTitle.setTextColor(R.color.color_fe3666.getColor())
                btnCommonDialogOk.setTextColor(R.color.color_fe3666.getColor())
            }
        }
    }

    override fun initListener() {
        with(mBinding) {
            btnCommonDialogOk.setOnClickListener {
                onOkClick?.invoke()
                dismiss()
            }

            btnCommonDialogCancel.setOnClickListener {
                onCancelClick?.invoke()
                dismiss()
            }

            btnSingleConfirm.setOnClickListener {
                onOkClick?.invoke()
                dismiss()
            }
        }
    }


    fun setOnOkClickListener(listener: () -> Unit) {
        onOkClick = listener
    }

    fun setOnCancelClickListener(listener: () -> Unit) {
        onCancelClick = listener
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setWindowAnimations(R.style.CommonDialogAnimation)
            setLayout(280.dp2px, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun dismiss() {
        super.dismiss()
        mBinding.root.isVisible = false
    }

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_MESSAGE = "arg_message"
        private const val ARG_OK_TEXT = "arg_ok_text"
        private const val ARG_CANCEL_TEXT = "arg_cancel_text"
        private const val ARG_CANCELABLE = "arg_cancelable"
        private const val ARG_THEME = "arg_theme"

        fun newInstance(
            title: String,
            message: String,
            okText: String = "",
            cancelText: String = "",
            cancelable: Boolean = true,
            blueTheme: Boolean = true
        ) = CommonDialog().apply {
            arguments = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE, message)
                putString(ARG_OK_TEXT, okText)
                putBoolean(ARG_THEME, blueTheme)
                putString(ARG_CANCEL_TEXT, cancelText)
                putBoolean(ARG_CANCELABLE, cancelable)
            }
        }
    }
}