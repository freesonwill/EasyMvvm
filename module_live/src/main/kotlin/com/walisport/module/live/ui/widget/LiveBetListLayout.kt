package com.walisport.module.live.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
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
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import java.lang.ref.WeakReference

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
        isSelected: Boolean,
        callback: (WeakReference<View>, Long, Float, Float) -> Unit
    ) {
        if (num == 0) {
            clearAllArranges()
        }

        when (state) {
            StatesArrange.DEFAULT_ARRANGE -> addToDefaultArrange(num, name, odds, marketId, active, oddStatus, isSelected, callback)
            StatesArrange.ONE_ARRANGE -> addToOneArrange(name, odds, marketId, active, oddStatus, isSelected, callback)
            StatesArrange.TOW_ARRANGE -> addToTowArrange(num, name, odds, marketId, active, oddStatus, isSelected, callback)
            StatesArrange.THREE_ARRANGE -> addToThreeArrange(num, name, odds, marketId, active, oddStatus, isSelected, callback)
            StatesArrange.BO_DIAN -> addToBoDian( name, odds, marketId, active, oddStatus, isSelected, callback)
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
        isSelected: Boolean,
        callback: (WeakReference<View>, Long, Float, Float) -> Unit
    ) {
        when (num % StatesArrange.DEFAULT_ARRANGE.value) {
            0 -> binding.llcOneArrange.addView(createViewOne(name, odds, marketId, active, oddStatus, isSelected, callback))
            1 -> binding.llcTowArrange.addView(createViewTow(name, odds, marketId, active, oddStatus, isSelected, callback))
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
        isSelected: Boolean,
        callback: (WeakReference<View>, Long, Float, Float) -> Unit
    ) {
        binding.llcOneArrange.addView(createViewOne(name, odds, marketId, active, oddStatus, isSelected, callback))
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
        isSelected: Boolean,
        callback: (WeakReference<View>, Long, Float, Float) -> Unit
    ) {
        when (num % StatesArrange.TOW_ARRANGE.value) {
            0 -> binding.llcOneArrange.addView(createViewOne(name, odds, marketId, active, oddStatus, isSelected, callback))
            1 -> binding.llcTowArrange.addView(createViewTow(name, odds, marketId, active, oddStatus, isSelected, callback))
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
        isSelected: Boolean,
        callback: (WeakReference<View>, Long, Float, Float) -> Unit
    ) {
        when (num % StatesArrange.THREE_ARRANGE.value) {
            0 -> binding.llcOneArrange.addView(createViewOne(name, odds, marketId, active, oddStatus, isSelected, callback))
            1 -> binding.llcTowArrange.addView(createViewTow(name, odds, marketId, active, oddStatus, isSelected, callback))
            2 -> binding.llcThreeArrange.addView(createViewThree(name, odds, marketId, active, oddStatus, isSelected, callback))
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
        isSelected: Boolean,
        callback: (WeakReference<View>, Long, Float, Float) -> Unit
    ) {
        name.split("-").takeIf { it.size == 2 }?.let { parts ->
            val left = parts[0].toIntOrNull() ?: return
            val right = parts[1].toIntOrNull() ?: return
            when {
                left > right -> binding.llcOneArrange.addView(createViewOne(name, odds, marketId, active, oddStatus, isSelected, callback))
                left == right -> binding.llcTowArrange.apply {
                    isVisible = true
                    addView(createViewTow(name, odds, marketId, active, oddStatus, isSelected, callback))
                }
                left < right -> binding.llcThreeArrange.apply {
                    isVisible = true
                    addView(createViewThree(name, odds, marketId, active, oddStatus, isSelected, callback))
                }
                else ->{}
            }
        } ?: binding.llcOther.addView(createViewTow(name, odds, marketId, active, oddStatus, isSelected, callback))
    }

    private fun createViewOne(
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        isSelected: Boolean,
        callback: (WeakReference<View>, Long, Float, Float) -> Unit
    ): View = LiveBetContentItemLayoutOneBinding.inflate(LayoutInflater.from(context), binding.root, false).run {
        setupView(this, name, odds, marketId, active, oddStatus, isSelected, callback, sclOne, sclOneLock)
        root
    }

    private fun createViewTow(
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        isSelected: Boolean,
        callback: (WeakReference<View>, Long, Float, Float) -> Unit
    ): View = LiveBetContentItemLayoutTowBinding.inflate(LayoutInflater.from(context), binding.root, false).run {
        setupView(this, name, odds, marketId, active, oddStatus, isSelected, callback, sclTow, sclTowLock)
        root
    }

    private fun createViewThree(
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        isSelected: Boolean,
        callback: (WeakReference<View>, Long, Float, Float) -> Unit
    ): View = LiveBetContentItemLayoutThreeBinding.inflate(LayoutInflater.from(context), binding.root, false).run {
        setupView(this, name, odds, marketId, active, oddStatus, isSelected, callback, sclThree, sclThreeLock)
        root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun <T : Any> setupView(
        binding: T,
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        isSelected: Boolean,
        callback: (WeakReference<View>, Long, Float, Float) -> Unit,
        selectableView: View,
        lockView: View
    ) {
        with(binding) {
            val oddsInt :Int = odds.toOdds()
            when (this) {
                is LiveBetContentItemLayoutOneBinding -> {
                    if (active) isOddsStatus(oddStatus, imgTop, imgDown)
                    tvBetDuelLeft.text = name
                    tvBetDuelRight.text = oddsInt.getDisplayOdds()
                    sclOne.isSelected = isSelected

                    sclOne.isPressed = false
                    sclOne.setOnTouchListener { v, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                v.isPressed = true
                                if (active) {
                                    v.postDelayed({
                                        if (v.isPressed) {
                                            v.isSelected = true
                                        }
                                    }, 100L)
                                }
                            }
                            MotionEvent.ACTION_UP -> {
                                if (active) {
                                    handleClick(marketId, callback, v)
                                }
                                v.isPressed = false
                            }

                            MotionEvent.ACTION_CANCEL -> {
                                if (active) {
                                    v.isSelected = false
                                }
                                v.isPressed = false
                            }
                        }
                        true
                    }
                }
                is LiveBetContentItemLayoutTowBinding -> {
                    if (active) isOddsStatus(oddStatus, imgTop, imgDown)
                    tvBetDuelLeft.text = name
                    tvBetDuelRight.text = oddsInt.getDisplayOdds()
                    sclTow.isSelected = isSelected

                    sclTow.isPressed = false
                    sclTow.setOnTouchListener { v, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                v.isPressed = true
                                if (active) {
                                    v.postDelayed({
                                        if (v.isPressed) {
                                            v.isSelected = true
                                        }
                                    }, 100L)
                                }
                            }
                            MotionEvent.ACTION_UP -> {
                                if (active) {
                                    handleClick(marketId, callback, v)
                                }
                                v.isPressed = false
                            }

                            MotionEvent.ACTION_CANCEL -> {
                                if (active) {
                                    v.isSelected = false
                                }
                                v.isPressed = false
                            }
                        }
                        true
                    }
                }
                is LiveBetContentItemLayoutThreeBinding -> {
                    if (active) isOddsStatus(oddStatus, imgTop, imgDown)
                    tvBetDuelLeft.text = name
                    tvBetDuelRight.text = oddsInt.getDisplayOdds()
                    sclThree.isSelected = isSelected

                    sclThree.isPressed = false
                    sclThree.setOnTouchListener { v, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                v.isPressed = true
                                if (active) {
                                    v.postDelayed({
                                        if (v.isPressed) {
                                            v.isSelected = true
                                        }
                                    }, 100L)
                                }
                            }
                            MotionEvent.ACTION_UP -> {
                                if (active) {
                                    handleClick(marketId, callback, v)
                                }
                                v.isPressed = false
                            }
                            MotionEvent.ACTION_CANCEL -> {
                                if (active) {
                                    v.isSelected = false
                                }
                                v.isPressed = false
                            }
                        }
                        true
                    }
                }
                else ->{}
            }
            selectableView.isClickable = active
            lockView.isVisible = !active
        }
    }




    private fun handleClick(marketId: Long, callback: (WeakReference<View>, Long, Float, Float) -> Unit, view: View) {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val x = location[0] + view.width / 2
        val y = location[1] + view.height / 2
        callback(WeakReference(view), marketId,x.toFloat(),y.toFloat())
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

    override fun onDetachedFromWindow() {
        handler.removeCallbacksAndMessages(null)
        super.onDetachedFromWindow()
    }
}