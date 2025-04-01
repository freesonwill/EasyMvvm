package com.walisport.lib_common.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.walisport.lib_common.R
import com.walisport.lib_common.databinding.DialogCommonBinding
import com.walisport.lib_common.utils.ViewUtils

class CommonDialog private constructor() : DialogFragment() {
    private lateinit var mBinding: DialogCommonBinding
    private var title: String? = null
    private var message: String? = null
    private var okText: String? = null
    private var cancelText: String? = null
    private var onOkClick: (() -> Unit)? = null
    private var onCancelClick: (() -> Unit)? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogCommonBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            message = it.getString(ARG_MESSAGE)
            okText = it.getString(ARG_OK_TEXT)
            cancelText = it.getString(ARG_CANCEL_TEXT)
        }

        with(mBinding) {
            tvCommonDialogTitle.apply {
                text = title ?: ""
                visibility = if (title.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            tvCommonDialogMessage.text = message ?: ""
            btnCommonDialogOk.text = okText ?: ""
            btnCommonDialogCancel.text = cancelText ?: ""

            btnCommonDialogOk.setOnClickListener {
                onOkClick?.invoke()
                dismiss()
            }

            btnCommonDialogCancel.setOnClickListener {
                onCancelClick?.invoke()
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
    fun showSafely(manager: FragmentManager, tag: String) {
        if (!isAdded && manager.findFragmentByTag(tag) == null) {
            show(manager, tag)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewUtils.dpToPx(280f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable( ContextCompat.getDrawable(requireContext(), R.drawable.shape_corner_12))
        }
    }

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_MESSAGE = "arg_message"
        private const val ARG_OK_TEXT = "arg_ok_text"
        private const val ARG_CANCEL_TEXT = "arg_cancel_text"

        fun newInstance(
            title: String,
            message: String,
            okText: String = "",
            cancelText: String = ""
        ) = CommonDialog().apply {
            arguments = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE, message)
                putString(ARG_OK_TEXT, okText)
                putString(ARG_CANCEL_TEXT, cancelText)
            }
        }
    }
}