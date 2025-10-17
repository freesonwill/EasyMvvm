package arch.cayenne.module.account.ui.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.account.R
import arch.cayenne.module.account.ui.viewmodel.AvatarViewModel
import arch.cayenne.module.account.databinding.FragmentAvatarBinding
import kotlin.reflect.KClass
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
class AvatarFragment : BaseFragment<AvatarViewModel, FragmentAvatarBinding>() {

    override val vbClass: KClass<FragmentAvatarBinding> = FragmentAvatarBinding::class
    override val vmClass: KClass<AvatarViewModel> = AvatarViewModel::class
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(
            R.string.user_avatar,
            { findNavController().navigateUp() }
        )
        var params: ViewGroup.LayoutParams = mBinding.ivUserAvatar.layoutParams
        params.height = params.width
        mBinding.ivUserAvatar.layoutParams = params

    }

    override fun initListener() {

        mBinding.systemAvatar.clickNoRepeat{
            navigate(AvatarFragmentDirections.actionAvatarFragmentToSystemAvatarFragment())
        }
    }

    override suspend fun createObserver() {

    }
}