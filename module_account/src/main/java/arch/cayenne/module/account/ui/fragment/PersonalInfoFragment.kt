package arch.cayenne.module.account.ui.fragment

import android.net.Uri
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.module.account.R
import arch.cayenne.module.account.databinding.FragmentPersonalInfoBinding
import arch.cayenne.module.account.ui.viewmodel.PersonalInfoViewModel
import kotlin.reflect.KClass
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
/**
 * @author: ricky.chang
 * @date: 2025/6/13 下午4:19
 * @description:
 */
class  PersonalInfoFragment : BaseFragment<PersonalInfoViewModel, FragmentPersonalInfoBinding>() {
    override val vbClass: KClass<FragmentPersonalInfoBinding> = FragmentPersonalInfoBinding::class
    override val vmClass: KClass<PersonalInfoViewModel> = PersonalInfoViewModel::class
    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            mBinding.titleBar.loadGeneralTitleBar(R.string.personal_info_title, {
                findNavController().navigateUp()
            })
            mBinding.root.touchBackPressed()
        }
    }

    override fun initListener() {
        mBinding.tvName.clickNoRepeat{
            navigate(PersonalInfoFragmentDirections.actionPersonalInfoFragmentToAccountEditNameFragment())
        }

        mBinding.ivAvatar.clickNoRepeat{
            navigate(PersonalInfoFragmentDirections.actionPersonalInfoFragmentToAvatarFragment(""))
        }

    }

    override suspend fun createObserver() {

    }
}