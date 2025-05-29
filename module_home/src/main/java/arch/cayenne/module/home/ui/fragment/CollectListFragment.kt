package arch.cayenne.module.home.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.home.databinding.FragmentCollectListBinding
import arch.cayenne.module.home.databinding.TitleBarFavoriteBinding
import arch.cayenne.module.home.ui.adapter.MatchItemAdapter
import arch.cayenne.module.home.ui.adapter.OnMatchItemClickListener
import arch.cayenne.module.home.ui.view.decoration.MatchCardItemDecoration
import arch.cayenne.module.home.ui.viewmodel.CollectListViewModel
import kotlin.reflect.KClass

/**
 * @author:
 * @date: 2025/5/23 上午11:30
 * @description:
 */
class CollectListFragment : BaseFragment<CollectListViewModel, FragmentCollectListBinding>() {
    override val vbClass: KClass<FragmentCollectListBinding> = FragmentCollectListBinding::class
    override val vmClass: KClass<CollectListViewModel> = CollectListViewModel::class
    private val titleBarBinding: TitleBarFavoriteBinding by lazy {
        TitleBarFavoriteBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }
    private lateinit var matchAdapter: MatchItemAdapter
    override fun initView(savedInstanceState: Bundle?) {
        with (mBinding) {
            titleBar.loadDynamicsTitleBar(titleBarBinding.root) {
                findNavController().navigateUp()
            }

            matchAdapter = MatchItemAdapter(object : OnMatchItemClickListener {
                override fun onLiveEntryClick(item: MatchWithMarkets) {
                    navigate(Uri.parse("walisport://module_live/liveFragment?matchId=${item.match.matchId}&sportId=${item.match.basicInfo.sportId}"))
                }

                override fun onFavoriteClick(item: MatchWithMarkets) {
//                    mViewModel.addMatchCollect(item, !item.match.collect)
                }

                override fun onOddsCellClick(selection: SelectionBeanLite) {
//                    lifecycleScope.launch {
//                        val status = mViewModel.setSelection(selection.selectionId)
//                        if (status == AddSelectionStatus.SINGLE) {
//                            BetSheetFragment.newInstance().show(parentFragmentManager)
//                        } else if (status == AddSelectionStatus.DISABLE_COMBO) {
//                            showToast(getString(R.string.disabled_to_combo))
//                        }
//                    }
                }
            })
            val decoration = MatchCardItemDecoration(12.dp2px)
            val layoutManager = LinearLayoutManager(context)
            rvCollectList.apply {
                this.layoutManager = layoutManager
                this.adapter = matchAdapter
                addItemDecoration(decoration)
            }
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getCollect()
    }

    override fun initListener() {
        titleBarBinding.llWalletEntry.clickNoRepeat {

        }
    }

    override fun createObserver() {
        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            titleBarBinding.tvMoney.text = it.getFormalMoney()
        }
        mViewModel.matchListChange.observe(viewLifecycleOwner) { matchList ->
            matchAdapter.submitList(matchList)
        }
    }
}