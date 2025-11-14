package arch.cayenne.module.chat.ui.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.databinding.ItemChatTablayoutLayoutBinding

/**
 * @author: wenxi
 * @date: 8/10/25 17:26
 * @description:
 */
class ChatTabItemView : LinearLayoutCompat {
    companion object {
        val NORMAL: Int = 0 //普通状态
        val LIVING: Int = 1 //直播
        val CUSTOMER: Int = 2 // 客服
    }

    private val binding: ItemChatTablayoutLayoutBinding =
        ItemChatTablayoutLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    var tabType: Int = NORMAL
    private val INVALID_ID = -1
    private var normalId: Int = INVALID_ID
    private var selectId: Int = INVALID_ID
    private var tabBackId: Int = INVALID_ID
    private var tabBackSelectId: Int = INVALID_ID
    private var tabTextColorId: Int = INVALID_ID
    private var tabTextColorSelectId: Int = INVALID_ID
    private var isSelect = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet?) : super(context, attr)
    constructor(context: Context, attr: AttributeSet?, defStyle: Int) : super(
        context,
        attr,
        defStyle
    )

    fun setTabText(text: String) {
        binding.tabTv.text = text
    }

    fun setTabIcon(@DrawableRes normalId: Int) {
        this.normalId = normalId
    }

    fun setTabIconSelect(@DrawableRes selectId: Int) {
        this.selectId = selectId
    }

    fun setTabBack(@ColorRes colorId: Int) {
        this.tabBackId = colorId
    }

    fun setTabBackSelect(@ColorRes colorId: Int) {
        this.tabBackSelectId = colorId
    }

    fun setTabTabTextColor(@ColorRes colorId: Int) {
        this.tabTextColorId = colorId
    }

    fun setTabTextColorSelect(@ColorRes colorId: Int) {
        this.tabTextColorSelectId = colorId
    }

    fun tabSelect(select: Boolean) {
        if (isSelect == select) {
            return
        }
        this.isSelect = select
        when (tabType) {
            NORMAL -> updateNormalStatus(isSelect)
            LIVING -> updateLivingStatus(isSelect)
            CUSTOMER -> updateCustomerStatus(isSelect)
            else -> {}
        }
    }

    fun initTab(tabType: Int) {

        updateNormalStatus(false)
        if (tabType == LIVING) {
            initLiving()
        } else if (tabType == CUSTOMER) {
            initCustomer()
        }
        updateItemUi(NORMAL)
        tabRectItemUi(false)
    }

    private fun initLiving() {
        binding.apply {
            tabRectIcon.setImageResource(R.drawable.icon_main_chat_tab_living)
            val lp = tabRectCircle.layoutParams as ConstraintLayout.LayoutParams
            lp.width = 8.dp2px
            lp.height = 8.dp2px
            lp.marginEnd = 0
            lp.topMargin = -(4.dp2px)
            tabRectCircle.layoutParams = lp

        }
    }

    private fun initCustomer() {
        binding.apply {
            tabRectIcon.setImageResource(R.drawable.icon_main_chat_tab_customer)
            val lp = tabRectCircle.layoutParams as ConstraintLayout.LayoutParams
            lp.width = 16.dp2px
            lp.height = 16.dp2px
            lp.marginEnd = -(8.dp2px)
            lp.topMargin = -(8.dp2px)

            tabRectCircle.layoutParams = lp
        }
    }

    private fun updateNormalStatus(isSelect: Boolean) {
        binding.apply {
            tabIcon.setImageResource(if (isSelect) selectId else normalId)
            tabIcon.backgroundTintList = ContextCompat.getColorStateList(
                context,
                if (isSelect) tabBackSelectId else tabBackId
            )
            tabTv.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isSelect) tabTextColorSelectId else tabTextColorId
                )
            )
        }

    }

    private fun updateLivingStatus(isSelect: Boolean) {
        binding.apply {
            tabIcon.setImageResource(if (isSelect) selectId else normalId)
            tabIcon.backgroundTintList = ContextCompat.getColorStateList(
                context,
                if (isSelect) tabBackSelectId else tabBackId
            )
            tabTv.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isSelect) tabTextColorSelectId else tabTextColorId
                )
            )
        }
    }

    private fun updateCustomerStatus(isSelect: Boolean) {
        binding.apply {
            tabIcon.setImageResource(if (isSelect) selectId else normalId)
            tabIcon.backgroundTintList = ContextCompat.getColorStateList(
                context,
                if (isSelect) tabBackSelectId else tabBackId
            )
            tabTv.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isSelect) tabTextColorSelectId else tabTextColorId
                )
            )
        }
    }

    /**
     * 更改tabItem的状态
     * @param type ChatTabItemView.NORMAL ChatTabItemView.LVING ChatTabItemView.CUSTOMER
     * */
    fun updateType(type: Int, content: String = "", msgSize: Int = 0,onAnimStart: (() -> Unit)? = null,onAnimEnd: (() -> Unit)? = null) {
        if (tabType == type) {
            return
        }
        tabType = type
        when (type) {
            NORMAL -> updateToNormal(onAnimStart,onAnimEnd)
            LIVING -> updateToLiving(content,onAnimStart,onAnimEnd)
            CUSTOMER -> updateToCustomer(content, msgSize,onAnimStart, onAnimEnd)
            else -> {}
        }
    }

    private fun updateToNormal(onAnimStart: (() -> Unit)? = null,onAnimEnd: (() -> Unit)? = null) {
        binding.apply {
            tabAnim(false, onStart = {
                modifyTvMargin(0)
//                tabRectItemUi(false)
                onAnimStart?.invoke()
            }, onEnd = {
                updateItemUi(NORMAL)
                onAnimEnd?.invoke()
            })
        }
    }

    private fun updateToLiving(content: String,onAnimStart: (() -> Unit)? = null,onAnimEnd: (() -> Unit)? = null) {
        binding.apply {
            tabRectContentTv.text = content
            tabAnim(true, onStart = {
                updateItemUi(LIVING)
                modifyTvMargin(0)
                onAnimStart?.invoke()
            }, onEnd = {
                onAnimEnd?.invoke()
            })
        }
    }

    private fun updateToCustomer(content: String, msgSize: Int,onAnimStart: (() -> Unit)? = null,onAnimEnd: (() -> Unit)? = null) {
        binding.apply {
            tabRectContentTv.text = content
            tabRectCircleTv.text = msgSize.toString()
            tabAnim(true, onStart = {
                updateItemUi(CUSTOMER)
                onAnimStart?.invoke()
                modifyTvMargin(8.dp2px)
            }, onEnd = {
                onAnimEnd?.invoke()
            })
        }

    }

    private fun modifyTvMargin(right: Int) {
        binding.apply {
            val tvLp = tabTv.layoutParams as ConstraintLayout.LayoutParams
            tvLp.rightMargin = right
            tabTv.layoutParams = tvLp
        }
    }

    private fun updateItemUi(type: Int) {
        binding.apply {
            tabRectBg.isVisible = type != NORMAL
            ivArrow.isVisible = type != NORMAL
        }
    }

    private fun tabRectItemUi(visible: Boolean, type: Int = LIVING) {
        binding.apply {
            tabInside.isVisible = visible
            tabRectCircle.isVisible = visible
            tabRectCircleTv.isVisible = visible && type == CUSTOMER
            tabRectContentTv.isVisible = visible
            tabRectIcon.isVisible = visible
        }
    }

  var mainAnim:AnimatorSet? = null

    @SuppressLint("Recycle")
    private fun tabAnim(isExpanded: Boolean, onStart: () -> Unit, onEnd: () -> Unit) {
        val scaleParams = if (isExpanded) floatArrayOf(0.35f, 1f) else floatArrayOf(1f, 0.4f)
        val tabRectParams = if (isExpanded) floatArrayOf(0f, 1f) else floatArrayOf(1f, 0f)
        val tabIconParams = if (isExpanded) floatArrayOf(1f, 0f) else floatArrayOf(0f, 1f)

        val animDuration = 250L
        val scaleAnim = ObjectAnimator.ofFloat(binding.clRect, "scaleX", *scaleParams)
            .apply {
                duration = animDuration
                addListener(onStart = {
                    if(!isExpanded){
                        tabRectItemUi(false)
                    }
                }, onEnd = {
                    binding.clRect.scaleX = 1f
                    if (isExpanded) {
                        tabRectItemUi(true, tabType)
                    }
                })
            }
        val tabRectAnim = ObjectAnimator.ofFloat(binding.tabInside, "alpha", *tabRectParams)
            .apply {
                duration = animDuration
                addListener(onStart = {
                    if (isExpanded) {
                        tabRectItemUi(true, tabType)
                    }
                }, onEnd = {
                    if (!isExpanded) {
                        tabRectItemUi(false)
                    }
                }
                )
            }
        val tabIconAnim = ObjectAnimator.ofFloat(binding.tabIcon, "alpha", *tabIconParams)
            .apply {
                duration = animDuration
                addListener(onEnd = {
                })
            }
        val alphaSet = AnimatorSet().apply {
            playTogether(tabIconAnim,tabRectAnim)
        }

        mainAnim = AnimatorSet().apply {
            addListener(onStart = {
                onStart.invoke()
            }, onEnd = {
                onEnd.invoke()
            })
            if (isExpanded) playSequentially(scaleAnim, alphaSet) //playSequentially(scaleAnim, tabIconAnim, tabRectAnim)
            else playSequentially(alphaSet, scaleAnim)  //playSequentially(tabRectAnim, tabIconAnim, scaleAnim)

            start()
        }

    }


}