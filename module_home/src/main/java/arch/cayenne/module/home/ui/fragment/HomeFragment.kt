package arch.cayenne.module.home.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.ActivityNavigatorExtras
import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.bet.ui.fragment.BetSheetFragment
import arch.cayenne.module.bet.ui.fragment.FloatingButtonFragment
import arch.cayenne.module.home.databinding.FragmentHomeBinding
import arch.cayenne.module.home.R
import kotlin.reflect.KClass

class HomeFragment : BaseFragment<EmptyViewModel, FragmentHomeBinding>() {
    override val vbClass: KClass<FragmentHomeBinding> = FragmentHomeBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        childFragmentManager.beginTransaction()
            .replace(mBinding.floatingContainer.id, FloatingButtonFragment())
            .commit()
    }

    override fun initListener() {
        /**
         * navigation:
         * fragment --> fragment(Internal)
         * fragment --> fragment(External)
         * activity -> fragment
         * fragment -> activity
         */
        mBinding.tv1.setOnClickListener{
            navigate(HomeFragmentDirections.actionHomeFragmentToSecondFragment("Tom"))
            //navigate(HomeFragmentDirections.actionHomeFragmentToLoginActivity(null))
        }
        //mBinding.tv2.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.LoginActivity,bundleOf("userId" to "David")))
        mBinding.tv2.setOnClickListener {
            //navigate(HomeFragmentDirections.actionHomeFragmentToLoginActivity())
           navigate(HomeFragmentDirections.actionHomeFragmentToLoginActivity("actionHomeFragmentToLoginActivity"))

            //navigate(R.id.LoginActivity, bundleOf("userId" to "David"))
            //navigate(R.id.loginSecondFragment)
            //apply plugin: 'androidx.navigation.safeargs.kotlin'
            //navigate(R.id.action_homeFragment_to_secondFragment)
            //navigate(R.id.secondFragment)

        }
        mBinding.tv3.setOnClickListener{
            //deep link
           navigate(Uri.parse("walisport://login_activity?userId=lucy"))
            //navigate(R.id.action_homeFragment_to_LoginActivity, bundleOf("userId" to "lili"))
          //navigate(Uri.parse("walisport://login_activity?userId=lucy"))
        }
        mBinding.tv4.setOnClickListener{
           navigate(HomeFragmentDirections.actionHomeFragmentToSecondFragment("toFragmentInner"))
            //navigate(Uri.parse("walisport://module_login/loginSecondFragment"))
        }
        mBinding.tv5.setOnClickListener {
            /*val request = NavDeepLinkRequest.Builder
                .fromUri("walisport://module_login/loginSecondFragment".toUri())
                .build()
           navigate(request)*/
           navigate(Uri.parse("walisport://module_login/loginSecondFragment"))
            //startActivity(Intent().apply { component  = ComponentName(requireActivity().packageName, "com.walisport.module_login.ui.LoginActivity") })
            //navigate(R.id.loginFragment)
        }

        mBinding.tv6.setOnClickListener{
            navigate(HomeFragmentDirections.actionHomeFragmentToNewHomeFragment())
        }

        mBinding.tv7.clickNoRepeat{
           navigate(Uri.parse("walisport://module_setting/settingFragment"))
        }

        mBinding.tv8.clickNoRepeat{
            navigate(Uri.parse("walisport://module_live/liveFragment?matchId=123"))
        }
        mBinding.tv10.setOnClickListener {
            BetSheetFragment.addMockData()
        }

        mBinding.tv11.clickNoRepeat{
            navigate(Uri.parse("walisport://module_search/searchFragment"))
        }

        mBinding.tv12.clickNoRepeat{
            navigate(Uri.parse("walisport://module_handicap/HandicapFragment"))
        }
    }

    private fun toFragmentInner(){

    }

    private fun toActivityByIdBundle(){
        val options = ActivityOptionsCompat.makeCustomAnimation(
            requireContext(),
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        val extras = ActivityNavigatorExtras(
            activityOptions = options,
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        )
        // 使用 Bundle 传递参数（如果 Safe Args 不支持 Activity 参数）
        val bundle = Bundle().apply { putString("userId", "userId") }
       navigate(R.id.LoginActivity, bundle, null, extras)
    }

    override fun createObserver() {

    }

}