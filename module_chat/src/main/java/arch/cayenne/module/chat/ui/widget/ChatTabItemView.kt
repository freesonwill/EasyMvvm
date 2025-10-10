package arch.cayenne.module.chat.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
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
        val LVING: Int = 1 //直播
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

    fun tabSelect(select:Boolean){
        if(isSelect == select){
            return
        }
        "tabSelect $select".logd("aaa")
        this.isSelect = select
        when (tabType) {
            NORMAL -> updateNormalStatus(isSelect)
            LVING -> updateLivingStatus(isSelect)
            CUSTOMER -> updateCustomerStatus(isSelect)
            else -> {}
        }
    }

    fun initTab(){
        updateNormalStatus(false)
    }

    private fun updateNormalStatus(isSelect: Boolean){
        binding.apply {
            tabIcon.setImageResource(if(isSelect) selectId else normalId)
            tabIcon.backgroundTintList = ContextCompat.getColorStateList(context,if(isSelect) tabBackSelectId else tabBackId)
            tabTv.setTextColor(ContextCompat.getColor(context,if(isSelect) tabTextColorSelectId else tabTextColorId))
        }

    }
    private fun updateLivingStatus(isSelect: Boolean){
        binding.apply {
//            tabIcon.backgroundTintList = ContextCompat.getColorStateList(context,if(isSelect) tabBackSelectId else tabBackId)
            tabTv.setTextColor(ContextCompat.getColor(context,if(isSelect) tabTextColorSelectId else tabTextColorId))
        }
    }
    private fun updateCustomerStatus(isSelect: Boolean){
        binding.apply {
//            tabIcon.setImageResource(if(isSelect) selectId else normalId)
//            tabIcon.backgroundTintList = ContextCompat.getColorStateList(context,if(isSelect) tabBackSelectId else tabBackId)
            tabTv.setTextColor(ContextCompat.getColor(context,if(isSelect) tabTextColorSelectId else tabTextColorId))
        }
    }

    /**
     * 更改tabItem的状态
     * @param type ChatTabItemView.NORMAL ChatTabItemView.LVING ChatTabItemView.CUSTOMER
     * */
    fun updateType(type: Int, content: String = "", msgSize: Int = 0) {
        tabType = type
        when (type) {
            NORMAL -> updateToNormal()
            LVING -> updateToLiving(content)
            CUSTOMER -> updateToCustomer(content, msgSize)
            else -> {}
        }
    }

    private fun updateToNormal() {
        binding.apply {
            tabIcon.isVisible = true
            clRect.isVisible = false
            val tvLp = tabTv.layoutParams as LinearLayout.LayoutParams
            tvLp.topMargin = 8.dp2px
            tabTv.layoutParams = tvLp
        }

    }

    private fun updateToLiving(content: String) {
        binding.apply {
            clRect.isVisible = true
            tabIcon.isVisible = false
            tabRectCircleTv.isVisible = false
            tabRectContentTv.text = content
            tabRectIcon.setImageResource(R.drawable.icon_main_chat_tab_living)
            val lp = tabRectCircle.layoutParams as ConstraintLayout.LayoutParams
            lp.width = 8.dp2px
            lp.height = 8.dp2px
            tabRectCircle.layoutParams = lp
            val tvLp = tabTv.layoutParams as LinearLayout.LayoutParams
            tvLp.topMargin = 2.dp2px
            tabTv.layoutParams = tvLp
            val tbBgLp = tabRectBg.layoutParams as ConstraintLayout.LayoutParams
            tbBgLp.topMargin = 4.dp2px
            tabRectBg.layoutParams = tbBgLp

        }
    }

    private fun updateToCustomer(content: String, msgSize: Int) {
        binding.apply {
            clRect.isVisible = true
            tabIcon.isVisible = false
            tabRectCircleTv.isVisible = true
            tabRectContentTv.text = content
            tabRectCircleTv.text = msgSize.toString()
            tabRectIcon.setImageResource(R.drawable.icon_main_chat_tab_customer)
            val lp = tabRectCircle.layoutParams as ConstraintLayout.LayoutParams
            lp.width = 16.dp2px
            lp.height = 16.dp2px
            tabRectCircle.layoutParams = lp
            val tvLp = tabTv.layoutParams as LinearLayout.LayoutParams
            tvLp.topMargin = 2.dp2px
            tabTv.layoutParams = tvLp
            val tbBgLp = tabRectBg.layoutParams as ConstraintLayout.LayoutParams
            tbBgLp.topMargin = 8.dp2px
            tabRectBg.layoutParams = tbBgLp

        }
    }


}