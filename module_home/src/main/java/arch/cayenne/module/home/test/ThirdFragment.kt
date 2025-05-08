package arch.cayenne.module.home.test

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.module.home.databinding.FragmentTestThirdBinding
import arch.cayenne.module.home.test.viewmodel.ThirdViewModel
import kotlin.reflect.KClass


class ThirdFragment : BaseFragment<ThirdViewModel, FragmentTestThirdBinding>() {
    override val vbClass: KClass<FragmentTestThirdBinding> = FragmentTestThirdBinding::class
    override val vmClass: KClass<ThirdViewModel> = ThirdViewModel::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        "keepViewOnNavigation--onViewCreated--->$view-->${mViewModel.run {
            """
                $textColor,$textString,${textColorFlow.value},
                ${textStringFlow.value},${textColorLiveData.value},
                ${textStringLiveData.value}
            """.trimIndent()
        }}".logd(TAG)
    }

    override fun initView(savedInstanceState: Bundle?) {
        refreshUI()
    }

    override val keepViewOnNavigation: Boolean = false

    override fun initListener() {
        mBinding.root.setOnClickListener {
            //sendResult("hello","ThirdFragment:${System.currentTimeMillis()}")
            //navigateUp()
            navigate(ThirdFragmentDirections.actionThirdFragmentToFourthFragment())
        }
        mBinding.tv.setOnClickListener {
            //如果keepViewOnNavigation==false,view状态必须依靠viewModel中的数据恢复
            testViewModelBareData()
            testViewModelFlow()
            testViewModelLiveData()
        }
    }
    private fun testViewModelBareData(){
        mViewModel.textString = "ThirdFragment"
        mViewModel.textColor = arch.cayenne.module.bet.R.color.red.getColor()
        refreshUI()
    }

    private fun testViewModelFlow(){
        mViewModel.textColorFlow.tryEmit(arch.cayenne.module.bet.R.color.red.getColor())
        mViewModel.textStringFlow.tryEmit("ThirdFragment")
    }

    private fun testViewModelLiveData(){
        mViewModel.textStringLiveData.value = "ThirdFragment"
        mViewModel.textColorLiveData.value = arch.cayenne.module.bet.R.color.red.getColor()
    }

    private fun refreshUI(){
        mViewModel.textColor?.let { mBinding.tv.setTextColor(it) }
        mViewModel.textString?.let { mBinding.tv.text = it }
    }

    override fun createObserver() {
        launch(Lifecycle.State.RESUMED) {
            launch {
                mViewModel.textStringFlow.collect {
                    if(it == null) return@collect
                    mBinding.tv.text = it
                }
            }
            launch {
                mViewModel.textColorFlow.collect {
                    if(it == null) return@collect
                    mBinding.tv.setTextColor(it)
                }
            }
        }

        mViewModel.textStringLiveData.observe(viewLifecycleOwner){
            if(it == null) return@observe
            mBinding.tv.text = it
        }
        mViewModel.textColorLiveData.observe(viewLifecycleOwner){
            if(it == null) return@observe
            mBinding.tv.setTextColor(it)
        }
    }

}