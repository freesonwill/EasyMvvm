package com.walisport.module_home.ui.fragment

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.walisport.lib_base.data.viewmodel.EmptyViewModel
import com.walisport.lib_base.ui.BaseFragment
import com.walisport.lib_base.ui.viewBind
import com.walisport.module_home.R
import com.walisport.module_home.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<EmptyViewModel, FragmentHomeBinding>() {
    override val mBinding: FragmentHomeBinding by viewBind()
    override val mViewModel: EmptyViewModel by viewModel()

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
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginActivity("Tom"))
            //findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginActivity(null))
        }
        mBinding.tv2.setOnClickListener{
            findNavController().navigate(R.id.LoginActivity, bundleOf("userId" to "David"))
//            findNavController().navigate(R.id.loginSecondFragment)
            //apply plugin: 'androidx.navigation.safeargs.kotlin'
            //findNavController().navigate(R.id.action_homeFragment_to_secondFragment)
            //findNavController().navigate(R.id.secondFragment)

        }
        mBinding.tv3.setOnClickListener{
            findNavController().navigate(Uri.parse("walisport://login_activity?userId=lucy"))
        }
        mBinding.tv4.setOnClickListener{
            //startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("walisport://login_activity")))
            //findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            //findNavController().navigate(Uri.parse("walisport://module_login/loginFragment?userId=lili"))
        }
        mBinding.tv5.setOnClickListener {
            //startActivity(Intent().apply { component  = ComponentName(requireActivity().packageName, "com.walisport.module_login.ui.LoginActivity") })
            //findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun createObserver() {

    }

    override fun lazyLoadData() {

    }

}