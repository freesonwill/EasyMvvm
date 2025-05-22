package com.walisport.module.live.ui.widget

import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
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
    var binding =
        LiveBetContentListItemLayoutBinding.inflate(LayoutInflater.from(context), this, false)
    var  isNotify : Boolean = false
    fun viewInit() {
        this.removeAllViews()
        this.addView(binding.root)
    }

    // active: Boolean, //true - 可以投注  false - 不可投注
    //请空,不需要懒加载
    fun submitList(
        state: StatesArrange?,
        num: Int,
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        isNotify:Boolean,
        callback: (Long) -> Unit
    ) {
        this.isNotify = isNotify
        if (num == 0) {
            binding.llcOneArrange.removeAllViews()
            binding.llcTowArrange.removeAllViews()
            binding.llcThreeArrange.removeAllViews()
            binding.llcOther.removeAllViews()
        }
        when (state?.code) {
            StatesArrange.DEFAULT_ARRANGE.code -> {
                if (num == 0 || num % StatesArrange.DEFAULT_ARRANGE.value == 0) {
                    binding.llcOneArrange.addView(
                        addViewOne(
                            name,
                            odds,
                            marketId,
                            active,
                            oddStatus,
                            callback
                        )
                    )
                }
                if (num == 1 || num % StatesArrange.DEFAULT_ARRANGE.value == 1) {
                    binding.llcTowArrange.addView(
                        addViewTow(
                            name,
                            odds,
                            marketId,
                            active,
                            oddStatus,
                            callback
                        )
                    )
                }
                binding.llcTowArrange.visibility = VISIBLE
                binding.llcThreeArrange.visibility = GONE
            }

            StatesArrange.ONE_ARRANGE.code -> {
                binding.llcOneArrange.addView(
                    addViewOne(
                        name,
                        odds,
                        marketId,
                        active,
                        oddStatus,
                        callback
                    )
                )
                binding.llcTowArrange.visibility = GONE
                binding.llcThreeArrange.visibility = GONE
            }

            StatesArrange.TOW_ARRANGE.code -> {
                binding.llcTowArrange.visibility = VISIBLE
                if (num == 0 || num % StatesArrange.TOW_ARRANGE.value == 0) {
                    binding.llcOneArrange.addView(
                        addViewOne(
                            name,
                            odds,
                            marketId,
                            active,
                            oddStatus,
                            callback
                        )
                    )
                }
                if (num == 1 || num % StatesArrange.TOW_ARRANGE.value == 1) {
                    binding.llcTowArrange.addView(
                        addViewTow(
                            name,
                            odds,
                            marketId,
                            active,
                            oddStatus,
                            callback
                        )
                    )
                }
                binding.llcThreeArrange.visibility = GONE
            }

            StatesArrange.THREE_ARRANGE.code -> {
                binding.llcThreeArrange.visibility = VISIBLE
                binding.llcTowArrange.visibility = VISIBLE
                if (num == 0 || num % StatesArrange.THREE_ARRANGE.value == 0) {
                    binding.llcOneArrange.addView(
                        addViewOne(
                            name,
                            odds,
                            marketId,
                            active,
                            oddStatus,
                            callback
                        )
                    )
                }
                if (num == 1 || num % StatesArrange.THREE_ARRANGE.value == 1) {
                    binding.llcTowArrange.addView(
                        addViewTow(
                            name,
                            odds,
                            marketId,
                            active,
                            oddStatus,
                            callback
                        )
                    )
                }
                if (num == 2 || num % StatesArrange.THREE_ARRANGE.value == 2) {
                    binding.llcThreeArrange.addView(
                        addViewThree(
                            name,
                            odds,
                            marketId,
                            active,
                            oddStatus,
                            callback
                        )
                    )
                }
            }

            StatesArrange.BO_DIAN.code -> {
                val parts = name.split("-")
                if (parts.size == 2) {
                    val left = parts[0].toIntOrNull() ?: return // If conversion fails, exit
                    val right = parts[1].toIntOrNull() ?: return // If conversion fails, exit
                    when {
                        left > right -> {
                            binding.llcOneArrange.addView(
                                addViewOne(
                                    name,
                                    odds,
                                    marketId,
                                    active,
                                    oddStatus,
                                    callback
                                )
                            )
                        }

                        left == right -> {
                            binding.llcTowArrange.visibility = VISIBLE
                            binding.llcTowArrange.addView(
                                addViewTow(
                                    name,
                                    odds,
                                    marketId,
                                    active,
                                    oddStatus,
                                    callback
                                )
                            )
                        }

                        left < right -> {
                            binding.llcThreeArrange.visibility = VISIBLE
                            binding.llcThreeArrange.addView(
                                addViewThree(
                                    name,
                                    odds,
                                    marketId,
                                    active,
                                    oddStatus,
                                    callback
                                )
                            )
                        }
                    }
                } else {
                    binding.llcOther.addView(
                        addViewTow(
                            name,
                            odds,
                            marketId,
                            active,
                            oddStatus,
                            callback
                        )
                    )
                }
            }
        }
    }

    fun addViewOne(
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        callback: (Long) -> Unit
    ): View {
        var itemBinding = LiveBetContentItemLayoutOneBinding.inflate(
            LayoutInflater.from(context),
            binding.root,
            false
        )
        if (active) {
            isOddsStatus(
                oddStatus,
                itemBinding.imgTop,
                itemBinding.imgDown

            )
        }
        //true - 可以投注  false - 不可投注
        isActive(active, itemBinding.sclOne)
        isActive(!active, itemBinding.sclOneLock)
        itemBinding.tvBetDuelLeft.text = name
        itemBinding.tvBetDuelRight.text = odds
        itemBinding.sclOne.clickNoRepeatSingle {
            callback(marketId)
            it.isSelected = true
            delayExample {
                it.isSelected = false
            }
        }
        return itemBinding.root
    }

    fun addViewTow(
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        callback: (Long) -> Unit
    ): View {
        var itemBinding = LiveBetContentItemLayoutTowBinding.inflate(
            LayoutInflater.from(context),
            binding.root,
            false
        )
        if (active) {
            isOddsStatus(
                oddStatus,
                itemBinding.imgTop,
                itemBinding.imgDown
            )
        }
        //true - 可以投注  false - 不可投注
        isActive(active, itemBinding.sclTow)
        isActive(!active, itemBinding.sclTowLock)
        itemBinding.tvBetDuelLeft.text = name
        itemBinding.tvBetDuelRight.text = odds
        itemBinding.sclTow.clickNoRepeatSingle {
            callback(marketId)
            it.isSelected = true
            delayExample {
                it.isSelected = false
            }
        }
        return itemBinding.root
    }

    private fun isOddsStatus(oddStatus: Int, top: View, down: View) {
        if (isNotify){
            when (oddStatus) {
                LiveOddsStatusEnum.UP.status -> {
                    down.visibility = GONE
                    top.visibility = VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        top.visibility = GONE
                    }, 2000)
                }

                LiveOddsStatusEnum.DOWN.status -> {
                    down.visibility = VISIBLE
                    top.visibility = GONE
                    Handler(Looper.getMainLooper()).postDelayed({
                        down.visibility = GONE
                    }, 2000)
                }

                LiveOddsStatusEnum.SAME.status -> {
                    down.visibility = GONE
                    top.visibility = GONE
                }
            }
        }
    }

    private fun isActive(active: Boolean, v: View) {
        if (active) {
            v.visibility = VISIBLE
        } else {
            v.visibility = GONE
        }
    }

    fun addViewThree(
        name: String,
        odds: String,
        marketId: Long,
        active: Boolean,
        oddStatus: Int,
        callback: (Long) -> Unit
    ): View {
        var itemBinding = LiveBetContentItemLayoutThreeBinding.inflate(
            LayoutInflater.from(context),
            binding.root,
            false
        )
        if (active) {
            isOddsStatus(
                oddStatus,
                itemBinding.imgTop,
                itemBinding.imgDown
            )
        }
        isActive(active, itemBinding.sclThree)
        isActive(!active, itemBinding.sclThreeLock)
        itemBinding.tvBetDuelLeft.text = name
        itemBinding.tvBetDuelRight.text = odds
        itemBinding.sclThree.clickNoRepeatSingle {
            callback(marketId)
            it.isSelected = true
            delayExample {
                it.isSelected = false
            }
        }
        return itemBinding.root
    }

    fun delayExample(delayCallback: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            delayCallback()
        }
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacksAndMessages(null)
    }
}