package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.home.databinding.FragmentFavoriteListBinding
import arch.cayenne.module.home.databinding.TitleBarFavoriteBinding
import arch.cayenne.module.home.ui.adapter.FavoriteListAdapter
import arch.cayenne.module.home.ui.viewmodel.FavoriteListViewModel
import kotlin.reflect.KClass

/**
 * @author:
 * @date: 2025/5/23 上午11:30
 * @description:
 */
class FavoriteListFragment : BaseFragment<FavoriteListViewModel, FragmentFavoriteListBinding>() {
    override val vbClass: KClass<FragmentFavoriteListBinding> = FragmentFavoriteListBinding::class
    override val vmClass: KClass<FavoriteListViewModel> = FavoriteListViewModel::class
    private val titleBarBinding: TitleBarFavoriteBinding by lazy {
        TitleBarFavoriteBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }
    private lateinit var favoriteListAdapter: FavoriteListAdapter
    override fun initView(savedInstanceState: Bundle?) {
        with (mBinding) {
            titleBar.loadDynamicsTitleBar(titleBarBinding.root) {
                findNavController().navigateUp()
            }
            favoriteListAdapter = FavoriteListAdapter()
            rvFavorite.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = favoriteListAdapter
            }
        }
    }

    override fun initListener() {
        titleBarBinding.llWalletEntry.clickNoRepeat {

        }
    }

    override fun createObserver() {
        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            titleBarBinding.tvMoney.text = it.getFormalMoney()
        }
    }
}