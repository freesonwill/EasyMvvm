package com.walisport.module.feedback.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.CheckBox
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.showToast
import com.google.protobuf.ByteString
import com.walisport.module.feedback.R
import com.walisport.module.feedback.databinding.FragmentFeedbackMainBinding
import com.walisport.module.feedback.ui.viewmodel.FeedbackMainViewModel
import kotlin.reflect.KClass

/**
 * 反馈详情页
 */
class FeedbackMainFragment : BaseFragment<FeedbackMainViewModel, FragmentFeedbackMainBinding>() {

    override val vbClass: KClass<FragmentFeedbackMainBinding> = FragmentFeedbackMainBinding::class
    override val vmClass: KClass<FeedbackMainViewModel> = FeedbackMainViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.model = mViewModel
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.feedback_title.getString(), {
                findNavController().navigateUp()
            })
        }
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {
        mBinding.editFeedback.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val inputLength = s?.length ?: 0
                mBinding.tvEditTextLength.text =
                    if (inputLength == 0) "" else "$inputLength/${mViewModel.maxInputLength}"

                mViewModel.setTextInputted(inputLength > 0)
            }
        })

        mBinding.buttonSubmit.clickNoRepeat(2000) {
            val codeArray: ByteString = mViewModel.feedbackLabelList.value?.let { labels ->
                mViewModel.feedbackLabelsToByteString(labels)
            }!!
            mViewModel.feedbackContentAdd(
                mBinding.tvEditTextLength.text.toString().trim(),
                codeArray
            )
        }
    }

    override suspend fun createObserver() {
        with(mViewModel) {
            btnEnabled.observe(viewLifecycleOwner) {
                mBinding.buttonSubmit.isEnabled = it
            }
            feedbackLabelList.observe(viewLifecycleOwner) {
                it!!.withIndex().forEach { (index, label) ->
                    val flexboxView = LayoutInflater.from(requireContext())
                        .inflate(
                            R.layout.feedback_label_flexbox_view,
                            mBinding.flexboxLayout,
                            false
                        )
                    var checkBox = flexboxView.findViewById<CheckBox>(R.id.checkbox)
                    checkBox.text = label.name
                    checkBox.isSelected = it[index].flags
                    checkBox.setOnCheckedChangeListener { _, bool ->
                        it[index].flags = bool
                        hasTrueFlags()
                    }
                    mBinding.flexboxLayout.addView(flexboxView)
                }
            }
            feedbackContentAdd.observe(viewLifecycleOwner) {
                if (it){
                    showToast(getString(R.string.feedback_meg))
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    fun hasTrueFlags() {
        val bol: Boolean = mViewModel.feedbackLabelList.value?.any { it.flags } ?: false
        mViewModel.setCheckBoxSelected(bol)
    }

    override fun initData() {
        mViewModel.getFeedbackLabelList()
        super.initData()
    }
}