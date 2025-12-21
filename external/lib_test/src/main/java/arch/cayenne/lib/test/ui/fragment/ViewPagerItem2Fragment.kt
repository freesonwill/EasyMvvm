package arch.cayenne.lib.test.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.test.databinding.FragmentTestViewPagerItem2Binding
import kotlin.reflect.KClass

/**
 * @author: zhangsan
 * @date: 2025/3/30 23:46
 * @description:
 */
class ViewPagerItem2Fragment : BaseFragment<EmptyViewModel, FragmentTestViewPagerItem2Binding>() {
    private val args: ViewPagerItem2FragmentArgs by navArgs()
    override val vbClass: KClass<FragmentTestViewPagerItem2Binding> = FragmentTestViewPagerItem2Binding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        "initView~~~~~~>args:$args,arguments:$arguments,$this".logd(TAG)
        mBinding.tv.text = args.title

        launch(Lifecycle.State.RESUMED) {
            "ViewPagerItem2Fragment resumed~~~~~~>".logd(TAG)
        }
    }

    override fun initListener() {
        mBinding.tv.setOnClickListener {
            //navigate(ViewPagerItem2FragmentDirections.actionTextViewScreenToHomeFragment3())
            //val navController = (requireActivity() as BaseNavActivity).findNavController()
            //navController.navigate(ViewPagerFragmentDirections.actionViewPagerItem2FragmentToHomeFragment())
            requireActivity().navigate(ViewPagerFragmentDirections.actionViewPagerItem2FragmentToHomeFragment())
        //            navigate(ViewPagerItem2FragmentDirections.actionTextViewScreenToHomeFragment3())
        }
        mBinding.root.setOnClickListener {
            startActivity(Intent(requireActivity(),Class.forName("com.walisport.app.ui.MainActivity")))
        }
    }

    override suspend fun createObserver() {
    }
}
