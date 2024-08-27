package com.cn.game.sdk2.ui.view.game

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.databinding.FragmentGamehallBinding
import com.cn.game.sdk2.databinding.ItemGamehallPageBinding
import com.cn.game.sdk2.databinding.ItemGamehallPageItemBinding
import com.cn.game.sdk2.ui.helper.ViewHelper.bindViewPagerNewGame
import com.cn.game.sdk2.ui.helper.ViewHelper.initGameViewPager2
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.dividerSpace
import com.drake.brv.utils.setup
import com.lxj.xpopup.core.BottomPopupView
import com.xcjh.base_lib2.utils.layoutInflater
import com.xcjh.base_lib2.utils.view.clickNoRepeat

class GameListView(context: Context) : BottomPopupView(context) {

    override fun getImplLayoutId(): Int =
        R.layout.fragment_gamehall

    private lateinit var binding: FragmentGamehallBinding

    var targetHeight: Int = 0

    override fun onCreate() {
        super.onCreate()
        binding =
            FragmentGamehallBinding.bind(popupImplView)
        binding.lltRoot.layoutParams.also {
            it.height = targetHeight
            binding.lltRoot.layoutParams = it
        }
        initView()
    }

    private fun initView() {
        val item = GameHallItem(
            "a",
            "快三",
            "3389在线"
        )
        val mViewBind =
            ItemGamehallPageBinding.inflate(
                context.layoutInflater!!,
                null,
                false
            )
        mViewBind.rvContent.itemAnimator = null
        mViewBind.rvContent.dividerSpace(
            context.dp2px(20),
            DividerOrientation.HORIZONTAL
        ).setup {
            it.layoutManager = GridLayoutManager(context, 4)
            addType<GameHallItem>(R.layout.item_gamehall_page_item)
            onBind {
                when (itemViewType) {
                    R.layout.item_gamehall_page_item -> {
                        getBinding<ItemGamehallPageItemBinding>().apply {
                            val bean =
                                _data as GameHallItem
                            tvName.text = bean.name
                            tvOnline.text =
                                bean.onlineA
                            if (bean.name == "快三") {
                                root.clickNoRepeat(
                                    true
                                ) {
                                    dismiss()
                                }
                            }
                        }
                    }
                }
            }
        }.models = listOf(item)
        val pages = listOf(
            "热门"
        )
        binding.viewPagerNew.initGameViewPager2(arrayListOf(mViewBind.root))
        binding.magicIndicator.bindViewPagerNewGame(
            binding.viewPagerNew,
            pages,
            scrollEnable = true,
            action = { PromptSoundPlay.btnPlayMedia() }
        )
        binding.viewPagerNew.offscreenPageLimit = pages.size
        binding.close.clickNoRepeat(true) {
            dismiss()
        }
    }

}