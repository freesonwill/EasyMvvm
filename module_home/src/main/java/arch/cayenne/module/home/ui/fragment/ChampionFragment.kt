package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.module.home.databinding.FragmentChampionBinding
import arch.cayenne.module.home.databinding.TittleBarChampionBinding
import arch.cayenne.module.home.ui.viewmodel.ChampionViewModel
import kotlin.reflect.KClass

class ChampionFragment: BaseFragment<ChampionViewModel, FragmentChampionBinding>(){

    override val vbClass: KClass<FragmentChampionBinding> = FragmentChampionBinding::class
    override val vmClass: KClass<ChampionViewModel> = ChampionViewModel::class
    private val args: ChampionFragmentArgs by navArgs()

    private val tittleBarBinding: TittleBarChampionBinding by lazy {
        TittleBarChampionBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }
    override fun initData() {
        super.initData()
        mViewModel.setMatchId(args.matchId)
        mViewModel.getChampionDetail()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadDynamicsTitleBar(tittleBarBinding.root)
    }

    override fun initListener() {
    }

    override fun createObserver() {
        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            tittleBarBinding.tvMoney.text = it.getFormalMoney()
        }
    }

}