package com.cn.game.sdk2.ui.view.game

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.DragFastEasyBinding
import com.cn.game.sdk2.utils.CommonUtils
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.viewmodel.GameAboutModel.Stage
import com.xcjh.base_lib2.utils.LogUtils

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/27 13:54
 **/
class DragFastEasy @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val TAG = javaClass.simpleName
    private val gcFunc = mutableListOf<() -> Unit>()
    private lateinit var binding: DragFastEasyBinding

    private fun onInit() {
        LogUtils.dTag(TAG, "onInit~~~~~~~~~~")
        binding = DragFastEasyBinding.bind(this)

        gameAboutModel.countDownSecondsLD.apply {
            observeForever(object : Observer<Int> {
                init {
                    gcFunc.add { removeObserver(this) }
                }

                override fun onChanged(t: Int) {
                    updateUI()
                }
            })
        }
        gameAboutModel.currentStage.apply {
            observeForever(object : Observer<Stage> {
                init {
                    gcFunc.add { removeObserver(this) }
                }

                override fun onChanged(t: Stage) {
                    updateUI()
                }
            })
        }
        updateUI()
    }

    private fun updateUI() {
        val stage = gameAboutModel.currentStage.value
        when (stage) {
            Stage.NEW -> {
                val time = gameAboutModel.countDownSecondsLD.value ?: 0
                binding.txtTime.text = CommonUtils.formatSeconds(time)
            }

            Stage.DEAL -> {
                binding.txtTime.text = context.getString(R.string.g_f3_dealing)
            }

            Stage.SETTLE -> {
                binding.txtTime.text = context.getString(R.string.g_f3_setting)
            }

            else -> {}
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.onInit()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        gcFunc.forEach { it() }
    }
}