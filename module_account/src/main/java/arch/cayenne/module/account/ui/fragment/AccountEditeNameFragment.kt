package arch.cayenne.module.account.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.clickNoRepeatSingle
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.module.account.ui.viewmodel.AccountEditNameViewModel
import arch.cayenne.module.account.databinding.FragmentAccountEditNameBinding
import arch.cayenne.module.account.databinding.TitleBarAccountBinding
import arch.cayenne.module.account.databinding.AccountEditNameFlexboxTextViewBinding
import kotlin.reflect.KClass
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.account.ui.viewmodel.PersonalInfoViewModel
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString

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
            mBinding.ceName.isEnabled = changeCount != 0
            titleBarBinding.tvSave.isClickable = false
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

                val inputLength = s?.length ?: 0
                mBinding.tvNumber.text =
                    if (inputLength == 0) "" else "$inputLength/${maxInputLength}"
                //超限输入
                //输入框停止接收新字符，字数统计显示为红色警示
                //有输入且未超限，字数统计显示为红色，保存按钮可用
                if (inputLength ==0) {
                    mBinding.tvNumber.setTextColor(
                        resources.getColor(
                            arch.cayenne.lib.common.R.color.color_999999,
                            null
                        )
                    )
                    titleBarBinding.tvSave.isClickable = false
                    titleBarBinding.tvSave.isSelected = false
                } else if (inputLength == maxInputLength) {
                    mBinding.tvNumber.setTextColor(
                        resources.getColor(
                            arch.cayenne.lib.common.R.color.color_FE3666,
                            null
                        )
                    )
                    titleBarBinding.tvSave.isClickable = true
                    titleBarBinding.tvSave.isSelected = true
                } else {
                    mBinding.tvNumber.setTextColor(
                        resources.getColor(
                            arch.cayenne.lib.common.R.color.color_999999,
                            null
                        )
                    )
                    titleBarBinding.tvSave.isClickable = true
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