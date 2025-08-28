package arch.cayenne.module.betslip.ui.fragment

import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.utils.copyToClipboard
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.lib.database.entity.OrderSelectionBean
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.LoadDataType
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipConfirmBinding
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.ui.viewmodel.ConfirmingSlipViewModel
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.betSlipInit
import kotlin.reflect.KClass


//注单确认‰‰
class BetSlipConfirmFragment : BaseBetSlipFragment<ConfirmingSlipViewModel, FragmentLiveBetslipConfirmBinding>() {

    override val vbClass: KClass<FragmentLiveBetslipConfirmBinding> =
        FragmentLiveBetslipConfirmBinding::class
    override val vmClass: KClass<ConfirmingSlipViewModel> = ConfirmingSlipViewModel::class
    override val betSlipAdapter: BetSlipAdapter by lazy {
        BetSlipAdapter(getBetSlipEnum())
    }
    private var canLoadMore = false

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
    }

    private fun initRecycler() {
        betSlipAdapter.setLiveListener(object : BetSlipAdapter.BetSlipLiveListener {
            override fun isShowLiveButton(): Boolean {
                return settingViewModel.isBetSlipDetail
            }
            override fun onLiveButtonClick(data: BetSlipSelectionData) {
                if (data is OrderSelectionBean) {
                    val matchId = data.matchBasic.matchId
                    val sportId = data.matchBasic.sportId
                    navigate(Uri.parse("walisport://module_live/liveFragment?matchId=${matchId}&sportId=${sportId}"))
                }
            }
        })
        betSlipAdapter.setBetSlipListener(object : BetSlipAdapter.BetSlipListener {
            override fun onCopyClip(number: String) {
                copyToClipboard(number) {
                    showToast(getString(arch.cayenne.lib.common.R.string.copy_to_clip))
                }
            }
        })
        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = betSlipAdapter
            it.betSlipInit()
        }
        //滑动到底部之前进行提前预加载
        mBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val lastItemPos = layoutManager!!.findLastCompletelyVisibleItemPosition()
                if (lastItemPos > betSlipAdapter.itemCount - 4 && canLoadMore) {
                    canLoadMore = false
                    mViewModel.loadMoreData(BetSlipEnum.Confirming)
                }
            }
        })
        initLoadRefresh()
    }

    private fun initLoadRefresh() {
        mBinding.refreshLayout.also {
            it.setOnRefreshListener {
                mViewModel.refreshData(BetSlipEnum.Confirming)
            }
            it.setOnLoadMoreListener {
                mViewModel.loadMoreData(BetSlipEnum.Confirming)
            }
        }
    }


    override fun initListener() {
    }

    override suspend fun createObserver() {
        super.createObserver()
        mViewModel.orderLiveData.observe(viewLifecycleOwner) {
            canLoadMore = true
            betSlipAdapter.submitList(it){
                val position = if (mViewModel.loadDataType == LoadDataType.LOAD_MORE) betSlipAdapter.itemCount - 1 else 0
                mBinding.recyclerView.scrollToPosition(position)
                mViewModel.loadDataType = LoadDataType.NONE
            }
        }
    }

    override fun getBetSlipEnum(): BetSlipEnum {
        return BetSlipEnum.Confirming
    }
}