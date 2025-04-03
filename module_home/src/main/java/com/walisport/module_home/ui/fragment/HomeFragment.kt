package com.walisport.module.home.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.home.R
import com.walisport.module.home.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<EmptyViewModel, FragmentHomeBinding>() {
    override val mBinding: FragmentHomeBinding by viewBind()
    override val mViewModel: EmptyViewModel by viewModel()

    val navOptions = NavOptions.Builder()
        .setEnterAnim(com.walisport.lib.common.R.anim.slide_in_right)  // 新页面进入动画
        .setExitAnim(com.walisport.lib.common.R.anim.slide_out_left)   // 旧页面退出动画
        .setPopEnterAnim(com.walisport.lib.common.R.anim.slide_in_left) // 返回时，新页面进入动画
        .setPopExitAnim(com.walisport.lib.common.R.anim.slide_out_right) // 返回时，当前页面退出动画
        .build()

    override fun initView(savedInstanceState: Bundle?) {

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

            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSecondFragment("Tom"))
            //findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginActivity(null))
        }
        //mBinding.tv2.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.LoginActivity,bundleOf("userId" to "David")))
        mBinding.tv2.setOnClickListener {
            //findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginActivity())
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginActivity("actionHomeFragmentToLoginActivity"))

            //findNavController().navigate(R.id.LoginActivity, bundleOf("userId" to "David"))
            //findNavController().navigate(R.id.loginSecondFragment)
            //apply plugin: 'androidx.navigation.safeargs.kotlin'
            //findNavController().navigate(R.id.action_homeFragment_to_secondFragment)
            //findNavController().navigate(R.id.secondFragment)

        }
        mBinding.tv3.setOnClickListener{
            //deep link
            findNavController().navigate(Uri.parse("walisport://login_activity?userId=lucy"))
            //findNavController().navigate(R.id.action_homeFragment_to_LoginActivity, bundleOf("userId" to "lili"))
          //findNavController().navigate(Uri.parse("walisport://login_activity?userId=lucy"))
        }
        mBinding.tv4.setOnClickListener{
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSecondFragment("toFragmentInner"))
            //findNavController().navigate(Uri.parse("walisport://module_login/loginSecondFragment"))
        }
        mBinding.tv5.setOnClickListener {
            /*val request = NavDeepLinkRequest.Builder
                .fromUri("walisport://module_login/loginSecondFragment".toUri())
                .build()
            findNavController().navigate(request)*/
            findNavController().navigate(Uri.parse("walisport://module_login/loginSecondFragment"))
            //startActivity(Intent().apply { component  = ComponentName(requireActivity().packageName, "com.walisport.module_login.ui.LoginActivity") })
            //findNavController().navigate(R.id.loginFragment)
        }

        mBinding.tv6.setOnClickListener{
            //
//            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSecondFragment("Tom"))

        }

        mBinding.tv7.setOnClickListener{
            findNavController().navigate(Uri.parse("walisport://module_setting/settingFragment"))
        }

        mBinding.tv8.setOnClickListener{
            findNavController().navigate(Uri.parse("walisport://module_live/leagueFragment"))
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
        findNavController().navigate(R.id.LoginActivity, bundle, null, extras)
    }

    override fun createObserver() {

    }

}