package com.walisport.module.live.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.skin.widget.SportLinearLayout
import com.walisport.module.live.databinding.LiveBetContentItemLayoutOneBinding
import com.walisport.module.live.databinding.LiveBetContentItemLayoutThreeBinding
import com.walisport.module.live.databinding.LiveBetContentItemLayoutTowBinding
import com.walisport.module.live.databinding.LiveBetContentListItemLayoutBinding

class LiveBetListLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SportLinearLayout(context, attrs, defStyleAttr) {

    var binding =
        LiveBetContentListItemLayoutBinding.inflate(LayoutInflater.from(context), this, false)

    // 0-默认为2列 1-一列 2-两列 3-三列 4-波胆
    enum class StatesArrange(val code: Int, val value: Int) {
        DEFAULT_ARRANGE(0, 2),
        ONE_ARRANGE(1, 1),
        TOW_ARRANGE(2, 2),
        THREE_ARRANGE(3, 3),
        BO_DIAN(4, 3);

        companion object {
            fun getStates(code: Int): StatesArrange? {
                return StatesArrange.entries.find { it.code == code }
            }
        }
    }

    fun viewInit() {
        this.removeAllViews()
        this.addView(binding.root)
    }

    //请空,不需要懒加载
    fun submitList(state: StatesArrange?, num: Int, name: String, odds: String) {
        if (num == 0) {
            binding.llcOneArrange.removeAllViews()
            binding.llcTowArrange.removeAllViews()
            binding.llcThreeArrange.removeAllViews()
            binding.llcOther.removeAllViews()
        }
        when (state?.code) {
            StatesArrange.DEFAULT_ARRANGE.code -> {
                if (num == 0 || num % StatesArrange.DEFAULT_ARRANGE.value == 0) {
                    binding.llcOneArrange.addView(addViewOne(name, odds))
                }
                if (num == 1 || num % StatesArrange.DEFAULT_ARRANGE.value == 1) {
                    binding.llcTowArrange.addView(addViewTow(name, odds))
                }
                binding.llcTowArrange.visibility = VISIBLE
                binding.llcThreeArrange.visibility = GONE
            }

            StatesArrange.ONE_ARRANGE.code -> {
                binding.llcOneArrange.addView(addViewOne(name, odds))
                binding.llcTowArrange.visibility = GONE
                binding.llcThreeArrange.visibility = GONE
            }

            StatesArrange.TOW_ARRANGE.code -> {
                binding.llcTowArrange.visibility = VISIBLE
                if (num == 0 || num % StatesArrange.TOW_ARRANGE.value == 0) {
                    binding.llcOneArrange.addView(addViewOne(name, odds))
                }
                if (num == 1 || num % StatesArrange.TOW_ARRANGE.value == 1) {
                    binding.llcTowArrange.addView(addViewTow(name, odds))
                }
                binding.llcThreeArrange.visibility = GONE
            }

            StatesArrange.THREE_ARRANGE.code -> {
                binding.llcThreeArrange.visibility = VISIBLE
                binding.llcTowArrange.visibility = VISIBLE
                if (num == 0 || num % StatesArrange.THREE_ARRANGE.value == 0) {
                    binding.llcOneArrange.addView(addViewOne(name, odds))
                }
                if (num == 1 || num % StatesArrange.THREE_ARRANGE.value == 1) {
                    binding.llcTowArrange.addView(addViewTow(name, odds))
                }
                if (num == 2 || num % StatesArrange.THREE_ARRANGE.value == 2) {
                    binding.llcThreeArrange.addView(addViewThree(name, odds))
                }
            }

            StatesArrange.BO_DIAN.code -> {
                val parts = name.split("-")
                if (parts.size == 2) {
                    val left = parts[0].toIntOrNull() ?: return // If conversion fails, exit
                    val right = parts[1].toIntOrNull() ?: return // If conversion fails, exit
                    when {
                        left > right -> {
                            binding.llcOneArrange.addView(addViewOne(name, odds))
                        }

                        left == right -> {
                            binding.llcTowArrange.visibility = VISIBLE
                            binding.llcTowArrange.addView(addViewTow(name, odds))
                        }

                        left < right -> {
                            binding.llcThreeArrange.visibility = VISIBLE
                            binding.llcThreeArrange.addView(addViewThree(name, odds))
                        }
                    }
                } else {
                    binding.llcOther.addView(addViewTow(name, odds))
                }
            }
        }
    }

    fun addViewOne(name: String, odds: String): View {
        var itemBinding = LiveBetContentItemLayoutOneBinding.inflate(
            LayoutInflater.from(context),
            binding.root,
            false
        )
        itemBinding.tvBetDuelLeft.text = name
        itemBinding.tvBetDuelRight.text = odds
        return itemBinding.root
    }

    fun addViewTow(name: String, odds: String): View {
        var itemBinding = LiveBetContentItemLayoutTowBinding.inflate(
            LayoutInflater.from(context),
            binding.root,
            false
        )
        itemBinding.tvBetDuelLeft.text = name
        itemBinding.tvBetDuelRight.text = odds
        return itemBinding.root
    }

    fun addViewThree(name: String, odds: String): View {
        var itemBinding = LiveBetContentItemLayoutThreeBinding.inflate(
            LayoutInflater.from(context),
            binding.root,
            false
        )
        itemBinding.tvBetDuelLeft.text = name
        itemBinding.tvBetDuelRight.text = odds
        return itemBinding.root
    }
}