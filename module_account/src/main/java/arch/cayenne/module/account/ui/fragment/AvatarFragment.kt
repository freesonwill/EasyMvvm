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
import arch.cayenne.module.account.databinding.TitleBarPreviewAvatarBinding
class AvatarFragment : BaseFragment<AvatarViewModel, FragmentAvatarBinding>() {

    override val vbClass: KClass<FragmentAvatarBinding> = FragmentAvatarBinding::class
    override val vmClass: KClass<AvatarViewModel> = AvatarViewModel::class
    private val titleBarBinding: TitleBarPreviewAvatarBinding by lazy {
        TitleBarPreviewAvatarBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadDynamicsTitleBar(titleBarBinding.root){
            findNavController().navigateUp()
        }

        var params: ViewGroup.LayoutParams = mBinding.ivUserAvatar.layoutParams
        params.height = params.width
        mBinding.ivUserAvatar.layoutParams = params
    }

    override fun initListener() {

        mBinding.systemAvatar.clickNoRepeat{
            navigate(AvatarFragmentDirections.actionAvatarFragmentToSystemAvatarFragment())
        }

        mBinding.photo.clickNoRepeat{
            navigate(AvatarFragmentDirections.actionAvatarFragmentToAvatarPreViewFragment())
        }

        mBinding.camera.clickNoRepeat{
            navigate(AvatarFragmentDirections.actionAvatarFragmentToAvatarPreViewFragment())
        }

    }

    override suspend fun createObserver() {

    }
}