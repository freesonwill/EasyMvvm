package com.cn.game.sdk2.ui.view.game

import android.content.Context
import android.util.AttributeSet
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.FragmentGamehallBinding
import com.cn.game.sdk2.ui.adapter.GameListViewPagerAdapter
import com.cn.game.sdk2.ui.page.fast3.Fast3GameHallItemFragment
import com.cn.game.sdk2.ui.popup.game.MoreListPopup
import com.cn.game.sdk2.ui.view.BounceRVEdgeEffectFactory
import com.cn.game.sdk2.utils.ext.CommonExt.getString
import com.cn.game.sdk2.utils.ext.bindTabNewGame
import com.cn.game.sdk2.utils.ext.setOverScrollModeExt
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.websocket.gameAboutModel
import com.lxj.xpopup.core.BottomPopupView
import com.xcjh.base_lib2.utils.view.clickNoRepeat
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

class GameListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val listener: MoreListPopup.OnMoreListPopupListener? = null
) : BottomPopupView(context) {

    override fun getImplLayoutId(): Int =
        R.layout.fragment_gamehall

    private lateinit var binding: FragmentGamehallBinding
    var targetHeight: Int = 0
    private val tabTitles = listOf(
        R.string.g_game_list_type_hot.getString(),
        R.string.g_game_list_type_board_game.getString(),
        R.string.g_game_list_type_live_video.getString(),
        R.string.g_game_list_type_fishing.getString(),
        R.string.g_game_list_type_sports.getString(),
        R.string.g_game_list_type_electronic.getString()
    )
    private val fragmentList = mutableListOf<Fast3GameHallItemFragment>()

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
        val gameTypes = gameAboutModel.moreGames.value?.map { it.gameType }?.distinct() ?: listOf()
        gameTypes.forEach { gameType ->
            val fragment = Fast3GameHallItemFragment.newInstance(gameType)
            fragmentList.add(fragment)
        }
        binding.vpGameList.adapter =
            listener?.let {
                GameListViewPagerAdapter(
                    it.getFragmentManager(),
                    lifecycle,
                    fragmentList
                )
            }
        binding.vpGameList.setOverScrollModeExt(OVER_SCROLL_IF_CONTENT_SCROLLS,OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL)
        binding.tlGameList.bindTabNewGame(
            viewPager = binding.vpGameList,
            titles = tabTitles,
            scrollEnable = true
        ) {
            PromptSoundPlay.btnPlayMedia()
        }
        binding.vpGameList.offscreenPageLimit = tabTitles.size
        binding.close.clickNoRepeat(true) {
            dismiss()
        }
    }

    override fun onDismiss() {
        super.onDismiss()
        cleanUpFragments()
    }

    private fun cleanUpFragments() {
        binding.vpGameList.adapter = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cleanUpFragments()
    }
}