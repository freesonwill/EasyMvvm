package com.cn.game.sdk2.view.game

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.cn.game.sdk2.data.bean.BettingRecordBean
import com.cn.game.sdk2.view.MoneyOKView
import game.mod.proc.yf.proto.res.GameRes2.AreaInfo

class GameAreaView : FrameLayout {
    lateinit var content: View
    lateinit var gameCallback:IGameView
    lateinit var moneyView:MoneyOKView
    var areaCode : Int = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView(){
        content = getChildAt(0);
        moneyView = MoneyOKView(context)
    }
}