package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.module.home.databinding.FragmentChampionBinding
import kotlin.reflect.KClass

class ChampionFragment: BaseFragment<EmptyViewModel, FragmentChampionBinding>(){
    // TODO 待實作
    override val vbClass: KClass<FragmentChampionBinding> = FragmentChampionBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

}