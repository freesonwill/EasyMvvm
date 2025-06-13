package com.walisport.module.feedback.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
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

        // 将 CheckBox 放入列表
        val checkBoxes = listOf(
            mBinding.checkbox1,
            mBinding.checkbox2,
            mBinding.checkbox3,
            mBinding.checkbox4,
            mBinding.checkbox5,
            mBinding.checkbox6
        )

        // 设置监听器
        checkBoxes.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, _ ->
                mViewModel.setCheckBoxSelected(checkBoxes.any { it.isChecked })
            }
        }

        mBinding.buttonSubmit.clickNoRepeat {
            if (mViewModel.checkBoxSelected.value != true) {
                showToast(getString(R.string.select_feedback_type))
            } else if (mViewModel.textInputted.value != true) {
                showToast(getString(R.string.enter_feedback_description))
            } else {
                showToast(getString(R.string.no_interface))
            }
        }

    }

    override fun createObserver() {
        with(mViewModel) {
            btnEnabled.observe(viewLifecycleOwner) {
                mBinding.buttonSubmit.isEnabled = it
            }
        }
    }


    override fun initData() {
        super.initData()
    }


}