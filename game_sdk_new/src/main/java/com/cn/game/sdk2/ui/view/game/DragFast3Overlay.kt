package com.cn.game.sdk2.ui.view.game

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.cn.game.sdk2.databinding.FragmentFast3OverlayBinding
import com.cn.game.sdk2.utils.ext.CommonExt.toPinyin
import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import com.cn.game.sdk2.websocket.constants.GameStage
import com.cn.game.sdk2.websocket.gameAboutModel
import com.xcjh.base_lib2.utils.LogUtils

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/27 13:54
 **/
class DragFast3Overlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val TAG = javaClass.simpleName
    private val gcFunc = mutableListOf<() -> Unit>()
    private lateinit var binding: FragmentFast3OverlayBinding

    private fun onInit() {
        LogUtils.dTag(TAG, "onInit~~~~~~~~~~")
        binding = FragmentFast3OverlayBinding.bind(this)

        gameAboutModel.historyRounds.apply {
            observeForever(object : Observer<List<*>> {
                init { gcFunc.add { removeObserver(this) } }
                override fun onChanged(t: List<*>?){
                    updateUI()
                }
            })
        }
        gameAboutModel.currentStage.apply {
            observeForever(object : Observer<GameStage> {
                init {
                    gcFunc.add { removeObserver(this) }
                }

                override fun onChanged(t: GameStage?) {
                    updateUI()
                }
            })
        }
        updateUI()
        //test()
    }

    private fun updateUI() {
        val roundInfo: RoundInfoBean? = gameAboutModel.currentSettleResult
        LogUtils.dTag(TAG,"updateUI: roundInfo:${roundInfo}")
        binding.apply {
            lltResult.visibility = if(roundInfo != null) View.VISIBLE else View.INVISIBLE
            roundInfo?.run {
                performs.forEachIndexed { index, item ->
                    val id = resources.getIdentifier(
                        "game_sdk_icon_dice_" + item.toPinyin(),
                        "mipmap",
                        context.packageName
                    )
                    when (index) {
                        0 -> ivDrawYi.setImageResource(id)
                        1 -> ivDrawEr.setImageResource(id)
                        2 -> ivDrawSan.setImageResource(id)
                    }
                }
                tvEdition.text = roundId
            }
        }
    }

    /*private fun test() {
        setOnClickListener {
            gameAboutModel.currentSettleResult = RoundInfoBean(
                "1",
                listOf(4, 2, 3), 6,
                isBig = true,
                isDouble = true
            )
            ((gameAboutModel.historyRounds) as MutableLiveData).value = null
            gameAboutModel.roundId = "12123"
            (gameAboutModel.currentStage as UnPeekLiveData).value = Stage.NEW
        }
    }*/

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.onInit()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        gcFunc.forEach { it() }
    }
}