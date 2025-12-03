package com.walisport.module.gamedetail.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigateUp
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.locationOnScreen
import com.bumptech.glide.Glide
import com.walisport.module.gamedetail.R
import com.walisport.module.gamedetail.data.model.CurrencyInfoBean
import com.walisport.module.gamedetail.data.model.GameDetailBean
import com.walisport.module.gamedetail.data.model.GamePreviewBean
import com.walisport.module.gamedetail.data.model.PlayerRankingBean
import com.walisport.module.gamedetail.data.model.PreviewType
import com.walisport.module.gamedetail.databinding.FragmentGameDetailBinding
import com.walisport.module.gamedetail.ui.adapter.GamePreviewAdapter
import com.walisport.module.gamedetail.ui.viewmodel.GameDetailViewModel
import java.util.Locale
import kotlin.reflect.KClass

class GameDetailFragment: BaseFragment<GameDetailViewModel, FragmentGameDetailBinding>() {
    override val vbClass: KClass<FragmentGameDetailBinding> = FragmentGameDetailBinding::class
    override val vmClass: KClass<GameDetailViewModel> = GameDetailViewModel::class

    private val previewAdapter by lazy {
        GamePreviewAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(autoPadding = true,
            noPaddingViewIds = listOf(R.id.carousel_scroll_view)
        )
        setStatusBar(StatusBarConfig, mBinding.root)

        with(mBinding) {
            viewBalance.init(childFragmentManager)

            val mockData = GameDetailBean(
                id = 1,
                name = "Slot Game 1",
                type = 1,
                supplier = "Supplier A",
                avatar = "https://example.com/game1.png",
                reward = 0.95,
                maxOdds = 1000,
                online = 1234,
                score = 4.5,
                comments = 150,
                tryIt = true,
                hasMore = true,
                collect = false,
                materials = true,
                currency = emptyList()
            )

            val previewMock = listOf(
//                GamePreviewBean(PreviewType.VIDEO, "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_1MB.mp4"),
                GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
                GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
                GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
                GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
                GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
                GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
                GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
                GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
                GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
                GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
                GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
                GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg")
            )

            // todo 串接資料
            tvCurrencySymbol.text = "¥"
            tvCurrencyName.text = "人民币"

            Glide.with(root.context).load(mockData.avatar).placeholder(R.mipmap.img_game_cover).into(ivGameCover)
            tvGameName.text = mockData.name
            // todo type enum待確認
            tvGameType.text = mockData.type.toString()
            tvGameVendor.text = mockData.supplier
            tvRtpValue.text = String.format(Locale.getDefault(), "%.1f%%", mockData.reward * 100)
            tvMaxRewardValue.text = String.format(Locale.getDefault(), "%dx", mockData.maxOdds)
            tvOnlineValue.text = mockData.online.toString()
            tvScore.text = mockData.score.toString()
            tvScoreCountValue.text = mockData.comments.toString()
            // todo 效果待確認
            ivFavorite.isSelected = mockData.collect
            ivStartTrial.visibility = if (mockData.tryIt) View.VISIBLE else View.GONE
            groupMoreGame.visibility = if (mockData.hasMore) View.VISIBLE else View.GONE

            carouselScrollView.apply {
                adapter = previewAdapter.apply {
                    setData(previewMock)
                }

                doOnLayout {
                    gotoPage(
                        pageIndex = 0,
                        magnifyImmediately = true
                    )
                }
            }
        }
    }

    override fun initListener() {
        with(mBinding) {
            ivBack.clickNoRepeat {
                navigateUp()
            }
            ivFavorite.clickNoRepeat {
                it.isSelected = !it.isSelected
            }
            ivStartTrial.clickNoRepeat {
            }
            tvBiggestWinner.clickNoRepeat (){
                closeExistingLuckyFragment()
                closeExistingCurrencyFragment()
                it.togglePlayerRankingFragment()
            }
            tvLuckiestWinner.clickNoRepeat() {
                closeExistingRankingFragment()
                closeExistingCurrencyFragment()
                it.toggleLuckFragment()
            }
            viewCurrencyBg.clickNoRepeat() {
                closeExistingRankingFragment()
                closeExistingLuckyFragment()
                it.toggleCurrencyFragment()
            }
        }
    }

    override suspend fun createObserver() {
    }

    override fun onStart() {
        super.onStart()
    }

    private fun closeExistingRankingFragment(afterClose: ((isSuccess: Boolean) -> Unit)? = null) {
        var isSuccess = false
        (childFragmentManager.findFragmentByTag(PlayerRankingFragment.TAG) as? PlayerRankingFragment).let {
            if(it?.isAdded == true) {
                it.close()
                childFragmentManager.executePendingTransactions()
                isSuccess = true
            }
            afterClose?.invoke(isSuccess)
        }
    }


    private fun closeExistingLuckyFragment(afterClose: ((isSuccess: Boolean) -> Unit)? = null) {
        var isSuccess = false
        (childFragmentManager.findFragmentByTag(LuckyFragment.TAG) as? LuckyFragment).let {
            if(it?.isAdded == true) {
                it.close()
                childFragmentManager.executePendingTransactions()
                isSuccess = true
            }
            afterClose?.invoke(isSuccess)
        }
    }

    private fun View.createRankingFragment() {
        with(mBinding) {
            PlayerRankingFragment.Builder().apply {
                val screenHeight = resources.displayMetrics.heightPixels
                val viewY = locationOnScreen[1]
                LogUtils.e("---->${screenHeight},${viewY}")
                setMarginBottom(screenHeight - viewY + 6.dp2px)
                setTouchThroughViews(listOf(tvBiggestWinner, tvLuckiestWinner, viewCurrencyBg))
                setOnDismissListener { unSelectButtons() }
                // todo 介接資料
                setRankingDatas(
                    listOf(
                        PlayerRankingBean(1, "美美eee桑内", 1, 123323, 240000, 33, 242),
                        PlayerRankingBean(2, "ff", 2, 5000, 12240000, 1000000009, 234),
                        PlayerRankingBean(3, "ffdfdsfs", 3, 3000, 8002400, 777777, 176042427629980),
                        PlayerRankingBean(4, "王sfdfs柏融", 4, 2000, 6024000, 662426666, 176242420427629980),
                        PlayerRankingBean(5, "sdfds", 5, 1000, 40000, 55245555, 1760427629980),
                        PlayerRankingBean(6, "df", 6, 800, 30000, 44442444, 1760427629980),
                        PlayerRankingBean(7, "松井秀喜", 7, 600, 2002400, 32433333, 1760427629980),
                        PlayerRankingBean(8, "清原和博", 8, 400, 10000, 222222, 1760427629980),
                        PlayerRankingBean(9, "佐佐木主浩", 9, 200, 5000, 111111, 1760427629980),
                        PlayerRankingBean(10, "田中將大", 10, 100, 3000, 101010, 1760427629980)
                    )
                )
            }.build().show(childFragmentManager, clRoot.id)
        }
        this.isSelected = true
    }


    private fun View.createLuckyFragment() {
        with(mBinding) {
            LuckyFragment.Builder().apply {
                val screenHeight = resources.displayMetrics.heightPixels
                val viewY = locationOnScreen[1]
                LogUtils.e("createFragment---->${screenHeight},${viewY}")
                setMarginBottom(screenHeight - viewY + 6.dp2px)
                setTouchThroughViews(listOf(tvBiggestWinner, tvLuckiestWinner, viewCurrencyBg))
                setOnDismissListener { unSelectButtons() }
                // todo 介接資料
                setRankingDatas(
                    listOf(
                        PlayerRankingBean(1, "美美桑内", 1, 100000000000000, 240000, 999999999999999, 1760427629980),
                        PlayerRankingBean(2, "小林同學", 2, 5000, 120000, 1000000009, 1760427629980),
                        PlayerRankingBean(3, "大谷翔平", 3, 3000, 80000, 777777, 1760427629980),
                        PlayerRankingBean(4, "王柏融", 4, 2000, 60000, 666666, 1760427629980),
                        PlayerRankingBean(5, "陽岱鋼", 5, 1000, 40000, 555555, 1760427629980),
                        PlayerRankingBean(6, "鈴木一朗", 6, 800, 30000, 444444, 1760427629980),
                        PlayerRankingBean(7, "松井秀喜", 7, 600, 20000, 333333, 1760427629980),
                        PlayerRankingBean(8, "清原和博", 8, 400, 10000, 222222, 1760427629980),
                        PlayerRankingBean(9, "佐佐木主浩", 9, 200, 5000, 111111, 1760427629980),
                        PlayerRankingBean(10, "田中將大", 10, 100, 3000, 101010, 1760427629980)
                    )
                )
            }.build().show(childFragmentManager, clRoot.id)
        }
        this.isSelected = true
    }


    private fun closeExistingCurrencyFragment(afterClose: ((isSuccess: Boolean) -> Unit)? = null) {
        var isSuccess = false
        (childFragmentManager.findFragmentByTag(CurrencySelectorFragment.TAG) as? CurrencySelectorFragment).let {
            if(it?.isAdded == true) {
                it.close()
                childFragmentManager.executePendingTransactions()
                isSuccess = true
            }
            afterClose?.invoke(isSuccess)
        }
    }

    private fun View.createCurrencyFragment() {
        with(mBinding) {
            CurrencySelectorFragment.Builder().apply {
                val screenHeight = resources.displayMetrics.heightPixels
                val viewY = locationOnScreen[1]
                LogUtils.e("createFragment---->${screenHeight},${viewY}")
                setMarginBottom(screenHeight - viewY + 6.dp2px)
                setTouchThroughViews(listOf(tvBiggestWinner, tvLuckiestWinner, viewCurrencyBg))
                // todo 介接資料
                setCurrencyDatas(
                    listOf(
                        CurrencyInfoBean(1, true, 1.1, null, "USDT"),
                        CurrencyInfoBean(2, true, 1.2, null, "BTC"),
                        CurrencyInfoBean(3, true, 1.2, null, "RMB"),
                        CurrencyInfoBean(4, true, 1.2, null, "欧元"),
                        CurrencyInfoBean(5, true, 1.2, null, "日元"),
                    )
                )
                setSelectedCurrencyId(1)
                setOnDismissListener { unSelectButtons() }
            }.build().show(childFragmentManager, mBinding.clRoot.id)
        }
        this.isSelected = true
    }

    private fun View.toggleCurrencyFragment() {
        closeExistingCurrencyFragment { isSuccess ->
            if(!isSuccess) createCurrencyFragment()
        }
    }

    private fun View.toggleLuckFragment() {
        closeExistingLuckyFragment{ isSuccess ->
            if(!isSuccess) createLuckyFragment()
        }
    }

    private fun View.togglePlayerRankingFragment() {
        closeExistingRankingFragment { isSuccess ->
            if(!isSuccess) createRankingFragment()
        }
    }

    private fun unSelectButtons() {
        with(mBinding) {
            listOf(tvBiggestWinner, tvLuckiestWinner, viewCurrencyBg).forEach {
                it.isSelected = false
            }
        }
    }
}