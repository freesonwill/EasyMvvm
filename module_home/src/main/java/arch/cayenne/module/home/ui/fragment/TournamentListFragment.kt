package arch.cayenne.module.home.ui.fragment

import android.content.Context
import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.home.databinding.FragmentTournamentListBinding
import arch.cayenne.module.home.ui.view.TournamentSectionView
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import kotlin.reflect.KClass

class TournamentListFragment : BaseFragment<HomeViewModel, FragmentTournamentListBinding>() {

    override val vbClass: KClass<FragmentTournamentListBinding> =
        FragmentTournamentListBinding::class
    override val vmClass: KClass<HomeViewModel> = HomeViewModel::class
    private var dropdownListener: TournamentSectionView.OnChampionDropdownListener? = null
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        dropdownListener = parentFragment as? TournamentSectionView.OnChampionDropdownListener
    }

    override fun initListener() {
    }

    override fun createObserver() {
        homeViewModel.allTournaments.observe(viewLifecycleOwner) { list ->
            "observe tournaments:$list".logd()
            if (!list.isNullOrEmpty()) {
//                mBinding.tsvContainer.postSetTournamentList(list)
                mBinding.tsvContainer.expandWithData(list)
                mBinding.tsvContainer.onTournamentClick = { id ->
                    mViewModel.selectTournament(id)
                    homeViewModel.setShowAllTournaments(false)
                    mBinding.tsvContainer.collapseWithAnimation()
                }

                // 使用者點擊 collapse icon
                mBinding.tsvContainer.onCollapse = {
                    // 呼叫 parent fragment（NewHomeFragment）的 toggle 方法
                    dropdownListener?.onRequestCollapseChampion()
                }
            }
        }
    }

    override fun initData() {
        super.initData()
    }

    override fun onDetach() {
        dropdownListener = null
        super.onDetach()
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

    companion object {
        fun newInstance(): TournamentListFragment {
            return TournamentListFragment()
        }
    }
}