package com.walisport.module.gamedetail.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.bumptech.glide.Glide
import com.walisport.module.gamedetail.R
import com.walisport.module.gamedetail.data.model.GameDetailBean
import com.walisport.module.gamedetail.databinding.FragmentGameDetailBinding
import com.walisport.module.gamedetail.ui.viewmodel.GameDetailViewModel
import java.util.Locale
import kotlin.reflect.KClass

class GameDetailFragment: BaseFragment<GameDetailViewModel, FragmentGameDetailBinding>() {
    override val vbClass: KClass<FragmentGameDetailBinding> = FragmentGameDetailBinding::class
    override val vmClass: KClass<GameDetailViewModel> = GameDetailViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
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
        }
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }

    override fun onStart() {
        super.onStart()
        StatusBarConfig.statusBarType = StatusBarMode.FULLSCREEN
        setStatusBar(StatusBarConfig, mBinding.root)
    }
}