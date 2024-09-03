package com.cn.game.sdk2.ui.view.game

import android.content.Context
import android.util.SparseArray
import android.view.View
import androidx.core.util.forEach
import androidx.core.view.doOnDetach
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.SortedList
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.databinding.FragmentGamehallBinding
import com.cn.game.sdk2.databinding.ItemGamehallPageBinding
import com.cn.game.sdk2.ui.adapter.GameListAdapter
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.utils.ext.bindViewPagerNewGame
import com.cn.game.sdk2.utils.ext.initGameViewPager2
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections

class GameListView(context: Context) : BottomPopupView(context) {

    override fun getImplLayoutId(): Int =
        R.layout.fragment_gamehall

    private lateinit var binding: FragmentGamehallBinding
    var targetHeight: Int = 0
    private val tabs = listOf("热门", "棋牌", "视讯", "捕鱼", "体育", "电子")
    private val tabLists = SparseArray<ArrayList<GameHallItem>>().apply {
        for (i in tabs.indices) {
            put(i, ArrayList())
        }
    }
    private val adapters = mutableListOf<GameListAdapter>()
    private var onItemClickListener: (item: GameHallItem) -> Unit = {
        val dataStr = Gson().toJson(it)
        LogUtils.d("onItemClick-->$dataStr")
        appListener?.onClickOtherGameWithBlock(dataStr)
        dismiss()
    }

    override fun onCreate() {
        super.onCreate()
        lifecycleScope.launch {
            binding = FragmentGamehallBinding.bind(popupImplView)
            binding.lltRoot.layoutParams.also {
                it.height = targetHeight
                binding.lltRoot.layoutParams = it
            }
            initView()
            createObserver()
        }
    }

    private suspend fun initView() {
        //获取游戏列表
        withContext(Dispatchers.IO) {
            val games = gameAboutModel.moreGames.value ?: listOf()
            games.forEach {
                val list = tabLists.get(it.gameType, null) ?: return@forEach
                list.add(it)
            }
        }
        //set up viewPager
        val views = ArrayList<View>()
        repeat(tabs.size) { index ->
            val gameHallList = tabLists[index]
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
            val adapter = GameListAdapter().also {
                it.onItemClickListener = onItemClickListener
                it.submitList(gameHallList)
            }
            mViewBind.rvContent.adapter = adapter
            views.add(mViewBind.root)
            adapters.add(adapter)
        }
        //viewPager与indicator相互绑定
        val pages = tabs
        binding.viewPagerNew.initGameViewPager2(views)
        binding.magicIndicator.bindViewPagerNewGame(
            binding.viewPagerNew,
            pages,
            scrollEnable = true,
            action = { PromptSoundPlay.btnPlayMedia() }
        )
        binding.viewPagerNew.offscreenPageLimit = pages.size
        //set click
        binding.close.clickNoRepeat(true) {
            dismiss()
        }
    }

    /**
     * 注册数据监听
     */
    private suspend fun createObserver() {
        Observer<List<GameHallItem>> {
            val games = gameAboutModel.moreGames.value ?: listOf()
            tabLists.forEach { _, value ->
                value.clear()
            }
            games.forEach {
                val list = tabLists.get(it.gameType, null) ?: return@forEach
                list.add(it)
            }
            adapters.forEach { it.notifyItemRangeChanged(0,adapters.size) }
        }.apply {
            gameAboutModel.moreGames.observeForever(this)
            //view销毁时移除observer
            doOnDetach { gameAboutModel.moreGames.removeObserver(this) }
        }
    }
}