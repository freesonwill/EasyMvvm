package arch.cayenne.module.account.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.databinding.TitleBarSimpleBinding
import arch.cayenne.lib.common.ui.view.CustomTabIndicator
import arch.cayenne.lib.common.ui.view.CustomTabLayoutMediator
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.lib.common.utils.ext.setupViewPagerScroll
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.module.account.R
import arch.cayenne.module.account.databinding.FragmentLoginOrRegisterBinding
import arch.cayenne.module.account.ui.viewmodel.LoginOrRegisterViewModel
import arch.cayenne.module.account.ui.viewmodel.SystemAvatarViewModel
import kotlin.reflect.KClass

class LoginOrRegisterFragment :
    BaseFragment<LoginOrRegisterViewModel, FragmentLoginOrRegisterBinding>() {
    override val vbClass: KClass<FragmentLoginOrRegisterBinding> =
        FragmentLoginOrRegisterBinding::class
    override val vmClass: KClass<LoginOrRegisterViewModel> = LoginOrRegisterViewModel::class

    private var tabMediator: CustomTabLayoutMediator? = null
    private var customIndicator: CustomTabIndicator? = null

    private val titleBarBinding: TitleBarSimpleBinding by lazy {
        TitleBarSimpleBinding.inflate(
            LayoutInflater.from(context),
            mBinding.titleBar,
            false
        )
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadDynamicsTitleBars(titleBarBinding.root)

        titleBarBinding.apply {
            tvTitleName.text = getString(R.string.title_login_or_register)
        }

        with(mBinding){
            val list = listOf(
                PagerBean(R.string.account_phone_register.getString()) { PhoneNumberFragment() },
                PagerBean(R.string.account_email_register.getString()) { EmailFragment() },
            )
            viewPager.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
            tabMediator?.detach()
            tabMediator = CustomTabLayoutMediator(
                tabLayout = mBinding.tabLayout,
                viewPager = mBinding.viewPager
            ) { tab, pos ->
                tab.text = list[pos].title
            }.also { layoutMediator ->
                layoutMediator.attach()
            }
        }

        mBinding.tabLayout.post {
            mBinding.tabLayout.getTabAt(1)?.view?.setPadding(21.dp2px, 0, 21.dp2px, 4.dp2px)
        }
        customIndicator = mBinding.homeIndicator
        mBinding.viewPager.setupViewPagerScroll(
            mBinding.tabLayout,
            customIndicator!!,
            tabIndicatorWidth = 0.45f,select = 1
        )
        mBinding.viewPager.setupHorizontalScrollDegree()
        mBinding.root.touchBackPressed()

    }

    override fun initData() {
        super.initData()
    }

    override fun initListener() {
        with(titleBarBinding) {
            ivBack.addScaleOnTouchAnimation()
            ivBack.clickNoRepeat {
                findNavController().navigateUp()
            }
        }

    }

    override suspend fun createObserver() {

    }

}