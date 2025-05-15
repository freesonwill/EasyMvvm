package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.home.databinding.FragmentChampionBinding
import arch.cayenne.module.home.ui.viewmodel.ChampionViewModel
import kotlin.reflect.KClass

class ChampionFragment: BaseFragment<ChampionViewModel, FragmentChampionBinding>(){

    override val vbClass: KClass<FragmentChampionBinding> = FragmentChampionBinding::class
    override val vmClass: KClass<ChampionViewModel> = ChampionViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {

    }

}