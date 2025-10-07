package arch.cayenne.lib.test.ui.fragment

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.test.databinding.FragmentMultipleNavHostTestBinding
import kotlin.reflect.KClass


class MultipleNavHostTestFragment: BaseFragment<EmptyViewModel, FragmentMultipleNavHostTestBinding>() {
    override val vbClass: KClass<FragmentMultipleNavHostTestBinding> = FragmentMultipleNavHostTestBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.apply {
            tv1.setOnClickListener {
                val navController = findNavController()
                navController.navigate(MultipleNavHostTestFragmentDirections.actionMultipleNavHostTestFragmentToHomeTestFragment())
            }
        }
    }

    override fun onBackPressed(): Boolean {
        if(mBinding.navHost2.findNavController().navigateUp()) return true
        if(mBinding.navHost3.findNavController().navigateUp()) return true
        return super.onBackPressed()
    }

    override suspend fun createObserver() {
    }

    /* override fun onDestroyView() {
         super.onDestroyView()
         mBinding.viewPagerNew.adapter = null
         *//*val fragmentManager = childFragmentManager
        val fragments = fragmentManager.fragments

        if (fragments.isNotEmpty()) {
            val transaction = fragmentManager.beginTransaction()
            for (fragment in fragments) {
                transaction.remove(fragment)
            }
            transaction.commitNowAllowingStateLoss()
        }*//*
    }*/

}