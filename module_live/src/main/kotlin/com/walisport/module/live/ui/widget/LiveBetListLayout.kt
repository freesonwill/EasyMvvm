package com.walisport.module.live.ui.widget

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import arch.cayenne.lib.common.utils.ext.clickNoRepeatSingle
import arch.cayenne.lib.skin.widget.SkinnableLinearLayout
import com.walisport.module.live.data.LiveOddsStatusEnum
import com.walisport.module.live.data.constants.StatesArrange
import com.walisport.module.live.databinding.LiveBetContentItemLayoutOneBinding
import com.walisport.module.live.databinding.LiveBetContentItemLayoutThreeBinding
import com.walisport.module.live.databinding.LiveBetContentItemLayoutTowBinding
import com.walisport.module.live.databinding.LiveBetContentListItemLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LiveBetListLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SkinnableLinearLayout(context, attrs, defStyleAttr) {

    private val binding = LiveBetContentListItemLayoutBinding.inflate(
        LayoutInflater.from(context), this, false
    )
    private var isNotify: Boolean = true
    private val handler = Handler(Looper.getMainLooper())

     fun viewInit() {
        removeAllViews()
        addView(binding.root)
    }

    fun submitList(
        state: StatesArrange?,
        num: Int,
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        isNotify: Boolean,
        isCombo: Boolean,
        callback: (Long, Float, Float) -> Unit
    ) {
        this.isNotify = isNotify
        if (num == 0) {
            clearAllArranges()
        }

        when (state) {
            StatesArrange.DEFAULT_ARRANGE -> addToDefaultArrange(num, name, odds, marketId, active, oddStatus, isCombo, callback)
            StatesArrange.ONE_ARRANGE -> addToOneArrange(name, odds, marketId, active, oddStatus, isCombo, callback)
            StatesArrange.TOW_ARRANGE -> addToTowArrange(num, name, odds, marketId, active, oddStatus, isCombo, callback)
            StatesArrange.THREE_ARRANGE -> addToThreeArrange(num, name, odds, marketId, active, oddStatus, isCombo, callback)
            StatesArrange.BO_DIAN -> addToBoDian( name, odds, marketId, active, oddStatus, isCombo, callback)
            null -> Unit // Handle null state gracefully
        }
    }

    private fun clearAllArranges() {
        with(binding) {
            llcOneArrange.removeAllViews()
            llcTowArrange.removeAllViews()
            llcThreeArrange.removeAllViews()
            llcOther.removeAllViews()
        }
    }

    private fun addToDefaultArrange(
        num: Int,
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        isCombo: Boolean,
        callback: (Long, Float, Float) -> Unit
    ) {
        when (num % StatesArrange.DEFAULT_ARRANGE.value) {
            0 -> binding.llcOneArrange.addView(createViewOne(name, odds, marketId, active, oddStatus, isCombo, callback))
            1 -> binding.llcTowArrange.addView(createViewTow(name, odds, marketId, active, oddStatus, isCombo, callback))
            else ->{}
        }
        binding.llcTowArrange.isVisible = true
        binding.llcThreeArrange.isVisible = false
    }

    private fun addToOneArrange(
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        isCombo: Boolean,
        callback: (Long, Float, Float) -> Unit
    ) {
        binding.llcOneArrange.addView(createViewOne(name, odds, marketId, active, oddStatus, isCombo, callback))
        binding.llcTowArrange.isVisible = false
        binding.llcThreeArrange.isVisible = false
    }

    private fun addToTowArrange(
        num: Int,
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        isCombo: Boolean,
        callback: (Long, Float, Float) -> Unit
    ) {
        when (num % StatesArrange.TOW_ARRANGE.value) {
            0 -> binding.llcOneArrange.addView(createViewOne(name, odds, marketId, active, oddStatus, isCombo, callback))
            1 -> binding.llcTowArrange.addView(createViewTow(name, odds, marketId, active, oddStatus, isCombo, callback))
        }
        binding.llcTowArrange.isVisible = true
        binding.llcThreeArrange.isVisible = false
    }

    private fun addToThreeArrange(
        num: Int,
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        isCombo: Boolean,
        callback: (Long, Float, Float) -> Unit
    ) {
        when (num % StatesArrange.THREE_ARRANGE.value) {
            0 -> binding.llcOneArrange.addView(createViewOne(name, odds, marketId, active, oddStatus, isCombo, callback))
            1 -> binding.llcTowArrange.addView(createViewTow(name, odds, marketId, active, oddStatus, isCombo, callback))
            2 -> binding.llcThreeArrange.addView(createViewThree(name, odds, marketId, active, oddStatus, isCombo, callback))
        }
        binding.llcTowArrange.isVisible = true
        binding.llcThreeArrange.isVisible = true
    }

    private fun addToBoDian(
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        isCombo: Boolean,
        callback: (Long, Float, Float) -> Unit
    ) {
        name.split("-").takeIf { it.size == 2 }?.let { parts ->
            val left = parts[0].toIntOrNull() ?: return
            val right = parts[1].toIntOrNull() ?: return
            when {
                left > right -> binding.llcOneArrange.addView(createViewOne(name, odds, marketId, active, oddStatus, isCombo, callback))
                left == right -> binding.llcTowArrange.apply {
                    isVisible = true
                    addView(createViewTow(name, odds, marketId, active, oddStatus, isCombo, callback))
                }
                left < right -> binding.llcThreeArrange.apply {
                    isVisible = true
                    addView(createViewThree(name, odds, marketId, active, oddStatus, isCombo, callback))
                }
                else ->{}
            }
        } ?: binding.llcOther.addView(createViewTow(name, odds, marketId, active, oddStatus, isCombo, callback))
    }

    private fun createViewOne(
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        isCombo: Boolean,
        callback: (Long, Float, Float) -> Unit
    ): View = LiveBetContentItemLayoutOneBinding.inflate(LayoutInflater.from(context), binding.root, false).run {
        setupView(this, name, odds, marketId, active, oddStatus, isCombo, callback, sclOne, sclOneLock)
        root
    }

    private fun createViewTow(
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        isCombo: Boolean,
        callback: (Long, Float, Float) -> Unit
    ): View = LiveBetContentItemLayoutTowBinding.inflate(LayoutInflater.from(context), binding.root, false).run {
        setupView(this, name, odds, marketId, active, oddStatus, isCombo, callback, sclTow, sclTowLock)
        root
    }

    private fun createViewThree(
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        isCombo: Boolean,
        callback: (Long, Float, Float) -> Unit
    ): View = LiveBetContentItemLayoutThreeBinding.inflate(LayoutInflater.from(context), binding.root, false).run {
        setupView(this, name, odds, marketId, active, oddStatus, isCombo, callback, sclThree, sclThreeLock)
        root
    }

    private fun <T : Any> setupView(
        binding: T,
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        isCombo: Boolean,
        callback: (Long, Float, Float) -> Unit,
        selectableView: View,
        lockView: View
    ) {
        with(binding) {
            when (this) {
                is LiveBetContentItemLayoutOneBinding -> {
                    if (active) isOddsStatus(oddStatus, imgTop, imgDown)
                    tvBetDuelLeft.text = name
                    tvBetDuelRight.text = odds
                    sclOne.isSelected = isCombo
                    sclOne.clickNoRepeatSingle {
                        handleClick(marketId, isCombo, callback, sclOne) }
                }
                is LiveBetContentItemLayoutTowBinding -> {
                    if (active) isOddsStatus(oddStatus, imgTop, imgDown)
                    tvBetDuelLeft.text = name
                    tvBetDuelRight.text = odds
                    sclTow.isSelected = isCombo
                    sclTow.clickNoRepeatSingle {
                        handleClick(marketId, isCombo, callback, sclTow) }
                }
                is LiveBetContentItemLayoutThreeBinding -> {
                    if (active) isOddsStatus(oddStatus, imgTop, imgDown)
                    tvBetDuelLeft.text = name
                    tvBetDuelRight.text = odds
                    sclThree.isSelected = isCombo
                    sclThree.clickNoRepeatSingle {
                        handleClick(marketId, isCombo, callback, sclThree) }
                }
                else ->{}
            }
            selectableView.isVisible = active
            lockView.isVisible = !active
        }
    }




    private fun handleClick(marketId: Long, isCombo: Boolean, callback: (Long, Float, Float) -> Unit, view: View) {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val x = location[0] + view.width / 2
        val y = location[1] + view.height / 2
        callback(marketId,x.toFloat(),y.toFloat())
        if (!isCombo) {
            view.isSelected = true
            delayExample { view.isSelected = false }
        }
    }

    private fun isOddsStatus(oddStatus: Int, top: View, down: View) {
        if (isNotify){
            when (oddStatus) {
                LiveOddsStatusEnum.UP.status -> {
                    down.isVisible = false
                    top.isVisible = true
                    handler.postDelayed({ top.isVisible = false }, 2000)
                }
                LiveOddsStatusEnum.DOWN.status -> {
                    down.isVisible = true
                    top.isVisible = false
                    handler.postDelayed({ down.isVisible = false }, 2000)
                }
                LiveOddsStatusEnum.SAME.status -> {
                    down.isVisible = false
                    top.isVisible = false
                }
                else ->{}
            }
        }
    }

    private fun delayExample(delayCallback: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            delayCallback()
        }
    }

    override fun onDetachedFromWindow() {
        handler.removeCallbacksAndMessages(null)
        super.onDetachedFromWindow()
    }
}