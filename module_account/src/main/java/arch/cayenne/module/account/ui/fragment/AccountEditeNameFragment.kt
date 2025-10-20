package arch.cayenne.module.account.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.clickNoRepeatSingle
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.module.account.ui.viewmodel.AccountEditNameViewModel
import arch.cayenne.module.account.databinding.FragmentAccountEditNameBinding
import arch.cayenne.module.account.databinding.TitleBarAccountBinding
import arch.cayenne.module.account.databinding.AccountEditNameFlexboxTextViewBinding
import kotlin.reflect.KClass

class AccountEditeNameFragment :
    BaseFragment<AccountEditNameViewModel, FragmentAccountEditNameBinding>() {
    override val vbClass: KClass<FragmentAccountEditNameBinding> = FragmentAccountEditNameBinding::class
    override val vmClass: KClass<AccountEditNameViewModel> = AccountEditNameViewModel::class
    var listName : List<String> = listOf("一一二三","一二","一二三","一二三四","一二三四五","一二三四五六","yi","一二三四五六七","一二三四五六","yu","一二三四五","一二三")
    val maxInputLength = 7
    private val titleBarBinding: TitleBarAccountBinding by lazy {
        TitleBarAccountBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadDynamicsTitleBar(titleBarBinding.root){
            findNavController().navigateUp()
        }
        titleBarBinding.tvSave.clickNoRepeat{
            findNavController().navigateUp()
        }
        listName.withIndex().forEach { (index, bean) ->
            val textBinding = AccountEditNameFlexboxTextViewBinding.inflate(
                LayoutInflater.from(context),
                mBinding.root,
                false
            )
            textBinding.apply {
                tvContent.text = bean
                tvContent.clickNoRepeatSingle {
                    mBinding.ceName.setText(bean)
                }
            }
            mBinding.flexboxLayout.addView(textBinding.root)
        }
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {
        mBinding.ceName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val inputLength = s?.length ?: 0
                mBinding.tvNumber.text =
                    if (inputLength == 0) "" else "$inputLength/${maxInputLength}"
            }
        })
    }

    override suspend fun createObserver() {

    }

}