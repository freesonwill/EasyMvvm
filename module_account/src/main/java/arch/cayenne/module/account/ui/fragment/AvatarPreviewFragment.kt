package arch.cayenne.module.account.ui.fragment

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.account.R
import arch.cayenne.module.account.ui.viewmodel.AvatarPreviewViewModel
import arch.cayenne.module.account.databinding.FragmentAvatarPreviewBinding
import arch.cayenne.module.account.databinding.TitleBarSystemAvatarBinding
import kotlin.reflect.KClass
import androidx.navigation.fragment.findNavController
class AvatarPreviewFragment : BaseFragment<AvatarPreviewViewModel, FragmentAvatarPreviewBinding>() {

    override val vbClass: KClass<FragmentAvatarPreviewBinding> = FragmentAvatarPreviewBinding::class
    override val vmClass: KClass<AvatarPreviewViewModel> = AvatarPreviewViewModel::class
    private val titleBarBinding: TitleBarSystemAvatarBinding by lazy {
        TitleBarSystemAvatarBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        val uri = arguments?.getString("uri")
        mBinding.ivUserAvatar.setImageURI(Uri.parse(uri))
        mBinding.titleBar.loadDynamicsTitleBar(titleBarBinding.root){
            findNavController().navigateUp()
        }
        mBinding.titleBar.post{
            titleBarBinding.tvTitle.text = getString(R.string.title_preview)
        }
    }

    override fun initListener() {
        mBinding.tvSave.clickNoRepeat{
            findNavController().navigateUp()
        }

    }

    override suspend fun createObserver() {

    }
}