package arch.cayenne.module.account.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.clickNoRepeatSingle
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.account.R
import arch.cayenne.module.account.databinding.AccountEditNameFlexboxTextViewBinding
import arch.cayenne.module.account.databinding.FragmentAccountEditNameBinding
import arch.cayenne.module.account.databinding.TitleBarAccountBinding
import arch.cayenne.module.account.ui.viewmodel.AccountEditNameViewModel
import arch.cayenne.module.account.ui.viewmodel.PersonalInfoViewModel
import kotlin.reflect.KClass

class AccountEditeNameFragment :
    BaseFragment<AccountEditNameViewModel, FragmentAccountEditNameBinding>() {
    override val vbClass: KClass<FragmentAccountEditNameBinding> =
        FragmentAccountEditNameBinding::class
    override val vmClass: KClass<AccountEditNameViewModel> = AccountEditNameViewModel::class
    private val personalViewModel: PersonalInfoViewModel by sharedViewModel<PersonalInfoViewModel, PersonalInfoFragment>()
    val maxInputLength = 12
    private var previousText = ""
    private val titleBarBinding: TitleBarAccountBinding by lazy {
        TitleBarAccountBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadDynamicsTitleBars(titleBarBinding.root)
        titleBarBinding.ivBack.clickNoRepeat {
            findNavController().navigateUp()
        }
        mBinding.tvRefresh.clickNoRepeat{
            mBinding.flexboxLayout.removeAllViews()
            getAccountNicknameRecommendations()
        }
        titleBarBinding.tvSave.clickNoRepeat {
                val inputName = mBinding.ceName.text.toString().trim()
                if (inputName.isEmpty()) {
                    showToast(arch.cayenne.module.account.R.string.account_edit_name_toast_empty.getString())
                    return@clickNoRepeat
                }
                mViewModel.changeNickname(inputName).observe(viewLifecycleOwner) { success ->
                    if (success) {
                        personalViewModel.getAccountInfo()
                        findNavController().navigateUp()
                    }
                }
        }
        mBinding.root.touchBackPressed()
    }

    override fun initData() {
        val changeCount = personalViewModel.onUserInfoListener.value?.nicknameChangeCount ?: 2
        mBinding.tvHint.text = String.format(
            getString(arch.cayenne.module.account.R.string.account_edit_name_text_hint),
            changeCount
        )
        //changeCount为0时，ceName不能输入， 在点击时展示toast
        if (changeCount == 0) {
            mBinding.ceName.isFocusable = false
            mBinding.ceName.isClickable = true
            mBinding.ceName.setOnClickListener {
                showToast(getString(R.string.account_edit_name_toast_limit))
            }
        } else {
            mBinding.ceName.isEnabled = true
        }
        titleBarBinding.tvSave.isSelected = false

        super.initData()
    }

    override fun initListener() {
        mBinding.ceName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {

                val current = s.toString()

                // 簡單判斷：如果新文字比之前短或有代理對 → 認為有 emoji 被加入
                val hasEmoji = current.length != previousText.length &&
                        current.any { Character.isSurrogate(it) }

                if (hasEmoji) {
                    // 1. 立刻把文字恢復成之前的（最接近「返回」）
                    mBinding.ceName.setText(previousText)

                    // 2. 把光標放回最後面（或原位置）
                    mBinding.ceName.setSelection(previousText.length)

                    return
                }
                previousText = s.toString()

                val input = s?.toString() ?: ""
                // 过滤表情和空格
//                val filtered = input.replace(Regex("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+|\\s"), "")
                val filtered = input.replace(Regex("[\\p{So}\\p{Cn}]|\\s"), "")
                    .replace(
                        "/[\u2190-\u21FF]|[\u2600-\u26FF]|[\u2700-\u27BF]|[\u3000-\u303F]|[\u1F300-\u1F64F]|[\u1F680-\u1F6FF]/g",
                        ""
                    )

                //再次过滤emoji字符
                val mappedString = filtered.mapIndexed { i, char ->
                    val type = Character.getType(char).toByte()
                    // Check for emoji-related character types
                    if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL || type == Character.NON_SPACING_MARK) {
                        "" // Return empty to block the character
                    } else {
                        char // Keep the character
                    }
                }.joinToString("")


                if (input != mappedString) {
                    mBinding.ceName.setText(mappedString)
                    mBinding.ceName.setSelection(mappedString.length)
                    return
                }
                val inputLength = mappedString.length
                mBinding.tvNumber.text = if (inputLength == 0) "" else "$inputLength/${maxInputLength}"
                if (inputLength == 0) {
                    mBinding.tvNumber.setTextColor(resources.getColor(arch.cayenne.lib.common.R.color.color_999999, null))
                    titleBarBinding.tvSave.isSelected = false
                } else if (inputLength == maxInputLength) {
                    mBinding.tvNumber.setTextColor(resources.getColor(arch.cayenne.lib.common.R.color.color_FE3666, null))
                    titleBarBinding.tvSave.isSelected = true
                } else {
                    mBinding.tvNumber.setTextColor(resources.getColor(arch.cayenne.lib.common.R.color.color_999999, null))
                    titleBarBinding.tvSave.isSelected = true
                }
            }
        })



    }

    override suspend fun createObserver() {
        getAccountNicknameRecommendations()
    }

    fun getAccountNicknameRecommendations() {
        mViewModel.getAccountNicknameRecommendations().observe(viewLifecycleOwner) { list ->
            list.withIndex().forEach { (index, bean) ->
                val textBinding = AccountEditNameFlexboxTextViewBinding.inflate(
                    LayoutInflater.from(context),
                    mBinding.root,
                    false
                )
                textBinding.apply {
                    tvContent.text = bean
                    tvContent.clickNoRepeatSingle {
                        val changeCount = personalViewModel.onUserInfoListener.value?.nicknameChangeCount ?: 2
                        if (changeCount != 0) {
                            mBinding.ceName.setText(bean)
                        } else {
                            //changeCount为0时，ceName不能输入， 在点击时展示toast
                            showToast(getString(R.string.account_edit_name_toast_limit))
                        }
                    }
                }

                // 先设置初始透明度为 0
                textBinding.root.alpha = 0f

                // 添加到 FlexboxLayout
                mBinding.flexboxLayout.addView(textBinding.root)

                // 启动渐变淡入动画
                textBinding.root.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .setStartDelay(index * 50L)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
        }
    }

}