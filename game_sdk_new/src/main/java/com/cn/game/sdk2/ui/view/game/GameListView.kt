package com.cn.game.sdk2.ui.view.game

import android.content.Context
import androidx.core.view.doOnDetach
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.databinding.FragmentGamehallBinding
import com.cn.game.sdk2.databinding.ItemGamehallPageBinding
import com.cn.game.sdk2.ui.adapter.GameListAdapter
import com.cn.game.sdk2.ui.helper.ViewHelper.bindViewPagerNewGame
import com.cn.game.sdk2.ui.helper.ViewHelper.initGameViewPager2
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.gameAboutModel
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.dividerSpace
import com.google.gson.Gson
import com.lxj.xpopup.core.BottomPopupView
import com.xcjh.base_lib2.utils.LogUtils
import com.xcjh.base_lib2.utils.layoutInflater
import com.xcjh.base_lib2.utils.view.clickNoRepeat

class GameListView(context: Context) : BottomPopupView(context) {

    override fun getImplLayoutId(): Int =
        R.layout.fragment_gamehall

    private lateinit var binding: FragmentGamehallBinding
    private val adapter = GameListAdapter()
    var targetHeight: Int = 0

    private var onItemClickListener:(item: GameHallItem) -> Unit = {
        val dataStr = Gson().toJson(it)
        LogUtils.d("onItemClick-->$dataStr")
        appListener?.onClickOtherGameWithBlock(dataStr)
    }

    override fun onCreate() {
        super.onCreate()
        binding = FragmentGamehallBinding.bind(popupImplView)
        binding.lltRoot.layoutParams.also {
            it.height = targetHeight
            binding.lltRoot.layoutParams = it
        }
        initView()
        createObserver()
    }

    private fun initView() {
        val gameHallList = mutableListOf<GameHallItem>().also { list->
            gameAboutModel.moreGames.value?.let { games->
                list.addAll(games)
            }
        }
        val mViewBind = ItemGamehallPageBinding.inflate(
                context.layoutInflater!!,
                null,
                false
            )
        mViewBind.rvContent.itemAnimator = null
        mViewBind.rvContent.dividerSpace(
            context.dp2px(20),
            DividerOrientation.HORIZONTAL
        )
        mViewBind.rvContent.layoutManager = GridLayoutManager(context, 4)
        mViewBind.rvContent.adapter = adapter
        adapter.onItemClickListener = onItemClickListener
        adapter.submitList(gameHallList)
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

    /**
     * 注册数据监听
     */
    private fun createObserver(){
        Observer<List<GameHallItem>> {
            adapter.submitList(it)
            adapter.notifyItemRangeChanged(0,it.size)
        }.apply {
            gameAboutModel.moreGames.observeForever(this)
            //view销毁时移除observer
            doOnDetach { gameAboutModel.moreGames.removeObserver(this) }
        }
    }
}