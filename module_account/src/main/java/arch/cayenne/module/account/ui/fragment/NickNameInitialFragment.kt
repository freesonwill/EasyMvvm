package arch.cayenne.module.account.ui.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.LinearInterpolator
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.startSafeObjectAnimator
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.account.R
import arch.cayenne.module.account.data.constants.NicknameInitialState
import arch.cayenne.module.account.databinding.FragmentNicknameInitialBinding
import arch.cayenne.module.account.ui.viewmodel.NickNameInitialViewModel
import kotlin.reflect.KClass

class NickNameInitialFragment :
    BaseFragment<NickNameInitialViewModel, FragmentNicknameInitialBinding>() {
    override val vbClass: KClass<FragmentNicknameInitialBinding> =
        FragmentNicknameInitialBinding::class
    override val vmClass: KClass<NickNameInitialViewModel> = NickNameInitialViewModel::class

    private var previousText = ""
    private var loadingAnim: ObjectAnimator? = null


    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.viewModel = mViewModel

        mBinding.tvNumber.text = "0/${mViewModel.maxInputLength}"

        //默认下一步按钮不可用
        mBinding.llNextWrapper.isEnabled = false
        mBinding.llNextWrapper.alpha = 0.4f
    }

    override fun initData() {
        super.initData()
    }

    override fun initListener() {

        mBinding.tvSkip.clickNoRepeat {
            //跳过，finish activity
            requireActivity().finish()
        }

        mBinding.llNextWrapper.clickNoRepeat {
            //修改昵称
            if (mViewModel.apiStateListener.value is DataState.Loading) {
                //正在加载中，防止重复点击
                return@clickNoRepeat
            }
            mViewModel.changeNickname(mBinding.etNickname.text.toString().trim())
        }

        mBinding.etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {

                val current = s.toString()

                // 簡單判斷：如果新文字比之前短或有代理對 → 認為有 emoji 被加入
                val hasEmoji = current.length != previousText.length &&
                        current.any { Character.isSurrogate(it) }

                if (hasEmoji) {
                    // 1. 立刻把文字恢復成之前的（最接近「返回」）
                    mBinding.etNickname.setText(previousText)

                    // 2. 把光標放回最後面（或原位置）
                    mBinding.etNickname.setSelection(previousText.length)

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
                    mBinding.etNickname.setText(mappedString)
                    mBinding.etNickname.setSelection(mappedString.length)
                    return
                }
                val inputLength = mappedString.length
                mBinding.tvNumber.text = "$inputLength/${mViewModel.maxInputLength}"
                val color = if (inputLength == mViewModel.maxInputLength) {
                    resources.getColor(arch.cayenne.lib.common.R.color.color_FE3666, null)
                } else {
                    resources.getColor(arch.cayenne.lib.common.R.color.color_999999, null)
                }
                mBinding.tvNumber.setTextColor(color)

                val isEnabled = inputLength > 0
                mBinding.llNextWrapper.isEnabled = isEnabled
                mBinding.llNextWrapper.alpha = if (isEnabled) 1f else 0.4f
            }
        })
    }

    override suspend fun createObserver() {
        mViewModel.apiStateListener.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.NetworkUnavailable -> {
                    showToast(getString(arch.cayenne.lib.common.R.string.error_net))


                    mBinding.tvNext.visibility = View.VISIBLE
                    mBinding.ivLoading.visibility = View.GONE
                    loadingAnim?.cancel()
                }

                is NicknameInitialState.Success -> {
                    //finish activity
                    requireActivity().finish()
                    loadingAnim?.cancel()
                }

                is NicknameInitialState.Failure -> {
                    showToast(getString(R.string.nickname_modify_failed))

                    mBinding.tvNext.visibility = View.VISIBLE
                    mBinding.ivLoading.visibility = View.GONE

                    loadingAnim?.cancel()
                }

                is DataState.Loading -> {
                    mBinding.tvNext.visibility = View.GONE
                    mBinding.ivLoading.visibility = View.VISIBLE

                    loadingAnim?.cancel()
                    loadingAnim = mBinding.ivLoading.startSafeObjectAnimator(
                        "rotation",  // 属性名称
                        0f, 360f // 从 0 度旋转到 360 度
                    ).run {
                        // 设置动画属性
                        setDuration(1500) // 持续时间 1.5 秒
                        repeatCount = ObjectAnimator.INFINITE // 无限循环
                        interpolator = LinearInterpolator() // 匀速旋转

                        // 启动动画
                        start()
                        this
                    }

                }
            }
        }
    }

}