package arch.cayenne.module.account.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.databinding.TitleBarSimpleBinding
import arch.cayenne.lib.common.utils.EditTextUtils
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.account.R
import arch.cayenne.module.account.databinding.FragmentNicknameInitialBinding
import arch.cayenne.module.account.databinding.FragmentSmsVerifyBinding
import arch.cayenne.module.account.ui.viewmodel.NickNameInitialViewModel
import arch.cayenne.module.account.ui.viewmodel.SmsVerifyViewModel
import kotlin.reflect.KClass

class NickNameInitialFragment :
    BaseFragment<NickNameInitialViewModel, FragmentNicknameInitialBinding>() {
    override val vbClass: KClass<FragmentNicknameInitialBinding> =
        FragmentNicknameInitialBinding::class
    override val vmClass: KClass<NickNameInitialViewModel> = NickNameInitialViewModel::class



    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        super.initData()
    }

    override fun initListener() {


    }

    override suspend fun createObserver() {

    }

}