package arch.cayenne.module.chat.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.databinding.TabChatTopLayoutBinding
import arch.cayenne.module.chat.ui.widget.ChatTabItemView.Companion.NORMAL
import arch.cayenne.module.chat.ui.widget.ChatTabItemView.Companion.LIVING
import arch.cayenne.module.chat.ui.widget.ChatTabItemView.Companion.CUSTOMER


/**
 * @author: wenxi
 * @date: 9/10/25 11:41
 * @description:
 */
class ChatTabView : LinearLayout {
    val binding = TabChatTopLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    private var select: Int = NORMAL
    private var chatClicks: Int = 0
    private var livingClicks: Int = 0
    private var customerClicks: Int = 0

    private val ACTION_NORMAL_TO_LIVING = 100
    private val ACTION_NORMAL_TO_CUSTOMER = 101
    private val ACTION_LIVING_TO_NORMAL = 102
    private val ACTION_CUSTOMER_TO_NORMAL = 103


    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet?) : super(context, attr)
    constructor(context: Context, attr: AttributeSet?, defStyle: Int = 0) : super(
        context,
        attr,
        defStyle
    )

    init {
        initListener()
        initChatTab()
        initLivingTab()
        initCustomerTab()
        setTabMargin()
        binding.apply {
            tabChatRoom.initTab(NORMAL)
            tabChatLiving.initTab(LIVING)
            tabChatCustomer.initTab(CUSTOMER)
        }
    }

    val livingStr: String = "世界杯首场法国VS巴西开赛了要不要来"
    val customerStr: String = "您好，我是您的福利客服，您要不要充钱"
    fun initListener() {
        binding.apply {
            tabChatRoom.setOnClickListener {
                tabSelect(NORMAL)
                clickTabAction(NORMAL)
            }
            tabChatLiving.setOnClickListener {
                tabSelect(LIVING)
                clickTabAction(LIVING)
            }
            tabChatCustomer.setOnClickListener {
                tabSelect(CUSTOMER)
                clickTabAction(CUSTOMER)
            }
        }
    }

    /**
     * 判断当前选择是聊天室 直播间  客服
     * */
    private fun tabSelect(type: Int) {
        select = type
        binding.apply {
            tabChatRoom.tabSelect(select == NORMAL)
            tabChatLiving.tabSelect(select == LIVING)
            tabChatCustomer.tabSelect(select == CUSTOMER)
        }
    }

    private fun initChatTab() {
        binding.tabChatRoom.apply {
            setTabText("聊天室")
            setTabIcon(R.drawable.icon_main_chat_room_inactive)
            setTabIconSelect(R.drawable.icon_main_chat_room_active)
            setTabTabTextColor(arch.cayenne.lib.common.R.color.color_999999)
            setTabTextColorSelect(arch.cayenne.lib.common.R.color.color_FFFFFF)
            setTabBack(arch.cayenne.lib.common.R.color.color_0FFFFFFF)
            setTabBackSelect(arch.cayenne.lib.common.R.color.color_E3603A)
        }
    }

    private fun initLivingTab() {
        binding.tabChatLiving.apply {
            setTabText("赛事直播")
            setTabIcon(R.drawable.icon_main_chat_living_inactive)
            setTabIconSelect(R.drawable.icon_main_chat_living_active)
            setTabTabTextColor(arch.cayenne.lib.common.R.color.color_999999)
            setTabTextColorSelect(arch.cayenne.lib.common.R.color.color_FFFFFF)
            setTabBack(arch.cayenne.lib.common.R.color.color_0FFFFFFF)
            setTabBackSelect(arch.cayenne.lib.common.R.color.color_E3603A)
        }
    }

    private fun initCustomerTab() {
        binding.tabChatCustomer.apply {
            setTabText("客服")
            setTabIcon(R.drawable.icon_main_chat_customer_inactive)
            setTabIconSelect(R.drawable.icon_main_chat_customer_active)
            setTabTabTextColor(arch.cayenne.lib.common.R.color.color_999999)
            setTabTextColorSelect(arch.cayenne.lib.common.R.color.color_FFFFFF)
            setTabBack(arch.cayenne.lib.common.R.color.color_0FFFFFFF)
            setTabBackSelect(arch.cayenne.lib.common.R.color.color_E3603A)
        }
    }


    /**
     *
     * @param type 点击按钮
     * */
    private fun clickTabAction(type: Int) {

        val actionType = actionType(type)
        if (actionType == -1) {
            return
        }
        binding.apply {
            val livingType = tabChatLiving.tabType
            val customerType = tabChatCustomer.tabType
            when (actionType) {
                ACTION_NORMAL_TO_LIVING -> {
                    if (customerType == CUSTOMER) {
                        setCustomerTabToNormal(animEnd = {
                            setTabToLiving(livingStr, animStart = { setTabMargin() })
                        })
                    } else {
                        setTabToLiving(livingStr, animStart = { setTabMargin() })
                    }
                }

                ACTION_NORMAL_TO_CUSTOMER -> {
                    if (livingType == LIVING) {
                        setLivingTabToNormal(animEnd = {
                            setTabToCustomer(
                                content = customerStr,
                                9,
                                animStart = { setTabMargin() })
                        })
                    } else {
                        setTabToCustomer(content = customerStr, 9, animStart = { setTabMargin() })
                    }
                }

                ACTION_LIVING_TO_NORMAL -> {
                    if (livingType == LIVING) {
                        setLivingTabToNormal(animEnd = { setTabMargin() })
                    }
                }

                ACTION_CUSTOMER_TO_NORMAL -> {
                    if (customerType == CUSTOMER) {
                        setCustomerTabToNormal(animEnd = { setTabMargin() })
                    }
                }
            }
        }
    }


    private fun setTabToLiving(
        content: String,
        animStart: (() -> Unit)? = null,
        animEnd: (() -> Unit)? = null
    ) {
        binding.tabChatLiving.updateType(
            LIVING,
            content,
            onAnimStart = animStart,
            onAnimEnd = animEnd
        )
    }

    private fun setTabToCustomer(
        content: String,
        msgSize: Int,
        animStart: (() -> Unit)? = null,
        animEnd: (() -> Unit)? = null
    ) {
        binding.tabChatCustomer.updateType(
            CUSTOMER,
            content,
            msgSize,
            onAnimStart = animStart,
            onAnimEnd = animEnd
        )
    }

    private fun setLivingTabToNormal(
        animStart: (() -> Unit)? = null,
        animEnd: (() -> Unit)? = null
    ) {
        binding.tabChatLiving.updateType(NORMAL, onAnimStart = animStart, onAnimEnd = animEnd)
    }

    private fun setCustomerTabToNormal(
        animStart: (() -> Unit)? = null,
        animEnd: (() -> Unit)? = null
    ) {
        binding.tabChatCustomer.updateType(NORMAL, onAnimStart = animStart, onAnimEnd = animEnd)
    }

    private fun actionType(type: Int): Int {
        val livingType = binding.tabChatLiving.tabType
        val customerType = binding.tabChatCustomer.tabType
        return when {
            type == NORMAL && livingType == LIVING -> ACTION_LIVING_TO_NORMAL
            type == NORMAL && customerType == CUSTOMER -> ACTION_CUSTOMER_TO_NORMAL

            type == LIVING && livingType == LIVING -> ACTION_LIVING_TO_NORMAL
            type == LIVING && livingType == NORMAL -> ACTION_NORMAL_TO_LIVING

            type == CUSTOMER && customerType == CUSTOMER -> ACTION_CUSTOMER_TO_NORMAL
            type == CUSTOMER && customerType == NORMAL -> ACTION_NORMAL_TO_CUSTOMER
            else -> -1
        }
    }


//    距离前一个Tab距离
//         normal        only-lving气泡 only-cutomer气泡  lving-and-customer 气泡
// 聊天室    48               48           36              36
//赛事直播    74              39           47              12
//客服       74               38           47              11

//    距离屏幕原点（x=0）
//         normal        only-lving气泡   only-cutomer气泡    lving-and-customer 气泡
// 聊天室    48               48             36                 36
//赛事直播    164              129           125                 90
//客服       286               286          220                 220

    // 10 6 2
    private fun setTabMargin() {
        binding.apply {
            val livingType = tabChatLiving.tabType
            val customerType = tabChatCustomer.tabType
            when {
                livingType == NORMAL && customerType == NORMAL -> {
                    val leftMargin = arrayOf(48, 74, 74)
                    val topMargin = arrayOf(10, 10, 10)
                    setTabLp(leftMargin)
                }

                livingType == LIVING && customerType == NORMAL -> {
                    val leftMargin = arrayOf(48, 39, 38)
                    setTabLp(leftMargin)
                }

                livingType == NORMAL && customerType == CUSTOMER -> {
                    val leftMargin = arrayOf(36, 47, 47)
                    val topMargin = arrayOf(10, 10, 2)
                    setTabLp(leftMargin)
                }

                else -> {}
            }
        }
    }

    private fun setTabLp(left: Array<Int>) {
        binding.apply {
            val chatLp = tabChatRoom.layoutParams as LinearLayout.LayoutParams
            val livingLp = tabChatLiving.layoutParams as LinearLayout.LayoutParams
            val customerLp = tabChatCustomer.layoutParams as LinearLayout.LayoutParams

            chatLp.apply {
                marginStart = left[0].dp2px
            }
            livingLp.apply {
                marginStart = left[1].dp2px
            }
            customerLp.apply {
                marginStart = left[2].dp2px
            }

            tabChatRoom.layoutParams = chatLp
            tabChatLiving.layoutParams = livingLp
            tabChatCustomer.layoutParams = customerLp
        }
    }

}