package com.walisport.lib_common.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.walisport.lib_common.R
import com.walisport.lib_common.databinding.DialogCommonBinding
import com.walisport.lib_common.utils.ViewUtils

class CommonDialog : DialogFragment() {
    private var _binding: DialogCommonBinding? = null
    private val binding get() = _binding!!
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
        _binding = DialogCommonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            message = it.getString(ARG_MESSAGE)
            okText = it.getString(ARG_OK_TEXT)
            cancelText = it.getString(ARG_CANCEL_TEXT)
        }

        binding.tvCommonDialogTitle.apply {
            text = title ?: ""
            if (title.isNullOrEmpty()) {
                visibility = View.GONE
            }
        }
        binding.tvCommonDialogMessage.text = message ?: ""
        binding.btnCommonDialogOk.text = okText ?: ""
        binding.btnCommonDialogCancel.text = cancelText ?: ""

        binding.btnCommonDialogOk.setOnClickListener {
            onOkClick?.invoke()
            dismiss()
        }

        binding.btnCommonDialogCancel.setOnClickListener {
            onCancelClick?.invoke()
            dismiss()
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
            setLayout(ViewUtils.dpToPx(280f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable( ContextCompat.getDrawable(requireContext(), R.drawable.shape_corner_12))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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