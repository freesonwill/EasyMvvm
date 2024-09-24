package com.cn.game.sdk2.ui.view.game

import android.content.Context
import android.util.AttributeSet
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.data.bean.PagerBean
import com.cn.game.sdk2.databinding.FragmentGamehallBinding
import com.cn.game.sdk2.ui.adapter.GameListViewPagerAdapter
import com.cn.game.sdk2.ui.helper.AnimHelper
import com.cn.game.sdk2.ui.helper.ToastHelper
import com.cn.game.sdk2.ui.page.fast3.Fast3GameHallItemFragment
import com.cn.game.sdk2.ui.xpopup.CustomPopupView
import com.cn.game.sdk2.utils.ext.CommonExt.getString
import com.cn.game.sdk2.utils.ext.bindTabNewGame
import com.cn.game.sdk2.utils.ext.removeAllTips
import com.cn.game.sdk2.utils.ext.setOverScrollModeExt
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.websocket.constants.GameStage
import com.cn.game.sdk2.websocket.gameAboutModel
import com.lxj.xpopup.core.BottomPopupView
import com.xcjh.base_lib2.utils.view.clickNoRepeat
import com.xcjh.base_lib2.utils.view.getString
import kotlinx.coroutines.Job
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

open class GameListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val fm: FragmentManager,
    private val miniGameId: Int?
) : CustomPopupView(context) {

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
//    private val fragmentList = mutableListOf<PagerBean>()//Fast3GameHallItemFragment>()
//    private lateinit var gameStageListener: Observer<GameStage>
    private val gameStageListener = object : Observer<GameStage> {
        override fun onChanged(t: GameStage) {
            // 因為gameAboutModel.currentStage裡面已經有資料，第一次observer就會trigger
            // 故用fragmentList作為初始化完成的依據，初始化完成後再監聽
            if (!::gameListAdapter.isInitialized) {
                return
            }
            when (t) {
                GameStage.NEW -> ToastHelper.instance.showHostToast(binding.lltRoot, getString(R.string.g_home_betting_begin))
                GameStage.SETTLE -> {}
                GameStage.DEAL -> ToastHelper.instance.showHostToast(binding.lltRoot, getString(R.string.g_home_betting_end))
            }
        }

    }
    private var switchTabAnimJob: Job? = null
    private lateinit var gameListAdapter: GameListViewPagerAdapter
    override fun onCreate() {
        gameAboutModel.currentStage.observeForever(gameStageListener)
        super.onCreate()
        binding = FragmentGamehallBinding.bind(popupImplView)
        binding.lltRoot.layoutParams.also {
            it.height = targetHeight
            binding.lltRoot.layoutParams = it
        }
        initView()
        initData()
    }

    private fun initView() {
        val gameTypes = gameAboutModel.moreGames.value?.map { it.gameType }?.distinct() ?: listOf()
        gameListAdapter = GameListViewPagerAdapter(fm, lifecycle, gameTypes)
        binding.vpGameList.adapter = gameListAdapter
        binding.vpGameList.setOverScrollModeExt(OVER_SCROLL_IF_CONTENT_SCROLLS,OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL)
        binding.tlGameList.bindTabNewGame(
            viewPager = binding.vpGameList,
            titles = tabTitles,
            scrollEnable = true
        ) {
            PromptSoundPlay.btnPlayMedia()
            switchTabAnimJob?.cancel()
            switchTabAnimJob = AnimHelper.doDirectViewPagerAnim(
                targetPosition = it,
                viewPager = binding.vpGameList,
                fakeViewPager = binding.fcvFakeViewPager

            )
        }
        binding.tlGameList.removeAllTips()
        binding.vpGameList.offscreenPageLimit = gameTypes.size
        binding.close.clickNoRepeat(true) {
            dismiss()
        }
    }

    private fun initData(){
        switchPage(this.miniGameId,false)
    }

    /**
     * 切换到游戏所在页面
     * @param miniGameId 游戏ID
     * @param smoothScroll 是否丝滑滚动
     */
    fun switchPage(miniGameId:Int?,smoothScroll:Boolean = true) {
        if(miniGameId == null) return
        gameAboutModel.moreGames.value?.let { list->
            val item = list.find { it.idp == miniGameId }
            if(item == null) return@let
            val page = GameHallItem.gameType2Index(item.gameType)
            binding.vpGameList.setCurrentItem(page,smoothScroll)
        }
    }

    override fun onDismiss() {
        gameAboutModel.currentStage.removeObserver(gameStageListener)
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