package arch.cayenne.module.home.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.bet.data.AddSelectionStatus
import arch.cayenne.module.bet.ui.fragment.BetSheetFragment
import arch.cayenne.module.bet.viewmodel.FloatingButtonControlViewModel
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentChampionBinding
import arch.cayenne.module.home.databinding.TitleBarChampionBinding
import arch.cayenne.module.home.ui.adapter.ChampionItemAdapter
import arch.cayenne.module.home.ui.adapter.OnChampionItemClickListener
import arch.cayenne.module.home.ui.view.decoration.MatchCardItemDecoration
import arch.cayenne.module.home.ui.viewmodel.ChampionViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.reflect.KClass

class ChampionFragment : BaseFragment<ChampionViewModel, FragmentChampionBinding>() {

    override val vbClass: KClass<FragmentChampionBinding> = FragmentChampionBinding::class
    override val vmClass: KClass<ChampionViewModel> = ChampionViewModel::class
    private val args: ChampionFragmentArgs by navArgs()
    private lateinit var championAdapter: ChampionItemAdapter
    private val fabViewModel: FloatingButtonControlViewModel by activityViewModel()

    private val tittleBarBinding: TitleBarChampionBinding by lazy {
        TitleBarChampionBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }

    override fun initData() {
        super.initData()
        mViewModel.setMatchId(args.matchId)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (enter && nextAnim != 0) {
            val animation = AnimationUtils.loadAnimation(requireContext(), nextAnim)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    mViewModel.subscribeMatch()
                    mViewModel.getChampionDetail()
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
            animation
        } else {
            super.onCreateAnimation(transit, enter, nextAnim)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {
            titleBar.loadDynamicsTitleBar(tittleBarBinding.root) {
                findNavController().navigateUp()
            }
            tittleBarBinding.apply {
                Glide.with(this@ChampionFragment)
                    .load(args.icon)
                    .error(R.drawable.title_league_icon)
                    .into(tittleBarBinding.ivLandscapeLeagueIcon)
                tittleBarBinding.tvCompetitionName.text = args.name
            }
            rvChampion.itemAnimator = null
            rvChampion.apply {
                championAdapter = ChampionItemAdapter(object : OnChampionItemClickListener {
                    override fun onOddsCellClick(selection: SelectionBeanLite) {
                        lifecycleScope.launch {
                            val status = mViewModel.setSelection(selection.selectionId)

                            if (status is AddSelectionStatus.Success.Single) {
                                BetSheetFragment.show(requireActivity())
                            } else if (status is AddSelectionStatus.Failure) {
                                status.msg?.let {
                                    showToast(it)
                                }
                            } else if (status is AddSelectionStatus.Success.Combo || status is AddSelectionStatus.Success.Update) {
                                fabViewModel.setClickAnimation(x, y)
                            }
                        }
                    }
                })

                this.adapter = championAdapter
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(MatchCardItemDecoration(12.dp2px))
            }
        }

    }

    override fun initListener() {
        tittleBarBinding.llWalletEntry.setOnClickListener {
            navigate(Uri.parse("walisport://module_topup/topUpFragment"))
        }
    }

    override suspend fun createObserver() {
        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            tittleBarBinding.tvMoney.text =
                getString(R.string.balance_format, CurrencySymbols.getSymbol(it?.currency?:""), (it?.balance?:0L).getFormalMoney())
        }
        mViewModel.matchWithMarketsChange.observe(viewLifecycleOwner) { matchWithMarkets ->
            with(mBinding) {
                if (matchWithMarkets != null && matchWithMarkets.markets.isNotEmpty()) {
                    clDynamics.visibility = View.GONE
                    championAdapter.submitList(matchWithMarkets.markets)
                } else {
                    clDynamics.visibility = View.VISIBLE
                    clDynamics.setState(
                        DynamicStateLayout.States.DATA_EMPTY,
                        R.string.lineup_empty.getString()
                    )
                }
            }
        }
        mViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!isLoading) {
                mBinding.loadingView.visibility = View.GONE
            } else {
                mBinding.loadingView.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        mViewModel.cancelSubscribeMatch()
        super.onDestroyView()
    }

}